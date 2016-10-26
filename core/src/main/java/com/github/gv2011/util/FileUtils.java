package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
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

public final class FileUtils {

  public static Path getPath(final String first, final String... more){
    return FileSystems.getDefault().getPath(first, more);
  }

  public static Reader getReader(final String first, final String... more){
    return call(()->Files.newBufferedReader(getPath(first, more), UTF_8));
  }

  public static String readText(final String first, final String... more){
    return readText(getPath(first, more));
  }

  public static String readText(final Path path){
    return tryReadText(path).get();
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
    writeText(text, getPath(path, morePathElements));
  }

  public static void writeText(final String text, final Path path){
    run(()->Files.write(path, text.getBytes(UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
  }

  public static long getSize(final String first, final String... more){
    return getSize(getPath(first, more));
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

}
