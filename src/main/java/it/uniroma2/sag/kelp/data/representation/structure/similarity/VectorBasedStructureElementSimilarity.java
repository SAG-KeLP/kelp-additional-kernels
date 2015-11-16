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

import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.manipulator.LexicalStructureElementManipulator;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.wordspace.WordspaceI;

/**
 * This class provides a useful abstraction of a StructureElement similarity based on a wordspace.
 * Each StructureElement can be associated to a vector. Then the similarity between two structure elements is a 
 * the kernel operation between their corresponding vectors.
 * If the structure elements have been enriched using a manipulator (i.e., LexicalStructureElementEnricher), 
 * the associated vectors are directly retrieved. Otherwise they are retrieved by a wordspace.
 * 
 * @author Danilo Croce, Simone Filice
 * 
 */
public abstract class VectorBasedStructureElementSimilarity implements StructureElementSimilarityI {
	
	private String enrichmentName = LexicalStructureElementManipulator.DEFAULT_ENRICHMENT_NAME;
	public static final String VECTOR_NAME = "vector";
	private static final Kernel DEFAULT_KERNEL = null;
	private SimpleExample example1 = new SimpleExample();
	private SimpleExample example2 = new SimpleExample();
	
	private WordspaceI wordspace;
	
	/**
	 * The kernel function applied between vectors
	 */
	private Kernel kernel = DEFAULT_KERNEL;

	/**
	 * Returns the kernel used in comparing two vectors
	 * 
	 * @return the kernel used in comparing two vectors
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * Sets the kernel to be used in comparing two vectors
	 * 
	 * <p>
	 * NOTE: the kernel cache mechanism cannot be enabled. Thus the 
	 * cache is automatically disabled to <code>kernel</code>
	 * 
	 * @param kernel the kernel to be used in comparing two vectors
	 */
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
		if(kernel!=null)
			kernel.disableCache();
	}

	/**
	 * Returns the wordspace from which the vectors associated to a word must be retrieved 
	 * 
	 * @return the wordspace from which the vectors associated to a word must be retrieved 
	 */
	public WordspaceI getWordspace() {
		return wordspace;
	}

	/**
	 * Sets the wordspace from which the vectors associated to a word must be retrieved 
	 * 
	 * @param wordspace the wordspace from which the vectors associated to a word must be retrieved 
	 */
	public void setWordspace(WordspaceI wordspace) {
		this.wordspace = wordspace;
	}
	
	/**
	 * Returns the identifier of the vectors associated to 
	 * a StructureElement during the manipulation operation performed
	 * by a Manipulator (i.e. LexicalStructureElementEnricher)
	 * 
	 * @return the enrichmentName
	 */
	public String getEnrichmentName() {
		return enrichmentName;
	}

	/**
	 * Sets the identifier of the vectors associated to 
	 * a StructureElement during the manipulation operation performed
	 * by a Manipulator (i.e. LexicalStructureElementEnricher)
	 * 
	 * This manipulation optimize the similarity evaluation cause it avoids
	 * retrieving vectors from the wordspaces
	 * 
	 * @param enrichmentName the enrichmentName to set
	 */
	public void setEnrichmentName(String enrichmentName) {
		this.enrichmentName = enrichmentName;
	}

	/**
	 * Returns the vector associated to <code>element</code>. It firstly tries to
	 * check whether a vector as been attached as additional information to <code>element</code>.
	 * If no vector has been attached it retrieves the vector associated to the textual representation
	 * of <code>element</code> (applying <code>element.getTextFromData()</code>) in the wordspace.
	 * 
	 * 
	 * @param element the structure element whose corresponding vector must be retrieved
	 * @return the vector associated to <code>element</code>
	 */
	protected Vector getCorrespondingVector(StructureElement element){
		
		if(element.containsAdditionalInfo(enrichmentName)){
			return (Vector) element.getAdditionalInformation(enrichmentName);
		}else{
			return this.wordspace.getVector(element.getTextFromData());
		}
		
	}
	
	/**
	 * Returns the similarity between <code>vector1</code> and <code>vector2</code> 
	 * computed using the kernel function
	 * 
	 * @param vector1 the first vector to be compared
	 * @param vector2 the second vector to be compared
	 * @return the similarity between <code>vector1</code> and <code>vector2</code>
	 */
	protected float getSimilarity(Vector vector1, Vector vector2){
		if(vector1==null || vector2==null){
			return 0;
		}
		if(kernel==null){
			return vector1.innerProduct(vector2);
		}
		example1.addRepresentation(VECTOR_NAME, vector1);
		example2.addRepresentation(VECTOR_NAME, vector2);
		return this.kernel.innerProduct(example1, example2);
	}
}
