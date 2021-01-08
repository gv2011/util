package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.CollectionUtils.single;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.util.OptionalInt;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Space;
import com.github.gv2011.util.icol.IList;

public class HttpServerImp implements HttpServer{

  private final Server jetty;

  public HttpServerImp(HttpFactoryImp http, final IList<Pair<Space,RequestHandler>> handlers, OptionalInt httpPort) {
    jetty = new Server(httpPort.orElse(80));
    
    disableSendServer();
    
    final Handler dispatcher = new Dispatcher(http, handlers);
    jetty.setHandler(dispatcher);
    
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

  private void disableSendServer() {
    for(Connector cn : jetty.getConnectors()) {
      for(ConnectionFactory cf : cn.getConnectionFactories()) {
        if(cf instanceof HttpConnectionFactory) {
          ((HttpConnectionFactory)cf).getHttpConfiguration().setSendServerVersion(false);
        }
      }
    }
  }


}
