/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.github.gv2011.activation;

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

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Locale;

/**
 * A parameter list of a MimeType
 * as defined in RFC 2045 and 2046. The Primary type of the
 * object must already be stripped off.
 *
 * @see com.github.gv2011.activation.MimeType
 */
public class MimeTypeParameterList {
    private final Hashtable<String,String> parameters;

    /**
     * A string that holds all the special chars.
     */
    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";


    /**
     * Default constructor.
     */
    public MimeTypeParameterList() {
        parameters = new Hashtable<>();
    }

    /**
     * Constructs a new MimeTypeParameterList with the passed in data.
     *
     * @param parameterList an RFC 2045, 2046 compliant parameter list.
     * @exception	MimeTypeParseException	if the MIME type can't be parsed
     */
    public MimeTypeParameterList(final String parameterList)
					throws MimeTypeParseException {
        parameters = new Hashtable<>();

        //    now parse rawdata
        parse(parameterList);
    }

    /**
     * A routine for parsing the parameter list out of a String.
     *
     * @param parameterList an RFC 2045, 2046 compliant parameter list.
     * @exception	MimeTypeParseException	if the MIME type can't be parsed
     */
    protected void parse(final String parameterList) throws MimeTypeParseException {
	if (parameterList == null)
	    return;

        final int length = parameterList.length();
        if (length <= 0)
	    return;

	int i;
	char c;
	for (i = skipWhiteSpace(parameterList, 0);
		i < length && (c = parameterList.charAt(i)) == ';';
		i = skipWhiteSpace(parameterList, i)) {
	    int lastIndex;
	    String name;
	    String value;

	    //    eat the ';'
	    i++;

	    //    now parse the parameter name

	    //    skip whitespace
	    i = skipWhiteSpace(parameterList, i);

	    // tolerate trailing semicolon, even though it violates the spec
	    if (i >= length)
		return;

	    //    find the end of the token char run
	    lastIndex = i;
	    while ((i < length) && isTokenChar(parameterList.charAt(i)))
		i++;

	    name = parameterList.substring(lastIndex, i).
						toLowerCase(Locale.ENGLISH);

	    //    now parse the '=' that separates the name from the value
	    i = skipWhiteSpace(parameterList, i);

	    if (i >= length || parameterList.charAt(i) != '=')
		throw new MimeTypeParseException(
		    "Couldn't find the '=' that separates a " +
		    "parameter name from its value.");

	    //    eat it and parse the parameter value
	    i++;
	    i = skipWhiteSpace(parameterList, i);

	    if (i >= length)
		throw new MimeTypeParseException(
			"Couldn't find a value for parameter named " + name);

	    //    now find out whether or not we have a quoted value
	    c = parameterList.charAt(i);
	    if (c == '"') {
		//    yup it's quoted so eat it and capture the quoted string
		i++;
		if (i >= length)
		    throw new MimeTypeParseException(
			    "Encountered unterminated quoted parameter value.");

		lastIndex = i;

		//    find the next unescaped quote
		while (i < length) {
		    c = parameterList.charAt(i);
		    if (c == '"')
			break;
		    if (c == '\\') {
			//    found an escape sequence
			//    so skip this and the
			//    next character
			i++;
		    }
		    i++;
		}
		if (c != '"')
		    throw new MimeTypeParseException(
			"Encountered unterminated quoted parameter value.");

		value = unquote(parameterList.substring(lastIndex, i));
		//    eat the quote
		i++;
	    } else if (isTokenChar(c)) {
		//    nope it's an ordinary token so it
		//    ends with a non-token char
		lastIndex = i;
		while (i < length && isTokenChar(parameterList.charAt(i)))
		    i++;
		value = parameterList.substring(lastIndex, i);
	    } else {
		//    it ain't a value
		throw new MimeTypeParseException(
			"Unexpected character encountered at index " + i);
	    }

	    //    now put the data into the hashtable
	    parameters.put(name, value);
	}
	if (i < length) {
	    throw new MimeTypeParseException(
		"More characters encountered in input than expected.");
	}
    }

    /**
     * Return the number of name-value pairs in this list.
     *
     * @return	the number of parameters
     */
    public int size() {
        return parameters.size();
    }

    /**
     * Determine whether or not this list is empty.
     *
     * @return	true if there are no parameters
     */
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    /**
     * Retrieve the value associated with the given name, or null if there
     * is no current association.
     *
     * @param name	the parameter name
     * @return		the parameter's value
     */
    public String get(final String name) {
        return (String)parameters.get(name.trim().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Set the value to be associated with the given name, replacing
     * any previous association.
     *
     * @param name	the parameter name
     * @param value	the parameter's value
     */
    public void set(final String name, final String value) {
        parameters.put(name.trim().toLowerCase(Locale.ENGLISH), value);
    }

    /**
     * Remove any value associated with the given name.
     *
     * @param name	the parameter name
     */
    public void remove(final String name) {
        parameters.remove(name.trim().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Retrieve an enumeration of all the names in this list.
     *
     * @return	an enumeration of all parameter names
     */
    public Enumeration<String> getNames() {
        return parameters.keys();
    }

    /**
     * Return a string representation of this object.
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(parameters.size() * 16);
			//    heuristic: 8 characters per field

        final Enumeration<String> keys = parameters.keys();
        while (keys.hasMoreElements()) {
            final String key = (String)keys.nextElement();
            buffer.append("; ");
            buffer.append(key);
            buffer.append('=');
	    buffer.append(quote((String)parameters.get(key)));
        }

        return buffer.toString();
    }

    //    below here be scary parsing related things

    /**
     * Determine whether or not a given character belongs to a legal token.
     */
    private static boolean isTokenChar(final char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    /**
     * return the index of the first non white space character in
     * rawdata at or after index i.
     */
    private static int skipWhiteSpace(final String rawdata, int i) {
        final int length = rawdata.length();
	while ((i < length) && Character.isWhitespace(rawdata.charAt(i)))
	    i++;
        return i;
    }

    /**
     * A routine that knows how and when to quote and escape the given value.
     */
    private static String quote(final String value) {
        boolean needsQuotes = false;

        //    check to see if we actually have to quote this thing
        final int length = value.length();
        for (int i = 0; (i < length) && !needsQuotes; i++) {
            needsQuotes = !isTokenChar(value.charAt(i));
        }

        if (needsQuotes) {
            final StringBuffer buffer = new StringBuffer();
            buffer.ensureCapacity((int)(length * 1.5));

            //    add the initial quote
            buffer.append('"');

            //    add the properly escaped text
            for (int i = 0; i < length; ++i) {
                final char c = value.charAt(i);
                if ((c == '\\') || (c == '"'))
                    buffer.append('\\');
                buffer.append(c);
            }

            //    add the closing quote
            buffer.append('"');

            return buffer.toString();
        } else {
            return value;
        }
    }

    /**
     * A routine that knows how to strip the quotes and
     * escape sequences from the given value.
     */
    private static String unquote(final String value) {
        final int valueLength = value.length();
        final StringBuffer buffer = new StringBuffer();
        buffer.ensureCapacity(valueLength);

        boolean escaped = false;
        for (int i = 0; i < valueLength; ++i) {
            final char currentChar = value.charAt(i);
            if (!escaped && (currentChar != '\\')) {
                buffer.append(currentChar);
            } else if (escaped) {
                buffer.append(currentChar);
                escaped = false;
            } else {
                escaped = true;
            }
        }

        return buffer.toString();
    }
}
