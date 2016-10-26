package com.github.gv2011.util.log.logback;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.github.gv2011.util.log.LogAdapter;

import ch.qos.logback.classic.LoggerContext;

public class LogbackLogAdapter implements LogAdapter{

  @Override
  public void configureLogging() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  @Override
  public void shutdownLogging() {
    final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.stop();
  }

}
