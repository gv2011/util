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

import static com.github.gv2011.util.CollectionUtils.atMostOne;
import static com.github.gv2011.util.Verify.verify;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.util.Optional;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.beans.TypeName;


final class PolymorphicBeanType<T> extends DefaultBeanType<T> {

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(PolymorphicBeanType.class);

  private final String typePropertyName;

  PolymorphicBeanType(final Class<T> beanClass, final DefaultTypeRegistry registry, final String typePropertyName) {
    super(beanClass, registry);
    this.typePropertyName = typePropertyName;
  }

  @Override
  <V> PropertyImp<V> createProperty(final Method m, final AbstractType<V> type) {
    if(!m.getName().equals(typePropertyName)) return super.createProperty(m, type);
    else{
      verify(m.getAnnotation(DefaultValue.class)==null);
      final V fixedValue = type.parse(parseTolerant(
        type, registry.jf,
        (
          atMostOne(
            Optional.ofNullable(clazz.getAnnotation(TypeName.class)).map(TypeName::value),
            Optional.ofNullable(m.getAnnotation(FixedValue.class)).map(FixedValue::value)
          )
          .orElseGet(()->clazz.getSimpleName())
        )
      ));
      return new PropertyImp<>(typePropertyName, type, Optional.empty(), Optional.of(fixedValue));
    }
  }

  @Override
  boolean isPolymorphic() {
    return true;
  }


}
