package com.github.gv2011.util.beans;

public interface ElementaryTypeHandlerFactory {

    boolean isSupported(Class<?> clazz);

    <T> ElementaryTypeHandler<T> getTypeHandler(Class<T> clazz);

}
