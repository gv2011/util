package com.github.gv2011.util.filewatch;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.tryGet;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.listBuilder;
import static java.util.stream.Collectors.joining;

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

public class DefaultFileWatchService implements FileWatchService, AutoCloseableNt{

  private static final Logger LOG = LoggerFactory.getLogger(DefaultFileWatchService.class);

  private final WatchService watchService;
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Thread thread;
  private final Object lock = new Object();
  private final Map<Path, Pair<WatchKey,List<Registration>>> current = new HashMap<>();
  private volatile boolean closing;

  public DefaultFileWatchService(){
    watchService = call(()->FileSystems.getDefault().newWatchService());
    thread = new Thread(this::run, "filewatch");
    thread.start();
  }

  @Override
  public void close() {
    closing = true;
    call(()->watchService.close());
    call(()->thread.join());
    executor.shutdown();
    verify(call(()->executor.awaitTermination(60, TimeUnit.SECONDS)));
    LOG.info("Closed.");
  }

  @Override
  public Bytes readFile(final Path file, final Function<Bytes, Boolean> changedCallback) {
    verify(file, Files::isRegularFile);
    final Path f = file.toAbsolutePath();
    final Bytes result = ByteUtils.read(f);
    watch(f, result.hash(), changedCallback);
    return result;
  }

  @Override
  public void watch(final Path file, final Hash256 hash, final Function<Bytes, Boolean> changedCallback) {
    verify(file, Files::isRegularFile);
    final Registration reg = new Registration(file.toAbsolutePath(), hash, changedCallback);
    watch(reg);
    LOG.info("Started watching {}.", reg);
  }

  private void watch(final Registration registration) {
    addRegistration(registration);
    final Bytes actualContent = ByteUtils.read(registration.file);
    final Hash256 actualHash = actualContent.hash();
    if(!actualHash.equals(registration.hash)){
      remove(registration);
      executor.execute(()->doCallback(registration));
    }
  }

  private void remove(final Registration registration) {
    synchronized(lock){
      final Path directory = registration.file.getParent();
      tryGet(current, directory).ifPresent(pair->{
        final List<Registration> list = pair.getValue();
        list.remove(registration);
        LOG.debug("Stopped watching {}.", registration);
        if(list.isEmpty()){
          pair.getKey().cancel();
          current.remove(directory);
          LOG.debug("Stopped watching directory {}.", directory);
        }
      });
    }
  }

  private void addRegistration(final Registration registration) {
    synchronized(lock){
      final Path directory = notNull(registration.file.getParent());
      current
        .computeIfAbsent(directory, d->{
          final WatchKey key = call(()->directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY));
          verifyEqual(key.watchable(), directory);
          LOG.debug("Watching directory {}.", directory);
          return pair(key, new ArrayList<>());
        })
        .getValue()
        .add(registration)
      ;
      LOG.debug("Watching {}.", registration);
    }
  }

  private void run(){
    while(!closing){
      call(()->{
        try {
          final WatchKey key = watchService.take();
          final Path directory = (Path) key.watchable();
          final List<WatchEvent<?>> events = key.pollEvents();
          if(LOG.isDebugEnabled()){
            LOG.debug(
              "Changes detected in directory {}:\n{}",
              directory,
              ( events.stream()
                .map(e->format("  Event: {} | {} | {}", e.context(), e.kind(), e.count()))
                .collect(joining("\n"))
              )
            );
          }
          final IList<Registration> matchingRegistrations = removeMatchingRegistrations(key, events);
          matchingRegistrations.forEach(r->executor.execute(()->doCallback(r)));
        }
        catch (final ClosedWatchServiceException e) {
          if(!closing) throw e;
          else LOG.debug("Closing, ignoring {}.", e.getClass().getName());
        }
      });
    }
  }

  private void doCallback(final Registration r){
    try{
      if(!closing){
        final Bytes content = ByteUtils.read(r.file);
        final Hash256 hash = content.hash();
        if(hash.equals(r.hash)){
          LOG.debug("File {} has not changed, watching it again.", r);
          watch(r.file, hash, r.changedCallback);
        }
        else{
          LOG.info("File {} has changed, running callback (content: {}).", r, hash);
          boolean active = false;
          active = r.changedCallback.apply(content);
          if(active && !closing){
            LOG.debug("Callback for {} done, resume watching.", r);
            r.updateHash(hash);
            watch(r);
          }else{
            LOG.debug("Callback for {} done.", r);
            if(active) LOG.info("Stopped watching {} (closing).", r);
            else LOG.info("Stopped watching {} (no more interest).", r);
          }
        }
      }
      else{
        LOG.info("Stopped watching {} (closing).", r);
      }
    }
    catch(final Throwable t){
      LOG.error(format("Callback for {} failed.", r), t);
      throw t;
    }
  }

  private IList<Registration> removeMatchingRegistrations(final WatchKey key, final List<WatchEvent<?>> events) {
    final Path directory = (Path) key.watchable();
    synchronized(lock){
      final Opt<Pair<WatchKey, List<Registration>>> optRegistrations = tryGet(current, directory);
      if(!optRegistrations.isPresent()){
        key.cancel();
        return emptyList();
      }
      else{
        final Pair<WatchKey, List<Registration>> dirEntry = optRegistrations.get();
        verifyEqual(key, dirEntry.getKey());
        final List<Registration> registrations = dirEntry.getValue();
        verify(!registrations.isEmpty());
        final IList.Builder<Registration> pending = listBuilder();
        key.reset();
        events.stream()
          .filter(e->StandardWatchEventKinds.ENTRY_MODIFY.equals(e.kind()))
          .forEach(event->{
            final Path file = directory.resolve((Path) event.context());
            final Iterator<Registration> it = registrations.iterator();
            while(it.hasNext()){
              final Registration r = it.next();
              if(r.file.equals(file)){
                pending.add(r);
                it.remove();
                LOG.debug("Stopped watching {} - callback pending.", r);
              }
            }
          })
        ;
        if(registrations.isEmpty()){
          key.cancel();
          current.remove(directory);
          LOG.debug("Stopped watching directory {}.", directory);
        }
        return pending.build();
      }
    }
  }

  private static final class Registration{
    private static final AtomicLong ID_COUNTER = new AtomicLong();
    private final long id = ID_COUNTER.incrementAndGet();

    private final Path file;
    private Hash256 hash;
    private final Function<Bytes, Boolean> changedCallback;

    private Registration(final Path file, final Hash256 hash, final Function<Bytes, Boolean> changedCallback) {
      this.file = file;
      this.hash = hash;
      this.changedCallback = changedCallback;
    }

    private void updateHash(final Hash256 hash) {
      this.hash = hash;
    }

    @Override
    public String toString() {
      return file+" (reg#"+id+")";
    }
  }

}
