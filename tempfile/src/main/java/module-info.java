module com.github.gv2011.http{
  requires transitive com.github.gv2011.util;
  requires org.slf4j;

  exports com.github.gv2011.tempfile to com.github.gv2011.tempfile;

  provides com.github.gv2011.util.tempfile.TempFileFactory with com.github.gv2011.tempfile.TempFileFactoryImp;
}