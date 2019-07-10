package com.github.gv2011.util.beans;

import java.util.function.Function;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
public interface BeanBuilder<T> {


    T build();

    <V> void set(Property<V> p, V value);

    <V> Setter<T,V> set(Function<T,V> method);

    <V> Setter<T,V> setOpt(Function<T,Opt<V>> method);

    <V extends TypedString<V>> Setter<T,String> setTStr(final Function<T,V> method);

    BeanBuilder<T> setAll(T bean);

    BeanBuilder<T> setProperties(T bean, ICollection<Function<T,?>> methods);

    public interface Setter<T,V> {
      BeanBuilder<T> to(V value);
    }

}
