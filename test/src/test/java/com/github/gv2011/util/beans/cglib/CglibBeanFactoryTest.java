package com.github.gv2011.util.beans.cglib;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.testutil.Matchers.isA;
import static com.github.gv2011.testutil.Matchers.mapWithSize;
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;

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
