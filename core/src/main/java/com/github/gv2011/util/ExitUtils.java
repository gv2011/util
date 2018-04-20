package com.github.gv2011.util;

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

import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.ex.Exceptions;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.icol.ISet;

public final class ExitUtils {

  private ExitUtils(){staticClass();}

  private static final Logger LOG = LoggerFactory.getLogger(ExitUtils.class);

	private static final Constant<ExitManager> MANAGER = Constants.cachedConstant(ExitManager::new);

  public static final void doAfterGarbageCollection(final Object obj, final ThrowingRunnable operation){
    MANAGER.get().doAfterGarbageCollection(obj, operation);
  }

  public static final ExecutorService defaultExecutorService(){
    return MANAGER.get().executorService;
  }

  public static final ThreadFacade newDeamonThread(final ThrowingRunnable operation){
    final Thread thread = new Thread(()->call(operation));
    thread.setDaemon(true);
    thread.start();
    return new ProtectedThread(thread);
  }

  private static final class ExitManager implements ThreadFactory, UncaughtExceptionHandler{

    private final Object lock = new Object();
    private final ExecutorService executorService;
    private final List<Pair<Reference<Object>, ThrowingRunnable>> entries = new LinkedList<>();
    private final Thread thread;
    private final WeakHashMap<Thread,Nothing> threads = new WeakHashMap<>();
    private ExecutorService shutdownEs = null;

    private ExitManager(){
      thread = new Thread(this::run);
      thread.setDaemon(true);
      thread.start();
      executorService = Executors.newCachedThreadPool(this);
      Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
      LOG.info("Created {}.", getClass().getSimpleName());
    }

    private boolean terminating(){
      synchronized(lock){return shutdownEs!=null;}
    }

    private void doAfterGarbageCollection(final Object obj, final ThrowingRunnable operation){
      synchronized(lock){
        verifyNotTerminating();
        entries.add(pair(new WeakReference<>(obj), operation));
      }
    }

    private void verifyNotTerminating() {
      if(terminating()) throw new IllegalStateException("Shutting down.");
    }

    private void run(){
      while(!terminating()){
        try {Thread.sleep(60000);} catch (final InterruptedException e) {
          LOG.debug("Dropper interrupted.");
        }
        drop();
      }
      synchronized(lock){
        final ListIterator<Pair<Reference<Object>, ThrowingRunnable>> it = entries.listIterator(entries.size());
        while(it.hasPrevious()){
          shutdownEs.submit(()->{it.previous().getValue().run(); return null;});
        }
      }
      LOG.info("Dropper terminated.");
    }

    private void drop() {
      synchronized(lock){
        if(!terminating()){
          final Iterator<Pair<Reference<Object>, ThrowingRunnable>> it = entries.iterator();
          while(it.hasNext()){
            final Pair<Reference<Object>, ThrowingRunnable> p = it.next();
            if(p.getKey().get()==null){
              executorService.submit(()->{p.getValue().run(); return null;});
              it.remove();
            }
          }
        }
      }
    }

    private void shutdown(){
      LOG.info("Shutting down.");
      synchronized(lock){
        shutdownEs = Executors.newCachedThreadPool(r->{
          final Thread result = new Thread(r);
          result.setDaemon(true);
          result.setUncaughtExceptionHandler(this);
          return result;
        });
      }
      executorService.shutdown();
      thread.interrupt();
      final ISet<Thread> threads;
      synchronized(lock){
        threads = iCollections().setFrom(this.threads.keySet());
      }
      threads.forEach(t->{
        t.interrupt();
        doParallel(()->join(t));
      });
      doParallel(()->join(thread));
      doParallel(()->awaitTermination(executorService));
      shutdownEs.shutdown();
      awaitTermination(shutdownEs);
      LOG.info("Shut down.");
    }

    private void doParallel(final ThrowingRunnable task){
      shutdownEs.submit(()->{task.run(); return null;});
    }

    private void join(final Thread t){
      while(t.isAlive()){
        Exceptions.call(()->t.join(10000));
        LOG.debug("Waiting for termination of {}.", t);
      }
      LOG.info("{} terminated.", t);
    }

    private void awaitTermination(final ExecutorService es){
      boolean terminated = false;
      while(!terminated){
        terminated = Exceptions.call(()->es.awaitTermination(10, TimeUnit.SECONDS));
        LOG.debug("Waiting for termination of {}.", es);
      }
      LOG.info("{} terminated.", es);
    }

    @Override
    public Thread newThread(final Runnable r) {
      final Thread result = new Thread(r);
      result.setDaemon(true);
      result.setUncaughtExceptionHandler(this);
      synchronized(threads){
        threads.put(result, Nothing.INSTANCE);
      }
      return result;
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
      LOG.error("UncaughtException in "+t+".", e);
    }
	}



}
