package com.github.gv2011.util.beans.imp;

/*-
 * #%L
 * util-beans
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;

import com.github.gv2011.testutil.Matchers;

public class TestComputed {
  
  @Test
  public void test() {
    DefaultTypeRegistry reg = new DefaultTypeRegistry();
    Host host = reg.beanType(Host.class).createBuilder()
      .set(Host::host).to("example.org")
      .build()
    ;
    assertThat(host.url(), Matchers.is(URI.create("https://example.org")));
    assertThat(
        reg.beanType(Host.class).toJson(host).serialize(),
        Matchers.is(
          "{\n"
          + "  \"host\": \"example.org\",\n"
          + "  \"secure\": true\n"
          + "}"
        )
      );
  }

}
