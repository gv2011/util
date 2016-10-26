package com.github.gv2011.util.main;

import static com.github.gv2011.util.ex.Exceptions.run;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.main.MainUtils.ServiceBuilder;

public class MainUtilsTest {

  public static void main(final String[] args){
    new Thread().start();
    final MainUtils mainUtils = new MainUtils();
    new Thread(()->{
      run(()->Thread.sleep(100));
      mainUtils.shutdown();
    }).start();
    mainUtils.runMain(args, new TestServiceBuilder());
    System.out.println(3);
  }

  private static class TestService implements AutoCloseableNt{
    @Override
    public void close() {}
  }

  private static class TestServiceBuilder implements ServiceBuilder<TestService> {
    @Override
    public void close() {}
    @Override
    public TestService startService(final String[] args) throws Exception {
      return new TestService();
    }

  }

}
