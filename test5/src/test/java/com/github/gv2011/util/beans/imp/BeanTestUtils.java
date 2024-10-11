package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.beans.BeanHandlerFactory;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.beans.imp.DefaultBeanFactory.DefaultBeanFactoryBuilder;
import com.github.gv2011.util.json.imp.JsonFactoryImp;

public final class BeanTestUtils {

  private BeanTestUtils(){staticClass();}

  public static final TypeRegistry createNewTypeRegistry(){
    return createNewTypeRegistry(new BeanHandlerFactory(){});
  }

  public static final TypeRegistry createNewTypeRegistry(final BeanHandlerFactory bhf){
    return new DefaultTypeRegistry(
      new JsonFactoryImp(),
      new DefaultBeanFactoryBuilder(),
      bhf
    );
  }
}
