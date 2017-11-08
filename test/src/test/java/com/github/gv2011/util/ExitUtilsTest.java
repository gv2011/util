package com.github.gv2011.util;

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
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ExitUtilsTest {

  public static void main(final String[] args) throws InterruptedException {
    Reference<Object> ref;
    {
      final Object obj = new Object(){
        @Override
        protected void finalize() throws Throwable {
          System.out.println("Finalized.");
        }
      };
      ref = new WeakReference<>(obj);
      ExitUtils.doAfterGarbageCollection(obj, ()->System.out.println("Closed"));
    }
    final List<Object> list = new ArrayList<>();
    while(ref.get()!=null){
      list.add(new byte[1000]);
    }
    list.clear();
    System.out.println("Done");
  }

}
