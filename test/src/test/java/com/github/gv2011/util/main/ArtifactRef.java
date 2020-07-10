package com.github.gv2011.util.main;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.tstr.TypedString;

public interface ArtifactRef extends Bean {
  
  static interface ArtifactId extends TypedString<ArtifactId>{}

  ArtifactId artifactId();

  @DefaultValue("")
  String classifier();

}
