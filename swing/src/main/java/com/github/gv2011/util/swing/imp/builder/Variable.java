package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.Opt;

public final class Variable<T> implements Invalidatable{

  private static final Logger LOG = getLogger(Variable.class);

  @FunctionalInterface
  public static interface Calculate<T>{
    T calculate(Variable<T> variable, Opt<T> previousValue);
  }

  private final Object owner;
  private final String name;
  private Opt<T> value;
  private boolean valid;
  private final Opt<Calculate<T>> function;
  private final Set<Invalidatable> listeners = new HashSet<>();
  private Set<Invalidatable> recipients = new HashSet<>();
  private Set<Invalidatable> nextInvalidation = new HashSet<>();
  private final boolean fixed;

  public Variable(final Object owner, final String name) {
    this(owner, name, Opt.empty(), Opt.empty(), false);
  }

  public Variable(final Object owner, final String name, final T value){
    this(owner, name, Opt.of(value), Opt.empty(), true);
  }

  public Variable(final Object owner, final String name, final Calculate<T> function){
    this(owner, name, Opt.empty(), Opt.of(function), false);
  }

  private Variable(final Object owner, final String name, final Opt<T> value, final Opt<Calculate<T>> function, final boolean fixed){
    this.owner = owner;
    this.name = name;
    this.value = value;
    this.function = function;
    if(function.isPresent()) verify(!fixed);
    this.fixed = fixed;
    valid = fixed || value.isPresent();
  }

  public void addListener(final Invalidatable listener){
    listeners.add(listener);
  }

  public T get(final Invalidatable recipient){
    final T result = get();
    if(!fixed) recipients.add(recipient);
    return result;
  }

  public T get(){
    if(!isValid()){
      verify(function.isPresent());
      value = Opt.of(function.get().calculate(this, value));
      valid = true;
      LOG.debug("{}: value calculated to {}.", this, value.get());
    }
    return value.get();
  }

  public void reset(){
    prepareInvalidation();
    LOG.debug("{}: resetting.", this);
    value = Opt.empty();
    valid = false;
    invalidateDependencies();
  }

  @Override
  public void invalidate(){
    if(isValid() && !fixed){
      prepareInvalidation();
      LOG.debug("{}: invalidating value {}.", this, value.get());
      valid = false;
      invalidateDependencies();
    }
  }

  public boolean isValid(){
    return valid;
  }

  public void set(final T value){
    if(fixed) verifyEqual(value, this.value.get());
    else{
      final Opt<T> old = this.value;
      if(!old.equals(Opt.of(value))){
        if(old.isPresent()) prepareInvalidation();
        this.value = Opt.of(value);
        valid = true;
        LOG.debug("{}: changed value from {} to {}.", this, old, value);
        if(old.isPresent()) invalidateDependencies();
      }
    }
  }

  @Override
  public String toString() {
    return owner+"."+ name + (isValid() ? "("+value.get()+")" : "-") + (fixed ? "F" : "");
  }

  private void prepareInvalidation(){
    nextInvalidation = recipients;
    recipients = new HashSet<>();
  }

  private void invalidateDependencies(){
    nextInvalidation.forEach(Invalidatable::invalidate);
    nextInvalidation.clear();
    listeners.forEach(Invalidatable::invalidate);
  }

}
