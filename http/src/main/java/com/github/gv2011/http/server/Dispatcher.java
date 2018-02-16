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
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.handler.AbstractHandler;

import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.Method;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Path;

final class Dispatcher extends AbstractHandler {

  Dispatcher(final IMap<Path, RequestHandler> handlers){
  }


  private Request convert(final HttpServletRequest request) {
     return new Request() {
      @Override
      public Optional<TypedBytes> entity() {
        // TODO Auto-generated method stub
        throw notYetImplementedException();
      }
      @Override
      public Method method() {
        // TODO Auto-generated method stub
        throw notYetImplementedException();
      }
      @Override
      public Path path() {
        // TODO Auto-generated method stub
        throw notYetImplementedException();
      }
    };
  }


  @Override
  public boolean isRunning() {
    return true;
  }

  @Override
  public void handle(
      final String target,
      final org.eclipse.jetty.server.Request baseRequest,
      final HttpServletRequest request,
      final HttpServletResponse response
  ) throws IOException, ServletException {
    final String url = request.getRequestURI();
    final Optional<RequestHandler> handler = selectHandler(url);
    if(handler.isPresent()) {
      write(handler.get().handle(convert(request)), response);
    }
    else {
      response.setStatus(404);
    }
  }

  private void write(final Response handlerResponse, final HttpServletResponse httpResponse) {
    throw notYetImplementedException();
  }


  private Optional<RequestHandler> selectHandler(final String url) {
    throw notYetImplementedException();
  }



}
