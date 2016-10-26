package com.github.gv2011.util;

import static com.github.gv2011.util.FileUtils.getReader;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.run;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtils {

  public static SafeProperties readProperties(final String first, final String... more){
    final SafeProperties props = new SafeProperties();
    run(()->{
      try(Reader r = getReader(first, more)){
        props.load(r);
      }
    });
    return props;
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

    @Override
    public String getProperty(final String key) {
      final String result = super.getProperty(key);
      if(result==null) throw new IllegalStateException(format("Property {} is missing.", key));
      return result;
    }

    public List<String> getMultiple(final String key) {
      return PropertyUtils.getMultiple(this, key);
    }

  }
}
