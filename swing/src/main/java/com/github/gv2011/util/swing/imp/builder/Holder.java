package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.swing.imp.builder.GuiImp.shrt;
import static com.github.gv2011.util.swing.imp.builder.Orientation.HORIZONTAL;
import static com.github.gv2011.util.swing.imp.builder.Orientation.VERTICAL;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.Opt;

class Holder extends Block{

  private static final Logger LOG = getLogger(Holder.class);

  private static final short INVALID = -1;

  private final JComponent component;
  private final Map<Orientation,Elasticity> flexibility = new HashMap<>();
  private short left = INVALID;
  private short width = INVALID;

  Holder(final JComponent component) {
    this.component = component;
    //check sizes:
    elasticity(Orientation.HORIZONTAL);
    elasticity(Orientation.VERTICAL);
  }

  @Override
  Opt<JComponent> component(){
    return Opt.of(component);
  }

  @Override
  public Elasticity elasticity(final Orientation orientation){
    return flexibility.computeIfAbsent(orientation, o->{
      final Elasticity f = new Elasticity(
        shrt(o.size(component.getMinimumSize())),
        shrt(o.size(component.getPreferredSize())),
        Math.min(o.size(component.getMaximumSize()), Short.MAX_VALUE)
      );
      LOG.debug("{} flexibility of {} is {}.", o, this, f);
      return f;
    });
  }

  @Override
  public void reset() {
    flexibility.clear();
    left = INVALID;
    width = INVALID;
  }

  @Override
  void setHorizontal(final short left, final short width) {
    verify(this.left==INVALID && this.width==INVALID, ()->format("{} not invalidated.", this));
    verify(width, w->elasticity(HORIZONTAL).fits(w));
    this.left = left;
    this.width = width;
  }

  @Override
  void setVertical(final short upper, final short height) {
    verify(left>=0 && width>=0);
    verify(height, h->elasticity(VERTICAL).fits(h), h->format("{}: {} does not fit {}.", this, h, elasticity(VERTICAL)));
    component.setBounds(new Rectangle(left, upper, width, height));
  }

  @Override
  void addTo(final JPanel contentPane) {
    contentPane.add(component);
  }

  @Override
  public String toString() {
    return Opt
      .ofNullable(component.getName())
      .orElseGet(()->
        component.getClass().getSimpleName()+
        (
          JLabel.class.isInstance(component)
          ? "("+JLabel.class.cast(component).getText()+")"
          : ""
        )
      )
    ;
  }

}
