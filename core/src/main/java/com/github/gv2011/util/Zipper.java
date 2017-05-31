package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.toISortedSet;
import static com.github.gv2011.util.FileUtils.contains;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.doWithCloseable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
    doWithCloseable(()->new ZipOutputStream(Files.newOutputStream(zipFile)), zos->{
      zipFile(source.getFileName().toString(), source, zos);
    });
  }

  private void zipFolder(final Path sourceFolder, final Path zipFile) {
    doWithCloseable(()->new ZipOutputStream(Files.newOutputStream(zipFile)), zos->{
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
    doWithCloseable(stream, s->{
      doWithCloseable(()->new ZipInputStream(s), zis->{
        final byte[] buffer = new byte[1024];
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
          final Path path = resolve(targetFolder, ze.getName());
          if(ze.isDirectory()){
            Files.createDirectories(path);
          }
          else{
            Files.createDirectories(path.getParent());
            doWithCloseable(()->Files.newOutputStream(path), os->{
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
