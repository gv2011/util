package com.github.gv2011.util.loc;

import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;
import static com.github.gv2011.util.CollectionUtils.toISortedSet;

import java.util.Arrays;
import java.util.Locale;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.tstr.TypedString;

public class Country extends TypedString<Country>{

  private final static ISortedMap<String,Country> COUNTRIES;

  static{
    COUNTRIES = Arrays.stream(Locale.getISOCountries()).collect(toISortedMap(k->k,k->{
      final String name = new Locale(Language.ENGLISH.getLanguage(), k).getDisplayCountry(Language.ENGLISH);
      final ISet<String> eu = iCollections().asSet(new String[]{
        "BE","BG","DK","DE","EE","FI","FR","GR","IE","IT","HR","LV","LT","LU",
        "MT","NL","AT","PL","PT","RO","SE","SK","SI","ES","CZ","HU","CY"}
      );
      final ISet<String> efta = iCollections().asSet(new String[]{"IS", "LI", "NO", "CH"});
      return new Country(k, name, eu.contains(k), efta.contains(k));
    }));
  }

  public static final Country forIso3166(final String iso3166){
    return COUNTRIES.get(iso3166);
  };

  public static final ISortedSet<Country> countries(){
    return COUNTRIES.values().stream().collect(toISortedSet());
  };

  private final String iso3166;
  private final String name;
  private final boolean euMember;
  private final boolean eftaMember;

  Country(final String iso3166, final String name, final boolean euMember, final boolean eftaMember) {
    this.iso3166 = iso3166;
    this.name = name;
    this.euMember = euMember;
    this.eftaMember = eftaMember;
  }

  @Override
  protected Country self() {
    return this;
  }

  @Override
  protected Class<Country> clazz() {
    return Country.class;
  }

  @Override
  public String toString() {
    return iso3166;
  }

  public String name() {
    return name;
  }

  public String name(final Locale locale) {
    return new Locale(Language.ENGLISH.getLanguage(), iso3166).getDisplayCountry(locale);
  }

  public boolean euMember(){
    return euMember;
  }

  public boolean eftaMember(){
    return eftaMember;
  }

}