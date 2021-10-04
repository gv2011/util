package com.github.gv2011.util.examples;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

class DecoderExample {

  private static final String MATH_E = "𝔼"; //MATHEMATICAL DOUBLE-STRUCK CAPITAL E
  @SuppressWarnings("unused")
  private static final String HAN_20010 = "𠀐";
  @SuppressWarnings("unused")
  private static final String FACE_WITH_TEARS_OF_JOY = "😂";

  private CharBuffer out;
  private final List<String> strings = new ArrayList<>();

  @Test
  void testChars() {
    assertThat(MATH_E.length(), is(2));
    assertThat(MATH_E.codePoints().mapToObj(i->i).collect(toIList()), is(listOf(0x1D53C)));

    final char char0 = MATH_E.charAt(0);
    assertThat((int) char0, is(0xD835));
    assertTrue(Character.isHighSurrogate(char0));

    final char char1 = MATH_E.charAt(1);
    assertThat((int) char1, is(0xDD3C));
    assertTrue(Character.isLowSurrogate(char1));

    final Bytes utf8 = ByteUtils.asUtf8(MATH_E).content();
    assertThat(utf8, is(ByteUtils.parseHex("F0 9D 94 BC")));
  }

  @Test
  void test() {
    final Charset charset = UTF_8;
    final CharsetDecoder decoderx = charset.newDecoder()
      .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
    ;
    final ByteBuffer overlap = ByteBuffer.allocate(4);

    final byte[] inArray1 = new byte[]{'A', 'B', (byte)0xF0, (byte)0x9D};
    final byte[] inArray2 = new byte[]{(byte)0x94, (byte)0xBC, 'Z'};
    final ByteBuffer in = ByteBuffer.wrap(inArray1).asReadOnlyBuffer();
    final ByteBuffer in2 = ByteBuffer.wrap(inArray2).asReadOnlyBuffer();

    final char[] outArray = new char[3];
    out = CharBuffer.wrap(outArray);
    boolean atEnd = false;

    CoderResult r = decode(decoderx, in, atEnd);
    assertTrue(r.isUnderflow());

    assertThat(in.remaining(), is(2));
    assertThat(overlap.remaining(), is(4));
    while(in.hasRemaining()){
      overlap.put(in.get());
    }
    assertThat(overlap.remaining(), is(2));
    overlap.put(in2.get());
    assertThat(overlap.remaining(), is(1));
    overlap.flip();
    assertThat(overlap.remaining(), is(3));

    r = decode(decoderx, overlap, atEnd);
    assertThat(overlap.remaining(), is(3));
    assertTrue(r.isUnderflow());
    assertThat(overlap.position(), is(0));
    assertThat(overlap.limit(), is(3));

    while(overlap.hasRemaining()){
      assertThat(overlap.position(), is(0));
      assertTrue(overlap.limit()<=3);
      overlap.position(overlap.limit());
      overlap.limit(overlap.limit()+1);
      overlap.put(in2.get());
      overlap.flip();
      r = decode(decoderx, overlap, atEnd);
    }
    overlap.clear();

    atEnd = true;
    r = decode(decoderx, in2, atEnd);
    assertTrue(r.isUnderflow());
    assertThat(strings.size(), is(2));
    assertThat(strings.get(0), is("AB"));
    assertThat(strings.get(0).length(), is(2));
    assertThat(strings.get(1), is("𝔼Z"));
    assertThat(strings.get(1).length(), is(3));
  }

  private CoderResult decode(final CharsetDecoder decoder, final ByteBuffer in, final boolean atEnd) {
    boolean done = false;
    CoderResult r = null;
    while(!done){
      r = decoder.decode(in, out, atEnd);
      assertFalse(r.isError());
      if(r.isOverflow()){
        out.flip();
        drop(out.toString());
        out.clear();
      }
      else done=true;
    }
    if(atEnd && out.position()>0){
        out.flip();
        drop(out.toString());
        out.clear();
    }
    return notNull(r);
  }

  private void drop(final String string) {
    strings.add(string);
  }

}
