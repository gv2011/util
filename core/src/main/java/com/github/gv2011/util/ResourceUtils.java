package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
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
 * #L%
 */




import static com.github.gv2011.util.CollectionUtils.atMostOne;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.LegacyCollections.asIterator;
import static com.github.gv2011.util.StreamUtils.readText;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.ISet;

public class ResourceUtils {

  private ResourceUtils(){staticClass();}

  public static final URL getResourceUrl(final String resourceName){
    return tryGetResourceUrl(resourceName)
      .orElseThrow(()->new IllegalStateException(format("No resource with name {}.", resourceName)))
    ;
  }

  public static final Optional<URL> tryGetResourceUrl(final String resourceName){
    return atMostOne(
      asIterator(
        call(()->Thread.currentThread().getContextClassLoader().getResources(resourceName))
      ),
      ()->format("Multiple resources found for name \"{}\".", resourceName)
    );
  }

  public static final URL getResourceUrl(final Class<?> refClass, final String relativeName){
    return getResourceUrl(resolveRelativeName(refClass, relativeName));
  }

  public static final Optional<URL> tryGetResourceUrl(final Class<?> refClass, final String relativeName){
    return tryGetResourceUrl(resolveRelativeName(refClass, relativeName));
  }


  public static String resolveRelativeName(final Class<?> refClass, final String name) {
    final String result;
    if(name.startsWith("/")) result = name;
    else{
      Class<?> c = refClass;
      while (c.isArray()) {
        c = c.getComponentType();
      }
      final String baseName = c.getName();
      final int index = baseName.lastIndexOf('.');
      if (index != -1) {
        result = baseName.substring(0, index).replace('.', '/')+"/"+name;
      }
      else result = name;
    }
    return result;
  }

  public static final String getTextResource(final Class<?> refClass, final String relativeName){
    return StreamUtils.readText(getResourceUrl(refClass, relativeName)::openStream);
  }

  public static final Bytes getBinaryResource(final Class<?> refClass, final String relativeName){
    return ByteUtils.copyFromStream(getResourceUrl(refClass, relativeName)::openStream);
  }

  public static final ISet<String> getTextResources(final String resourceName){
    return
        LegacyCollections.stream(
          call(()->Thread.currentThread().getContextClassLoader().getResources(resourceName))
        )
        .map((Function<URL,String>)u->readText(u::openStream))
        .collect(toISet())
    ;
  }

  public static final ISet<URL> getResourceUrls(final String resourceName){
    return
        LegacyCollections.stream(
          call(()->Thread.currentThread().getContextClassLoader().getResources(resourceName))
        )
        .collect(toISet())
    ;
  }

  public static URL getClassResource(final Class<?> refClass, final String fileNameExtension) {
    return getResourceUrl(refClass, refClass.getSimpleName()+"."+fileNameExtension);
  }

}
