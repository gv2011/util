package com.github.gv2011.util.beans.imp;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonNode;

final class DefaultTypeResolver<B> implements TypeResolver<B>{

  private static final String DFAULT_TYPE_PROPERTY_NAME = "type";

  private final IMap<String, Class<? extends B>> subTypes;
  private final String typePropertyName;

  DefaultTypeResolver(final Class<B> rootType, final IMap<String, Class<? extends B>> subTypes, final AnnotationHandler annotationHandler) {
    this.subTypes = subTypes;
    typePropertyName = annotationHandler.typeNameProperty(rootType).orElse(DFAULT_TYPE_PROPERTY_NAME);
  }

  @Override
  public Opt<String> typePropertyName() {
    return Opt.of(typePropertyName);
  }

  @Override
  public Class<? extends B> resolve(final JsonNode json) {
    return subTypes.get(json.asObject().get(typePropertyName).asString());
  }

  @Override
  public void addTypeProperty(final BeanBuilder<? extends B> builder) {}

}
