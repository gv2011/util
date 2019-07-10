module com.github.gv2011.util.beans.cglib{
  requires org.slf4j;
  requires com.github.gv2011.util;
  requires com.github.gv2011.util.beans.imp;
  requires cglib;
  provides com.github.gv2011.util.beans.imp.BeanFactoryBuilder with com.github.gv2011.util.beans.cglib.CglibBeanFactoryBuilder;

}
