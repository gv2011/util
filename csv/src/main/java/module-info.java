module com.github.gv2011.util.csv{
  requires transitive com.github.gv2011.util;
  requires commons.csv;
  provides com.github.gv2011.util.CsvUtils.CsvEngine with com.github.gv2011.util.csv.DefaultCsvEngine;
}