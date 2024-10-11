package com.github.gv2011.util.beans.imp;


import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanHandlerFactory;
import com.github.gv2011.util.json.JsonFactory;

public interface BeanFactoryBuilder {

  BeanFactory build(
    JsonFactory jf,
    AnnotationHandler annotationHandler,
    DefaultTypeRegistry defaultTypeRegistry,
    BeanHandlerFactory beanHandlerFactory
  );

}
