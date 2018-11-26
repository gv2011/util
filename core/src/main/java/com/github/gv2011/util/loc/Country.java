package com.github.gv2011.util.loc;

import static com.github.gv2011.util.icol.ICollections.asSet;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.time.LocalDate;

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

import java.util.Arrays;
import java.util.Locale;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.tstr.AbstractTypedString;

@SuppressWarnings("unused")
public class Country extends AbstractTypedString<Country>{

  private final static ISortedMap<String,Country> COUNTRIES;

  static{
    COUNTRIES = Arrays.stream(Locale.getISOCountries()).collect(toISortedMap(k->k,k->{
      final String name = new Locale(Language.ENGLISH.getLanguage(), k).getDisplayCountry(Language.ENGLISH);
      final ISet<String> eu = asSet(new String[]{
        "BE","BG","DK","DE","EE","FI","FR","GR","IE","IT","HR","LV","LT","LU",
        "MT","NL","AT","PL","PT","RO","SE","SK","SI","ES","CZ","HU","CY"}
      );
      final ISet<String> ecsc = asSet(new String[]{"BE", "DE", "FR", "IT", "LU", "NL"});
      final LocalDate escscStart = LocalDate.parse("1952-07-23");

      final ISet<String> weu = asSet(new String[]{"GB", "BE", "FR", "LU", "NL"});
      final LocalDate weuStart = LocalDate.parse("1948-08-25");

      final ISet<String> efta = asSet(new String[]{"IS", "LI", "NO", "CH"});
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
  public Country self() {
    return this;
  }

  @Override
  public Class<Country> clazz() {
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

  public boolean euMember(final LocalDate time){
    return euMember;
  }

  public boolean eftaMember(){
    return eftaMember;
  }

}
