package com.github.gv2011.util.streams;

import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.Ignore;
import org.junit.Test;

import com.github.gv2011.testutil.AbstractTest;

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
      new URI("https://docs.aws.amazon.com/aws-sdk-php/v3/guide/getting-started/basic-usage.html").toURL()::openStream,
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
