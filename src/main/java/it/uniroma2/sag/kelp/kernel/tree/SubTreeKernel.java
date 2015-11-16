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
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNodePairs;
import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.DeltaMatrix;
import it.uniroma2.sag.kelp.kernel.tree.deltamatrix.StaticDeltaMatrix;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * SubTree Kernel implementation.
 * 
 * A SubTree Kernel is a convolution kernel that evaluates the tree fragments 
 * shared between two trees. The considered fragments are are subtrees, i.e. a node and its
 * complete descendancy. 
 * 
 * The kernel function is defined as:
 * </br>
 * 
 * \(K(T_1,T_2) = \sum_{n_1 \in N_{T_1}} \sum_{n_2 \in N_{T_2}}
 * \Delta(n_1,n_2)\)
 * 
 * </br>
 * 
 * where \(\Delta(n_1,n_2)\) can be computed as:</br> - if productions at
 * \(n_1\) and \(n_2\) are different then \(\Delta(n_1,n_2)=0\)</br> - if the
 * productions at n1 and n2 are the same, and \(n_1\) and \(n_2\) have only leaf
 * children then \(\Delta(n_1,n_2)=\lambda\)</br> - if the productions at n1 and
 * n2 are the same, and \(n_1\) and \(n_2\) are not pre-terminals then</br>
 * \(\Delta(n_1,n_2)=\lambda \prod_{j=1}^{nc(n_1)} (\Delta(c_{n_1}^j,
 * c_{n_2}^j))\)
 * 
 * </br></br> For more details see [Vishwanathan and Smola, 2003; Moschitti,
 * EACL 2006]
 * 
 * [Vishwanathan and Smola, 2003], S.V.N. Vishwanathan and A.J. Smola. Fast
 * kernels on strings and trees. In Proceedings of Neural Information Processing
 * Systems, 2003.
 * 
 * [Moschitti, EACL2006] Alessandro Moschitti. Making Tree Kernels Practical for
 * Natural Language Learning EACL, (2006)
 * 
 * @author Danilo Croce, Giuseppe Castellucci, Simone Filice
 */

@JsonTypeName("stk")
public class SubTreeKernel extends DirectKernel<TreeRepresentation>{

	/**
	 * Decay factor
	 */
	private float lambda;

	private boolean includeLeaves = true;
	
	/**
	 * The delta matrix, used to cache the delta functions applied to subtrees
	 */
	private DeltaMatrix deltaMatrix = StaticDeltaMatrix.getInstance();

	/**
	 * SubTree Kernel
	 * 
	 * @param lambda
	 *            Decay Factor
	 * @param representationIdentifier
	 *            Identifier of the Tree representation on which the kernel
	 *            works
	 */
	public SubTreeKernel(float lambda, String representationIdentifier) {
		super(representationIdentifier);
		this.lambda = lambda;
	}

	/**
	 * SubTree Kernel constructor. It uses lambda=0.4
	 * 
	 * @param representationIdentifier
	 *            Identifier of the Tree representation on which the kernel
	 *            works
	 */
	public SubTreeKernel(String representationIdentifier) {
		this(0.4f, representationIdentifier);
	}

	/**
	 * SubTree Kernel: default constructor. It should not be used, please use
	 * SubTreeKernel(String) or SubTreeKernel(float,String). This is only used
	 * by the json serializer/deserializer.
	 */
	public SubTreeKernel() {
		this(0.4f, "0");
	}

	/**
	 * Get the decay factor
	 * 
	 * @return the decay factor
	 */
	public float getLambda() {
		return lambda;
	}

	/**
	 * Set the decay factor
	 * 
	 * @param lambda
	 *            the decay factor
	 */
	public void setLambda(float lambda) {
		this.lambda = lambda;
	}
	
	/**
	 * Get the DeltaMatrix used to store the evaluated delta functions 
	 * of this tree kernel 
	 * 
	 * @return the the DeltaMatrix
	 */
	@JsonIgnore
	public DeltaMatrix getDeltaMatrix() {
		return deltaMatrix;
	}

	/**
	 * Sets the DeltaMatrix used to store the evaluated delta functions 
	 * of this tree kernel 
	 * 
	 * @param the the DeltaMatrix to set
	 */
	@JsonIgnore
	public void setDeltaMatrix(DeltaMatrix deltaMatrix) {
		this.deltaMatrix = deltaMatrix;
	}

	/**
	 * Returns whether the leaves must be involved in the kernel computation
	 * 
	 * @return the includeLeaves
	 */
	public boolean getIncludeLeaves() {
		return includeLeaves;
	}

	/**
	 * Sets whether the leaves must be involved in the kernel computation.
	 * When leaves are not included this kernel corresponds
	 * to the ST kernel in SvmLightTK (option -F 0). 
	 * When leaves are included this kernel becomes an implicit linear combination
	 * between the standard ST kernel and a Bag-of-Words on the leaves
	 * 
	 * @param includeLeaves the includeLeaves to set
	 */
	public void setIncludeLeaves(boolean includeLeaves) {
		this.includeLeaves = includeLeaves;
	}

	@Override
	public float kernelComputation(TreeRepresentation repA,
			TreeRepresentation repB) {
		float sum = 0;

		// Initialize the delta function cache
		deltaMatrix.clear();

		// Determine the subtrees whose root have the same label. This
		// optimization has been proposed in [Moschitti, EACL 2006].
		ArrayList<TreeNodePairs> pairs = TreeKernelUtils.findCommonNodesByProduction(repA, repB, deltaMatrix, includeLeaves);
//		if(includeLeaves){
//			pairs = TreeKernelUtils.findCommonNodesByProduction(a, b, deltaMatrix);
//		}else{
//			pairs = TreeKernelUtils.findCommonNodesByProductionIgnoringLeaves(a, b, deltaMatrix);
//		}
		
		
		// Estimate the kernel function
		for (int i = 0; i < pairs.size(); i++) {

			float deltaValue = TreeKernelUtils.productionBasedDeltaFunction(pairs.get(i).getNx(), pairs
					.get(i).getNz(), 0, lambda, deltaMatrix);

			sum += deltaValue;
		}

		return sum;
	}
	
}