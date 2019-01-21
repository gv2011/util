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
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

  private static final Pattern HOURS = Pattern.compile("(-?\\d+)(:([0-5]\\d)(:(([0-5]\\d)([,\\.](\\d+))?))?)?");
  private static final double NANOS_PER_SECOND = ChronoUnit.SECONDS.getDuration().toNanos();

  private TimeUtils(){staticClass();}

  public static void await(final Instant instant){
    Clock.INSTANCE.get().await(instant);
  }

  public static Duration parseHours(final String withColons) {
    final Matcher matcher = HOURS.matcher(withColons);
    verify(withColons, t->matcher.matches());
    final int hours = Integer.parseInt(matcher.group(1));
    final int minutes = Optional.ofNullable(matcher.group(3)).map(Integer::parseInt).orElse(0);
    final double seconds = matcher.group(5) == null
      ? 0d
      : Double.parseDouble(matcher.group(6))
    ;
    final Duration d =
      Duration.ofNanos((long)(seconds * NANOS_PER_SECOND))
      .plus(minutes, ChronoUnit.MINUTES)
      .plus(Math.abs(hours), ChronoUnit.HOURS)
    ;
    return hours>=0 ? d : d.negated();
  }

  public static double toSeconds(final Duration time) {
    double result = time.getSeconds();
    result += ((double)time.getNano()) / NANOS_PER_SECOND;
    return result;
  }

  public static String fileSafeFormat(final Instant instant) {
    return instant.toString().replace(':', '.');
  }

  public static String fileSafeInstant() {
    return fileSafeFormat(Instant.now());
  }

  public static boolean olderThan(final Temporal instant, final Duration duration) {
    return Duration.between(instant, Instant.now()).compareTo(duration) > 0;
  }
}
