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

package it.uniroma2.sag.kelp.data.representation.structure.similarity;

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

/**
 * Implements a similarity between StructureElements applying a hard match on their 
 * textual representation (i.e., the method <code>sim</code> will return 1 if two structure 
 * elements matches, 0 otherwise)
 * 
 * @author Simone Filice
 *
 */
@JsonTypeName("exactMatching")
public class ExactMatchingStructureElementSimilarity implements StructureElementSimilarityI{

	

	private boolean ignorePosOnLexicals = false;

	
	public ExactMatchingStructureElementSimilarity() {
		
	}

	/**
	 * Constructor for ExactMatchingStructureElementSimilarity
	 * 
	 * @param whether the part-of-speech must ignored in comparing two
	 * LexicalStructureElements
	 */
	public ExactMatchingStructureElementSimilarity(boolean ignorePosOnLexicals) {
		this.ignorePosOnLexicals = ignorePosOnLexicals;
	}


	@Override
	public float sim(StructureElement sx, StructureElement sd) {
		if(sx.getTextFromData().equals(sd.getTextFromData())){
			return 1;
		}
		if(ignorePosOnLexicals){
			if(sx instanceof LexicalStructureElement && sd instanceof LexicalStructureElement){
				LexicalStructureElement lex1 = (LexicalStructureElement) sx;
				LexicalStructureElement lex2 = (LexicalStructureElement) sd;
				if (lex1.getLemma().equals(lex2.getLemma())){
					return 1;
				}
			}
		}
		return 0;
	}


	/**
	 * Returns whether the part-of-speech is ignored in comparing two
	 * LexicalStructureElements
	 * 
	 * @return whether the part-of-speech is ignored in comparing two
	 * LexicalStructureElements
	 */
	public boolean getIgnorePosOnLexicals() {
		return ignorePosOnLexicals;
	}


	/**
	 * Sets whether the part-of-speech must be ignored in comparing two
	 * LexicalStructureElements
	 * 
	 * @param ignorePosOnLexicals whether the part-of-speech must be ignored in comparing two
	 * LexicalStructureElements
	 */
	public void setIgnorePosOnLexicals(boolean ignorePosOnLexicals) {
		this.ignorePosOnLexicals = ignorePosOnLexicals;
	}

}
