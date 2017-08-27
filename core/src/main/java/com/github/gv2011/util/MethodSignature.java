package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.Comparison.compareByAttribute;
import static com.github.gv2011.util.Comparison.listComparator;

import java.lang.reflect.Method;
import java.util.Comparator;

import com.github.gv2011.util.icol.IList;

public final class MethodSignature implements Comparable<MethodSignature>{
  
  private static Comparator<IList<Class<?>>> PCOMP = listComparator(compareByAttribute(Class::getName));
  
  private final String name;
  private final IList<Class<?>> parameters;
  
  public MethodSignature(final Method m) {
    name = m.getName();
    parameters = iCollections().asList(m.getParameterTypes());
  }

  public String name() {
    return name;
  }
  
  public IList<Class<?>> parameters(){
    return parameters;
  }

  @Override
  public int hashCode() {
    return Equal.hashCode(MethodSignature.class, name, parameters);
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(this, obj, MethodSignature.class);
  }

  @Override
  public String toString() {
    return name+parameters;
  }

  @Override
  public int compareTo(final MethodSignature o) {
    int result = name.compareTo(o.name);
    if(result==0)  result = PCOMP.compare(parameters, o.parameters);
    return result;
  }

}
