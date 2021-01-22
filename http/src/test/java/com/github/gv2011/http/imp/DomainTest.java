package com.github.gv2011.http.imp;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.security.auth.x500.X500Principal;

import org.junit.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;

public class DomainTest {

  @Test
  public void testFrom() {
    final X500Principal ppal = new X500Principal("CN=genf1.server.eu");
    assertThat(ppal.getName(), is("CN=genf1.server.eu"));
    assertThat(ppal.getName(X500Principal.CANONICAL), is("cn=genf1.server.eu"));
    assertThat(ppal.getName(X500Principal.RFC1779), is("CN=genf1.server.eu"));
    assertThat(ppal.getName(X500Principal.RFC2253), is("CN=genf1.server.eu"));
    
    final Domain domain = Domain.from(ppal);
    assertThat(domain.toString(), is("genf1.server.eu"));
    assertThat(domain.toAscii(), is("genf1.server.eu"));
  }
  
  @Test
  public void testPunicode() {
    Domain domain = Domain.parse("münchen.server.eu");
    assertThat(domain.toString(),  is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toAscii(),   is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toUnicode(), is("münchen.server.eu"));
    
    domain = Domain.parse("xn--mnchen-3ya.server.eu");
    assertThat(domain.toString(),  is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toAscii(),   is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toUnicode(), is("münchen.server.eu"));
  }
  
  @Test
  public void testIDNA2008() {
    Domain domain = Domain.parse("münchen.server.eu");
    assertThat(domain.toString(),  is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toAscii(),   is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toUnicode(), is("münchen.server.eu"));
    
    domain = Domain.parse("xn--mnchen-3ya.server.eu");
    assertThat(domain.toString(),  is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toAscii(),   is("xn--mnchen-3ya.server.eu"));
    assertThat(domain.toUnicode(), is("münchen.server.eu"));
    
    
    //IDNA2008:
    
    domain = Domain.parse("spaß.de");
    assertThat(domain.toString(), is("xn--spa-7ka.de"));

    domain = Domain.parse("SPAẞ.DE");
    assertThat(domain.toString(), is("xn--spa-7ka.de"));
    
    domain = Domain.parse("xn--spa-7ka.de");
    assertThat(domain.toString(), is("xn--spa-7ka.de"));
    assertThat(domain.toAscii(), is("xn--spa-7ka.de"));
    assertThat(domain.toUnicode(), is("spaß.de"));
  }
  

  public static interface TestBean extends Bean{Domain domain();}
  
  @Test
  public void beanTest(){
    final Domain domain = Domain.parse("host1");
    final TestBean bean = BeanUtils.beanBuilder(TestBean.class).set(TestBean::domain).to(domain).build();
    assertThat(bean.domain(), is(domain));
  }

  @Test
  public void ktest(){
    System.out.println(RsaKeyPair.create().getPublic().getFormat());
  }

}
