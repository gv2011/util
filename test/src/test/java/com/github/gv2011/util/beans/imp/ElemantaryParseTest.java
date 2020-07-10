package com.github.gv2011.util.beans.imp;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeRegistry;

public class ElemantaryParseTest {
	
	public static interface TestBean extends Bean{
		String str();
	}

	@Test
	public void test() {
		final TypeRegistry typeRegistry = new DefaultTypeRegistry();
		final BeanType<TestBean> type = typeRegistry.beanType(TestBean.class);
		type.createBuilder().set(TestBean::str).to("test");
		final Type<?> strType = type.properties().values().single().type();
		assertThat(strType.getClass(), is(ElementaryTypeImp.class));
		System.out.println(strType.parse("test").getClass());
	}

}
