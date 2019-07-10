package com.github.gv2011.util.beans;

import com.github.gv2011.util.serviceloader.Service;
import com.github.gv2011.util.tstr.TypedString;

@Service(defaultImplementation="com.github.gv2011.util.beans.imp/com.github.gv2011.util.beans.imp.DefaultTypeRegistry")
public interface TypeRegistry {

    <T> BeanType<T> beanType(Class<T> beanClass);

    <S extends TypedString<S>> S typedString(Class<S> clazz, String value);

    boolean isSupported(Class<?> clazz);


}
