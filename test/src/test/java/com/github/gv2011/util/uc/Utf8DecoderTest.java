package com.github.gv2011.util.uc;

import static com.github.gv2011.util.ResourceUtils.getResourceUrl;
import static com.github.gv2011.util.StreamUtils.asIterator;
import static com.github.gv2011.util.ex.Exceptions.call;
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
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.Test;

import com.github.gv2011.util.ResourceUtils;

public class Utf8DecoderTest {

  static final String TEST_FILE = "UTF-8-test-file.txt";

  @Test
  public void testDecodeInputStream() {
    final String expected = ResourceUtils.getTextResource(Utf8DecoderTest.class, TEST_FILE);
    final IntStream intStream =
      new Utf8Decoder().decode(
        asIterator(
          call(()->getResourceUrl(Utf8DecoderTest.class, TEST_FILE).openStream())
        )
      )
    ;
    final AtomicInteger index = new AtomicInteger();
    intStream.forEach(cp->{
      final String ch = new StringBuilder().appendCodePoint(cp).toString();
      final String expectedCh = expected.substring(index.get(), index.get()+ch.length());
      if(!ch.equals(expectedCh)) {
        fail(format("Codepoint: {}, index: {}, exp.:{}, actual: {}", cp, index.get(), expectedCh, ch));
      }
      index.addAndGet(ch.length());
    });
  }
}
