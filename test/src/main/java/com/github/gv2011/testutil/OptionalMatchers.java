package com.github.gv2011.testutil;

import static com.github.gv2011.util.ex.Exceptions.format;

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

  static <T> Matcher<Optional<T>> isOpt(final T value) {
    return new TypeSafeMatcher<Optional<T>>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText(format("An optional of {}.", value));
      }
      @Override
      protected boolean matchesSafely(final Optional<T> item) {
        return item.isPresent() ? item.get().equals(value) : false;
      }
    };
  }

}
