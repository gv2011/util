package com.github.gv2011.jacksonadapter;

import static com.github.gv2011.testutil.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gv2011.util.beans.Bean;

public class ImmutableBeansModuleTest {

  private final ImmutableBeansModule module = new ImmutableBeansModule();
  private final ObjectMapper mapper;

  public ImmutableBeansModuleTest() {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(module);
      this.mapper = mapper;
  }

  public static interface BeanB extends Bean{
      String propA();
      Integer propB();
  }

    @Test
    public void testSerialize() throws JsonProcessingException {
        final BeanB beanB = createBeanB();

        final String serialized = mapper
            .writerFor(BeanB.class)
            .writeValueAsString(beanB)
        ;
        assertThat(serialized, is("{\"propA\":\"value1\",\"propB\":2}"));
    }

    private BeanB createBeanB() {
        return new BeanB() {
            @Override
            public String propA() {
                return "value1";
            }
            @Override
            public Integer propB() {
                return 2;
            }
        };
    }

    @Test
    public void testDeserialize() throws IOException {
        final Object value = mapper
        .readerFor(BeanB.class)
        .readValue("{\"propA\":\"value1\",\"propB\":2}");
        assertThat(value, is(createBeanB()));
    }

}
