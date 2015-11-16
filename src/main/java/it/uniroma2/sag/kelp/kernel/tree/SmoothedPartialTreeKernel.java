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

import it.uniroma2.sag.kelp.data.representation.structure.similarity.StructureElementSimilarityI;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNodePairs;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
//import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;
//import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.StaticDeltaMatrix;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.StaticDeltaMatrix;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Partial Tree Kernel implementation.
 * 
 * A Partial Tree Kernel is a convolution kernel. The kernel function is defined
 * as: </br>
 * 
 * \(K(T_1,T_2) = \sum_{n_1 \in N_{T_1}} \sum_{n_2 \in N_{T_2}}
 * \Delta(n_1,n_2)\)
 * 
 * </br> where \(\Delta(n_1,n_2)=\sum^{|F|}_i=1 I_i(n_1) I_i(n_2)\), that is the
 * number of common fragments rooted at the n1 and n2. It can be computed as:
 * </br> - if the involved node are leaves then
 * \(\Delta_{\sigma}(n_1,n_2)=\mu\lambda\sigma(n_1,n_2)\) </br> - else \( *
 * \Delta_{\sigma}(n_1,n_2)= \displaystyle \mu \sigma(n_1,n_2)\times \Big
 * (\lambda^2 + \hspace{-2em}
 * \sum_{\vec{I}_1,\vec{I}_2,l(\vec{I}_1)=l(\vec{I}_2)}
 * \lambda^{d(\vec{I}_1)+d(\vec{I}_2)} \prod_{j=1}^{l(\vec{I}_1)}
 * \Delta_{\sigma}(c_{n_1}({\vec{I}_{1j}}),c_{n_2}({\vec{I}_{2j})})\Big) \)
 * 
 * </br> where:
 * 
 * </br> - \(\sigma(n_1, n_2)\) is any similarity function between nodes \(n_1\)
 * and \(n_2\), e.g., between their lexical labels;
 * 
 * </br> - \(\vec I_1\) and \(\vec I_2\) denote two sequences of indexes, i.e.,
 * \(\vec I = (i_1, i_{2},..,l(I))\), with \(1 \leq i_1 < i_2 < ..< i_{l(I)} \);
 * 
 * 
 * </br> - \({d(\vec{I}_1)} = \vec{I}_{1l(\vec{I}_1)} - \vec{I}_{11}+1$ and
 * $d(\vec{I}_2)= \vec{I}_{2l(\vec{I}_2)} -\vec{I}_{21}+1$;
 * 
 * </br> - \(c_{n_{1}}(h)\) is the \(h^{th}\) child of the node \(n_{1}\); </br>
 * - \(\lambda, \mu \in [0,1]\) are decay factors to penalize the contribution
 * of large sized fragments.
 * 
 * 
 * </br></br> For more details </br> [Croce et al(2011)] Croce D., Moschitti A.,
 * Basili R. (2011) Structured lexical similarity via convolution kernels on
 * dependency trees. In: Proceedings of EMNLP, Edinburgh, Scotland, UK.
 * 
 * @author Danilo Croce, Giuseppe Castellucci
 */
@JsonTypeName("sptk")
public class SmoothedPartialTreeKernel extends DirectKernel<TreeRepresentation> {
	/**
	 * Vertical Decay Factor
	 */
	private float mu = 0.4f;
	/**
	 * Horizontal Decay factor
	 */
	private float lambda = 0.4f;

	/**
	 * Horizontal Decay factor, pow 2
	 */
	@JsonIgnore
	private float lambda2;

	/**
	 * Multiplicative factor to scale up/down the leaves contribution
	 */
	private float terminalFactor = 1;

	/**
	 * All similarity score below this threshold are ignored
	 */
	private float similarityThreshold = 0.01f;

	/**
	 * The similarity function between tree nodes
	 */
	private StructureElementSimilarityI nodeSimilarity;

	/**
	 * The delta matrix, used to cache the delta functions applied to subtrees
	 */
	private DeltaMatrix deltaMatrix = StaticDeltaMatrix.getInstance();

	private int recursion_id = 0;

	private static final int NO_RESPONSE = -1;

	private static final int MAX_RECURSION = 40;
	private static final int MAX_CHILDREN = 5;

	private static final int MAX_NUMBER_OF_CHILDREN_PT = 100;

	private final float[][][] DPS_buffer = new float[MAX_RECURSION][MAX_NUMBER_OF_CHILDREN_PT][MAX_NUMBER_OF_CHILDREN_PT];
	private final float[][][] DP_buffer = new float[MAX_RECURSION][MAX_NUMBER_OF_CHILDREN_PT][MAX_NUMBER_OF_CHILDREN_PT];
	private final float[][] kernel_mat_buffer = new float[MAX_RECURSION][MAX_NUMBER_OF_CHILDREN_PT
			* MAX_NUMBER_OF_CHILDREN_PT];

	public SmoothedPartialTreeKernel() {
		
	}

	/**
	 * @param LAMBDA
	 *            Horizontal Decay factor
	 * @param MU
	 *            Vertical Decay Factor
	 * @param terminalFactor
	 *            Multiplicative factor to scale up/down the leaves contribution
	 * @param similarityThreshold
	 *            All similarity score below this threshold are ignored
	 * @param nodeSimilarityI
	 *            The similarity function between tree nodes
	 * @param representationIdentifier
	 *            the representation on which operate
	 */
	public SmoothedPartialTreeKernel(float LAMBDA, float MU,
			float terminalFactor, float similarityThreshold,
			StructureElementSimilarityI nodeSimilarity,
			String representationIdentifier) {
		super(representationIdentifier);
		this.lambda = LAMBDA;
		this.lambda2 = LAMBDA * LAMBDA;
		this.mu = MU;
		this.terminalFactor = terminalFactor;
		this.similarityThreshold = similarityThreshold;
		this.nodeSimilarity = nodeSimilarity;
	}

	/**
	 * Determine the subtrees (from the two trees) whose root have a similarity
	 * score above a given threshold
	 * 
	 * @param a
	 *            First Tree
	 * @param b
	 *            Second Tree
	 * 
	 * @return The node pairs having the similarity score above @param
	 *         similarityThreshold
	 */
	private ArrayList<TreeNodePairs> determineSubList(TreeRepresentation a,
			TreeRepresentation b) {
		ArrayList<TreeNodePairs> intersect = new ArrayList<TreeNodePairs>();
		int i = 0, j = 0;
		int n_a, n_b;
		List<TreeNode> list_a, list_b;

		list_a = a.getOrderedNodeSetByLabel();
		list_b = b.getOrderedNodeSetByLabel();
		n_a = list_a.size();
		n_b = list_b.size();

		float sim;
		for (i = 0; i < n_a; i++) {
			for (j = 0; j < n_b; j++) {
				sim = nodeSimilarity.sim(list_a.get(i).getContent(), list_b
						.get(j).getContent());

				if (sim >= similarityThreshold) {
					intersect.add(new TreeNodePairs(list_a.get(i), list_b
							.get(j)));
					deltaMatrix.add(list_a.get(i).getId(), list_b.get(j)
							.getId(), NO_RESPONSE);
				} else {
					deltaMatrix.add(list_a.get(i).getId(), list_b.get(j)
							.getId(), 0);
				}
			}
		}
		return intersect;
	}

	/**
	 * Evaluate the Smoothed Partial Tree Kernel
	 * 
	 * @param a
	 *            First tree
	 * @param b
	 *            Second Tree
	 * @return Kernel value
	 */
	private float evaluateKernelNotNormalize(TreeRepresentation repA,
			TreeRepresentation repB) {

		// Initialize the delta function cache
		deltaMatrix.clear();
		
		ArrayList<TreeNodePairs> pairs = determineSubList(repA, repB);

		float sum = 0;

		for (int i = 0; i < pairs.size(); i++) {
			sum += sptkDeltaFunction(pairs.get(i).getNx(), pairs.get(i).getNz());
		}

		return sum;
	}

	@JsonIgnore
	public DeltaMatrix getDeltaMatrix() {
		return deltaMatrix;
	}

	/**
	 * Get the Vertical Decay factor
	 * 
	 * @return Vertical Decay factor
	 */
	public float getLambda() {
		return lambda;
	}

	/**
	 * Get the Horizontal Decay factor
	 * 
	 * @return Horizontal Decay factor
	 */
	public float getMu() {
		return mu;
	}

	/**
	 * @return The similarity function between tree nodes
	 */
	public StructureElementSimilarityI getNodeSimilarity() {
		return nodeSimilarity;
	}

	/**
	 * @return The similarity threshold. All similarity score below this
	 *         threshold are ignored
	 */
	public float getSimilarityThreshold() {
		return similarityThreshold;
	}

	/**
	 * @return Multiplicative factor to scale up/down the leaves contribution
	 */
	public float getTerminalFactor() {
		return terminalFactor;
	}

	@Override
	public float kernelComputation(TreeRepresentation repA,
			TreeRepresentation repB) {
		return (float) evaluateKernelNotNormalize(repA, repB);
	}

	@JsonIgnore
	public void setDeltaMatrix(DeltaMatrix deltaMatrix) {
		this.deltaMatrix = deltaMatrix;
	}

	/**
	 * @param lambda
	 *            Horizontal Decay factor
	 */
	public void setLambda(float lambda) {
		this.lambda = lambda;
		this.lambda2 = this.lambda * this.lambda;
	}

	/**
	 * @param mu
	 *            Vertical Decay Factor
	 */
	public void setMu(float mu) {
		this.mu = mu;
	}

	/**
	 * @param nodeSimilarity
	 *            The similarity function between structure elements contained in tree nodes
	 */
	public void setNodeSimilarity(StructureElementSimilarityI nodeSimilarity) {
		this.nodeSimilarity = nodeSimilarity;
	}

	/**
	 * @param similarityThreshold
	 *            All similarity score below this threshold are ignored
	 */
	public void setSimilarityThreshold(float similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	/**
	 * @param terminalFactor
	 *            Multiplicative factor to scale up/down the leaves contribution
	 */
	public void setTerminalFactor(float terminalFactor) {
		this.terminalFactor = terminalFactor;
	}

	/**
	 * The Smoothed String Kernel formulation, that recursively estimates the
	 * partial overal between children sequences.
	 * 
	 * @param Sx
	 *            childer of the first subtree
	 * @param Sz
	 *            childer of the second subtree
	 * 
	 * @return string kernel score
	 */
	private float smoothedStringKernelDeltaFunction(ArrayList<TreeNode> Sx,
			ArrayList<TreeNode> Sz) {

		int n = Sx.size();
		int m = Sz.size();

		float[][] DPS = DPS_buffer[recursion_id];
		float[][] DP = DP_buffer[recursion_id];
		float[] kernel_mat = kernel_mat_buffer[recursion_id];
		recursion_id++;

		int i, j, l, p;
		float K;

		p = n;
		if (m < n)
			p = m;

		if (p > MAX_CHILDREN)
			p = MAX_CHILDREN;

		float temp;
		kernel_mat[0] = 0;
		for (i = 1; i <= n; i++) {
			for (j = 1; j <= m; j++) {
				temp = sptkDeltaFunction(Sx.get(i - 1), Sz.get(j - 1));
				// temp = Delta_PT(*(Sx + i - 1), *(Sz + j - 1)) * node_sim(*Sx,
				// *Sz);
				if (temp != NO_RESPONSE) {
					DPS[i][j] = temp;
					kernel_mat[0] += DPS[i][j];
				} else
					DPS[i][j] = 0;
			}
		}
		for (l = 1; l < p; l++) {
			kernel_mat[l] = 0;
			for (j = 0; j <= m; j++)
				DP[l - 1][j] = 0.0f;
			for (i = 0; i <= n; i++)
				DP[i][l - 1] = 0.0f;
			for (i = l; i <= n; i++)
				for (j = l; j <= m; j++) {
					DP[i][j] = DPS[i][j] + lambda * DP[i - 1][j] + lambda
							* DP[i][j - 1] - lambda2 * DP[i - 1][j - 1];

					temp = sptkDeltaFunction(Sx.get(i - 1), Sz.get(j - 1));
					// temp = Delta_PT(*(Sx + i - 1), *(Sz + j - 1)) *
					// node_sim(*Sx, *Sz);
					if (temp != NO_RESPONSE) {
						DPS[i][j] = temp * DP[i - 1][j - 1];
						kernel_mat[l] += DPS[i][j];
					} // else DPS[i][j] = 0;
				}
		}
		// K=kernel_mat[p-1];
		K = 0;
		for (l = 0; l < p; l++) {
			K += kernel_mat[l];
			// printf("String kernel of legnth %d: %1.7f \n\n",l+1,kernel_mat[l]);
		}
		recursion_id--;
		return K;
	}

	/**
	 * Smoothed Partial Tree Kernel Delta Function
	 * 
	 * @param Nx
	 *            root of the first tree
	 * @param Nz
	 *            root of the second tree
	 * @return
	 */
	private float sptkDeltaFunction(TreeNode Nx, TreeNode Nz) {
		if (deltaMatrix.get(Nx.getId(), Nz.getId()) != NO_RESPONSE) {
			return deltaMatrix.get(Nx.getId(), Nz.getId()); // already there
		}
		float sum = 0;
		float sim = this.nodeSimilarity.sim(Nx.getContent(), Nz.getContent());

		if (sim < similarityThreshold) {
			deltaMatrix.add(Nx.getId(), Nz.getId(), 0);
			return 0;
		} else {
			if (Nx.getNoOfChildren() == 0 || Nz.getNoOfChildren() == 0) {
				float val = mu * lambda2 * terminalFactor * sim;
				deltaMatrix.add(Nx.getId(), Nz.getId(), val);
				return val;
			} else {
				sum = sim
						* mu
						* (lambda2 + smoothedStringKernelDeltaFunction(
								Nx.getChildren(), Nz.getChildren()));
				deltaMatrix.add(Nx.getId(), Nz.getId(), sum);
				return sum;
			}
		}
	}
}
