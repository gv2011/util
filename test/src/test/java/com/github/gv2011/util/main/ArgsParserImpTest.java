package com.github.gv2011.util.main;

import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.mapOf;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.gv2011.util.Alternative;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.main.ArtifactRef.ArtifactId;
import com.github.gv2011.util.tstr.TypedString;

public class ArgsParserImpTest {

	@Test
	public void testParse() {
		final Alternative<StartArgs, String> result = new ArgsParserImp().parse(
			StartArgs.class, 
			listOf(
				"--artifactId", "\"la\\\"la\"",
				"-c", "zip"
			)
		);
		assertTrue(result.isA());
		assertThat(result.getA().artifact().artifactId(), is(TypedString.create(ArtifactId.class, "la\"la")));
		assertThat(result.getA().artifact().classifier(), is("zip"));
	}
	
	@Test
	public void testGetShortKeys() {
		assertThat(
			new ArgsParserImp().getShortKeys(setOf("anna", "anton", "bärbel", "claus", "d")),
			is(ICollections.<Character,String>mapBuilder()
				.put('b', "bärbel")
				.put('c', "claus")
				.build()
			)
		);
	}
	
	@Test
	public void testAsMap() {
		IMap<Character, String> shortKeys = mapOf('a', "anton");
		final IMap<String, Opt<String>> map = new ArgsParserImp().asMap(listOf("-a", "v1", "--berta", "--c"), shortKeys);
		assertThat(map.size(), is(3));
		assertThat(map.get("anton"), is(Opt.of("v1")));
		assertThat(map.get("berta"), is(Opt.empty()));
		assertThat(map.get("c"), is(Opt.empty()));
	}
	
	@Test
	public void testFlattenProperties() {
		final TypeRegistry reg = BeanUtils.typeRegistry();
		final BeanType<StartArgs> type = reg.beanType(StartArgs.class);
		final IMap<String, Property<?>> flatProps = new ArgsParserImp().flattenProperties(type);
		flatProps.entrySet().forEach(System.out::println);
	}

}
