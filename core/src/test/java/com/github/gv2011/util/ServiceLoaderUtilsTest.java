package com.github.gv2011.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;

public class ServiceLoaderUtilsTest {

  public static interface Service{}

  public static class ServiceImpl implements Service{};

  @Test
  public void testLoadService() {
    final Iterator<Service> services = ServiceLoader.load(Service.class).iterator();
    assertTrue(services.hasNext());
    assertThat(services.next().getClass(), is((Object)ServiceImpl.class));
    assertFalse(services.hasNext());
  }

}
