package com.github.gv2011.http.imp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.gv2011.util.sec.Domain;

public class ConfigurableHttpsDomainPredicateTest {

  @Test
  public void testIsHttpsDomain() {
    final ConfigurableHttpsDomainPredicate p = new ConfigurableHttpsDomainPredicate(()->
      "\n"+
      " some.domain \n"+
      " 1.2.3.4\n"+
      " localhost\n"+
      " *.wild.card\r"
    );
    
    assertTrue(p.test(Domain.parse("some.domain")));
    assertTrue(p.test(Domain.parse("a.wild.card")));
    
    assertFalse(p.test(Domain.parse("wild.card")));
    assertFalse(p.test(Domain.parse("other.domain")));
    assertFalse(p.test(Domain.parse("host-only")));
    assertFalse(p.test(Domain.parse("localhost")));
    assertFalse(p.test(Domain.parse("1.2.3.4")));
  }

}
