package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.icol.ICollections.emptySortedSet;
import static com.github.gv2011.util.icol.ICollections.pStream;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;
import static com.github.gv2011.util.icol.ICollections.xStream;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.AbstractISortedSet;
import com.github.gv2011.util.icol.ISortedSet;

final class IntegerSet extends AbstractISortedSet<Integer, ISortedSet<Integer>>{

  int lowerIncl;
  int upperExcl;

  IntegerSet(final int lowerIncl, final int upperExcl) {
    verify(upperExcl>lowerIncl);
    this.lowerIncl = lowerIncl;
    this.upperExcl = upperExcl;
  }

  @Override
  public XStream<Integer> stream() {
    return xStream(IntStream.range(lowerIncl, upperExcl).boxed());
  }

  @Override
  public XStream<Integer> parallelStream() {
    return pStream(IntStream.range(lowerIncl, upperExcl).parallel().boxed());
  }

  @Override
  public XStream<Integer> descendingStream() {
    return descendingStream(upperExcl);
  }

  @Override
  public XStream<Integer> descendingStream(final Integer startExclusive) {
    final int startEx = Math.min(startExclusive, upperExcl);
    return xStream(IntStream.range(lowerIncl+1, startEx+1).map(i->startEx-i).boxed());
  }

  @Override
  public ISortedSet<Integer> addElement(final Integer other) {
    return contains(other) ? this : parallelStream().concat(Stream.of(other)).collect(toISortedSet());
  }

  @Override
  public ISortedSet<Integer> subSet(final Integer from, final boolean fromInclusive, final Integer to, final boolean toInclusive) {
    final int lowerIncl = Math.max(this.lowerIncl, fromInclusive ? from : from+1);
    final int upperExcl = Math.min(this.upperExcl, toInclusive ? to+1 : to);
    return lowerIncl == this.lowerIncl && upperExcl == this.upperExcl
      ? this
      : upperExcl > lowerIncl
      ? new IntegerSet(lowerIncl, upperExcl)
      : emptySortedSet()
    ;
  }

}
