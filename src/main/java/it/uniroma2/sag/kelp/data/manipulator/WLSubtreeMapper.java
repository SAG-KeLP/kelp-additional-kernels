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

package it.uniroma2.sag.kelp.data.manipulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.representation.graph.DirectedGraphRepresentation;
import it.uniroma2.sag.kelp.data.representation.graph.GraphNode;
import it.uniroma2.sag.kelp.data.representation.vector.SparseVector;


/**
 * Explicit feature extraction for the Weisfeiler-Lehman Subtree Kernel for Graphs.
 * The features of the kernel are trees resulting from breadth-first visits of 
 * depth up to h. 
 * The implementation is based on a relabeling process of the nodes of the 
 * graphs (such novel labels are stored into an ad hoc field of the GraphNode object.  
 * 
 * The only parameter of the kernel is the maximum depth of the visits, h.
 * In order to set the parameter, use the method setIterationNumber(int h);
 * 
 * Reference paper: 
 * [1] N. Shervashidze, “Weisfeiler-lehman graph kernels,” JMLR, vol. 12, pp. 2539–2561, 2011.
 * 
 * @author Giovanni Da San Martino
 *
 */
public class WLSubtreeMapper implements Manipulator{

	private String graphRepresentation;
	private String vectorRepresentation;
	private static final String NODE_RELABELLING_NAME="hash";
	private static TObjectIntMap<String> fromContentToInt = new TObjectIntHashMap<String>();
	private static Integer contentCounter = 0;
	private int h = -1; 

	public WLSubtreeMapper(String graphName, String vectorName, int h) {
		this.graphRepresentation = graphName;
		this.vectorRepresentation = vectorName;
		this.h = h;
	}
	
	/**
	 * Sets the maximum depth of the visits of the WL kernel
	 * 
	 * @param h the maximum depth of the visits
	 */
	public void setIterationNumber(int h) {
		this.h = h;
	}

	/**
	 * Returns the maximum depth of the visits of the WL kernel
	 * 
	 * @return the maximum depth of the visits of the WL kernel
	 */
	public int getIterationNumber() {
		return this.h;
	}

	@Override
	public void manipulate(Example example) {
		DirectedGraphRepresentation g = (DirectedGraphRepresentation) example.getRepresentation(this.graphRepresentation);
		if(g==null){
			return;
		}
		SparseVector featureVector = new SparseVector();
		List<GraphNode> nodeList = g.getNodeList();
		int nnodes = nodeList.size();
		int[] newlabels = new int[nnodes];
		int tmpLabel;
		//relabel nodes as integers, so that the relabeling function is always int -> int
		for (int i=0; i<nnodes; i+=1) {
			featureVector.incrementFeature(String.valueOf(mapNodeLabelToInt(nodeList.get(i))), 1.0f);
		}

		for (int l=1; l <= this.h; l++) {
			for (int i=0; i<nnodes; i++) {
				tmpLabel = relabelNode(nodeList.get(i));
				if (tmpLabel > -1 && tmpLabel != this.getContentHash(nodeList.get(i))) {
					newlabels[i] = tmpLabel;	
					featureVector.incrementFeature(String.valueOf(newlabels[i]), 1.0f);
				}
			}
			//write the new labels in the GraphNode Object  
			for (int i=0; i<nnodes; i+=1) {
				this.setContentHash(nodeList.get(i), newlabels[i]);
			}
		}
		example.addRepresentation(vectorRepresentation, featureVector);
		
	}

	/**
	 * Computes the novel label of the node according to the relabeling scheme defined in [1]
	 * 
	 * @param node
	 * @return the int representing the encoding of the node 
	 */
	private int relabelNode(GraphNode node) {
		ArrayList<Integer> tmpLabels = new ArrayList<Integer>();
		for (GraphNode v : node.getNeighbours()) {
			//System.out.println("h=" + this.getIterationNumber() + ", from node " + this.nodeList.get(i).getContent().getTextFromData() + " to node " + v.getContent().getTextFromData());
			tmpLabels.add((Integer) v.getContent().getAdditionalInformation(NODE_RELABELLING_NAME));
		}
		if (tmpLabels.size() == 0) 
			return -1;
		//order the list
		Collections.sort(tmpLabels);
		//add the int of the current node as a prefix
		tmpLabels.add(0, this.getContentHash(node));
		return this.ConvertStringToInt(tmpLabels.toString());		
	}

	/**
	 * Creates a new Structure Element field. The value of such field is the transformation 
	 * of the label of a node into a numerical one (int). 
	 * 
	 * @param node 
	 * @return the novel numerical (int) label 
	 */
	private int mapNodeLabelToInt(GraphNode node) {

		String nodeLabel = node.getContent().getTextFromData();
		int intLabel = this.ConvertStringToInt(nodeLabel);
		this.setContentHash(node, intLabel);

		//System.out.println("Adding " + nodeLabel + " encoded as " + intLabel);
		//System.out.println(fromContentToInt.toString());

		return intLabel;
	}

	/**
	 * Convert a string into a numerical value
	 * 
	 * @param s, the string to be converted
	 * @return the integer resulting from the conversion
	 */
	private int ConvertStringToInt(String s) {
		int intLabel;
		if (fromContentToInt.containsKey(s)) {
			intLabel= fromContentToInt.get(s);
		} else {
			intLabel = contentCounter;
			fromContentToInt.put(s, intLabel);
			contentCounter += 1;
		}
		return intLabel;
	}



	/**
	 * Reads the current numerical label of the node, as produced by the relabeling 
	 * process of the WL kernel.  
	 * 
	 * @param node
	 * @return the novel numerical label of the node
	 */
	private int getContentHash(GraphNode node) {
		return (Integer) node.getContent().getAdditionalInformation(NODE_RELABELLING_NAME);
	}

	/**
	 * Set the current numerical label of a node (in an ad hoc field of the GraphNode object)
	 * @param node
	 * @param hashValue the novel numerical label of the node
	 */
	private void setContentHash(GraphNode node, int hashValue) {
		node.getContent().addAdditionalInformation(NODE_RELABELLING_NAME, hashValue);
	}

}
