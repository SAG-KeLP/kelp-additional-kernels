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
public class PruneNodeNumberOfChildren extends NodePruner {
	protected int maxNumberOfChildren;
	
	public PruneNodeNumberOfChildren(int maxNumberOfChildren) {
		this.maxNumberOfChildren = maxNumberOfChildren;
	}
	
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
