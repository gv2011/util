package com.github.gv2011.util;

import static com.github.gv2011.testutil.Assert.assertFalse;
import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Assert.assertTrue;
import static org.hamcrest.Matchers.is;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;

public class ServiceLoaderUtilsTest {

  public static interface Service{}

  public static class ServiceImpl implements Service{}

  @Test
  public void testLoadService() {
    final Iterator<Service> services = ServiceLoader.load(Service.class).iterator();
    assertTrue(services.hasNext());
    assertThat(services.next().getClass(), is((Object)ServiceImpl.class));
    assertFalse(services.hasNext());
  }

}
