package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.CollectionUtils.listOf;
import static com.github.gv2011.util.CollectionUtils.pair;

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
import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonUtils;

public class HtmlBuilderImpTest {

  @Test
  public void testBuild() {
    String html = new HtmlBuilderImp()
      .setTitle("title1")
      .build().toString()
    ;
    html = html.replace(" />", "/>");
    final String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    final String remaining =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
       +   " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
       + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
       + "<head><title>title1</title></head>\n"
       + "<body/>\n"
       + "</html>"
     ;
    if(!(
      html.equals(prefix+remaining) ||
      html.equals(prefix+"\n"+remaining)
    )) {
      final JsonFactory jf = JsonUtils.jsonFactory();
      final JsonObject msg = listOf(pair("expected", prefix+remaining), pair("actual", html)).stream()
        .collect(jf.toJsonObject(Pair::getKey, p->jf.primitive(p.getValue())));
      fail(msg.serialize());
    };
    System.out.println(html);
  }

  @Test
  public void testLogin() {
    final HtmlBuilderImp htmlBuilder = new HtmlBuilderImp();
    htmlBuilder
      .setTitle("title1")
      .addForm()
        .addTextField()
          .setName("username")
        .close()
        .addTextField()
          .setName("password")
          .setPassword()
        .close()
        .addSubmit()
      .close()
    ;
    final String html = htmlBuilder.build().toString();
    System.out.println(html);
  }


}
