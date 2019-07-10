package com.github.gv2011.jacksonadapter;

import static com.github.gv2011.testutil.Assert.assertThat;
/*-
 * #%L
 * jacksonadapter
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
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
