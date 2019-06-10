package com.github.gv2011.util.cache;

import static com.github.gv2011.util.Nothing.nothing;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.lang.ref.SoftReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.gv2011.util.FConsumer;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.Opt;

public final class CacheUtils {

  private CacheUtils(){staticClass();}

  public static <T> Supplier<T> cache(final Supplier<T> supplier){
    return new SoftRefCache<>(supplier)::get;
  }

  public static <K,V> SoftIndex<K,V> softIndex(final Function<K,Opt<? extends V>> constantFunction){
    return new SoftIndexImp<>(constantFunction, p->nothing());
  }

  public static <K,V> SoftIndex<K,V> softIndex(
    final Function<K,Opt<? extends V>> constantFunction, final FConsumer<Pair<K,Opt<V>>> addedListener
  ){
    return new SoftIndexImp<>(constantFunction, addedListener);
  }

  public static <C> C readFromReference(
    final @Nullable SoftReference<C> ref,
    final Consumer<SoftReference<C>> refSetter,
    final Supplier<C> constructor
  ){
    @Nullable C cache = ref!=null ? ref.get() : null;
    if(cache == null){
      cache = constructor.get();
      refSetter.accept(new SoftReference<>(cache));
    }
    return cache;
  }

  public static <T> T lazy(
      @Nullable T value,
      final Consumer<T> store,
      final Supplier<T> calculator) {
    if(value==null){
      value = calculator.get();
      store.accept(value);
    }
    return value;
  }
}
