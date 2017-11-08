package com.github.gv2011.util;

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



import static com.github.gv2011.util.StringUtils.alignRight;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigInteger;
import java.util.UUID;

public class IntUtils {

private IntUtils(){staticClass();}

static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);
static final BigInteger TWO_POW_128 = BigInteger.ONE.shiftLeft(128);
static final BigInteger ONES_64 = TWO_POW_64.subtract(BigInteger.ONE);


public static BigInteger toInt(final UUID uuid) {
	BigInteger i = unsigned(uuid.getMostSignificantBits());
	i = i.shiftLeft(64).add(unsigned(uuid.getLeastSignificantBits()));
	return i;
}

public static UUID toUUID(final BigInteger i) {
	if(i.signum()<0) throw new IllegalArgumentException();
	if(i.compareTo(TWO_POW_128)>=0) throw new IllegalArgumentException();
	final long leastSignificant = i.and(ONES_64).longValue();
	final long mostSignificant = i.shiftRight(64).and(ONES_64).longValue();
	return new UUID(mostSignificant, leastSignificant);
}

public static BigInteger unsigned(final long unsignedLong) {
	BigInteger i = BigInteger.valueOf(unsignedLong);
	if(i.signum()<0) i = i.add(TWO_POW_64);
	return i;
}


public static String toHex(final BigInteger i, final int bytes) {
	if(i.signum()<0) throw new IllegalArgumentException();
	return alignRight(i.toString(16), bytes*2, '0');
}

}
