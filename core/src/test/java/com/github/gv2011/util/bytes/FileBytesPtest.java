package com.github.gv2011.util.bytes;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileBytesPtest {

  private byte[] array;
  private Path file;
  private FileBytes fileBytes;
  private Bytes bytes;

  @Before
  public void setup(){
    final long size = 100000;
    array = new byte[(int)size];
    new SecureRandom().nextBytes(array);
    bytes = ByteUtils.newBytes(array);
    assertThat(bytes.longSize(), is(size));
    for(int i=0; i<size; i++){
      assertThat(bytes.get((long)i), is(array[i]));
    }

    file = FileSystems.getDefault().getPath("test.bin");
    bytes.write(file);
    fileBytes = ByteUtils.newFileBytes(file);


    assertThat(fileBytes, is(bytes));
    assertThat(fileBytes.longSize(), is(size));
    assertThat(fileBytes.hash(), is(bytes.hash()));
    assertThat(fileBytes.hashCode(), is(bytes.hashCode()));
    for(long i=0; i<size; i++){
      assertThat(fileBytes.get(i), is(bytes.get(i)));
    }
  }

  @After
  public void close() throws IOException{
    Files.deleteIfExists(file);
  }


  @Test
  public void testGetLong() {
    final Duration testDuration = Duration.ofSeconds(5);
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
