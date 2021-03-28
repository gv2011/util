package com.github.gv2011.util.main;

import com.github.gv2011.util.icol.Nothing;

public class MainUtilsTest2 {

  public static void main(final String[] args){
    MainUtils.runCommand(args, conf->System.out.println("Hello World!"), Nothing.class);
  }

}
