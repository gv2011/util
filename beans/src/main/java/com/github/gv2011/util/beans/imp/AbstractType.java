package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verifyEqual;

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

import com.github.gv2011.util.Equal;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.json.JsonFactory;

public abstract class AbstractType<T> implements Type<T> {

    final Class<T> clazz;

    AbstractType(final Class<T> clazz) {
      this.clazz = clazz;
    }

    void initialize() {}

    abstract JsonFactory jf();

    @Override
    public String name() {
        return clazz.getName();
    }

    @SuppressWarnings("unchecked")
    <T2> AbstractType<T2> castTo(final Class<T2> clazz) {
        verifyEqual(clazz, this.clazz);
        return (AbstractType<T2>) this;
    }

    @Override
    public T cast(final Object object) {
        return clazz.cast(object);
    }

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    public <C> CollectionType<C,Nothing,T> collectionType(final Structure<C,Nothing,T> structure) {
      return new CollectionType<>(structure, this);
    }

    public <C,V> CollectionType<C,T,V> mapType(final Structure<C,T,V> structure, final AbstractType<V> valueType) {
      return new CollectionType<>(structure, this, valueType);
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof CollectionType) return false;
        else return Equal.equal(this, obj, Type.class, o->o.name().equals(name()));
    }

    public boolean isDefault(final T obj) {
      return false;
    }

    public Optional<T> getDefault() {
      return Optional.empty();
    }

    public boolean isCollectionType() {
        return false;
    }

    boolean isOptional() {
      return false;
    }

    boolean isPolymorphic(){
      return false;
    }

    boolean isAbstractBean(){
      return false;
    }

}
