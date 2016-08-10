/*
 * Copyright 2014-2016 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
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

import java.util.HashSet;
import java.util.Set;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner.NodePruner;
import it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner.NodeToBePrunedCheckerAbstractClass;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectRepresentationFromExample;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectRepresentationFromExample.representationSelectorInExample;
import it.uniroma2.sag.kelp.data.representation.tree.utils.TreeNodeSelector;

/**
 * A manipulator for performing pruning on a tree. 
 * Roughly speaking the class performs a visit of the tree and, for each node,
 * checks whether it should be pruned.  
 * The behavior of the class depends on the classes passed as parameters to the 
 * constructor. However, a general schema is as follows:
 *   
 * 1) one or few nodes of the tree are selected, the visit of the tree will
 * start from such nodes (depends on <code>nodeSelectorObj</code>) 
 * 2) the visit can be pre-order or post-order and a limit to the number of
 * recursive calls can be set (depends on <code>visit</code> and 
 * <code>maxDepthVisitsWhilePruning</code>
 * 3) a class for checking whether a node has to be pruned (<code>nodePrunerObj</code>); 
 * optionally a second class for pruning non leaf nodes only can be provided
 * (<code>internalNodePrunerObj</code>)  
 * 
 * When dealing with ExamplePair objects, it is possible to apply a pruning 
 * strategy to the trees of one pair Example of the pair only (in this case class 
 * <code>treeSelectorObj</code> need to be set up properly)
 *  
 * @author Giovanni Da San Martino
 *
 * Contributor Alessandro Moschitti
 */
public class TreeNodePruner implements Manipulator {

	/**
	 * A class for selecting a tree from an Example
	 */
	private SelectRepresentationFromExample treeSelector;
	/**
	 * A class for determining whether a node should be pruned. The policy 
	 * returns a boolean, the node is pruned by the function performing the 
	 * visit of the tree.  
	 */
	private NodeToBePrunedCheckerAbstractClass nodePrunerChecker;
	/**
	 * An optional class for defining a different pruning policy for internal 
	 * nodes. Set it to null if not necessary
	 */
	private NodeToBePrunedCheckerAbstractClass internalNodePrunerChecker;
	
	/**
	 * Determines for which nodes the pruning procedure is invoked 
	 */
	private TreeNodeSelector nodeSelector;
	
	/**
	 * Pruning strategy for pre-order visit. Compared to 
	 * <code>NodeToBePrunedCheckerAbstractClass</code> this strategy directly
	 * prunes descendant nodes
	 */
	private NodePruner nodePrunerForPreOrderVisit;
	
	/**
	 * Determines the maximum number of recursive calls for the pruning 
	 * procedure. A negative value means unlimited recursive calls, 0 means the
	 * procedure is not invoked for the children of the node.    
	 */
	private int maxDepthVisits = UNLIMITED_RECURSION;
	public static final int UNLIMITED_RECURSION = Integer.MAX_VALUE;
	public static final int NO_DISTANCE_CONSTRAINT = -1;
	
	public enum visitType {
		POST_ORDER, PRE_ORDER
	}
	private visitType visit;
		
	/**
	 * Create a tree pruner which performs a post-order traversal of the tree.  
	 * 
	 * @param nodePrunerObj an object for establishing whether a node has to be pruned
	 * @param treeSelectorObj an object for selecting trees from an Example
	 * @param internalNodePrunerObj an object for establishing whether an internal 
	 * node of the tree has to be pruned
	 * @param nodeSelectorObj an object for selecting a list of nodes from which 
	 * the visit of the tree will start
	 * @param maxDepthVisitsWhilePruning maximum number of recursive calls when
	 * visiting a tree
	 */
	public TreeNodePruner(NodeToBePrunedCheckerAbstractClass nodePrunerObj,
			SelectRepresentationFromExample treeSelectorInExample, 
			NodeToBePrunedCheckerAbstractClass internalNodePrunerObj,
			TreeNodeSelector nodeSelectorObj, int maxDepthVisitsWhilePruning) {
		nodePrunerChecker = nodePrunerObj;
		internalNodePrunerChecker = internalNodePrunerObj;
		nodeSelector = nodeSelectorObj;
		maxDepthVisits = maxDepthVisitsWhilePruning;
		visit = visitType.POST_ORDER;
		treeSelector = treeSelectorInExample;
	}

	/**
	 * Create a tree pruner which performs a pre-order traversal of the tree.
	 * 
	 * @param nodePrunerForPreOrderVisit an object performing the pruning of a node
	 * @param nodeSelectorObj an object for selecting a list of nodes from which 
	 * the visit of the tree will start
	 * @param maxDepthVisitsWhilePruning maximum number of recursive calls when
	 * visiting a tree
	 */
	public TreeNodePruner(NodePruner nodePrunerForPreOrderVisit, 
			SelectRepresentationFromExample treeSelectorInExample,
			TreeNodeSelector nodeSelectorObj, int maxDepthVisitsWhilePruning) {
		
		this.nodePrunerForPreOrderVisit = nodePrunerForPreOrderVisit;
		nodeSelector = nodeSelectorObj;
		maxDepthVisits = maxDepthVisitsWhilePruning;
		visit = visitType.PRE_ORDER;
		treeSelector = treeSelectorInExample;
	}

	public TreeNodePruner(NodeToBePrunedCheckerAbstractClass nodePrunerObj,
			String representationName, NodeToBePrunedCheckerAbstractClass internalNodePrunerObj,
			TreeNodeSelector nodeSelectorObj, int maxDepthVisitsWhilePruning) {
		this(nodePrunerObj, new SelectRepresentationFromExample(representationName), 
				internalNodePrunerObj, nodeSelectorObj, maxDepthVisitsWhilePruning);
	}

	
	public TreeNodePruner(NodeToBePrunedCheckerAbstractClass nodePrunerObj, 
			String representationName) {
		this(nodePrunerObj, new SelectRepresentationFromExample(representationName), 
				null, null, UNLIMITED_RECURSION);
	}

	public TreeNodePruner(NodeToBePrunedCheckerAbstractClass nodePrunerObj, 
			String representationName, boolean applyToLeftExampleInPair) {
		
		this(nodePrunerObj, new SelectRepresentationFromExample(representationName, 
				applyToLeftExampleInPair?representationSelectorInExample.LEFT:representationSelectorInExample.RIGHT), 
				null, null, UNLIMITED_RECURSION);
	}

	/**
	 * Create a tree pruner which performs a pre-order traversal of the tree.
	 * 
	 * @param nodePrunerForPreOrderVisit an object performing the pruning of a node
	 * @param representationName name of the representation of the trees in the Example
	 * @param nodeSelectorObj an object for selecting a list of nodes from which 
	 * the visit of the tree will start
	 * @param maxDepthVisitsWhilePruning maximum number of recursive calls when
	 * visiting a tree
	 */
	public TreeNodePruner(NodePruner nodePrunerForPreOrderVisit, 
			String representationName, TreeNodeSelector nodeSelectorObj, 
			int maxDepthVisitsWhilePruning) {
		
		this.nodePrunerForPreOrderVisit = nodePrunerForPreOrderVisit;
		nodeSelector = nodeSelectorObj;
		maxDepthVisits = maxDepthVisitsWhilePruning;
		visit = visitType.PRE_ORDER;
		treeSelector = new SelectRepresentationFromExample(representationName);
	}
	
	public void applyPruningToLeftElementOfExamplePair() {
		treeSelector.setSelectionToLeftExampleInPairOnly();
	}
	
	public void applyPruningToRightElementOfExamplePair() {
		treeSelector.setSelectionToRightExampleInPairOnly();
	}

	/**
	 * Describe what the elements of the tree-pruner will do once the manipulator
	 * is invoked.  
	 * @return a String with the description
	 */
	public String describe() {
		String msg = "Tree Node Pruner object instantiated as follows:" + System.getProperty("line.separator")
				+ "Tree Selector object: " + treeSelector.describe() + System.getProperty("line.separator");
		if (visit == visitType.POST_ORDER) {
			msg += "A post order visit will be performed" + System.getProperty("line.separator");
			if(internalNodePrunerChecker!=null) {
				msg += "Pruning Strategy for non-leaf nodes: " 
						+ internalNodePrunerChecker.describe() + System.getProperty("line.separator");
				msg += "Pruning Strategy for leaf nodes: " 
						+ nodePrunerChecker.describe() + System.getProperty("line.separator");
			} else {
				msg += "General Pruning Strategy for nodes: " 
						+ nodePrunerChecker.describe() + System.getProperty("line.separator");
			}
		}
		if (visit == visitType.PRE_ORDER) {
			msg += "A pre order visit will be performed" + System.getProperty("line.separator");
			msg += "Pruning strategy: " + nodePrunerForPreOrderVisit.describe() 
					+ System.getProperty("line.separator");
		}
		if(nodeSelector != null) {
			msg += "Node Selected as starting points of the tree visit: " + 
					nodeSelector.describe() + System.getProperty("line.separator");
		}else {
			msg += "Node Selected as starting point of the tree visit: "
					+ "the root of the tree" + System.getProperty("line.separator");
		}
		msg += "The Maximum depth of the recursive visits is " + 
				(maxDepthVisits==UNLIMITED_RECURSION?"unlimited": maxDepthVisits);
		return msg + System.getProperty("line.separator");
	}
	
	@Override
	public void manipulate(Example example) {
		TreeRepresentation tree = (TreeRepresentation) treeSelector.extractRepresentation(example);
		if(tree != null) {
			pruneTree(tree);
		}
	}

	public void pruneTree(TreeRepresentation tree) {
		if(nodeSelector==null) {
			/* by default pruning is applied starting from the root node of the tree */
			manipulateNodes(tree.getRoot(), maxDepthVisits);
		}else{
			for(TreeNode node: nodeSelector.getNodeList(tree.getRoot())) {
				manipulateNodes(node, maxDepthVisits);
			}
		}
		tree.updateTree();		
	}
	
	public void setMaximumDepthOfVisits(int maxDepth) {
		maxDepthVisits = maxDepth;
	}
	
	private void setVisitType(visitType treeVisit) {
		visit = treeVisit;
	}

	public void setVisitTypePostOrder() {
		setVisitType(visitType.POST_ORDER);
	}

	public void setVisitTypePreOrder() {
		setVisitType(visitType.PRE_ORDER);
	}

	private void manipulateNodes(TreeNode node, int numberOfRecursiveCalls) {
		if(visit == visitType.POST_ORDER) { 
			nodePrunerChecker.initPruner();
			pruneNodesWithPostOrderVisit(node, numberOfRecursiveCalls);
		}else if(visit == visitType.PRE_ORDER) {
			nodePrunerForPreOrderVisit.initPruner();
			pruneNodesWithPreOrderVisit(node, numberOfRecursiveCalls);
		}
	}
	
	/**
	 * Performs a post-order visit of the tree and apply nodePrunerChecker to each node.
	 * The visit ensures the descendants of a node have been tested before 
	 * the current node. 
	 * If <code>internalNodePrunerChecker</code> is set, the function uses two different
	 * strategies for pruning leaf and non-leaf nodes. 
	 *  When the function is invoked on a <code>node</code>, it checks whether 
	 * the children of <code>node</code> should be pruned (by invoking 
	 * <code>isNodeToBePruned()</code> which returns a boolean), possibly prune 
	 * them, then checks whether <code>node</code> should be pruned and returns 
	 * a boolean (<code>node</code> will be pruned by its father). 
	 * 
	 * @param node the node of the tree which is currently being checked for pruning
	 * @param numberOfRecursiveCalls 
	 * @return true if <code>node</code> should be pruned 
	 */
	private boolean pruneNodesWithPostOrderVisit(TreeNode node, int numberOfRecursiveCalls) {
		if(node == null){
			return false;
		}
		if (node.hasChildren()) {
			if(numberOfRecursiveCalls>0) {
				boolean nodeTobeRemoved;
				Set<TreeNode> nodesToRemove = new HashSet<TreeNode>();
				for (int i=0;i<node.getNoOfChildren();i++) { 
					nodeTobeRemoved = pruneNodesWithPostOrderVisit(
							node.getChildren().get(i), numberOfRecursiveCalls-1);
					if (nodeTobeRemoved)
						nodesToRemove.add(node.getChildren().get(i));
				}
				for (TreeNode ntr : nodesToRemove)
					node.getChildren().remove(ntr);
			}
			if(internalNodePrunerChecker != null) {
				return internalNodePrunerChecker.isNodeToBePruned(node);
			}
		} 
		return nodePrunerChecker.isNodeToBePruned(node);
	}

	/**
	 * The pruning condition is applied to the node and then to the children, 
	 * if any. Since a node cannot prune itself, but it can only prune its children,
	 * and since the visit is pre-order, the node-pruning class need to directly 
	 * prune children nodes, i.e. it must be an object implementing the 
	 * interface <code>NodePruner</code>.  
	 * Note that the node on which <code>pruneNodesWithPreOrderVisit</code> is
	 * first invoked cannot be pruned. 
	 *  
	 * @param node
	 * @param numberOfRecursiveCalls
	 * @return
	 */
	private void pruneNodesWithPreOrderVisit(TreeNode node, int numberOfRecursiveCalls) {
		if(node == null){
			return;
		}
		nodePrunerForPreOrderVisit.pruneNodes(node);
		if (node.hasChildren()) {
			if(numberOfRecursiveCalls>0) {
				for (int i=0;i<node.getNoOfChildren();i++) { 
					pruneNodesWithPreOrderVisit(node.getChildren().get(i), 
							numberOfRecursiveCalls-1);
				}
			}
		} 		
	}
}
