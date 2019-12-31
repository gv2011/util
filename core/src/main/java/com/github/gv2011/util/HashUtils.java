package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import com.github.gv2011.util.bytes.AbstractTypedBytes;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.ex.ThrowingSupplier;

public final class HashUtils {

  private HashUtils(){staticClass();}

  public static TypedBytes hash(final HashAlgorithm algorithm, final ThrowingSupplier<InputStream> input){
    return callWithCloseable(input, in->{
      final MessageDigest md = algorithm.createMessageDigest();
      final DigestInputStream dis = new DigestInputStream(in, md);
      StreamUtils.count(dis);
      final Bytes hash = ByteUtils.newBytes(md.digest());
      return new AbstractTypedBytes(){
        @Override
        public Bytes content() {return hash;}
        @Override
        public DataType dataType() {return algorithm.getDataType();}
      };
    });
  }

}
