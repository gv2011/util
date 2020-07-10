package com.github.gv2011.util;

public interface Alternative<A, B> {
	
	public static <A,B> Alternative<A,B> altA(A a){
		return new AlternativeImp.AlternativeA<>(a);
	}
	
	public static <A,B> Alternative<A,B> altB(B b){
		return new AlternativeImp.AlternativeB<>(b);
	}
	
	enum Alt{A,B}
	
	boolean is(final Alt alt);
	
	default boolean isA(){return is(Alt.A);}
	
	default boolean isB(){return is(Alt.B);}
	
	A getA();
	
	B getB();

}
