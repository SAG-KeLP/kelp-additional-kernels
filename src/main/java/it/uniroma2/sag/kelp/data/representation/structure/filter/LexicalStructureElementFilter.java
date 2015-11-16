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

package it.uniroma2.sag.kelp.data.representation.structure.filter;

import java.util.Set;

import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

/**
 * This implementation of <code>StructureElementFilter</code> selects only LexicalStructureElements whose
 * lemma is not a stopword and whose pos is among the set of posOfInterest
 * 
 * @author Simone Filice
 *
 */
public class LexicalStructureElementFilter implements StructureElementFilter{

	private Set<String> stopwords;
	private Set<String> posOfInterest;
	
	/**
	 * Constructor for LexicalStructureElementFilter
	 * 
	 * @param stopwords the set of stopwords (i.e., lemmas not to be selected)
	 * @param posOfInterest the set of part-of-speeches of interest (i.e., lexicalStructureElements
	 * whose pos is out of this set will not be selected by the method <code>isElementOfInterest</code>)
	 */
	public LexicalStructureElementFilter(Set<String> stopwords,
			Set<String> posOfInterest) {

		this.stopwords = stopwords;
		this.posOfInterest = posOfInterest;
	}

	@Override
	public boolean isElementOfInterest(StructureElement element) {
		if(element instanceof LexicalStructureElement){
			LexicalStructureElement lexical = (LexicalStructureElement) element;
			String lemma = lexical.getLemma();
			if(stopwords.contains(lemma)){
				return false;
			}
			
			String pos = lexical.getPos();
			if(posOfInterest.contains(pos)){
				return true;
			}
		}
		return false;
	}

}
