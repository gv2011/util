package com.github.gv2011.http.imp;

import static com.github.gv2011.util.StreamUtils.asStream;
import static com.github.gv2011.util.StreamUtils.countAndClose;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.json.JsonNode;

final class JsonEntity extends AbstractHttpEntity implements AutoCloseableNt{

  private static final ContentType JSON = ContentType.create(DataTypes.APPLICATION_JSON.toString());

  private final JsonNode json;

  JsonEntity(final JsonNode json) {
    super(JSON, null);
    this.json = json;
  }

  @Override
  public InputStream getContent(){
    return asStream(new StringReader(json.serialize()));
  }

  @Override
  public boolean isRepeatable() {
    return true;
  }

  @Override
  public boolean isStreaming() {
    return false;
  }

  @Override
  public long getContentLength() {
    return countAndClose(this::getContent);
  }

  @Override
  public void close(){}

}
