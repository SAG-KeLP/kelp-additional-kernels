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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

/**
 * Implements a similarity between StructureElements that first verifies whether 
 * two structure elements contains the same additional informations in a list of
 * specified additional informations. If at least one additional information is contained in 
 * one of the structure element, but not in the other their similarity is set to 0.
 * Otherwise their similarity is estimated applying another structure element similarity
 * specified by the class field <code>baseSimilarity</code> 
 * 
 * @author Simone Filice
 *
 */
@JsonTypeName("sameAdditionalInfo")
public class SameAdditionalInfoStructureElementSimilarity implements StructureElementSimilarityI{

	List<String> additionalInfos;
	StructureElementSimilarityI baseSimilarity;

	public SameAdditionalInfoStructureElementSimilarity(){
		
	}
	
	public SameAdditionalInfoStructureElementSimilarity(List<String> additionalInfos, StructureElementSimilarityI baseSimilarity) {
		this.additionalInfos = additionalInfos;
		this.baseSimilarity = baseSimilarity;
	}
	
	/**
	 * Returns the list of additionalInfos two structure elements must
	 * both have or not have in order to have a non zero similarity
	 * 
	 * @return the additionalInfos
	 */
	public List<String> getAdditionalInfos() {
		return additionalInfos;
	}

	/**
	 * Sets the list of additionalInfos two structure elements must
	 * both have or not have in order to have a non zero similarity
	 * 
	 * @param additionalInfos the additionalInfos to set
	 */
	public void setAdditionalInfos(List<String> additionalInfos) {
		this.additionalInfos = additionalInfos;
	}
	
	/**
	 * Returns the base similarity applied when two structure elements have 
	 * the same additional infos
	 * 
	 * @return the baseSimilarity
	 */
	public StructureElementSimilarityI getBaseSimilarity() {
		return baseSimilarity;
	}

	/**
	 * Sets the base similarity applied when two structure elements have 
	 * the same additional infos
	 * 
	 * @param baseSimilarity the baseSimilarity to set
	 */
	public void setBaseSimilarity(StructureElementSimilarityI baseSimilarity) {
		this.baseSimilarity = baseSimilarity;
	}
	
	@Override
	public float sim(StructureElement sx, StructureElement sd) {
		for(String additionalInfo : additionalInfos){
			if(sx.containsAdditionalInfo(additionalInfo)!=sd.containsAdditionalInfo(additionalInfo)){
				return 0;
			}
		}
		return baseSimilarity.sim(sx, sd);
	}

}
