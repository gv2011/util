package com.github.gv2011.http.server;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

abstract class AbstractHandler2 implements Handler{

  @Override
  public void start() throws Exception {
    unsupported();
  }

  @Override
  public void stop() throws Exception {
    unsupported();
  }

  @Override
  public boolean isRunning() {
    return unsupported();
  }

  @Override
  public boolean isStarted() {
    return unsupported();
  }

  @Override
  public boolean isStarting() {
    return unsupported();
  }

  @Override
  public boolean isStopping() {
    return unsupported();
  }

  @Override
  public boolean isStopped() {
    return unsupported();
  }

  @Override
  public boolean isFailed() {
    return unsupported();
  }

  @Override
  public void setServer(final Server server) {
  }

  @Override
  public Server getServer() {
    return unsupported();
  }

  @Override
  public void destroy() {
    unsupported();
  }

  private <T> T unsupported() {
    throw new UnsupportedOperationException();
  }

}
