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

package it.uniroma2.sag.kelp.data.representation.sequence;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

import java.io.Serializable;

/**
 * A SequenceElement is a generic element in a SequenceRepresentation
 * 
 * @author Simone Filice
 * 
 */
public class SequenceElement implements Serializable{

	
	private static final long serialVersionUID = -468185636685287237L;

	private StructureElement content;
	
	private SequenceElement previous = null;
	private SequenceElement next = null;
	
	public SequenceElement(StructureElement content){
		this.content = content;
	}
	
	public SequenceElement(StructureElement content, SequenceElement previous, SequenceElement next){
		this.content = content;
		this.previous = previous;
		this.next = next;
	}
	
	/**
	 * Returns the content of this SequenceElement
	 * 
	 * @return the content
	 */
	public StructureElement getContent() {
		return content;
	}

	/**
	 * Sets the content of this SequenceElement
	 * 
	 * @param content the content to set
	 */
	public void setContent(StructureElement content) {
		this.content = content;
	}

	/**
	 * Returns the previous element in the sequence
	 * 
	 * @return the previous element
	 */
	public SequenceElement getPrevious() {
		return previous;
	}

	/**
	 * Sets the previous element in the sequence
	 * 
	 * @param previous the previous to set
	 */
	public void setPrevious(SequenceElement previous) {
		this.previous = previous;
	}

	/**
	 * Returns the next element in the sequence
	 * 
	 * @return the next element of the sequence
	 */
	public SequenceElement getNext() {
		return next;
	}

	/**
	 * Sets the previous element in the sequence
	 * 
	 * @param next the next element to set
	 */
	public void setNext(SequenceElement next) {
		this.next = next;
	}

}
