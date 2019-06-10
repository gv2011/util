/*
 * Original Copyright And License Notice:
 * Copyright (c) 2005 Brian Goetz and Tim Peierls
 * Released under the Creative Commons Attribution License
 *   (http://creativecommons.org/licenses/by/2.5)
 * Official home: http://www.jcip.net
 *
 * Any republication or derived work distributed in source code form
 * must include this copyright and license notice.
 */
package com.github.gv2011.util.ann;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The field or method to which this annotation is applied can only be accessed
 * when holding a particular lock, which may be a built-in (synchronization) lock,
 * or may be an explicit java.util.concurrent.Lock.
 *
 * The argument determines which lock guards the annotated field or method:
 * <ul>
 * <li>
 * <code>this</code> : The intrinsic lock of the object in whose class the field is defined.
 * </li>
 * <li>
 * <code>class-name.this</code> : For inner classes, it may be necessary to disambiguate 'this';
 * the <em>class-name.this</em> designation allows you to specify which 'this' reference is intended
 * </li>
 * <li>
 * <code>itself</code> : For reference fields only; the object to which the field refers.
 * </li>
 * <li>
 * <code>field-name</code> : The lock object is referenced by the (instance or static) field
 * specified by <em>field-name</em>.
 * </li>
 * <li>
 * <code>class-name.field-name</code> : The lock object is reference by the static field specified
 * by <em>class-name.field-name</em>.
 * </li>
 * <li>
 * <code>method-name()</code> : The lock object is returned by calling the named nil-ary method.
 * </li>
 * <li>
 * <code>class-name.class</code> : The Class object for the specified class should be used as the lock object.
 * </li>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardedBy {
    String value();
}
