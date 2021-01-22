package com.github.gv2011.http.imp;

import java.util.function.Predicate;

import com.github.gv2011.util.sec.Domain;

public final class SimpleHttpsDomainPredicate implements Predicate<Domain>{

  @Override
  public final boolean test(Domain d) {
    return isHttpsDomain(d);
  }
  
  public final boolean isHttpsDomain(Domain d){
    return !d.isInetAddress() && d.asPath().size() > 1;
  }

}
