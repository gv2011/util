package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Equal.calcEqual;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.num.NumUtils.safeAdd;
import static com.github.gv2011.util.swing.imp.builder.GuiImp.shrt;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import com.github.gv2011.util.Equal;
import com.github.gv2011.util.icol.Opt;

class Elasticity{

  static final Elasticity ZERO = new Elasticity((short)0, (short)0, 0);
  static final Elasticity FLEX = new Elasticity((short)0, (short)0, Short.MAX_VALUE);

  private final short min;
  private final short pref;
  private final int max;

  Elasticity(final short rigidSize) {
    this(rigidSize, rigidSize, rigidSize);
  }

  Elasticity(final short min, final short pref, final int max) {
    this.min = min;
    this.pref = pref;
    this.max = max;
    verify(min, m->m>=0);
    verify(pref, p->p>=min, p->format("Preferred size of {} is less than minimum size {}.", p, min));
    verify(max, m->m>=pref);
  }

  final short min() {
    return min;
  }

  final short pref() {
    return pref;
  }

  final int max() {
    return max;
  }

  final boolean isRigid(){
    return min==pref && pref==max;
  }

  public double compressionValue(){
    return pref-min;
  }

  public double tensionValue(){
    return max-pref;
  }

  @Override
  public final String toString() {
    if(equals(FLEX)) return "FLEX";
    else if(min==pref && pref==max){
      return ""+pref;
    }
    else{
      final String maxStr = max==Short.MAX_VALUE ? "X" : Integer.toString(max);
      return min+"|"+pref+"|"+maxStr;
    }
  }

  public Opt<Elasticity> restrict(final Elasticity other){
    if(other.min()<=min() && Math.min(other.pref(), max())<=pref() && other.max()>=max()) return Opt.empty();
    else{
      verify(other.min()<=max() && other.max()>=min());
      final int max = Math.min(max(), other.max());
      return Opt.of(new Elasticity(
        shrt(Math.max(min(), other.min())),
        shrt(Math.min(Math.max(pref(), other.pref()), max)),
        max
      ));
    }
  }

  public static final Elasticity parallel(final Collection<Elasticity> elements){
    final Optional<Elasticity> greatestMin = elements.stream().sorted(Comparator.comparing(Elasticity::min).reversed()).findFirst();
    final short min = greatestMin.map(Elasticity::min).orElse(GuiImp.ZERO);
    final int max =
      elements.stream()
      .mapToInt(c->verify(c.max(), m->m>=min, m->format("Max value {} of {} is less than min value of {}.", m, c, greatestMin.get())))
      .min().orElse(0)
    ;
    assert max >= min;
    final short pref = shrt(
      Math.min(
        elements.stream().mapToInt(Elasticity::pref).max().orElse(0),
        max
      )
    );
    assert pref>=min && pref<=max;
    return new Elasticity(min, pref, max);
  }

  double getFactor(final int x){
    final short min = min();
    final short pref = pref();
    final int max = max();

    assert min>=0 && pref>=min && max>=pref && x>=min && x<=max;

    final double factor;
    if(x<pref){
      final int base = pref - min;
      verify(base>0);
      factor = ((double)(x-min)) / (double)base;
      assert factor>=0d && factor<1d;
    }
    else{
      final int base = max - pref;
      if(base==0){
        verify(x==pref);
        factor = 1d;
      }
      else{
        factor = ((double)(x-pref)) / ((double)base) + 1d;
        assert factor>=1d && factor<=2d;
      }
    }
    assert Double.isFinite(factor);
    return factor;
  }

  final boolean fits(final short x){
    return x>=min() && x<=max();
  }

  final Elasticity plus(final Elasticity f2){
    return new Elasticity(
      shrt(min()  + f2.min() ),
      shrt(pref() + f2.pref()),
      safeAdd(max(), f2.max())
    );
  }

  short getSize(final double factor){
    assert factor>=0d && factor<=2d;
    final short size;
    if(factor<1d){
      final short min = min();
      final double base = pref() - min;
      size = shrt(min + factor * base);
    }
    else{
      final short pref = pref();
      final double base = max() - pref;
      size = shrt(pref + (factor-1d) * base);
    }
    return limit(size);
  }

  @Override
  public int hashCode() {
    return Equal.hashCode(Elasticity.class, min, pref, max);
  }

  @Override
  public boolean equals(final Object obj) {
    return calcEqual(this, obj, Elasticity.class, Elasticity::min, Elasticity::pref, Elasticity::max);
  }

  final short limit(final short size) {
    return limit(size, min, max);
  }

  static final short limit(final short size, final short min, final int max) {
    return shrt(Math.min(Math.max(min, size), max));
  }


}
