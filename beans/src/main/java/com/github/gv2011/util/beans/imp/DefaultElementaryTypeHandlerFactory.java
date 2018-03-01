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
import static com.github.gv2011.util.ex.Exceptions.format;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import com.github.gv2011.util.IsoDay;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.json.JsonBoolean;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonString;

final class DefaultElementaryTypeHandlerFactory implements ElementaryTypeHandlerFactory{

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
        else if(clazz.equals(int.class)) result = new IntegerType();
        else if(clazz.equals(Long.class)) result = new LongType();
        else if(clazz.equals(long.class)) result = new PrimitiveLongType();
        else if(clazz.equals(BigDecimal.class)) result = new DecimalType();
        else if(clazz.equals(Instant.class)) result = stringBasedType(Instant.class);
        else if(clazz.equals(Date.class)) result = new DateType();
        else if(clazz.equals(Duration.class)) result = new DurationType();
        else if(clazz.equals(IsoDay.class)) result = stringBasedType(IsoDay.class);
        else if(clazz.equals(InetSocketAddress.class)) result = new InetSocketAddressType();
        else throw new UnsupportedOperationException(format("No handler for {}.", clazz));
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
      public JsonNull toJson(final Nothing object, final JsonFactory jf) {
        return jf.jsonNull();
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
      public JsonBoolean toJson(final Boolean b, final JsonFactory jf) {
        return jf.primitive(b);
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
      public JsonNumber toJson(final Integer i, final JsonFactory jf) {
        return jf.primitive(i);
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
      public JsonNumber toJson(final Long i, final JsonFactory jf) {
        return jf.primitive(i);
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

    private static class PrimitiveLongType extends LongType {
        @Override
        public Long cast(final Class<Long> clazz, final Object object) {
            return (Long) object;
        }
      }

    private static class DecimalType extends AbstractElementaryTypeHandler<BigDecimal> {
      private static final Optional<BigDecimal> ZERO = Optional.of(BigDecimal.ZERO);
      @Override
      public BigDecimal fromJson(final JsonNode json) {
        return json.asNumber();
      }
      @Override
      public JsonNumber toJson(final BigDecimal dec, final JsonFactory jf) {
        return jf.primitive(dec);
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

    private static class DateType extends AbstractElementaryTypeHandler<Date> {
        @Override
        public Date fromJson(final JsonNode json) {
          if(json instanceof JsonNumber) {
              return new Date(json.asNumber().longValueExact());
          }
          else return Date.from(Instant.parse(json.asString()));
        }
        @Override
        public JsonString toJson(final Date date, final JsonFactory jf) {
          return jf.primitive(date.toInstant().toString());
        }
        @Override
        public Optional<Date> defaultValue() {
          return Optional.empty();
        }
        @Override
        public JsonNodeType jsonNodeType() {
          return JsonNodeType.STRING;
        }
      }

    private static class DurationType extends AbstractElementaryTypeHandler<Duration> {
        private static final Optional<Duration> ZERO = Optional.of(Duration.ZERO);
        @Override
        public Duration fromJson(final JsonNode json) {
          if(json instanceof JsonNumber) {
              return Duration.ofMillis(json.asNumber().longValueExact());
          }
          else return Duration.parse(json.asString());
        }
        @Override
        public JsonString toJson(final Duration duration, final JsonFactory jf) {
          return jf.primitive(duration.toString());
        }
        @Override
        public Optional<Duration> defaultValue() {
          return ZERO;
        }
        @Override
        public JsonNodeType jsonNodeType() {
          return JsonNodeType.STRING;
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
      @Override
      public JsonString toJson(final InetSocketAddress socket, final JsonFactory jf) {
        return jf.primitive(socket.getHostString()+":"+socket.getPort());
      }
    }

}
