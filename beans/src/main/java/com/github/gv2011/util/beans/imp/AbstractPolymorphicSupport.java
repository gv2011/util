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
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;

abstract class AbstractPolymorphicSupport<B> extends ObjectTypeSupport<B>{


  final DefaultTypeRegistry registry;

  AbstractPolymorphicSupport(final DefaultTypeRegistry registry, final Class<B> clazz) {
    super(clazz);
    this.registry = registry;
  }

  @Override
  final JsonFactory jf() {
    return registry.jf();
  }

  abstract PolymorphicRootType<? super B> rootType();

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final JsonNode toJson(final B object) {
    return ((Type)registry.getTypeOfObject(object)).toJson(object);
  }

  @Override
  public final B parse(final JsonNode json) {
    final Class<?> clazz = typeResolver().resolve(json);
    assert this.clazz.isAssignableFrom(clazz) && !this.clazz.equals(clazz) && clazz.isInterface(); //real subclass
    return this.clazz.cast(registry.type(clazz).parse(json));
  }


  @Override
  final boolean isPolymorphic() {
    return true;
  }

  abstract TypeResolver<? super B> typeResolver();

  @Override
  public final boolean isAbstract() {
    return true;
  }

  @Override
  protected final Opt<BeanTypeSupport<B>> asBeanType(){
    return Opt.empty();
  }

}
