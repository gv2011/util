package com.github.gv2011.util.beans.model;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.beans.BeanHandler;
import com.github.gv2011.util.beans.BeanHandlerFactory;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.beans.imp.BeanTestUtils;
import com.github.gv2011.util.icol.Opt;

class TypeSetTest {

  @Test
  void test() {
    final TypeRegistry typeRegistry = BeanTestUtils.createNewTypeRegistry(
      new BeanHandlerFactory(){
        @Override
        public <T> Opt<BeanHandler<T>> createBeanHandler(final Class<T> beanClass) {
          return Opt.of(new BeanHandler<T>(){});
        }
      }
    );
    typeRegistry.beanType(TypeSet.class);
  }

}
