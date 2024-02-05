package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.nothing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class NothingTest {

	@Test
	public void testParse() {
		assertThat(Nothing.parse(nothing().toString()), is(nothing()));
	}

	@Test
	public void testToString() {
		assertThat(nothing().toString(), is("Nothing"));
	}

}
