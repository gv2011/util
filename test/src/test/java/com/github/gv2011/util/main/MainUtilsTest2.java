package com.github.gv2011.util.main;

import java.io.IOException;

import com.github.gv2011.util.Nothing;

public class MainUtilsTest2 {

  public static void main(final String[] args) throws IOException{
    MainUtils.runCommand(args, conf->System.out.println("Hello World!"), Nothing.class);
  }

}
