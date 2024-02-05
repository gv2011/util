module com.github.gv2011.jsong{
  requires transitive com.github.gv2011.util;
  requires transitive com.github.gv2011.util.json.imp;
  requires com.google.gson;
  exports com.github.gv2011.jsong to com.github.gv2011.util;
  provides com.github.gv2011.util.json.Adapter with com.github.gv2011.jsong.JsongAdapter;
}
