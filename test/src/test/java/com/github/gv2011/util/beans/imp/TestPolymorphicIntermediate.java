package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.testutil.Assert.assertThat;

import org.junit.Test;

import com.github.gv2011.testutil.Matchers;
import com.github.gv2011.util.beans.AbstractRoot;
import com.github.gv2011.util.beans.Bean;

public class TestPolymorphicIntermediate {

  @AbstractRoot(subClasses = { ConcreteBean.class })
  public static interface RootBean extends Bean{
    String type();
  }

  @AbstractRoot
  public static interface Intermediate extends RootBean {}

  public static interface ConcreteBean extends Intermediate {
    String prop1();
    Intermediate intermediate();
  }

  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    reg.beanType(ConcreteBean.class).createBuilder();
    final TypeSupport<Intermediate> type = reg.type(Intermediate.class);
    assertThat(type, Matchers.instanceOf(PolymorphicIntermediateType.class));
  }

}
