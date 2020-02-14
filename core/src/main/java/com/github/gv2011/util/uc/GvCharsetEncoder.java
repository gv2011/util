package com.github.gv2011.util.uc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import com.github.gv2011.util.ann.Nullable;

final class GvCharsetEncoder extends CharsetEncoder{

  GvCharsetEncoder(final Gv1Charset cs) {
    super(cs, 1, 4);
  }

  @Override
  protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out) {
    @Nullable CoderResult result = null;
    while(result==null){
      if(!in.hasRemaining()) result = CoderResult.UNDERFLOW;
      else{
        final char ch = in.get();
        int codePoint = -1;
        if(Character.isSurrogate(ch)){
          if(!Character.isHighSurrogate(ch)) result = CoderResult.malformedForLength(1);
          else{
            if(!in.hasRemaining()) result = CoderResult.UNDERFLOW;
            else{
              final char low = in.get();
              if(!Character.isSurrogatePair(ch, low)) result = CoderResult.malformedForLength(2);
              else codePoint = Character.toCodePoint(ch, low);
            }
          }
        }
        else{
           codePoint = ch;
        }
        if(result==null) result = write(codePoint, out);
      }
    }
    return result ;
  }

  private @Nullable CoderResult write(final int codePoint, final ByteBuffer out) {
    @Nullable CoderResult result = null;
    final int mapped = tryMapToByte(codePoint);
    if(mapped==-1) result = writeEscape(codePoint, out);
    else{
      if(!out.hasRemaining()) result = CoderResult.OVERFLOW;
      else out.put((byte) mapped);
    }
    return result;
  }

  private @Nullable CoderResult writeEscape(final int codepoint, final ByteBuffer out) {
    @Nullable CoderResult result = null;
    final byte high = (byte) (codepoint>>16);
    final byte middle = (byte) (codepoint>>8);
    final byte low = (byte) codepoint;
    if(high==0){
      if(out.remaining()<3) result = CoderResult.OVERFLOW;
      else{
        out.put(Gv1Charset.ESCAPE_2_BYTES);
        out.put(middle);
        out.put(low);
      }
    }
    else{
      if(out.remaining()<4) result = CoderResult.OVERFLOW;
      else{
        out.put(Gv1Charset.ESCAPE_3_BYTES);
        out.put(high);
        out.put(middle);
        out.put(low);
      }
    }
    return result;
  }

  private int tryMapToByte(final int codePoint) {
    // TODO Auto-generated method stub
    return 0;
  }

}
