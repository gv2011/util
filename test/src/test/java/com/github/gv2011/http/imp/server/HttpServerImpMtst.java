package com.github.gv2011.http.imp.server;

import java.time.Duration;
import java.util.OptionalInt;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.time.TimeUtils;

public class HttpServerImpMtst {

  public static void main(final String[] args) {
    try(HttpServer server = new HttpServerImp(new HttpFactoryImp(), ICollections.emptyList(), OptionalInt.of(0))){
      TimeUtils.sleep(Duration.ofHours(1));
    }
  }

}
