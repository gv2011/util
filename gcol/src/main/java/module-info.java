module com.github.gv2011.util.gcol {
  requires transitive com.github.gv2011.util;
  requires com.github.gv2011.guava;
  requires org.slf4j;
  exports com.github.gv2011.util.gcol;
  provides com.github.gv2011.util.icol.ICollectionFactorySupplier
    with com.github.gv2011.util.gcol.GcolICollectionFactorySupplier;
  provides com.github.gv2011.util.net.NetUtilsSpi
    with com.github.gv2011.util.gcol.net.GuavaNetUtilsSpi;
  exports com.github.gv2011.util.gcol.net to com.github.gv2011.util;
}
