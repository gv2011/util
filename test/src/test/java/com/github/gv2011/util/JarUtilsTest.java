package com.github.gv2011.util;

import static com.github.gv2011.testutil.Assert.assertThat;
import static org.hamcrest.Matchers.is;

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
