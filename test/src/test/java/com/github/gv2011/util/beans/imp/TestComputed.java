package com.github.gv2011.util.beans.imp;


import static com.github.gv2011.testutil.Assert.assertThat;

import java.net.URI;

import org.junit.Test;

import com.github.gv2011.testutil.Matchers;

public class TestComputed {

  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    final Host host = reg.beanType(Host.class).createBuilder()
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
