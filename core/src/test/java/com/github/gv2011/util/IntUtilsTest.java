package com.github.gv2011.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.UUID;

import org.junit.Test;
import static com.github.gv2011.util.IntUtils.*;

public class IntUtilsTest {

	@Test
	public void testToInt() {
		BigInteger expected = new BigInteger("ec0266da59485baeb829fedf58067a", 16);
		UUID uuid = UUID.fromString("00ec0266-da59-485b-aeb8-29fedf58067a");
		BigInteger i = toInt(uuid);
		assertThat(i, is(expected));
	}

	@Test
	public void testToUUID() {
		BigInteger i = new BigInteger("ec0266da59485baeb829fedf58067a", 16);
		UUID expected = UUID.fromString("00ec0266-da59-485b-aeb8-29fedf58067a");
		UUID uuid = toUUID(i);
		assertThat(uuid, is(expected));
	}

	@Test
	public void testUnsigned() {
		long l = Long.MAX_VALUE+1;
		assertThat(l, is(Long.MIN_VALUE));
		BigInteger expected = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
		BigInteger i = unsigned(l);
		assertThat(i, is(expected));
	}

	@Test
	public void testToHex() {
		String expected = "000000007fffffff";
		BigInteger i = BigInteger.valueOf(Integer.MAX_VALUE);
		String s = toHex(i,8);
		assertThat(s, is(expected));
	}

}
