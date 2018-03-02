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
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;

import com.github.gv2011.util.beans.Abstract;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.beans.TypeName;
import com.github.gv2011.util.icol.IList;


public class PolymorphismTest {

  @Abstract
  public static interface Sized{
    int size();
  }

  public static interface Coloured{
    String colour();
  }

  @Abstract(subClasses={BlackPea.class, ChickPea.class})
  public static interface Pea extends Sized, Coloured{
    String type();
    IList<? extends Pea> neighbours();
  }

  public static interface BlackPea extends Pea{
    @Override
    IList<Pea> neighbours();
    String propA();
  }

  public static interface ChickPea extends Pea{
    @Override
    IList<Pea> neighbours();
    @Override
    @FixedValue("chicks")
    String type();
    String propB();
  }

  @Abstract
  public static interface SpecialPea extends Pea{
    @Override
    IList<SpecialPea> neighbours();
  }

  @TypeName("saccharatum")
  public static interface SnowPea extends SpecialPea{
    String propC();
  }

  public static interface Pot{
    Pea content();
  }

  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    reg.beanType(Pot.class);

    final PolymorphicAbstractBeanRootType<Pea> peaType = reg.abstractBeanType(Pea.class);
    assertThat(peaType.isAbstractBean(), is(true));
    assertThat(peaType.isPolymorphic(), is(true));

    final DefaultBeanType<BlackPea> blackPeaType = reg.beanType(BlackPea.class);
    assertThat(blackPeaType.isAbstractBean(), is(false));
    assertThat(blackPeaType.isPolymorphic(), is(true));

    final BlackPea blackPea = reg.createBuilder(BlackPea.class).build();
    assertThat(blackPea.type(), is(BlackPea.class.getSimpleName()));

    final DefaultBeanType<ChickPea> chickPeaType = reg.beanType(ChickPea.class);
    assertThat(chickPeaType.getClass(), is(PolymorphicConcreteBeanType.class));
    final PropertyImp<String> typeProp = chickPeaType.getProperty(ChickPea::type);
    assertThat(typeProp.fixedValue(), is(Optional.of("chicks")));
    final ChickPea chickPea = reg.createBuilder(ChickPea.class).build();
    assertThat(chickPea.type(), is("chicks"));

    final SnowPea snowPea = reg.createBuilder(SnowPea.class).build();
    assertThat(snowPea.type(), is("saccharatum"));

  }


}
