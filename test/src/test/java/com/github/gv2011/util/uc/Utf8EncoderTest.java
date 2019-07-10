package com.github.gv2011.util.uc;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.ResourceUtils.getBinaryResource;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.Test;

import com.github.gv2011.util.ResourceUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

public class Utf8EncoderTest {

  private static final Bytes EXPECTED = getBinaryResource(Utf8DecoderTest.class, Utf8DecoderTest.TEST_FILE);
  private static final String TEXT = ResourceUtils.getTextResource(Utf8DecoderTest.class, Utf8DecoderTest.TEST_FILE);

  @Test
  public void testText() {
    verifyEqual(ByteUtils.asUtf8(TEXT).content(), EXPECTED);
  }

  @Test
  public void testEncodeAll() {
    final Bytes actual = ByteUtils.collectBytes(
      new Utf8Encoder().encode(TEXT.codePoints())
      .map(i->{
        verify(i>=0 && i<256);
        return i;
      })
    );
    assertThat(actual, is(EXPECTED));
  }

  @Test
  public void testEncodeSingle() {
    final Utf8Encoder encoder = new Utf8Encoder();

    final AtomicInteger index = new AtomicInteger();
    TEXT.codePoints().forEach(cp->{
      final Bytes encoded = ByteUtils.collectBytes(encoder.encode(IntStream.of(cp)));
      final Bytes expectedChunk = EXPECTED.subList(index.get(), index.get()+encoded.size());
      assertThat("i:"+index.get()+ " "+cp+" '"+ new String(Character.toChars(cp))+"'", encoded, is(expectedChunk));
      index.addAndGet(encoded.size());
    });
  }

}
