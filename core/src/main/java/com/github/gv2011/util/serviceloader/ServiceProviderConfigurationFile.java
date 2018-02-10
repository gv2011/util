package com.github.gv2011.util.serviceloader;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.CollectionUtils.toIList;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.ResourceUtils.getResourceUrls;
import static com.github.gv2011.util.StreamUtils.readText;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;


public final class ServiceProviderConfigurationFile<S> {

    public static <S> ISet<ServiceProviderConfigurationFile<S>> files(final Class<S> service){
        return getResourceUrls("META-INF/services/"+service.getName()).stream()
            .map(u->new ServiceProviderConfigurationFile<>(service, u))
            .collect(toISet())
        ;
    }

    private final Class<S> service;
    private final IList<String> implementations;
    private final URL url;


    private ServiceProviderConfigurationFile(final Class<S> service, final URL url) {
        this.service = service;
        this.url = url;
        final Set<String> set = new HashSet<>();
        implementations = lines(readText(url::openStream)).stream()
            .map(this::stripComment)
            .map(String::trim)
            .filter(not(String::isEmpty))
            .filter(l->set.add(l)) //ignore duplicates
            .collect(toIList())
        ;
    }

    public Class<S> service(){
        return service;
    }

    public URL url(){
        return url;
    }

    public IList<String> implementations(){
        return implementations;
    }

    public static IList<String> lines(final String text){
        return lines(new StringReader(text)).collect(toIList());
    }

    public static Stream<String> lines(final Reader text){
        return new BufferedReader(text).lines();
    }

    public static <T> Predicate<T> not(final Predicate<T> p){
        return o->!p.test(o);
    }

    private final String stripComment(final String line) {
        final int i = line.indexOf('#');
        return i==-1 ? line : line.substring(0, i);
    }
}
