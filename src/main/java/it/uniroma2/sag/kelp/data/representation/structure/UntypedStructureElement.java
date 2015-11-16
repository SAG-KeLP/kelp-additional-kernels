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
 * It represent a StuctureElement without type. It is the default element.
 * 
 * @author Simone Filice, Danilo Croce
 * 
 */
@JsonTypeName("NOTYPE")
public class UntypedStructureElement extends StructureElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4121141061396740459L;
	/**
	 * The label of the element
	 */
	private String label;

	public UntypedStructureElement() {
		
	}

	/**
	 * @param label
	 *            The label of the element
	 */
	public UntypedStructureElement(String label) {
		this.label = label;
	}

	/**
	 * @return The label of the element
	 */
	public String getLabel() {
		return label;
	}

	@Override
	public String getTextFromData() {
		return label;
	}

	@Override
	public void setDataFromText(String structureElementDescription)
			throws Exception {
		this.label = structureElementDescription;

	}

	/**
	 * @param label
	 *            The label of the element
	 */
	public void setLabel(String label) {
		this.label = label;
	}


}
