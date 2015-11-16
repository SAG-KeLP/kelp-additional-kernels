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

package it.uniroma2.sag.kelp.data.manipulator;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.manipulator.Manipulator;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.wordspace.WordspaceI;

/**
 * 
 * This class implements functions to enrich <code>LexicalStructureElement</code>
 * s with a vector from the Word Space. This is useful to avoid the
 * computational cost of re-load vectors for estimating a similarity function
 * within the <code>VectorBasedStructureElementSimilarity</code>
 * 
 * @author Danilo Croce
 * 
 */
public class LexicalStructureElementManipulator implements Manipulator {

	public static final String DEFAULT_ENRICHMENT_NAME = "LEXICAL_ENRICHMENT";
	
	private WordspaceI wordSpace;

	private String representationToBeEnriched;
	
	private String enrichmentName;

	public LexicalStructureElementManipulator(
			WordspaceI wordSpace,
			String representationToBeEnriched, String enrichmentName) {
		super();
		this.wordSpace = wordSpace;
		this.representationToBeEnriched = representationToBeEnriched;
		this.enrichmentName = enrichmentName;
	}
	public LexicalStructureElementManipulator(WordspaceI wordSpace, String representationToBeEnriched){
		this(wordSpace, representationToBeEnriched, DEFAULT_ENRICHMENT_NAME);
	}

	/**
	 * This function enriches each <code>LexicalStructureElement</code> in the
	 * tree with a vector from the Word Space
	 * 
	 * @param treeRepresentation
	 *            The tree to be enriched
	 */
	public static void enrichTreeRepresentation(TreeRepresentation treeRepresentation,
			WordspaceI wordSpace, String enrichmentName) {
		for (TreeNode treeNode : treeRepresentation.getAllNodes()) {
			StructureElement content = treeNode.getContent();
			if (content instanceof LexicalStructureElement) {
				Vector vector = wordSpace.getVector(content.getTextFromData());
				content.addAdditionalInformation(enrichmentName, vector);
			}
		}
	}

	@Override
	public void manipulate(Example example) {
		TreeRepresentation tree = (TreeRepresentation) example.getRepresentation(representationToBeEnriched);
		if(tree!=null){
			enrichTreeRepresentation(tree, wordSpace, enrichmentName);
		}
	
	}

}
