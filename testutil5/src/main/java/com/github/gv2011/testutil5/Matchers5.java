package com.github.gv2011.testutil5;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Optional;
import java.util.function.Predicate;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.gv2011.util.icol.Opt;

public final class Matchers5 {

  private Matchers5(){staticClass();}

  public static <T> Matcher<T> meets(final Predicate<? super T> predicate){
    return meets(predicate, "an object that meets the given predicate");
  }

  public static <T> Matcher<T> meets(final Predicate<? super T> predicate, final String description){
    return new TypeSafeMatcher<T>(){
      @Override
      public void describeTo(final Description d) {
        d.appendText(description);
      }
      @Override
      protected boolean matchesSafely(final T item) {
        return predicate.test(item);
      }
    };
  }

  public static <T> Matcher<Optional<T>> isPresent() {
    return OptionalMatchers.isPresent();
  }

  public static <T> Matcher<Optional<T>> isEmpty() {
    return OptionalMatchers.isEmpty();
  }

  public static <T> Matcher<Opt<T>> isOpt(final T value) {
    return OptionalMatchers.isOpt(value);
  }

}
