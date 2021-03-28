module com.github.gv2011.util.tika{
  requires transitive com.github.gv2011.util;
  requires org.apache.tika.core;
  uses com.github.gv2011.util.icol.ICollectionFactorySupplier;
  exports com.github.gv2011.util.tika;
  provides com.github.gv2011.util.bytes.DataTypeProvider with com.github.gv2011.util.tika.TikaDataTypeProvider;
}
