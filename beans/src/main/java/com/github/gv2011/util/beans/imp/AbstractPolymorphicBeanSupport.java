package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.CollectionUtils.toIMap;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Optional;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.json.JsonNode;

abstract class AbstractPolymorphicBeanSupport<B> extends AbstractType<B>{

  final DefaultTypeRegistry registry;

  AbstractPolymorphicBeanSupport(final DefaultTypeRegistry registry, final Class<B> clazz) {
    super(registry.jf, clazz);
    this.registry = registry;
  }

  abstract PolymorphicAbstractBeanRootType<? super B> rootType();

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final JsonNode toJson(final B object) {
    return ((Type)registry.getType(object)).toJson(object);
  }

  @Override
  public final B parse(final JsonNode json) {
    final Class<?> clazz = typeResolver().resolve(json);
    assert this.clazz.isAssignableFrom(clazz) && !this.clazz.equals(clazz) && clazz.isInterface(); //real subclass
    return this.clazz.cast(registry.type(clazz).parse(json));
  }



  @Override
  final boolean isPolymorphic() {
    return true;
  }

  @Override
  final boolean isAbstractBean() {
    return true;
  }



  abstract TypeResolver<? super B> typeResolver();

  final Optional<TypeResolver<B>> getAnnotatedTypeResolver() {
    return registry.annotationHandler.typeResolver(clazz)
      .map(c->new TypeResolverWrapper<>(clazz, call(c::newInstance)))
    ;
  }

  final boolean hasDefaultTypeResolver() {
    return typeResolver().getClass().equals(DefaultTypeResolver.class);
  }

  final TypeResolver<B> createDefaultTypeResolver(final ISet<Class<?>> classes) {
    final IMap<String, Class<? extends B>> subTypes = classes.stream().collect(toIMap(
      Class::getSimpleName,
      c -> c.asSubclass(clazz)
    ));
    return new DefaultTypeResolver(subTypes);
  }

  final Optional<String> typePropertyName() {
    return hasDefaultTypeResolver() ? Optional.of("type") : Optional.empty();
  }


  private final class DefaultTypeResolver implements TypeResolver<B> {

    private final IMap<String, Class<? extends B>> subTypes;

    private DefaultTypeResolver(final IMap<String, Class<? extends B>> subTypes) {
      this.subTypes = subTypes;
    }

    @Override
    public Class<? extends B> resolve(final JsonNode json) {
      return subTypes.get(json.asObject().get("type").asString());
    }

    @Override
    public void addTypeProperty(final BeanBuilder<? extends B> builder) {}

  }

  /**
   * Verifies results of external TypeResolver.
   */
  private static final class TypeResolverWrapper<B> implements TypeResolver<B>{

    private final Class<B> clazz;
    private final TypeResolver<?> delegate;

    private TypeResolverWrapper(final Class<B> clazz, final TypeResolver<?> delegate) {
      this.clazz = clazz;
      this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends B> resolve(final JsonNode json) {
      final Class<?> result = delegate.resolve(json);
      verify(clazz.isAssignableFrom(result) && !clazz.equals(result) && result.isInterface());
      return (Class<? extends B>) result;
    }
  }

}
