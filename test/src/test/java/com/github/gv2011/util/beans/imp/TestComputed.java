package com.github.gv2011.util.beans.imp;


import static com.github.gv2011.testutil.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.net.URI;

import org.junit.Test;

import com.github.gv2011.testutil.Matchers;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.Constructor;
import com.github.gv2011.util.beans.Final;

public class TestComputed {

  @Test
  public void testStaticMethod() {
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

  @Final(implementation=BeanAImp.class)
  public static interface BeanA extends Bean{
    Integer a();
    @Computed
    Integer b();
  }

  public static final class BeanAImp implements BeanA{
    private final BeanA core;
    @Constructor
    public BeanAImp(final BeanA core) {
      this.core = core;
    }
    @Override
    public Integer a() {
      return core.a();
    }
    @Override
    public @Computed Integer b() {
      return core.a()+1;
    }
  }

  @Test
  public void testWrapper() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry();
    final BeanA beanA = reg.beanType(BeanA.class).createBuilder()
      .set(BeanA::a).to(1)
      .build()
    ;
    assertThat(beanA.a(), is(1));
    assertThat(beanA.b(), is(2));
  }


}
