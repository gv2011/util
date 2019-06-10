package com.github.gv2011.util.main;

import static com.github.gv2011.util.FileUtils.delete;
import static com.github.gv2011.util.Nothing.nothing;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.ex.Exceptions.format;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.JmxUtils;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.ResourceUtils;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.Exceptions;
import com.github.gv2011.util.json.JsonUtils;
import com.github.gv2011.util.log.LogAdapter;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public abstract class MainUtils implements MainUtilsMBean, AutoCloseableNt{

  public static interface ServiceBuilder<S extends AutoCloseableNt, C>{
    S startService(C configuration);
  }

  public static <C> MainUtils create(
    final String[] args, final ServiceBuilder<?,C> serviceBuilder, final Class<C> configurationClass
  ) {
    return new MainRunner<>(args, serviceBuilder, configurationClass);
  }


  public static <C> int run(
    final String[] mainArgs,
    final ServiceBuilder<?, C> serviceBuilder,
    final Class<C> configurationClass
  ) {
    return create(mainArgs, serviceBuilder, configurationClass).runMain();
  }

  public static int run(final Supplier<AutoCloseableNt> serviceBuilder){
    return run(
      new String[]{},
      new ServiceBuilder<>(){
        @Override
        public AutoCloseableNt startService(final Nothing noConfiguration){
          return serviceBuilder.get();
        }
      },
      Nothing.class
    );
  }

  private MainUtils(){}

  public abstract int runMain();

  private static final class MainRunner<C> extends MainUtils{

    private final AutoCloseableNt serviceLoader;
    private final AtomicLong uncaughtExceptionCount = new AtomicLong();
    private final Instant startTime = Instant.now();
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private final String[] mainArgs;
    private final ServiceBuilder<?,C> serviceBuilder;
    private final Class<C> configurationClass;
    private final Object lock = new Object();
    private boolean started = false;
    private boolean closing = false;
    private final Logger log;
    private @Nullable AutoCloseableNt pidLog = null;


    private MainRunner(
      final String[] mainArgs, final ServiceBuilder<?, C> serviceBuilder, final Class<C> configurationClass
    ) {
      this.mainArgs = mainArgs;
      this.serviceBuilder = serviceBuilder;
      this.configurationClass = configurationClass;
      serviceLoader = RecursiveServiceLoader.externallyClosedInstance();
      RecursiveServiceLoader.service(LogAdapter.class).ensureInitialized();
      log = getLogger(MainUtils.class);
    }

    @Override
    public int runMain() {
      synchronized(lock){
        verify(!closing);
        started = true;
      }
      int resultCode = 3;
      final CountDownLatch mainDone = new CountDownLatch(1);
      try{
        pidLog = logPid(log);
        Runtime.getRuntime().addShutdownHook(new Thread(
          ()->{
            shutdownLatch.countDown();
            call(()->mainDone.await());
          },
          "shutdown"
        ));

        // Log all uncaught exceptions:
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
          uncaughtExceptionCount.incrementAndGet();
          log.error(format("Uncaught exception in thread {}", t), e);
          if(Exceptions.ASSERTIONS_ON){
            log.error(format("Uncaught exception in thread {}. Fail-fast: terminating.", t), e);
            shutdownLatch.countDown();
          }else{
            log.error(format("Uncaught exception in thread {}", t), e);
          }
        });

        try(AutoCloseableNt jmxHandle = JmxUtils.registerMBean(this)){
          try(final AutoCloseableNt service =
            serviceBuilder.startService(readConfiguration(mainArgs, configurationClass))
          ){
            call(()->shutdownLatch.await());
            resultCode = uncaughtExceptionCount.get()==0L ? 0 : 2;
          }
        }
      }
      catch(final Throwable t){
        log.error("Error in main method. Terminating.", t);
        resultCode = 1;
      }
      finally{
        try{serviceLoader.close();}
        finally{mainDone.countDown();}
      }
      return resultCode;
    }

    public static <C> C readConfiguration(final String[] mainArgs, final Class<C> configurationClass) {
      if(configurationClass.equals(Nothing.class)) return configurationClass.cast(nothing());
      else{
        final Path configFile = Paths.get("configuration.json");
        if(!Files.exists(configFile)) copyDefaultConfigFile(configFile);
        return BeanUtils.typeRegistry().beanType(configurationClass)
          .parse(JsonUtils.jsonFactory().deserialize(
            FileUtils.readText(configFile)
          ))
        ;
      }
    }


    private static void copyDefaultConfigFile(final Path configFile) {
      FileUtils.writeText(ResourceUtils.getTextResource("default-configuration.json"), configFile);
    }

    private AutoCloseableNt logPid(final Logger logger) {
      final String user = System.getProperty("user.name");
      int pid = -1;
      final Path pidFile = Paths.get("log", "pid.txt").toAbsolutePath();
      try {
        final String runTimeName = ManagementFactory.getRuntimeMXBean().getName();
        pid = Integer.parseInt(runTimeName.substring(0, runTimeName.indexOf('@')));
        logger.warn("Started. Process ID (pid) is {}. Running as user {}.", pid, user);
        Files.createDirectories(pidFile .getParent());
        Files.write(pidFile, Integer.toString(pid).getBytes(StandardCharsets.UTF_8), TRUNCATE_EXISTING, CREATE);
        logger.debug("Written process id {} to {}.", pid, pidFile);
      } catch (final Exception e) {
        logger.warn(format("Could not determine or write process ID (pid) {} (user {}).", pid, user), e);
      }
      return ()->{
        delete(pidFile);
      };
    }

    @Override
    public void shutdown() {
      close();
    }

    @Override
    public void close() {
      @Nullable AutoCloseableNt pidLog = null;
      try{
        synchronized(lock){
          closing = true;
          pidLog = this.pidLog;
          if(started) shutdownLatch.countDown();
          else serviceLoader.close();
        }
      }
      finally{
        if(pidLog!=null) pidLog.close();
      }
    }

    @Override
    public long getUncaughtExceptionCount() {
      return uncaughtExceptionCount.get();
    }

    @Override
    public Instant getStartTime() {
      return startTime ;
    }
  }

}
