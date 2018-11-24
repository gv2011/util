package com.github.gv2011.util.uc;

import static com.github.gv2011.util.ex.Exceptions.format;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.Test;

import com.github.gv2011.util.ResourceUtils;

public class Utf8DecoderTest {

  private static final String TEST_FILE = "UTF-8-test-file.txt";

  @Test
  public void testDecodeInputStream() {
    final String expected = ResourceUtils.getTextResource(Utf8DecoderTest.class, TEST_FILE);
    final IntStream intStream =
      new Utf8Decoder().decode(ResourceUtils.getResourceUrl(Utf8DecoderTest.class, TEST_FILE)::openStream)
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
