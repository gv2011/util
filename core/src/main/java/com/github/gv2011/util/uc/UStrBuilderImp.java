package com.github.gv2011.util.uc;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
public class UStrBuilderImp implements UStrBuilder{

  private final Object lock = new Object();
  private DelegateBuilder delegate = new Iso8859_1Builder();
  private int size = 0;

  @Override
  public UStr build() {
    synchronized(lock){
      return delegate.build();
    }
  }

  @Override
  public UStrBuilder append(final UStr str) {
    synchronized(lock){
      for(int i=0; i<str.size(); i++) append(str.getCodePoint(i));
    }
    return this;
  }

  @Override
  public UStrBuilder append(final UChar ch) {
    return append(ch.codePoint());
  }

  @Override
  public UStrBuilder append(final int codePoint) {
    synchronized(lock){
      if(!delegate.fits(codePoint)){
        switchBuilder(codePoint);
      }
      delegate.append(codePoint);
      size++;
    }
    return this;
  }

  private void switchBuilder(final int codePoint) {
    final UStr str = delegate.build();
    if(codePoint<=UChar.MAX_BMP){
      delegate = new BmpBuilder();
    }
    else{
      delegate = new UniBuilder();
    }
    for(int i=0; i<str.size(); i++) append(str.get(i));
  }

  @Override
  public final UStrBuilder set(final int index, final UChar ch) {
    return set(index, ch.codePoint());
  }

  @Override
  public UStrBuilder set(final int index, final int codePoint) {
    synchronized(lock){
      while(size<index) append(0);
      if(index==size) append(codePoint);
      else{
        assert index<size;
        if(!delegate.fits(codePoint)){
          switchBuilder(codePoint);
        }
        delegate.set(index, codePoint);
      }
    }
    return this;
  }

}
