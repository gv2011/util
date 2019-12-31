package com.github.gv2011.util.uc;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import com.github.gv2011.util.bytes.ByteUtils;

public class UnicodeBlockTest {

  @Test
  public void test() {
    final Collator collator = Collator.getInstance(Locale.GERMANY);
    collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    collator.setStrength(Collator.IDENTICAL);
//    IntStream.concat(
//      IntStream.range('!','~'),
//      IntStream.of('ẞ','ẞ', '€', 'ä', 'Ä')
//    )   e2 eb c5 20 1 5 1 5 1 21 1 f0 ab 0
    //    4a 4a 1 86 e0 8d 5 1 89 89 bd 1 23 1 d2 92 0
//    IntStream.range(Character.MIN_CODE_POINT, 10000)
//    .filter(c->!Character.isISOControl(c))
//    .mapToObj(Character::toString)
//    Stream.of("s","t","ß")
    Stream.of("s","ss","t","ß","SS","T","ẞ")
    .sorted(collator)
//    .sorted()com.ibm.icu.text.Collator
    .forEach(c->System.out.println(Character.codePointAt(c, 0)+" "+c+" "+
ByteUtils.newBytes(collator.getCollationKey(c).toByteArray())));
  }

  @Test
  public void test2() {
    Arrays.stream(Collator.getAvailableLocales()).forEach(System.out::println);
  }


  @Test
  public void testToString() {
    UnicodeBlock.BLOCKS.values().stream()
    .filter(b->b.offset()<0x30000 || b.offset()>=0xE0000)
    .map(this::toString)
    .forEach(System.out::println);
    System.out.println(UnicodeBlock.BLOCKS.size()+" blocks.");
    System.out.println(
      UnicodeBlock.BLOCKS.values().stream()
      .filter(b->b.offset()<0x2fa20 || b.offset()>=0xE0000)
      .mapToInt(b->(b.size()-1)/1000+1)
      .sum()
    );

  }

  private String toString(final UnicodeBlock b){
    return b.index()+" "+Integer.toHexString(b.offset())+" "+b+" "+b.size()+" "+(b.surrogate?"surrogate":"");
  }

}
