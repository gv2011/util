module com.github.gv2011.util.json.imp{
  requires transitive com.github.gv2011.util;
  uses com.github.gv2011.util.json.imp.Adapter;
  exports com.github.gv2011.util.json.imp;
  provides com.github.gv2011.util.json.JsonFactory with com.github.gv2011.util.json.imp.JsonFactoryImp;
}
