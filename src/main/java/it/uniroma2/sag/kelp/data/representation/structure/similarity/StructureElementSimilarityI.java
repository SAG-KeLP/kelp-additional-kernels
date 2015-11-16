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

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * This interface is used to implement similarity functions between Structured
 * Element
 * 
 * @author Danilo Croce
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(StructureElementSimilarityTypeResolver.class)
public interface StructureElementSimilarityI {

	/**
	 * This function measure the similarity between structure elements
	 * 
	 * @param sx
	 *            the first structure element
	 * @param sd
	 *            the second structure element
	 * @return the similarity between two structure elements
	 */
	public float sim(StructureElement sx, StructureElement sd);
}
