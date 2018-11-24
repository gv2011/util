package com.github.gv2011.util.uc;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.wrap;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

import com.github.gv2011.util.ex.ThrowingSupplier;

final class Utf8Decoder {

  private static final int MASK_PREFIX_ONE =      Integer.parseInt("10000000", 2);
  private static final int PREFIX_ONE =           Integer.parseInt("00000000", 2);

  private static final int MASK_PREFIX_MULTIPLE = Integer.parseInt("11000000", 2);
  private static final int PREFIX_MULTIPLE =      Integer.parseInt("11000000", 2);

  private static final int MASK_PREFIX_TWO =      Integer.parseInt("11100000", 2);
  private static final int PREFIX_TWO =           Integer.parseInt("11000000", 2);

  private static final int MASK_PREFIX_THREE =    Integer.parseInt("11110000", 2);
  private static final int PREFIX_THREE =         Integer.parseInt("11100000", 2);

  private static final int MASK_PREFIX_FOUR =     Integer.parseInt("11111000", 2);
  private static final int PREFIX_FOUR =          Integer.parseInt("11110000", 2);

  private static final int MASK_DATA_TWO =        Integer.parseInt("00011111", 2);
  private static final int MASK_DATA_THREE =      Integer.parseInt("00001111", 2);
  private static final int MASK_DATA_FOUR =       Integer.parseInt("00000111", 2);

  private static final int MASK_PREFIX_FOLLOW =   Integer.parseInt("11000000", 2);
  private static final int PREFIX_FOLLOW =        Integer.parseInt("10000000", 2);
  private static final int MASK_DATA_FOLLOW =     Integer.parseInt("00111111", 2);


  IntStream decode(final ThrowingSupplier<InputStream> utf8){
    boolean success = false;
    final InputStream in = call(utf8::get);
    try {
      final IntStream intStream = decode(()->{
        try {return in.read();}
        catch (final IOException e) {throw wrap(e);}
      });
      intStream.onClose(()->call(in::close));
      success = true;
      return intStream;
    } finally {
      if(!success) call(in::close);
    }
  }

  IntStream decode(final IntSupplier utf8){
    return IntStream.iterate(decodeNext(utf8), i->i!=-1, (i)->decodeNext(utf8));
  }

  private int decodeNext(final IntSupplier utf8){
    final int result;
    final int b0 = readNext(utf8);
    if(b0==-1) result = b0;
    else {
      if((b0 & MASK_PREFIX_ONE)==PREFIX_ONE) result = b0;
      else{// >=2 bytes
        verify((b0 & MASK_PREFIX_MULTIPLE)==PREFIX_MULTIPLE);
        final int b1 = readFollowing(utf8);
        if((b0 & MASK_PREFIX_TWO)==PREFIX_TWO) {
          result = ((b0 & MASK_DATA_TWO)<<6) | (b1 & MASK_DATA_FOLLOW);
        }
        else {
          final int b2 = readFollowing(utf8);
          if((b0 & MASK_PREFIX_THREE)==PREFIX_THREE) {
            result = ((b0 & MASK_DATA_THREE)<<12) | ((b1 & MASK_DATA_FOLLOW)<<6) | (b2 & MASK_DATA_FOLLOW);
          }
          else {
            verify((b0 & MASK_PREFIX_FOUR)==PREFIX_FOUR);
            final int b3 = readFollowing(utf8);
            result =
              ((b0 & MASK_DATA_FOUR  )<<18) |
              ((b1 & MASK_DATA_FOLLOW)<<12) |
              ((b2 & MASK_DATA_FOLLOW)<<6)  |
              (b3 & MASK_DATA_FOLLOW)
            ;
          }
        }
      }
    }
    return result;
  }

  private int readNext(final IntSupplier s){
    final int b = s.getAsInt();
    return b == -1 ? b : Byte.toUnsignedInt((byte) b);
  }

  private int readFollowing(final IntSupplier s){
    final int b = s.getAsInt();
    verify(b!=-1);
    final int result = Byte.toUnsignedInt((byte) b);
    verify((b & MASK_PREFIX_FOLLOW) == PREFIX_FOLLOW);
    return result;
  }

}
