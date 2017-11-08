package com.github.gv2011.testutil;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Map;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

final class IsMapWithSize<K,V> extends FeatureMatcher<Map<? extends K, ? extends V>, Integer> {
  private IsMapWithSize(final Matcher<? super Integer> sizeMatcher) {
    super(sizeMatcher, "a map with size", "map size");
  }

  @Override
  protected Integer featureValueOf(final Map<? extends K, ? extends V> actual) {
    return actual.size();
  }

  @Factory
  public static <K,V> Matcher<Map<? extends K, ? extends V>> hasSize(final Matcher<? super Integer> sizeMatcher) {
    return new IsMapWithSize<>(sizeMatcher);
  }


  @Factory
  public static <K,V> Matcher<Map<? extends K, ? extends V>> hasSize(final int size) {
    final Matcher<? super Integer> matcher = equalTo(size);
    return IsMapWithSize.<K,V>hasSize(matcher);
  }

}
