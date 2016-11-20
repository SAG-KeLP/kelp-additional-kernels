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
import it.uniroma2.sag.kelp.data.example.ExamplePair;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectRepresentationFromExample;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectRepresentationFromExample.representationSelectorInExample;
import it.uniroma2.sag.kelp.data.representation.tree.utils.TreeNodeSelector;

/**
 * The class adds to an additional field to a selected number of nodes. The value
 * of the field is determined by a List of Doubles passed to the constructor.
 * 
 * Note that <code>treeNodeSelector</code> determines the nodes of the tree 
 * which additional information is added to, which means that not necessarily 
 * information has to be added to all nodes of the tree.   
 * The static method <code>addInfoToNodes()</code> allows to perform the same
 * operation on a single tree. 
 * 
 * @author Giovanni Da San Martino
 *
 */
public class TreeAddAdditionalInfoFromArray implements Manipulator {

	/**
	 * A class for selecting the tree to be manipulated from the Example.  
	 */
	private SelectRepresentationFromExample representationSelector;
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
	private String infoFieldName;
	private int exampleIndex;
	private static final String DEFAULT_FIELD_NAME = "weight";
	/**
	 * Create a manipulator which adds an additional field to selected tree nodes
	 * whose values are determined by the parameter <code>info</code>. 
	 * 
	 * @param treeNodeSelector determines the nodes of the tree which additional 
	 * information is added to  
	 * @param info an array of Double containing the additional information to 
	 * be added. It must be ensured that the order of the elements in the array 
	 * is consistent with the order of the nodes as returned by <code>treeNodeSelector</code>
	 * @param fieldName the name of the field in the nodes of trees which will 
	 * store the additional info
	 * @param representationSelectorInExampleClass A class for retrieving a tree 
	 * from the Example
	 */
	public TreeAddAdditionalInfoFromArray(TreeNodeSelector treeNodeSelector, 
			List<List<Double>> info, String fieldName, 
			SelectRepresentationFromExample representationSelectorInExampleClass) {  
		this.treeNodeSelector = treeNodeSelector;
		datasetNodeInfo = info;
		exampleIndex = 0;
		infoFieldName = fieldName;
		representationSelector = representationSelectorInExampleClass;
	}

	
	/**
	 * Create a manipulator which adds an additional field to selected tree nodes
	 * whose values are determined by the parameter <code>info</code>. 
	 * 
	 * @param treeNodeSelector determines the nodes of the tree which additional 
	 * information is added to  
	 * @param info an array of Double containing the additional information to 
	 * be added. It must be ensured that the order of the elements in the array 
	 * is consistent with the order of the nodes as returned by <code>treeNodeSelector</code>
	 * @param fieldName the name of the field in the nodes of trees which will
	 * store the additional info
	 * @param representation name of the representation of the trees in the Example
	 * It assumes Example not to be an ExamplePair  
	 */
	public TreeAddAdditionalInfoFromArray(TreeNodeSelector treeNodeSelector, 
			List<List<Double>> info, String fieldName, 
			String representation) {
		this(treeNodeSelector, info, fieldName, 
				new SelectRepresentationFromExample(representation));
	}

	
	/**
	 * Create a manipulator which adds an additional field to selected tree nodes
	 * whose values are given by the List<List<Double>> <code>info</code>
	 * passed as parameter to the constructor. 
	 * 
	 * @param treeNodeSelector determines the nodes of the tree which additional 
	 * information is added to  
	 * @param info an array of Double containing the additional information to 
	 * be added. It must be ensured that the order of the elements in the array 
	 * is consistent with the order of the nodes as returned by <code>treeNodeSelector</code>  
	 * @param fieldName the name of the field in which the additional information
	 * will be stored. 
	 * @param representation name of the representation of the trees in the Example
	 * It assumes Example is an ExamplePair
	 * @param applyToLeftExampleInPair a boolean specifying whether the 
	 * additional info is to be applied to the left element of the 
	 * ExamplePair (true) or to the right one (false)
	 * 
	 */
	public TreeAddAdditionalInfoFromArray(TreeNodeSelector treeNodeSelector, 
			List<List<Double>> info, String fieldName, 
			String representation, boolean applyToLeftExampleInPair) {

		this(treeNodeSelector, info, fieldName, 
				new SelectRepresentationFromExample(representation, 
						applyToLeftExampleInPair?representationSelectorInExample.LEFT:
							representationSelectorInExample.RIGHT));
	}

	/**
	 * Create a manipulator which adds an additional field to selected tree nodes
	 * whose values are given by the List<List<Double>> <code>info</code>
	 * passed as parameter to the constructor. The data is added to a node field
	 * with the default name "weight" 
	 * 
	 * @param treeNodeSelector determines the nodes of the tree which additional 
	 * information is added to  
	 * @param info an array of Double containing the additional information to 
	 * be added. It must be ensured that the order of the elements in the array 
	 * is consistent with the order of the nodes as returned by <code>treeNodeSelector</code> 
	 * @param representation name of the representation of the trees in the Example
	 * It assumes Example is an ExamplePair
	 * @param applyToLeftExampleInPair a boolean specifying whether the 
	 * additional info is to be applied to the left element of the 
	 * ExamplePair (true) or to the right one (false)
	 * 
	 */
	public TreeAddAdditionalInfoFromArray(TreeNodeSelector treeNodeSelector, 
			List<List<Double>> info, String representation, boolean applyToLeftExampleInPair) {

		this(treeNodeSelector, info, DEFAULT_FIELD_NAME, 
				new SelectRepresentationFromExample(representation, 
						applyToLeftExampleInPair?representationSelectorInExample.LEFT:
							representationSelectorInExample.RIGHT));
	}

	public String describe() {
		String msg = "Created Manpulator which adds an additional field to tree "
				+ "nodes. The object is instantiated as follows:"  + System.getProperty("line.separator")
				+ "Tree Selector object: " + representationSelector.describe() 
				+ System.getProperty("line.separator");
		msg += "The nodes for which the new field will be added are:" 
				+ treeNodeSelector.describe() + " , i.e. the root of the tree" + System.getProperty("line.separator");
		msg += "new field name: " + infoFieldName + System.getProperty("line.separator");
		msg += "Example of values from the first example: " + datasetNodeInfo.get(0).toString() + System.getProperty("line.separator");
		return msg;
	}
	
	@Override
	public void manipulate(Example example) {
		
		if(exampleIndex==0) {
			/* checking that it has not been invoked for all examples of an Example Pair */
			if(example instanceof ExamplePair && representationSelector.isInvokedForAllPairElements()) {
				System.out.println("ERROR: the class TreeAddAdditionalInfoFromArray "
						+ "must be invoked only on one example of an Example Pair!");
			}
		}
		TreeRepresentation tree = (TreeRepresentation) representationSelector.extractRepresentation(example);
		if(tree != null) {
			if(! addInfoToNodes(treeNodeSelector.getNodeList(tree.getRoot()), 
					datasetNodeInfo.get(exampleIndex), infoFieldName)) {
				System.out.println("Error in example " + (exampleIndex+1));
				System.exit(1);
			}
			exampleIndex += 1;
		}
	}
	
	/**
	 * A function for adding additional info to tree nodes. 
	 * Note that the function is public static, so it can be invoked directly 
	 * to add info to a single tree.  
	 * 
	 * @param nodes a TreeNode list representing the nodes which the info will
	 * be added to  
	 * @param nodeInfo a Double list with the info that will be attached to nodes
	 * @param fieldName the name of the field in the tree structure
	 */
	public static boolean addInfoToNodes(List<TreeNode> nodes, 
			List<Double> nodeInfo, String fieldName) {
		//TreeNodeSelector getNodeList = node -> node.getLeaves(); 
		//TreeNodeSelector getNodeList = node -> node.getAllNodes();
		if(nodes.size() != nodeInfo.size()) {
			System.out.println(String.format("ERROR: trying to add info to "
					+ "nodes from an array which does not have the same size (%d)"
					+ " of the nodes of the tree (%d)%n", nodeInfo.size(), nodes.size()));
			for(TreeNode n: nodes) {
				System.out.format(" %s", n.getContent().getTextFromData());
			}
			System.out.format("%nexternal info to be added: %s%n", nodeInfo.toString());
			return false;
		}
		int i=0;
		for(TreeNode n: nodes) {
			n.getContent().addAdditionalInformation(fieldName, nodeInfo.get(i));
			i+=1;
		}
		return true;
	}

}
