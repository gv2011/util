package com.github.gv2011.util;

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
import static com.github.gv2011.util.CollectionUtils.stream;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.github.gv2011.util.icol.ISortedSet;

public final class EnumUtils {

    private EnumUtils(){staticClass();}

    private static final Map<Class<? extends Enum<?>>, ISortedSet<? extends Enum<?>>> CACHE =
      Collections.synchronizedMap(new WeakHashMap<>())
    ;

    private static final Map<Class<? extends Enum<?>>, ISortedSet<String>> CACHE_STR =
      Collections.synchronizedMap(new WeakHashMap<>())
    ;

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> ISortedSet<E> values(final Class<E> enumClass){
      return (ISortedSet<E>) CACHE.computeIfAbsent(
        enumClass,
        c->stream(enumClass.getEnumConstants()).collect(toISortedSet())
      );
    }

    public static <E extends Enum<E>> ISortedSet<String> stringValues(final Class<E> enumClass){
      return CACHE_STR.computeIfAbsent(
        enumClass,
        c->values(enumClass).stream().map(Object::toString).collect(toISortedSet())
      );
    }

}
