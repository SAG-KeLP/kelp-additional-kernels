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

package it.uniroma2.sag.kelp.kernel.tree;

import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNodePairs;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Class providing some static methods useful for various tree kernels
 * 
 * @author Danilo Croce, Simone Filice
 */
public class TreeKernelUtils {

	/**
	 * Determine the nodes sharing the same production in the given trees.
	 * This is an optimization proposed in [Moschitti, EACL 2006] for a fast
	 * implementation of the SubTreeKernel and the SubSetTreeKernel.
	 * 
	 * @param a
	 *            First Tree
	 * @param b
	 *            Second Tree
	 * @return The node pairs having the same production
	 */
	public static ArrayList<TreeNodePairs> findCommonNodesByProduction(TreeRepresentation a,
			TreeRepresentation b, DeltaMatrix deltaMatrix, boolean includeLeaves) {

		ArrayList<TreeNodePairs> intersect = new ArrayList<TreeNodePairs>();

		int i = 0, j = 0, jOld, jFinal;
		int nA, nB;
		int cfr;


		List<TreeNode> nodesA;
		List<TreeNode> nodesB;
		if(includeLeaves){
			nodesA = a.getOrderedNodeSetByProduction();
			nodesB = b.getOrderedNodeSetByProduction();
		}else{
			nodesA = a.getOrderedNodeSetByProductionIgnoringLeaves();
			nodesB = b.getOrderedNodeSetByProductionIgnoringLeaves();
		}



		nA = nodesA.size();
		nB = nodesB.size();

		while (i < nA && j < nB) {

			if ((cfr = (nodesA.get(i).getProduction().compareTo(nodesB.get(j)
					.getProduction()))) > 0){
				j++;
			}
			else if (cfr < 0){
				i++;}
			else {
				jOld = j;
				do {
					do {
						intersect.add(new TreeNodePairs(nodesA.get(i), nodesB
								.get(j)));

						deltaMatrix.add(nodesA.get(i).getId(), nodesB.get(j)
								.getId(), DeltaMatrix.NO_RESPONSE);

						j++;
					} while (j < nB
							&& (nodesA.get(i).getProduction().equals(nodesB
									.get(j).getProduction())));
					i++;
					jFinal = j;
					j = jOld;
				} while (i < nA
						&& (nodesA.get(i).getProduction().equals(nodesB.get(j)
								.getProduction())));
				j = jFinal;
			}
		}

		return intersect;
	}

	//	/**
	//	 * Determine the nodes sharing the same production in the given trees, ignoring the leaves.
	//	 * This optimization has been proposed in [Moschitti, EACL 2006].
	//	 * 
	//	 * @param a
	//	 *            First Tree
	//	 * @param b
	//	 *            Second Tree
	//	 * @return The node pairs having the same production ignoring the leaves
	//	 */
	//	public static ArrayList<TreeNodePairs> findCommonNodesByProductionIgnoringLeaves(TreeRepresentation a,
	//			TreeRepresentation b, DeltaMatrix deltaMatrix) {
	//		
	//		ArrayList<TreeNodePairs> intersect = new ArrayList<TreeNodePairs>();
	//
	//		int i = 0, j = 0, jOld, jFinal;
	//		int nA, nB;
	//		int cfr;
	//
	//		List<TreeNode> nodesA = a.getOrderedNodeSetByProductionIgnoringLeaves();
	//		List<TreeNode> nodesB = b.getOrderedNodeSetByProductionIgnoringLeaves();
	//		
	//		nA = nodesA.size();
	//		nB = nodesB.size();
	//		
	//		while (i < nA && j < nB) {
	//			
	//			if ((cfr = (nodesA.get(i).getProductionIgnoringLeaves().compareTo(nodesB.get(j)
	//					.getProductionIgnoringLeaves()))) > 0){
	//				j++;
	//			}
	//			else if (cfr < 0){
	//				i++;}
	//			else {
	//				jOld = j;
	//				do {
	//					do {
	//						intersect.add(new TreeNodePairs(nodesA.get(i), nodesB
	//								.get(j)));
	//
	//						deltaMatrix.add(nodesA.get(i).getId(), nodesB.get(j)
	//								.getId(), DeltaMatrix.NO_RESPONSE);
	//
	//						j++;
	//					} while (j < nB
	//							&& (nodesA.get(i).getProductionIgnoringLeaves().equals(nodesB
	//									.get(j).getProductionIgnoringLeaves())));
	//					i++;
	//					jFinal = j;
	//					j = jOld;
	//				} while (i < nA
	//						&& (nodesA.get(i).getProductionIgnoringLeaves().equals(nodesB.get(j)
	//								.getProductionIgnoringLeaves())));
	//				j = jFinal;
	//			}
	//		}
	//
	//		return intersect;
	//	}

	/**
	 * Delta Function for tree kernels operation at production level, like 
	 * SubTreeKernel and SubSetTreeKernel. Given two nodes \(n_1\) and \(n_2\)
	 *  \(\Delta(n_1,n_2)\) can be computed as:</br> - if productions at
	 * \(n_1\) and \(n_2\) are different then \(\Delta(n_1,n_2)=0\)</br> - if the
	 * productions at n1 and n2 are the same, and \(n_1\) and \(n_2\) have only leaf
	 * children then \(\Delta(n_1,n_2)=\lambda\)</br> - if the productions at n1 and
	 * n2 are the same, and \(n_1\) and \(n_2\) are not pre-terminals then</br>
	 * \(\Delta(n_1,n_2)=\lambda \prod_{j=1}^{nc(n_1)} (\sigma + \Delta(c_{n_1}^j,
	 * c_{n_2}^j))\)
	 * 
	 * @param Nx
	 *            the root of the first tree
	 * @param Nz
	 *            the root of the second tree
	 *            
	 * @param sigma the sigma coefficient in the delta formula (sigma=0 for SubTreeKernel, sigma=1 for SubSetTreeKernel)
	 * @param lambda the lambda decay factor in the delta formula 
	 * @param DeltaMatrix the delta matrix where the partial delta evaluations are stored 
	 * @return the result of the delta function
	 */
	public static float productionBasedDeltaFunction(TreeNode Nx, TreeNode Nz, int sigma, float lambda, DeltaMatrix deltaMatrix) {
		if (deltaMatrix.get(Nx.getId(), Nz.getId()) != DeltaMatrix.NO_RESPONSE)
			return deltaMatrix.get(Nx.getId(), Nz.getId()); // cashed
		else {
			float prod = 1;
			ArrayList<TreeNode> NxChildren = Nx.getChildren();
			ArrayList<TreeNode> NzChildren = Nz.getChildren();
			for (int i = 0; i < NxChildren.size() && i < NzChildren.size(); i++) {

				if (NxChildren.get(i).hasChildren()
						&& NzChildren.get(i).hasChildren()
						&& NxChildren.get(i).getProduction()
						.equals(NzChildren.get(i).getProduction())) {

					prod *= sigma + productionBasedDeltaFunction(NxChildren.get(i),
							NzChildren.get(i), sigma, lambda, deltaMatrix);
				}
			}
			deltaMatrix.add(Nx.getId(), Nz.getId(), lambda * prod);
			return lambda * prod;
		}
	}

}
