package com.github.gv2011.util.bytes;

import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.beans.Elementary;

/**
 * SHA-256
 */
public interface Hash256 extends TypedBytes, Elementary{

  public static final HashAlgorithm ALGORITHM = HashAlgorithm.SHA_256;
  public static final int SIZE = 32;

  public static Hash256 parse(final CharSequence hex) {
    return new Hash256Imp(ByteUtils.hexToByteArray(hex));
  }

  @Override
  default DataType dataType() {
    return ALGORITHM.getDataType();
  }

}
