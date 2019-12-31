package com.github.gv2011.http.server;

import java.time.Duration;

import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.time.TimeUtils;

public class HttpServerImpMtst {

  public static void main(final String[] args) {
    try(HttpServer server = new HttpServerImp(ICollections.emptyMap())){
      TimeUtils.sleep(Duration.ofHours(1));
    }

  }

}
