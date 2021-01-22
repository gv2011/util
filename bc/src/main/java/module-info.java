module com.github.gv2011.util.bc{
  requires org.slf4j;
  requires com.github.gv2011.util;
  requires org.bouncycastle.provider;
  requires org.bouncycastle.pkix;
  
  provides com.github.gv2011.util.sec.SecProvider with com.github.gv2011.util.bc.BcSecProvider;
}
