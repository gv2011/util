package com.github.gv2011.util.beans.imp;

/*-
 * #%L
 * util-beans
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.CollectionUtils.listOf;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.util.Optional;

import org.slf4j.Logger;

import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.beans.Abstract;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.beans.TypeName;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.IList;

final class DefaultAnnotationHandler implements AnnotationHandler{

  private static final Logger LOG = getLogger(DefaultAnnotationHandler.class);

  @SuppressWarnings("rawtypes")
  @Override
  public Optional<Class<? extends TypeResolver>> typeResolver(final Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(Abstract.class))
      .flatMap(a->{
        final Class<? extends TypeResolver> typeResolver = a.typeResolver();
        if(typeResolver.equals(TypeResolver.class)) return Optional.empty();
        else return Optional.of(typeResolver);
      })
    ;
  }

  @Override
  public boolean declaredAsAbstract(final Class<?> clazz) {
    return clazz.getAnnotation(Abstract.class)!=null;
  }

  @Override
  public boolean isPolymorphicRoot(final Class<?> clazz) {
    final boolean result = Optional.ofNullable(clazz.getAnnotation(Abstract.class))
      .map(a->!subClasses(a).isEmpty())
      .orElse(false)
    ;
    LOG.debug("{} {} a polymorphic root.", clazz, result?"is":"is not");
    return result;
  }

  @Override
  public Optional<String> defaultValue(final Method m) {
    return Optional.ofNullable(m.getAnnotation(DefaultValue.class)).map(DefaultValue::value);
  }

  @Override
  public Optional<String> fixedValue(final Method m) {
    return Optional.ofNullable(m.getAnnotation(FixedValue.class)).map(FixedValue::value);
  }

  @Override
  public IList<Class<?>> subClasses(final Class<?> clazz) {
    return subClasses(notNull(clazz.getAnnotation(Abstract.class)));
  }

  private IList<Class<?>> subClasses(final Abstract annotation) {
    final Class<?>[] subClasses = annotation.subClasses();
    verify(subClasses.length>0);
    if(subClasses.length==1 && subClasses[0].equals(Nothing.class)) return listOf();
    else return listOf(subClasses);
  }

  @Override
  public Optional<String> typeName(final Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(TypeName.class)).map(TypeName::value);
  }

}
