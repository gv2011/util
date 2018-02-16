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

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.ex.ThrowingFunction;

public final class FileUtils {

  private FileUtils(){staticClass();}

  public static final Path WORK_DIR = call(()->FileSystems.getDefault().getPath(".").toRealPath());

//  @Deprecated
//  public static Path getPath(final String first, final String... more){
//    return path(first, more);
//  }

  public static Path path(final String first, final String... more){
    return FileSystems.getDefault().getPath(first, more);
  }

  public static boolean sameFile(final Path f1, final Path f2){
    return call(()->Files.isSameFile(f1, f2));
  }

  public static Stream<Path> list(final Path dir){
    return call(()->Files.list(dir));
  }

  public static Reader getReader(final String first, final String... more){
    return call(()->Files.newBufferedReader(path(first, more), UTF_8));
  }

  public static Writer getWriter(final String first, final String... more){
    return call(()->Files.newBufferedWriter(path(first, more), UTF_8));
  }

  public static Reader getReaderRemoveBom(final String first, final String... more){
    return call(()->{
      final BufferedReader reader = Files.newBufferedReader(path(first, more), UTF_8);
      final int bom = reader.read();
      verifyEqual(bom, 0xFEFF);
      return reader;
    });
  }

  public static InputStream getStream(final String first, final String... more){
    return getStream(path(first, more));
  }

  public static InputStream getStream(final Path path){
    return call(()->Files.newInputStream(path));
  }

  public static InputStream tryGetStream(final Path path){
    return call(()->{
      InputStream newInputStream;
      try {
        newInputStream = Files.newInputStream(path);
      } catch (final NoSuchFileException e) {
        newInputStream = new ByteArrayInputStream(new byte[0]);
      }
      return newInputStream;
    });
  }

  public static String readText(final String first, final String... more){
    return readText(path(first, more));
  }

  public static String readText(final Path path){
    return tryReadText(path).get();
  }

  public static Reader reader(final Path path){
    return call(()->Files.newBufferedReader(path, UTF_8));
  }

  public static Bytes read(final Path path){
    return ByteUtils.read(path);
  }

  public static Optional<String> tryReadText(final Path path){
    return call(()->{
      try(InputStream in = Files.newInputStream(path)){
        final Bytes bytes = ByteUtils.fromStream(in);
        return Optional.of(bytes.utf8ToString());
      }catch(final NoSuchFileException e){return Optional.empty();}
    });
  }

  public static void writeText(final String text, final String path, final String... morePathElements) {
    writeText(text, path(path, morePathElements));
  }

  public static void writeText(final String text, final Path path){
    call(()->Files.write(path, text.getBytes(UTF_8), TRUNCATE_EXISTING, CREATE));
  }

  public static long getSize(final String first, final String... more){
    return getSize(path(first, more));
  }

  public static long getSize(final Path path){
    return call(()->Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes().size());
  }

  public static String removeExtension(final Path path){
    final Path fileName = path.getFileName();
    if(fileName==null) throw new IllegalArgumentException();
    final String n = fileName.toString();
    final int i = n.lastIndexOf('.');
    return i==-1?n:n.substring(0, i);
  }

  public static void delete(final Path file) {
    if(Files.isDirectory(file)) deleteFolder(file);
    else deleteFile(file);
  }

  /**
   * Deletes a file if it exists. Cannot be used with directories.
   *
   * @return  {@code true} if the file was deleted by this method;
   *          {@code false} if the file could not be deleted because it did not exist.
   */
  public static boolean deleteFile(final Path file) {
    verify(!Files.isDirectory(file));
    final boolean result = call(()->Files.deleteIfExists(file));
    verify(!Files.exists(file));
    return result;
  }

  public static void deleteContents(final Path folder) {
    call(()->Files.list(folder)).forEach(FileUtils::delete);
  }

  private static void deleteFolder(final Path folder) {
    deleteContents(folder);
    call(()->Files.delete(folder));
    verify(!Files.exists(folder));
  }

  public static boolean contains(final Path folder, final Path file){
    final Path c = folder.toAbsolutePath();
    final Path f = file.toAbsolutePath();
    boolean done = false;
    boolean result = false;
    Path parent = f.getParent();
    while(!done){
      if(parent==null) done=true;
      else{
        if(parent.equals(c)){
          done = true;
          result = true;
        }
        else parent = parent.getParent();
      }
    }
    return result;
  }

  public static void zip(final Path source, final Path target) {
    new Zipper().zip(source, target);
  }

  public static void unZip(final Path zipFile, final Path targetFolder) {
    new Zipper().unZip(zipFile, targetFolder);
  }

  public static <T> T callWithTempFolder(final Class<?> clazz, final ThrowingFunction<Path,? extends T> f) {
    return call(()->{
      final Path folder = Files.createTempDirectory(clazz.getName());
      try{return f.apply(folder);}
      finally{
        delete(folder);
      }
    });
  }

  public static Writer writer(final Path file) {
    return call(()->Files.newBufferedWriter(file, UTF_8));
  }

  public static long copy(final Path src, final Path target) {
    return callWithCloseable(()->Files.newOutputStream(target, CREATE, TRUNCATE_EXISTING), out->{
      return StreamUtils.copy(()->Files.newInputStream(src), out);
    });
  }

}
