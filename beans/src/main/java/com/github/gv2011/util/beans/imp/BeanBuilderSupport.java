package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.sortedMapFrom;
import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;

public abstract class BeanBuilderSupport<T> implements ExtendedBeanBuilder<T> {

    private final Map<String,Object> map = new HashMap<>();
    private final UnaryOperator<T> resultWrapper;
    private final UnaryOperator<T> validator;

    protected abstract BeanTypeSupport<T> beanType();

    protected BeanBuilderSupport(final UnaryOperator<T> resultWrapper, final UnaryOperator<T> validator){
      this.resultWrapper = resultWrapper;
      this.validator = validator;
    }

    @Override
    public T buildUnvalidated() {
      //verify fixed values:
      for(final PropertyImp<T,?> p: beanType().properties().values()) {
          p.fixedValue().ifPresentDo(fv->{
              final String propName = p.name();
              if(map.containsKey(propName)) {
                verifyEqual(map.get(propName),fv, (e,a)->
                  format("{}: The fixed value of property {} is {}, but the value {} was set.", beanType(), p, e, a)
                );
              }
          });
      }
      //verify computed properties:
      for(final PropertyImp<T,?> p: beanType().properties().values()) {
        if(p.function().isPresent()) {
          final String propName = p.name();
          verify(
            !map.containsKey(propName),
            ()->format("{}: The comuted property {} must not be set.", beanType(), p)
          );
        }
      }
      //remove default values:
      for(final Property<?> p: beanType().properties().values()) {
          p.defaultValue().ifPresentDo(dv->{
              final String propName = p.name();
              final @Nullable Object actual = map.get(propName);
              if(actual!=null) {
                  if(dv.equals(actual)) map.remove(propName);
              }
          });
      }
      //verify all other properties are set:
      final ISet<Property<?>> missing = beanType().properties().values().stream()
          .filter(p->!p.defaultValue().isPresent())
          .filter(p->!p.function().isPresent())
          .filter(p->!map.keySet().contains(p.name()))
          .collect(toISet())
      ;
      verify(missing, Set::isEmpty, m->format("{}: The required properties {} have not been set.", beanType(), m));
      //create proxy:
      final ISortedMap<String, Object> imap = sortedMapFrom(map);
      return resultWrapper.apply(create(imap));
    }

    protected abstract T create(final ISortedMap<String, Object> imap);

    @Override
    public final T build() {
      return validator.apply(buildUnvalidated());
    }

    @Override
    public Partial<T> buildPartial() {
        return new PartialImp<>(sortedMapFrom(map));
    }

    @Override
    public BeanBuilder<T> setAll(final T bean) {
        for(final PropertyImp<T,?> p: beanType().properties().values()) {
            copy(p, bean);
        }
        return this;
    }

    @Override
    public BeanBuilder<T> setProperties(final T bean, final ICollection<Function<T, ?>> methods) {
      final BeanTypeSupport<T> beanType = beanType();
      methods.stream()
        .map(m->beanType.getProperty(m))
        .forEach(p->copy(p, bean))
      ;
      return this;
    }

    private <V >void copy(final PropertyImp<T,V> p, final T bean) {
        map.put(p.name(), beanType().get(bean, p));
    }

    @Override
    public <V> void set(final Property<V> p, final V value) {
        map.put(p.name(), value);
    }

    @Override
    public <V> Setter<T,V> set(final Function<T, V> method) {
      final PropertyImp<T,V> property = beanType().getProperty(method);
      return new SetterImp<>(property);
    }

    @Override
    public final <V extends TypedString<V>> Setter<T, String> setTStr(final Function<T, V> method) {
      final PropertyImp<T,V> property = beanType().getProperty(method);
      return vStr->{
        final V value = TypedString.create(property.type().clazz, vStr);
        return new SetterImp<>(property).to(value);
      };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <V> Setter<T,V> setOpt(final Function<T, Opt<V>> method) {
      final PropertyImp<T,Opt<V>> property = beanType().getProperty(method);
      verify(property.type() instanceof CollectionType);
      final CollectionType optType = (CollectionType) property.type();
      verifyEqual(optType.structure, Structure.opt());
      return new OptSetter<>(property);
    }

    private class SetterImp<V> implements Setter<T,V> {
      private final PropertyImp<T,V> property;
      private SetterImp(final PropertyImp<T,V> property) {
        this.property = property;
      }
      @Override
      public BeanBuilder<T> to(final V value) {
        notNull(value, ()->format("Trying to set {} to null.", property));
        verify(!property.function().isPresent());
        property.fixedValue().ifPresentDo(fv->{
          verifyEqual(value, fv, (e,a)->
            format(
              "{}: Trying to set property {} to value {}, but it is fixed to value {}.",
              beanType(), property, e, a
            )
          );
        });
        map.put(
          property.name(),
          property.type().cast(value)
        );
        return BeanBuilderSupport.this;
      }
    }

    private class OptSetter<V> implements Setter<T,V> {
      private final PropertyImp<T,Opt<V>> property;
      private OptSetter(final PropertyImp<T,Opt<V>> property) {
        this.property = property;
      }
      @SuppressWarnings("rawtypes")
      @Override
      public BeanBuilder<T> to(final V value) {
        map.put(
          property.name(),
          Opt.of(((CollectionType) property.type()).elementType().cast(notNull(value)))
        );
        return BeanBuilderSupport.this;
      }
    }
}
