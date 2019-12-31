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
import static com.github.gv2011.util.bytes.DataTypes.APPLICATION_OCTET_STREAM;
import static com.github.gv2011.util.bytes.DataTypes.TEXT_PLAIN;
import static com.github.gv2011.util.icol.ICollections.emptySortedSet;
import static com.github.gv2011.util.icol.ICollections.sortedSetOf;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Arrays;

import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;

public final class DefaultDataTypeProvider implements DataTypeProvider{

  private static final FileExtension TXT = new FileExtension("txt");

  @Override
  public ISet<DataType> knownDataTypes() {
    return
      XStream.of(TEXT_PLAIN, APPLICATION_OCTET_STREAM)
      .concat(Arrays.stream(HashAlgorithm.values()).map(HashAlgorithm::getDataType))
      .collect(toISet())
    ;
  }

  @Override
  public Opt<FileExtension> preferredFileExtension(final DataType dataType) {
    if(dataType.baseType().equals(TEXT_PLAIN)) return Opt.of(TXT);
    else return Opt.empty();
  }

  @Override
  public ISortedSet<FileExtension> fileExtensions(final DataType dataType) {
    if(dataType.baseType().equals(TEXT_PLAIN)) return sortedSetOf(TXT);
    else return emptySortedSet();
  }

  @Override
  public ISet<DataType> dataTypesForExtension(final FileExtension extension) {
    return knownDataTypes().stream().filter(dt->dt.fileExtensions().contains(extension)).collect(toISet());
  }

  @Override
  public DataType dataTypeForExtension(final FileExtension extension) {
    final IList<DataType> candidates = knownDataTypes().stream()
      .filter(dt->dt.preferredFileExtension().map(e->e.equals(extension)).orElse(false))
      .collect(toIList())
    ;
    return candidates.size()==1 ? candidates.single() : APPLICATION_OCTET_STREAM;
  }

}
