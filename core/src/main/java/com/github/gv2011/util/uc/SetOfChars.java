package com.github.gv2011.util.uc;

import static com.github.gv2011.util.icol.ICollections.xStream;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.IntStream;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.ISortedSet;

public interface SetOfChars extends ISortedSet<UChar>{

  @Override
  default boolean contains(final Object o) {
    return (o instanceof UChar) ? containsChar((UChar) o) : false;
  }

  default boolean contains(final UChar o) {
    return containsChar((UChar) o);
  }

  default boolean containsChar(final UChar ch){
    return containsChar(ch.codePoint());
  }

  boolean containsChar(int codePoint);

  default int rangeStart() {
    return Character.MIN_CODE_POINT;
  }

  default int rangeEnd() {
    return Character.MAX_CODE_POINT+1;
  }

  @Override
  SetOfChars subSet(final UChar from, final boolean fromInclusive, final UChar to, final boolean toInclusive);

  @Override
  SetOfChars intersection(final Collection<?> other);

  @Override
  SetOfChars addElement(final UChar other);

  @Override
  default int size() {
    return (int) streamCodepoints().parallel().count();
  }

  @Override
  default boolean isEmpty() {
    return streamCodepoints().parallel().findAny().isEmpty();
  }

  @Override
  default Iterator<UChar> iterator() {
    return streamCodepoints().mapToObj(UChars::uChar).iterator();
  }

  @Override
  default XStream<UChar> stream() {
    return xStream(streamCodepoints().mapToObj(UChars::uChar));
  }

  default IntStream streamCodepoints() {
    return IntStream.range(rangeStart(), rangeEnd())
      .filter(cp->cp>Character.MAX_LOW_SURROGATE ? true : !Character.isSurrogate((char) cp))
      .filter(this::containsChar)
    ;
  }



}
