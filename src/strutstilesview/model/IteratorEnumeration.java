package strutstilesview.model;
/*
 * Converts an iterator to an enumerator.
 * Copyright (C) 2004-2010 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * Copyright (C) 2006 Jonathan Faivre-Vuillin
 * public dot lp at free dot fr
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See LICENSE.txt for details.
 */

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Converts an iterator to an enumerator.
 * <p>
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/Iterator_Enumeration.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @param <ElementType> Type of element being enumerated
 * @since ostermillerutils 1.03.00
 */
public class IteratorEnumeration implements Enumeration {

	/**********************
	 * Member variable(s) *
	 **********************/
	
	/**
	 * Iterator being converted to enumeration.
	 */
	private Iterator iterator;

	/******************
	 * Constructor(s) *
	 ******************/
	
	/**
	 * Create an Enumeration from an Iterator.
	 *
	 * @param iterator Iterator to convert to an enumeration.
	 *
	 * @since ostermillerutils 1.03.00
	 */
	public IteratorEnumeration(Iterator iterator){
		this.iterator = iterator;
	}

	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Tests if this enumeration contains more elements.
	 *
	 * @return true if and only if this enumeration object contains at least
	 * one more element to provide; false otherwise.
	 *
	 * @since ostermillerutils 1.03.00
	 */
	public boolean hasMoreElements(){
		return iterator.hasNext();
	}

	/**
	 * Returns the next element of this enumeration if this enumeration
	 * object has at least one more element to provide.
	 *
	 * @return the next element of this enumeration.
	 * @throws NoSuchElementException if no more elements exist.
	 *
	 * @since ostermillerutils 1.03.00
	 */
	public Object nextElement() throws NoSuchElementException {
		return iterator.next();
	}

}