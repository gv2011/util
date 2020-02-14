package com.github.gv2011.util.uc;

import static com.github.gv2011.util.Verify.verifyEqual;

import java.nio.charset.Charset;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class Iso8859_15Test {

  final Charset iso15 = Charset.forName("ISO8859-15");
  final Charset win = Charset.forName("Windows-1252");

  @Test
  public void list(){
    for(int i=0x00; i<0x100; i++){
      System.out.println(
        Integer.toHexString(i)+" "+Character.getName(i)+ " "+ print(i));
    }
  }

  private String print(final int cp) {
    return Character.isISOControl(cp) ? "" : Character.toString(cp);
  }

  @Test
  public void test(){
    final SortedSet<Integer> codePoints = new TreeSet<>();
    for(int i=0x00; i<0x100; i++){
      if(i<(int)' ') codePoints.add(i);
      add(codePoints, i);
      add(codePoints, getCodePoint((byte) i, iso15));
      add(codePoints, getCodePoint((byte) i, win));

//      final String x = new String(new byte[]{(byte)i}, iso15);
//      verifyEqual(x.length(), 1);
//      final char iso = x.charAt(0);
//      final int cp = (int)iso;
//      if(cp!=i){
//        System.out.print(
//          Integer.toBinaryString(i) +" "+
//          Integer.toHexString(i)+" " +
//              "0x"+Integer.toHexString(cp) +", "
//          + " " + iso + " " + Character.toString(i)
//        );
//      }
    }
    System.out.println(codePoints.size());
    codePoints.forEach(cp->System.out.println(Integer.toHexString(cp)+" "+Character.getName(cp)+ " "+ Character.toString(cp)));
  }

  private void add(final SortedSet<Integer> codePoints, final int cp) {
    if(!Character.isISOControl(cp)) codePoints.add(cp);
  }

  private int getCodePoint(final byte b, final Charset cs) {
    final String x = new String(new byte[]{b}, cs);
    verifyEqual(x.codePoints().count(), 1L);
    return x.codePointAt(0);
  }

}
