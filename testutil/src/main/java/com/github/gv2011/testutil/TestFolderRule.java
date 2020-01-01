package com.github.gv2011.testutil;

/*-
 * #%L
 * util-test
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

import static com.github.gv2011.util.FileUtils.WORK_DIR;
import static com.github.gv2011.util.ResourceUtils.tryGetResourceUrl;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.listBuilder;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;

import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.Zipper;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.time.Clock;

public class TestFolderRule implements TestRule{

  public static final TestFolderRule create(){return new TestFolderRule(()->{});}
  public static final TestFolderRule create(final ThrowingRunnable after){return new TestFolderRule(after);}

  private static final Logger LOG = getLogger(TestFolderRule.class);

  private final CachedConstant<Path> testFolder = Constants.cachedConstant(this::createTestFolder);
  private final CachedConstant<Description> testDescription = Constants.cachedConstant();
  private final CachedConstant<Boolean> testFolderCreated = Constants.cachedConstant(()->false);
  private volatile boolean dontDeleteTestFolder;

  private final ThrowingRunnable after;

  private TestFolderRule(final ThrowingRunnable after){
    this.after = after;
  }

  public Path testFolder(){
    return testFolder.get();
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    testDescription.set(description);
    return new Statement(){
      @Override
      public void evaluate() throws Throwable {
        boolean success = false;
        try{
          try{base.evaluate();}
          finally{after.run();}
          success=true;
          finished(description, true);
        }
        finally{
          if(!success) finished(description, false);
        }
      }
    };
  }

  private void finished(final Description description, final boolean successful) {
    if(successful && !dontDeleteTestFolder && testFolderCreated.get()){
      final Path folder = testFolder.get();
      try {
        FileUtils.delete(folder);
      } catch (final Exception e) {
        LOG.warn(format("Could not delete test folder {}.", folder), e);
      }
    }
  }

  private final Path createTestFolder(){
    testFolderCreated.set(true);
    final Class<?> testClass = testDescription.get().getTestClass();
    int tryNo = 1;
    Opt<Path> result = Opt.empty();
    while(result.isEmpty()){
      final Path testFolder = getPath(testClass, tryNo);
      try {
        if(!FileUtils.exists(testFolder)){
          call(()->Files.createDirectories(testFolder));
          LOG.debug("Test {}: Created test folder {}.", testClass.getName(), result);
        }else{
          LOG.debug("Test {}: Cleaning test folder {}.", testClass.getName(), result);
          FileUtils.deleteContents(testFolder);
        }
        prepareTestFolder(testFolder);
        result = Opt.of(testFolder);
      } catch (final Exception e) {
        if(tryNo>10) throw e;
        else{
          LOG.warn("Trying again.", e);
          Clock.get().sleep(Duration.ofMillis(10));
        }
      }
      tryNo++;
    }
    return result.get();
  }
  private Path getPath(final Class<?> testClass, final int tryNo) {
    Path result;
    final String name = testClass.getName() + (tryNo<=1?"":tryNo);
    if(WORK_DIR.getFileName().toString().equals("surefire-work")){
      result = WORK_DIR.resolve(name);
    }else{
      result = WORK_DIR.resolve("target/tests").resolve(name);
    }
    return result;
  }

  private void prepareTestFolder(final Path testFolder) {
    verify(FileUtils.isEmpty(testFolder));
    final Class<?> testClass = testDescription.get().getTestClass();
    final Opt<URL> zip = tryGetResourceUrl(testClass, testClass.getSimpleName()+".zip");
    zip.ifPresent(url->{
      final Builder<Path> pathCollector = listBuilder();
      Zipper.newZipper().unZip(url::openStream, testFolder, pathCollector::add);
      final IList<Path> paths = pathCollector.build();
      assertTrue(paths.stream().allMatch(Files::exists));
      assertTrue(paths.stream().allMatch(p->testFolder.equals(p.getParent())));
    });
  }

  public void dontDeleteTestFolder() {
    dontDeleteTestFolder = true;
  }


}
