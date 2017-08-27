package com.github.gv2011.util.text;

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
  
  public void increaseIndention() {
    verify(indentLevel, l->l<maxIndentLevel);
    indentLevel++;
    prefix = multiply(indent, indentLevel);
  }

  public void decreaseIndention() {
    verify(indentLevel, l->l>0);
    indentLevel--;
    prefix = multiply(indent, indentLevel);
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
