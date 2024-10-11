package com.github.gv2011.util.swing.imp;

import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;

public final class SwingSetup {

  private static final Logger LOG = getLogger(SwingSetup.class);

  private static final Object LOCK = new Object();
  private static boolean setupDone = false;

  public static void setup() {
    synchronized(LOCK) {
      if(!setupDone) {
        call(()->SwingUtilities.invokeAndWait(()->{
          Thread.currentThread().setUncaughtExceptionHandler(SwingSetup::uncaughtException);
          //run(()->UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        }));
        setupDone = true;
      }
    }
  }

  private static void uncaughtException(final Thread t, final Throwable e) {
    LOG.error("Uncaught exception in Swing thread: ", e);
  }
}
