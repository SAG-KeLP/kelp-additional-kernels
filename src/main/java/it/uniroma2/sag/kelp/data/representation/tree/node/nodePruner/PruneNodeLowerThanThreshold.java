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
 * Prune a node if the absolute value of the field <code>weightfield</code>
 * (default value "weight") is lower than <code>threshold</code>.  
 * Nodes which do not have a weight are given a default value <code>defaultWeightValue</code>. 
 * Depending on the value of boolean variable <code>avoidOrphans</code>, a node
 * is never removed if it has descendants which are not removed (avoidOrphans==true).   
 * 
 * @author Giovanni Da San Martino
 * 
 * Contributor Alessandro Moschitti
 */
public class PruneNodeLowerThanThreshold extends NodeToBePrunedCheckerAbstractClass {

	protected double threshold;
	protected String weightField;
	protected boolean avoidOrphans;
	protected double defaultWeightValue;
	protected nodeComparisonOperator nodeComparisonType;
	public enum nodeComparisonOperator {
		LOWER_THAN_ABSOLUTE_VALUE, LOWER_THAN
	}

	public PruneNodeLowerThanThreshold(double pruningThreshold, String weightFieldName, 
			boolean avoidOrphans, double defaultWeightValue) {
		threshold = pruningThreshold;
		weightField = weightFieldName;
		this.avoidOrphans = avoidOrphans;
		this.defaultWeightValue = defaultWeightValue;
		setComparisonTypeLowerThanAbsoluteValue();
	}
	
	public PruneNodeLowerThanThreshold(double threshold) {
		this(threshold, "weight", true, Double.MAX_VALUE);
	}
	
	public String describe() {
		String msg = "a node is pruned if the "; 
		if (nodeComparisonType == nodeComparisonOperator.LOWER_THAN_ABSOLUTE_VALUE) {
			msg += String.format("absolute value of the field %s is lower than %f",
					weightField, threshold);
		} else {
			msg += String.format(" value of the field %s is lower than %f",
					weightField, threshold);
		}
		return msg;
	}

	@Override
	public boolean isNodeToBePruned(TreeNode node) {
		return checkThresholdCondition(node);
	}

	public boolean checkThresholdCondition(TreeNode node) {
		Object additionalInfo = node.getContent().getAdditionalInformation(weightField);
		Double weight;
		if(additionalInfo == null) {
			weight = defaultWeightValue;
		} else {
			weight = (Double) additionalInfo;
		}
		//weight = (Double)node.getContent().getAdditionalInformation(weightField);
		boolean comparisonResult;
		if(nodeComparisonType == nodeComparisonOperator.LOWER_THAN_ABSOLUTE_VALUE) {
			comparisonResult = (Math.abs(weight) < threshold);
		}else{
			comparisonResult = (weight < threshold);
		}
		if (avoidOrphans) {
			return (!node.hasChildren() && comparisonResult);
		} else {
			return comparisonResult;
		}
	}
	
	private void setComparisonType(nodeComparisonOperator comparisonOperator) {
		nodeComparisonType = comparisonOperator;
	}

	public void setComparisonTypeLowerThanAbsoluteValue() {
		setComparisonType(nodeComparisonOperator.LOWER_THAN_ABSOLUTE_VALUE);
	}

	public void setComparisonTypeLowerThanValue() {
		setComparisonType(nodeComparisonOperator.LOWER_THAN);
	}

	public void setDefaultWeightValue(double weightValue) {
		defaultWeightValue = weightValue;
	}
	
}
