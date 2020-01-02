package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

/**
 * Units of measurement and their conversion to SI base units.
 */
public class Unit {

  private Unit(){staticClass();}

  public static final double INCH = 0.0254d;

  /**
   * Dots Per Inch
   */
  public static final double DPI = 1d/INCH;

  public static final double CM = 0.01d;

  public static final double PERCENT = 0.01d;

}
