package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.Verify.verifyEqual;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class FileBytesTest {

  private static final Logger LOG = getLogger(FileBytesTest.class);

  @Test
  void testPerformanceFileBytes() throws IOException {
    testPerformance(FileBytes::new);
  }

  @Test
  void testPerformanceCachedFileBytes() throws IOException {
    testPerformance(CachedFileBytes::new);
  }

  private void testPerformance(final Function<Path,Bytes> implementation) throws IOException {
    //testPerformance(implementation,       1);//warm up
    //testPerformance(implementation,       1);
    testPerformance(implementation,  100000);
    //testPerformance(implementation, 1000000);
    //testPerformance(implementation,10000000);
  }

  private Duration testPerformance(final Function<Path,Bytes> implementation, final long size) throws IOException {
    final Path file = createFile(size);
    try{
      final int iterations1 = 10;
      final int iterations2 = 100;
      final Bytes bytes = implementation.apply(file);
      final Instant start = Instant.now();
      for(int i=0; i<iterations1; i++){
        for(int j=0; j<iterations2; j++){
          final long index = Math.min(size * ((long)j) / ((long)iterations2), size-1L);
          verifyEqual(bytes.get(index), (byte) Long.hashCode(index));
        }
      }
      final Duration duration = Duration.between(start, Instant.now()).dividedBy(iterations1*iterations2);
      LOG.info("Size {}: {} μs per access", size, duration.toNanos()/1000);
      return duration;
    }
    finally{
      Files.delete(file);
    }
  }


  private Path createFile(final long size) throws IOException {
    final Path file = Files.createTempFile(FileBytesTest.class.getName(), ".txt");
    file.toFile().deleteOnExit();
    try(OutputStream s = Files.newOutputStream(file)){
      for(long i=0; i<size; i++){
        s.write(Long.hashCode(i));
      }
    }
    verifyEqual(Files.size(file), size);
    return file;
  }

  //FileBytes:
  //Size 1: 82 μs per access
  //Size 100000: 236 μs per access
  //Size 1000000: 1115 μs per access
  //Size 10000000: 12052 μs per access

  //CachedFileBytes:
  //Size 1: 0 μs per access
  //Size 100000: 11 μs per access
  //Size 1000000: 42 μs per access
  //Size 10000000: 1290 μs per access
}
