package com.github.gv2011.util.image;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.github.gv2011.util.XStream;

public class Tools {

  @Test
  public void test() {
    XStream.ofArray(ImageIO.getReaderFormatNames()).forEach(System.out::println);
  }

}
