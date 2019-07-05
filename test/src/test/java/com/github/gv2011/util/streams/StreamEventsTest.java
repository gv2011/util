package com.github.gv2011.util.streams;

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

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.Ignore;
import org.junit.Test;

import com.github.gv2011.testutil.AbstractTest;
import com.github.gv2011.util.streams.StreamEvent;
import com.github.gv2011.util.streams.StreamEvents;

@Ignore//TODO
public class StreamEventsTest extends AbstractTest{

  private final CountDownLatch finished = new CountDownLatch(1);

  @Test
  public void testProcess() throws Exception {
    final Random r = new Random();
    final Path testFile = testFolder().resolve("testfile.txt");
    try(OutputStream out = Files.newOutputStream(testFile)){
      for(int i=0; i<400000; i++) out.write(r.nextInt(32));
    }
    StreamEvents.process(()->Files.newInputStream(testFile), this::event);
    finished.await();
  }

  @Test
  public void testProcessWeb() throws Exception {
    StreamEvents.process(
      new URL("https://docs.aws.amazon.com/aws-sdk-php/v3/guide/getting-started/basic-usage.html")::openStream,
      this::event
    );
    finished.await();
  }
  //
  private void event(final StreamEvent e){
    System.out.println(e.state()+" "+e.data().longSize());
    if(e.state().finished()) finished.countDown();
  }

}
