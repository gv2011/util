package com.github.gv2011.testutil;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.gv2011.util.icol.Opt;

final class OptionalMatchers {

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
