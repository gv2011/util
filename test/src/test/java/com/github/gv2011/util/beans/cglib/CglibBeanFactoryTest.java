package com.github.gv2011.util.beans.cglib;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;

import com.github.gv2011.util.beans.cglib.PolymorphicTestModel.Elephant;
import com.github.gv2011.util.beans.cglib.TestModel.Id;
import com.github.gv2011.util.beans.imp.BeanTypeSupport;
import com.github.gv2011.util.beans.imp.DefaultTypeRegistry;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.tstr.TypedString;

public class CglibBeanFactoryTest {

  private final DefaultTypeRegistry registry = new DefaultTypeRegistry(jsonFactory(), new CglibBeanFactoryBuilder());

  @Test
  public void testModel() {
    final ISortedMap<Id, ISortedSet<UUID>> map = ICollections.<Id, ISortedSet<UUID>>sortedMapBuilder()
      .put(TypedString.create(Id.class, "n1"), sortedSetOf(UUID.randomUUID(),UUID.randomUUID()))
      .build()
    ;
    final TestModel adder = registry.beanType(TestModel.class).createBuilder()
      .set(TestModel::number1).to(1)
      .set(TestModel::number2).to(2L)
      .set(TestModel::ids).to(map)
      .build()
    ;
    assertThat(adder.sum(), is(4L));

    assertThat(adder.ids(), is(map));
    final BeanTypeSupport<TestModel> beanType = registry.beanType(TestModel.class);
    assertThat(beanType, isA(CglibBeanType.class));
    assertThat(beanType.properties(), mapWithSize(3));
  }

  @Test
  public void testPolymorphicModel() {
    final BeanTypeSupport<Elephant> beanType = registry.beanType(Elephant.class);
    beanType.isAbstract();
    final Elephant elephant = registry.beanType(Elephant.class).createBuilder()
      .build()
    ;
    assertThat(elephant.count(), is(0));

  }

}
