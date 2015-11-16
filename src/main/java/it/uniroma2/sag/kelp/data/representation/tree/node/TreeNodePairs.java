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

import java.io.Serializable;

/**
 * This class represents a node pairs, used in the various tree kernel
 * formulation to compare two subtrees rooted in the tree node pairs.
 * 
 * @author Danilo Croce, Giuseppe Castellucci
 * 
 */
public class TreeNodePairs implements Serializable {

	private static final long serialVersionUID = 7256958840350258797L;

	/**
	 * The first node in the pair
	 */
	private TreeNode Nx;
	/**
	 * The second node in the pair
	 */
	private TreeNode Nz;

	public TreeNodePairs(TreeNode nx, TreeNode nz) {
		super();
		Nx = nx;
		Nz = nz;
	}

	public TreeNode getNx() {
		return Nx;
	}

	public TreeNode getNz() {
		return Nz;
	}

	@Override
	public String toString() {
		return Nx.getProduction() + "-" + Nx.getId() + "\t"
				+ Nz.getProduction() + "-" + Nz.getId();
	}
}
