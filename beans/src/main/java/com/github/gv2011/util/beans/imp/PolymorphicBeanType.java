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

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;

final class PolymorphicBeanType<T> extends BeanTypeSupport<T> {

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(PolymorphicBeanType.class);

  private final Opt<String> typePropertyName;
  private final TypeNameStrategy typeNameStrategy;


  PolymorphicBeanType(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final BeanFactory beanFactory,
    final Opt<String> typePropertyName,
    final TypeNameStrategy typeNameStrategy
  ) {
    super(beanClass, jf, annotationHandler, beanFactory);
    this.typePropertyName = typePropertyName;
    this.typeNameStrategy = typeNameStrategy;
  }

  @Override
  <V> PropertyImp<T,V> createProperty(final Method m, final TypeSupport<V> type) {
    if(!isTypeProperty(m)) return super.createProperty(m, type);
    else{
      verify(!annotationHandler.defaultValue(m).isPresent());
      final V fixedValue = type.parse(parseTolerant(
        type, jf(),
        (
          annotationHandler.typeName(clazz).merge(annotationHandler.fixedValue(m))
          .orElseGet(()->typeNameStrategy.typeName(clazz))
        )
      ));
      return PropertyImp.createFixed(this, m, typePropertyName.get(), type, fixedValue);
    }
  }

  @Override
  public BeanBuilder<T> createBuilder() {
    return new DefaultBeanBuilder<>(this);
  }

  @Override
  void checkProperties(final ISortedMap<String, PropertyImp<T,?>> properties) {
    typePropertyName.ifPresent(n->
      verify(
        properties.keySet(),
        s->s.contains(n),
        s->format("The type property \"{}\" is missing in {} (available properties are: {}).", n, clazz, s)
      )
    );
  }

  private boolean isTypeProperty(final Method m) {
    return typePropertyName.map(n->n.equals(m.getName())).orElse(false);
  }

  @Override
  boolean isPolymorphic() {
    return true;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

}
