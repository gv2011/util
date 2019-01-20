package com.github.gv2011.util.bytes;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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

import org.junit.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.json.JsonUtils;

public class DataTypeTest {

  @Test
  public void test() {
    final String encoded =
      "multipart/related; boundary=example-2; start=\"<950118.AEBH@XIson.com>\"; type=\"Text/x-Okie\""
    ;
    final DataType type = DataType.parse(encoded);
    assertThat(type.getClass(), is(DataTypeImp.class));

    assertThat(type.primaryType(), is("multipart"));
    assertThat(type.subType(),     is("related"));
    assertThat(type.baseType(),    is(DataType.parse("multipart/related")));

    assertThat(type.parameters(),  is(
      ICollections.mapBuilder()
      .put("boundary", "example-2")
      .put("start",    "<950118.AEBH@XIson.com>")
      .put("type",     "Text/x-Okie")
      .build()
    ));

    assertThat(type.toString(),    is(encoded));
    assertThat(
      BeanUtils.typeRegistry().beanType(DataType.class).toJson(type),
      is(JsonUtils.jsonFactory().primitive(encoded))
    );
  }

  @Test(expected=IllegalStateException.class)
  public void testValidation() {
    BeanUtils.beanBuilder(DataType.class)
      .set(DataType::primaryType).to("multipart")
      .set(DataType::subType).to("@")
      .build()
    ;
  }

  @Test//(expected=IllegalStateException.class)
  public void testValidation2() {
    BeanUtils.beanBuilder(DataType.class)
      .set(DataType::primaryType).to("multipart")
      .set(DataType::subType).to("related")
      .build()
    ;
  }


}
