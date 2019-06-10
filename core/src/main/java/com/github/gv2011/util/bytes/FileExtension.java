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
import static com.github.gv2011.util.Verify.verify;

import java.util.regex.Pattern;

import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.tstr.AbstractTypedString;

public final class FileExtension extends AbstractTypedString<FileExtension>{

  private static final Pattern PATTERN = Pattern.compile("\\w[\\-\\w\\.\\+]*");

  private final String extension;

  public FileExtension(final String extension) {
    if(!extension.isEmpty()){
      verify(extension, StringUtils::isLowerCase);
      verify(extension, e->PATTERN.matcher(e).matches());
    }
    this.extension = extension;
  }

  @Override
  public FileExtension self() {
    return this;
  }

  @Override
  public Class<FileExtension> clazz() {
    return FileExtension.class;
  }

  @Override
  public String toString() {
    return extension;
  }

}
