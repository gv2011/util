package com.github.gv2011.util.html.imp;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Optional;

import org.w3c.dom.Element;

import com.github.gv2011.util.html.BlockBuilder;
import com.github.gv2011.util.html.BlockType;
import com.github.gv2011.util.html.FormBuilder;

abstract class AbstractBlockBuilder<B extends AbstractBlockBuilder<B>> implements BlockBuilder{

  abstract B self();

  private final Optional<AbstractBlockBuilder<?>> parent;

  AbstractBlockBuilder(final Optional<AbstractBlockBuilder<?>> parent) {
    this.parent = parent;
  }

  @Override
  public final BlockBuilder close() {
    return parent.get();
  }

  abstract Element element();

  @Override
  public FormBuilder addForm() {
    return new FormBuilderImp(this);
  }

  @Override
  public final BlockType blockType(final String name) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final B setBlockType(final BlockType blockType) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final B addText(final String text) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final BlockBuilderImp addBlock() {
    return new BlockBuilderImp(this);
  }

}
