package com.github.gv2011.http.imp;

import org.eclipse.jetty.http.HttpStatus.Code;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.http.StatusCode;

final class StatusCodes {
  
  static final StatusCode OK = BeanUtils.beanBuilder(StatusCode.class).set(StatusCode::code).to(Code.OK.getCode()).build();
  static final StatusCode NOT_FOUND = BeanUtils.beanBuilder(StatusCode.class).set(StatusCode::code).to(Code.NOT_FOUND.getCode()).build();

}
