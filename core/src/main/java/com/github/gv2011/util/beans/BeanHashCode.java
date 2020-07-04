package com.github.gv2011.util.beans;

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toIMap;
import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.icol.ISet;

public final class BeanHashCode {

  private BeanHashCode(){staticClass();}

  @SafeVarargs
  public static <B> ToIntFunction<B> createHashCodeFunction(
    final Class<B> beanInterface, final Function<B,?>... attributes
  ){
    return createHashCodeFunction(beanInterface, Arrays.asList(attributes));
  }

  public static <B> ToIntFunction<B> createHashCodeFunction(
      final Class<B> beanInterface, final Collection<Function<B,?>> attributes
    ){
    return createHashCodeFunctionNamed(
      beanInterface,
      ( attributes.stream()
        .collect(toIMap(
          a->ReflectionUtils.methodName(beanInterface, a),
          a->a
        ))
      )
    );
  }

  public static <B> ToIntFunction<B> createHashCodeFunctionNamed(
    final Class<B> beanClass, final Map<String, Function<B,?>> attributes
  ){
    final int base = beanClass.hashCode() * 31;
    final ISet<ToIntFunction<B>> attributeFunctions =
      attributes.entrySet().stream()
      .map(a->{
        final int attributeNameHash = a.getKey().hashCode();
        final Function<B,?> attributeValeFunction = a.getValue();
        return (ToIntFunction<B>) b->attributeNameHash ^ attributeValeFunction.apply(b).hashCode();
      })
      .collect(toISet())
    ;
    return b->base + attributeFunctions.parallelStream().mapToInt(af->af.applyAsInt(b)).sum();
  }

}
