package com.github.gv2011.util.main;

import static com.github.gv2011.util.icol.ICollections.iCollections;

import com.github.gv2011.util.Alternative;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.icol.IList;

public interface ArgsParser {
	
	static final Constant<ArgsParser> INSTANCE = Constant.of(new ArgsParserImp());

  default <T> Alternative<T,String> parse(Class<T> type, String[] args){
    return parse(type, iCollections().asList(args));
  }

  <T> Alternative<T,String> parse(Class<T> type, IList<String> args);

}
