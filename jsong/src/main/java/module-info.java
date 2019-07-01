module com.github.gv2011.jsong{
  requires com.github.gv2011.util.json.imp;
  requires com.github.gv2011.gson;
  provides com.github.gv2011.util.json.imp.Adapter with com.github.gv2011.jsong.JsongAdapter;
}