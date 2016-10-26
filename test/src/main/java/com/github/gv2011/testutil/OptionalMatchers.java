package com.github.gv2011.testutil;

import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

final class OptionalMatchers {

  static <T> Matcher<Optional<T>> isPresent() {
    return new TypeSafeMatcher<Optional<T>>(){
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
    return new TypeSafeMatcher<Optional<T>>(){
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

}
