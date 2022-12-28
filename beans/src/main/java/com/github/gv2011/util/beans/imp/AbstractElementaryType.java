package com.github.gv2011.util.beans.imp;

import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;

abstract class AbstractElementaryType<E> extends TypeSupport<E>{

  private final JsonFactory jf;

  AbstractElementaryType(final JsonFactory jf, final Class<E> clazz) {
    super(clazz);
    this.jf = jf;
  }

  @Override
  final JsonFactory jf() {
    return jf;
  }

  abstract ElementaryTypeHandler<E> handler();

  @Override
  public final E parse(final JsonNode json) {
    return handler().fromJson(json);
  }

  @Override
  public final JsonNode toJson(final E object) {
    return handler().toJson(object, jf());
  }

  @Override
  public final boolean isDefault(final E obj) {
    return handler().defaultValue().map(d->d.equals(obj)).orElse(false);
  }

  @Override
  public final Opt<E> getDefault() {
    return handler().defaultValue();
  }

  final JsonNodeType jsonNodeType() {
    return handler().jsonNodeType();
  }

  @Override
  public E cast(final Object object) {
    return handler().cast(clazz, object);
  }

  @Override
  public final boolean isForeignType() {
    return false;
  }

  @Override
  public final E parse(String string) {
    return handler().parse(string);
  }

}
