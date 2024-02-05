package com.github.gv2011.h2;

import com.github.gv2011.util.beans.Key;
import com.github.gv2011.util.beans.KeyBean;

public interface Name extends @Key HasPersonalId, KeyBean<PersonalId>{

  String givenName();

  String surname();

}
