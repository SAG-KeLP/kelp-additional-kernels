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

package it.uniroma2.sag.kelp.data.representation.tree.node.similarity;

import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

/**
 * Evaluates the similarity between two TreeNodes comparing their StructureElements 
 * using a <code>StructureElementSimilarityI</code>
 * 
 * @author Simone Filice
 *
 */
public class ContentBasedTreeNodeSimilarity implements TreeNodeSimilarity{
	
	private StructureElementSimilarityI structureElementSimilarity;

	/**
	 * Constructor 
	 * 
	 * @param structureElementSimilarity the similarity to be applied on the contents of two
	 * TreeNode by the method <code>getSimilarity</code>
	 */
	public ContentBasedTreeNodeSimilarity(StructureElementSimilarityI structureElementSimilarity){
		this.structureElementSimilarity = structureElementSimilarity;
	}
	
	@Override
	public float getSimilarity(TreeNode nodeA, TreeNode nodeB) {
		return structureElementSimilarity.sim(nodeA.getContent(), nodeB.getContent());
	}

}
