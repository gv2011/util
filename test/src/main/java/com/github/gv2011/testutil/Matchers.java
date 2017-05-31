/*
 * Copyright (C) 2016      Vinz (https://github.com/gv2011)
 * Copyright (c) 2000-2006 hamcrest.org
 */
package com.github.gv2011.testutil;

import static com.github.gv2011.util.Equal.equal;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.namespace.NamespaceContext;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;


public final class Matchers {

  public static <T> Matcher<Optional<T>> isPresent(){
    return OptionalMatchers.isPresent();
  }

  public static <T> Matcher<Optional<T>> isEmptyOptional(){
    return OptionalMatchers.isEmpty();
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  public static <T> Matcher<T> allOf(final Iterable<Matcher<? super T>> matchers) {
    return org.hamcrest.core.AllOf.<T>allOf(matchers);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> Matcher<T> allOf(final Matcher<? super T>... matchers) {
    return org.hamcrest.core.AllOf.<T>allOf(matchers);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  public static <T> Matcher<T> allOf(final Matcher<? super T> first, final Matcher<? super T> second) {
    return org.hamcrest.core.AllOf.<T>allOf(first, second);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  public static <T> Matcher<T> allOf(final Matcher<? super T> first, final Matcher<? super T> second, final Matcher<? super T> third) {
    return org.hamcrest.core.AllOf.<T>allOf(first, second, third);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  public static <T> Matcher<T> allOf(final Matcher<? super T> first, final Matcher<? super T> second, final Matcher<? super T> third, final Matcher<? super T> fourth) {
    return org.hamcrest.core.AllOf.<T>allOf(first, second, third, fourth);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  public static <T> Matcher<T> allOf(final Matcher<? super T> first, final Matcher<? super T> second, final Matcher<? super T> third, final Matcher<? super T> fourth, final Matcher<? super T> fifth) {
    return org.hamcrest.core.AllOf.<T>allOf(first, second, third, fourth, fifth);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ALL</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  public static <T> Matcher<T> allOf(final Matcher<? super T> first, final Matcher<? super T> second, final Matcher<? super T> third, final Matcher<? super T> fourth, final Matcher<? super T> fifth, final Matcher<? super T> sixth) {
    return org.hamcrest.core.AllOf.<T>allOf(first, second, third, fourth, fifth, sixth);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Iterable<Matcher<? super T>> matchers) {
    return org.hamcrest.core.AnyOf.<T>anyOf(matchers);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Matcher<T> first, final Matcher<? super T> second, final Matcher<? super T> third) {
    return org.hamcrest.core.AnyOf.<T>anyOf(first, second, third);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Matcher<T> first, final Matcher<? super T> second, final Matcher<? super T> third, final Matcher<? super T> fourth) {
    return org.hamcrest.core.AnyOf.<T>anyOf(first, second, third, fourth);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Matcher<T> first, final Matcher<? super T> second, final Matcher<? super T> third, final Matcher<? super T> fourth, final Matcher<? super T> fifth) {
    return org.hamcrest.core.AnyOf.<T>anyOf(first, second, third, fourth, fifth);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Matcher<T> first, final Matcher<? super T> second, final Matcher<? super T> third, final Matcher<? super T> fourth, final Matcher<? super T> fifth, final Matcher<? super T> sixth) {
    return org.hamcrest.core.AnyOf.<T>anyOf(first, second, third, fourth, fifth, sixth);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Matcher<T> first, final Matcher<? super T> second) {
    return org.hamcrest.core.AnyOf.<T>anyOf(first, second);
  }

  /**
   * Creates a matcher that matches if the examined object matches <b>ANY</b> of the specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("foo"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> org.hamcrest.core.AnyOf<T> anyOf(final Matcher<? super T>... matchers) {
    return org.hamcrest.core.AnyOf.<T>anyOf(matchers);
  }

  /**
   * Creates a matcher that matches when both of the specified matchers match the examined object.
   * <p/>
   * For example:
   * <pre>assertThat("fab", both(containsString("a")).and(containsString("b")))</pre>
   */
  public static <LHS> org.hamcrest.core.CombinableMatcher.CombinableBothMatcher<LHS> both(final Matcher<? super LHS> matcher) {
    return org.hamcrest.core.CombinableMatcher.<LHS>both(matcher);
  }

  /**
   * Creates a matcher that matches when either of the specified matchers match the examined object.
   * <p/>
   * For example:
   * <pre>assertThat("fan", either(containsString("a")).and(containsString("b")))</pre>
   */
  public static <LHS> org.hamcrest.core.CombinableMatcher.CombinableEitherMatcher<LHS> either(final Matcher<? super LHS> matcher) {
    return org.hamcrest.core.CombinableMatcher.<LHS>either(matcher);
  }

  /**
   * Wraps an existing matcher, overriding its description with that specified.  All other functions are
   * delegated to the decorated matcher, including its mismatch description.
   * <p/>
   * For example:
   * <pre>describedAs("a big decimal equal to %0", equalTo(myBigDecimal), myBigDecimal.toPlainString())</pre>
   *
   * @param description
   *     the new description for the wrapped matcher
   * @param matcher
   *     the matcher to wrap
   * @param values
   *     optional values to insert into the tokenised description
   */
  public static <T> Matcher<T> describedAs(final java.lang.String description, final Matcher<T> matcher, final java.lang.Object... values) {
    return org.hamcrest.core.DescribedAs.<T>describedAs(description, matcher, values);
  }

  /**
   * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
   * examined {@link Iterable} yields items that are all matched by the specified
   * <code>itemMatcher</code>.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("bar", "baz"), everyItem(startsWith("ba")))</pre>
   *
   * @param itemMatcher
   *     the matcher to apply to every item provided by the examined {@link Iterable}
   */
  public static <U> Matcher<Iterable<U>> everyItem(final Matcher<U> itemMatcher) {
    return org.hamcrest.core.Every.<U>everyItem(itemMatcher);
  }

  /**
   * A shortcut to the frequently used <code>is(equalTo(x))</code>.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(smelly))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(equalTo(smelly)))</pre>
   */
  public static <T> Matcher<T> is(final T value) {
    return org.hamcrest.core.Is.<T>is(value);
  }

  /**
   * Decorates another Matcher, retaining its behaviour, but allowing tests
   * to be slightly more expressive.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(equalTo(smelly)))</pre>
   * instead of:
   * <pre>assertThat(cheese, equalTo(smelly))</pre>
   */
  public static <T> Matcher<T> is(final Matcher<T> matcher) {
    return org.hamcrest.core.Is.<T>is(matcher);
  }

  /**
   * A shortcut to the frequently used <code>is(instanceOf(SomeClass.class))</code>.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, isA(Cheddar.class))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(instanceOf(Cheddar.class)))</pre>
   */
  public static Matcher<Object> isA(final Class<?> type) {
    return new TypeSafeMatcher<Object>(){

      @Override
      public void describeTo(final Description description) {
        description.appendText(format("An instance of {}.",type.getName()));
      }

      @Override
      protected boolean matchesSafely(final Object obj) {
        return type.isInstance(obj);
      }};
  }

  public static Matcher<Object> notNull() {
    return not(nullValue());
  }

  public static Matcher<Object> toStringIs(final String str) {
    return new TypeSafeMatcher<Object>(){

      @Override
      public void describeTo(final Description description) {
        description.appendText(format("An object whose toString() method returns {}.", str));
      }

      @Override
      protected boolean matchesSafely(final Object obj) {
        return equal(obj.toString(),str);
      }};
  }

  /**
   * Creates a matcher that always matches, regardless of the examined object.
   */
  public static Matcher<java.lang.Object> anything() {
    return org.hamcrest.core.IsAnything.anything();
  }

  /**
   * Creates a matcher that always matches, regardless of the examined object, but describes
   * itself with the specified {@link String}.
   *
   * @param description
   *     a meaningful {@link String} used when describing itself
   */
  public static Matcher<java.lang.Object> anything(final java.lang.String description) {
    return org.hamcrest.core.IsAnything.anything(description);
  }

  /**
   * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
   * examined {@link Iterable} yields at least one item that is equal to the specified
   * <code>item</code>.  Whilst matching, the traversal of the examined {@link Iterable}
   * will stop as soon as a matching item is found.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), hasItem("bar"))</pre>
   *
   * @param item
   *     the item to compare against the items provided by the examined {@link Iterable}
   */
  public static <T> Matcher<Iterable<? super T>> hasItem(final T item) {
    return org.hamcrest.core.IsCollectionContaining.<T>hasItem(item);
  }

  /**
   * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
   * examined {@link Iterable} yields at least one item that is matched by the specified
   * <code>itemMatcher</code>.  Whilst matching, the traversal of the examined {@link Iterable}
   * will stop as soon as a matching item is found.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), hasItem(startsWith("ba")))</pre>
   *
   * @param itemMatcher
   *     the matcher to apply to items provided by the examined {@link Iterable}
   */
  public static <T> Matcher<Iterable<? super T>> hasItem(final Matcher<? super T> itemMatcher) {
    return org.hamcrest.core.IsCollectionContaining.<T>hasItem(itemMatcher);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when consecutive passes over the
   * examined {@link Iterable} yield at least one item that is equal to the corresponding
   * item from the specified <code>items</code>.  Whilst matching, each traversal of the
   * examined {@link Iterable} will stop as soon as a matching item is found.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar", "baz"), hasItems("baz", "foo"))</pre>
   *
   * @param items
   *     the items to compare against the items provided by the examined {@link Iterable}
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<T>> hasItems(final T... items) {
    return org.hamcrest.core.IsCollectionContaining.<T>hasItems(items);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when consecutive passes over the
   * examined {@link Iterable} yield at least one item that is matched by the corresponding
   * matcher from the specified <code>itemMatchers</code>.  Whilst matching, each traversal of
   * the examined {@link Iterable} will stop as soon as a matching item is found.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar", "baz"), hasItems(endsWith("z"), endsWith("o")))</pre>
   *
   * @param itemMatchers
   *     the matchers to apply to items provided by the examined {@link Iterable}
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<T>> hasItems(final Matcher<? super T>... itemMatchers) {
    return org.hamcrest.core.IsCollectionContaining.<T>hasItems(itemMatchers);
  }

  /**
   * Creates a matcher that matches when the examined object is logically equal to the specified
   * <code>operand</code>, as determined by calling the {@link java.lang.Object#equals} method on
   * the <b>examined</b> object.
   *
   * <p>If the specified operand is <code>null</code> then the created matcher will only match if
   * the examined object's <code>equals</code> method returns <code>true</code> when passed a
   * <code>null</code> (which would be a violation of the <code>equals</code> contract), unless the
   * examined object itself is <code>null</code>, in which case the matcher will return a positive
   * match.</p>
   *
   * <p>The created matcher provides a special behaviour when examining <code>Array</code>s, whereby
   * it will match if both the operand and the examined object are arrays of the same length and
   * contain items that are equal to each other (according to the above rules) <b>in the same
   * indexes</b>.</p>
   * <p/>
   * For example:
   * <pre>
   * assertThat("foo", equalTo("foo"));
   * assertThat(new String[] {"foo", "bar"}, equalTo(new String[] {"foo", "bar"}));
   * </pre>
   */
  public static <T> Matcher<T> equalTo(final T operand) {
    return org.hamcrest.core.IsEqual.<T>equalTo(operand);
  }

  /**
   * Creates a matcher that matches when the examined object is an instance of the specified <code>type</code>,
   * as determined by calling the {@link java.lang.Class#isInstance(Object)} method on that type, passing the
   * the examined object.
   *
   * <p>The created matcher forces a relationship between specified type and the examined object, and should be
   * used when it is necessary to make generics conform, for example in the JMock clause
   * <code>with(any(Thing.class))</code></p>
   * <p/>
   * For example:
   * <pre>assertThat(new Canoe(), instanceOf(Canoe.class));</pre>
   */
  public static <T> Matcher<T> any(final java.lang.Class<T> type) {
    return org.hamcrest.core.IsInstanceOf.<T>any(type);
  }

  /**
   * Creates a matcher that matches when the examined object is an instance of the specified <code>type</code>,
   * as determined by calling the {@link java.lang.Class#isInstance(Object)} method on that type, passing the
   * the examined object.
   *
   * <p>The created matcher assumes no relationship between specified type and the examined object.</p>
   * <p/>
   * For example:
   * <pre>assertThat(new Canoe(), instanceOf(Paddlable.class));</pre>
   */
  public static <T> Matcher<T> instanceOf(final java.lang.Class<?> type) {
    return org.hamcrest.core.IsInstanceOf.<T>instanceOf(type);
  }

  /**
   * Creates a matcher that wraps an existing matcher, but inverts the logic by which
   * it will match.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(not(equalTo(smelly))))</pre>
   *
   * @param matcher
   *     the matcher whose sense should be inverted
   */
  public static <T> Matcher<T> not(final Matcher<T> matcher) {
    return org.hamcrest.core.IsNot.<T>not(matcher);
  }

  /**
   * A shortcut to the frequently used <code>not(equalTo(x))</code>.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(not(smelly)))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(equalTo(smelly))))</pre>
   *
   * @param value
   *     the value that any examined object should <b>not</b> equal
   */
  public static <T> Matcher<T> not(final T value) {
    return org.hamcrest.core.IsNot.<T>not(value);
  }

  /**
   * Creates a matcher that matches if examined object is <code>null</code>.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(nullValue())</pre>
   */
  public static Matcher<java.lang.Object> nullValue() {
    return org.hamcrest.core.IsNull.nullValue();
  }

  /**
   * Creates a matcher that matches if examined object is <code>null</code>. Accepts a
   * single dummy argument to facilitate type inference.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(nullValue(Cheese.class))</pre>
   *
   * @param type
   *     dummy parameter used to infer the generic type of the returned matcher
   */
  public static <T> Matcher<T> nullValue(final java.lang.Class<T> type) {
    return org.hamcrest.core.IsNull.<T>nullValue(type);
  }

  /**
   * A shortcut to the frequently used <code>not(nullValue())</code>.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(notNullValue()))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(nullValue())))</pre>
   */
  public static Matcher<java.lang.Object> notNullValue() {
    return org.hamcrest.core.IsNull.notNullValue();
  }

  /**
   * A shortcut to the frequently used <code>not(nullValue(X.class)). Accepts a
   * single dummy argument to facilitate type inference.</code>.
   * <p/>
   * For example:
   * <pre>assertThat(cheese, is(notNullValue(X.class)))</pre>
   * instead of:
   * <pre>assertThat(cheese, is(not(nullValue(X.class))))</pre>
   *
   * @param type
   *     dummy parameter used to infer the generic type of the returned matcher
   */
  public static <T> Matcher<T> notNullValue(final java.lang.Class<T> type) {
    return org.hamcrest.core.IsNull.<T>notNullValue(type);
  }

  /**
   * Creates a matcher that matches only when the examined object is the same instance as
   * the specified target object.
   *
   * @param target
   *     the target instance against which others should be assessed
   */
  public static <T> Matcher<T> sameInstance(final T target) {
    return org.hamcrest.core.IsSame.<T>sameInstance(target);
  }

  /**
   * Creates a matcher that matches only when the examined object is the same instance as
   * the specified target object.
   *
   * @param target
   *     the target instance against which others should be assessed
   */
  public static <T> Matcher<T> theInstance(final T target) {
    return org.hamcrest.core.IsSame.<T>theInstance(target);
  }

  /**
   * Creates a matcher that matches if the examined {@link String} contains the specified
   * {@link String} anywhere.
   * <p/>
   * For example:
   * <pre>assertThat("myStringOfNote", containsString("ring"))</pre>
   *
   * @param substring
   *     the substring that the returned matcher will expect to find within any examined string
   */
  public static Matcher<java.lang.String> containsString(final java.lang.String substring) {
    return org.hamcrest.core.StringContains.containsString(substring);
  }

  /**
   * Creates a matcher that matches if the examined {@link String} starts with the specified
   * {@link String}.
   * <p/>
   * For example:
   * <pre>assertThat("myStringOfNote", startsWith("my"))</pre>
   *
   * @param prefix
   *      the substring that the returned matcher will expect at the start of any examined string
   */
  public static Matcher<java.lang.String> startsWith(final java.lang.String prefix) {
    return org.hamcrest.core.StringStartsWith.startsWith(prefix);
  }

  /**
   * Creates a matcher that matches if the examined {@link String} ends with the specified
   * {@link String}.
   * <p/>
   * For example:
   * <pre>assertThat("myStringOfNote", endsWith("Note"))</pre>
   *
   * @param suffix
   *      the substring that the returned matcher will expect at the end of any examined string
   */
  public static Matcher<java.lang.String> endsWith(final java.lang.String suffix) {
    return org.hamcrest.core.StringEndsWith.endsWith(suffix);
  }

  /**
   * Creates a matcher that matches arrays whose elements are satisfied by the specified matchers.  Matches
   * positively only if the number of matchers specified is equal to the length of the examined array and
   * each matcher[i] is satisfied by array[i].
   * <p/>
   * For example:
   * <pre>assertThat(new Integer[]{1,2,3}, is(array(equalTo(1), equalTo(2), equalTo(3))))</pre>
   *
   * @param elementMatchers
   *     the matchers that the elements of examined arrays should satisfy
   */
  @SafeVarargs
  public static <T> org.hamcrest.collection.IsArray<T> array(final Matcher<? super T>... elementMatchers) {
    return org.hamcrest.collection.IsArray.<T>array(elementMatchers);
  }

  /**
   * A shortcut to the frequently used <code>hasItemInArray(equalTo(x))</code>.
   * <p/>
   * For example:
   * <pre>assertThat(hasItemInArray(x))</pre>
   * instead of:
   * <pre>assertThat(hasItemInArray(equalTo(x)))</pre>
   *
   * @param element
   *     the element that should be present in examined arrays
   */
  public static <T> Matcher<T[]> hasItemInArray(final T element) {
    return org.hamcrest.collection.IsArrayContaining.<T>hasItemInArray(element);
  }

  /**
   * Creates a matcher for arrays that matches when the examined array contains at least one item
   * that is matched by the specified <code>elementMatcher</code>.  Whilst matching, the traversal
   * of the examined array will stop as soon as a matching element is found.
   * <p/>
   * For example:
   * <pre>assertThat(new String[] {"foo", "bar"}, hasItemInArray(startsWith("ba")))</pre>
   *
   * @param elementMatcher
   *     the matcher to apply to elements in examined arrays
   */
  public static <T> Matcher<T[]> hasItemInArray(final Matcher<? super T> elementMatcher) {
    return org.hamcrest.collection.IsArrayContaining.<T>hasItemInArray(elementMatcher);
  }

  /**
   * Creates a matcher for arrays that matches when each item in the examined array satisfies the
   * corresponding matcher in the specified list of matchers.  For a positive match, the examined array
   * must be of the same length as the specified list of matchers.
   * <p/>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, contains(Arrays.asList(equalTo("foo"), equalTo("bar"))))</pre>
   *
   * @param itemMatchers
   *     a list of matchers, each of which must be satisfied by the corresponding item in an examined array
   */
  public static <E> Matcher<E[]> arrayContaining(final java.util.List<Matcher<? super E>> itemMatchers) {
    return org.hamcrest.collection.IsArrayContainingInOrder.<E>arrayContaining(itemMatchers);
  }

  /**
   * Creates a matcher for arrays that matcheswhen each item in the examined array is
   * logically equal to the corresponding item in the specified items.  For a positive match,
   * the examined array must be of the same length as the number of specified items.
   * <p/>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, contains("foo", "bar"))</pre>
   *
   * @param items
   *     the items that must equal the items within an examined array
   */
  @SafeVarargs
  public static <E> Matcher<E[]> arrayContaining(final E... items) {
    return org.hamcrest.collection.IsArrayContainingInOrder.<E>arrayContaining(items);
  }

  /**
   * Creates a matcher for arrays that matches when each item in the examined array satisfies the
   * corresponding matcher in the specified matchers.  For a positive match, the examined array
   * must be of the same length as the number of specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, contains(equalTo("foo"), equalTo("bar")))</pre>
   *
   * @param itemMatchers
   *     the matchers that must be satisfied by the items in the examined array
   */
  @SafeVarargs
  public static <E> Matcher<E[]> arrayContaining(final Matcher<? super E>... itemMatchers) {
    return org.hamcrest.collection.IsArrayContainingInOrder.<E>arrayContaining(itemMatchers);
  }

  /**
   * Creates an order agnostic matcher for arrays that matches when each item in the
   * examined array is logically equal to one item anywhere in the specified items.
   * For a positive match, the examined array must be of the same length as the number of
   * specified items.
   * <p/>
   * N.B. each of the specified items will only be used once during a given examination, so be
   * careful when specifying items that may be equal to more than one entry in an examined
   * array.
   * <p>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, containsInAnyOrder("bar", "foo"))</pre>
   *
   * @param items
   *     the items that must equal the entries of an examined array, in any order
   */
  @SafeVarargs
  public static <E> Matcher<E[]> arrayContainingInAnyOrder(final E... items) {
    return org.hamcrest.collection.IsArrayContainingInAnyOrder.<E>arrayContainingInAnyOrder(items);
  }

  /**
   * Creates an order agnostic matcher for arrays that matches when each item in the
   * examined array satisfies one matcher anywhere in the specified matchers.
   * For a positive match, the examined array must be of the same length as the number of
   * specified matchers.
   * <p/>
   * N.B. each of the specified matchers will only be used once during a given examination, so be
   * careful when specifying matchers that may be satisfied by more than one entry in an examined
   * array.
   * <p>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, arrayContainingInAnyOrder(equalTo("bar"), equalTo("foo")))</pre>
   *
   * @param itemMatchers
   *     a list of matchers, each of which must be satisfied by an entry in an examined array
   */
  @SafeVarargs
  public static <E> Matcher<E[]> arrayContainingInAnyOrder(final Matcher<? super E>... itemMatchers) {
    return org.hamcrest.collection.IsArrayContainingInAnyOrder.<E>arrayContainingInAnyOrder(itemMatchers);
  }

  /**
   * Creates an order agnostic matcher for arrays that matches when each item in the
   * examined array satisfies one matcher anywhere in the specified collection of matchers.
   * For a positive match, the examined array must be of the same length as the specified collection
   * of matchers.
   * <p/>
   * N.B. each matcher in the specified collection will only be used once during a given
   * examination, so be careful when specifying matchers that may be satisfied by more than
   * one entry in an examined array.
   * <p>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, arrayContainingInAnyOrder(Arrays.asList(equalTo("bar"), equalTo("foo"))))</pre>
   *
   * @param itemMatchers
   *     a list of matchers, each of which must be satisfied by an item provided by an examined array
   */
  public static <E> Matcher<E[]> arrayContainingInAnyOrder(final Collection<Matcher<? super E>> itemMatchers) {
    return org.hamcrest.collection.IsArrayContainingInAnyOrder.<E>arrayContainingInAnyOrder(itemMatchers);
  }

  /**
   * Creates a matcher for arrays that matches when the <code>length</code> of the array
   * satisfies the specified matcher.
   * <p/>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, arrayWithSize(equalTo(2)))</pre>
   *
   * @param sizeMatcher
   *     a matcher for the length of an examined array
   */
  public static <E> Matcher<E[]> arrayWithSize(final Matcher<? super java.lang.Integer> sizeMatcher) {
    return org.hamcrest.collection.IsArrayWithSize.<E>arrayWithSize(sizeMatcher);
  }

  /**
   * Creates a matcher for arrays that matches when the <code>length</code> of the array
   * equals the specified <code>size</code>.
   * <p/>
   * For example:
   * <pre>assertThat(new String[]{"foo", "bar"}, arrayWithSize(2))</pre>
   *
   * @param size
   *     the length that an examined array must have for a positive match
   */
  public static <E> Matcher<E[]> arrayWithSize(final int size) {
    return org.hamcrest.collection.IsArrayWithSize.<E>arrayWithSize(size);
  }

  /**
   * Creates a matcher for arrays that matches when the <code>length</code> of the array
   * is zero.
   * <p/>
   * For example:
   * <pre>assertThat(new String[0], emptyArray())</pre>
   */
  public static <E> Matcher<E[]> emptyArray() {
    return org.hamcrest.collection.IsArrayWithSize.<E>emptyArray();
  }

  /**
   * Creates a matcher for {@link Collection}s that matches when the <code>size()</code> method returns
   * a value that satisfies the specified matcher.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), hasSize(equalTo(2)))</pre>
   *
   * @param sizeMatcher
   *     a matcher for the size of an examined {@link Collection}
   */
  public static <E> Matcher<Collection<? extends E>> hasSize(final Matcher<? super java.lang.Integer> sizeMatcher) {
    return org.hamcrest.collection.IsCollectionWithSize.<E>hasSize(sizeMatcher);
  }

  /**
   * Creates a matcher for {@link Collection}s that matches when the <code>size()</code> method returns
   * a value equal to the specified <code>size</code>.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), hasSize(2))</pre>
   *
   * @param size
   *     the expected size of an examined {@link Collection}
   */
  public static <E> Matcher<Collection<? extends E>> hasSize(final int size) {
    return org.hamcrest.collection.IsCollectionWithSize.<E>hasSize(size);
  }

  public static <K,V> Matcher<Map<? extends K, ? extends V>> mapWithSize(final int size) {
    return IsMapWithSize.<K,V>hasSize(size);
  }

  /**
   * Creates a matcher for {@link Collection}s matching examined collections whose <code>isEmpty</code>
   * method returns <code>true</code>.
   * <p/>
   * For example:
   * <pre>assertThat(new ArrayList&lt;String&gt;(), is(empty()))</pre>
   */
  public static <E> Matcher<Collection<? extends E>> empty() {
    return org.hamcrest.collection.IsEmptyCollection.<E>empty();
  }

  /**
   * Creates a matcher for {@link Collection}s matching examined collections whose <code>isEmpty</code>
   * method returns <code>true</code>.
   * <p/>
   * For example:
   * <pre>assertThat(new ArrayList&lt;String&gt;(), is(emptyCollectionOf(String.class)))</pre>
   *
   * @param type
   *     the type of the collection's content
   */
  public static <E> Matcher<Collection<E>> emptyCollectionOf(final java.lang.Class<E> type) {
    return org.hamcrest.collection.IsEmptyCollection.<E>emptyCollectionOf(type);
  }

  /**
   * Creates a matcher for {@link Iterable}s matching examined iterables that yield no items.
   * <p/>
   * For example:
   * <pre>assertThat(new ArrayList&lt;String&gt;(), is(emptyIterable()))</pre>
   */
  public static <E> Matcher<Iterable<? extends E>> emptyIterable() {
    return org.hamcrest.collection.IsEmptyIterable.<E>emptyIterable();
  }

  /**
   * Creates a matcher for {@link Iterable}s matching examined iterables that yield no items.
   * <p/>
   * For example:
   * <pre>assertThat(new ArrayList&lt;String&gt;(), is(emptyIterableOf(String.class)))</pre>
   *
   * @param type
   *     the type of the iterable's content
   */
  public static <E> Matcher<Iterable<E>> emptyIterableOf(final java.lang.Class<E> type) {
    return org.hamcrest.collection.IsEmptyIterable.<E>emptyIterableOf(type);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields a series of items, each satisfying the corresponding
   * matcher in the specified matchers.  For a positive match, the examined iterable
   * must be of the same length as the number of specified matchers.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), contains(equalTo("foo"), equalTo("bar")))</pre>
   *
   * @param itemMatchers
   *     the matchers that must be satisfied by the items provided by an examined {@link Iterable}
   */
  @SafeVarargs
  public static <E> Matcher<Iterable<? extends E>> contains(final Matcher<? super E>... itemMatchers) {
    return org.hamcrest.collection.IsIterableContainingInOrder.<E>contains(itemMatchers);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields a series of items, each logically equal to the
   * corresponding item in the specified items.  For a positive match, the examined iterable
   * must be of the same length as the number of specified items.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), contains("foo", "bar"))</pre>
   *
   * @param items
   *     the items that must equal the items provided by an examined {@link Iterable}
   */
  @SafeVarargs
  public static <E> Matcher<Iterable<? extends E>> contains(final E... items) {
    return org.hamcrest.collection.IsIterableContainingInOrder.<E>contains(items);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields a single item that satisfies the specified matcher.
   * For a positive match, the examined iterable must only yield one item.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo"), contains(equalTo("foo")))</pre>
   *
   * @param itemMatcher
   *     the matcher that must be satisfied by the single item provided by an
   *     examined {@link Iterable}
   */
  public static <E> Matcher<Iterable<? extends E>> contains(final Matcher<? super E> itemMatcher) {
    return org.hamcrest.collection.IsIterableContainingInOrder.<E>contains(itemMatcher);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields a series of items, each satisfying the corresponding
   * matcher in the specified list of matchers.  For a positive match, the examined iterable
   * must be of the same length as the specified list of matchers.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), contains(Arrays.asList(equalTo("foo"), equalTo("bar"))))</pre>
   *
   * @param itemMatchers
   *     a list of matchers, each of which must be satisfied by the corresponding item provided by
   *     an examined {@link Iterable}
   */
  public static <E> Matcher<Iterable<? extends E>> contains(final java.util.List<Matcher<? super E>> itemMatchers) {
    return org.hamcrest.collection.IsIterableContainingInOrder.<E>contains(itemMatchers);
  }

  /**
   * Creates an order agnostic matcher for {@link Iterable}s that matches when a single pass over
   * the examined {@link Iterable} yields a series of items, each logically equal to one item
   * anywhere in the specified items. For a positive match, the examined iterable
   * must be of the same length as the number of specified items.
   * <p/>
   * N.B. each of the specified items will only be used once during a given examination, so be
   * careful when specifying items that may be equal to more than one entry in an examined
   * iterable.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), containsInAnyOrder("bar", "foo"))</pre>
   *
   * @param items
   *     the items that must equal the items provided by an examined {@link Iterable} in any order
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<? extends T>> containsInAnyOrder(final T... items) {
    return org.hamcrest.collection.IsIterableContainingInAnyOrder.<T>containsInAnyOrder(items);
  }

  /**
   * Creates an order agnostic matcher for {@link Iterable}s that matches when a single pass over
   * the examined {@link Iterable} yields a series of items, each satisfying one matcher anywhere
   * in the specified collection of matchers.  For a positive match, the examined iterable
   * must be of the same length as the specified collection of matchers.
   * <p/>
   * N.B. each matcher in the specified collection will only be used once during a given
   * examination, so be careful when specifying matchers that may be satisfied by more than
   * one entry in an examined iterable.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), containsInAnyOrder(Arrays.asList(equalTo("bar"), equalTo("foo"))))</pre>
   *
   * @param itemMatchers
   *     a list of matchers, each of which must be satisfied by an item provided by an examined {@link Iterable}
   */
  public static <T> Matcher<Iterable<? extends T>> containsInAnyOrder(final Collection<Matcher<? super T>> itemMatchers) {
    return org.hamcrest.collection.IsIterableContainingInAnyOrder.<T>containsInAnyOrder(itemMatchers);
  }

  /**
   * Creates an order agnostic matcher for {@link Iterable}s that matches when a single pass over
   * the examined {@link Iterable} yields a series of items, each satisfying one matcher anywhere
   * in the specified matchers.  For a positive match, the examined iterable must be of the same
   * length as the number of specified matchers.
   * <p/>
   * N.B. each of the specified matchers will only be used once during a given examination, so be
   * careful when specifying matchers that may be satisfied by more than one entry in an examined
   * iterable.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), containsInAnyOrder(equalTo("bar"), equalTo("foo")))</pre>
   *
   * @param itemMatchers
   *     a list of matchers, each of which must be satisfied by an item provided by an examined {@link Iterable}
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<? extends T>> containsInAnyOrder(final Matcher<? super T>... itemMatchers) {
    return org.hamcrest.collection.IsIterableContainingInAnyOrder.<T>containsInAnyOrder(itemMatchers);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields a single item that satisfies the specified matcher.
   * For a positive match, the examined iterable must only yield one item.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo"), containsInAnyOrder(equalTo("foo")))</pre>
   *
   * @deprecated use contains(Matcher<? super E> itemMatcher) instead
   * @param itemMatcher
   *     the matcher that must be satisfied by the single item provided by an
   *     examined {@link Iterable}
   */
  @Deprecated
  public static <E> Matcher<Iterable<? extends E>> containsInAnyOrder(final Matcher<? super E> itemMatcher) {
    return org.hamcrest.collection.IsIterableContainingInAnyOrder.<E>containsInAnyOrder(itemMatcher);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields an item count that satisfies the specified
   * matcher.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), iterableWithSize(equalTo(2)))</pre>
   *
   * @param sizeMatcher
   *     a matcher for the number of items that should be yielded by an examined {@link Iterable}
   */
  public static <E> Matcher<Iterable<E>> iterableWithSize(final Matcher<? super java.lang.Integer> sizeMatcher) {
    return org.hamcrest.collection.IsIterableWithSize.<E>iterableWithSize(sizeMatcher);
  }

  /**
   * Creates a matcher for {@link Iterable}s that matches when a single pass over the
   * examined {@link Iterable} yields an item count that is equal to the specified
   * <code>size</code> argument.
   * <p/>
   * For example:
   * <pre>assertThat(Arrays.asList("foo", "bar"), iterableWithSize(2))</pre>
   *
   * @param size
   *     the number of items that should be yielded by an examined {@link Iterable}
   */
  public static <E> Matcher<Iterable<E>> iterableWithSize(final int size) {
    return org.hamcrest.collection.IsIterableWithSize.<E>iterableWithSize(size);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s matching when the examined {@link java.util.Map} contains
   * at least one entry whose key equals the specified <code>key</code> <b>and</b> whose value equals the
   * specified <code>value</code>.
   * <p/>
   * For example:
   * <pre>assertThat(myMap, hasEntry("bar", "foo"))</pre>
   *
   * @param key
   *     the key that, in combination with the value, must be describe at least one entry
   * @param value
   *     the value that, in combination with the key, must be describe at least one entry
   */
  public static <K, V> Matcher<java.util.Map<? extends K, ? extends V>> hasEntry(final K key, final V value) {
    return org.hamcrest.collection.IsMapContaining.<K,V>hasEntry(key, value);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s matching when the examined {@link java.util.Map} contains
   * at least one entry whose key satisfies the specified <code>keyMatcher</code> <b>and</b> whose
   * value satisfies the specified <code>valueMatcher</code>.
   * <p/>
   * For example:
   * <pre>assertThat(myMap, hasEntry(equalTo("bar"), equalTo("foo")))</pre>
   *
   * @param keyMatcher
   *     the key matcher that, in combination with the valueMatcher, must be satisfied by at least one entry
   * @param valueMatcher
   *     the value matcher that, in combination with the keyMatcher, must be satisfied by at least one entry
   */
  public static <K, V> Matcher<java.util.Map<? extends K, ? extends V>> hasEntry(final Matcher<? super K> keyMatcher, final Matcher<? super V> valueMatcher) {
    return org.hamcrest.collection.IsMapContaining.<K,V>hasEntry(keyMatcher, valueMatcher);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s matching when the examined {@link java.util.Map} contains
   * at least one key that satisfies the specified matcher.
   * <p/>
   * For example:
   * <pre>assertThat(myMap, hasKey(equalTo("bar")))</pre>
   *
   * @param keyMatcher
   *     the matcher that must be satisfied by at least one key
   */
  public static <K> Matcher<java.util.Map<? extends K, ?>> hasKey(final Matcher<? super K> keyMatcher) {
    return org.hamcrest.collection.IsMapContaining.<K>hasKey(keyMatcher);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s matching when the examined {@link java.util.Map} contains
   * at least one key that is equal to the specified key.
   * <p/>
   * For example:
   * <pre>assertThat(myMap, hasKey("bar"))</pre>
   *
   * @param key
   *     the key that satisfying maps must contain
   */
  public static <K> Matcher<java.util.Map<? extends K, ?>> hasKey(final K key) {
    return org.hamcrest.collection.IsMapContaining.<K>hasKey(key);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s matching when the examined {@link java.util.Map} contains
   * at least one value that is equal to the specified value.
   * <p/>
   * For example:
   * <pre>assertThat(myMap, hasValue("foo"))</pre>
   *
   * @param value
   *     the value that satisfying maps must contain
   */
  public static <V> Matcher<java.util.Map<?, ? extends V>> hasValue(final V value) {
    return org.hamcrest.collection.IsMapContaining.<V>hasValue(value);
  }

  /**
   * Creates a matcher for {@link java.util.Map}s matching when the examined {@link java.util.Map} contains
   * at least one value that satisfies the specified valueMatcher.
   * <p/>
   * For example:
   * <pre>assertThat(myMap, hasValue(equalTo("foo")))</pre>
   *
   * @param valueMatcher
   *     the matcher that must be satisfied by at least one value
   */
  public static <V> Matcher<java.util.Map<?, ? extends V>> hasValue(final Matcher<? super V> valueMatcher) {
    return org.hamcrest.collection.IsMapContaining.<V>hasValue(valueMatcher);
  }

  /**
   * Creates a matcher that matches when the examined object is found within the
   * specified collection.
   * <p/>
   * For example:
   * <pre>assertThat("foo", isIn(Arrays.asList("bar", "foo")))</pre>
   *
   * @param collection
   *     the collection in which matching items must be found
   */
  public static <T> Matcher<T> isIn(final Collection<T> collection) {
    return org.hamcrest.collection.IsIn.<T>isIn(collection);
  }

  public static <T> Matcher<T> isIn(final T[] param1) {
    return org.hamcrest.collection.IsIn.<T>isIn(param1);
  }

  /**
   * Creates a matcher that matches when the examined object is equal to one of the
   * specified elements.
   * <p/>
   * For example:
   * <pre>assertThat("foo", isIn("bar", "foo"))</pre>
   *
   * @param elements
   *     the elements amongst which matching items will be found
   */
  @SafeVarargs
  public static <T> Matcher<T> isOneOf(final T... elements) {
    return org.hamcrest.collection.IsIn.<T>isOneOf(elements);
  }

  /**
   * Creates a matcher of {@link Double}s that matches when an examined double is equal
   * to the specified <code>operand</code>, within a range of +/- <code>error</code>.
   * <p/>
   * For example:
   * <pre>assertThat(1.03, is(closeTo(1.0, 0.03)))</pre>
   *
   * @param operand
   *     the expected value of matching doubles
   * @param error
   *     the delta (+/-) within which matches will be allowed
   */
  public static Matcher<java.lang.Double> closeTo(final double operand, final double error) {
    return org.hamcrest.number.IsCloseTo.closeTo(operand, error);
  }

  /**
   * Creates a matcher of {@link java.math.BigDecimal}s that matches when an examined BigDecimal is equal
   * to the specified <code>operand</code>, within a range of +/- <code>error</code>. The comparison for equality
   * is done by BigDecimals {@link java.math.BigDecimal#compareTo(java.math.BigDecimal)} method.
   * <p/>
   * For example:
   * <pre>assertThat(new BigDecimal("1.03"), is(closeTo(new BigDecimal("1.0"), new BigDecimal("0.03"))))</pre>
   *
   * @param operand
   *     the expected value of matching BigDecimals
   * @param error
   *     the delta (+/-) within which matches will be allowed
   */
  public static Matcher<java.math.BigDecimal> closeTo(final java.math.BigDecimal operand, final java.math.BigDecimal error) {
    return org.hamcrest.number.BigDecimalCloseTo.closeTo(operand, error);
  }

  /**
   * Creates a matcher of {@link Comparable} object that matches when the examined object is
   * equal to the specified value, as reported by the <code>compareTo</code> method of the
   * <b>examined</b> object.
   * <p/>
   * For example:
   * <pre>assertThat(1, comparesEqualTo(1))</pre>
   *
   * @param value
   *     the value which, when passed to the compareTo method of the examined object, should return zero
   */
  public static <T extends java.lang.Comparable<T>> Matcher<T> comparesEqualTo(final T value) {
    return org.hamcrest.number.OrderingComparison.<T>comparesEqualTo(value);
  }

  /**
   * Creates a matcher of {@link Comparable} object that matches when the examined object is
   * greater than the specified value, as reported by the <code>compareTo</code> method of the
   * <b>examined</b> object.
   * <p/>
   * For example:
   * <pre>assertThat(2, greaterThan(1))</pre>
   *
   * @param value
   *     the value which, when passed to the compareTo method of the examined object, should return greater
   *     than zero
   */
  public static <T extends java.lang.Comparable<T>> Matcher<T> greaterThan(final T value) {
    return org.hamcrest.number.OrderingComparison.<T>greaterThan(value);
  }

  /**
   * Creates a matcher of {@link Comparable} object that matches when the examined object is
   * greater than or equal to the specified value, as reported by the <code>compareTo</code> method
   * of the <b>examined</b> object.
   * <p/>
   * For example:
   * <pre>assertThat(1, greaterThanOrEqualTo(1))</pre>
   *
   * @param value
   *     the value which, when passed to the compareTo method of the examined object, should return greater
   *     than or equal to zero
   */
  public static <T extends java.lang.Comparable<T>> Matcher<T> greaterThanOrEqualTo(final T value) {
    return org.hamcrest.number.OrderingComparison.<T>greaterThanOrEqualTo(value);
  }

  /**
   * Creates a matcher of {@link Comparable} object that matches when the examined object is
   * less than the specified value, as reported by the <code>compareTo</code> method of the
   * <b>examined</b> object.
   * <p/>
   * For example:
   * <pre>assertThat(1, lessThan(2))</pre>
   *
   * @param value
   *     the value which, when passed to the compareTo method of the examined object, should return less
   *     than zero
   */
  public static <T extends java.lang.Comparable<T>> Matcher<T> lessThan(final T value) {
    return org.hamcrest.number.OrderingComparison.<T>lessThan(value);
  }

  /**
   * Creates a matcher of {@link Comparable} object that matches when the examined object is
   * less than or equal to the specified value, as reported by the <code>compareTo</code> method
   * of the <b>examined</b> object.
   * <p/>
   * For example:
   * <pre>assertThat(1, lessThanOrEqualTo(1))</pre>
   *
   * @param value
   *     the value which, when passed to the compareTo method of the examined object, should return less
   *     than or equal to zero
   */
  public static <T extends java.lang.Comparable<T>> Matcher<T> lessThanOrEqualTo(final T value) {
    return org.hamcrest.number.OrderingComparison.<T>lessThanOrEqualTo(value);
  }

  /**
   * Creates a matcher of {@link String} that matches when the examined string is equal to
   * the specified expectedString, ignoring case.
   * <p/>
   * For example:
   * <pre>assertThat("Foo", equalToIgnoringCase("FOO"))</pre>
   *
   * @param expectedString
   *     the expected value of matched strings
   */
  public static Matcher<java.lang.String> equalToIgnoringCase(final java.lang.String expectedString) {
    return org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase(expectedString);
  }

  /**
   * Creates a matcher of {@link String} that matches when the examined string is equal to
   * the specified expectedString, when whitespace differences are (mostly) ignored.  To be
   * exact, the following whitespace rules are applied:
   * <ul>
   *   <li>all leading and trailing whitespace of both the expectedString and the examined string are ignored</li>
   *   <li>any remaining whitespace, appearing within either string, is collapsed to a single space before comparison</li>
   * </ul>
   * <p/>
   * For example:
   * <pre>assertThat("   my\tfoo  bar ", equalToIgnoringWhiteSpace(" my  foo bar"))</pre>
   *
   * @param expectedString
   *     the expected value of matched strings
   */
  public static Matcher<java.lang.String> equalToIgnoringWhiteSpace(final java.lang.String expectedString) {
    return org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(expectedString);
  }

  /**
   * Creates a matcher of {@link String} that matches when the examined string has zero length.
   * <p/>
   * For example:
   * <pre>assertThat("", isEmptyString())</pre>
   */
  public static Matcher<java.lang.String> isEmptyString() {
    return org.hamcrest.text.IsEmptyString.isEmptyString();
  }

  /**
   * Creates a matcher of {@link String} that matches when the examined string is <code>null</code>, or
   * has zero length.
   * <p/>
   * For example:
   * <pre>assertThat(((String)null), isEmptyString())</pre>
   */
  public static Matcher<java.lang.String> isEmptyOrNullString() {
    return org.hamcrest.text.IsEmptyString.isEmptyOrNullString();
  }

  /**
   * Creates a matcher of {@link String} that matches when the examined string contains all of
   * the specified substrings, regardless of the order of their appearance.
   * <p/>
   * For example:
   * <pre>assertThat("myfoobarbaz", stringContainsInOrder(Arrays.asList("bar", "foo")))</pre>
   *
   * @param substrings
   *     the substrings that must be contained within matching strings
   */
  public static Matcher<java.lang.String> stringContainsInOrder(final Iterable<java.lang.String> substrings) {
    return org.hamcrest.text.StringContainsInOrder.stringContainsInOrder(substrings);
  }

  /**
   * Creates a matcher that matches any examined object whose <code>toString</code> method
   * returns a value that satisfies the specified matcher.
   * <p/>
   * For example:
   * <pre>assertThat(true, hasToString(equalTo("TRUE")))</pre>
   *
   * @param toStringMatcher
   *     the matcher used to verify the toString result
   */
  public static <T> Matcher<T> hasToString(final Matcher<? super java.lang.String> toStringMatcher) {
    return org.hamcrest.object.HasToString.<T>hasToString(toStringMatcher);
  }

  /**
   * Creates a matcher that matches any examined object whose <code>toString</code> method
   * returns a value equalTo the specified string.
   * <p/>
   * For example:
   * <pre>assertThat(true, hasToString("TRUE"))</pre>
   *
   * @param expectedToString
   *     the expected toString result
   */
  public static <T> Matcher<T> hasToString(final java.lang.String expectedToString) {
    return org.hamcrest.object.HasToString.<T>hasToString(expectedToString);
  }

  /**
   * Creates a matcher of {@link Class} that matches when the specified baseType is
   * assignable from the examined class.
   * <p/>
   * For example:
   * <pre>assertThat(Integer.class, typeCompatibleWith(Number.class))</pre>
   *
   * @param baseType
   *     the base class to examine classes against
   */
  public static <T> Matcher<java.lang.Class<?>> typeCompatibleWith(final java.lang.Class<T> baseType) {
    return org.hamcrest.object.IsCompatibleType.<T>typeCompatibleWith(baseType);
  }

  /**
   * Creates a matcher of {@link java.util.EventObject} that matches any object
   * derived from <var>eventClass</var> announced by <var>source</var>.
   * </p>
   * For example:
   * <pre>assertThat(myEvent, is(eventFrom(PropertyChangeEvent.class, myBean)))</pre>
   *
   * @param eventClass
   *     the class of the event to match on
   * @param source
   *     the source of the event
   */
  public static Matcher<java.util.EventObject> eventFrom(final java.lang.Class<? extends java.util.EventObject> eventClass, final java.lang.Object source) {
    return org.hamcrest.object.IsEventFrom.eventFrom(eventClass, source);
  }

  /**
   * Creates a matcher of {@link java.util.EventObject} that matches any EventObject
   * announced by <var>source</var>.
   * </p>
   * For example:
   * <pre>assertThat(myEvent, is(eventFrom(myBean)))</pre>
   *
   * @param source
   *     the source of the event
   */
  public static Matcher<java.util.EventObject> eventFrom(final java.lang.Object source) {
    return org.hamcrest.object.IsEventFrom.eventFrom(source);
  }

  /**
   * Creates a matcher that matches when the examined object has a JavaBean property
   * with the specified name.
   * <p/>
   * For example:
   * <pre>assertThat(myBean, hasProperty("foo"))</pre>
   *
   * @param propertyName
   *     the name of the JavaBean property that examined beans should possess
   */
  public static <T> Matcher<T> hasProperty(final java.lang.String propertyName) {
    return org.hamcrest.beans.HasProperty.<T>hasProperty(propertyName);
  }

  /**
   * Creates a matcher that matches when the examined object has a JavaBean property
   * with the specified name whose value satisfies the specified matcher.
   * <p/>
   * For example:
   * <pre>assertThat(myBean, hasProperty("foo", equalTo("bar"))</pre>
   *
   * @param propertyName
   *     the name of the JavaBean property that examined beans should possess
   * @param valueMatcher
   *     a matcher for the value of the specified property of the examined bean
   */
  public static <T> Matcher<T> hasProperty(final java.lang.String propertyName, final Matcher<?> valueMatcher) {
    return org.hamcrest.beans.HasPropertyWithValue.<T>hasProperty(propertyName, valueMatcher);
  }

  /**
   * Creates a matcher that matches when the examined object has values for all of
   * its JavaBean properties that are equal to the corresponding values of the
   * specified bean.
   * <p/>
   * For example:
   * <pre>assertThat(myBean, samePropertyValuesAs(myExpectedBean))</pre>
   *
   * @param expectedBean
   *     the bean against which examined beans are compared
   */
  public static <T> Matcher<T> samePropertyValuesAs(final T expectedBean) {
    return org.hamcrest.beans.SamePropertyValuesAs.<T>samePropertyValuesAs(expectedBean);
  }

  /**
   * Creates a matcher of {@link org.w3c.dom.Node}s that matches when the examined node contains a node
   * at the specified <code>xPath</code> within the specified namespace context, with any content.
   * <p/>
   * For example:
   * <pre>assertThat(xml, hasXPath("/root/something[2]/cheese", myNs))</pre>
   *
   * @param xPath
   *     the target xpath
   * @param namespaceContext
   *     the namespace for matching nodes
   */
  public static Matcher<org.w3c.dom.Node> hasXPath(final java.lang.String xPath, final javax.xml.namespace.NamespaceContext namespaceContext) {
    return org.hamcrest.xml.HasXPath.hasXPath(xPath, namespaceContext);
  }

  /**
   * Creates a matcher of {@link org.w3c.dom.Node}s that matches when the examined node contains a node
   * at the specified <code>xPath</code>, with any content.
   * <p/>
   * For example:
   * <pre>assertThat(xml, hasXPath("/root/something[2]/cheese"))</pre>
   *
   * @param xPath
   *     the target xpath
   */
  public static Matcher<org.w3c.dom.Node> hasXPath(final java.lang.String xPath) {
    return org.hamcrest.xml.HasXPath.hasXPath(xPath);
  }

  /**
   * Creates a matcher of {@link org.w3c.dom.Node}s that matches when the examined node has a value at the
   * specified <code>xPath</code>, within the specified <code>namespaceContext</code>, that satisfies
   * the specified <code>valueMatcher</code>.
   * <p/>
   * For example:
   * <pre>assertThat(xml, hasXPath("/root/something[2]/cheese", myNs, equalTo("Cheddar")))</pre>
   *
   * @param xPath
   *     the target xpath
   * @param namespaceContext
   *     the namespace for matching nodes
   * @param valueMatcher
   *     matcher for the value at the specified xpath
   */
  public static Matcher<org.w3c.dom.Node> hasXPath(final java.lang.String xPath, final NamespaceContext namespaceContext, final Matcher<java.lang.String> valueMatcher) {
    return org.hamcrest.xml.HasXPath.hasXPath(xPath, namespaceContext, valueMatcher);
  }

  /**
   * Creates a matcher of {@link org.w3c.dom.Node}s that matches when the examined node has a value at the
   * specified <code>xPath</code> that satisfies the specified <code>valueMatcher</code>.
   * <p/>
   * For example:
   * <pre>assertThat(xml, hasXPath("/root/something[2]/cheese", equalTo("Cheddar")))</pre>
   *
   * @param xPath
   *     the target xpath
   * @param valueMatcher
   *     matcher for the value at the specified xpath
   */
  public static Matcher<org.w3c.dom.Node> hasXPath(final java.lang.String xPath, final Matcher<java.lang.String> valueMatcher) {
    return org.hamcrest.xml.HasXPath.hasXPath(xPath, valueMatcher);
  }

  public static Matcher<Object> hasClass(final Class<?> clazz) {
    return new TypeSafeMatcher<Object>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText(format("An object of {}.", clazz));
      }
      @Override
      protected void describeMismatchSafely(final Object item, final Description mismatchDescription) {
        mismatchDescription.appendText(format("an object of {}", item.getClass()));
      }
      @Override
      protected boolean matchesSafely(final Object obj) {
        return obj.getClass()==clazz;
      }
    };
  }

  public static Matcher<Bytes> isBytesWithSize(final long size) {
    return new TypeSafeMatcher<Bytes>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText(format("Bytes with size {}.", size));
      }
      @Override
      protected boolean matchesSafely(final Bytes bytes) {
        return bytes.longSize()==size;
      }
    };
  }


  public static Matcher<Bytes> isBytes(final String hex) {
    final Bytes expected = ByteUtils.parseHex(hex);
    return new TypeSafeMatcher<Bytes>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText(format("Bytes with content {}.", expected));
      }
      @Override
      protected boolean matchesSafely(final Bytes bytes) {
        return bytes.equals(expected);
      }
    };
  }

  public static <T> Matcher<T> meets(final Predicate<? super T> predicate) {
    return meets(predicate, Object::toString);
  }

  public static <T> Matcher<T> meets(final Predicate<? super T> predicate, final Function<? super T, String> message) {
    return new TypeSafeMatcher<T>(){
      @Override
      protected boolean matchesSafely(final T item) {
        return predicate.test(item);
      }
      @Override
      public void describeTo(final Description description) {
      }
      @Override
      protected void describeMismatchSafely(final T item, final Description mismatchDescription) {
        mismatchDescription.appendText(message.apply(item));
      }
    };
  }



}
