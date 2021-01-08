package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.asList;
import static com.github.gv2011.util.icol.ICollections.emptySortedMap;
import static com.github.gv2011.util.icol.ICollections.pathFrom;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static java.util.Comparator.comparing;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.HostName;
import com.github.gv2011.util.http.Method;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.http.Space;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.tstr.TypedString;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class Dispatcher extends AbstractHandler {
  
  private static final Logger LOG = getLogger(Dispatcher.class);

  private final HttpFactoryImp http;
  private final IList<Pair<Space,RequestHandler>> handlers;


  Dispatcher(HttpFactoryImp http, final IList<Pair<Space,RequestHandler>> handlers){
    this.http = http;
    this.handlers = handlers;
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
    final Opt<Request> optReq = tryConvert(request);
    Opt<Pair<Space, RequestHandler>> handler = optReq.flatMap(this::selectHandler);
    final Opt<Response> resp = 
      optReq
      .flatMap(req->
        handler
        .map(h->
          h.getValue()
          .handle(
            removePathPrefix(req, pathFrom(h.getKey().path()))
          )
        )
      )
    ;
    LOG.info(
      "Host: {}, path: {}, params: {}, handler: {}, status: {}, entity: {} bytes.", 
      optReq.flatMap(Request::host),
      optReq.map(Request::path),
      optReq.map(Request::parameters).orElse(emptySortedMap()),
      handler,
      resp.map(r->r.statusCode().code()).orElse(404),
      resp.flatMap(Response::entity).map(e->Long.toString(e.content().longSize())).orElse("-")
    );
    resp
      .ifPresent(
        r->write(r, response)
      ).orElseDo(()->
        response.setStatus(404)
      )
    ;
      
  }

  private Request removePathPrefix(Request req, Path prefix) {
    return BeanUtils.beanBuilder(Request.class)
      .setAll(req)
      .set(Request::path).to(http.getPath(req).removePrefix(prefix))
      .build()
    ;
  }

  private Opt<Request> tryConvert(final HttpServletRequest request) {
    try{
      return Opt.of(convert(request));
    }
    catch(RuntimeException e){
      LOG.error("Could not convert request.", e);
      return Opt.empty();
    }
  }

  private Request convert(final HttpServletRequest request) {
    Opt<HostName> host = Opt.ofNullable(request.getHeader("Host"))
      .map(h->{
        final IList<String> parts = StringUtils.split(h, ':');
        verify(parts.size()==1 || parts.size()==2);
        return TypedString.create(HostName.class, parts.get(0));
      })
    ;
    
    Method method = TypedString.create(Method.class, request.getMethod());
    
    IList<String> path = http.decodePath(request.getRequestURI());
    
    ISortedMap<String, IList<String>> parameters = 
      request.getParameterMap().entrySet().stream().collect(toISortedMap(
        Entry::getKey,
        e->asList(e.getValue())
      ))
    ;
    
    final Opt<TypedBytes> entity;
    final Optional<Long> length = request.getContentLengthLong()==-1 ? Optional.empty() : Optional.of(request.getContentLengthLong());
    final Bytes bytes = length
      .map(l->ByteUtils.copyFromStream(call(request::getInputStream), l))
      .orElseGet(()->ByteUtils.copyFromStream(request::getInputStream))
    ;
    if(bytes.isEmpty()) {
      entity = Opt.empty();
    } else{
      final DataType dataType = Opt.ofNullable(request.getContentType())
        .map(t->
          Opt.ofNullable(request.getCharacterEncoding())
          .map(cs->DataType.parse(t).withCharset(Charset.forName(cs)))
          .orElseGet(()->DataType.parse(t))
        )
        .orElse(DataTypes.APPLICATION_OCTET_STREAM)
      ;
      entity = Opt.of(bytes.typed(dataType));
    }

    return BeanUtils.beanBuilder(Request.class)
      .set(Request::host).to(host)
      .set(Request::method).to(method)
      .set(Request::path).to(path)
      .set(Request::parameters).to(parameters)
      .set(Request::entity).to(entity)
      .build()
    ;
  }

  private void write(final Response handlerResponse, final HttpServletResponse httpResponse) {
    httpResponse.setStatus(handlerResponse.statusCode().code());
    handlerResponse.entity().ifPresent(e->{
      httpResponse.setContentType(e.dataType().toString());
      e.dataType().charset().ifPresent(
        cs->httpResponse.setCharacterEncoding(cs.name())
      );
      httpResponse.setContentLengthLong(e.content().longSize());
      final ServletOutputStream out = call(httpResponse::getOutputStream);
      e.content().write(out);
    });
  }


  private Opt<Pair<Space,RequestHandler>> selectHandler(final Request request) {
    return handlers.stream()
      .filter(e->pathMatches(request, e.getKey()))
      .sorted(
         comparing((Pair<Space,RequestHandler> p)->p.getKey().path().size())
         .reversed()
      )
      .filter(e->e.getValue().accepts(request))
      .tryFindFirst()
    ;
  }
  
  private boolean pathMatches(Request request, Space space){
    boolean matches = true;
    if(space.host().isPresent()){
      if(!request.host().equals(space.host())) matches = false;
    }
    if(matches){
      if(!pathFrom(request.path()).startsWith(pathFrom(space.path()))) matches = false;
    }
    return matches;
  }



}
