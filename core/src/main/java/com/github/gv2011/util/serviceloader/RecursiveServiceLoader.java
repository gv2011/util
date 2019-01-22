package com.github.gv2011.util.serviceloader;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.ICollections.setFrom;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ICollectionFactorySupplier;
import com.github.gv2011.util.icol.IEmpty;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Single;
import com.github.gv2011.util.log.LogAdapter;


public final class RecursiveServiceLoader implements AutoCloseableNt{

  static final String FILE_WATCH_SERVICE = "com.github.gv2011.util.filewatch.FileWatchService";
  static final String DEFAULT_FILE_WATCH_SERVICE = "com.github.gv2011.util.filewatch.DefaultFileWatchService";

  static final String DATA_TYPE_PROVIDER = "com.github.gv2011.util.bytes.DataTypeProvider";
  static final String DEFAULT_DATA_TYPE_PROVIDER = "com.github.gv2011.util.bytes.DefaultDataTypeProvider";

  static final String CLOCK = "com.github.gv2011.util.time.Clock";
  static final String DEFAULT_CLOCK = "com.github.gv2011.util.time.DefaultClock";

  private static final Map<String,String> DEFAULT_SERVICES =
    Collections.unmodifiableMap(
      Arrays.stream(new String[][]{
        new String[]{FILE_WATCH_SERVICE, DEFAULT_FILE_WATCH_SERVICE},
        new String[]{DATA_TYPE_PROVIDER, DEFAULT_DATA_TYPE_PROVIDER},
        new String[]{CLOCK, DEFAULT_CLOCK}
      })
      .collect(Collectors.toMap(e->e[0], e->e[1]))
    )
  ;

  private static final CachedConstant<RecursiveServiceLoader> INSTANCE =
    Constants.cachedConstant(()->{
      final RecursiveServiceLoader loader = new RecursiveServiceLoader();
      Runtime.getRuntime().addShutdownHook(new Thread(
        loader::close,
        RecursiveServiceLoader.class.getSimpleName()+"-shutdown"
      ));
      return loader;
    })
  ;

  public static final <S> S service(final Class<S> serviceClass) {
    return INSTANCE.get().getService(serviceClass);
  }

  public static final <S> Constant<S> lazyService(final Class<S> serviceClass) {
    return Constants.cachedConstant(()->service(serviceClass));
  }


  public static final <S> Opt<S> tryGetService(final Class<S> serviceClass) {
    return INSTANCE.get().tryGetServiceInternal(serviceClass);
  }

  public static final <S> ISet<S> services(final Class<S> serviceClass) {
    return setFrom(INSTANCE.get().getAllServices(serviceClass));
  }

  public static AutoCloseableNt externallyClosedInstance(){
    RecursiveServiceLoader loader;
    try {
      loader = new RecursiveServiceLoader();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    INSTANCE.set(loader);
    return loader;
  }

  private final Object lock = new Object();
  private final Map<Class<?>,Set<?>> services = new HashMap<>();
  private final Set<Class<?>> loading = new HashSet<>();
  private final List<AutoCloseable> closeableServices = new ArrayList<>();
  private boolean closing = false;
  private @Nullable Logger logger = null;
  private final @Nullable LogAdapter logAdapter;

  private RecursiveServiceLoader() throws Exception {
    logAdapter = loadBasicService(LogAdapter.class, false);
    if(logAdapter!=null){
      services.put(LogAdapter.class, Collections.singleton(logAdapter));
    }
    else{
      services.put(LogAdapter.class, Collections.emptySet());
    }
    services.put(
      ICollectionFactorySupplier.class,
      Collections.singleton(loadBasicService(ICollectionFactorySupplier.class, true))
    );
  }

  private static <S> @Nullable S loadBasicService(
    final Class<S> serviceClass, final boolean required
  ) throws Exception {
    final Set<String> implementationClassNames =
      ServiceProviderConfigurationFile.filesInternal(serviceClass)
      .flatMap(ServiceProviderConfigurationFile::implementationsInternal)
      .collect(Collectors.toSet())
    ;
    if(implementationClassNames.isEmpty()){
      if(required){
        throw new IllegalStateException(format("No implementation for {} found.", serviceClass));
      }
      return null;
    }
    else{
      if(implementationClassNames.size()>1){throw new IllegalStateException(
        format("Multiple implementations for service {}: {}.", serviceClass, implementationClassNames)
      );}
      return
        Class.forName(implementationClassNames.iterator().next())
        .asSubclass(serviceClass)
        .getConstructor()
        .newInstance()
      ;
    }
  }

  private <S> S getService(final Class<S> serviceClass) {
    return tryGetServiceInternal(serviceClass)
      .orElseThrow(()->new IllegalStateException(format("No implementation for {} found.", serviceClass))
    );
  }

  @SuppressWarnings("unchecked")
  private <S> Opt<S> tryGetServiceInternal(final Class<S> serviceClass) {
    final Set<S> services = getAllServices(serviceClass);
    if(services.isEmpty()) return IEmpty.INSTANCE;
    else{
      verify(services, s->s.size()==1, s->format("Multiple implementations for service {}: {}.", serviceClass, s));
      return Single.of(services.iterator().next());
    }
  }

  @SuppressWarnings("unchecked")
  private <S> Set<S> getAllServices(final Class<S> serviceClass) {
    synchronized(lock) {
      verify(!closing);
      Opt<Set<?>> entry = Single.ofNullable(services.get(serviceClass));
      if(!entry.isPresent()) {
        final boolean added = loading.add(serviceClass);
        try{
          if(!added){
            throw new RuntimeException(format("Infinite recursion: already loading {}.", serviceClass.getName()));
          }
          loadServices(serviceClass);
          entry = Single.of(services.get(serviceClass));
        }
        finally{loading.remove(serviceClass);}
      }
      return (Set<S>) entry.get();
    }
  }

  private <S> void loadServices(final Class<S> serviceClass) {
    final Set<S> implementations = Collections.unmodifiableSet(
      Stream.of(
        call(()->ServiceProviderConfigurationFile.filesInternal(serviceClass))
        .flatMap(f->f.implementationsInternal())
        .collect(toSet())
      )
      .flatMap(s->s.isEmpty()
        ? Optional.ofNullable(DEFAULT_SERVICES.get(serviceClass.getName())).stream()
        : s.stream()
      )
      .map(n->createInstance(serviceClass, n))
      .collect(toSet())
    );
    synchronized(lock){
      services.put(serviceClass, implementations);
    }
  }

  private <S> S createInstance(final Class<S> serviceClass, final String implementationClassName) {
    final Class<?> implClass = call(()->Class.forName(implementationClassName));
    verify(serviceClass.isAssignableFrom(implClass));
    final Constructor<?> constr = Arrays.stream(implClass.getConstructors())
      .filter(c->c.getParameterCount()==0)
      .findAny()
      .orElseThrow(()->new NoSuchElementException(format("{} has no no-arg constructor.", implClass)))
    ;
    final Class<?>[] pTypes = constr.getParameterTypes();
    final Object[] initargs = new Object[pTypes.length];
    for(int i=0; i<initargs.length; i++) {
      initargs[i] = getService(pTypes[i]);
    }
    final S serviceInstance = serviceClass.cast(call(()->constr.newInstance(initargs)));
    if(serviceInstance instanceof AutoCloseable){
      synchronized(lock){
        verify(!closing);
        closeableServices.add((AutoCloseable) serviceInstance);
      }
      getLogger().info(
        "Loaded closeable service {} - implementation class: {}.", serviceClass.getName(), implClass.getName()
      );
    }
    else{
      getLogger().info(
        "Loaded service {} - implementation class: {}.", serviceClass.getName(), implClass.getName()
      );
    }
    return serviceInstance;
  }

  @Override
  public void close() {
    final boolean doClose;
    iCollections();
    final Logger logger = getLogger();
    synchronized(lock){
      doClose = !closing;
      closing = true;
    }
    if(doClose){
      logger.info("Closing.");
      Opt<AutoCloseable> toClose = getLast();
      while(toClose.isPresent()){
        final AutoCloseable service = toClose.get();
        try {
          logger.debug("Closing service {}", service);
          service.close();
          logger.debug("Closed service {}", service);
        } catch (final Exception e) {
          logger.error(format("Could not close {}.", service), e);
        }
        synchronized(lock){
          closeableServices.remove(service);
        }
        toClose = getLast();
      }
      if(logAdapter!=null){
        logger.info("Closing logging as last action - goodbye.");
        logAdapter.close();
      }
      else logger.info("Closed - goodbye.");
    }
  }

  private Logger getLogger() {
    Logger logger = this.logger;
    if(logger==null){
      logger = LoggerFactory.getLogger(RecursiveServiceLoader.class);
      this.logger = logger;
    }
    return logger;
  }

  private Opt<AutoCloseable> getLast() {
    synchronized(lock){
      return
        closeableServices.isEmpty()
        ? Opt.empty()
        : Opt.of(closeableServices.get(closeableServices.size()-1))
      ;
    }
  }


}
