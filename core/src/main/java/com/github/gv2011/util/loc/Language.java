package com.github.gv2011.util.loc;

/*-
 * #%L
 * The MIT License (MIT)
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




import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;
import static com.github.gv2011.util.CollectionUtils.toISortedSet;

import java.util.Arrays;
import java.util.Locale;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.tstr.TypedString;

public class Language extends TypedString<Language>{

  static final Locale ENGLISH = Locale.UK;
  private final static ISortedMap<String,Language> LANGUAGES;

  static{
    LANGUAGES = Arrays.stream(Locale.getISOLanguages())
      .map(i2->new Locale(i2).getISO3Language())
      .collect(toISet())
      .stream()
      .map(i3->{
        return pair(i3, Locale.forLanguageTag(i3));
      })
      .collect(toISortedMap(
        Pair::getKey,
        p->new Language(p.getKey(), p.getValue().getDisplayLanguage(ENGLISH))
      ));
  }

  public static final Language language(final String iso639_2, final String name){
    return LANGUAGES.get(iso639_2);
  }

  public static final ISortedSet<Language> languages(){
    return LANGUAGES.values().stream().collect(toISortedSet());
  };


  private final String iso639_2;
  private final String name;

  Language(final String iso639_2, final String name) {
    this.iso639_2 = iso639_2;
    this.name = name;
  }

  @Override
  protected Language self() {
    return this;
  }

  @Override
  protected Class<Language> clazz() {
    return Language.class;
  }

  @Override
  public String toString() {
    return iso639_2;
  }

  public String name() {
    return name;
  }

  public Locale locale() {
    return Locale.forLanguageTag(iso639_2);
  }

  public String name(final Locale locale) {
    return locale().getDisplayLanguage(locale);
  }

}
