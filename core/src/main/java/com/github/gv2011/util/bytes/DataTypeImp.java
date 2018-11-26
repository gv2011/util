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
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.activation.MimeType;
import javax.activation.MimeTypeParameterList;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Parser;
import com.github.gv2011.util.beans.Validator;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISortedMap;

public final class DataTypeImp implements DataType {

  public static final DataType APPLICATION_OCTET_STREAM = BeanUtils.parse(DataType.class, "application/octet-stream");
  public static final DataType SHA_256 = BeanUtils.parse(DataType.class, "application/x-sha-256");
  public static final DataType TEXT = BeanUtils.parse(DataType.class, "text/plain;charset=UTF-8");

  private final DataType core;

  public DataTypeImp(final DataType core) {
    this.core = core;
  }

  @Override
  public String primaryType() {
    return core.primaryType();
  }

  @Override
  public String subType() {
    return core.subType();
  }

  @Override
  public ISortedMap<String, String> parameters() {
    return core.parameters();
  }

  @Override
  @Computed
  public MimeType mimeType() {
    final MimeType mimeType = call(()->new MimeType(primaryType(), subType()));
    for(final Entry<String, String> e: parameters().entrySet()) {
      mimeType.setParameter(e.getKey(), e.getValue());
    }
    return mimeType;
  }

  @Override
  @Computed
  public String baseType() {
    return primaryType() + "/" + subType();
  }

  @Override
  public int hashCode() {
    return core.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return core.equals(obj);
  }

  @Override
  public String toString() {
    return baseType() + parametersToString();
  }

  private String parametersToString() {
    return
      parameters().entrySet().stream()
      .map(e -> "; " + e.getKey() + "=" + quote(e.getValue()))
      .collect(Collectors.joining())
    ;
  }

  private static String quote(final String value) {
    final MimeTypeParameterList mtpl = new MimeTypeParameterList();
    mtpl.set("k", value);
    return StringUtils.removePrefix(mtpl.toString(),"; k=");
  }

  private static DataType parse(final String encoded, final ExtendedBeanBuilder<DataType> builder) {
    final MimeType mimeType = call(()->new MimeType(encoded));
    final ISortedMap.Builder<String, String> parameters = ICollections.sortedMapBuilder();
    final Enumeration<?> names = mimeType.getParameters().getNames();
    while(names.hasMoreElements()) {
      final String name = (String) names.nextElement();
      parameters.put(name, mimeType.getParameter(name));
    }
    builder
      .set(DataType::primaryType).to(mimeType.getPrimaryType())
      .set(DataType::subType).to(mimeType.getSubType())
      .set(DataType::parameters).to(parameters.build())
    ;
    return builder.buildUnvalidated();
  }

  public static final class DataTypeParser implements Parser<DataType>{
    @Override
    public DataType parse(final String encoded, final ExtendedBeanBuilder<DataType> builder) {
      return DataTypeImp.parse(encoded, builder);
    }
  }

  public static final class DataTypeValidator implements Validator<DataType>{
    @Override
    public String invalidMessage(final DataType dataType) {
      final String encoded = dataType.toString();
      final DataType parsed;
      try {
        parsed = parse(
          encoded,
          BeanUtils.typeRegistry().beanType(DataType.class).createBuilder()
        );
      } catch (final Exception e) {
        return format("Invalid: {} ({}).", encoded, e.getMessage());
      }
      return dataType.equals(parsed) ? Validator.VALID : "Parse mismatch.";
    }
  }

}
