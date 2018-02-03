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
import static com.github.gv2011.util.CollectionUtils.single;
import static com.github.gv2011.util.ex.Exceptions.call;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Path;

public class HttpServerImp implements HttpServer{

  private final Server jetty;

  public HttpServerImp(final IMap<Path, RequestHandler> handlers) {
    jetty = new Server(80);
    final Handler dispatcher = new Dispatcher(handlers);
    jetty.setHandler(dispatcher );
    call(jetty::start);
  }

  @Override
  public void close() {
    call(jetty::stop);
    call(jetty::join);
  }

  @Override
  public int port() {
    return ((AbstractNetworkConnector)single(jetty.getConnectors())).getPort();
  }

}
