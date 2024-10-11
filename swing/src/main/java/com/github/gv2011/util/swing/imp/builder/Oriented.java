package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verifyEqual;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.Opt;

abstract class Oriented implements Flexible{

  private static final Logger LOG = getLogger(Oriented.class);

  static final String ELASTICITY = "elasticity";
  static final String POSITION = "position";

  final Variable<Elasticity> elasticity;


  Oriented(){
    elasticity = new Variable<>(this, ELASTICITY, this::calculateElasticity);
  }

  abstract Orientation orientation();

  abstract Elasticity calculateElasticity(Variable<Elasticity> variable, Opt<Elasticity> previous);

  Opt<Elasticity> restrict(final Elasticity other) {
    final Elasticity before = elasticity.get();
    final Opt<Elasticity> restricted = before.restrict(other);
    restricted.ifPresentDo(r->{
      LOG.debug("Restricted elasticity of {} from {} to {}.", this, before, r);
      elasticity.set(r);
    });
    return restricted;
  }

  final void addListener(final Variable<Elasticity> listener) {
    elasticity.addListener(listener);
  }

  final Elasticity elasticity(){
    return elasticity.get();
  }

  @Override
  public final Elasticity elasticity(final Orientation orientation){
    verifyEqual(orientation, orientation());
    return elasticity();
  }

}
