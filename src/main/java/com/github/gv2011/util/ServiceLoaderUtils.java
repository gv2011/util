package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class ServiceLoaderUtils {

  public static <T> T loadService(final Class<T> service){
    final Iterator<T> services = ServiceLoader.load(service).iterator();
    final T result = services.next();
    if(services.hasNext()) throw new IllegalStateException(
      format("Multiple implementations for {} found.", service.getName())
    );
    return result;
  }

}
