package com.github.gv2011.util;

/*-
 * #%L
 * util-test
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

import static com.github.gv2011.testutil.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

  private static final String RAW =
      "GET / HTTP/1.1\r\n"
    + "Host: localhost:50668\r\n"
    + "Connection: keep-alive\r\n"
    + "Upgrade-Insecure-Requests: 1\r\n"
    + "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
      + "Chrome/57.0.2987.133 Safari/537.36\r\n"
    + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
    + "DNT: 1\r\nAccept-Encoding: gzip, deflate, sdch, br\r\n"
    + "Accept-Language: de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4\r\n"
    + "\r\n"
  ;

  private static final String ENCODED =
    "GET / HTTP/1.1[d][a]\n" +
    "Host: localhost:50668[d][a]\n" +
    "Connection: keep-alive[d][a]\n" +
    "Upgrade-Insecure-Requests: 1[d][a]\n" +
    "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
      "Chrome/57.0.2987.133 Safari/537.36[d][a]\n" +
    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8[d][a]\n" +
    "DNT: 1[d][a]\n" +
    "Accept-Encoding: gzip, deflate, sdch, br[d][a]\n" +
    "Accept-Language: de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4[d][a]\n" +
    "[d][a]\n"
  ;

  @Test
  public void testShowSpecial() {
    assertThat(StringUtils.showSpecial(RAW),is(ENCODED));
  }

  @Test
  public void testFromSpecial() {
    assertThat(StringUtils.fromSpecial(ENCODED),is(RAW));
  }

}
