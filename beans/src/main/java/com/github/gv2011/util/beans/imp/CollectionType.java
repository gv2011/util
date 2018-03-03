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
import java.util.Optional;

import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;

final class CollectionType<C,K,E> extends AbstractType<C>{

  private final Optional<AbstractType<K>> keyType;

  private final AbstractType<E> elementType;

  final Structure<C,K,E> structure;

  CollectionType(final Structure<C,K,E> structure, final AbstractType<E> elementType) {
    this(structure, Optional.empty(), elementType);
    assert !structure.equals(Structure.map());
  }

  CollectionType(final Structure<C,K,E> structure, final AbstractType<K> keyType, final AbstractType<E> elementType) {
    this(structure, Optional.of(keyType), elementType);
    assert structure.equals(Structure.map());
  }

  private CollectionType(
    final Structure<C,K,E> structure, final Optional<AbstractType<K>> keyType, final AbstractType<E> elementType
  ) {
    super(structure.clazz());
    this.structure = structure;
    this.keyType = keyType;
    this.elementType = elementType;
  }

  @Override
  JsonFactory jf() {
    return elementType.jf();
  }

  @Override
  public int hashCode() {
    return 31*elementType.hashCode() + keyType.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if(!(obj instanceof CollectionType)) return false;
    else {
      final CollectionType<?, ?, ?> other = (CollectionType<?,?,?>)obj;
      return
        elementType.equals(other.elementType()) &&
        keyType.equals(other.keyType())
      ;
    }
  }

  public Optional<AbstractType<K>> keyType() {
    return keyType;
  }

  public AbstractType<E> elementType() {
    return elementType;
  }

  @Override
  public C parse(final JsonNode json) {
    return structure.convert(json, this);
  }

  @Override
  public JsonNode toJson(final C object) {
    return structure.toJson(this, object);
  }

  @Override
  public boolean isDefault(final C obj) {
    return structure.isEmpty(obj);
  }

  @Override
  public Optional<C> getDefault() {
    return Optional.of(structure.empty());
  }

  @Override
  public String name() {
    return super.name()+"<"+elementType.name()+">";
  }

  @Override
  public String toString() {
    return super.toString()+"<"+elementType+">";
  }

  @Override
  public boolean isCollectionType() {
      return true;
  }

  @Override
  boolean isOptional() {
    return structure.equals(Structure.opt());
  }


}
