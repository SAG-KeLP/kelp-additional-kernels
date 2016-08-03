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

package it.uniroma2.sag.kelp.data.representation.graph;
import gnu.trove.map.hash.TIntIntHashMap;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElementFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * This class implements a directed Graph representation. 
 * 
 * 
 * @author Giovanni Da San Martino
 *
 */
@JsonTypeName("G")
public class DirectedGraphRepresentation implements Representation {
			
	private Logger logger = LoggerFactory.getLogger(DirectedGraphRepresentation.class);
	
	private static final String NODE_SEPARATOR = "&&";
	public static final String EDGE_SEPARATOR = "&&";
	private static final String NODE_EDGE_SEPARATOR = "%%";
	private static final long serialVersionUID = 5688462570989530911L;

	protected List<GraphNode> nodes; 	
	protected TIntIntHashMap nodesIdToGraphObjs;
	protected List<NodeDistance> nodesDistances = null;
	
	public DirectedGraphRepresentation() {
		this.nodes = new ArrayList<GraphNode>();
		this.nodesIdToGraphObjs = new TIntIntHashMap(0, 0.5f,-1,-1);
	}

	@Override //required by Representation
	public String getTextFromData() { 
		StringBuilder graphString = new StringBuilder();
		String tmpEdgeString;
		for(GraphNode g : this.nodes){ //TODO: order nodes by Id
			graphString.append(g.toString());
			graphString.append(NODE_SEPARATOR);
		}
		if (graphString.length() == 0) {
			System.out.println("Empty Graph!!");
			return "";
		}		
		graphString.setLength(graphString.length() - NODE_SEPARATOR.length()); //delete last separator
		graphString.append(NODE_EDGE_SEPARATOR);
		for(GraphNode g : this.nodes){
			tmpEdgeString = g.edgesToString();
			if (tmpEdgeString.length() > 0) {
				graphString.append(tmpEdgeString + EDGE_SEPARATOR);
			}
		}
		return graphString.toString();
	}

	@Override //required by Representation
	public void setDataFromText(String graphString) throws Exception { 
		String inputString = graphString.trim();
		String[] nodeList = null;
		String[] edgeList = null;
		if (inputString.contains(NODE_EDGE_SEPARATOR)) {
			String[] resultString = inputString.split(NODE_EDGE_SEPARATOR);
			nodeList = resultString[0].split(NODE_SEPARATOR);
			edgeList = resultString[1].split(EDGE_SEPARATOR);
		}else { //no edges are present
			nodeList = inputString.split(NODE_SEPARATOR);
		}
		//this.InitNodesData(nodeList.length);
		for(String nodeS : nodeList){
			//read a number, discard following space, read the rest as StructureElement
			String[] res = nodeS.split(" ");
			Integer nodeId = Integer.parseInt(res[0]);
			StructureElement content = StructureElementFactory.getInstance().parseStructureElement(res[1]);
			this.addNode(nodeId, content);
		}
		if (edgeList != null) {
			for(String edgeS : edgeList){
				String[] res = edgeS.split(" ");
				Integer firstNodeId = Integer.parseInt(res[0]);
				Integer secondNodeId = Integer.parseInt(res[1]);
				this.addEdge(firstNodeId, secondNodeId);
			}
		}
	}

	/**
	 * Given a node id, return the corresponding GraphNode object
	 * @param Id
	 * @return the graphNode
	 */
	public GraphNode getNodeFromID(Integer Id) {
		
		Integer arrayPosition = this.nodesIdToGraphObjs.get(Id);
		if (arrayPosition == -1) {
			System.out.print("Node ID not Found! ");
			System.out.println(Id);
			System.exit(0);
		}
		return this.nodes.get(arrayPosition);
	}
	
	/**
	 * Adds a node whose id is <code>nodeId</code> and whose content is <code>nodeContent</code>
	 * 
	 * @param nodeId the id of the node to be added
	 * @param nodeContent the content of the node to be added
	 */
	public void addNode(Integer nodeId, StructureElement nodeContent) {
		
		GraphNode v = new GraphNode(nodeId , nodeContent);
		this.nodes.add(v);
		this.nodesIdToGraphObjs.put(nodeId, this.nodes.size()-1);
	}
	
	
	/**
	 * Adds an edge between the nodes whose IDs are <code>firstNodeId</code>
	 * and <code>secondNodeId</code>
	 * 
	 * @param firstNodeId the id of the first node
	 * @param secondNodeId the id of the second node
	 */
	public void addEdge(Integer firstNodeId, Integer secondNodeId) {
		
		this.getNodeFromID(firstNodeId).addOutgoingEdge(this.getNodeFromID(secondNodeId));
		//this.getNodeFromID(secondNodeId).addIncomingEdge(this.getNodeFromID(firstNodeId));//set a flag to control whether incoming edges are actually added?
	}
	
	/**
	 * Returns a list containing all the nodes in this graph
	 * 
	 * @return a list of GraphNode objects
	 */
	@JsonIgnore
	public List<GraphNode> getNodeList() {
		return this.nodes;
	}
	
	/**
	 * Returns the number of nodes in the graph.
	 * @return an int representing the number of nodes of the graph
	 */
	@JsonIgnore
	public int getNumberOfNodes() {
		return this.nodes.size();
	}

/*	*//**
	 * Compute the distances between a given node and all other nodes in the graph.
	 * The results are stored into the field <code>nodesDistancesMap</code> (an hash map). 
	 * In order to access or modify the hash map use the methods <code>setNodeDistance()</code>
	 * and <code>getNodeDistance()</code>
	 *//*
	public int[] computeDistancesFromNode(int nodeIndex) {
		this.nodesDistances = new ArrayList<NodeDistance>(0);
		int nnodes = this.getNumberOfNodes();
		int[] tmpNodeDistances = new int[nnodes];
		ArrayList<Integer> stack = new ArrayList<Integer>();
		int j, arrayPosition, depth;
		for(j=0; j<nnodes; j++) {
				tmpNodeDistances[j] = Integer.MAX_VALUE;
			}
			stack.add(nodeIndex);
			depth = 0;
			while(stack.size()>0) {
				j=stack.remove(0);
				depth++;
				for(GraphNode v : this.nodes.get(j).getNeighbours()) {
					arrayPosition = this.nodesIdToGraphObjs.get(v.getId());
					if(depth < tmpNodeDistances[arrayPosition]) {
						tmpNodeDistances[arrayPosition] = depth;
						stack.add(arrayPosition);
						//System.out.println("SP: adding pair (" + i + ", " + arrayPosition + ") -> " + depth);
					}
				}
		}
		return tmpNodeDistances;		
	}
*/
	
	/**
	 * Compute the distances between all pairs of nodes in the graph.
	 * The results are stored into the field <code>nodesDistancesMap</code> (an hash map). 
	 * In order to access or modify the hash map use the methods <code>setNodeDistance()</code>
	 * and <code>getNodeDistance()</code>
	 */
	private void computeNodeDistances() {
		this.nodesDistances = new ArrayList<NodeDistance>(0);
		int nnodes = this.getNumberOfNodes();
		int[] tmpNodeDistances = new int[nnodes];
		ArrayList<Integer> stack = new ArrayList<Integer>();
		int j, arrayPosition, depth;
		for(int i=0; i<nnodes; i++) {
			for(j=0; j<nnodes; j++) {
				tmpNodeDistances[j] = Integer.MAX_VALUE;
			}
			stack.add(i);
			depth = 0;
			while(stack.size()>0) {
				j=stack.remove(0);
				depth++;
				for(GraphNode v : this.nodes.get(j).getNeighbours()) {
					arrayPosition = this.nodesIdToGraphObjs.get(v.getId());
					if(depth < tmpNodeDistances[arrayPosition]) {
						tmpNodeDistances[arrayPosition] = depth;
						stack.add(arrayPosition);
						//System.out.println("SP: adding pair (" + i + ", " + arrayPosition + ") -> " + depth);
					}
				}
			}
			for(j=0; j<nnodes; j++) {
				depth = tmpNodeDistances[j];
				if (depth < Integer.MAX_VALUE) {
					this.setNodesDistance(i, j, (float) tmpNodeDistances[j]);
				}
			}
		}
		
		Collections.sort(this.nodesDistances);
		
	}
	
	/**
	 * Returns the list of NodeDistance Objects
	 * @return
	 */
	@JsonIgnore
	public List<NodeDistance> getNodeDistances(){
		if(this.nodesDistances==null){
			computeNodeDistances();
		}
		return this.nodesDistances;
	}
	
	/**
	 * Encode the distance between two nodes in the field nodesDistancesMap  
	 * @param firstNodeIndex, index of the first node of the pair in <code>nodes</code>
	 * @param secondNodeIndex, index of the second node of the pair in <code>nodes</code>
	 * @param distance, the distance between the two nodes
	 */
	private void setNodesDistance(int firstNodeIndex, int secondNodeIndex, float distance) {
		NodeDistance nodeDistance = new NodeDistance(this.nodes.get(firstNodeIndex), this.nodes.get(secondNodeIndex), distance);
		
		this.nodesDistances.add(nodeDistance);
	}
	
	@Override
	public boolean isCompatible(Representation rep) {
		if (!( rep instanceof DirectedGraphRepresentation)){
			logger.error("incompatible representations: " + this.getClass().getSimpleName() + " vs " + rep.getClass().getSimpleName());
			return false;
		}
		return true;
	}

//	/**
//	 * Given a pair of nodes, return the, previously computed, distance between them
//	 * @param nodePairKey, long encoding the node-pair
//	 * @return the distance between the two nodes
//	 */
//	public float getNodePairDistance(long nodePairKey) {
//		if(this.nodesDistancesMap==null) {
//			this.computeNodeDistances();
//		}
//		return this.nodesDistancesMap.get(nodePairKey);
//		//return this.nodesDistancesMap.get(this.getNodePairKey(firstNodeIndex, secondNodeIndex));
//	}
	
//	/**
//	 * Given a pair of node indices, returns the corresponding key in the hash map <code>nodesDistancesMap</code> 
//	 * @param firstNodeIndex, index of the first node of the pair in <code>nodes</code>
//	 * @param secondNodeIndex, index of the second node of the pair in <code>nodes</code>
//	 * @return the key related to the node pair in <code>nodesDistancesMap</code>
//	 */
//	private long getNodePairKey(int firstNodeIndex, int secondNodeIndex) {
//		if (firstNodeIndex <= secondNodeIndex) {
//			return (long)firstNodeIndex << 32 | secondNodeIndex & 0xFFFFFFFFL;
//		}else{
//			return (long)secondNodeIndex << 32 | firstNodeIndex & 0xFFFFFFFFL;
//		}
//	}

}

