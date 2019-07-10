package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileBytesTest {



  private byte[] array;
  private Bytes bytes;
  private Path file;
  private Bytes fileBytes;

  @Before
  public void setup(){
    array = new byte[]{0,1,2,3,4,5,6,7,8,9,0xA,0xB,0xC,0xD,0xE,0xF};
    bytes = ByteUtils.newBytes(array);
    file = FileSystems.getDefault().getPath("test.bin");
    bytes.write(file);
    fileBytes = ByteUtils.read(file);
  }

  @After
  public void close() throws IOException{
    Files.deleteIfExists(file);
  }


  @Test
  public void testGetLong() {
    long i = 0;
    for(final byte b: array){
      assertThat(bytes.get(i), is(b));
      assertThat(fileBytes.get(i), is(b));
      i++;
    }
  }

  @Test
  public void testToString() {
    final String expected = "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F";
    assertThat(bytes.toString(), is(expected));
    assertThat(fileBytes.toString(), is(expected));
  }

}
