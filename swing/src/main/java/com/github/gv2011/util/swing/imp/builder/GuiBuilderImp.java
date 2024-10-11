package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.swing.imp.builder.GuiImp.shrt;
import static com.github.gv2011.util.swing.imp.builder.Orientation.HORIZONTAL;
import static com.github.gv2011.util.swing.imp.builder.Orientation.VERTICAL;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.github.gv2011.util.swing.Gui;
import com.github.gv2011.util.swing.GuiBuilder;
import com.github.gv2011.util.swing.HTab;
import com.github.gv2011.util.swing.Position;

public final class GuiBuilderImp implements GuiBuilder{

  private final List<VTabB> vTabs = new ArrayList<>();
  private final List<HTabB> hTabs = new ArrayList<>();

  private final HTabB right;

  private final Set<JComponent> components = new HashSet<>();

  private final List<Block> currentRow = new ArrayList<>();

  private PositionImp position;

  private Dimension initialSize = new Dimension(600,400);


  public GuiBuilderImp(){
    final VTabB top = new VTabB(this, "top");
    vTabs.add(top);
    final HTabB left = new HTabB(this, "left");
    hTabs.add(left);
    right = new HTabB(this, "right");

    position = new PositionImp(top, left);
  }

  private GuiBuilder add(final Block block, final boolean newLine) {
    block.component().ifPresentDo(c->verify(components.add(c)));
    currentRow.add(block);
    if(newLine){
      right.add(position.hTab(), block);
      final VTabB upper = position.vTab();
      final VTabB lower = newVTab();
      currentRow.forEach(b->lower.add(upper, b));
      currentRow.clear();
      position = new PositionImp(lower, left());
    }
    else{
      final HTabB newHTab = new HTabB(this, "h"+hTabs.size(), position.hTab(), block);
      hTabs.add(newHTab);
      position = new PositionImp(position.vTab(), newHTab);
    }
    return this;
  }

  private GuiBuilder add(final Block block, final HTabB right) {
    verify(hTabs.contains(right) || right.equals(this.right));
    block.component().ifPresentDo(c->verify(!components.contains(c)));

    right.add(position.hTab(), block);
    currentRow.add(block);
    block.component().ifPresentDo(components::add);
    position = new PositionImp(position.vTab(), right);
    return this;
  }

  private VTabB newVTab() {
    final VTabB lower = new VTabB(this, "v"+vTabs.size());
    vTabs.add(lower);
    return lower;
  }

  @Override
  public GuiBuilder add(final JComponent component, final boolean newLine) {
    return add(new Holder(component), newLine);
  }

  @Override
  public GuiBuilder add(final JComponent component, final HTab rightTab) {
    return add(new Holder(component), (HTabB)rightTab);
  }

  @Override
  public GuiBuilder addSoftGap(final boolean newLine) {
    return add(gap(), newLine);
  }

  @Override
  public GuiBuilder addHorizontalHardGap(final int size, final boolean newLine) {
    return add(gap(size, HORIZONTAL), newLine);
  }

  @Override
  public GuiBuilder addVerticalHardGap(final int size, final boolean newLine) {
    return add(gap(size, VERTICAL), newLine);
  }

  @Override
  public Position position() {
    return position;
  }

  @Override
  public GuiBuilder setSize(final int width, final int height) {
    this.initialSize = new Dimension(width, height);
    return this;
  }

  @Override
  public Gui build() {
    if(!currentRow.isEmpty()){
      final VTabB upper = position.vTab();
      final VTabB lower = newVTab();
      currentRow.forEach(b->lower.add(upper, b));
    }
    final HTabImp rightImp = right.build();
    final VTabImp bottomImp = vTabs.get(vTabs.size()-1).build();

    final AtomicReference<Gui> gui = new AtomicReference<>();
    call(()->SwingUtilities.invokeAndWait(()->{
      gui.set(new GuiImp(rightImp, bottomImp, initialSize));
    }));
    return notNull(gui.get());
  }

  VTabB top() {
    return vTabs.get(0);
  }

  HTabB left() {
    return hTabs.get(0);
  }

  private Block gap() {
    return new Gap(Elasticity.FLEX, Elasticity.FLEX);
  }

  private Block gap(final int size, final Orientation rigid) {
    return new Gap(shrt(size), rigid);
  }

}
