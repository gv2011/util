package com.github.gv2011.http.imp;

import static com.github.gv2011.util.icol.ICollections.iCollections;

import java.util.OptionalInt;
import java.util.function.Predicate;

import com.github.gv2011.http.imp.acme.AcmeCertHandler;
import com.github.gv2011.http.imp.acme.AcmeFileStore;
import com.github.gv2011.http.imp.server.HttpServerImp;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.UrlEncoding;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.AcmeStore;
import com.github.gv2011.util.http.CertificateHandler;
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
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.http.RestClient;
import com.github.gv2011.util.http.Space;
import com.github.gv2011.util.http.StatusCode;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.time.Clock;

public final class HttpFactoryImp implements HttpFactory{

  @Override
  public RestClient createRestClient() {
    return ApacheRestClient.createInstance();
  }

  @Override
  public HttpServer createServer(final IList<Pair<Space,RequestHandler>> handlers) {
    return createServer(handlers, OptionalInt.empty(), Opt.empty(), h->false, OptionalInt.empty());
  }

  @Override
  public HttpServer createServer(
    IList<Pair<Space, RequestHandler>> handlers, 
    OptionalInt httpPort, 
    OptionalInt httpsPort,
    AcmeStore acmeStore
  ) {
    final CachedConstant<HttpServerImp> server = Constants.cachedConstant();
    server.set(
      new HttpServerImp(
        this, 
        handlers, 
        httpPort, 
        Opt.of(new AcmeCertHandler(Clock.get(), acmeStore, server::get, acmeStore.production())), 
        new SimpleHttpsDomainPredicate(), 
        httpsPort
      )
    );
    return server.get();
  }

  @Override
  public HttpServer createServer(
    IList<Pair<Space,RequestHandler>> handlers, 
    OptionalInt httpPort,
    Opt<CertificateHandler> certHandler,
    Predicate<Domain> isHttpsHost,
    OptionalInt httpsPort
  ) {
    return new HttpServerImp(this, handlers, httpPort, certHandler, isHttpsHost, httpsPort);
  }

  @Override
  public Response createResponse(final StatusCode statusCode, final Opt<TypedBytes> entity) {
    return BeanUtils.beanBuilder(Response.class)
      .set(Response::statusCode).to(statusCode)
      .set(Response::entity).to(entity)
      .build()
    ;
  }

  @Override
  public StatusCode statusOk() {
    return StatusCodes.OK;
  }

  @Override
  public StatusCode statusNotFound() {
    return StatusCodes.NOT_FOUND;
  }
  
  public Path getPath(Request request){
    return iCollections().pathFrom(request.path());
  }

  public String encodePath(Path path) {
    return UrlEncoding.encodePath(path);
  }

  public Path decodePath(String path) {
    return UrlEncoding.decodePath(StringUtils.removePrefix(path, "/"));
  }

  @Override
  public AcmeStore openAcmeStore(java.nio.file.Path directory) {
    return new AcmeFileStore(directory);
  }

  
}
