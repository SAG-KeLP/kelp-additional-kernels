/*
 * Copyright 2014-2015 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * and Giovanni Da San Martino and Alessandro Moschitti
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
package it.uniroma2.sag.kelp.data.representation.tree.utils;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExamplePair;

/**
 * 
 * 
 * 
 * @author Giovanni Da San Martino
 *
 */
public class SelectRepresentationFromExample {
	
	private String representation;
	public enum representationSelectorInExample {
		LEFT, RIGHT, ALL
	}
	private representationSelectorInExample representationSelector; 
	
	public SelectRepresentationFromExample(String repr, 
			representationSelectorInExample reprSelector) {
		representation = repr;
		representationSelector = reprSelector;
	}

	public SelectRepresentationFromExample(String repr) {
		this(repr, representationSelectorInExample.ALL);
	}
	
	public String describe() {
		String msg = "Selecting representation -" + representation + "- ";
		if(representationSelector == representationSelectorInExample.ALL) {
			msg += "from Example";
		}else {
			msg += String.format("from %s example in Example Pair", 
					representationSelector == representationSelectorInExample.LEFT?"left":"right");
		}
		return msg;
	}

	/**
	 * Given an Example, extracts the object related to the given representation
	 * (determined at construction time of class <code>SelectRepresentationFromExample</code>).
	 *   
	 * @param example a KeLP Example
	 * @return an Object related to the given representation. It is the duty of
	 * the invoking class to cast the object to the appropriate class. 
	 */
	public Object extractRepresentation(Example example) {
		if(example instanceof ExamplePair){
			ExamplePair pair = (ExamplePair)example;
			if(representationSelector == representationSelectorInExample.LEFT) {
				return pair.getLeftExample().getRepresentation(representation);
			}else if(representationSelector == representationSelectorInExample.RIGHT){
				return pair.getRightExample().getRepresentation(representation);
			}
			/* representationSelector == representationSelectorInExample.ALL, 
			 * which means that both trees need to be selected. They will 
			 * be selected when the manipulator is invoked for each Example of 
			 * Example Pair, not now*/ 
		} else if(example instanceof Example){
			if(representationSelector == representationSelectorInExample.ALL) {
				return example.getRepresentation(representation);
			}
		}
		return null;
	}
	
	public boolean isInvokedForAllPairElements() {
		return representationSelector == representationSelectorInExample.ALL;
	}
	
	public String getRepresentationName() {
		return representation;
	}
	
	public void setSelectionToLeftExampleInPairOnly() {
		representationSelector = representationSelectorInExample.LEFT;
	}
	
	public void setSelectionToRightExampleInPairOnly() {
		representationSelector = representationSelectorInExample.RIGHT;
	}

	public void setSelectionAllExamples() {
		representationSelector = representationSelectorInExample.ALL;
	}

}
