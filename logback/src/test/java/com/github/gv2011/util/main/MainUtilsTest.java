package com.github.gv2011.util.main;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.ex.Exceptions.call;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.main.MainUtils.ServiceBuilder;
import com.github.gv2011.util.time.SimpleLatch;

public class MainUtilsTest {

  public static void main(final String[] args) throws IOException{
    // Files.deleteIfExists(Paths.get("logback.xml"));
    final MainUtils mainUtils = MainUtils.create(args, new TestServiceBuilder(), Nothing.class);
    new Thread(()->{
      call(()->Thread.sleep(3000));
      getLogger(MainUtilsTest.class).info("Modify");
      call(()->Files.write(Paths.get("logback.xml"), "\n".getBytes(UTF_8), StandardOpenOption.APPEND));
      call(()->Thread.sleep(3000));
      getLogger(MainUtilsTest.class).info("Modify 2");
      call(()->Files.write(Paths.get("logback.xml"), "\n".getBytes(UTF_8), StandardOpenOption.APPEND));
      call(()->Thread.sleep(3000));
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
    public TestService startService(final Nothing configuration) {
      return new TestService();
    }
  }

}
