package com.github.gv2011.util.bytes;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileBytesPtest {

  private Bytes bytes;
  private Path file;
  private FileBytes fileBytes;

  @Before
  public void setup(){
    bytes = ByteUtils.newRandomBytes(100000);
    file = FileSystems.getDefault().getPath("test.bin");
    bytes.write(file);
    fileBytes = ByteUtils.newFileBytes(file);
  }

  @After
  public void close() throws IOException{
    Files.deleteIfExists(file);
  }


  @Test
  public void testGetLong() {
    final Duration testDuration = Duration.ofSeconds(10);
    final Instant end = Instant.now().plus(testDuration);
    long count = 0;
    long i = 0;
    while(Instant.now().isBefore(end)){
      assertThat(fileBytes.get(i), is(bytes.get(i)));
      i++; if(i>=bytes.size()) i=0;
      count++;
    }
    System.out.println(testDuration.dividedBy(count).toNanos()+" ns per read");
  }

}
