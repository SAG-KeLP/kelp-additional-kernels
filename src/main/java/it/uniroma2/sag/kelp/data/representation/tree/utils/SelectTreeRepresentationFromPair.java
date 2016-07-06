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
 * 
 * 
 * 
 * @author Giovanni Da San Martino
 *
 */
public class SelectTreeRepresentationFromPair implements SelectTreeRepresentationInterface {
	
	private String representation; 
	private boolean firstTree;
	
	public SelectTreeRepresentationFromPair(String repr, boolean firstTree) {
		representation = repr;
		this.firstTree = firstTree;
	}

	@Override
	public String describe() {
		return String.format("Selecting tree with representation %s from %s "
				+ "example of an ExamplePair", representation, firstTree?"left":"right");
	}

	@Override
	public TreeRepresentation GetTreeRepresentation(Example example) {
		if(example instanceof ExamplePair){
			ExamplePair pair = (ExamplePair)example;
			if(firstTree) {
				return (TreeRepresentation) pair.getLeftExample().getRepresentation(representation);
			}else{
				return (TreeRepresentation) pair.getRightExample().getRepresentation(representation);
			}
		}
		return null;
	}
}
