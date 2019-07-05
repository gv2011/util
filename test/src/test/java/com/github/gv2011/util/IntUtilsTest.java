package com.github.gv2011.util;

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

import static org.hamcrest.Matchers.is;
import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.util.IntUtils.toInt;
import static com.github.gv2011.util.IntUtils.toUUID;
import static com.github.gv2011.util.IntUtils.unsigned;
import static com.github.gv2011.util.IntUtils.toHex;

import java.math.BigInteger;
import java.util.UUID;

import org.junit.Test;

public class IntUtilsTest {

	@Test
	public void testToInt() {
		final BigInteger expected = new BigInteger("ec0266da59485baeb829fedf58067a", 16);
		final UUID uuid = UUID.fromString("00ec0266-da59-485b-aeb8-29fedf58067a");
		final BigInteger i = toInt(uuid);
		assertThat(i, is(expected));
	}

	@Test
	public void testToUUID() {
		final BigInteger i = new BigInteger("ec0266da59485baeb829fedf58067a", 16);
		final UUID expected = UUID.fromString("00ec0266-da59-485b-aeb8-29fedf58067a");
		final UUID uuid = toUUID(i);
		assertThat(uuid, is(expected));
	}

	@Test
	public void testUnsigned() {
		final long l = Long.MAX_VALUE+1;
		assertThat(l, is(Long.MIN_VALUE));
		final BigInteger expected = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
		final BigInteger i = unsigned(l);
		assertThat(i, is(expected));
	}

	@Test
	public void testToHex() {
		final String expected = "000000007fffffff";
		final BigInteger i = BigInteger.valueOf(Integer.MAX_VALUE);
		final String s = toHex(i,8);
		assertThat(s, is(expected));
	}

}
