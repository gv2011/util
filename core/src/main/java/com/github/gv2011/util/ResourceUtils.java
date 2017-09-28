package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.atMostOne;
import static com.github.gv2011.util.LegacyCollections.asIterator;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.URL;
import java.util.Optional;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

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



}
