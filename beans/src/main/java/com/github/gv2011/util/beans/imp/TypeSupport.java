package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verifyEqual;

import com.github.gv2011.util.Equal;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;

public abstract class TypeSupport<T> implements Type<T> {

    public final Class<T> clazz;

    TypeSupport(final Class<T> clazz) {
      this.clazz = clazz;
    }

    void initialize() {}

    abstract JsonFactory jf();

    @Override
    public String name() {
        return clazz.getName();
    }

    @SuppressWarnings("unchecked")
    <T2> TypeSupport<T2> castTo(final Class<T2> clazz) {
        verifyEqual(clazz, this.clazz);
        return (TypeSupport<T2>) this;
    }

    @Override
    public boolean isInstance(final Object object) {
      return clazz.isInstance(object);
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

    public <C,V> CollectionType<C,T,V> mapType(final Structure<C,T,V> structure, final TypeSupport<V> valueType) {
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

    public boolean isCollectionType() {
      return false;
    }

    public abstract boolean isForeignType();

    boolean isOptional() {
      return false;
    }

    boolean isPolymorphic(){
      return false;
    }

    public Opt<T> getDefault() {
      return Opt.empty();
    }

    @Override
    public T parse(final String string) {
      return parse(jf().deserialize(string));
    }

    protected boolean isInitialized() {
      return true;
    }

    protected abstract boolean hasStringForm();
}
