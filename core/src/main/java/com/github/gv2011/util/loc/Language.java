package com.github.gv2011.util.loc;

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
