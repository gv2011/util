package com.github.gv2011.testutil;

import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.FileUtils.WORK_DIR;
import static com.github.gv2011.util.ResourceUtils.tryGetResourceUrl;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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

public class TestFolderRule implements TestRule{

  public static final TestFolderRule create(){return new TestFolderRule(()->{});}
  public static final TestFolderRule create(final ThrowingRunnable after){return new TestFolderRule(after);}

  private static final Logger LOG = getLogger(TestFolderRule.class);

  private final CachedConstant<Path> testFolder = Constants.cachedConstant(this::createTestFolder);
  private final CachedConstant<Description> testDescription = Constants.cachedConstant();
  private final CachedConstant<Boolean> testFolderCreated = Constants.cachedConstant(()->false);

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
    if(successful && testFolderCreated.get()){
      FileUtils.delete(testFolder.get());
    }
  }

  private final Path createTestFolder(){
    testFolderCreated.set(true);
    Path result;
    final Class<?> testClass = testDescription.get().getTestClass();
    final String name = testClass.getName();
    if(WORK_DIR.getFileName().toString().equals("surefire-work")){
      result = WORK_DIR.resolve(name);
    }else{
      result = WORK_DIR.resolve("target/tests").resolve(name);
    }
    if(!result.toFile().exists()){
      int i=0;
      while(i<1000){
        try{
          call(()->result.toFile().mkdirs());
          i=1000;
        }catch(final RuntimeException e){
          i++;
          if(i==1000) throw e;
          else run(()->Thread.sleep(10));
        }
      }
      LOG.debug("Test {}: Created test folder {}.", testClass.getName(), result);
    }else{
      LOG.debug("Test {}: Cleaning test folder {}.", testClass.getName(), result);
      FileUtils.deleteContents(result);
    }
    prepareTestFolder(result);
    return result;
  }

  private void prepareTestFolder(final Path testFolder) {
    final Class<?> testClass = testDescription.get().getTestClass();
    final Optional<URL> zip = tryGetResourceUrl(testClass, testClass.getSimpleName()+".zip");
    zip.ifPresent(url->{
      final Builder<Path> pathCollector = iCollections().listBuilder();
      Zipper.newZipper().unZip(url::openStream, testFolder, pathCollector::add);
      final IList<Path> paths = pathCollector.build();
      assertTrue(paths.stream().allMatch(Files::exists));
      assertTrue(paths.stream().allMatch(p->testFolder.equals(p.getParent())));
    });
   }


}
