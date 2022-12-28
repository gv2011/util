package com.github.gv2011.util.csv;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toIMap;

import java.io.Reader;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.CsvUtils.CsvEngine;
import com.github.gv2011.util.CsvUtils.CsvFormat;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;

public class DefaultCsvEngine implements CsvEngine{

  @Override
  public <B> IList<B> read(final Bytes csvFile, final CsvFormat format, final Class<B> beanClass) {
    return stream(csvFile, format, beanClass).collect(toIList());
  }

  private <B> Stream<B> stream(final Bytes csvFile, final CsvFormat format, final Class<B> beanClass) {
    final Function<CSVRecord,B> converter = createConverter(format, beanClass);
    final Reader csv = csvFile.reader();
    return XStream
      .fromIterator(
        call(()->new CSVParser(csv, convertFormat(format)))
        .iterator()
      )
      .onClose(()->call(csv::close))
      .map(converter)
    ;
  }

  private <B> Function<CSVRecord, B> createConverter(final CsvFormat format, final Class<B> beanClass) {
    final BeanType<B> beanType = BeanUtils.typeRegistry().beanType(beanClass);
    final IMap<String, Property<?>> propsByColumns = beanType.properties().entrySet().stream().collect(toIMap(
        e->format.getColumnName(e.getKey()),
        Entry::getValue
    ));
    return r->{
      final ExtendedBeanBuilder<B> builder = beanType.createBuilder();
      propsByColumns.forEach((c,p)->{
        set(builder, p, r.get(c));
      });
      return builder.build();
    };
  }

  private <B,V> void set(final BeanBuilder<B> builder, final Property<V> p, final String s){
    builder.set(p, p.type().parse(s));
  }

  private CSVFormat convertFormat(final CsvFormat format) {
  	return CSVFormat.Builder
  		.create(CSVFormat.RFC4180)
  		.setDelimiter(';')
  		.setQuote('"')
  		.setRecordSeparator("\r\n")
  		.setHeader().setSkipHeaderRecord(true)
  		.build()
  	;
  }

}
