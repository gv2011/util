package com.github.gv2011.util.html.imp;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class HtmlBuilderImpTest {

  @Test
  public void testBuild() {
    final String html = new HtmlBuilderImp()
      .setTitle("title1")
      .build().toString()
    ;
    assertThat(html, is(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
      +   " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
      + "<head><title>title1</title></head>\n"
      + "<body/>\n"
      + "</html>"
    ));
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
