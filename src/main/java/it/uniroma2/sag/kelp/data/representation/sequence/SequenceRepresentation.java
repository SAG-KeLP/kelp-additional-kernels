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

import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElementFactory;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * Sequence Representation used for example to represent a sentence (i.e. a sequence of words)
 * to be used in sequence kernels
 * 
 * @author Simone Filice
 */

@JsonTypeName("SQ")
public class SequenceRepresentation implements Representation{

	private Logger logger = LoggerFactory.getLogger(SequenceRepresentation.class);
	private static final String BEGIN_ELEMENT = "(";
	private static final String END_ELEMENT =")";

	private static final long serialVersionUID = -2845957944433203042L;
	
	private List<SequenceElement> elements;
	
	public SequenceRepresentation(){
		this.elements = new ArrayList<SequenceElement>();
	}
	
	/**
	 * Returns the elements of this sequence
	 * 
	 * @return the elements of this sequence
	 */
	@JsonIgnore
	public List<SequenceElement> getElements(){
		return this.elements;
	}
	
	@Override
	public void setDataFromText(String representationDescription)
			throws Exception {
		
		int beginIndex = representationDescription.indexOf(BEGIN_ELEMENT);
		
		
		SequenceElement previous = null;
		
		this.elements.clear();
		while(beginIndex!=-1){
			int endIndex = representationDescription.indexOf(END_ELEMENT, beginIndex+1);
			if(endIndex==-1){
				throw new IOException("Umbalanced parantheses in: " + representationDescription);
			}
			String elementContentDescription = representationDescription.substring(beginIndex+1, endIndex);
			StructureElement content = StructureElementFactory.getInstance().parseStructureElement(elementContentDescription);
			SequenceElement currentElement = new SequenceElement(content);
			currentElement.setPrevious(previous);
			
			this.elements.add(currentElement);
			if(previous!=null){
				previous.setNext(currentElement);
			}
			previous = currentElement;
			beginIndex = representationDescription.indexOf(BEGIN_ELEMENT, endIndex);
			
		}
		
	}

	@Override
	public String getTextFromData() {
		String text = "";
		for(SequenceElement element : this.elements){
			text += BEGIN_ELEMENT + StructureElementFactory.getTextualRepresentation(element.getContent()) + END_ELEMENT;
		}
		return text;
	}
	
	@Override
	public String toString(){
		return this.getTextFromData();
	}
	
	@Override
	public boolean isCompatible(Representation rep) {
		if (!( rep instanceof TreeRepresentation)){
			logger.error("incompatible representations: " + this.getClass().getSimpleName() + " vs " + rep.getClass().getSimpleName());
			return false;
		}
		return true;
	}

}
