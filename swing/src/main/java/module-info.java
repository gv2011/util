module com.github.gv2011.util.swing.imp{
  requires com.github.gv2011.util;
  requires java.desktop;
  requires ch.qos.logback.classic;
  requires ch.qos.logback.core;

  provides com.github.gv2011.util.swing.SwingFactory with com.github.gv2011.util.swing.imp.SwingFactoryImp;
}