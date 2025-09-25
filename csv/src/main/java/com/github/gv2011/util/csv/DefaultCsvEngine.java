package com.github.gv2011.util.csv;

import static com.github.gv2011.util.Verify.verify;
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
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Opt;

public class DefaultCsvEngine implements CsvEngine{

  @Override
  public <B> IList<B> read(final Bytes csvFile, final CsvFormat format, final Class<B> beanClass) {
    return stream(csvFile, format, beanClass).collect(toIList());
  }

  private <B> Stream<B> stream(final Bytes csvFile, final CsvFormat format, final Class<B> beanClass) {
    final Function<CSVRecord,B> converter = createConverter(format, beanClass);
    final Reader csv = csvFile.removeBom().reader();
    return XStream
      .fromIterator(
        call(()->{
          final CSVParser csvParser = CSVParser.builder()
            .setFormat(convertFormat(format))
            .setReader(csv)
            .get()
          ;
          try{
            csvParser.getHeaderNames().forEach(h->verify(h, StringUtils::isTrimmed));
            return csvParser.iterator();
          }
          catch(final Throwable t){
            csvParser.close();
            throw t;
          }
        })
      )
      .onClose(()->call(csv::close))
      .map(converter)
    ;
  }

  private <B> Function<CSVRecord, B> createConverter(final CsvFormat format, final Class<B> beanClass) {
    final BeanType<B> beanType = BeanUtils.typeRegistry().beanType(beanClass);

    @SuppressWarnings("unchecked")
    final Opt<Property<Long>> recordNumberProperty =
      format.recordNumberProperty()
      .map(n->
        (Property<Long>) verify(
          beanType.properties().get(n),
          p1->p1.type().name().equals(Long.class.getName())
        )
      )
    ;

    final IMap<String, Property<?>> propsByColumns = beanType.properties().entrySet().stream()
      .filter(e->format.tryGetColumnName(e.getKey()).isPresent())
      .collect(toIMap(
          e->format.tryGetColumnName(e.getKey()).get(),
          Entry::getValue
      ))
    ;
    final IMap<Property<?>, String> fixedValues = beanType.properties().entrySet().stream()
      .filter(e->format.tryGetValue(e.getKey()).isPresent())
      .collect(toIMap(
          Entry::getValue,
          e->format.tryGetValue(e.getKey()).get()
      ))
    ;
    return r->{
      final ExtendedBeanBuilder<B> builder = beanType.createBuilder();
      recordNumberProperty.ifPresentDo(p->{
        builder.set(p, r.getRecordNumber());
      });
      fixedValues.forEach((p, v)->{
        set(builder, p, v);
      });
      propsByColumns.forEach((c,p)->{
        set(builder, p, getValue(r, c));
      });
      return builder.build();
    };
  }

  private String getValue(final CSVRecord rec, final String columnName) {
    return rec.get(columnName);
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
  		.get()
  	;
  }

}
