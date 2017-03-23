package com.github.gv2011.testutil;

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
