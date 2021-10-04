package com.github.gv2011.util.swing;

import java.util.Optional;

import com.github.gv2011.util.icol.IList;

public interface TreeNode<E> extends IList<TreeNode<E>>{

  default Optional<E> payload(){
    return Optional.empty();
  }

}
