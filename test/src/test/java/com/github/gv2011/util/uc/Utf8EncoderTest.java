package com.github.gv2011.util.uc;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.testutils.Matchers.is;
import static com.github.gv2011.util.ResourceUtils.getBinaryResource;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static org.junit.Assert.assertThat;

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
