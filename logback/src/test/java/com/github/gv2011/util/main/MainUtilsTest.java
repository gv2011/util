package com.github.gv2011.util.main;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */
import static com.github.gv2011.util.ex.Exceptions.run;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.main.MainUtils.ServiceBuilder;

public class MainUtilsTest {

  public static void main(final String[] args){
    new Thread().start();
    final MainUtils mainUtils = new MainUtils();
    new Thread(()->{
      run(()->Thread.sleep(100));
      mainUtils.shutdown();
    }).start();
    mainUtils.runMain(args, new TestServiceBuilder());
    System.out.println(3);
  }

  private static class TestService implements AutoCloseableNt{
    @Override
    public void close() {}
  }

  private static class TestServiceBuilder implements ServiceBuilder<TestService> {
    @Override
    public void close() {}
    @Override
    public TestService startService(final String[] args) throws Exception {
      return new TestService();
    }

  }

}
