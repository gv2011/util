package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.tryGet;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.pathFrom;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.URIUtil;
import org.slf4j.Logger;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.http.Space;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class Dispatcher extends AbstractHandler {

  private static final Logger LOG = getLogger(Dispatcher.class);

  private final HttpFactoryImp http;
  private final IList<Pair<Space,RequestHandler>> handlers;

  private final Map<Domain,Pair<Path,TypedBytes>> activeTokens = new ConcurrentHashMap<>();

  private final Predicate<Domain> isHttpsHost;

  private final Consumer<Domain> httpsActivator;
  private final RequestConverter requestConverter;

  Dispatcher(
    final HttpFactoryImp http, 
    Predicate<Domain> isHttpsHost, 
    final IList<Pair<Space,RequestHandler>> handlers,
    Consumer<Domain> httpsActivator
  ){
    this.http = http;
    this.isHttpsHost = isHttpsHost;
    this.handlers = handlers;
    this.httpsActivator = httpsActivator;
    this.requestConverter = new RequestConverter(http);
  }

  @Override
  public boolean isRunning() {
    return true;
  }

  @Override
  public void handle(
      final String target,
      final org.eclipse.jetty.server.Request baseRequest,
      final HttpServletRequest servletRequest,
      final HttpServletResponse servletResponse
  ) throws IOException, ServletException {
    
    final Opt<Request> request = tryConvert(servletRequest);

    final Opt<TypedBytes> token = getMatchingToken(request);

    final Opt<Pair<Space, RequestHandler>> optSpaceAndHandler;
    
    final Opt<Response> response;
    
    final Opt<String> redirectUrl;
    
    if(token.isPresent()){
      //Token response
      optSpaceAndHandler = Opt.empty();
      redirectUrl = Opt.empty();
      response = token.map(http::createResponse);
    }
    else{      
      if(request.isPresent() ? !servletRequest.isSecure() && isHttpsHost.test(request.get().host()) : false){
        //Redirect to https
        httpsActivator.accept(request.get().host());
        redirectUrl = Opt.of(URIUtil.newURI(
          "https",
          stripWww(baseRequest.getServerName()),
          443,
          baseRequest.getRequestURI(),
          baseRequest.getQueryString()
        ));
        optSpaceAndHandler = Opt.empty();
        response = Opt.empty();
      }
      else{
        //Regular response
        redirectUrl = Opt.empty();
        optSpaceAndHandler = request.flatMap(this::selectHandler);
        response = request
          .flatMap(req->
            optSpaceAndHandler
            .map(spaceAndHandler->{
              final RequestHandler hndlr = spaceAndHandler.getValue();
              return hndlr.handle(removePathPrefix(req, pathFrom(spaceAndHandler.getKey().path())));
            })
          )
        ;
      }
    }
    
    //Log request:
    if(LOG.isInfoEnabled())LOG.info(
      Stream.of(
        Opt.of("From "+servletRequest.getRemoteHost()),
        request.map(r->"host: "+r.host()),
        request.map(r->"path: "+r.path()),
        request.flatMap(r->r.parameters().isEmpty() ? Opt.empty() : Opt.of(r.parameters())),
        token.map(t->"token"),
        redirectUrl.map(r->"redirect: "+r),
        optSpaceAndHandler.map(sh->"handler: "+sh),
        response.map(r->"status: "+r.statusCode().code()),
        response.flatMap(Response::entity).map(e->"entity: "+e.content().longSize()+" bytes")
      )
      .filter(Opt::isPresent)
      .map(o->o.get().toString())
      .collect(joining(", "))
    );
    
    response
      .ifPresent(
        r->write(r, servletResponse)
      ).orElseDo(()->{
        redirectUrl.ifPresent(u->{
          servletResponse.setHeader("Location", u);          
          servletResponse.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
          call(servletResponse::flushBuffer);
        }).orElseDo(()->{
          servletResponse.setStatus(HttpStatus.NOT_FOUND_404);
          call(servletResponse::flushBuffer);
        });
      })
    ;

  }

  private String stripWww(String serverName) {
    return StringUtils.tryRemovePrefix(serverName.toLowerCase(Locale.ROOT), "www.").orElse(serverName);
  }

  private Opt<TypedBytes> getMatchingToken(final Opt<Request> optReq) {
    final Opt<TypedBytes> token = optReq
      .flatMap(req->
        tryGet(activeTokens, req.host())
        .flatMap(activeToken->
          req.path().equals(activeToken.getKey())
          ? Opt.of(activeToken.getValue())
          : Opt.empty()
        )
      )
    ;
    return token;
  }

  private Request removePathPrefix(final Request req, final Path prefix) {
    return BeanUtils.beanBuilder(Request.class)
      .setAll(req)
      .set(Request::path).to(http.getPath(req).removePrefix(prefix))
      .build()
    ;
  }

  private Opt<Request> tryConvert(final HttpServletRequest request) {
    try{
      return Opt.of(requestConverter.convert(request));
    }
    catch(final RuntimeException e){
      LOG.error("Could not convert request.", e);
      return Opt.empty();
    }
  }

  private void write(final Response handlerResponse, final HttpServletResponse httpResponse) {
    httpResponse.setStatus(handlerResponse.statusCode().code());
    handlerResponse.entity().ifPresent(e->{
      final DataType dataType = e.dataType();
      httpResponse.setContentType(dataType.toString());
      e.dataType().charset().ifPresent(
        cs->httpResponse.setCharacterEncoding(cs.name())
      );
      final long size = e.content().longSize();
      httpResponse.setContentLengthLong(size);
      final ServletOutputStream out = call(httpResponse::getOutputStream);
      e.content().write(out);
      call(httpResponse::flushBuffer);
      LOG.info("Entity sent: {} bytes, type: {}.", size, dataType);
    });
  }


  private Opt<Pair<Space,RequestHandler>> selectHandler(final Request request) {
    return handlers.stream()
      .filter(e->pathMatches(request, e.getKey()))
      .sorted(
         comparing((final Pair<Space,RequestHandler> p)->p.getKey().path().size())
         .reversed()
      )
      .filter(e->e.getValue().accepts(request))
      .tryFindFirst()
    ;
  }

  private boolean pathMatches(final Request request, final Space space){
    boolean matches = true;
    if(space.host().isPresent()){
      if(!request.host().equals(space.host().get())) matches = false;
    }
    if(matches){
      if(!pathFrom(request.path()).startsWith(pathFrom(space.path()))) matches = false;
    }
    return matches;
  }

  final AutoCloseableNt activate(final Domain host, final Path tokenPath, final TypedBytes token) {
    activeTokens.put(host, pair(tokenPath, token));
    return ()->activeTokens.remove(host);
  }

}
