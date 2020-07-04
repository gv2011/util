package com.github.gv2011.testutil;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.meets;

import java.nio.file.Files;

import org.junit.Test;

import com.github.gv2011.util.FileUtils;

public class AbstractTestTest extends AbstractTest{

  @Test
  public void testA() {
  }

  @Test
  public void testB() {
    assertThat(testFolder(), meets(Files::isDirectory));
    assertThat("file1.txt", meets(n->FileUtils.readText(testFolder().resolve(n)).equals("txt1")));
    assertThat("file2.txt", meets(n->FileUtils.readText(testFolder().resolve(n)).equals("text2")));
  }

}
