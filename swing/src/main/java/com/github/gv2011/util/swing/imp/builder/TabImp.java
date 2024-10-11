package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

abstract class TabImp<G extends Group<?>> extends Oriented{

  private static final Logger LOG = getLogger(TabImp.class);

  final int index;

  private final IList<G> previousGroups;

  final Variable<Short> position;

  TabImp(final IList<G> previousGroups) {
    this.previousGroups = previousGroups;
    index = previousGroups.stream().mapToInt(p->p.tab.index).max().orElse(-1) + 1;
    position = previousGroups.isEmpty() ? new Variable<>(this, POSITION, (short)0) : new Variable<>(this, POSITION);
    previousGroups.forEach(g->{
      g.addListener(elasticity);
      elasticity.addListener(g::invalidateElasticity);
    });
  }

  final Stream<Block> blocks() {
    return previousGroups.stream().flatMap(p->Stream.concat(
      p.tab.blocks(), Stream.of(p.block)
    ));
  }

  @Override
  final Elasticity calculateElasticity(final Variable<Elasticity> unused, final Opt<Elasticity> previous) {
    if(previousGroups.isEmpty()) return Elasticity.ZERO;
    else{
      Opt<Elasticity> newElasticity;
      if(position.isValid()){
        newElasticity = combineWithOld(previous, new Elasticity(position.get()));
        newElasticity.ifPresentDo(this::restrictGroups);
      }
      else{
        newElasticity = Opt.empty();
        boolean nothingRestricted = false;
        while(!nothingRestricted){
          final Opt<Elasticity> combined = combineWithOld(
            previous,
            ( previousGroups.stream()
              .map(g->g.elasticity())
              .reduce((e1,e2)->e1.restrict(e2).orElse(e1))
              .orElseThrow()
            )
          );
          if(combined.isEmpty()){
            nothingRestricted = true; //nothing changed
          }
          else{
            newElasticity = combined;
            nothingRestricted = restrictGroups(combined.get());
          }
        }
      }
      return newElasticity.orElseGet(previous::get);
    }
  }

  private Opt<Elasticity> combineWithOld(final Opt<Elasticity> previous, final Elasticity elasticity) {
    return previous.map(p->p.restrict(elasticity)).orElse(Opt.of(elasticity));
  }

  @Override
  Opt<Elasticity> restrict(final Elasticity other) {
    final Opt<Elasticity> restricted = super.restrict(other);
    restricted.ifPresentDo(this::restrictGroups);
    return restricted.isPresent() ? Opt.of(elasticity()) : Opt.empty();
  }

  /**
   * @return true if nothing was restricted
   */
  private boolean restrictGroups(final Elasticity elasticity){
    return previousGroups.stream()
      .filter(g->{
        final boolean restricted = g.restrict(elasticity).isPresent();
        return restricted;
      })
      .count() == 0L
    ;
  }

  @Override
  public final void reset() {
    if(!previousGroups.isEmpty()){
      position.reset();
      elasticity.reset();
      previousGroups.forEach(Group::reset);
    }
  }

  @Override
  public final String toString() {
    return orientation().name().charAt(0)+""+index;
  }

  final void setPosition(final short position) {
    if(this.position.isValid()) verifyEqual(position, this.position.get());
    else {
      LOG.debug("Setting position of {} to {}.", this, position);
      verify(elasticity().fits(position));
      this.position.set(position);
      restrict(new Elasticity(position));


      previousGroups.forEach(Group::fixPosition);
    }
  }

}
