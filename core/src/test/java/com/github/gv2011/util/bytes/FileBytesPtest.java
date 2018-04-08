package com.github.gv2011.util.bytes;

/*-
 * #%L
 * The MIT License (MIT)
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

public class FileBytesPtest {

  private byte[] array;
  private Path file;
  private Bytes fileBytes;
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
    fileBytes = ByteUtils.read(file);


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


  //@Test longrunning
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
