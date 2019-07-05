package com.github.gv2011.util;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;

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
