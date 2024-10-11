package com.github.gv2011.util.text;

import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.text.TextUtils.TreeNode;

class TextUtilsTest {

  @Test
  void testFormatTree() {
    final TestNode tree = new TestNode(
      "root",
      listOf(
        new TestNode(
          "dir1",
          listOf(
            new TestNode("dir1.1"),
            new TestNode("dir1.2")
          )
        ),
        new TestNode(
          "dir2",
            listOf(
              new TestNode("dir2.1")
            )
        )
      )
    );
    TextUtils.formatTree(tree).forEach(System.out::println);
  }

  private static class TestNode implements TreeNode<TestNode>{
    private final String name;
    private final IList<TestNode> children;
    private TestNode(final String name) {
      this(name, emptyList());
    }
    private TestNode(final String name, final IList<TestNode> children) {
      this.name = name;
      this.children = children;
    }
    @Override
    public String name() {
      return name;
    }
    @Override
    public Stream<TestNode> children() {
      return children.stream();
    }
  }

}
