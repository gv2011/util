package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import static com.github.gv2011.util.ResourceUtils.getResourceUrl;
import static com.github.gv2011.util.ResourceUtils.resolveRelativeName;
import static com.github.gv2011.util.StreamUtils.readText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Test;

public class ResourceUtilsTest {

  @Test
  public void testGetResourceUrlString() {
    final URL url = getResourceUrl(
      ResourceUtilsTest.class.getPackage().getName().replace('.', '/')+"/tüstR€source.txt"
    );
    assertThat(readText(url::openStream), is("tüstR€source\n"));
  }

  @Test
  public void testGetResourceUrlClassString() {
    final URL url = getResourceUrl(
      ResourceUtilsTest.class,"tüstR€source.txt"
    );
    assertThat(readText(url::openStream), is("tüstR€source\n"));
  }

  @Test
  public void testResolveRelativeName() {
    assertThat(
      resolveRelativeName(ResourceUtilsTest.class,"tüstR€source.txt"),
      is(ResourceUtilsTest.class.getPackage().getName().replace('.', '/')+"/tüstR€source.txt")
    );
  }

}
