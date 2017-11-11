package com.github.gv2011.util.text;

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




import static com.github.gv2011.util.StringUtils.multiply;
import static com.github.gv2011.util.Verify.verify;

public class Indenter implements Appendable{

  private final StringBuilder sb = new StringBuilder();
  private int indentLevel;
  private final String indent = "  ";
  private String prefix = "";
  private final String continuationPrefix = "  ";
  private boolean inLine = false;
  private boolean onContinuationLine = false;
  private int column = 0;
  private final int maxWidth = 120;
  private final Integer maxIndentLevel = (maxWidth/indent.length())/2;

  public int getIndentLevel() {
    return indentLevel;
  }

  public Indenter increaseIndention() {
    verify(indentLevel, l->l<maxIndentLevel);
    indentLevel++;
    prefix = multiply(indent, indentLevel);
    return this;
  }

  public Indenter decreaseIndention() {
    verify(indentLevel, l->l>0);
    indentLevel--;
    prefix = multiply(indent, indentLevel);
    return this;
  }


  private boolean onLastColumn() {
    return column==maxWidth -1;
  }

  public Indenter addLine(final Object line) {
    finishLine();
    append(line.toString());
    finishLine();
    return this;
  }

  public Indenter finishLine() {
    onContinuationLine = false;
    if(inLine) finishLineInternal();
    else assert column==0;
    return this;
  }

  private void finishLineInternal() {
    sb.append('\n');
    column = 0;
    inLine = false;
  }

  @Override
  public Indenter append(final CharSequence csq){
    for(int i=0; i<csq.length(); i++) append(csq.charAt(i));
    return this;
  }

  @Override
  public Indenter append(final CharSequence csq, final int start, final int end){
    return append(csq.subSequence(start, end));
  }

  @Override
  public Indenter append(final char c){
    if(!inLine) startLine();
    if(c=='\n') {
      finishLineInternal();
      onContinuationLine = true;
    }
    else if(onLastColumn()) {
      sb.append("↩");
      finishLineInternal();
      onContinuationLine = true;
      append(c);
    }
    else {
      sb.append(c);
      column++;
    }
    return this;
  }

  private void startLine() {
    assert !inLine && column == 0;
    inLine = true;
    sb.append(prefix);
    column  = prefix.length();
    if(onContinuationLine) {
      sb.append(continuationPrefix);
      column += continuationPrefix.length();
    }
  }

  @Override
  public String toString() {
    return sb.toString();
  }



}
