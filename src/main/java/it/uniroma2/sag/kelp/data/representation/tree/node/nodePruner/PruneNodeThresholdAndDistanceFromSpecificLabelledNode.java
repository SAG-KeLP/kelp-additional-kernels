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
 * Prune a node if the following conditions are met:
 * 
 * 1) there is no node at distance not greater than <code>distance</code> whose
 * label starts with the prefix <code>labelPrefix</code>
 * 
 * 2) the conditions specified in the parent class
 * 
 * @author Giovanni Da San Martino
 * 
 * Contributor Alessandro Moschitti
 */
public class PruneNodeThresholdAndDistanceFromSpecificLabelledNode 
										extends PruneNodeLowerThanThreshold {

	protected String labelPrefix;
	protected int distance;
	
	public PruneNodeThresholdAndDistanceFromSpecificLabelledNode(double threshold, 
			 String weightField, boolean avoidOrphans, double defaultWeightValue, 
			 String labelPrefix, int distance) {
		super(threshold, weightField, avoidOrphans, defaultWeightValue);
		init(labelPrefix, distance);
	}

	public PruneNodeThresholdAndDistanceFromSpecificLabelledNode(double threshold,
			String labelPrefix) {
		super(threshold);
		init(labelPrefix, distance);
	}
	
	protected void init(String labelPrefix, int distance) {
		this.labelPrefix = labelPrefix;
		this.distance = distance;
	}

	/**
	 * Check if there is a node at a distance no greater than <code>distance</code>
	 * (the distance between a node and its neighbors is 1) whose label prefix 
	 * starts with <code>labelPrefix</code>. 
	 * It returns true if such node does not exists
	 * 
	 * Example of invocation: noCloseNodeWithSpecificLabelPrefix(node, "REL", 1, node)
	 * 
	 * @param node TreeNode 
	 * @param labelPrefix prefix of node label
	 * @param distance number of steps of the visit
	 * @param previousNode used to avoid to call the recursive function on the same node
	 * @return true if there is no node in the neighborhood which satisfies the conditions above
	 */
	public static boolean noCloseNodeWithSpecificLabelPrefix(TreeNode node,	String labelPrefix, 
			int distance, TreeNode previousNode) {
		
		boolean res = true;
		if(!(node==null)) {
			if(distance<=0) {
				return !node.getContent().getTextFromData().startsWith(labelPrefix);
			} else {
				if(!(node.getFather()==previousNode)) {
					res = res && noCloseNodeWithSpecificLabelPrefix(node.getAncestor(1), labelPrefix, distance-1, node); 
				}
				for(int i=0;i<node.getNoOfChildren();i+=1) {
					if(!(node.getChildren().get(i) == previousNode)) {
						res = res && noCloseNodeWithSpecificLabelPrefix(node.getChildren().get(i), labelPrefix, distance-1, node);
					}
				}
			}
		}
		return res;
	}

	
	@Override
	public boolean isNodeToBePruned(TreeNode node) {

		boolean res = checkThresholdCondition(node);
		return (res && noCloseNodeWithSpecificLabelPrefix(node, labelPrefix, distance, node));
		
	}
	
	public String describe() {
		String msg = super.describe();
		msg += String.format(" However, if there is a node whose label starts "
				+ "with %s and whose distance is not greater than %d, such node"
				+ " is not pruned", labelPrefix, (distance<0)?0:distance);
		return msg;
	}

}
