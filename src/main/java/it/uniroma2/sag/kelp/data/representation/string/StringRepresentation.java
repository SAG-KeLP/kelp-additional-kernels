/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.data.representation.string;

import it.uniroma2.sag.kelp.data.representation.Representation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * String representation. <br>
 * Useful for String or WordSequence Kernel function
 * Useful also for comments
 * 
 * @author Giuseppe Castellucci
 */

@JsonTypeName("S")
public class StringRepresentation implements Representation {
	private Logger logger = LoggerFactory.getLogger(StringRepresentation.class);
	private static final long serialVersionUID = 7545341469080995862L;

	private String string;
	private char[] charArray;

	/**
	 * Empty constructor necessary for making <code>RepresentationFactory</code>
	 * support this implementation.
	 * 
	 * @param featureVector
	 *            is an array of feature values
	 */
	public StringRepresentation() {
	}

	@Override
	public void setDataFromText(String string) {
		this.string = string;
		this.charArray = string.toCharArray();
	}

	/**
	 * Initializing constructor.
	 * 
	 * @param representation
	 *            is the string representation
	 */
	public StringRepresentation(String representation) {
		this.string = representation;
		this.charArray = string.toCharArray();
	}

	public char[] getCharArray() {
		return charArray;
	}

	@Override
	public boolean equals(Object representation) {
		if (representation == null) {
			return false;
		}
		if (this == representation) {
			return true;
		}
		if (representation instanceof StringRepresentation) {
			StringRepresentation that = (StringRepresentation) representation;
			return that.toString().equals(this.toString());
		}

		return false;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public String getTextFromData() {
		return this.toString();
	}
	
	@Override
	public boolean isCompatible(Representation rep) {
		if (!( rep instanceof StringRepresentation)){
			logger.error("incompatible representations: " + this.getClass().getSimpleName() + " vs " + rep.getClass().getSimpleName());
			return false;
		}
		return true;
	}
}
