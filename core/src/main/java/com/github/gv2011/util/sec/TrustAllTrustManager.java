package com.github.gv2011.util.sec;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

public final class TrustAllTrustManager extends X509ExtendedTrustManager{

  @Override
  public void checkClientTrusted(final X509Certificate[] chain, final String authType){
  }

  @Override
  public void checkServerTrusted(final X509Certificate[] chain, final String authType){
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket){
  }

  @Override
  public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket){
  }

  @Override
  public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine){
  }

  @Override
  public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine)
      throws CertificateException {
  }

}
