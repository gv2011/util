package com.github.gv2011.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Test;

public class JarUtilsTest {

  @Test
  public void test() {
    final URL url = ResourceUtils.getResourceUrl("helloworld-api-0.0.3-SNAPSHOT.jar");
    assertThat(
      JarUtils.getMavenId(url::openStream),
      is(JarUtils.mvnJarId("com.github.gv2011.helloworld","helloworld-api", "0.0.3-SNAPSHOT"))
    );
  }

}
