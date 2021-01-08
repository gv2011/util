package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.html.HtmlBuilder;
import com.github.gv2011.util.html.HtmlUtils;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonUtils;

public class HtmlBuilderImpTest {
  
  @Test
  public void testBuild() {
    String html = HtmlUtils.htmlFactory().newBuilder()
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
    }
    System.out.println(html);
  }

  @Test
  public void testLogin() {
    final HtmlBuilder htmlBuilder = HtmlUtils.htmlFactory().newBuilder();
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
