package com.github.gv2011.util.beans.imp;


import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toOpt;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static com.github.gv2011.util.icol.ICollections.toIMap;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static com.github.gv2011.util.icol.ICollections.upcast;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanHandler;
import com.github.gv2011.util.beans.BeanHashCode;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.KeyBean;
import com.github.gv2011.util.beans.Parser;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.beans.Ref;
import com.github.gv2011.util.beans.Validator;
import com.github.gv2011.util.icol.IEntry;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonObject;


public abstract class BeanTypeSupport<T> extends ObjectTypeSupport<T> implements BeanType<T> {

  private static final Logger LOG = getLogger(BeanTypeSupport.class);

  @SuppressWarnings("rawtypes")
  private static final Partial EMPTY_PARTIAL = createEmptyPartial();

  private final JsonFactory jf;
  final AnnotationHandler annotationHandler;
  private final BeanFactory beanFactory;
  private final boolean isKeyBean;
  private final boolean writeAsJsonString;
  private final Opt<BeanHandler<T>> beanHandler;
  private final Function<String,T> parser;
  private final Constant<ToIntFunction<T>> hashCodeFunction = Constants.cachedConstant(this::createHashCodeFunction);
  protected final UnaryOperator<T> resultWrapper;
  protected final UnaryOperator<T> validator;
  private final Function<T,String> toStringFunction;
  private final IMap<Method,BiFunction<T, Object[],Object>> defaultMethods;

  //recursion, init later
  private volatile @Nullable ISortedMap<String, PropertyImp<T,?>> properties;
  //recursion, init later
  private volatile @Nullable Opt<T> defaultValue;
  //recursion, init later
  protected volatile @Nullable Opt<Function<ISortedMap<String, Object>, T>> constructor;
  //recursion, init later
  private volatile @Nullable Opt<PropertyImp<T, ?>> keyProperty;


  protected BeanTypeSupport(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final BeanFactory beanFactory,
    final Opt<BeanHandler<T>> beanHandler
  ) {
    super(beanClass);
    this.jf = jf;
    this.annotationHandler = annotationHandler;
    this.beanFactory = beanFactory;
    this.isKeyBean = isKeyBean(beanClass);

    this.beanHandler = beanHandler;
    final Opt<Class<? extends Parser<?>>> annotatedParser = annotationHandler.getParser(clazz);
    writeAsJsonString = annotatedParser.isPresent();
    parser = beanHandler
      .flatMap(h->h.canParse() ? Opt.of(h::parse) : Opt.<Function<String,T>>empty())
      .orElseGet(()->
        annotatedParser
        .map(this::createParserFromClass)
        .orElseGet(()->
          s->parse(jf().deserialize(s).asObject())
        )
      )
    ;
    final Opt<Class<?>> implementingClass = annotationHandler.getImplementingClass(clazz);
    implementingClass.ifPresentDo(this::verifyHasConstructor);
    resultWrapper = createResultWrapper(implementingClass, beanHandler);
    validator = annotationHandler.getValidatorClass(clazz)
      .map(this::createValidatorFromClass)
      .orElseGet(UnaryOperator::identity)
    ;
    this.toStringFunction = toStringFunction(beanClass).orElseGet(()->this::defaultToString);
    defaultMethods = ReflectionUtils.getDefaultMethodInvokers(beanClass);
  }

  private static final <T> Optional<Function<T,String>> toStringFunction(final Class<T> beanClass){
    return Arrays.stream(beanClass.getMethods()).filter(m->isToStringMethod(m, beanClass)).findAny()
      .map(m->(b->(String)call(()->m.invoke(null, new Object[]{b}))))
    ;
  }

  private static final boolean isToStringMethod(final Method m, final Class<?> beanClass){
    if(m.getName().equals("toString") && Modifier.isStatic(m.getModifiers()) && m.getParameterCount()==1){
      verifyEqual(m.getReturnType(), String.class);
      verifyEqual(m.getParameterTypes()[0], beanClass);
      return true;
    }
    else return false;
  }

  static final boolean isKeyBean(final Class<?> beanClass) {
    return KeyBean.class.isAssignableFrom(beanClass);
  }

  private void verifyHasConstructor(final Class<?> implementingClass) {
    verifyEqual(
      ( XStream.of(implementingClass.getConstructors())
        .filter(annotationHandler::annotatedAsConstructor)
        .count()
      ),
      1L
    );
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private final Function<String,T> createParserFromClass(final Class<? extends Parser<?>> parserClass){
    final Parser parserInstance = call(()->parserClass.getConstructor().newInstance());
    return s->clazz.cast(parserInstance.parse(s, createBuilder()));
  }

  private final UnaryOperator<T> createResultWrapper(final Opt<Class<?>> implementingClass, final Opt<BeanHandler<T>> beanHandler){
    final UnaryOperator<T> annotatedWrapper = implementingClass
      .flatMap(this::tryGetCoreConstructor)
      .orElseGet(UnaryOperator::identity)
    ;
    return beanHandler
      .map(h->(UnaryOperator<T>)core->h.wrapBean(core, annotatedWrapper))
      .orElse(annotatedWrapper)
    ;
  }

  private Opt<UnaryOperator<T>> tryGetCoreConstructor(final Class<?> c) {
    return
      XStream.ofArray(call(()->c.asSubclass(clazz).getConstructors()))
      .tryFindAny(constr->annotationHandler.delegateConstructor(constr))
      .map(constr->{
        verifyEqual(constr.getParameterCount(), 1);
        verifyEqual(constr.getParameterTypes()[0], clazz);
        return (core->clazz.cast(call(()->constr.newInstance(core))));
      })
    ;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private final UnaryOperator<T> createValidatorFromClass(final Class<? extends Validator<?>> validatingClass){
    final Validator validatorInstance = call(()->validatingClass.getConstructor().newInstance());
    return unvalidated->{
      final String msg = validatorInstance.invalidMessage(unvalidated);
      verify(msg, String::isEmpty);
      return unvalidated;
    };
  }

  @Override
  final JsonFactory jf() {
    return jf;
  }

  private final Function<Type,TypeSupport<?>> registry(){
    return beanFactory.registry();
  }

  @Override
  final void initialize() {
    if(!isInitialized()) {
      verify(properties==null);
      verify(defaultValue==null);
      LOG.debug("{}: initializing properties.", this);
      properties = createProperties();
      checkProperties(properties);
      LOG.debug("{}: initialized properties: {}.", this, properties.keySet());
      keyProperty  = properties.values().stream().filter(p->p.isKey()).collect(toOpt());
      constructor = new ConstructorHandler<>(clazz, annotationHandler, properties)
        .tryCreateConstructor(annotationHandler.getImplementingClass(clazz))
      ;
      LOG.debug("{}: initializing default value.", this);
      defaultValue = createDefaultValue(properties);
      LOG.debug("{} initialized default value.", this);
    }
    else{
      verify(properties!=null);
      verify(defaultValue!=null);
    }
  }

  @Override
  protected final boolean isInitialized() {
    return defaultValue!=null;
  }

  @Override
  public final int hashCode(final T bean) {
    return hashCodeFunction.get().applyAsInt(bean);
  }

  private ToIntFunction<T> createHashCodeFunction(){
    final IMap<String, Function<T,?>> attributes =
      properties.values().stream()
      .filter(not(PropertyImp::computed))
      .collect(toIMap(
        Property::name,
        p->bean->p.getValue(bean)
      ))
    ;
    return BeanHashCode.createHashCodeFunctionNamed(clazz, attributes);
  }

  @Override
  public final boolean equal(final T bean, final Object other) {
    if(bean==other) return true;
    else if(!clazz.isInstance(other)) return false;
    else if(bean.hashCode()!=other.hashCode()) return false;
    else{
      final T otherBean = clazz.cast(other);
      return
        properties.values().stream()
        .filter(not(PropertyImp::computed))
        .allMatch(p->p.getValue(bean).equals(p.getValue(otherBean)))
      ;
    }
  }

  @Override
  public final String toString(final T bean) {
    return toStringFunction.apply(bean);
  }

  private String defaultToString(final T bean) {
    return
      properties.values().stream()
      .filter(not(PropertyImp::computed))
      .map(p->p.name() + IEntry.KEY_VALUE_SEPARATOR + p.getValue(bean))
      .collect(joining(IMap.ENTRY_SEPARATOR, clazz.getSimpleName()+IMap.PREFIX, IMap.SUFFIX))
    ;
  }

  final ISortedMap<String, Object> getValues(final T bean) {
    return properties().values().stream()
      .flatMap(p->{
        Stream<Pair<String,Object>> result;
        final Object value = p.getValue(bean);
        if(p.defaultValue().map(dv->dv.equals(value)).orElse(false)){
          result = XStream.empty(); //remove default values
        }
        else result = XStream.of(pair(p.name(),value));
        return result;
      })
      .collect(toISortedMap())
    ;
  }

  void checkProperties(final ISortedMap<String, PropertyImp<T,?>> properties) {}

  private Opt<T> createDefaultValue(final ISortedMap<String, PropertyImp<T,?>> properties) {
    LOG.debug("Creating default value for {}.", this);
    if(properties.values().stream()
      .allMatch(p->{
        Opt<?> defaultValue;
        try {
          defaultValue = p.defaultValue();
        } catch (final Exception e) {
          throw new IllegalStateException(format("Could not get default value of {} of {}.", p, this), e);
        }
        return defaultValue.isPresent();
      })
    ){
      final BeanBuilder<T> b = createBuilder();
      return Opt.of(b.build());
    }
    else return Opt.empty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Partial<T> emptyPartial() {
      return EMPTY_PARTIAL;
  }

  @Override
  public final ISortedMap<String, PropertyImp<T,?>> properties() {
      if(properties==null) initialize();
      return notNull(properties);
  }

  @Override
  public final Opt<Property<?>> keyProperty() {
    if(properties==null) initialize();
    return upcast(notNull(keyProperty));
  }

  @Override
  public final boolean isKeyBean() {
      return isKeyBean;
  }

  protected final Opt<Function<ISortedMap<String, Object>, T>> constructor() {
      return notNull(constructor);
  }

  private ISortedMap<String, PropertyImp<T,?>> createProperties() {
    return Arrays.stream(clazz.getMethods())
    .filter(m->beanFactory.isPropertyMethod(clazz, m))
    .map(this::createProperty)
    .collect(toISortedMap(
        p->p.name(),
        p->p
    ));
  }

  private <V> PropertyImp<T,V> createProperty(final Method m) {
    @SuppressWarnings("unchecked")
    final TypeSupport<V> type = (TypeSupport<V>) registry().apply(m.getGenericReturnType());
    try {
      return createProperty(m, type);
    } catch (final RuntimeException e) {
      throw new RuntimeException(format("{}: Could not create property of type {} for method {}.", this, type, m), e);
    }
  }

  <V> PropertyImp<T,V> createProperty(final Method m, final TypeSupport<V> type) {
    final Opt<V> fixedValue =
      annotationHandler.fixedValue(m)
      .map(v->type.parse(parseTolerant(type, jf, v)))
    ;
    final Opt<V> annotatedDefaultValue =
      annotationHandler.defaultValue(m)
      .map(v->type.parse(parseTolerant(type, jf, v)))
    ;
    final Opt<Function<T,V>> function =
      annotationHandler.annotatedAsComputed(m)
      ? Opt.of(createFunction(m))
      : Opt.empty()
    ;
    if(fixedValue.isPresent()) {
      verify(!function.isPresent());
      if(annotatedDefaultValue.isPresent()) verifyEqual(annotatedDefaultValue, fixedValue);
      return PropertyImp.createFixed(this, m, m.getName(), type, fixedValue.get());
    }
    else if(function.isPresent()) {
      verify(!annotatedDefaultValue.isPresent());
      return PropertyImp.createComputed(this, m, type, function.get());
    }
    else{
      final Opt<V> defaultValue = annotatedDefaultValue
        .or(()->type.isInitialized() ? type.getDefault() : Opt.empty())
      ;
      return PropertyImp.create(
        this,
        m,
        type,
        defaultValue,
        annotationHandler.annotatedAsKey(m)
      );
    }
  }

  @SuppressWarnings("unchecked")
  private <V> Function<T,V> createFunction(final Method m) {
    if(annotationHandler.getImplementingClass(clazz).isPresent()) return unsupported();
    else {
      Opt<Method> optStaticMethod;
      try {
        optStaticMethod = Opt.of(clazz.getMethod(m.getName(), new Class<?>[] {clazz}));
      } catch (final NoSuchMethodException e) {
        optStaticMethod = Opt.empty();
        if(beanHandler.isEmpty()){
          throw new RuntimeException(format("No implementation found for computed attribute {}.", m.getName()), e);
        }
      }
      return optStaticMethod
        .map(staticMethod->{
          verify(Modifier.isStatic(staticMethod.getModifiers()));
          verify(m.getReturnType().isAssignableFrom(staticMethod.getReturnType()));
          return (Function<T,V>) t->(V)call(()->staticMethod.invoke(null, t));
        })
        .orElseGet(this::unsupported)
      ;
    }
  }

  private <V> Function<T, V> unsupported() {
    return t->{throw new UnsupportedOperationException();};
  }

  static final JsonNode parseTolerant(final TypeSupport<?> type, final JsonFactory jf, final String string) {
    try {
      JsonNode result;
          if(type.isOptional()) {
              if(string.trim().equals("null")) result = jf.jsonNull();
              else {
                  final CollectionType<?,?,?> cType = (CollectionType<?,?,?>) type;
                  result = parseTolerant(cType.elementType(), jf, string);
              }
          }
          else if(type instanceof AbstractElementaryType) {
            final AbstractElementaryType<?> eType = (AbstractElementaryType<?>) type;
            if(eType.jsonNodeType().equals(JsonNodeType.STRING)) {
              final String trimmed = string.trim();
              if(trimmed.isEmpty()?true:trimmed.charAt(0)!='"') {
                  result = jf.primitive(string);
              }
              else result = jf.deserialize(string);
            }
            else result = jf.deserialize(string);
        }
        else result = jf.deserialize(string);
        return result;
    } catch (final Exception e) {
      throw new IllegalArgumentException(format(
          "Could not parse annotated default value \"{}\" to type {}.",
          string, type
      ));
    }
  }

  @Override
  public final T parse(final JsonNode json) {
    final JsonObject obj = (JsonObject) json;
    verifyJsonHasNoAdditionalProperties(obj);
    final BeanBuilder<T> b = createBuilder();
    for(final PropertyImp<T,?> p: properties().values()) {
      final Opt<JsonNode> childNode = obj.tryGet(p.name());
      if(childNode.isPresent()) {
        final JsonNode value = childNode.get();
        try {
          setJsonValue(b, p, value);
        }
        catch (final RuntimeException e) { throw new IllegalArgumentException(
          format("Could not set property {} from JSON {}.", p, value), e);
        }
      }
    }
    return b.build();
  }

  @Override
  public final T parse(final String string) {
    return parser.apply(string);
  }


  private void verifyJsonHasNoAdditionalProperties(final JsonObject obj) {
    final TreeSet<String> additional = new TreeSet<>(obj.keySet());
    additional.removeAll(properties().keySet());
    verify(
      additional.isEmpty(),
      ()->format(
        "{}: Cannot parse JSON because of additional properties: {}", this, additional
      )
    );
  }


  private <V> void setJsonValue(final BeanBuilder<T> b, final Property<V> p, final JsonNode json) {
    setValue(b, p, p.type().parse(json));
  }

  private <V> void setValue(final BeanBuilder<T> b, final Property<V> p, final V value) {
    b.set(p, value);
  }

  @Override
  public final JsonNode toJson(final T object) {
    return writeAsJsonString ? jf.primitive(object.toString()) : toJsonObject(object);
  }

  public final JsonObject toJsonObject(final T object) {
    return properties().values().stream()
      .filter(p->!p.function().isPresent())
      .flatOpt(p->toJson(object, p).map(j->pair(p.name(), j)))
      .collect(jf.toJsonObject())
    ;
  }

  @Override
  public final boolean isDefault(final T obj) {
    return properties().values().stream().allMatch(p->isDefault(obj, p));
  }



  @Override
  public final Opt<T> getDefault() {
    initialize();
    return notNull(defaultValue, ()->clazz.toString());
  }

  private <V> boolean isDefault(final T bean, final PropertyImp<T,V> property) {
    return property.type().isDefault(get(bean, property));
  }

  private <V> Opt<JsonNode> toJson(final T bean, final PropertyImp<T,V> property) {
    final V value = get(bean, property);
    final TypeSupport<V> type = property.type();
    if(type.isDefault(value)) return Opt.empty();
    else return Opt.of(type.toJson(value));
  }

  @Override
  public final <V> V get(final T bean, final Property<V> property) {
    try {
      return property.type().cast(clazz.getMethod(property.name()).invoke(bean));
    } catch (final Exception e) {
      throw wrap(e, format("Could not read property {} of {}.", property, this));
    }
  }

  @Override
  public final Opt<Ref<?,T>> ref(final T bean) {
    //return keyProperty.map(p->new DefaultRef(get(bean, p), ()->bean));
    return notYetImplemented();
  }

  private static <B> Partial<B> createEmptyPartial() {
      return new Partial<>() {
          @Override
          public boolean isAvailable(final Property<?> p) {return false;}
          @Override
          public boolean isAvailable(final String propertyName) {return false;}
          @Override
          public <T> T get(final Property<T> p) {throw new NoSuchElementException();}
      };
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final <V> PropertyImp<T,V> getProperty(final Function<T, V> method) {
    final Method m = getMethod(method);
    final PropertyImp<T,?> result = properties().get(m.getName());
    verifyEqual(result.type().clazz, m.getReturnType());
    return (PropertyImp)result;
  }


  protected <V> Method getMethod(final Function<T, V> method) {
    return ReflectionUtils.method(clazz, method);
  }

  Bean getKey(final ISortedMap<String, Object> values) {
    return notYetImplemented();
  }


  protected final Object invokeDefault(final T proxy, final Method method, final Object[] args) {
    return defaultMethods.get(method).apply(proxy, args);
  }

  @Override
  protected final Opt<BeanTypeSupport<T>> asBeanType(){
    return Opt.of(this);
  }

}
