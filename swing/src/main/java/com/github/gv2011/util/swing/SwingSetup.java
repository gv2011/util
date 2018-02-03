package com.github.gv2011.util.swing;

/*-
 * #%L
 * util-swing
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
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
