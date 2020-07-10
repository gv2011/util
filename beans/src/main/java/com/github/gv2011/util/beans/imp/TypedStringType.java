package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.Constructor;
import java.util.function.Function;

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

import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.tstr.TypedString;

class TypedStringType<S extends TypedString<S>> extends AbstractElementaryType<S>{

  private final TypeHandler handler;
  private final Opt<S> defaultValue;
  private final Function<String,S> constructor;

  TypedStringType(final JsonFactory jf, final Class<S> clazz) {
    super(jf, clazz);
    if(clazz.isInterface()){
      constructor = v->TypedString.create(clazz, v);
    }
    else{
      final Constructor<S> constr = call(()->clazz.getConstructor(String.class));
      constructor = v->call(()->constr.newInstance(v));
    }
    handler = new TypeHandler();
    this.defaultValue = Opt.of(handler.parse(""));
  }

  @Override
  ElementaryTypeHandler<S> handler(){return handler;}

  @Override
  public final boolean hasStringForm() {
    return true;
  }
  

  private final class TypeHandler extends AbstractElementaryTypeHandler<S>{
    @Override
    public Opt<S> defaultValue() {
      return defaultValue;
    }

    @Override
    public S parse(String string) {
      return constructor.apply(string);
    }
  }


}
