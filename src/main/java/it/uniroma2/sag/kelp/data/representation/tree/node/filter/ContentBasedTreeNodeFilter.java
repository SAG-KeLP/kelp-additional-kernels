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

package it.uniroma2.sag.kelp.data.representation.tree.node.filter;

import it.uniroma2.sag.kelp.data.representation.structure.filter.StructureElementFilter;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

/**
 * This implementation of <code>TreeNodeFilter</code> selects only treeNode containing
 * a StructureElement interesting w.r.t. a <code>StructureElementFilter</code>
 * 
 * @author Simone Filice
 *
 */
public class ContentBasedTreeNodeFilter implements TreeNodeFilter{

	private StructureElementFilter elementFilter;
	
	/**
	 * Constructor for ContentBasedTreeNodeFilter
	 * 
	 * @param elementFilter a StructureElementFilter to be used for selecting tree nodes of
	 * interest by the method <code>isNodeOfInterest</code>
	 */
	public ContentBasedTreeNodeFilter(StructureElementFilter elementFilter) {
		this.elementFilter = elementFilter;
	}

	@Override
	public boolean isNodeOfInterest(TreeNode node) {
		return elementFilter.isElementOfInterest(node.getContent());
	}

}
