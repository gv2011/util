package com.github.gv2011.util.bytes;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BytesBuilderTest {

  @Test
  public void testBuild() {
    final Bytes expected = ByteUtils.newRandomBytes(100000);
    CloseableBytes bytes = null;
    try{
      try(BytesBuilder builder = new BytesBuilder()){
        bytes = builder.append(expected).build();
      }
      assertThat(bytes, is(expected));
      assertThat(bytes.hash(), is(expected.hash()));
      assertThat(bytes.hashCode(), is(expected.hashCode()));
    }finally{
      if(bytes!=null)bytes.close();
    }
  }

}
