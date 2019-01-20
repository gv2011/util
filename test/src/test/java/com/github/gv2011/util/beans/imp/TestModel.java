package com.github.gv2011.util.beans.imp;

/*-
 * #%L
 * util-beans
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.github.gv2011.util.beans.AbstractRoot;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Default;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.beans.Other;
import com.github.gv2011.util.beans.TypeName;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.time.TimeSpan;


public class TestModel {

  @AbstractRoot
  public static interface Sized{
    int size();
  }

  @AbstractRoot
  public static interface Coloured{
    String colour();
  }

  @AbstractRoot(subClasses={BlackPea.class, ChickPea.class})
  public static interface Pea extends Bean, Sized, Coloured{
    String type();
    TimeSpan timeSpan();
  }

  public static interface BlackPea extends Pea{
    String propA();
  }

  public static interface ChickPea extends Pea{
    @Override
    @FixedValue("chicks")
    String type();
    String propB();
  }

  @TypeName("saccharatum")
  public static interface SnowPea extends Pea{
    String propC();
  }

  public static enum Colour{RED, @Default BLUE, @Other OTHER}

  @AbstractRoot(subClasses={NormalPot.class, ChickPeaPot.class})
  public static interface Pot extends Bean{
    String type();
    Pea content();
    IList<? extends Pea> moreContent();
  }

  public static interface NormalPot extends Pot{}

  public static interface ChickPeaPot extends Pot{
    @Override
    ChickPea content();
    @Override
    IList<ChickPea> moreContent();
  }

}
