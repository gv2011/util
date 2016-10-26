package com.github.gv2011.util;

import static com.github.gv2011.util.StringUtils.alignRight;

import java.math.BigInteger;
import java.util.UUID;

public class IntUtils {
	
static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);
static final BigInteger TWO_POW_128 = BigInteger.ONE.shiftLeft(128);
static final BigInteger ONES_64 = TWO_POW_64.subtract(BigInteger.ONE);

	
public static BigInteger toInt(UUID uuid) {
	BigInteger i = unsigned(uuid.getMostSignificantBits());
	i = i.shiftLeft(64).add(unsigned(uuid.getLeastSignificantBits()));
	return i;
}

public static UUID toUUID(BigInteger i) {
	if(i.signum()<0) throw new IllegalArgumentException();
	if(i.compareTo(TWO_POW_128)>=0) throw new IllegalArgumentException();
	long leastSignificant = i.and(ONES_64).longValue();
	long mostSignificant = i.shiftRight(64).and(ONES_64).longValue();
	return new UUID(mostSignificant, leastSignificant);
}

public static BigInteger unsigned(long unsignedLong) {
	BigInteger i = BigInteger.valueOf(unsignedLong);
	if(i.signum()<0) i = i.add(TWO_POW_64);
	return i;
}


public static String toHex(BigInteger i, int bytes) {
	if(i.signum()<0) throw new IllegalArgumentException();
	return alignRight(i.toString(16), bytes*2, '0');
}

}
