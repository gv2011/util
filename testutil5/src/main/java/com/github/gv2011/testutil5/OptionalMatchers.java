package com.github.gv2011.testutil5;

import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.gv2011.util.icol.Opt;

final class OptionalMatchers {

  private OptionalMatchers(){staticClass();}

  static <T> Matcher<Optional<T>> isPresent() {
    return new TypeSafeMatcher<>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText("A non-empty optional.");
      }
      @Override
      protected boolean matchesSafely(final Optional<T> item) {
        return item.isPresent();
      }
    };
  }

  static <T> Matcher<Optional<T>> isEmpty() {
    return new TypeSafeMatcher<>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText("An empty optional.");
      }
      @Override
      protected boolean matchesSafely(final Optional<T> item) {
        return !item.isPresent();
      }
    };
  }

  static <T> Matcher<Opt<T>> isOpt(final T value) {
    return new TypeSafeMatcher<>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText(format("An optional of {}.", value));
      }
      @Override
      protected boolean matchesSafely(final Opt<T> item) {
        return item.isPresent() ? item.get().equals(value) : false;
      }
    };
  }

}
