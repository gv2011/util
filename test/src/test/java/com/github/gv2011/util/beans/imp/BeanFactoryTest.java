package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.testutil.Matchers.not;

import org.junit.Test;

import com.github.gv2011.util.beans.BeanHandlerFactory;
import com.github.gv2011.util.beans.imp.DefaultBeanFactory.DefaultBeanFactoryBuilder;
import com.github.gv2011.util.beans.imp.TestModel.Pea;
import com.github.gv2011.util.json.imp.JsonFactoryImp;

public class BeanFactoryTest {

  private final DefaultBeanFactory beanFactory;

  public BeanFactoryTest() {
    final DefaultTypeRegistry typeRegistry = new DefaultTypeRegistry(
      new JsonFactoryImp(),
      new DefaultBeanFactoryBuilder(),
      new BeanHandlerFactory(){}
    );
    beanFactory = (DefaultBeanFactory) typeRegistry.beanFactory;
  }

  @Test
  public void test() {
    assertThat(beanFactory.notBeanReason(Pea.class), not(is("")));
    assertThat(beanFactory.isBeanClass(Pea.class), is(false));
    assertThat(beanFactory.notPolymorphicRootClassReason(Pea.class), is(""));
  }

}
