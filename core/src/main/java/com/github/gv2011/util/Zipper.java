package com.github.gv2011.util;

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

import static com.github.gv2011.util.CollectionUtils.toISortedSet;
import static com.github.gv2011.util.FileUtils.contains;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.ISortedSet;

public final class Zipper {

  public static final Zipper newZipper(){return new Zipper();};

  Zipper(){};

  public void zip(final Path source, final Path zipFile) {
    if(Files.isDirectory(source)) zipFolder(source, zipFile);
    else zipFile(source, zipFile);
  }

  private void zipFile(final Path source, final Path zipFile) {
    callWithCloseable(()->new ZipOutputStream(Files.newOutputStream(zipFile)), zos->{
      zipFile(source.getFileName().toString(), source, zos);
    });
  }

  private void zipFolder(final Path sourceFolder, final Path zipFile) {
    callWithCloseable(()->new ZipOutputStream(Files.newOutputStream(zipFile)), zos->{
      zipFolder(sourceFolder.getFileName().toString()+"/", sourceFolder, zos);
    });
  }

  private void zipFolder(final String prefix, final Path folder, final ZipOutputStream zos) throws IOException {
    final ISortedSet<Path> childNames = Files.list(folder).collect(toISortedSet());
    if(childNames.isEmpty()){
      final ZipEntry ze = new ZipEntry(prefix);
      ze.setTime(TimeZone.getDefault().getOffset(0));
      zos.putNextEntry(ze);
      zos.closeEntry();
    }
    else{
      for(final Path cn: childNames) {
        if(Files.isDirectory(cn)) zipFolder(prefix+cn+"/", cn, zos);
        else zipFile(prefix+cn.getFileName(), cn, zos);
      }
    }
  }

  private void zipFile(final String name, final Path file, final ZipOutputStream zos) throws IOException {
    final ZipEntry ze = new ZipEntry(name);
    zos.putNextEntry(ze);
    StreamUtils.copy(()->Files.newInputStream(file), zos);
    zos.closeEntry();
  }

  public void unZip(final Path zipFile, final Path targetFolder) {
    unZip(()->Files.newInputStream(zipFile), targetFolder);
  }

  public void unZip(final ThrowingSupplier<InputStream> stream, final Path targetFolder) {
    unZip(stream, targetFolder, p->{});
  }

  public void unZip(
    final ThrowingSupplier<InputStream> stream, final Path targetFolder, final Consumer<Path> pathCollector
  ) {
    callWithCloseable(stream, s->{
      callWithCloseable(()->new ZipInputStream(s), (ThrowingConsumer<ZipInputStream>)zis->{
        final byte[] buffer = new byte[1024];
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
          final Path path = resolve(targetFolder, ze.getName());
          if(ze.isDirectory()){
            Files.createDirectories(path);
          }
          else{
            Files.createDirectories(path.getParent());
            callWithCloseable(()->Files.newOutputStream(path), (ThrowingConsumer<OutputStream>)os->{
              int count = zis.read(buffer);
              while(count!=-1){
                os.write(buffer, 0, count);
                count = zis.read(buffer);
              }
            });
          }
          pathCollector.accept(path);
          ze = zis.getNextEntry();
        }
      });
    });
  }

  private Path resolve(final Path targetFolder, final String relPath) {
    final Path result = targetFolder.resolve(relPath);
    verify(contains(targetFolder, result));
    return result;
  }

}
