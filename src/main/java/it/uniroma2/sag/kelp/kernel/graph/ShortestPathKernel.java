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

package it.uniroma2.sag.kelp.kernel.graph;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import it.uniroma2.sag.kelp.data.representation.graph.DirectedGraphRepresentation;
import it.uniroma2.sag.kelp.data.representation.graph.GraphUtils;
import it.uniroma2.sag.kelp.data.representation.graph.NodeDistance;
import it.uniroma2.sag.kelp.kernel.DirectKernel;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Implementation of the Shortest Path Kernel for Graphs
 * 
 * Reference paper:
 * [1] K. M. Borgwardt and H.-P. Kriegel, “Shortest-Path Kernels on Graphs,” in 
 * Proceedings of the Fifth IEEE International Conference on Data Mining, 2005, pp. 74–81.
 * 
 * @author Giovanni Da San Martino, Simone Filice
 */
@JsonTypeName("shortestPath")
public class ShortestPathKernel extends DirectKernel<DirectedGraphRepresentation> {

	public ShortestPathKernel() {
		super();
	}
	
	public ShortestPathKernel(String representation){
		super(representation);
	}

	@Override
	public float kernelComputation(DirectedGraphRepresentation exA,
			DirectedGraphRepresentation exB) {

		TObjectIntMap<NodeDistance> labelDistancesA = GraphUtils.getLabelDistances(exA);
		TObjectIntMap<NodeDistance> labelDistancesB = GraphUtils.getLabelDistances(exB);
		
		float sum = 0;

		TObjectIntMap<NodeDistance> shortest;
		TObjectIntMap<NodeDistance> longest;
		if (labelDistancesA.size() < labelDistancesB.size()) {
			shortest = labelDistancesA;
			longest = labelDistancesB;
		} else {
			shortest = labelDistancesB;
			longest = labelDistancesA;
		}
		for (TObjectIntIterator<NodeDistance> it = shortest.iterator(); it.hasNext();) {
			it.advance();
			float shortestValue = it.value();
			float longestValue = longest.get(it.key());
			sum += shortestValue * longestValue;
		}
		return sum;
				
	}
}
