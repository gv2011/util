package com.github.gv2011.util;

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
