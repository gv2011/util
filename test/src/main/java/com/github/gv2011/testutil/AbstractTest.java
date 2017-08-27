package com.github.gv2011.testutil;

import static com.github.gv2011.util.ServiceLoaderUtils.loadService;
import static com.github.gv2011.util.StreamUtils.readText;
import static com.github.gv2011.util.bytes.ByteUtils.copyFromStream;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.nio.file.Path;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.slf4j.Logger;

import com.github.gv2011.jsoncore.JsonFactory;
import com.github.gv2011.jsoncore.JsonNode;
import com.github.gv2011.util.ResourceUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

public abstract class AbstractTest {

  private final TestFolderRule testFolderRule;
  protected final Logger log;


  protected AbstractTest(){
    log = getLogger(getClass());
    testFolderRule = TestFolderRule.create(this::after);
  }

  @Rule
  public final TestFolderRule testFolderRule(){
    return testFolderRule;
  }

  protected final Path testFolder(){
    return testFolderRule.testFolder();
  }

  protected void after(){}

  protected final String getResourceAsString(final String extension){
    return getResourceAsString(getClass(), extension);
  }

  protected final String getResourceAsString(final Class<?> base, final String extension){
    return readText(getResource(base, extension)::openStream);
  }

  protected final JsonNode getResourceAsJson(final String extension){
    return loadService(JsonFactory.class).newJsonReader().parse(getResource(extension)::openStream);
  }

  protected final URL getResource(final String extension){
    return getResource(getClass(), extension);
  }

  protected final Bytes getResourceBytes(final String extension){
    return ByteUtils.copyFromStream(getResource(getClass(), extension)::openStream);
  }

  protected final URL getResource(final Class<?> base, final String extension){
    return ResourceUtils.getResourceUrl(base, getLocalResourceName(base, extension));
  }

  private String getLocalResourceName(final Class<?> base, final String extension) {
    return base.getSimpleName()+"."+extension;
  }

  protected final Matcher<Bytes> isBytes(final String extension){
    return isBytes(getClass(), extension);
  }

  protected final Matcher<Bytes> isBytes(final Class<?> base, final String extension){
    final String local = getLocalResourceName(base, extension);
    final URL url = ResourceUtils.getResourceUrl(base, local);
    final Bytes expected = copyFromStream(url::openStream);
    return new TypeSafeMatcher<Bytes>(){
      @Override
      public void describeTo(final Description description) {
        description.appendText(format("A byte array with same content as resource {} ({}).", local, base));
      }
      @Override
      protected boolean matchesSafely(final Bytes bytes) {
        return bytes.equals(expected);
      }
    };
  }

}