package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.CollectionUtils.toSingle;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.asList;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static java.util.function.Predicate.not;
import java.util.Map.Entry;

import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.slf4j.Logger;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.Method;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.tstr.TypedString;

import jakarta.servlet.http.HttpServletRequest;

final class RequestConverter {

  private static final Logger LOG = getLogger(RequestConverter.class);

  private final HttpFactoryImp http;

  RequestConverter(final HttpFactoryImp http) {
    this.http = http;
  }

  final Request convert(final HttpServletRequest request) {
    final Domain host = Opt.ofNullable(request.getHeader("Host"))
      .map(String::trim)
      .filter(not(String::isEmpty))
      .map(this::stripPort)
      .map(Domain::parse)
      .filter(not(Domain::isInetAddress))
      .orElse(Domain.LOCALHOST)
    ;

    final boolean secure = request.isSecure();
    final IList<X509Certificate> peerCertificateChain;
    if(secure){
      final ExtendedSSLSession session =
        Opt.ofNullable(request.getAttribute("org.eclipse.jetty.servlet.request.ssl_session"))
        .tryCast(ExtendedSSLSession.class)
        .get()
      ;
      checkSelectedServerCertificate(host, session);
      peerCertificateChain = getPeerCertificateChain(session);
      final Opt<Domain> sniHost = getSniHost(session);
      sniHost.ifPresentDo(sni->verifyEqual(host, sni));
      verifyEqual(Domain.from(session.getLocalPrincipal()), host);
    }
    else{
      peerCertificateChain = emptyList();
    }

    final Method method = TypedString.create(Method.class, request.getMethod());

    final IList<String> path = http.decodePath(request.getRequestURI());

    final ISortedMap<String, IList<String>> parameters =
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
      .set(Request::secure).to(secure)
      .set(Request::peerCertificateChain).to(peerCertificateChain)
      .set(Request::method).to(method)
      .set(Request::path).to(path)
      .set(Request::parameters).to(parameters)
      .set(Request::entity).to(entity)
      .build()
    ;
  }

  private String stripPort(final String hostAndOptionalPort) {
    final IList<String> parts = StringUtils.split(hostAndOptionalPort, ':');
    verify(parts.size()==1 || parts.size()==2);
    return parts.get(0).trim();
  }

  private void checkSelectedServerCertificate(final Domain host, final ExtendedSSLSession session) {
    verifyEqual(Domain.from(((X509Certificate)session.getLocalCertificates()[0]).getSubjectDN()), host);
  }

  private IList<X509Certificate> getPeerCertificateChain(final ExtendedSSLSession session) {
    try{
      return Arrays.stream(session.getPeerCertificates()).map(c->(X509Certificate)c).collect(toIList());
    }
    catch (final SSLPeerUnverifiedException e) {
      return emptyList();
    }
    catch(final RuntimeException e){
      LOG.error("Could not obtain peer certificate chain.", e);
      return emptyList();
    }
  }

  private Opt<Domain> getSniHost(final ExtendedSSLSession session) {
    final List<SNIServerName> sniNames = session.getRequestedServerNames();
    try{
      return Opt.of(sniNames.stream()
        .filter(sni->sni instanceof SNIHostName)
        .map(sni->{
          verifyEqual(sni.getType(), 0);
          return Domain.parse(((SNIHostName)sni).getAsciiName());
        })
        .collect(toSingle()))
      ;
      }
    catch(final RuntimeException e){
      LOG.error(format("Could not obtain sni name from {}.", sniNames), e);
      return Opt.empty();
    }
  }


}
