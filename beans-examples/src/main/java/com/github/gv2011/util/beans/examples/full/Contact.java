package com.github.gv2011.util.beans.examples.full;

import com.github.gv2011.util.beans.AbstractRoot;
import com.github.gv2011.util.icol.ISet;

@AbstractRoot(subClasses={Organisation.class, Person.class})
public interface Contact {

  String name();

  ISet<Channel> adresses();

}
