package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;

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
