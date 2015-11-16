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

package it.uniroma2.sag.kelp.data.representation.structure;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * It represent a StuctureElement that contains the syntactic informations, i.e.
 * the <code>syntacticRelation</code>
 * 
 * @author Simone Filice, Danilo Croce
 * 
 */
@JsonTypeName("SYNT")
public class SyntacticStructureElement extends StructureElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1890367479304586367L;
	/**
	 * The syntactic relation
	 */
	private String syntacticRelation;

	public SyntacticStructureElement() {

	}

	/**
	 * @param syntacticRelation
	 *            The syntactic relation
	 */
	public SyntacticStructureElement(String syntacticRelation) {
		this.syntacticRelation = syntacticRelation;
	}

	/**
	 * @return the The syntactic relation
	 */
	public String getSyntacticRelation() {
		return syntacticRelation;
	}

	@Override
	public String getTextFromData() {
		return syntacticRelation;
	}

	@Override
	public void setDataFromText(String structureElementDescription)
			throws Exception {
		this.syntacticRelation = structureElementDescription;

	}

	/**
	 * @param syntacticRelation
	 *            The syntactic relation to set
	 */
	public void setSyntacticRelation(String syntacticRelation) {
		this.syntacticRelation = syntacticRelation;
	}

}
