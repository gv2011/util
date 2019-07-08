package com.github.gv2011.util.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.MODULE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Artifact {

  String groupId();
  String artifactId();

}
