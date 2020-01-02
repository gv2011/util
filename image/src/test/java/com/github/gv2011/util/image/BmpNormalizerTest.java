package com.github.gv2011.util.image;

import org.junit.Test;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.BytesBuilder;

public class BmpNormalizerTest {

  @Test
  public void testNormalize() {
    getClass().getResourceAsStream("/test-image.jpg");
    try(BytesBuilder bytesBuilder = ByteUtils.newBytesBuilder()){
      new BmpNormalizer().normalize(
        ()->getClass().getResourceAsStream("/test-image.jpg"),
        CommonImageType.JPEG,
        0.05*1.333, 0.05, bytesBuilder
      );
      //final Bytes bmp = bytesBuilder.build();
      //bmp.write(Paths.get("out.bmp"));
    }
  }

}
