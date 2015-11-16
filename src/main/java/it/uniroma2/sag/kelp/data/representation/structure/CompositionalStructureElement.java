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

import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * CompositionalStructureElement represents a compositional node. It is
 * characterized by a Head, a Modifier and the dependency relation linking them.
 * 
 * </br></br> For more details: </br> [Annesi et al(2014)] Paolo Annesi, Danilo
 * Croce, Roberto Basili (2014) Semantic Compositionality in Tree Kernels. In:
 * Proceedings of the 23rd ACM International Conference on Conference on
 * Information and Knowledge Management
 * 
 * @author Giuseppe Castellucci
 */
@JsonTypeName("COMP")
public class CompositionalStructureElement extends StructureElement {
	private static final long serialVersionUID = -1904392939080268356L;

	private Logger logger = LoggerFactory
			.getLogger(CompositionalStructureElement.class);

	private static final String ANG_O_BRACK = "<";
	private static final String ANG_C_BRACK = ">";
	private static final String HM_SEPARATOR = ",";

	private String dependencyRelation;
	private LexicalStructureElement head;
	private LexicalStructureElement modifier;

	@JsonIgnore
	private String textFromData;

	public CompositionalStructureElement() {
	}

	@Override
	public void setDataFromText(String structureElementDescription)
			throws Exception {
		logger.debug(structureElementDescription);
		int indexo = structureElementDescription.indexOf(ANG_O_BRACK);
		int indexc = structureElementDescription.indexOf(ANG_C_BRACK);
		int indexhm = structureElementDescription.indexOf(HM_SEPARATOR);

		this.dependencyRelation = structureElementDescription.substring(0,
				indexo);

		this.head = new LexicalStructureElement();
		this.head.setDataFromText(structureElementDescription.substring(
				indexo + 1, indexhm));

		this.modifier = new LexicalStructureElement();
		this.modifier.setDataFromText(structureElementDescription.substring(
				indexhm + 1, indexc));

		logger.debug("\t" + dependencyRelation);
		logger.debug("\t" + head.getTextFromData());
		logger.debug("\t" + modifier.getTextFromData());
	}

	@Override
	public String getTextFromData() {
		if (textFromData == null) {
			updateTextFromData();
		}
		return textFromData;
	}

	/**
	 * @return the dependencyRelation
	 */
	public String getDependencyRelation() {
		return dependencyRelation;
	}

	/**
	 * @param dependencyRelation
	 *            the dependencyRelation to set
	 */
	public void setDependencyRelation(String dependencyRelation) {
		this.dependencyRelation = dependencyRelation;
		updateTextFromData();
	}

	/**
	 * @return the head
	 */
	@JsonIgnore
	public LexicalStructureElement getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	@JsonIgnore
	public void setHead(LexicalStructureElement head) {
		this.head = head;
	}

	/**
	 * @return the modifier
	 */
	@JsonIgnore
	public LexicalStructureElement getModifier() {
		return modifier;
	}

	/**
	 * @param modifier
	 *            the modifier to set
	 */
	@JsonIgnore
	public void setModifier(LexicalStructureElement modifier) {
		this.modifier = modifier;
	}

	/**
	 * The text to be returned by the method <code>getTextFromData()</code> is
	 * built using the <code>head</code>, <code>modifier</code> and
	 * <code>dependencyRelation</code> of this element.
	 */
	private void updateTextFromData() {
		this.textFromData = dependencyRelation + "<" + head.getTextFromData()
				+ "," + modifier.getTextFromData() + ">";
	}

	/**
	 * This method verifies if this object and csz have the same Part of speech
	 * on the heads and modifiers
	 * 
	 * @param csz
	 * @return
	 */
	public boolean matchPosWith(CompositionalStructureElement csz) {
		return getHead().getPos().equals(csz.getHead().getPos())
				&& getModifier().getPos().equals(csz.getModifier().getPos());
	}

	/**
	 * This method verifies if the dependency relation between this object and
	 * csz matches.
	 * 
	 * @param csz
	 * @return
	 */
	public boolean matchSyntacticRelationWith(CompositionalStructureElement csz) {
		return getDependencyRelation().equals(csz.getDependencyRelation());
	}
}
