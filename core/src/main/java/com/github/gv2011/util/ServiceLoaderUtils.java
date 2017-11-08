package com.github.gv2011.util;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * %---license-end---
 */



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
