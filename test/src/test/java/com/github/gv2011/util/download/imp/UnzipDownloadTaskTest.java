package com.github.gv2011.util.download.imp;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.testutil.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

import com.github.gv2011.testutil.AbstractTest;
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
