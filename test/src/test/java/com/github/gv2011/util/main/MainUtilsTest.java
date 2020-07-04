package com.github.gv2011.util.main;

import static com.github.gv2011.util.ex.Exceptions.call;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.main.MainUtils.ServiceBuilder;
import com.github.gv2011.util.time.Clock;
import com.github.gv2011.util.time.SimpleLatch;

public class MainUtilsTest {

  public static void main(final String[] args){
    // Files.deleteIfExists(Paths.get("logback.xml"));
    final MainUtils mainUtils = MainUtils.create(args, new TestServiceBuilder(), Nothing.class);
    new Thread(()->{
      final Clock clock = Clock.INSTANCE.get();
      clock.sleep(Duration.ofSeconds(3));
      getLogger(MainUtilsTest.class).info("Modify");
      call(()->Files.write(Paths.get("logback.xml"), "\n".getBytes(UTF_8), StandardOpenOption.APPEND));
      clock.sleep(Duration.ofSeconds(3));
      getLogger(MainUtilsTest.class).info("Modify 2");
      call(()->Files.write(Paths.get("logback.xml"), "\n".getBytes(UTF_8), StandardOpenOption.APPEND));
      clock.sleep(Duration.ofSeconds(3));
      getLogger(MainUtilsTest.class).info("Closing");
      mainUtils.close();
    }).start();
    System.exit(mainUtils.runMain());
  }

  private static class TestService implements AutoCloseableNt{
    private final Thread thread;
    private final SimpleLatch latch = SimpleLatch.create();
    private TestService(){
      thread = new Thread(()->call(()->latch.await()));
      thread.start();
    }
    @Override
    public void close() {
      latch.release();
      call(()->thread.join());
    }
  }

  private static class TestServiceBuilder implements ServiceBuilder<TestService, Nothing> {
    @Override
    public TestService startService(final Nothing configuration, final Runnable shutdownTrigger) {
      return new TestService();
    }
  }

}
