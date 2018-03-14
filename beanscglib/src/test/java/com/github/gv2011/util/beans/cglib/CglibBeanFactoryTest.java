package com.github.gv2011.util.beans.cglib;

/*-
 * #%L
 * util-beans-cglib
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
import static com.github.gv2011.testutil.Matchers.isA;
import static com.github.gv2011.testutil.Matchers.mapWithSize;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.gv2011.util.beans.imp.BeanTypeSupport;
import com.github.gv2011.util.beans.imp.DefaultTypeRegistry;

public class CglibBeanFactoryTest {

  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry(jsonFactory(), new CglibBeanFactoryBuilder());
    final TestModel adder = reg.createBuilder(TestModel.class)
      .set(TestModel::number1).to(1)
      .set(TestModel::number2).to(2L)
      .build()
    ;
    assertThat(adder.sum(), is(3L));

    final BeanTypeSupport<TestModel> beanType = reg.beanType(TestModel.class);
    assertThat(beanType, isA(CglibBeanType.class));
    assertThat(beanType.properties(), mapWithSize(2));
  }

}
