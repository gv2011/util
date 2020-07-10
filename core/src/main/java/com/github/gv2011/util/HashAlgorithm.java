package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.security.MessageDigest;

import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.icol.Opt;

/**
 * See <a
 * href="https://docs.oracle.com/en/java/javase/12/docs/specs/security/standard-names.html#messagedigest-algorithms"
 * >Java Security Standard Algorithm Names - MessageDigest Algorithms</a>
 *
 */
public enum HashAlgorithm {

  MD2("MD2"),
  MD5("MD5"),
  SHA_1("SHA-1"),
  SHA_224("SHA-224"),
  SHA_256("SHA-256"),
  SHA_384("SHA-384"),
  SHA_512_224("SHA-512/224"),
  SHA_512_256("SHA-512/256"),
  SHA3_224("SHA3-224"),
  SHA3_256("SHA3-256"),
  SHA3_384("SHA3-384"),
  SHA3_512("SHA3-512");

  private final String algorithmName;
  private final Constant<DataType> dataType;

  private HashAlgorithm(final String algorithmName){
    this.algorithmName = algorithmName;
    dataType = Constants.cachedConstant(()->
      DataType.parse(DataTypes.APPLICATION+"/x-"+StringUtils.toLowerCase(algorithmName.replace('/','-')))
    );
  }


  public String getName() {
    return algorithmName;
  }

  public DataType getDataType() {
    return dataType.get();
  }

  @Override
  public String toString() {
    return getName();
  }

  public MessageDigest createMessageDigest(){
    return call(()->MessageDigest.getInstance(algorithmName));
  }

  public static Opt<HashAlgorithm> forDataType(final DataType dataType){
    return XStream.ofArray(HashAlgorithm.values())
      .filter(n->n.dataType.get().equals(dataType))
      .tryFindAny()
    ;
  }

}
