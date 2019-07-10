import com.github.gv2011.util.ann.Artifact;

@Artifact(groupId = "com.github.gv2011", artifactId = "util-logback")
module com.github.gv2011.util.log.logback{
  requires transitive com.github.gv2011.util;
  requires transitive org.slf4j;
  requires transitive ch.qos.logback.classic;
  requires transitive ch.qos.logback.core;

  exports com.github.gv2011.util.log.logback to com.github.gv2011.util;

  provides ch.qos.logback.classic.spi.Configurator with com.github.gv2011.util.log.logback.LogbackConfigurator;
  provides com.github.gv2011.util.log.LogAdapter with com.github.gv2011.util.log.logback.LogbackLogAdapter;
}
