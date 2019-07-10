package com.github.gv2011.util.json;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;

import org.junit.Test;

import com.github.gv2011.util.json.imp.JsonFactoryImp;

public class JsonUtilsTest {

  @Test
  public void testJsonFactory() {
    assertThat(
      JsonUtils.jsonFactory().getClass().getName(),
      is(JsonFactoryImp.class.getName())
    );
  }

}
