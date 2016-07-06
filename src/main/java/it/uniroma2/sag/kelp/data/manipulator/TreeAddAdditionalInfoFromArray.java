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

import java.util.List;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectTreeRepresentationInterface;
import it.uniroma2.sag.kelp.data.representation.tree.utils.TreeNodeSelector;

/**
 * The class adds to an additional field to a selected number of nodes. The value
 * of the field is determined by a List of Doubles passed to the constructor.
 * 
 * Note that <code>treeNodeSelector</code> determines the nodes of the tree 
 * which additional information is added to, which means not necessarily information
 * has to be added to all nodes of the tree.   
 * 
 * @author Giovanni Da San Martino
 *
 */
public class TreeAddAdditionalInfoFromArray implements Manipulator {

	/**
	 * A class for selecting the tree to be manipulated from the Example.  
	 */
	private SelectTreeRepresentationInterface treeSelector;
	/**
	 * A class for determining the nodes of the tree which additional 
	 * information will be added to
	 */
	private TreeNodeSelector treeNodeSelector;
	/**
	 * The inner List<Double> is a list of values related to the nodes of one tree.
	 * The outer list has as many elements as the examples in the dataset.  
	 */
	private List<List<Double>> datasetNodeInfo;
	private String infoFieldName = "weight";
	private int exampleIndex;

	/**
	 * Create a manipulator which adds an additional field to selected tree nodes
	 * whose values are determined by the parameter <code>info</code>. 
	 * 
	 * @param treeSelector A class for retrieving a tree from the Example
	 * @param treeNodeSelector determines the nodes of the tree which additional 
	 * information is added to  
	 * @param info an array of Double containing the additional information to 
	 * be added. It must be ensured that the order of the elements in the array 
	 * is consistent with the order of the nodes as returned by <code>treeNodeSelector</code>  
	 */
	public TreeAddAdditionalInfoFromArray(SelectTreeRepresentationInterface treeSelector, 
			TreeNodeSelector treeNodeSelector, List<List<Double>> info) {  
		this.treeSelector = treeSelector;
		this.treeNodeSelector = treeNodeSelector;
		datasetNodeInfo = info;
		exampleIndex = 0;
	}

	/**
	 * Create a manipulator which adds an additional field to selected tree nodes
	 * whose values are given by the List<List<Double>> <code>info</code>
	 * passed as parameter to the constructor. 
	 * 
	 * @param treeSelector A class for retrieving a tree from the Example
	 * @param treeNodeSelector determines the nodes of the tree which additional 
	 * information is added to  
	 * @param info an array of Double containing the additional information to 
	 * be added. It must be ensured that the order of the elements in the array 
	 * is consistent with the order of the nodes as returned by <code>treeNodeSelector</code>  
	 * @param fieldName the name of the field in which the additional information
	 * is stored. 
	 */
	public TreeAddAdditionalInfoFromArray(SelectTreeRepresentationInterface treeSelector, 
			TreeNodeSelector treeNodeSelector, List<List<Double>> info, String fieldName) {
		this(treeSelector, treeNodeSelector, info);
		infoFieldName = fieldName;
	}
	
	public String describe() {
		String msg = "Created Manpulator which adds an additional field to tree "
				+ "nodes. The object is instantiated as follows:"  + System.lineSeparator()
				+ "Tree Selector object: " + treeSelector.describe() + System.lineSeparator();
		msg += "The nodes which the new field will be added are:" 
				+ treeNodeSelector.describe() + " , i.e. the root of the tree" + System.lineSeparator();
		msg += "new field name: " + infoFieldName + System.lineSeparator();
		msg += "Example of values from the first example: " + datasetNodeInfo.get(0).toString() + System.lineSeparator();
		return msg;
	}
	
	@Override
	public void manipulate(Example example) {
		TreeRepresentation tree = treeSelector.GetTreeRepresentation(example);
		manipulateTree(tree, datasetNodeInfo.get(exampleIndex));
		exampleIndex += 1;
	}

	private void manipulateTree(TreeRepresentation tree, List<Double> nodeInfo) {
		//TreeNodeSelector getNodeList = node -> node.getLeaves(); 
		//TreeNodeSelector getNodeList = node -> node.getAllNodes();		
		List<TreeNode> nodes;
		if(tree != null){
			nodes = treeNodeSelector.getNodeList(tree.getRoot());
			if(nodes.size() != nodeInfo.size()) {
				System.out.println(String.format("ERROR: trying to add info to "
						+ "nodes from an array which does not have the same size (%d)"
						+ " of the nodes of the tree (%d)%n", nodeInfo.size(), nodes.size()));
				for(TreeNode n: nodes) {
					System.out.format(" %s", n.getContent().getTextFromData());
				}
				System.out.format("%nexternal info to be added: %s", nodeInfo.toString());
				System.exit(1);
			}
	        int i=0;
	        for(TreeNode n: nodes) {
	            n.getContent().addAdditionalInformation(infoFieldName, nodeInfo.get(i));
	            i+=1;
	        }
		}
	}

}
