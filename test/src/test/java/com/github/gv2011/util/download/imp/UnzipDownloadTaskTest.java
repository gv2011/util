package com.github.gv2011.util.download.imp;

import static com.github.gv2011.testutils.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import com.github.gv2011.testutils.AbstractTest;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.download.DownloadTask.Status;
import com.github.gv2011.util.download.DownloadTask.StatusInfo;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.lock.Latch;

public class UnzipDownloadTaskTest extends AbstractTest{

  @Test
  public void test() throws Exception {
    final Path dir = testFolder();
    try(final Latch<StatusInfo> latch = Latch.create()){
      try(
        final UnzipDownloadTask task = new UnzipDownloadTask(
          getResource("testfile.zip").toURI(), 43346,
          ByteUtils.parseHash("F120F3BB0D82372EE83C724C852A602AB372CDB70DDFF6F11A8C3648C1B4BAB2"),
          Opt.of("java-12-openjdk-12.0.1.12-1.windows.ojdkbuild.x86_64/"),
          s->log.info(s.message()), s->latch.release(s), dir
        )
      ){
        task.start();
        final StatusInfo status = latch.await();
        assertThat(status.status(), is(Status.FINISHED));
        assertTrue(FileUtils.exists(dir.resolve("bin/java.exe")));
      }
    }
  }

}
