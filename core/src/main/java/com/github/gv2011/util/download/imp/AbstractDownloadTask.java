package com.github.gv2011.util.download.imp;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.Verify.verifyIn;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.logExceptions;
import static com.github.gv2011.util.icol.ICollections.setOf;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.download.DownloadTask;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.lock.Lock;


abstract class AbstractDownloadTask implements DownloadTask{

  private static final Duration UPDATE_INTERVAL = Duration.ofSeconds(1);

  private static final Logger LOG = LoggerFactory.getLogger(AbstractDownloadTask.class);

  private static final AtomicInteger COUNTER = new AtomicInteger();
  private static final BeanType<StatusInfo> STATUS_INFO_TYPE = BeanUtils.typeRegistry().beanType(StatusInfo.class);


  final Lock lock = Lock.create();
  final AtomicLong total = new AtomicLong();

  private final int id = COUNTER.getAndIncrement();

  private Status status = Status.INITIAL;
  private final Thread thread;
  private final Thread statusThread;
  private final Consumer<StatusInfo> statusListener;
  private float throttle = Float.POSITIVE_INFINITY;


  AbstractDownloadTask(final Consumer<StatusInfo> statusListener, final Consumer<StatusInfo> onFinished) {
    this.statusListener = statusListener;
    thread = new Thread(
      ()->{
        boolean success = false;
        try{
          success = run();
        } catch(final Throwable t){
          LOG.error(format("Exception in {}.", Thread.currentThread()), t);
          lock.run(()->setStatus(Status.ERROR));
        }
        if(success){
          lock.run(()->{if(status.equals(Status.RUNNING)) setStatus(Status.FINISHED);});
        }
        final StatusInfo statusInfo = getStatusInfo();
        new Thread(
          logExceptions(()->onFinished.accept(statusInfo)),
          format("{}-onFinished", this)
        ).start();
      },
      format("{}-downloader", this)
    );
    statusThread = new Thread(logExceptions(this::statusUpdates), format("{}-statusUpdater", this));
    LOG.info("{} created.", this);
  }

  private void setStatus(final Status newStatus){
    assert lock.isLocked();
    status = newStatus;
    lock.publish();
  }

  @Override
  public final void close() {
    cancel();
  }

  @Override
  public final void start() {
    lock.run(()->{
      verifyEqual(status, Status.INITIAL);
      setStatus(Status.RUNNING);
      thread.start();
      statusThread.start();
      LOG.info("{} started.", this);
    });
  }

  @Override
  public final boolean cancel() {
    final boolean cancelled = lock.get(()->{
      final boolean cancelled2;
      if(status==Status.RUNNING){
        cancelled2 = true;
        setStatus(Status.CANCELLED);
        LOG.info("{} cancelled.", this);
      }
      else cancelled2 = false;
      verifyIn(
        status,
        setOf(Status.FINISHED, Status.CANCELLED, Status.ERROR),
        (exp, act)->format("Could not cancel {}, status: {}.", this, act)
      );
      return cancelled2;
    });
    call(()->thread.join());
    return cancelled;
  }

  @Override
  public final StatusInfo getStatusInfo() {
    return lock.get(()->{
      return STATUS_INFO_TYPE.createBuilder()
        .set(StatusInfo::status).to(status)
        .set(StatusInfo::bytesDone).to(total.get())
        .set(StatusInfo::message).to(format("{}: {} bytes done.", status, total.get()))
        .build()
      ;
    });
  }

  final Status getStatus() {
    return lock.get(()->status);
  }

  final Float getThrottle() {
    return lock.get(()->throttle);
  }

  abstract boolean run();

  private void statusUpdates(){
    final ISet<Status> terminationStates = setOf(Status.CANCELLED, Status.FINISHED, Status.ERROR);
    boolean shouldRun = true;
    while(shouldRun){
      statusListener.accept(getStatusInfo());
      Thread.yield();
      shouldRun = lock.apply(shouldRun, sr->{
        if(terminationStates.contains(status)) return false;
        else {
          lock.await(UPDATE_INTERVAL);
          return sr;
        }
      });
    }
  }



  @Override
  public void setThrottle(final float bytesPerSecond) {
    lock.run(()->throttle=bytesPerSecond);
  }

  @Override
  public String toString() {
    return format("{}-{}", getClass().getSimpleName(), id);
  }

}
