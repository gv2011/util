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

import java.util.Map.Entry;

import javax.activation.MimeType;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.bytes.DataTypeImp.DataTypeParser;
import com.github.gv2011.util.bytes.DataTypeImp.DataTypeValidator;
import com.github.gv2011.util.icol.ISortedMap;

/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined in RFC 2045
 * and 2046.
 */
@Bean(implementation=DataTypeImp.class, parser=DataTypeParser.class, validator=DataTypeValidator.class)
public interface DataType{

  String primaryType();

  String subType();

  ISortedMap<String,String> parameters();

  @Computed
  MimeType mimeType();

  /**
   * @return a String representation without the parameter list
   */
  @Computed
  String baseType();

  public static MimeType mimeType(final DataType dataType){
    final MimeType result = call(()->new MimeType(dataType.primaryType(), dataType.subType()));
    for(final Entry<String, String> e: dataType.parameters().entrySet()) {
      result.setParameter(e.getKey(), e.getValue());
    }
    return result;
  }

  public static String getBaseType(final DataType dataType) {
    return dataType.primaryType() + "/" + dataType.subType();
  }



}
