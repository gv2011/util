package com.github.gv2011.http.imp;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Optional;

import com.github.gv2011.http.server.HttpServerImp;
import com.github.gv2011.util.bytes.TypedBytes;
/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2017 Vinz (https://github.com/gv2011)
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
import com.github.gv2011.util.http.HttpFactory;
import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.http.RestClient;
import com.github.gv2011.util.http.StatusCode;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Path;

public final class HttpFactoryImp implements HttpFactory{

  @Override
  public RestClient createRestClient() {
    return ApacheRestClient.createInstance();
  }

  @Override
  public HttpServer createServer(final IMap<Path, RequestHandler> handlers) {
    return new HttpServerImp(handlers);
  }

  @Override
  public Response createResponse() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Response createResponse(final TypedBytes entity) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Response createResponse(final StatusCode statusCode, final Optional<TypedBytes> entity) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
