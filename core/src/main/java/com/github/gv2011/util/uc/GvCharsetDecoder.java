package com.github.gv2011.util.uc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import com.github.gv2011.util.ann.Nullable;

public class GvCharsetDecoder extends CharsetDecoder{


  private static final int[] TABLE = new int[]{};

  GvCharsetDecoder(final Gv1Charset cs) {
    super(cs, 1, 2);
  }

  @Override
  protected CoderResult decodeLoop(final ByteBuffer in, final CharBuffer out) {
    @Nullable CoderResult result = null;
    final char[] buffer = new char[2];
    while(result == null){
      if(!in.hasRemaining()) result = CoderResult.UNDERFLOW;
      else{
        if(out.remaining()<2) result = CoderResult.OVERFLOW;
        else{
          final byte b = in.get();
          if(isEscape(b)) result = handleEscape(b, in, buffer, out);
          else{
            final int codepoint = toCodepoint(b);
            writeCodepoint(out, buffer, codepoint);
          }
        }
        if(result==null) in.get();
      }
    }
    return result;
  }

  private void writeCodepoint(final CharBuffer out, final char[] buffer, final int codepoint) {
    final int length = Character.toChars(codepoint, buffer, 0);
    out.put(buffer, 0, length);
  }

  private int toCodepoint(final byte b) {
    if((((int)b) & 0x80) == 0) return Byte.toUnsignedInt(b);
    else return TABLE[((int)b) & 0x7F];
  }

  private @Nullable CoderResult handleEscape(final byte b, final ByteBuffer in, final char[] buffer, final CharBuffer out) {
    int codepoint = -1;
    CoderResult result = null;
    if(b==Gv1Charset.ESCAPE_1_BYTE){
      if(in.remaining()<1) result = CoderResult.UNDERFLOW;
      else codepoint = Byte.toUnsignedInt(in.get());
    }
    else if(b==Gv1Charset.ESCAPE_2_BYTES){
      if(in.remaining()<2) result = CoderResult.UNDERFLOW;
      else codepoint = (Byte.toUnsignedInt(in.get()) << 8) + Byte.toUnsignedInt(in.get());
    }
    else{
      assert b==Gv1Charset.ESCAPE_3_BYTES;
      if(in.remaining()<3) return CoderResult.UNDERFLOW;
      else codepoint = (Byte.toUnsignedInt(in.get()) << 16) + (Byte.toUnsignedInt(in.get()) << 8) + Byte.toUnsignedInt(in.get());
    }
    if(result==null) writeCodepoint(out, buffer, codepoint);
    return result;
  }

  private boolean isEscape(final byte b) {
    return b==Gv1Charset.ESCAPE_1_BYTE || b==Gv1Charset.ESCAPE_2_BYTES || b==Gv1Charset.ESCAPE_3_BYTES;
  }

}
