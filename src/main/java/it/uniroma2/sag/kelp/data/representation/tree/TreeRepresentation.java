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

package it.uniroma2.sag.kelp.data.representation.tree;

import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.utils.TreeIO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Tree Representation used for example to represent the syntactic tree of a
 * sentence. It can be exploited by convolution kernels
 * 
 * @author Danilo Croce, Giuseppe Castellucci, Simone Filice
 */
@JsonTypeName("T")
public class TreeRepresentation implements Representation {

	
	private Logger logger = LoggerFactory.getLogger(TreeRepresentation.class);
	private static final long serialVersionUID = 5856527731146702094L;

	/**
	 * The root node of the tree
	 */
	protected TreeNode root;

	/**
	 * The complete set of tree nodes ordered alphabetically by label. Used by
	 * several Tree Kernel functions.
	 */
	protected List<TreeNode> orderedNodeSetByLabel = null;

	/**
	 * The complete set of tree nodes ordered alphabetically by production
	 * string. Used by several Tree Kernel functions.
	 */
	protected List<TreeNode> orderedNodeSetByProduction = null;
	
	/**
	 * The of non terminal tree nodes ordered alphabetically by production
	 * string. Used by several Tree Kernel functions.
	 */
	protected List<TreeNode> orderedNodeSetByProductionIgnoringLeaves = null;

	/**
	 * Compare tree nodes by Label
	 */
	private static final Comparator<TreeNode> AlphabeticalLabelComparator = new Comparator<TreeNode>() {
		public int compare(TreeNode e1, TreeNode e2) {
			return e1.getContent().getTextFromData().compareTo(e2.getContent().getTextFromData());
		}
	};

	/**
	 * Compare tree nodes by Production
	 */
	private static final Comparator<TreeNode> AlphabeticalProductionComparator = new Comparator<TreeNode>() {
		public int compare(TreeNode e1, TreeNode e2) {
			return e1.getProduction().compareTo(e2.getProduction());
		}
	};
	
//	/**
//	 * Compare tree nodes by Production ignoring the leaves
//	 */
//	private static final Comparator<TreeNode> AlphabeticalProductionComparatorIgnoringLeaves = new Comparator<TreeNode>() {
//		public int compare(TreeNode e1, TreeNode e2) {
//			return e1.getProductionIgnoringLeaves().compareTo(e2.getProductionIgnoringLeaves());
//		}
//	};

	public TreeRepresentation() {

	}

	/**
	 * Build a tree representation from a TreeNode
	 * 
	 * @param root
	 *            Root of the Tree
	 */
	public TreeRepresentation(TreeNode root) {
		this.root = root;
	}

	@Override
	public TreeRepresentation clone() {
		return SerializationUtils.clone(this);
	}

	@Override
	public boolean equals(Object representation) {
		if (representation == null) {
			return false;
		}
		if (this == representation) {
			return true;
		}
		if (representation instanceof TreeRepresentation) {
			TreeRepresentation that = (TreeRepresentation) representation;
			return that.toString().equals(this.toString());
		}
		return false;
	}

	public void updateOrderedNodeLists(){
		this.orderedNodeSetByLabel = null;
		this.orderedNodeSetByProduction = null;
	}
	
	/**
	 * 
	 * @return the complete set of nodes
	 */
	@JsonIgnore
	public List<TreeNode> getAllNodes() {
		return root.getAllNodes();
	}

	/**
	 * Get the max id within all nodes
	 * 
	 * @return the max id value
	 */
	@JsonIgnore
	public Integer getMaxId() {
		return root.getMaxId();
	}

	/**
	 * @return the complete set of nodes ordered alphabetically by the node
	 *         label
	 */
	@JsonIgnore
	public List<TreeNode> getOrderedNodeSetByLabel() {
		if (this.orderedNodeSetByLabel == null) {
			orderedNodeSetByLabel = root.getAllNodes();

			Collections
			.sort(orderedNodeSetByLabel, AlphabeticalLabelComparator);
		}
		return orderedNodeSetByLabel;
	}

	/**
	 * @return the complete set of nodes ordered alphabetically by production
	 *         string
	 */
	@JsonIgnore
	public List<TreeNode> getOrderedNodeSetByProduction() {
		if (this.orderedNodeSetByProduction == null) {
			orderedNodeSetByProduction = root.getAllNodes();

			Collections.sort(orderedNodeSetByProduction,
					AlphabeticalProductionComparator);
		}
		return orderedNodeSetByProduction;
	}
	
	/**
	 * @return the complete set of nodes ordered alphabetically by production
	 *         string ignoring leaves
	 */
	@JsonIgnore
	public List<TreeNode> getOrderedNodeSetByProductionIgnoringLeaves() {
		if (this.orderedNodeSetByProductionIgnoringLeaves == null) {
			
			orderedNodeSetByProductionIgnoringLeaves = new ArrayList<TreeNode>();
			for(TreeNode node : root.getAllNodes()){
				if(node.hasChildren()){
					orderedNodeSetByProductionIgnoringLeaves.add(node);
				}
				
			}
			Collections.sort(orderedNodeSetByProductionIgnoringLeaves,
					AlphabeticalProductionComparator);
			//Collections.sort(orderedNodeSetByProductionIgnoringLeaves,
					//AlphabeticalProductionComparatorIgnoringLeaves);
		}
		return orderedNodeSetByProductionIgnoringLeaves;
	}

	/**
	 * @return the tree root
	 */
	@JsonIgnore
	public TreeNode getRoot() {
		return root;
	}

	@Override
	public String getTextFromData() {
		return this.toString();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public void setDataFromText(String representationDescription)
			throws Exception {
		this.root = new TreeIO()
		.parseCharniakSentence(representationDescription);
	}

	@Override
	public String toString() {
		return root.toString();
	}
	
	@JsonIgnore
	public String getTextualEnrichedTree(){
		return root.getTextualEnrichedFormat();
	}

	/**
	 * Returns all the nodes whose content has type <code>clazz</code>
	 * 
	 * @param clazz the type of the content 
	 * @return all the nodes whose content has type <code>clazz</code>
	 */
	@JsonIgnore
	public List<TreeNode> getNodesWithContentType(Class<? extends StructureElement> clazz){

		ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
		for(TreeNode node : this.getOrderedNodeSetByLabel()){
			if(node.getContent().getClass().equals(clazz)){
				nodes.add(node);
			}
		}
		return nodes;
	}

	
	/**
	 * Returns all the leaves, i.e. the nodes without children
	 * 
	 * @return the leaves of this tree
	 */
	@JsonIgnore
	public List<TreeNode> getLeaves(){

		return root.getLeaves();
	}
	
	/**
	 * Returns all the nodes that have at least a <code>generationHops</code>-generation descendant being a leaf 
	 * (for instance using <code>generationHops</code>=1 will produce a list of all the fathers of the leaves,
	 * <code>generationHops</code>=2 will produce a list of all the grandfathers of the leaves, etc)
	 * 
	 * @param generationHops the number of generations from the leaves
	 * @return all the nodes that have at least a <code>generationHops</code>-generation descendant being a leaf 
	 */
	@JsonIgnore
	public List<TreeNode> getPreLeafNodes(int generationHops){
		ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
		List<TreeNode> leaves = this.getLeaves();
		HashSet<TreeNode> uniqueNodes = new HashSet<TreeNode>();
		for(TreeNode leaf : leaves){
			TreeNode ancestor = leaf.getAncestor(generationHops);
			if(ancestor==null){
				continue;
			}
			if(uniqueNodes.add(ancestor)){
				nodes.add(ancestor);
			}
		}
 		return nodes;
	}
	
	@Override
	public boolean isCompatible(Representation rep) {
		if (!( rep instanceof TreeRepresentation)){
			logger.error("incompatible representations: " + this.getClass().getSimpleName() + " vs " + rep.getClass().getSimpleName());
			return false;
		}
		return true;
	}

}
