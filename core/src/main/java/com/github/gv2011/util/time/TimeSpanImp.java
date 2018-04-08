package com.github.gv2011.util.time;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.Verify.verify;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TimeSpanImp implements TimeSpan {

  private static final Pattern SYNTAX = Pattern.compile("\\(([0-9TZ.:-]+,([0-9TZ.:-]+)\\)");

  private final Instant from;
  private final Instant until;

  static TimeSpan parse(final CharSequence chars) {
    final Matcher matcher = SYNTAX.matcher(chars);
    verify(matcher, Matcher::matches);
    return new TimeSpanImp(Instant.parse(matcher.group(1)), Instant.parse(matcher.group(2)));
  }

  TimeSpanImp(final Instant from, final Instant until) {
    this.from = from;
    this.until = until;
  }

  @Override
  public Instant from() {
    return from;
  }

  @Override
  public Instant until() {
    return until;
  }

  @Override
  public TimeSpan self() {
    return this;
  }

}
