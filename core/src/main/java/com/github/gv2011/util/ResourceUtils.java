package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.single;
import static com.github.gv2011.util.LegacyCollections.asIterator;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.net.URL;

public class ResourceUtils {

  private ResourceUtils(){staticClass();}

  public static final URL getResourceUrl(final String resourceName){
    return single(
      asIterator(
        call(()->Thread.currentThread().getContextClassLoader().getResources(resourceName))
      )
    );
  }

}
