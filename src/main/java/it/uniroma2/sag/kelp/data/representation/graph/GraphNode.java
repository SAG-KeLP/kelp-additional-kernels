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

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElementFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class implements a graph node
 *  
 * @author Giovanni Da San Martino 
 * 
 */
public class GraphNode implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 4670210629107405156L;

	private StructureElement content;
	
	private int id;
	
	//private ArrayList<GraphNode> incomingEdges;
	private List<GraphNode> outgoingEdges;
	
	/**
	 * Initializes a graph node
	 * 
	 * @param id the id of the node
	 * @param content the content of the node
	 */
	public GraphNode(int id, StructureElement content) {
		this.id = id;
		this.setContent(content);
		this.outgoingEdges = new ArrayList<GraphNode>();
		//this.incomingEdges = new ArrayList<GraphNode>();
	}

	/**
	 * Adds an incoming edge from <code>node</code> 
	 * 
	 * @param node the origin of the incoming edge
	 
	public void addIncomingEdge(GraphNode node) {
		this.incomingEdges.add(node);
	}
	 **/
	/**
	 * Adds an outgoing edge from this node to <code>n</code>
	 * 
	 * @param n the destination node
	 */
	public void addOutgoingEdge(GraphNode n) {
		this.outgoingEdges.add(n);
	}
	
	@JsonIgnore
	public StructureElement getContent(){
		return this.content;
	}
	
	/**
	 * Get the label of a node
	 * @return a string representing the label of a node
	 */
	public String getNodeLabel() {
		if (this.content.getTextFromData()==null) {
			System.out.println("Trying to access the label of a node which does not have one");
			System.exit(1);
		}
		return this.content.getTextFromData();
	}
	
	/**
	 * Sets the content of this node
	 * 
	 * @param content the content to be set
	 */
	public void setContent(StructureElement content){
		this.content=content;
	}
	
	/**
	 * Returns the id of this node
	 * 
	 * @return the id of this node
	 */
	public int getId() {
		return id;
	}
	
//	/**
//	 * Sets 
//	 * 
//	 * @param id
//	 */
//	public void setId(Integer id) {
//		this.id = id;
//	}
	
	public List<GraphNode> getNeighbours() {
		return this.outgoingEdges;
	}
	
	@Override
	public String toString() {
		String graphNodeString = "";
		graphNodeString += this.getId() + " ";
		graphNodeString += StructureElementFactory.getTextualRepresentation(this.getContent());
		return graphNodeString;
	}
	
	public String edgesToString() {
		StringBuilder edgeString = new StringBuilder();
		for(GraphNode e : this.outgoingEdges){
			edgeString.append(this.getId() + " " + e.getId() + DirectedGraphRepresentation.EDGE_SEPARATOR);
		}
		if (edgeString.length() == 0) {
			return "";
		}		
		edgeString.setLength(edgeString.length() - DirectedGraphRepresentation.EDGE_SEPARATOR.length()); //delete last char, i.e. #
		return edgeString.toString();
	}
	
	
}
