package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.single;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toIMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import static java.util.function.Predicate.*;
import java.util.stream.IntStream;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.Constructor.Variant;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;

final class ConstructorHandler<T> {

  private final Class<T> clazz;
  private final AnnotationHandler annotationHandler;
  private final ISortedMap<String, PropertyImp<T, ?>> properties;

  ConstructorHandler(
    final Class<T> clazz,
    final AnnotationHandler annotationHandler,
    final ISortedMap<String, PropertyImp<T, ?>> properties
  ) {
    this.clazz = clazz;
    this.annotationHandler = annotationHandler;
    this.properties = properties;
  }

  Opt<Function<ISortedMap<String, Object>, T>> tryCreateConstructor(final Opt<Class<?>> implementingClass) {
    return implementingClass.flatMap(this::tryGetMapConstructor);
  }

  private Opt<Function<ISortedMap<String, Object>, T>> tryGetMapConstructor(final Class<?> implementingClass) {
    verify(clazz.isAssignableFrom(implementingClass));
    return
      XStream.ofArray(call(()->implementingClass.getConstructors()))
      .toOpt(annotationHandler::propertiesConstructor)
      .map(this::buildConstructorFunction)
    ;
  }

  private void signatureMatchesByNames(final Constructor<?> constructor){
    verify(constructor, this::hasParameterNames, c->format("{}: missing parameter name information.", c));
    final IMap<String, Parameter> byName =
      XStream.ofArray(constructor.getParameters()).collect(toIMap(Parameter::getName, p->p))
    ;
    verify(
      properties.values().stream()
      .filter(not(PropertyImp::computed))
      .filter(p->p.defaultValue().isEmpty())
      .filter(p->p.fixedValue().isEmpty())
      .allMatch(prop->byName.containsKey(prop.name()))
      &&
      byName.values().stream().allMatch(param->
        properties.tryGet(param.getName())
        .map(prop->param.getType().equals(prop.type().clazz))
        .orElse(false)
      )
    );
  }

  private boolean hasParameterNames(final Constructor<?> c) {
    return XStream.ofArray(c.getParameters()).allMatch(Parameter::isNamePresent);
  }

  private Function<ISortedMap<String, Object>, T> buildConstructorFunction(final Constructor<?> constr){
    final Variant type = annotationHandler.getType(constr);
    final IList<PropertyImp<T, ?>> props;
    if(type.equals(Variant.PARAMETER_NAMES)){
      signatureMatchesByNames(constr);
      props =
        XStream.ofArray(constr.getParameters())
        .map(param->properties.get(param.getName()))
        .collect(toIList())
      ;
    }
    else{
      verifyEqual(type, Variant.ALPHABETIC);
      signatureMatchesByOrder(constr);
      props = properties.values();
    }
    verifyEqual(constr.getParameterCount(), props.size());
    return map->{
      final Object[] args = new Object[props.size()];
      for(int i=0; i<args.length; i++){
        args[i] = getValue(map, props.get(i));
      }
      return clazz.cast(call(()->constr.newInstance(args)));
    };
  }

  private void signatureMatchesByOrder(final Constructor<?> c){
    verifyEqual(c.getParameterCount(), properties.size());
    final IList<PropertyImp<T, ?>> props = properties.values();
    final Parameter[] params = c.getParameters();
    verify(
      IntStream.range(0, properties.size())
      .allMatch(i->params[i].getType().equals(props.get(i).type().clazz))
    );
  }

  private <V> V getValue(final ISortedMap<String, Object> map, final PropertyImp<T, V> property) {
    final Opt<V> v1 = map.tryGet(property.name())
      .<Opt<V>>map(v->single(property.type().clazz.cast(v)))
      .orElseGet(()->property.defaultValue())
    ;
    final V value = v1
      .map(v->{
        property.fixedValue().ifPresentDo(f->verifyEqual(v,f));
        return v;
      })
      .orElseGet(()->property.fixedValue().orElseThrow(()->new IllegalArgumentException("Missing property.")))
    ;
    return value;
  }


}
