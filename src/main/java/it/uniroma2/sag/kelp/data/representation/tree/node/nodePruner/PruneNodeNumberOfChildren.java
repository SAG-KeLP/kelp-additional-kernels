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
package it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner;

import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

/**
 * This pruning strategy makes sure that a node does not have more than 
 * <code>maxNumberOfChildren</code> children.  
 *    
 * 
 * @author Giovanni Da San Martino
 *
 */
//public class PruneNodeNumberOfChildren extends NodeToBePrunedCheckerAbstractClass {
public class PruneNodeNumberOfChildren extends NodePruner {
	protected int maxNumberOfChildren;
	
	public PruneNodeNumberOfChildren(int maxNumberOfChildren) {
		this.maxNumberOfChildren = maxNumberOfChildren;
	}

//	/**
//	 * The pruning strategy keeps the first <code>maxNumberOfChildren</code> child
//	 * of a node and discard the following ones. 
//	 * This pruning strategy is assumed to be invoked in a pre-order tree visit.  
//	 * 
//	 * The function directly removes some of the children of a <code>node</code>.
//	 * Note that, since the function has been invoked on <code>node</code>, the 
//	 * index of <code>node</code> in the children list of its parent node is lower 
//	 * than <code>maxNumberOfChildren</code>. Therefore <node> should never be 
//	 * pruned and the function always returns false. 
//	 */
//	@Override
//	public boolean isNodeToBePruned(TreeNode node) {
//		for(int i=node.getNoOfChildren()-1;i>=maxNumberOfChildren;i-=1) {
//			node.getChildren().remove(i);
//		}
//		return false;
//	}
	
	public String describe() {
		return String.format("a node is pruned if it is the n-th child, where "
				+ " n>%d", maxNumberOfChildren);
	}

	@Override
	public void pruneNodes(TreeNode node) {
		for(int i=node.getNoOfChildren()-1;i>=maxNumberOfChildren;i-=1) {
			node.getChildren().remove(i);
		}
	}

}
