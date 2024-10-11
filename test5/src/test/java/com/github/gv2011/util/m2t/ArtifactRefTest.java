package com.github.gv2011.util.m2t;

import static com.github.gv2011.util.tstr.TypedString.create;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;

class ArtifactRefTest {

  @Test
  void testToString() {
    assertThat(
      BeanUtils.beanBuilder(ArtifactRef.class)
      .set(ArtifactRef::groupId).to(create(GroupId.class, "example.group"))
      .set(ArtifactRef::artifactId).to(create(ArtifactId.class, "art-a"))
      .set(ArtifactRef::version).to(create(Version.class, "1.0"))
      .build()
      .toString(),
      is("example.group:art-a:1.0")
    );
  }

}
