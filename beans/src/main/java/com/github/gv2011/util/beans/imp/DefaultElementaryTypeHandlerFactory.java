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
import static com.github.gv2011.util.CollectionUtils.sortedSetOf;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import com.github.gv2011.util.IsoDay;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;

public final class DefaultElementaryTypeHandlerFactory implements ElementaryTypeHandlerFactory{

    private static final ISortedSet<String> SUPPORTED_CLASS_NAMES = sortedSetOf(
        Nothing.class.getName(),
        Boolean.class.getName(),
        Integer.class.getName(),
        Long.class.getName(),
        BigDecimal.class.getName(),
        String.class.getName(),
        Instant.class.getName(),
        Duration.class.getName(),
        IsoDay.class.getName(),
        InetSocketAddress.class.getName()
    );

    @Override
    public boolean isSupported(final Class<?> clazz) {
        return SUPPORTED_CLASS_NAMES.contains(clazz.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ElementaryTypeHandler<T> getTypeHandler(final Class<T> clazz) {
        AbstractElementaryTypeHandler<?> result;
        if(clazz.equals(String.class)) result = new StringType();
        else if(clazz.equals(Nothing.class)) result = new NothingType();
        else if(clazz.equals(Boolean.class)) result = new BooleanType();
        else if(clazz.equals(Integer.class)) result = new IntegerType();
        else if(clazz.equals(Long.class)) result = new LongType();
        else if(clazz.equals(BigDecimal.class)) result = new DecimalType();
        else if(clazz.equals(Instant.class)) result = stringBasedType(Instant.class);
        else if(clazz.equals(Duration.class)) result = stringBasedType(Duration.class);
        else if(clazz.equals(IsoDay.class)) result = stringBasedType(IsoDay.class);
        else if(clazz.equals(InetSocketAddress.class)) result = new InetSocketAddressType();
//        else if(TypedString.class.isAssignableFrom(clazz)) result = new TypedStringHandler(clazz);
        else throw new UnsupportedOperationException();
        return (ElementaryTypeHandler<T>) result;
    }

    private static <T> AbstractElementaryTypeHandler<T> stringBasedType(final Class<T> parseable) {
      final Method method = call(()->parseable.getMethod("parse", CharSequence.class));
      final Function<? super String,T> constructor = s->call(()->parseable.cast(method.invoke(null, s)));
      return stringBasedType(constructor , Optional.empty());
    }

    @SuppressWarnings("unused")
    private static <T> AbstractElementaryTypeHandler<T> stringBasedType(final Function<? super String,T> constructor) {
      return stringBasedType(constructor, Optional.empty());
    }

    private static <T> AbstractElementaryTypeHandler<T> stringBasedType(
        final Function<? super String,T> constructor, final Optional<T> defaultValue
      ) {
      return new AbstractElementaryTypeHandler<T>() {
        @Override
        public T fromJson(final JsonNode json) {
          return constructor.apply(json.asString());
        }
        @Override
        public Optional<T> defaultValue() {
          return defaultValue;
        }
      };
    }



    private static class StringType extends AbstractElementaryTypeHandler<String> {
        private static final Optional<String> EMPTY = Optional.of("".intern());
        @Override
        public String fromJson(final JsonNode json) {
          return json.asString();
        }
        @Override
        public Optional<String> defaultValue() {
          return EMPTY;
        }
    }

    private static class NothingType extends AbstractElementaryTypeHandler<Nothing> {
      private static final Optional<Nothing> DEF = Optional.of(Nothing.INSTANCE);
      @Override
      public Nothing fromJson(final JsonNode json) {
        json.asNull();
        return Nothing.INSTANCE;
      }
      @Override
      public Optional<Nothing> defaultValue() {
        return DEF;
      }
      @Override
      public JsonNodeType jsonNodeType() {
        return JsonNodeType.NULL;
      }
    }

    private static class BooleanType extends AbstractElementaryTypeHandler<Boolean> {
      private static final Optional<Boolean> FALSE = Optional.of(false);
      @Override
      public Boolean fromJson(final JsonNode json) {
        return json.asBoolean();
      }
      @Override
      public Optional<Boolean> defaultValue() {
        return FALSE;
      }
      @Override
      public JsonNodeType jsonNodeType() {
        return JsonNodeType.BOOLEAN;
      }
    }

    private static class IntegerType extends AbstractElementaryTypeHandler<Integer> {
      private static final Optional<Integer> ZERO = Optional.of(0);
      @Override
      public Integer fromJson(final JsonNode json) {
        return json.asNumber().intValueExact();
      }
      @Override
      public Optional<Integer> defaultValue() {
        return ZERO;
      }
      @Override
      public JsonNodeType jsonNodeType() {
        return JsonNodeType.NUMBER;
      }
    }

    private static class LongType extends AbstractElementaryTypeHandler<Long> {
      private static final Optional<Long> ZERO = Optional.of(0l);
      @Override
      public Long fromJson(final JsonNode json) {
        return json.asNumber().longValueExact();
      }
      @Override
      public Optional<Long> defaultValue() {
        return ZERO;
      }
      @Override
      public JsonNodeType jsonNodeType() {
        return JsonNodeType.NUMBER;
      }
    }

    private static class DecimalType extends AbstractElementaryTypeHandler<BigDecimal> {
      private static final Optional<BigDecimal> ZERO = Optional.of(BigDecimal.ZERO);
      @Override
      public BigDecimal fromJson(final JsonNode json) {
        return json.asNumber();
      }
      @Override
      public Optional<BigDecimal> defaultValue() {
        return ZERO;
      }
      @Override
      public JsonNodeType jsonNodeType() {
        return JsonNodeType.NUMBER;
      }
    }

    private static class InetSocketAddressType extends AbstractElementaryTypeHandler<InetSocketAddress>{
      @Override
      public InetSocketAddress fromJson(final JsonNode json) {
        final String colonNotation = json.asString();
        final int i = colonNotation.lastIndexOf(':');
        verify(i!=-1);
        final String host = colonNotation.substring(0, i);
        final int port = Integer.parseInt(colonNotation.substring(i+1));
        return InetSocketAddress.createUnresolved(host, port);
      }
    }

}
