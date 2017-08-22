package com.github.gv2011.util;

import java.util.Optional;

public interface Either<A,B> {

  boolean isThis();

  default boolean isThat(){
    return !isThis();
  }

  A getThis();

  B getThat();

  default Optional<A> tryGetThis(){
    return isThis() ? Optional.of(getThis()) : Optional.empty();
  }

  default Optional<B> tryGetThat(){
    return isThis() ? Optional.empty() : Optional.of(getThat());
  }

  default Object get(){
    return isThis() ? getThis() : getThat();
  }
}
