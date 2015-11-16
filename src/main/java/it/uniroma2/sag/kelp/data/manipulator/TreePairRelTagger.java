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

package it.uniroma2.sag.kelp.data.manipulator;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExamplePair;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.node.filter.TreeNodeFilter;
import it.uniroma2.sag.kelp.data.representation.tree.node.similarity.TreeNodeSimilarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * This manipulator establishes relations between two tree representations.
 * Given two trees whose nodes are \(N_A=\left \{ n_1^A, n_2^A... \right \}) and 
 * \(N_B=\left \{ n_1^B, n_2^B... \right \}), the manipulators firstly selects
 * two subsets \(S_A \subseteq N_A\) and \(S_B \subseteq N_B\) according to the
 * filtering policy defined by a <code>TreeNodeFilter</code>. Then the nodes 
 * \(n_i^A \in S_A\) and the nodes \(n_i^B \in S_B\) become anchors if their similarity 
 * evaluated with <code>nodeSimilarity</code> is higher than <code>simThreshold</code>.
 * Finally anchor nodes are marked using a <code>markingPolicy</code> and the same marking 
 * is applied to their ascendants (till <code>upwardPropagation</code> generations) and
 * descendants (till <code>downPropagation</code> generations).
 * 
 * @author Simone Filice
 *
 */
public class TreePairRelTagger implements Manipulator{

	/**
	 * The marking policy applied by <code>TreePairRelTagger</code>
	 * 
	 * @author Simone Filice
	 *
	 */
	public enum MARKING_POLICY{
		/**
		 * The node is marked by adding an additional info to its StructureElement 
		 */
		AS_ADDITIONAL_INFO,
		/**
		 * The node is marked by adding a prefix to the label (i.e. the textual description)
		 * of its StructureElement.
		 */
		ON_NODE_LABEL
	}

	public static final String DEFAULT_PREFIX = "REL-";

	private int upwardPropagation;
	private int downwordPropagation;
	private String markingPrefix = DEFAULT_PREFIX;
	private String representation;
	private TreeNodeFilter nodeFilter;
	private MARKING_POLICY markingPolicy = MARKING_POLICY.ON_NODE_LABEL;
	private TreeNodeSimilarity nodeSimilarity;
	private float simThreshold;

	/**
	 * Constructor for TreePairRelTagger
	 * <p>
	 * NOTE: the related nodes will be marked using DEFAULT_PREFIX. To specify an alternative prefix
	 * the <code>setMarkingPrefix</code> can be used
	 * 
	 * @param upwardPropagation the number of generations of ascendants to which the mark is propagated 
	 * @param downwordPropagation the number of generations of descendants to which the mark is propagated
	 * @param representation the name of the tree representations that must be related
	 * @param nodeFilter the policy to be applied in selecting potential anchor nodes
	 * @param markingPolicy the marking policy specifying what kind of mark must be applied
	 * @param nodeSimilarity the similarity metric between tree nodes 
	 * @param simThreshold the similarity threshold that two nodes should overcome to be considered anchors (and then marked)
	 *  
	 */
	public TreePairRelTagger(int upwardPropagation, int downwordPropagation,
			String representation, TreeNodeFilter nodeFilter,
			MARKING_POLICY markingPolicy, TreeNodeSimilarity nodeSimilarity,
			float simThreshold) {

		this.upwardPropagation = upwardPropagation;
		this.downwordPropagation = downwordPropagation;
		this.representation = representation;
		this.nodeFilter = nodeFilter;
		this.markingPolicy = markingPolicy;
		this.nodeSimilarity = nodeSimilarity;
		this.simThreshold = simThreshold;
	}

	/**
	 * Returns the prefix used to mark the related nodes
	 * 
	 * @return the markingPrefix
	 */
	public String getMarkingPrefix() {
		return markingPrefix;
	}

	/**
	 * Sets the prefix used to mark the related nodes
	 * 
	 * @param markingPrefix the markingPrefix to set
	 */
	public void setMarkingPrefix(String markingPrefix) {
		this.markingPrefix = markingPrefix;
	}

	@Override
	public void manipulate(Example example) {
		if(example instanceof ExamplePair){
			ExamplePair pair = (ExamplePair)example;
			TreeRepresentation rep1 = (TreeRepresentation) pair.getLeftExample().getRepresentation(representation);
			TreeRepresentation rep2 = (TreeRepresentation) pair.getRightExample().getRepresentation(representation);
			if(rep1 != null && rep2!= null){
				establishNodeRelations(rep1, rep2);
			}
		}

	}

	/**
	 * Establishes relations between <code>treeA</code> and <code>treeA</code>
	 * Let \(N_A=\left \{ n_1^A, n_2^A... \right \}) be the nodes of <code>treeA</code> and 
	 * \(N_B=\left \{ n_1^B, n_2^B... \right \}) be the nodes of <code>treeB</code>,
	 * the method firstly selects two subsets \(S_A \subseteq N_A\) and \(S_B \subseteq N_B\) according to the
	 * filtering policy defined by a <code>TreeNodeFilter</code>. Then the nodes 
	 * \(n_i^A \in S_A\) and the nodes \(n_i^B \in S_B\) become anchors if their similarity 
	 * evaluated with <code>nodeSimilarity</code> is higher than <code>simThreshold</code>.
	 * Finally anchor nodes are marked using a <code>markingPolicy</code> and the same marking 
	 * is applied to their ascendants (till <code>upwardPropagation</code> generations) and
	 * descendants (till <code>downPropagation</code> generations).
	 * 
	 * @param treeA
	 * @param treeB
	 */
	public void establishNodeRelations(TreeRepresentation treeA, TreeRepresentation treeB){
		List<TreeNode> nodesA = getNodesOfInterest(treeA);
		List<TreeNode> nodesB = getNodesOfInterest(treeB);
		HashSet<TreeNode> nodesToMarkA = new HashSet<TreeNode>();
		HashSet<TreeNode> nodesToMarkB = new HashSet<TreeNode>();
		for(TreeNode nodeA : nodesA){
			for(TreeNode nodeB : nodesB){
				if(this.nodeSimilarity.getSimilarity(nodeA, nodeB)>=this.simThreshold){
					nodesToMarkA.add(nodeA);
					nodesToMarkB.add(nodeB);
					propagateMarkingUpward(nodesToMarkA, nodeA);
					propagateMarkingUpward(nodesToMarkB, nodeB);
					propagateMarkingDownward(nodesToMarkA, nodeA, downwordPropagation);
					propagateMarkingDownward(nodesToMarkB, nodeB, downwordPropagation);
				}
			}
		}
		for(TreeNode node : nodesToMarkA){
			markNode(node);
		}
		for(TreeNode node : nodesToMarkB){
			markNode(node);
		}
		treeA.updateOrderedNodeLists();
		treeB.updateOrderedNodeLists();
	}

	private List<TreeNode> getNodesOfInterest(TreeRepresentation tree){
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		List<TreeNode> allNodes = tree.getAllNodes();
		for(TreeNode node : allNodes){
			if(this.nodeFilter.isNodeOfInterest(node)){
				nodes.add(node);
			}
		}
		return nodes;
	}

	private void propagateMarkingUpward(HashSet<TreeNode> nodesToMark, TreeNode anchorNode){
		TreeNode currentNode = anchorNode;
		for(int i=0; i<this.upwardPropagation; i++){
			if(currentNode.getFather()==null){
				break;
			}
			currentNode = currentNode.getFather();
			nodesToMark.add(currentNode);
		}
	}

	private void propagateMarkingDownward(HashSet<TreeNode> nodesToMark, TreeNode anchorNode, int downwardPropagation){
		if(downwardPropagation<=0){
			return;
		}
		for(TreeNode child : anchorNode.getChildren()){
			nodesToMark.add(child);
			propagateMarkingDownward(nodesToMark, child, downwardPropagation-1);
		}
	}

	private void markNode(TreeNode node){

		switch (markingPolicy) {
		case AS_ADDITIONAL_INFO:
			node.getContent().addAdditionalInformation(markingPrefix, null);
			return;
		case ON_NODE_LABEL:
			String original = node.getContent().getTextFromData();
			String newContent;

			newContent = markingPrefix+original;

			try {
				node.getContent().setDataFromText(newContent);
				node.updateProduction();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		default:
			break;
		}

	}

}
