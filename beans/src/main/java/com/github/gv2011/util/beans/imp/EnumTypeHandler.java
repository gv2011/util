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
import static com.github.gv2011.util.CollectionUtils.stream;
import static com.github.gv2011.util.ex.Exceptions.call;


import com.github.gv2011.util.beans.Default;
import com.github.gv2011.util.beans.Other;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;

final class EnumTypeHandler<E extends Enum<E>> extends AbstractElementaryTypeHandler<E> {

  private final Class<E> enumClass;
  private final Opt<E> defaultValue;
  private final Opt<E> otherValue;

  EnumTypeHandler(final Class<E> enumClass) {
    this.enumClass = enumClass;
    defaultValue = stream(enumClass.getEnumConstants()).toOpt(
      e->call(()->enumClass.getField(e.name())).getAnnotation(Default.class)!=null
    );
    otherValue = stream(enumClass.getEnumConstants()).toOpt(
      e->call(()->enumClass.getField(e.name())).getAnnotation(Other.class)!=null
    );
  }

  @Override
  public Opt<E> defaultValue() {
    return defaultValue;
  }

  @Override
  public JsonNode toJson(final E enm, final JsonFactory jf) {
    return jf.primitive(enm.name());
  }

  @Override
  public E parse(String string) {
    try {
      return Enum.valueOf(enumClass, string);
    } catch (final IllegalArgumentException ex) {
      if(otherValue.isPresent()) return otherValue.get();
      else throw ex;
    }
  }

}

