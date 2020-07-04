package com.github.gv2011.util.time;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vinz (https://github.com/gv2011)
 * @deprecated Use {@link LocalDate} instead.
 */
@Deprecated(forRemoval=true)
public class IsoDay implements Comparable<IsoDay>{

  private static final Pattern PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
  private static final Pattern DD_MM_YYYY = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{4})");

  public static IsoDay parse(final CharSequence yyyyMmDd) {
    return new IsoDay(LocalDate.parse(yyyyMmDd));
  }

  public static final IsoDay fromDdMmYyyy(final String ddMmYyyy){
    final Matcher m = DD_MM_YYYY.matcher(ddMmYyyy);
    if(!m.matches()) throw new IllegalArgumentException();
    return parse(m.group(3)+"-"+m.group(2)+"-"+m.group(1));
  }

  private final LocalDate isoDay;

  private IsoDay(final LocalDate isoDay) {
    assert PATTERN.matcher(isoDay.toString()).matches();
    this.isoDay = isoDay;
  }

  public byte day() {
    return (byte) isoDay.getDayOfMonth();
  }

  public byte month() {
    return (byte) isoDay.getMonthValue();
  }

  public short year() {
    return (short) isoDay.getYear();
  }

  @Override
  public int hashCode() {
    return isoDay.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(!(obj instanceof IsoDay)) return false;
    else return isoDay.equals(((IsoDay)obj).isoDay);
  }

  @Override
  public String toString() {
    return isoDay.toString();
  }

  @Override
  public int compareTo(final IsoDay o) {
    return isoDay.compareTo(o.isoDay);
  }

  public long daysSince(final IsoDay o){
    return isoDay.toEpochDay()-o.isoDay.toEpochDay();
  }
}
