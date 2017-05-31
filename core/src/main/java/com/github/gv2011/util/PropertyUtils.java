package com.github.gv2011.util;

import static com.github.gv2011.util.FileUtils.getStream;
import static com.github.gv2011.util.ResourceUtils.getResourceUrl;
import static com.github.gv2011.util.Verify.tryCast;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import com.github.gv2011.util.ex.ThrowingSupplier;

public final class PropertyUtils {

  private PropertyUtils(){staticClass();}

  public static SafeProperties readProperties(final Class<?> refClass){
    return readProperties(refClass, refClass.getSimpleName()+".properties");
  }

  public static SafeProperties readProperties(final Class<?> refClass, final String relativeName){
    return readProperties(getResourceUrl(refClass, relativeName)::openStream);
  }

  public static SafeProperties readProperties(final String first, final String... more){
    return readProperties(()->getStream(first, more));
  }

  public static SafeProperties readProperties(final Path file) {
    return readProperties(()->Files.newInputStream(file));
  }

  public static SafeProperties readProperties(final ThrowingSupplier<InputStream> streamSupplier){
    return callWithCloseable(streamSupplier, s->{
      final SafeProperties props = new SafeProperties();
      props.load(new InputStreamReader(s, UTF_8));
      return props;
    });
  }

  public static List<String> getMultiple(final Properties props, final String key){
    final List<String> result = new ArrayList<>();
    final String value = props.getProperty(key);
    if(value!=null){
      for(String element: value.split(",")){
        element = element.trim();
        if(!element.isEmpty()) result.add(element);
      }
    }
    return result;
  }

  public static final class SafeProperties extends Properties{

    /**
     * @deprecated Use getProperty instead.
     */
    @Override
    @Deprecated //Use getProperty
    public String get(final Object key) {
      return getProperty(tryCast(key, String.class).get());
    }

    @Override
    public String getProperty(final String key) {
      return tryGet(key)
        .orElseThrow(()->new NoSuchElementException(format("There is no property {}.", key)))
      ;
    }

    public Optional<String> tryGet(final String key) {
      return Optional.ofNullable(super.getProperty(key));
    }

    public List<String> getMultiple(final String key) {
      return PropertyUtils.getMultiple(this, key);
    }


  }

}
