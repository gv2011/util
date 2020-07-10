package com.github.gv2011.util.main;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.mapBuilder;
import static com.github.gv2011.util.icol.ICollections.toIMap;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.github.gv2011.util.Alternative;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.IMap.Builder;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonUtils;

final class ArgsParserImp implements ArgsParser {

  private static final Logger LOG = getLogger(ArgsParserImp.class);

  private final TypeRegistry reg = BeanUtils.typeRegistry();
  private final JsonFactory jsonFactory = JsonUtils.jsonFactory();

  @Override
  public <T> Alternative<T, String> parse(Class<T> argsClass, IList<String> args) {
    final BeanType<T> type = reg.beanType(argsClass);
    Opt<T> argsObj = Opt.empty();
    String error = "";
    try {
      final IMap<String, Opt<String>> argsMap = asMap(args, getShortKeys(flattenProperties(type).keySet()));
      argsObj = Opt.of(beanFromArgs(type, argsMap));
    } catch (RuntimeException e) {
      LOG.error("Illegal args.", e);
      error = String.valueOf(e.getMessage());
    }
    if (argsObj.isPresent()) return Alternative.<T, String>altA(argsObj.get());
    else return Alternative.<T, String>altB(error);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  final <T> T beanFromArgs(BeanType<T> beanType, IMap<String, Opt<String>> argsMap) {
    final ExtendedBeanBuilder<T> b = beanType.createBuilder();
    for (Property p : beanType.properties().values()) {
      final Type propType = p.type();
      if (propType instanceof BeanType) {
        b.set(p, beanFromArgs((BeanType) propType, argsMap));
      }
      else {
        argsMap.tryGet(p.name()).orElse(Opt.empty())
          .ifPresent(v -> {
            final Object value;
            try {
              value = v.trim().startsWith("\"") 
                ? propType.parse(jsonFactory.deserialize(v))
                : propType.parse(v)
              ;
            }
            catch (Exception e) {
              throw new IllegalArgumentException(format("Cannot parse {} as {}.", v, propType), e);
            }
            b.set(p, value);
          })
        ;
      }
    }
    return b.build();
  }

  final IMap<Character, String> getShortKeys(ISet<String> keySet) {
    final ISet<Character> exclude = keySet.stream().filter(k -> k.length() == 1).map(k -> k.charAt(0)).collect(toISet());
    return keySet.stream()
      .collect(Collectors.groupingBy(n -> n.charAt(0)))
      .entrySet().stream()
      .filter(e -> e.getValue().size() == 1)
      .filter(e -> !exclude.contains(e.getKey()))
      .collect(toIMap(
        e -> (Character) e.getKey(), 
        e -> (String) e.getValue().get(0)
      ))
    ;
  }

  final IMap<String, Opt<String>> asMap(IList<String> args, final IMap<Character, String> shortKeys) {
    int i = 0;
    boolean endOfOpts = false;
    final Builder<String, Opt<String>> mapBuilder = mapBuilder();
    while (!endOfOpts) {
      final String e = args.get(i);
      if (!isOptionKey(e))
        endOfOpts = true;
      else {
        final String key;
        final Opt<String> value;
        if (e.startsWith("--")) {
          verify(e.length() > 2);
          key = e.substring(2);
        } else {
          verify(e.length() == 2);
          verify(e.charAt(0) == '-');
          key = shortKeys.get(e.charAt(1));
        }
        i++;
        if (i >= args.size()) {
          value = Opt.empty();
          endOfOpts = true;
        } else {
          if (isOptionKey(args.get(i))) value = Opt.empty();
          else {
            value = Opt.of(args.get(i));
            i++;
          }
          if (i >= args.size()) endOfOpts = true;
        }
        mapBuilder.put(key, value);
      }
    }
    return mapBuilder.build();
  }

  private boolean isOptionKey(String e) {
    return e.startsWith("-") || e.startsWith("--");
  }

  final IMap<String, Property<?>> flattenProperties(BeanType<?> type) {
    return flattenProperties(type, new HashSet<>());
  }

  private IMap<String, Property<?>> flattenProperties(BeanType<?> type, Set<BeanType<?>> exclude) {
    exclude.add(type);
    return type.properties().values().stream()
      .flatMap(p -> {
        if (p.type() instanceof BeanType) {
          if (exclude.contains(p.type())) return XStream.empty();
          else return flattenProperties((BeanType<?>) p.type(), exclude).values().stream();
        }
        else return XStream.of(p);
      })
      .collect(toIMap(Property::name, p -> p))
    ;
  }

}
