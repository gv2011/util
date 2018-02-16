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

import static com.github.gv2011.util.FileUtils.getStream;
import static com.github.gv2011.util.ResourceUtils.getResourceUrl;
import static com.github.gv2011.util.Verify.tryCast;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

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
    return readProperties(()->FileUtils.tryGetStream(file));
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

    public Map<String,String> asMap() {
      return new StringMap();
    }

    private class StringMap extends AbstractMap<String, String> {
      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public Set entrySet() {
        return SafeProperties.this.entrySet();
      }
    }

    public void store(final Path path) {
      call(()->store(FileUtils.writer(path), ""));
    }
  }

  public static void writeProperties(final Map<String, ?> props, final Path path) {
    final SafeProperties sp = new SafeProperties();
    props.keySet().stream().sorted().forEach(k->sp.put(k, props.get(k).toString()));
    sp.store(path);
  }

}
