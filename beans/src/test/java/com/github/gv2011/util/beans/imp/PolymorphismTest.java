package com.github.gv2011.util.beans.imp;

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

import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.CollectionUtils.listOf;
import static com.github.gv2011.util.CollectionUtils.setOf;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

import com.github.gv2011.util.beans.BeanBuilder.CollectionSetter;
import com.github.gv2011.util.beans.BeanBuilder.ListSetter;
import com.github.gv2011.util.beans.BeanBuilder.Setter;
import com.github.gv2011.util.beans.imp.TestModel.BlackPea;
import com.github.gv2011.util.beans.imp.TestModel.ChickPea;
import com.github.gv2011.util.beans.imp.TestModel.NormalPot;
import com.github.gv2011.util.beans.imp.TestModel.Pea;
import com.github.gv2011.util.beans.imp.TestModel.Pot;
import com.github.gv2011.util.beans.imp.TestModel.SnowPea;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;


public class PolymorphismTest {


  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    assertThat(reg.notSupportedReason(Pea.class), is(""));
    reg.beanType(NormalPot.class);

    final AbstractPolymorphicSupport<Pea> peaType = reg.abstractBeanType(Pea.class);
    assertThat(peaType.isAbstractBean(), is(true));
    assertThat(peaType.isPolymorphic(), is(true));

    final DefaultBeanType<BlackPea> blackPeaType = reg.beanType(BlackPea.class);
    assertThat(blackPeaType.isAbstractBean(), is(false));
    assertThat(blackPeaType.isPolymorphic(), is(true));

    final BlackPea blackPea = reg.createBuilder(BlackPea.class).build();
    assertThat(blackPea.type(), is(BlackPea.class.getSimpleName()));

    final DefaultBeanType<ChickPea> chickPeaType = reg.beanType(ChickPea.class);
    assertThat(chickPeaType.getClass(), is(PolymorphicBeanType.class));
    final PropertyImp<ChickPea,String> typeProp = chickPeaType.getProperty(ChickPea::type);
    assertThat(typeProp.fixedValue(), is(Optional.of("chicks")));
    final ChickPea chickPea = reg.createBuilder(ChickPea.class).build();
    assertThat(chickPea.type(), is("chicks"));

    final SnowPea snowPea = reg.createBuilder(SnowPea.class).build();
    assertThat(snowPea.type(), is("saccharatum"));

  }

  @Test
  public void testPolySet() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    final SnowPea snowPea1 = reg.createBuilder(SnowPea.class).build();
    final SnowPea snowPea2 = reg.createBuilder(SnowPea.class)
      .set(SnowPea::propC).to("snow2")
      .build()
    ;
    final Function<Pot,IList<? extends Pea>> f = (Pot::moreContent);
    final IList<SnowPea> list = listOf(snowPea2, snowPea2);
    reg.createBuilder(Pot.class).<Pea>setList(Pot::moreContent).to(list);
//    final IList<Object> lala = null;
//    lala.cast();
//    final Setter<NormalPot, ?> set = reg.createBuilder(NormalPot.class).set(Pot::moreContent);
//
//    final ISet<SnowPea> peas = setOf(snowPea1, snowPea2);
//    final Collection<Pea> peas2;
//    final CollectionSetter<NormalPot, ISet<? extends Pea>, ? extends Pea> setC = reg.createBuilder(NormalPot.class)
//      .set(Pot::content).to(snowPea1)
//      .setC(Pot::moreContent);
//    setC.to(null)
//      .build()
//    ;
  }


}
