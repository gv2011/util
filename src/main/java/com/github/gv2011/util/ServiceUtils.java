package com.github.gv2011.util;

import java.util.ServiceLoader;

public class ServiceUtils {

  public static <S> S getService(final Class<S> interfaze){
    return interfaze.cast(ServiceLoader.load(interfaze).iterator().next());
  }

}
