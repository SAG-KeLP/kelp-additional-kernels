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

package it.uniroma2.sag.kelp.data.representation.structure.similarity;

import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.wordspace.WordspaceI;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * This class implements a similarity function between
 * <code>StructureElement</code>s. A strict label matching is applied on all 
 * structure elements whose type differs <code>LexicalStructureElement</code>. 
 * Similarity between <code>LexicalStructureElement</code>s is estimated by projecting
 * represented words in a WordSpace and evaluating a Kernel function between
 * these vectors. By default a dot product provided by a LinearKernel is estimated. 
 * </code>
 * 
 * @author Danilo Croce, Simone Filice
 * 
 */
@JsonTypeName("lexical_similarity")
public class LexicalStructureElementSimilarity extends
VectorBasedStructureElementSimilarity {
	//	private final static Logger logger = LoggerFactory
	//			.getLogger(StructureElementSimilarityOnRel.class);

	private boolean allowDifferentPOS = false;
	private boolean ignorePosInLemmaMatches = false;


	public LexicalStructureElementSimilarity() {

	}

	/**
	 * 
	 * @param matrixPath
	 *            The path of the wordSpace to be loaded.
	 *            By default the wordpspace will be of the class <code>Wordpspace</code>
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public LexicalStructureElementSimilarity(WordspaceI wordspace)
			throws NumberFormatException, IOException {
		this.setWordspace(wordspace);
	}

	@Override
	public float sim(StructureElement sx, StructureElement sz) {
		/*
		 * Both elements must have the same type
		 */
		if (!sx.getClass().equals(sz.getClass())) {
			return 0;
		}

		return getContentSimilarity(sx, sz);


	}


	private float getContentSimilarity(StructureElement element1, StructureElement element2){
		
		
		String text1 = element1.getTextFromData();
		String text2 = element2.getTextFromData();
		
		/*
		 * If both elements have the same type, if they have the same label, the
		 * similarity is 1. It is true for SYNT and POS elements
		 */
		if(text1.equals(text2)){
			return 1;
		}

		if(element1 instanceof LexicalStructureElement ){
			LexicalStructureElement lex1 = (LexicalStructureElement) element1;
			LexicalStructureElement lex2 = (LexicalStructureElement) element2;
			
			if(ignorePosInLemmaMatches){
				if(lex1.getLemma().equals(lex2.getLemma())){
					return 1;
				}
			}
			
			if(!allowDifferentPOS){
				String pos1 = lex1.getPos();
				String pos2 = lex2.getPos();
				if(!pos1.equals(pos2)){
					return 0;
				}
			}
			Vector vect1 = this.getCorrespondingVector(element1);
			Vector vect2 = this.getCorrespondingVector(element2);
			
			float sim = this.getSimilarity(vect1, vect2);
			if(sim>0){
				return sim;
			}
	
		}
		
		return 0;
	}

	/**
	 * Returns whether the similarity between words having different Part-of-Speech is allowed
	 * or if it must be set to 0
	 * 
	 * @return whether the similarity between words having different Part-of-Speech is allowed
	 */
	public boolean getAllowDifferentPOS() {
		return allowDifferentPOS;
	}

	/**
	 * Sets whether the similarity between words having different Part-of-Speech is allowed
	 * or if it must be set to 0
	 * 
	 * @param allowDifferentPOS
	 */
	public void setAllowDifferentPOS(boolean allowDifferentPOS) {
		this.allowDifferentPOS = allowDifferentPOS;
	}

	/**
	 * Returns whether two lexical structure elements must provide a perfect match if their lemmas are the same,
	 * regardless their part-of-speeches
	 * 
	 * @return whether two lexical structure elements must provide a perfect match if their lemmas are the same,
	 * regardless their part-of-speeches
	 */
	public boolean getIgnorePosInLemmaMatches() {
		return ignorePosInLemmaMatches;
	}

	/**
	 * Sets whether two lexical structure elements must provide a perfect match if their lemmas are the same,
	 * regardless their part-of-speeches
	 * 
	 * @param ignorePosInLemmaMatches whether two lexical structure elements must provide a perfect match if their lemmas are the same,
	 * regardless their part-of-speeches
	 */
	public void setIgnorePosInLemmaMatches(boolean ignorePosInLemmaMatches) {
		this.ignorePosInLemmaMatches = ignorePosInLemmaMatches;
	}


}
