package com.github.gv2011.util;

/**
 * Marker class for "void" parameters (avoids null).
 */
public final class Nothing {

  public static final Nothing INSTANCE = new Nothing();

  private Nothing(){}

}
