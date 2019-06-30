package com.github.gv2011.util.serviceloader;

/*-
 * #%L
 * The MIT License (MIT)
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
import static com.github.gv2011.util.serviceloader.RecursiveServiceLoader.*;
import static com.github.gv2011.util.serviceloader.RecursiveServiceLoader.DEFAULT_DATA_TYPE_PROVIDER;
import static com.github.gv2011.util.serviceloader.RecursiveServiceLoader.DEFAULT_FILE_WATCH_SERVICE;
import static com.github.gv2011.util.serviceloader.RecursiveServiceLoader.FILE_WATCH_SERVICE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.gv2011.util.bytes.DataTypeProvider;
import com.github.gv2011.util.bytes.DefaultDataTypeProvider;
import com.github.gv2011.util.download.DownloadTask;
import com.github.gv2011.util.download.imp.DefaultDownloadTaskFactory;
import com.github.gv2011.util.filewatch.DefaultFileWatchService;
import com.github.gv2011.util.filewatch.FileWatchService;
import com.github.gv2011.util.lock.DefaultLockFactory;
import com.github.gv2011.util.lock.Lock;
import com.github.gv2011.util.time.Clock;
import com.github.gv2011.util.time.DefaultClock;

public class RecursiveServiceLoaderTest {

  @Test
  public void testClassNames() {
    assertThat(FILE_WATCH_SERVICE, is(FileWatchService.class.getName()));
    assertThat(DEFAULT_FILE_WATCH_SERVICE, is(DefaultFileWatchService.class.getName()));

    assertThat(DATA_TYPE_PROVIDER, is(DataTypeProvider.class.getName()));
    assertThat(DEFAULT_DATA_TYPE_PROVIDER, is(DefaultDataTypeProvider.class.getName()));

    assertThat(CLOCK, is(Clock.class.getName()));
    assertThat(DEFAULT_CLOCK, is(DefaultClock.class.getName()));

    assertThat(LOCK_FACTORY, is(Lock.Factory.class.getName()));
    assertThat(DEFAULT_LOCK_FACTORY, is(DefaultLockFactory.class.getName()));

    assertThat(DOWNLOADER_FACTORY, is(DownloadTask.Factory.class.getName()));
    assertThat(DEFAULT_DOWNLOADER_FACTORY, is(DefaultDownloadTaskFactory.class.getName()));
  }

}
