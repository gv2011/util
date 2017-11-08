package com.github.gv2011.util.main;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.tryAll;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.JmxUtils;
import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.log.LogAdapter;

public class MainUtils implements MainUtilsMBean{

	public static interface ServiceBuilder<S extends AutoCloseableNt> extends AutoCloseableNt{
		S startService(String[] args) throws Exception;
	}

	private final Logger LOG;

	private final AtomicBoolean started;
	private final Path pidFile;
	private final AtomicLong uncaughtExceptionCount = new AtomicLong();
	private final Instant startTime = Instant.now();
  private final LogAdapter logAdapter;

  private volatile @Nullable ServiceBuilder<?> serverFactory;
  private volatile @Nullable AutoCloseableNt jmxHandle;

	public MainUtils() {
	  logAdapter = ServiceLoaderUtils.loadService(LogAdapter.class);
	  logAdapter.configureLogging();
		LOG = LoggerFactory.getLogger(MainUtils.class);
		pidFile = FileSystems.getDefault().getPath("log/pid").toAbsolutePath();
		started = new AtomicBoolean();
	}


	/**
	 * Runs a service. Shutdown by SIGINT.
	 */
	public void runMain(final String[] mainArgs, final ServiceBuilder<?> serviceBuilder){
		if(started.getAndSet(true)) throw new IllegalStateException("Started before.");
		final String[] args = mainArgs.clone();
		serverFactory = serviceBuilder;
		//Log all uncaught exceptions:
		Thread.setDefaultUncaughtExceptionHandler(
			(final Thread t, final Throwable e) -> {
				uncaughtExceptionCount.incrementAndGet();
				LOG.error(format("Uncaught exception in thread {}", t), e);
			}
		);
		logPid();
		jmxHandle = JmxUtils.registerMBean(this);
		try {
			final AutoCloseableNt service = serviceBuilder.startService(args.clone());
			prepareShutdownAndWaitForIt(service);
		} catch (final Throwable t) {
			//Don't rely on System.out.
			LOG.error("Error in main method. Terminating.", t);
			System.exit(1);
		}
	}

	private void logPid() {
		int pid = -1;
		try {
			final String runTimeName = ManagementFactory.getRuntimeMXBean().getName();
			pid = Integer.parseInt(runTimeName.substring(0, runTimeName.indexOf('@')));
			LOG.warn("Started. Process ID (pid) is {}.", pid);
			Files.createDirectories(pidFile.getParent());
			Files.write(pidFile, Integer.toString(pid).getBytes(StandardCharsets.UTF_8),
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			LOG.debug("Written process id {} to {}.", pid, pidFile);
		} catch (final Exception e) {
			LOG.warn(format("Could not determine or write process ID (pid) {}.", pid), e);
		}
		LOG.info("Running as user {}.", System.getProperty("user.name"));
	}

	private void removePid(){
		run(()->Files.delete(pidFile));
		LOG.info("Removed pid file {}.", pidFile);
	}


	public void prepareShutdownAndWaitForIt(final AutoCloseableNt service)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			latch.countDown();
			runShutdown(service);
		},"shutdown-hook"));
		latch.await();
	}

	private void runShutdown(final AutoCloseableNt service){
		LOG.info("Server shutdown started.");
		try{
			tryAll(
				()->{
					service.close();
					LOG.info("Server succesfully shut down.");
				},
				()->{
					serverFactory.close();
					LOG.info("Server factory closed.");
				},
				()->{
					jmxHandle.close();
					LOG.info("Unregistered MBean.");
				},
				()->{
					removePid();
				}
			);
		}catch(final Throwable t){
			LOG.error("", t);
		} finally{
		  LOG.info("Shutting down log system now. Goodbye.");
		  logAdapter.shutdownLogging();
		}
	}

	@Override
	public void shutdown() {
		System.exit(0);
	}


	@Override
	public long getUncaughtExceptionCount() {
		return uncaughtExceptionCount.get();
	}


	@Override
	public Instant getStartTime() {
		return startTime;
	}



}
