package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.Optional;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.CloseableBytes;
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

  public static Reader getReader(final String first, final String... more){
    return call(()->Files.newBufferedReader(path(first, more), UTF_8));
  }

  public static InputStream getStream(final String first, final String... more){
    return call(()->Files.newInputStream(path(first, more)));
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

  public static Optional<String> tryReadText(final Path path){
    return call(()->{
      try(InputStream in = Files.newInputStream(path)){
        try(CloseableBytes bytes = ByteUtils.fromStream(in)){
          return Optional.of(bytes.utf8ToString());
        }
      }catch(final NoSuchFileException e){return Optional.empty();}
    });
  }

  public static void writeText(final String text, final String path, final String... morePathElements) {
    writeText(text, path(path, morePathElements));
  }

  public static void writeText(final String text, final Path path){
    run(()->Files.write(path, text.getBytes(UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
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

  public static boolean deleteFile(final Path file) {
    final boolean result = call(()->Files.deleteIfExists(file));
    verify(!Files.exists(file));
    return result;
  }

  public static void deleteContents(final Path folder) {
    call(()->Files.list(folder)).forEach(FileUtils::delete);
  }

  private static void deleteFolder(final Path folder) {
    deleteContents(folder);
    run(()->Files.delete(folder));
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

}
