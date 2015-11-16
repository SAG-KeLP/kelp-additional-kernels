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

package it.uniroma2.sag.kelp.data.representation.structure.similarity.compositional.product;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.manipulator.Manipulator;
import it.uniroma2.sag.kelp.data.representation.Vector;
import it.uniroma2.sag.kelp.data.representation.structure.CompositionalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.LexicalStructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.similarity.compositional.CompositionalNodeSimilarity;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.wordspace.WordspaceI;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class implements a specific node similarity that computes the similarity
 * between compositional nodes, by applying the "prod" operator, i.e. the
 * pointwise product operator.
 * 
 * </br></br> For more details: </br> [Annesi et al(2014)] Paolo Annesi, Danilo
 * Croce, Roberto Basili (2014) Semantic Compositionality in Tree Kernels. In:
 * Proceedings of the 23rd ACM International Conference on Conference on
 * Information and Knowledge Management
 * 
 * @author Giuseppe Castellucci
 */
public class CompositionalNodeSimilarityProduct extends
		CompositionalNodeSimilarity implements Manipulator {
	private String representationToBeEnriched;

	/**
	 * Default constructor, used only for JSON serialization/deserialization
	 * purposes.
	 */
	public CompositionalNodeSimilarityProduct() {
		super();
		enrichmentName = "COMP_PROD";
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
	public CompositionalNodeSimilarityProduct(WordspaceI wordspace)
			throws NumberFormatException, IOException {
		super(wordspace);
		enrichmentName = "COMP_PROD";
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
	public CompositionalNodeSimilarityProduct(WordspaceI wordspace,
			boolean posRestriction, boolean syntacticRestriction)
			throws NumberFormatException, IOException {
		super(wordspace, posRestriction, syntacticRestriction);
		enrichmentName = "COMP_PROD";
	}

	/**
	 * @return the representationToBeEnriched
	 */
	public String getRepresentationToBeEnriched() {
		return representationToBeEnriched;
	}

	/**
	 * @param representationToBeEnriched
	 *            the representationToBeEnriched to set
	 */
	public void setRepresentationToBeEnriched(String representationToBeEnriched) {
		this.representationToBeEnriched = representationToBeEnriched;
	}

	protected float getScore(CompositionalStructureElement csx,
			CompositionalStructureElement csz) {
		Vector storedSx = (Vector) csx.getAdditionalInformation(enrichmentName);
		if (storedSx == null && csx.containsAdditionalInfo(enrichmentName))
			return 0.0f;

		Vector storedSz = (Vector) csz.getAdditionalInformation(enrichmentName);
		if (storedSz == null && csz.containsAdditionalInfo(enrichmentName))
			return 0.0f;

		if (storedSx == null) {
			storedSx = getCompositionalInformationFor(csx.getHead(),
					csx.getModifier());
			if (storedSx == null) {
				return 0.0f;
			}
		}

		if (storedSz == null) {
			storedSz = getCompositionalInformationFor(csz.getHead(),
					csz.getModifier());
			if (storedSz == null) {
				return 0.0f;
			}
		}

		return getSimilarity(storedSx, storedSz);
	}

	/**
	 * This method takes in input two LexicalStructureElement representing a
	 * head and a modifier. It returns an Example that contains a
	 * representation, whose name is <code>VECTOR_NAME</code>, containing the
	 * pointwise product of the two Vector representations of the head and
	 * modifier. In case of missing head, the resulting composition is obtained
	 * by copying the modifier in the head. In case of missing modifier, the
	 * resulting composition is obtained by copying the head in the modifier. In
	 * case of missing head and modifier, the method returns null.
	 * 
	 * @param head
	 * @param modifier
	 * @param ws
	 *            the StructureElementSimilarityWordSpace used to retrieve the
	 *            Vector representations of head and modifiers.
	 * 
	 * @return the compositional Example
	 */
	public Vector getCompositionalInformationFor(LexicalStructureElement head,
			LexicalStructureElement modifier) {
		Vector hExample = getWordspace().getVector(head.getTextFromData());
		Vector mExample = getWordspace().getVector(modifier.getTextFromData());

		if (hExample == null && mExample == null) {
			return null;
		}

		if (hExample == null && mExample != null) {
			hExample = mExample;
		}

		if (mExample == null && hExample != null) {
			mExample = hExample;
		}

		return product(hExample, mExample);
	}

	private static Vector product(Vector hExample, Vector mExample) {
		Vector prod = hExample.copyVector();
		prod.pointWiseProduct(mExample);
		prod.normalize();

		return prod;
	}

	@Override
	public void manipulate(Example example) {
		TreeRepresentation repr = (TreeRepresentation) example
				.getRepresentation(representationToBeEnriched);
		if (repr != null)
			enrichTreeWithCompositionalProduct(repr);
	}

	/**
	 * This method enriches the CompositionalStructureElement{s} with the
	 * pointwise product of the vectors of head and modifiers.
	 * 
	 * @param repr
	 *            the representation to be enriched
	 */
	private void enrichTreeWithCompositionalProduct(TreeRepresentation repr) {
		for (TreeNode node : repr.getAllNodes()) {
			if (node.getContent() instanceof CompositionalStructureElement) {
				CompositionalStructureElement el = (CompositionalStructureElement) node
						.getContent();
				Vector compositionalInfo = getCompositionalInformationFor(
						el.getHead(), el.getModifier());

				el.addAdditionalInformation(enrichmentName, compositionalInfo);
			}
		}
	}
}
