package com.github.gv2011.util;

import static com.github.gv2011.util.Constants.softRefConstant;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class ServiceLoaderUtils {

  private ServiceLoaderUtils(){staticClass();}

  public static <T> T loadService(final Class<T> service){
    final Iterator<T> services = ServiceLoader.load(service).iterator();
    if(!services.hasNext()) throw new IllegalStateException(
      format("No implementations for {} found.", service.getName())
    );
    final T result = services.next();
    if(services.hasNext()) throw new IllegalStateException(
      format("Multiple implementations for {} found.", service.getName())
    );
    return result;
  }

  public static <T> Constant<T> lazyServiceLoader(final Class<T> service){
    return softRefConstant(()->loadService(service));
  }

}
