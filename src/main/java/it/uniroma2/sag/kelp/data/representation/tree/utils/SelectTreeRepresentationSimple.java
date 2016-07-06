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
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;

/**
 * A class for selecting a tree from an Example. 
 * It simply selects the tree corresponding to the representation passed as 
 * parameter to the constructor.  
 * The class implements the interface <code>SelectTreeRepresentationInterface</code>.
 *  
 * @author Giovanni Da San Martino
 *
 */
public class SelectTreeRepresentationSimple implements SelectTreeRepresentationInterface {

	protected String representation;
	
	public SelectTreeRepresentationSimple(String repr) {
		representation = repr;
	}

	@Override
	public String describe() {
		return "Selecting tree whose representation name is -" + representation 
				+ "- from Example";
	}
	
	@Override
	public TreeRepresentation GetTreeRepresentation(Example example) {
		if(example instanceof ExamplePair){
			return null;
		}
		return (TreeRepresentation) example.getRepresentation(representation);
	}
	
}
