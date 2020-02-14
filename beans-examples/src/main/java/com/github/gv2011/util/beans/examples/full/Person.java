package com.github.gv2011.util.beans.examples.full;

import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.icol.IList;

public interface Person extends Contact{

  @Override
  @Computed
  String name();

  @Computed
  String forname();

  IList<String> fornames();

  @Computed
  String surname();

  String firstSurname();

  String secondSurname();

}
