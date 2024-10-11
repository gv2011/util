package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.Pair;

abstract class TabB<T extends TabB<T, TI>, TI extends TabImp<?>> {

  final List<Pair<T,Block>> previousGroups = new ArrayList<>();
  final GuiBuilderImp guiBuilder;

  private final Constant<TI> tabImp = Constants.cachedConstant(this::createTabImp);
  private String name;

  TabB(final GuiBuilderImp guiBuilder, final String name) {
    this.guiBuilder = guiBuilder;
    this.name = name;
  }

  TabB(final GuiBuilderImp guiBuilder, final String name, final T previous, final Block block) {
    this(guiBuilder, name);
    previousGroups.add(pair(previous, block));
  }

  final void add(final T previous, final Block block) {
    verify(previous, p->!p.allTabs().contains(this)); //prevent circles
    previousGroups.add(pair(previous, block));
  }

  final Set<T> allTabs(){
    final Set<T> collector = new HashSet<>();
    allTabs(collector);
    return collector;
  }

  final void allTabs(final Set<T> collector){
    if(collector.add(self())){
      previousGroups.stream().map(Pair::getKey).forEach(t->t.allTabs(collector));
    }
  }

  abstract T self();

  final TI build(){
    return tabImp.get();
  }

  abstract TI createTabImp();

  @Override
  public final String toString() {
    return name;
  }


}
