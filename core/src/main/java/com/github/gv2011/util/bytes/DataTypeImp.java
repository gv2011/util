package com.github.gv2011.util.bytes;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import static com.github.gv2011.util.ex.Exceptions.call;

import javax.activation.MimeType;

public class DataTypeImp implements DataType{

  public static final DataType APPLICATION_OCTET_STREAM = new DataTypeImp("application/octet-stream");
  public static final DataType TEXT = new DataTypeImp("text/plain;charset=UTF-8");

  private final MimeType mimeType;

  public static final DataType parse(final String mimeType) {
    return new DataTypeImp(mimeType);
  }

  private DataTypeImp(final String mimeType) {
    this(call(()->new MimeType(mimeType)));
  }

  private DataTypeImp(final MimeType mimeType) {
    this.mimeType = mimeType;
  }

  @Override
  public final MimeType mimeType() {
    return mimeType;
  }

  protected String extensionId() {
    return "";
  }

  @Override
  public int hashCode() {
    return mimeType().hashCode() * 31 + extensionId().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(obj instanceof DataType) {
      if(obj instanceof DataTypeImp) {
        final DataTypeImp dt = (DataTypeImp)obj;
        return dt.mimeType().equals(mimeType) && dt.extensionId().equals(extensionId());
      }
      else return obj.equals(this);
    }
    else return false;
  }

  @Override
  public String toString() {
    return mimeType.toString();
  }



}
