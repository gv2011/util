package com.github.gv2011.util.icol;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;

import java.util.NavigableSet;
import java.util.TreeSet;

import org.junit.Test;

public class ArrayISortedSetTest {

  @Test
  public void testSubSet() {
    final NavigableSet<Character> treeSet = new TreeSet<>();
    for(char c='b'; c<='h'; c++) treeSet.add(c);
    treeSet.remove('c');
    final ArrayISortedSet<Character> arrayISortedSet = new ArrayISortedSet<>(treeSet);
    for(char from='a'; from<='i'; from++){
      for(char to=from; to<='i'; to++){
        final NavigableSet<Character> expected = treeSet.subSet(from, false, to, false);
        final NavigableSet<Character> actual = arrayISortedSet.subSet(from, false, to, false);
        assertThat(from+" "+to, actual, is(expected));
      }
    }
  }
}
