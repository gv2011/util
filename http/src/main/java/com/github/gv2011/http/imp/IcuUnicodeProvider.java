package com.github.gv2011.http.imp;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.ibm.icu.text.IDNA.NONTRANSITIONAL_TO_ASCII;
import static com.ibm.icu.text.IDNA.NONTRANSITIONAL_TO_UNICODE;
import static com.ibm.icu.text.IDNA.USE_STD3_RULES;

import com.github.gv2011.util.uc.UnicodeProvider;
import com.ibm.icu.text.IDNA;
import com.ibm.icu.text.IDNA.Info;

public final class IcuUnicodeProvider implements UnicodeProvider{
  
  private static final IDNA UTS46 = IDNA.getUTS46Instance(USE_STD3_RULES|NONTRANSITIONAL_TO_ASCII|NONTRANSITIONAL_TO_UNICODE);

  @Override
  public String idnaNameToASCII(CharSequence name) {
    final Info info = new Info();
    final String result = UTS46.nameToASCII(name, new StringBuilder(), info).toString();
    verify(!info.hasErrors(), ()->format("Illegal domain \"{}\" ({}).", name, info.getErrors()));
    return result;
  }

  @Override
  public String idnaNameToUnicode(CharSequence name) {
    final Info info = new Info();
    final String result = UTS46.nameToUnicode(name, new StringBuilder(), info).toString();
    verify(!info.hasErrors(), ()->info.getErrors().toString());
    return result;
  }

}
