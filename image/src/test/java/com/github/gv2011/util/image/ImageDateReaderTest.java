package com.github.gv2011.util.image;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.Nothing.nothing;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.Test;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.github.gv2011.util.CollectionUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;

public class ImageDateReaderTest {

  private static final ISet<String> NO_META = setOf(
      "csm_3c_4c0de31be4.jpg",
      "P1140948.jpg"
  );

  @Test
  public void testWalk() {
    Paths.get("C:/Dateien/Fotos");
    CollectionUtils
      .recursiveStream(
        Paths.get("C:/Dateien/Fotos"),
        p->Files.isDirectory(p) ? call(()->Files.list(p)) : XStream.empty()
      )
      .filter(Files::isRegularFile)
      .filter(p->!NO_META.contains(p.getFileName().toString()))
      .filter(p->{
        final String name = p.getFileName().toString().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg");
      })
      .filter(f->!hasDateTaken(f))
      .forEach(this::printMeta)
    ;
  }


  @Test
  public void test() {
    final ThrowingSupplier<InputStream> streamSupplier =
//      ()->Files.newInputStream(Paths.get("C:\\Dateien\\Fotos\\2019\\Bilder Fete\\Andi\\20190420_113126-EFFECTS.jpg"))
//        ()->Files.newInputStream(Paths.get("C:\\Dateien\\Fotos\\2019\\Fotograf Noelia Herbst 2019\\csm_3c_4c0de31be4.jpg"))
        ()->Files.newInputStream(Paths.get("C:\\Dateien\\Fotos\\2019\\Omas Grab Herbst 2019\\P1140948.jpg"))
//      getClass().getResource("/IMG_2216.JPG")::openStream
    ;
    callWithCloseable(streamSupplier, in->{
      final Metadata metadata = ImageMetadataReader.readMetadata(in);
      for (final Directory directory : metadata.getDirectories()) {
        System.out.println(format("Directory: '{}'", directory.getName()));
        for (final Tag tag : directory.getTags()) {
          System.out.println(format("  Tag: '{}', description: {}", tag.getTagName(), tag.getDescription()));
        }
        if (directory.hasErrors()) {
          for (final String error : directory.getErrors()) {
            System.err.format("ERROR: %s", error);
          }
        }
      }
      return nothing();
    });
  }

  public void printMeta(final Path foto) {
    final ThrowingSupplier<InputStream> streamSupplier = ()->Files.newInputStream(foto);
    System.out.println(format("File: '{}'", foto));
    try {
      callWithCloseable(streamSupplier, in->{
        final Metadata metadata = ImageMetadataReader.readMetadata(in);
        for (final Directory directory : metadata.getDirectories()) {
          System.out.println(format("  Directory: '{}'", directory.getName()));
          for (final Tag tag : directory.getTags()) {
            System.out.println(format("    Tag: '{}', description: {}", tag.getTagName(), tag.getDescription()));
          }
          if (directory.hasErrors()) {
            for (final String error : directory.getErrors()) {
              System.err.format("ERROR: %s", error);
            }
          }
        }
        return nothing();
      });
    } catch (final Exception e) {
      e.printStackTrace();
    }
    //call(()->System.in.read());
  }

  @Test
  public void testDateTakenRaw() {
    System.out.println(getDateTakenRaw(
      ()->Files.newInputStream(Paths.get("C:\\Dateien\\Fotos\\2019\\Bilder Fete\\Andi\\20190420_113126-EFFECTS.jpg"))
//      getClass().getResource("/IMG_2216.JPG")::openStream
    ));
  }

  public boolean hasDateTaken(final Path foto) {
    try {
      return !getDateTakenRaw(()->Files.newInputStream(foto)).isBlank();
    } catch (final Exception e) {
      //e.printStackTrace();
      return false;
    }
  }

//  public LocalDateTime getDateTaken(final ThrowingSupplier<InputStream> foto) {
//    //Example: 2019:04:20 11:31:26
//    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy':'MM':'dd' '", Locale.ROOT);
//    LocalDateTime.parse("yyyy' 'MM' 'dd", dtf);
//  }

  public String getDateTakenRaw(final ThrowingSupplier<InputStream> foto) {
    return callWithCloseable(foto, in->{
      final Iterable<Directory> directories = ImageMetadataReader.readMetadata(in).getDirectories();
      return
        tryGetTag(directories, "Exif IFD0", "Date/Time")
        .orElseGet(()->
          tryGetTag(directories, "Exif SubIFD", "Date/Time Original")
          .orElse("")
        )
      ;
    });
  }


  private Opt<String> tryGetTag(final Iterable<Directory> directories, final String directory, final String tag) {
    return iCollections()
      .xStream(directories.spliterator(), false)
      .filter(d->d.getName().equals(directory))
      .flatMap(d->d.getTags().stream())
      .filter(t->t.getTagName().equals(tag))
      .flatOpt(t->Opt.ofNullable(t.getDescription()))
      .toOpt();
  }

}
