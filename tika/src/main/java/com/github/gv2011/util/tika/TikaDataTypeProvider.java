package com.github.gv2011.util.tika;

import static com.github.gv2011.util.StringUtils.removePrefix;
import static com.github.gv2011.util.StringUtils.toLowerCase;
/*-
 * #%L
 * filetypes-tika
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
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.emptySet;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;
import static com.github.gv2011.util.icol.ICollections.xStream;

import java.util.stream.Stream;

import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeTypes;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypeProvider;
import com.github.gv2011.util.bytes.DefaultDataTypeProvider;
import com.github.gv2011.util.bytes.FileExtension;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;

public final class TikaDataTypeProvider implements DataTypeProvider{

  private final MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

  private final Constant<ISet<DataType>> knownDataTypes =
    Constants.softRefConstant(()->obtainKnownDataTypes(mimeTypes))
  ;

  @Override
  public ISet<DataType> knownDataTypes() {
    return knownDataTypes.get();
  }

  private static ISet<DataType> obtainKnownDataTypes(final MimeTypes mimeTypes) {
    final MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
    return
      new DefaultDataTypeProvider().knownDataTypes().stream()
      .concat(
        registry.getTypes().parallelStream()
        .flatMap(t->Stream.concat(Stream.of(t),registry.getAliases(t).parallelStream()))
        .distinct()
        .map(m->TikaDataTypeProvider.convertToDataType(m))
      )
      .collect(toISet())
    ;
  }

  private static DataType convertToDataType(final MediaType tikaType){
    return DataType.parse(tikaType.toString());
  }

  private FileExtension fileExtension(final String extension){
    return FileExtension.parse(toLowerCase(removePrefix(extension, ".")));
  }

  @Override
  public Opt<FileExtension> preferredFileExtension(final DataType dataType) {
    return
      xStream(
        call(()->mimeTypes.forName(dataType.toString()))
        .getExtensions()
      )
      .tryFindFirst()
      .filter(e->e.startsWith("."))
      .map(this::fileExtension)
    ;
  }

  @Override
  public ISortedSet<FileExtension> fileExtensions(final DataType dataType) {
    return call(()->mimeTypes.forName(dataType.toString()))
      .getExtensions().stream()
      .filter(e->e.startsWith("."))
      .map(this::fileExtension)
      .collect(toISortedSet())
    ;
  }

  @Override
  public ISet<DataType> dataTypesForExtension(final FileExtension extension) {
    return
      extension.isEmpty()
      ? emptySet()
      :(
        knownDataTypes().stream()
        .filter(dt->dt.fileExtensions().contains(extension))
        .collect(toISet())
      )
    ;
  }

  @SuppressWarnings("deprecation")
  @Override
  public DataType dataTypeForExtension(final FileExtension extension) {
    return convertToDataType(mimeTypes.getMimeType("a."+extension).getType());
  }

}
