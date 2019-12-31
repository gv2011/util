package com.github.gv2011.util.bytes;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.bytes.DataType.CHARSET_PARAMETER_NAME;
import static com.github.gv2011.util.bytes.DataType.parse;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public final class DataTypes {

  private DataTypes(){staticClass();}

  public static final String APPLICATION = "application";
  public static final String OCTET_STREAM = "octet-stream";
  public static final DataType APPLICATION_OCTET_STREAM = parse(APPLICATION+"/"+OCTET_STREAM);

  public static final String TEXT = "text";
  public static final String PLAIN = "plain";
  public static final DataType TEXT_PLAIN = parse(TEXT+"/"+PLAIN);
  public static final DataType TEXT_PLAIN_UTF_8 = parse(TEXT_PLAIN+";"+CHARSET_PARAMETER_NAME+"="+UTF_8.name());

  public ISet<DataType> getAllKnownDataTypes() {
    return
      RecursiveServiceLoader.services(DataTypeProvider.class).stream()
      .flatMap(p->p.knownDataTypes().stream())
      .collect(toISet())
    ;
  }

}
