package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.swing.imp.builder.GuiImp.shrt;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.Opt;

abstract class Group<T extends TabImp<?>> extends Oriented{

  private static final Logger LOG = getLogger(Group.class);

  final T tab;
  final Block block;

  Group(final T tab, final Block block) {
    this.tab = tab;
    this.block = block;
    tab.addListener(elasticity);
  }

  @Override
  final Elasticity calculateElasticity(final Variable<Elasticity> variable, final Opt<Elasticity> previous) {
    final Elasticity newElasticity = tab.elasticity().plus(block.elasticity(orientation()));
    return previous.map(p->p.restrict(newElasticity).orElse(p)).orElse(newElasticity);
  }

  @Override
  public final void reset() {
    elasticity.reset();
    tab.reset();
    block.reset();
  }

  void invalidateElasticity(){
    elasticity.invalidate();
  }

  @Override
  Opt<Elasticity> restrict(final Elasticity other) {
    final Opt<Elasticity> restricted = super.restrict(other);
    restricted.ifPresentDo(r->{
      final Elasticity blockElasticity = block.elasticity(orientation());
      final short tabMin = shrt(Math.max(r.min() - blockElasticity.max(), 0));
      final int tabMax = r.max() - blockElasticity.min();
      tab.restrict(new Elasticity(tabMin, tabMin, tabMax));
    });
    return restricted;
  }

  final void fixPosition() {
    final Elasticity e = elasticity();
    verify(e.isRigid());
    final short higherEdge = e.pref();
    LOG.debug("Fixing position of {} to {}.", this, higherEdge);

    final Elasticity tabElasticity = tab.elasticity();
    final Elasticity blockRestricted; //Restrict max to possible value
    {
      final Elasticity blockElasticity = block.elasticity(orientation());
      blockRestricted = blockElasticity
        .restrict(new Elasticity((short)0, (short)0, higherEdge-tabElasticity.min()))
        .orElse(blockElasticity)
      ;
    }
    final Elasticity sum = tabElasticity.plus(blockRestricted);
    verify(sum.fits(higherEdge));

    final double f = sum.getFactor(higherEdge);

    final short tabPos = tab.elasticity().getSize(f);
    final short blockSize = shrt(higherEdge-tabPos);
    verify(block.elasticity(orientation()).fits(blockSize));

    tab.setPosition(tabPos);
    setHigherEdge(higherEdge);
  }

  private final void setHigherEdge(final short higherEdge){
//    verify(higherEdge, flexibility(orientation())::fits);
//    final double factor = flexibility(orientation()).getFactor(higherEdge);
    final short position = tab.position.get();
    final short size = (short) (higherEdge-position);
    setBounds(block, position, size);
  }

  abstract void setBounds(Block block, short position, short size);

  @Override
  public final String toString() {
    return getClass().getSimpleName().charAt(0)+"-"+tab+"-"+block;
  }


}
