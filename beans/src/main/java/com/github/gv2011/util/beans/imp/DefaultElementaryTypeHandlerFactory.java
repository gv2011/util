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
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;

import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonBoolean;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonString;
import com.github.gv2011.util.time.IsoDay;

@SuppressWarnings("removal")
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
    LocalDate.class.getName(),
    UUID.class.getName(),
    InetSocketAddress.class.getName(),
    Hash256.class.getName()
  );

  private final AutoElementarySupport auto = new AutoElementarySupport();

  @Override
  public boolean isSupported(final Class<?> clazz) {
    return
      clazz.isEnum() ||
      SUPPORTED_CLASS_NAMES.contains(clazz.getName()) ||
      auto.isSupported(clazz)
    ;
  }

@Override
  public <T> ElementaryTypeHandler<T> getTypeHandler(final Class<T> clazz) {
    return tryGetTypeHandler(clazz)
      .orElseThrow(()->
        new NoSuchElementException(format("No handler for {}.", clazz))
      )
    ;
  }

  @SuppressWarnings("unchecked")
  <T> Opt<ElementaryTypeHandler<T>> tryGetTypeHandler(final Class<T> clazz) {
    @Nullable AbstractElementaryTypeHandler<?> result;
    if(clazz.isEnum()) result = new EnumTypeHandler<>(clazz.asSubclass(Enum.class));
    else if(clazz.equals(String.class)) result = new StringType();
    else if(clazz.equals(Nothing.class)) result = new NothingType();
    else if(clazz.equals(Boolean.class)) result = new BooleanType();
    else if(clazz.equals(Integer.class)) result = new IntegerType();
    else if(clazz.equals(int.class)) result = new IntegerType();
    else if(clazz.equals(Long.class)) result = new LongType();
    else if(clazz.equals(long.class)) result = new PrimitiveLongType();
    else if(clazz.equals(BigDecimal.class)) result = new DecimalType();
    else if(clazz.equals(Instant.class)) result = stringBasedType(Instant.class);
    else if(clazz.equals(UUID.class)) result = new UuidType();
    else if(clazz.equals(Date.class)) result = new DateType();
    else if(clazz.equals(Duration.class)) result = new DurationType();
    else if(clazz.equals(LocalDate.class)) result = stringBasedType(LocalDate.class);
    else if(clazz.equals(IsoDay.class)) result = stringBasedType(IsoDay.class);
    else if(clazz.equals(InetSocketAddress.class)) result = new InetSocketAddressType();
    else if(clazz.equals(Hash256.class)) result = stringBasedType(Hash256.class);
    else if(auto.isSupported(clazz)) result = auto.createType(clazz);
    else result = null;
    return Opt.ofNullable((AbstractElementaryTypeHandler<T>) result);
  }



  private static <T> AbstractElementaryTypeHandler<T> stringBasedType(final Class<T> parseable) {
    final Method method = call(()->parseable.getMethod("parse", CharSequence.class));
    final Function<? super String,T> constructor = s->call(()->parseable.cast(method.invoke(null, s)));
    return stringBasedType(constructor , Opt.empty());
  }

  @SuppressWarnings("unused")
  private static <T> AbstractElementaryTypeHandler<T> stringBasedType(final Function<? super String,T> constructor) {
    return stringBasedType(constructor, Opt.empty());
  }

  static <T> AbstractElementaryTypeHandler<T> stringBasedType(
    final Function<? super String,T> constructor, final Opt<T> defaultValue
  ) {
    return new AbstractElementaryTypeHandler<T>() {
      @Override
      public T fromJson(final JsonNode json) {
        return constructor.apply(json.asString());
      }
      @Override
      public Opt<T> defaultValue() {
        return defaultValue;
      }
    };
  }

  private static final class StringType extends AbstractElementaryTypeHandler<String> {
    private static final Opt<String> EMPTY = Opt.of("".intern());
    @Override
    public String fromJson(final JsonNode json) {
      if(json.jsonNodeType().equals(JsonNodeType.LIST)){
        return json.asList().stream().map(JsonNode::asString).collect(joining());
      }
      else return json.asString();
    }
    @Override
    public Opt<String> defaultValue() {
      return EMPTY;
    }
    @Override
    public boolean hasStringForm() {
      return true;
    }
  }

  private static class NothingType extends AbstractElementaryTypeHandler<Nothing> {
    private static final Opt<Nothing> DEF = Opt.of(Nothing.INSTANCE);
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
    public Opt<Nothing> defaultValue() {
      return DEF;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.NULL;
    }
  }

  private static class BooleanType extends AbstractElementaryTypeHandler<Boolean> {
    private static final Opt<Boolean> FALSE = Opt.of(false);
    @Override
    public Boolean fromJson(final JsonNode json) {
      return json.asBoolean();
    }
    @Override
    public JsonBoolean toJson(final Boolean b, final JsonFactory jf) {
      return jf.primitive(b);
    }
    @Override
    public Opt<Boolean> defaultValue() {
      return FALSE;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.BOOLEAN;
    }
  }

  private static class IntegerType extends AbstractElementaryTypeHandler<Integer> {
    private static final Opt<Integer> ZERO = Opt.of(0);
    @Override
    public Integer fromJson(final JsonNode json) {
      return json.asNumber().intValueExact();
    }
    @Override
    public JsonNumber toJson(final Integer i, final JsonFactory jf) {
      return jf.primitive(i);
    }
    @Override
    public Opt<Integer> defaultValue() {
      return ZERO;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.NUMBER;
    }
  }

  private static class LongType extends AbstractElementaryTypeHandler<Long> {
    private static final Opt<Long> ZERO = Opt.of(0l);
    @Override
    public Long fromJson(final JsonNode json) {
      return json.asNumber().longValueExact();
    }
    @Override
    public JsonNumber toJson(final Long i, final JsonFactory jf) {
      return jf.primitive(i);
    }
    @Override
    public Opt<Long> defaultValue() {
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
    private static final Opt<BigDecimal> ZERO = Opt.of(BigDecimal.ZERO);
    @Override
    public BigDecimal fromJson(final JsonNode json) {
      return json.asNumber();
    }
    @Override
    public JsonNumber toJson(final BigDecimal dec, final JsonFactory jf) {
      return jf.primitive(dec);
    }
    @Override
    public Opt<BigDecimal> defaultValue() {
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
    public Opt<Date> defaultValue() {
      return Opt.empty();
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.STRING;
    }
  }

  private static class DurationType extends AbstractElementaryTypeHandler<Duration> {
    private static final Opt<Duration> ZERO = Opt.of(Duration.ZERO);
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
    public Opt<Duration> defaultValue() {
      return ZERO;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.STRING;
    }
  }

  private static class UuidType extends AbstractElementaryTypeHandler<UUID> {
    @Override
    public UUID fromJson(final JsonNode json) {
      return UUID.fromString(json.asString());
    }
    @Override
    public JsonString toJson(final UUID uuid, final JsonFactory jf) {
      return jf.primitive(uuid.toString());
    }
    @Override
    public Opt<UUID> defaultValue() {
      return Opt.empty();
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
