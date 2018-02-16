package com.github.gv2011.util.swing;

/*-
 * #%L
 * util-swing
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import java.util.Optional;
import java.util.function.Function;

import com.github.gv2011.util.icol.AbstractIList;
import com.github.gv2011.util.icol.IList;

public class IListTree<E> extends AbstractIList<TreeNode<E>> implements TreeNode<E>{

  public static <E> IListTree<E> create(
    final E obj,
    final Function<E, String> getName,
    final Function<E, IList<? extends E>> getChildren
  ) {
    return new IListTree<>(obj, getName, getChildren);
  }

  private final E obj;
  private final Function<E, String> getName;
  private final Function<E, IList<? extends E>> getChildren;


  private IListTree(final E obj, final Function<E, String> getName, final Function<E, IList<? extends E>> getChildren) {
    this.obj = obj;
    this.getName = getName;
    this.getChildren = getChildren;
  }

  @Override
  public IListTree<E> get(final int index) {
    final E child = getChildren.apply(obj).get(index);
    return new IListTree<>(child, getName, getChildren);
  }

  @Override
  public int size() {
    return getChildren.apply(obj).size();
  }

  @Override
  public String toString() {
    return getName.apply(obj);
  }

  @Override
  public Optional<E> payload() {
    return Optional.of(obj);
  }



}
