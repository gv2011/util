package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.pathOf;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;

import com.github.gv2011.util.html.HtmlDocument;

public final class DirectoryFormatterTest {

  private static final Logger LOG = getLogger(DirectoryFormatterTest.class);

  @Test
  public void test(){
    final HtmlDocument doc = new DirectoryFormatter().format(
      pathOf("host","dir-1",""),
      Stream.of(pair(false,"Süß"), pair(false,"with/Slash"), pair(true,"dir-2"))
    );
    final String expected =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
      "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
      + "<head><title>dir-1</title></head>\n"
      + "<body>\n"
      + "<div><a href=\"../\">host</a>/dir-1</div>\n"
      + "<div>f <a href=\"S%C3%BC%C3%9F\">Süß</a> <a href=\"S%C3%BC%C3%9F?txt=true\">as text</a></div>\n"
      + "<div>f <a href=\"with%2FSlash\">with/Slash</a> <a href=\"with%2FSlash?txt=true\">as text</a></div>\n"
      + "<div>d <a href=\"dir-2/\">dir-2</a></div>\n"
      + "</body>\n"
      + "</html>"
    ;
    final String html = doc.toString();
    if(!html.equals(expected)){
      LOG.error("Expected:\n{}\nActual:\n{}", expected, html);
      fail();
    }
  }

}
