package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonBoolean;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonString;
import com.github.gv2011.util.num.BigDecimalUtils;
import com.github.gv2011.util.num.Decimal;
import com.github.gv2011.util.num.NumUtils;
import com.github.gv2011.util.time.IsoDay;

@SuppressWarnings("removal")//IsoDay
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
    LocalDateTime.class.getName(),
    LocalTime.class.getName(),
    UUID.class.getName(),
    InetSocketAddress.class.getName(),
    Hash256.class.getName(),
    Bytes.class.getName()
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
    else if(clazz.equals(Decimal.class)) result = new NumberType();
    else if(clazz.equals(Integer.class)) result = new IntegerType();
    else if(clazz.equals(Bytes.class)) result = new BytesType();
    else if(clazz.equals(int.class)) result = new IntegerType();
    else if(clazz.equals(Long.class)) result = new LongType();
    else if(clazz.equals(long.class)) result = new PrimitiveLongType();
    else if(clazz.equals(BigDecimal.class)) result = new DecimalType();
    else if(clazz.equals(Instant.class)) result = stringBasedType(Instant.class);
    else if(clazz.equals(UUID.class)) result = new UuidType();
    else if(clazz.equals(Date.class)) result = new DateType();
    else if(clazz.equals(Duration.class)) result = new DurationType();
    else if(clazz.equals(LocalDate.class)) result = stringBasedType(LocalDate.class);
    else if(clazz.equals(LocalDateTime.class)) result = stringBasedType(LocalTime.class);
    else if(clazz.equals(LocalTime.class)) result = stringBasedType(LocalTime.class);
    else if(clazz.equals(IsoDay.class)) result = stringBasedType(IsoDay.class);
    else if(clazz.equals(InetSocketAddress.class)) result = new InetSocketAddressType();
    else if(clazz.equals(Hash256.class)) result = stringBasedType(Hash256.class);
    else if(clazz.equals(URI.class)) result = stringBasedType(URI::create);
    else if(auto.isSupported(clazz)) result = auto.createType(clazz);
    else result = null;
    return Opt.ofNullable((AbstractElementaryTypeHandler<T>) result);
  }



  private static <T> AbstractElementaryTypeHandler<T> stringBasedType(final Class<T> parseable) {
    final Method method = call(()->parseable.getMethod("parse", CharSequence.class));
    final Function<? super String,T> constructor = s->call(()->parseable.cast(method.invoke(null, s)));
    return stringBasedType(constructor , Opt.empty());
  }

  private static <T> AbstractElementaryTypeHandler<T> stringBasedType(final Function<? super String,T> constructor) {
    return stringBasedType(constructor, Opt.empty());
  }

  static <T> AbstractElementaryTypeHandler<T> stringBasedType(
    final Function<? super String,T> constructor, final Opt<T> defaultValue
  ) {
    return new AbstractElementaryTypeHandler<>() {
      @Override
      public Opt<T> defaultValue() {
        return defaultValue;
      }
      @Override
      public T parse(String string) {
        return constructor.apply(string);
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
    @Override
    public String parse(String string) {
      return string;
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
    @Override
    public Nothing parse(String string) {
       return Nothing.parse(string);
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
    @Override
    public Boolean parse(String string) {
      return Boolean.valueOf(string);
    }
  }

  //TODO rename to DecimalType
  private static class NumberType extends AbstractElementaryTypeHandler<Decimal> {
    private static final Opt<Decimal> ZERO = Opt.of(NumUtils.zero());
    @Override
    public Decimal fromJson(final JsonNode json) {
      return json.asNumber();
    }
    @Override
    public JsonNumber toJson(final Decimal n, final JsonFactory jf) {
      return jf.primitive(n);
    }
    @Override
    public Opt<Decimal> defaultValue() {
      return ZERO;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.NUMBER;
    }
    @Override
    public Decimal parse(String string) {
      return NumUtils.parse(string);
    }
  }

  private static class IntegerType extends AbstractElementaryTypeHandler<Integer> {
    private static final Opt<Integer> ZERO = Opt.of(0);
    @Override
    public Integer fromJson(final JsonNode json) {
      return json.asNumber().intValue();
    }
    @Override
    public JsonNumber toJson(final Integer i, final JsonFactory jf) {
      return jf.primitive(NumUtils.from(i));
    }
    @Override
    public Opt<Integer> defaultValue() {
      return ZERO;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.NUMBER;
    }
    @Override
    public Integer parse(String string) {
      return Integer.valueOf(string);
    }
  }

  private static class LongType extends AbstractElementaryTypeHandler<Long> {
    private static final Opt<Long> ZERO = Opt.of(0l);
    @Override
    public Long fromJson(final JsonNode json) {
      return json.asNumber().longValue();
    }
    @Override
    public JsonNumber toJson(final Long i, final JsonFactory jf) {
      return jf.primitive(NumUtils.from(i));
    }
    @Override
    public Opt<Long> defaultValue() {
      return ZERO;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.NUMBER;
    }
    @Override
    public Long parse(String string) {
      return Long.valueOf(string);
    }
  }

  private static class PrimitiveLongType extends LongType {
    @Override
    public Long cast(final Class<Long> clazz, final Object object) {
        return (Long) object;
    }
  }

  //TODO rename to BigDecimalType
  private static class DecimalType extends AbstractElementaryTypeHandler<BigDecimal> {
    private static final Opt<BigDecimal> ZERO = Opt.of(BigDecimal.ZERO);
    @Override
    public BigDecimal fromJson(final JsonNode json) {
      return json.asNumber().toBigDecimal();
    }
    @Override
    public JsonNumber toJson(final BigDecimal dec, final JsonFactory jf) {
      return jf.primitive(NumUtils.from(dec));
    }
    @Override
    public Opt<BigDecimal> defaultValue() {
      return ZERO;
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.NUMBER;
    }
    @Override
    public BigDecimal parse(String string) {
      return BigDecimalUtils.canonical(new BigDecimal(string));
    }
  }

  private static class BytesType extends AbstractElementaryTypeHandler<Bytes> {
    @Override
    public Bytes fromJson(final JsonNode json) {
      return ByteUtils.parseBase64(json.asString());
    }
    @Override
    public JsonString toJson(final Bytes i, final JsonFactory jf) {
      return jf.primitive(i.toBase64String());
    }
    @Override
    public Opt<Bytes> defaultValue() {
      return Opt.of(ByteUtils.emptyBytes());
    }
    @Override
    public JsonNodeType jsonNodeType() {
      return JsonNodeType.STRING;
    }
    @Override
    public Bytes parse(String string) {
      return ByteUtils.parseBase64(string);
    }
  }

  private static class DateType extends AbstractElementaryTypeHandler<Date> {
    @Override
    public Date fromJson(final JsonNode json) {
      if(json instanceof JsonNumber) {
          return new Date(json.asNumber().longValue());
      }
      else return parse(json.asString());
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
    @Override
    public Date parse(String string) {
      return Date.from(Instant.parse(string));
    }
  }

  private static class DurationType extends AbstractElementaryTypeHandler<Duration> {
    private static final Opt<Duration> ZERO = Opt.of(Duration.ZERO);
    @Override
    public Duration fromJson(final JsonNode json) {
      if(json instanceof JsonNumber) {
          return Duration.ofMillis(json.asNumber().longValue());
      }
      else return parse(json.asString());
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
    @Override
    public Duration parse(String string) {
      return Duration.parse(string);
    }
  }

  private static class UuidType extends AbstractElementaryTypeHandler<UUID> {
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
    @Override
    public UUID parse(String string) {
      return UUID.fromString(string);
    }
  }

  private static class InetSocketAddressType extends AbstractElementaryTypeHandler<InetSocketAddress>{
    @Override
    public JsonString toJson(final InetSocketAddress socket, final JsonFactory jf) {
      return jf.primitive(socket.getHostString()+":"+socket.getPort());
    }
    @Override
    public InetSocketAddress parse(String colonNotation) {
      final int i = colonNotation.lastIndexOf(':');
      verify(i!=-1);
      final String host = colonNotation.substring(0, i);
      final int port = Integer.parseInt(colonNotation.substring(i+1));
      return InetSocketAddress.createUnresolved(host, port);
    }
  }

}
