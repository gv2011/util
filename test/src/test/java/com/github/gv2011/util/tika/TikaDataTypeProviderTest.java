package com.github.gv2011.util.tika;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import java.util.Comparator;
import java.util.Map.Entry;

import org.junit.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.FileExtension;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;

public class TikaDataTypeProviderTest {

  private final TikaDataTypeProvider tikaDataTypeProvider = new TikaDataTypeProvider();


  @Test
  public void test() {
    tikaDataTypeProvider.knownDataTypes().parallelStream()
    .map(d->pair(d.toString(), d))
    .collect(toISortedMap()).entrySet().parallelStream()
    .map(Entry::getValue)
    .map(dt->dt.toString()+" "+dt.fileExtensions())
    .forEach(System.out::println);
  }

  @Test
  public void test2() {
    fileExtensions()
    .forEach(System.out::println);
  }

  private ISortedSet<FileExtension> fileExtensions() {
    return tikaDataTypeProvider.knownDataTypes().parallelStream()
    .flatMap(dt->dt.fileExtensions().stream())
    .collect(toISortedSet());
  }

  @Test
  public void test3() {
    tikaDataTypeProvider.knownDataTypes().parallelStream()
    .flatMap(dt->dt.fileExtensions().stream().map(e->pair(e,dt)))
    .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toISet())))
    .entrySet().stream()
    .sorted(Comparator.comparing(Entry::getKey))
    .forEach(System.out::println);
  }

  @Test
  public void test4() {
    fileExtensions().stream()
    .map(e->pair(e, tikaDataTypeProvider.dataTypeForExtension(e)))
    .forEach(System.out::println);
  }

  @Test
  public void testSerialize() {
    final BeanType<DataType> beanType = BeanUtils.typeRegistry().beanType(DataType.class);
    tikaDataTypeProvider.knownDataTypes().stream()
    .map(beanType::toJson)
    .forEach(System.out::println);
  }

  @Test
  public void testSerialize2() {
    final ISortedMap<FileExtension, DataType> map = fileExtensions().stream().collect(toISortedMap(
      ext->ext,
      tikaDataTypeProvider::dataTypeForExtension
    ));

    final BeanType<DataTypesList> beanType = BeanUtils.typeRegistry().beanType(DataTypesList.class);
    System.out.println(
      beanType.toJson(beanType.createBuilder().set(DataTypesList::dataTypes).to(map).build()).serialize()
    );
  }

  public static interface DataTypesList extends Bean{
    ISortedMap<FileExtension, DataType> dataTypes();
  }


}
