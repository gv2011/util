package com.github.gv2011.util;

import java.util.NoSuchElementException;

abstract class AlternativeImp<A,B> implements Alternative<A,B>{

	@Override
	public A getA() { 
		throw new NoSuchElementException();
	}

	@Override
	public B getB() {
		throw new NoSuchElementException();
	}
		
	final static class AlternativeA<A1,B1> extends AlternativeImp<A1,B1>{
		private final A1 a;
		AlternativeA(A1 a) {
			this.a = a;
		}
		@Override
		public boolean is(Alt alt) {
			return alt==Alt.A;
		}
		@Override
		public A1 getA() { // TODO Auto-generated method stub
			return a;
		}
	}
		
	final static class AlternativeB<A1,B1> extends AlternativeImp<A1,B1>{
		private final B1 b;
		AlternativeB(B1 b) {
			this.b = b;
		}
		@Override
		public boolean is(Alt alt) {
			return alt==Alt.B;
		}
		@Override
		public B1 getB() {
			return b;
		}
	}
}
