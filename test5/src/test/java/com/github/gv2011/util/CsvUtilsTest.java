package com.github.gv2011.util;



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import com.github.gv2011.util.CsvUtils.CsvFormat;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.num.Decimal;
import com.github.gv2011.util.num.NumUtils;

class CsvUtilsTest {

  @Test
  void testCsvEngine() {
    CsvUtils.csvEngine();
  }

  public static interface CsvEntry extends Bean{
    LocalDate wertstellung();
    String    buchungstext();
    Decimal   amount();
  }

  @Test
  void testRead() {
    final IList<CsvEntry> entries = CsvUtils.read(
      ByteUtils.asUtf8(
          "wertstellung;buchungstext;amount\r\n"+
          "2022-02-12;text;12.99"
      ).content(),
      new CsvFormat(){},
      CsvEntry.class
    );
    final CsvEntry parsed = entries.single();
    assertThat(parsed.wertstellung(), is(LocalDate.parse("2022-02-12")));
    assertThat(parsed.buchungstext(), is("text"));
    assertThat(parsed.amount(), is(NumUtils.parse("12.99")));

    assertThat(parsed.wertstellung().hashCode(), is(4141196));
    assertThat(parsed.buchungstext().hashCode(), is(3556653));
    assertThat(parsed.amount().hashCode(), is(-1202268499));
    assertThat(NumUtils.parse("12.99").hashCode(), is(-1202268499));

    assertThat(
      parsed,
      is(
          BeanUtils.beanBuilder(CsvEntry.class)
          .set(CsvEntry::wertstellung).to(LocalDate.parse("2022-02-12"))
          .set(CsvEntry::buchungstext).to("text")
          .set(CsvEntry::amount).to(NumUtils.parse("12.99"))
          .build()
      )
    );
  }

  @Test
  void testCsvParser() throws IOException {
    try(CSVParser p = CSVParser
      .builder()
      .setReader(
        ByteUtils.asUtf8(
            "wertstellung;buchungstext;amount\r\n"+
            "2022-02-12;text;12.99"
        ).content().reader()
      )
      .setFormat(
          CSVFormat.Builder
          .create(CSVFormat.RFC4180)
          .setDelimiter(';')
          .setQuote('"')
          .setRecordSeparator("\r\n")
          .setHeader().setSkipHeaderRecord(true)
          .get()
      )
      .get()
    ){
      final Iterator<CSVRecord> it = p.iterator();
      while(it.hasNext()) it.next();
    }
  }

}
