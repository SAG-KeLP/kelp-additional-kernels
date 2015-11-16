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

package it.uniroma2.sag.kelp.data.representation.structure.similarity.compositional;

import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.structure.CompositionalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.LexicalStructureElementSimilarity;
import it.uniroma2.sag.kelp.wordspace.WordspaceI;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class implements a specific node similarity that computes the similarity
 * between compositional nodes, by applying the "sum" operator.
 * 
 * </br></br> For more details: </br> [Annesi et al(2014)] Paolo Annesi, Danilo
 * Croce, Roberto Basili (2014) Semantic Compositionality in Tree Kernels. In:
 * Proceedings of the 23rd ACM International Conference on Conference on
 * Information and Knowledge Management
 * 
 * @author Giuseppe Castellucci
 */
public abstract class CompositionalNodeSimilarity extends
		LexicalStructureElementSimilarity {
	protected String enrichmentName;

	private boolean posRestriction;
	private boolean syntacticRestriction;

	/**
	 * Default constructor, used only for JSON serialization/deserialization
	 * purposes.
	 */
	public CompositionalNodeSimilarity() {
		posRestriction = true;
		syntacticRestriction = false;
	}

	/**
	 * Constructor that enable to specify the wordspace path. The kernel applied
	 * to compute the inner product is the one obtained through the
	 * <code>getDefaultKernel</code> method. <code>posRestriction</code>
	 * parameter is set to true. <code>syntacticRestriction</code> parameter is
	 * set to false.
	 * 
	 * @param matrixPath
	 *            The path of the Word Space to be loaded
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public CompositionalNodeSimilarity(WordspaceI wordspace)
			throws NumberFormatException, IOException {
		super(wordspace);
		posRestriction = true;
		syntacticRestriction = false;
	}

	/**
	 * Constructor that enable to specify the wordspace path. The kernel applied
	 * to compute the inner product is the one obtained through the
	 * <code>getDefaultKernel</code> method.
	 * 
	 * @param matrixPath
	 *            The path of the Word Space to be loaded.
	 * @param posRestriction
	 *            if true, two nodes will be compared only if heads and
	 *            modifiers of these nodes will match.
	 * @param syntacticRestriction
	 *            if true, two nodes will be compared only if the dependency
	 *            relation of these will match.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public CompositionalNodeSimilarity(WordspaceI wordspace,
			boolean posRestriction, boolean syntacticRestriction)
			throws NumberFormatException, IOException {
		this(wordspace);
		this.posRestriction = posRestriction;
		this.syntacticRestriction = syntacticRestriction;
	}

	/**
	 * @param enrichmentName
	 *            the enrichmentName to set
	 */
	public void setEnrichmentName(String enrichmentName) {
		this.enrichmentName = enrichmentName;
	}

	/**
	 * @return the enrichmentName
	 */
	public String getEnrichmentName() {
		return enrichmentName;
	}

	/**
	 * @return the posRestriction
	 */
	public boolean isPosRestriction() {
		return posRestriction;
	}

	/**
	 * @param posRestriction
	 *            the posRestriction to set
	 */
	public void setPosRestriction(boolean posRestriction) {
		this.posRestriction = posRestriction;
	}

	/**
	 * @return the syntacticRestriction
	 */
	public boolean isSyntacticRestriction() {
		return syntacticRestriction;
	}

	/**
	 * @param syntacticRestriction
	 *            the syntacticRestriction to set
	 */
	public void setSyntacticRestriction(boolean syntacticRestriction) {
		this.syntacticRestriction = syntacticRestriction;
	}

	/**
	 * This method computes the similarity between two compositional nodes by
	 * applying the "sum" operator.
	 */
	@Override
	public float sim(StructureElement sx, StructureElement sz) {
		if (!sx.getClass().equals(sz.getClass())) {
			return 0;
		} else if (sx.getTextFromData().equals(sz.getTextFromData())) {
			return 1.0f;
		} else if (sx instanceof CompositionalStructureElement) {
			CompositionalStructureElement csx = (CompositionalStructureElement) sx;
			CompositionalStructureElement csz = (CompositionalStructureElement) sz;

			if (csx.getHead() == null && csz.getHead() == null) {
				return super.sim(csz.getModifier(), csz.getModifier());
			}

			if (csx.getModifier() == null && csz.getModifier() == null) {
				return super.sim(csz.getHead(), csz.getHead());
			}

			if (posRestriction) {
				if (!csx.matchPosWith(csz))
					return 0.0f;

				if (syntacticRestriction) {
					if (!csx.matchSyntacticRelationWith(csz))
						return 0.0f;

					return getScore(csx, csz);
				} else {
					return getScore(csx, csz);
				}
			} else {
				if (syntacticRestriction) {
					if (!csx.matchSyntacticRelationWith(csz))
						return 0.0f;

					return getScore(csx, csz);
				} else {
					return getScore(csx, csz);
				}
			}
		} else {
			super.sim(sx, sz);
		}
		return 0;
	}

	protected abstract float getScore(CompositionalStructureElement csx,
			CompositionalStructureElement csz);

	/**
	 * This method takes in input two LexicalStructureElement representing a
	 * head and a modifier. It returns an Example that contains a
	 * representation, whose name is <code>VECTOR_NAME</code>, containing the
	 * sum of the two vector representations of the head and modifier. In case
	 * of missing head, the resulting composition is obtained by copying the
	 * modifier in the head. In case of missing modifier, the resulting
	 * composition is obtained by copying the head in the modifier. In case of
	 * missing head and modifier, the method returns null.
	 * 
	 * @param head
	 * @param modifier
	 * @param ws
	 *            the StructureElementSimilarityWordSpace used to retrieve the
	 *            vector representations of head and modifiers.
	 * 
	 * @return the compositional Vector
	 */
	public abstract Vector getCompositionalInformationFor(
			LexicalStructureElement head, LexicalStructureElement modifier);
}
