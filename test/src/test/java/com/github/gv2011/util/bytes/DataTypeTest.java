package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.internal.DataTypeImp;
import com.github.gv2011.util.json.JsonUtils;

public class DataTypeTest {

  @Test
  public void testParamterNamesAreAvailable(){
    Arrays.stream(DataTypeImp.class.getConstructors())
    .forEach(c->{
      Arrays.stream(c.getParameters()).forEach(
        p->assertTrue(format("Parameter {} of constructor {} has no name.", p, c), p.isNamePresent())
      );
    });
  }


  @Test
  public void testParse(){
    final String encoded =
      "multipart/related; boundary=example-2; start=\"<950118.AEBH@XIson.com>\"; type=\"Text/x-Okie\""
    ;
    final DataType dataType = DataType.parse(encoded);
    assertThat(dataType.getClass(), is(DataTypeImp.class));

    assertThat(dataType.primaryType(), is("multipart"));
    assertThat(dataType.subType(),     is("related"));
    assertThat(dataType.baseType(),    is(DataType.parse("multipart/related")));

    assertThat(dataType.parameters(),  is(
      ICollections.mapBuilder()
      .put("boundary", "example-2")
      .put("start",    "<950118.AEBH@XIson.com>")
      .put("type",     "Text/x-Okie")
      .build()
    ));

    assertThat(dataType.toString(),    is(encoded));
    assertThat(
      BeanUtils.typeRegistry().beanType(DataType.class).toJson(dataType),
      is(JsonUtils.jsonFactory().primitive(encoded))
    );
  }

  @Test(expected=IllegalStateException.class)
  public void testValidation1() {
    BeanUtils.beanBuilder(DataType.class)
      .set(DataType::primaryType).to("multipart")
      .set(DataType::subType).to("@")
      .build()
    ;
  }

  @Test
  public void testValidation2() {
    assertThat(create().getClass(), is(DataTypeImp.class));
  }

  @Test
  public void testEquals() {
    assertThat(create(), is(create()));
  }

  @Test
  public void testHashCode() {
    assertThat(create().hashCode(), is(create().hashCode()));
  }


  private DataType create() {
    return BeanUtils.beanBuilder(DataType.class)
      .set(DataType::primaryType).to("multipart")
      .set(DataType::subType).to("related")
      .build();
  }


}
