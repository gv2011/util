package com.github.gv2011.util.bytes;

public final class DefaultTypedBytes extends AbstractTypedBytes{

  private final Bytes content;
  private final DataType dataType;

  public DefaultTypedBytes(final Bytes content, final DataType dataType) {
    this.content = content;
    this.dataType = dataType;
  }

  @Override
  public DataType dataType() {
    return dataType;
  }

  @Override
  public Bytes content() {
    return content;
  }

}
