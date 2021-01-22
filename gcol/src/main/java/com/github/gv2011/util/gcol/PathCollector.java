package com.github.gv2011.util.gcol;


import java.util.Set;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.AbstractCollectionCollector;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Path;
import com.google.common.collect.ImmutableList;

final class PathCollector extends AbstractCollectionCollector<Path, String, Path.Builder> {

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).build()
  ;

  PathCollector() {super(ADD);}

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

  @Override
  public Supplier<Path.Builder> supplier() {
    return PathBuilder::new;
  }

  final static class PathBuilder extends AbstractIListBuilder<Path,String,Path.Builder> implements Path.Builder {
  
    @Override
    Path.Builder self() {
      return this;
    }
  
  
    @Override
    public Path build() {
      synchronized(list){
        if(list.isEmpty()) return PathImp.EMPTY;
        return new PathImp(ImmutableList.copyOf(list));
      }
    }
  }

}
