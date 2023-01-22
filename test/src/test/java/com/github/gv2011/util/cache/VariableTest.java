package com.github.gv2011.util.cache;

import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static org.junit.Assert.*;

import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;

import com.github.gv2011.util.icol.ISet;

public class VariableTest{

  private final Variable<ISet<String>> names = Variables.createVariable(this::calculateAllNames);

  private final ISet<Function<Invalidator,ISet<String>>> suppliers = notYetImplemented();

  public final ISet<String> getAllNames(final Invalidator invalidator){
    return names.get(invalidator);
  }

  public final ISet<String> calculateAllNames(final Invalidator invalidator){
    return suppliers.stream().flatMap(s->s.apply(invalidator).stream()).collect(toISet());
  }


  @Test
  @Ignore //TODO wip
  public void test() {
    fail("Not yet implemented");
  }

}
