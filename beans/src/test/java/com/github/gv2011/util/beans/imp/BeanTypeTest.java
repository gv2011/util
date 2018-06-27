package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.BeanUtils.beanBuilder;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.mapBuilder;
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;

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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;

import org.junit.Test;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.beans.imp.TestModel.BlackPea;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;

public class BeanTypeTest {

  public static interface TestBeanA{
    String stringProp();
    @DefaultValue("5") Integer intProp();
    @DefaultValue("localhost:443") Opt<InetSocketAddress> socket();
  }

  public static interface Colour extends TypedString<Colour>{}


  public static interface TestBeanB{
    TestBeanA beanA();
    IList<TestBeanA> beans();
    Colour colour();
    ISortedMap<Colour,ISortedSet<String>> colourInfo();
  }

    @Test
    public void testStringProperty() {
      final BeanType<TestBeanA> beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class);
      final Property<?> stringProp = beanTypeA.properties().get("stringProp");
      assertThat(stringProp.name(), is("stringProp"));
      assertThat(stringProp.type().name(), is(String.class.getName()));
      assertThat(stringProp.defaultValue(), is(Opt.of("")));
    }

    @Test
    public void testHashCode() {
      @SuppressWarnings("unused")
      final TestBeanA beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class).createBuilder()
        .set(TestBeanA::stringProp).to("value1")
        .build()
      ;
      @SuppressWarnings("unused")
      final IMap<Object, Object> map = mapBuilder()
        .put("stringProp", "value1")
        .put("intProp", "value1")
        .put("socket", "value1")
        .build()
      ;
    }

    @Test
    public void testIntProperty() {
      final BeanType<TestBeanA> beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class);
      final Property<?> prop = beanTypeA.properties().get("intProp");
      assertThat(prop.name(), is("intProp"));
      assertThat(prop.type().name(), is(Integer.class.getName()));
      assertThat(prop.defaultValue(), is(Opt.of(5)));
    }

    @Test
    public void testSocketProperty() {
      final BeanType<TestBeanA> beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class);
      final Property<?> prop = beanTypeA.properties().get("socket");
      assertThat(prop.name(), is("socket"));
      assertThat(prop.type().name(), is("com.github.gv2011.util.icol.Opt<java.net.InetSocketAddress>"));
      assertThat(
        prop.defaultValue(),
        is(Opt.of(Opt.of(InetSocketAddress.createUnresolved("localhost", 443))))
      );
    }

    @Test
    public void testName() {
      final BeanType<TestBeanA> beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class);
      assertThat(beanTypeA.name(), is(TestBeanA.class.getName()));
    }

    @Test
    public void testBuildWithDefaults() {
      final BeanType<TestBeanA> beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class);
      final TestBeanA bean = beanTypeA.createBuilder().build();
      assertThat(bean.stringProp(), is(""));
      assertThat(bean.intProp(), is(5));
      assertThat(bean.socket(), is(Opt.of(InetSocketAddress.createUnresolved("localhost", 443))));
    }

    @Test
    public void testBuilder() {
      final BeanType<TestBeanA> beanTypeA = new DefaultTypeRegistry().beanType(TestBeanA.class);
      final BeanBuilder<TestBeanA> builder = beanTypeA.createBuilder();
      builder.set(TestBeanA::stringProp).to("lala");
      final InetSocketAddress socket = InetSocketAddress.createUnresolved("lila", 2);
      builder.setOpt(TestBeanA::socket).to(socket);
      final TestBeanA bean = builder.build();
      assertThat(bean.stringProp(), is("lala"));
      assertThat(bean.socket(), is(Opt.of(socket)));
    }

    @Test
    public void testNested() {
      final DefaultTypeRegistry reg = new DefaultTypeRegistry();
      final BeanBuilder<TestBeanB> b = reg.beanType(TestBeanB.class).createBuilder();
      final TestBeanB bean = b.build();
      assertThat(bean.beanA(), is(reg.beanType(TestBeanA.class).createBuilder().build()));
      assertThat(bean.beans(), is(emptyList()));
    }

    @Test
    public void testMap() {
      final ISortedMap<Colour,ISortedSet<String>> colourInfo =
        ICollections.<Colour,ISortedSet<String>>sortedMapBuilder()
        .put(TypedString.create(Colour.class, "red"), sortedSetOf("a","b"))
        .build()
      ;
      final DefaultTypeRegistry reg = new DefaultTypeRegistry();
      final TestBeanB b = reg.beanType(TestBeanB.class).createBuilder()
        .set(TestBeanB::colourInfo).to(colourInfo)
        .build()
      ;
      assertThat(b.colourInfo(), is(colourInfo));
      assertThat(
        reg.beanType(TestBeanB.class).toJson(b).serialize(),
        is(   "{\n"
            + "  \"beanA\": {\n"
            + "    \"intProp\": 5,\n"
            + "    \"socket\": \"localhost:443\"\n"
            + "  },\n"
            + "  \"colourInfo\": {\n"
            + "    \"red\": [\n"
            + "      \"a\",\n"
            + "      \"b\"\n"
            + "    ]\n"
            + "  }\n"
            + "}"
        )
      );
    }

    @Test
    public void testTimeSpan(){
      @SuppressWarnings("unused")
      final BlackPea pea = beanBuilder(BlackPea.class).build();
    }
}
