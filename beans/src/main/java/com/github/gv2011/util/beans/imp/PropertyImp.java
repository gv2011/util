package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.ex.Exceptions.format;

/*-
 * #%L
 * util-beans
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
import java.util.Optional;

import com.github.gv2011.util.beans.Property;

public final class PropertyImp<T> implements Property<T> {

    private final String name;
    private final AbstractType<T> type;
    private final Optional<T> defaultValue;

    PropertyImp(final String name, final AbstractType<T> type, final Optional<T> defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public AbstractType<T> type() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Optional<T> defaultValue() {
        try {
          return defaultValue.isPresent() ? defaultValue : type.getDefault();
        }catch(final RuntimeException e) {
            throw new IllegalStateException(format("Could not obtain default value of {}.", type), e);
        }
    }

    boolean isOptional() {
      return type.isOptional();
    }

}
