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

package it.uniroma2.sag.kelp.data.representation.tree.node;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElementFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A TreeNode represents a generic node in a TreeRepresentation
 * 
 * @author Danilo Croce, Giuseppe Castellucci, Simone Filice
 * 
 */
public class TreeNode implements Serializable {

	private static final long serialVersionUID = 3112378816044567678L;



	private StructureElement content;

	/**
	 * The node identifier, used in the tree kernel implementations to access
	 * caches. It MUST be unique for each node in the tree.
	 */
	private Integer id;

	/**
	 * The father of the node within the hosting tree representation
	 */
	private TreeNode father;

	/**
	 * The children nodes
	 */
	private ArrayList<TreeNode> children;

	/**
	 * A string encoding the node production, e.g. in (S(NP)(VP)) the production
	 * string is S->NP,VP
	 */
	private String production;
	
	/**
	 * A string encoding the node production, e.g. in (S(NP)(VP)) the production
	 * string is S->NP,VP. Leaf children are ignored
	 */
	private String productionIgnoringLeaves;

	public TreeNode(int id, StructureElement content, TreeNode father) {
		this.id = id;
		this.content = content;
		this.children = new ArrayList<TreeNode>();
		this.father = father;
	}

	public StructureElement getContent(){
		return this.content;
	}
	
	public void setContent(StructureElement content){
		this.content=content;
		this.updateProduction();
	}

	/**
	 * Get recursively all Tree Nodes below the target node
	 * 
	 * @return the list of all nodes
	 */
	public List<TreeNode> getAllNodes() {
		ArrayList<TreeNode> res = new ArrayList<TreeNode>();
		res.add(this);
		for (TreeNode child : children) {
			res.addAll(child.getAllNodes());
		}
		return res;
	}

	/**
	 * Get the direct children of the target node
	 * 
	 * @return the children of the target node
	 */
	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	/**
	 * Get the father of the target node
	 * 
	 * @return the node father. Null if the target node does not have any
	 *         father.
	 */
	public TreeNode getFather() {
		return father;
	}

	public Integer getId() {
		return id;
	}

	/**
	 * Get the max id within all node under the target node
	 * 
	 * @return the max id value
	 */
	public Integer getMaxId() {
		int resId = this.id;
		for (TreeNode child : getChildren()) {
			int childId = child.getMaxId();
			if (childId > resId) {
				resId = childId;
			}
		}
		return resId;
	}

	public int getNoOfChildren() {
		if (children == null)
			return 0;
		return children.size();
	}

	/**
	 * Get the node production in the form of string. It is a string encoding
	 * the node production, e.g. in (S(NP)(VP)) the production string is S->NP,VP
	 * 
	 * @return the node production as a string
	 */
	public String getProduction() {
		if (production != null)
			return production;

		//production = this.content.getTextFromData() + "->";
		production = StructureElementFactory.getTextualRepresentation(this.content) + "->";

		for (TreeNode child : children) {
			//production += child.getContent().getTextFromData() + " ";
			production += StructureElementFactory.getTextualRepresentation(child.getContent()) + " ";
		}

		return production;
	}
	
	/**
	 * Get the node production in the form of string. It is a string encoding
	 * the node production, e.g. in (S(NP)(VP)) the production string is S->NP,VP. 
	 *  Leaf children are ignored
	 * 
	 * @return the node production as a string. Leaf children are ignored
	 */
	public String getProductionIgnoringLeaves() {
		if (productionIgnoringLeaves != null)
			return productionIgnoringLeaves;

		//production = this.content.getTextFromData() + "->";
		productionIgnoringLeaves = StructureElementFactory.getTextualRepresentation(this.content) + "->";

		for (TreeNode child : children) {
			if(!child.hasChildren()){
				continue;
			}
			//production += child.getContent().getTextFromData() + " ";
			productionIgnoringLeaves += StructureElementFactory.getTextualRepresentation(child.getContent()) + " ";
		}

		return productionIgnoringLeaves;
	}
	
	/**
	 * Notifies a modification in the label of the node, that
	 * must reflect in an update of the production of this node and
	 * in the one of its father (if it has)
	 */
	public void updateProduction(){
		this.production = null;
		this.productionIgnoringLeaves = null;
		if(this.father!=null){
			this.father.production=null;
			this.father.productionIgnoringLeaves = null;
		}
	}

	public boolean hasChildren() {
		if (children != null && children.size() > 0)
			return true;
		return false;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("(");
		b.append(StructureElementFactory.getTextualRepresentation(content));
		if (children != null && children.size() > 0) {
			for (TreeNode node : children) {
				b.append(node.toString());
			}
		}
		b.append(")");
		return b.toString().trim();
	}

	public String getTextualEnrichedFormat() {
		StringBuilder b = new StringBuilder();
		b.append("(");
		b.append(content.getTextFromDataWithAdditionalInfo());
		if (children != null && children.size() > 0) {
			for (TreeNode node : children) {
				b.append(node.getTextualEnrichedFormat());
			}
		}
		b.append(")");
		return b.toString().trim();
	}
	
	/**
	 * Returns the <code>generation</code> generation ancestor of this node 
	 * (for instance 1-generation ancestor is the father, 2-generation ancestor
	 * is the grandfather, etc)
	 * 
	 * @param generation the number of generations
	 * @return the <code>generation</code> generation ancestor of this node 
	 */
	public TreeNode getAncestor(int generation){
		TreeNode ancestor = this;
		for(int i=0; i<generation; i++){
			if(ancestor.getFather()==null){
				return null;
			}
			ancestor=ancestor.getFather();
		}
		return ancestor;
	}

	/**
	 * Returns all the <code>generation</code> generations descendants of this node
	 * (for instance 1-generation descendants are the children, the 2-generations 
	 * descendants are the grandchildren, etc)
	 * 
	 * @param generation the number of generations
	 * @return all the <code>generation</code> generations descendants of this node
	 */
	public List<TreeNode> getDescendants(int generation){

		if(generation<=1){
			return this.children;
		}
		ArrayList<TreeNode> descendands = new ArrayList<TreeNode>();
		if(this.hasChildren()){
			for(TreeNode child : this.getChildren()){
				descendands.addAll(child.getDescendants(generation-1));
			}
		}

		return descendands;
	}
	
	/**
	 * Returns the height of the tree rooted by this node (i.e. 
	 * the number of edges on the longest downward path between that node and a leaf)
	 * 
	 * @return the height of this node
	 */
	public int getHeight(){
		
		int max = 0;
		for(TreeNode child : children){
			if(child.getHeight()+1 > max){
				max = child.getHeight()+1;
			}
		}
		return max;
	}
	
	/**
	 * Returns whether this node has at least a child that is a leaf (i.e. the child is terminal)
	 * 
	 * @return whether this node is preterminal
	 */
	public boolean isPreterminal(){
		for(TreeNode child : this.children){
			if(!child.hasChildren()){
				return true;
			}
		}
		return false;
	}
	
	public List<TreeNode> getLeaves(){
		List<TreeNode> leaves = new ArrayList<TreeNode>();
		for(TreeNode node : this.getAllNodes()){
			if(!node.hasChildren()){
				leaves.add(node);
			}
		}
		return leaves;
	}
	
	public TreeNode getRoot(){
		if(this.father==null){
			return this;
		}
		return father.getRoot();
	}
}
