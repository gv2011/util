package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;

import org.junit.Test;

import com.github.gv2011.util.beans.imp.TestModel.BlackPea;
import com.github.gv2011.util.beans.imp.TestModel.ChickPea;
import com.github.gv2011.util.beans.imp.TestModel.NormalPot;
import com.github.gv2011.util.beans.imp.TestModel.Pea;
import com.github.gv2011.util.beans.imp.TestModel.SnowPea;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.time.TimeSpan;


public class PolymorphismTest {


  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    assertThat(reg.notSupportedReason(Pea.class), is(""));
    reg.beanType(NormalPot.class);

    final AbstractPolymorphicSupport<Pea> peaType = reg.abstractBeanType(Pea.class);
    assertThat(peaType.isAbstract(), is(true));
    assertThat(peaType.isPolymorphic(), is(true));

    final BeanTypeSupport<BlackPea> blackPeaType = reg.beanType(BlackPea.class);
    assertThat(blackPeaType.isAbstract(), is(false));
    assertThat(blackPeaType.isPolymorphic(), is(true));

    final BlackPea blackPea = blackPeaType.createBuilder()
      .set(BlackPea::timeSpan).to(TimeSpan.parse("(2022-12-28T09:15:53Z,2022-12-28T09:15:55Z)"))
      .build()
    ;
    assertThat(blackPea.type(), is(BlackPea.class.getSimpleName()));

    final BeanTypeSupport<ChickPea> chickPeaType = reg.beanType(ChickPea.class);
    assertThat(chickPeaType.getClass(), is(PolymorphicBeanType.class));
    final PropertyImp<ChickPea,String> typeProp = chickPeaType.getProperty(ChickPea::type);
    assertThat(typeProp.fixedValue(), is(Opt.of("chicks")));
    final ChickPea chickPea = chickPeaType.createBuilder()
      .set(ChickPea::timeSpan).to(TimeSpan.parse("(2022-12-27T09:15:53Z,2022-12-28T09:15:55Z)"))
      .build()
    ;
    assertThat(chickPea.type(), is("chicks"));

    final BeanTypeSupport<SnowPea> snowPeaType = reg.beanType(SnowPea.class);
    final SnowPea snowPea = snowPeaType.createBuilder()
      .set(SnowPea::timeSpan).to(TimeSpan.parse("(2022-12-26T09:15:53Z,2022-12-28T09:15:55Z)"))
      .build()
    ;
    assertThat(snowPea.type(), is("saccharatum"));

  }

}
