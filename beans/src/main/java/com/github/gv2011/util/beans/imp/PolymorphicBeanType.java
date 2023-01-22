package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;

final class PolymorphicBeanType<T> extends BeanTypeSupport<T> {

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(PolymorphicBeanType.class);

  private final Opt<String> typePropertyName;
  private final TypeNameStrategy typeNameStrategy;


  PolymorphicBeanType(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final BeanFactory beanFactory,
    final Opt<String> typePropertyName,
    final TypeNameStrategy typeNameStrategy
  ) {
    super(beanClass, jf, annotationHandler, beanFactory);
    this.typePropertyName = typePropertyName;
    this.typeNameStrategy = typeNameStrategy;
  }

  @Override
  <V> PropertyImp<T,V> createProperty(final Method m, final TypeSupport<V> type) {
    if(!isTypeProperty(m)) return super.createProperty(m, type);
    else{
      verify(!annotationHandler.defaultValue(m).isPresent());
      final V fixedValue = type.parse(parseTolerant(
        type, jf(),
        (
          annotationHandler.typeName(clazz).merge(annotationHandler.fixedValue(m))
          .orElseGet(()->typeNameStrategy.typeName(clazz))
        )
      ));
      return PropertyImp.createFixed(this, m, typePropertyName.get(), type, fixedValue);
    }
  }

  @Override
  public ExtendedBeanBuilder<T> createBuilder() {
    return new DefaultBeanBuilder<>(this, resultWrapper, validator);
  }

  @Override
  void checkProperties(final ISortedMap<String, PropertyImp<T,?>> properties) {
    typePropertyName.ifPresentDo(n->
      verify(
        properties.keySet(),
        s->s.contains(n),
        s->format("The type property \"{}\" is missing in {} (available properties are: {}).", n, clazz, s)
      )
    );
  }

  private boolean isTypeProperty(final Method m) {
    return typePropertyName.map(n->n.equals(m.getName())).orElse(false);
  }

  @Override
  boolean isPolymorphic() {
    return true;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

}
