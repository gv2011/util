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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;
import java.util.Optional;

import org.junit.Test;

import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.Property;

public class BeanTypeTest {

    public static interface TestBean{
        String stringProp();
        @DefaultValue("5")
        Integer intProp();
        @DefaultValue("[\"localhost:443\"]")
        Optional<InetSocketAddress> socket();
    }

    private final BeanType<TestBean> beanType = new DefaultTypeRegistry().beanType(TestBean.class);

    @Test
    public void testStringProperty() {
        final Property<?> stringProp = beanType.properties().get("stringProp");
        assertThat(stringProp.name(), is("stringProp"));
        assertThat(stringProp.type().name(), is(String.class.getName()));
        assertThat(stringProp.defaultValue(), is(Optional.of("")));
    }

    @Test
    public void testIntProperty() {
        final Property<?> prop = beanType.properties().get("intProp");
        assertThat(prop.name(), is("intProp"));
        assertThat(prop.type().name(), is(Integer.class.getName()));
        assertThat(prop.defaultValue(), is(Optional.of(5)));
    }

    @Test
    public void testSocketProperty() {
        final Property<?> prop = beanType.properties().get("socket");
        assertThat(prop.name(), is("socket"));
        assertThat(prop.type().name(), is("java.util.Optional<java.net.InetSocketAddress>"));
        assertThat(
          prop.defaultValue(),
          is(Optional.of(Optional.of(InetSocketAddress.createUnresolved("localhost", 443))))
        );
    }

    @Test
    public void testName() {
        assertThat(beanType.name(), is(TestBean.class.getName()));
    }

    @Test
    public void testBuilder() {
        final TestBean bean = beanType.createBuilder().build();
        assertThat(bean.stringProp(), is(""));
        assertThat(bean.intProp(), is(5));
        assertThat(bean.socket(), is(Optional.of(InetSocketAddress.createUnresolved("localhost", 443))));
    }

}
