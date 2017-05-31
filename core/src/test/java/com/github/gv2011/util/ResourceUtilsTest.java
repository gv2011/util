package com.github.gv2011.util;

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
