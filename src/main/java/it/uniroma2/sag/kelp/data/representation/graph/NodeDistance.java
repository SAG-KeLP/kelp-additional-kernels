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

import java.io.Serializable;

/**
 * This class corresponds to a triple \(<n_a, n_b, d>\) where \(n_a\) is the initial node,
 * \(n_b\) is the final node and \(d\) is the distance of the shortest path from \(n_a\) to \(n_b)
 * 
 * @author Simone Filice
 *
 */
public class NodeDistance implements Serializable, Comparable<NodeDistance>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2760208191540762316L;

	private GraphNode initialNode;

	private GraphNode finalNode;
	private float distance;
	private int hash;

	public NodeDistance(GraphNode initialNode, GraphNode finalNode, float distance) {
		super();
		this.initialNode = initialNode;
		this.finalNode = finalNode;
		this.distance = distance;
		this.hash = (initialNode.getContent().getTextFromData()+finalNode.getContent().getTextFromData()+distance).hashCode();
	}

	/**
	 * @return the initialNode
	 */
	public GraphNode getInitialNode() {
		return initialNode;
	}

	/**
	 * @return the finalNode
	 */
	public GraphNode getFinalNode() {
		return finalNode;
	}

	/**
	 * @return the distance
	 */
	public float getDistance() {
		return distance;
	}

	@Override
	public int compareTo(NodeDistance nodeDistance) {
		int initialNodeComparison = this.initialNode.getContent().getTextFromData().compareTo(nodeDistance.initialNode.getContent().getTextFromData());
		if(initialNodeComparison==0){
			int finalNodeComparison = this.finalNode.getContent().getTextFromData().compareTo(nodeDistance.finalNode.getContent().getTextFromData());
			if(finalNodeComparison==0){
				if(this.distance>nodeDistance.distance){
					return 1;
				}else if(this.distance==nodeDistance.distance){
					return 0;
				}else{
					return -1;
				}
				
			}
			return finalNodeComparison;
		}
		return initialNodeComparison;
	}


	public boolean equalsIgnoreDistance(Object nodeDistance){
		if(this==nodeDistance){
			return true;
		}
		if(nodeDistance instanceof NodeDistance){
			NodeDistance that = (NodeDistance) nodeDistance;
			if(!this.initialNode.getContent().getTextFromData().equals(that.initialNode.getContent().getTextFromData())){
				return false;
			}
			return this.finalNode.getContent().getTextFromData().equals(that.finalNode.getContent().getTextFromData());
		}
		return false;
	}

	@Override
	public boolean equals(Object nodeDistance){


		if(this==nodeDistance){
			return true;
		}
		if(nodeDistance instanceof NodeDistance){

			NodeDistance that = (NodeDistance) nodeDistance;
			if(this.distance != that.distance){
				return false;
			}
			if(!this.initialNode.getContent().getTextFromData().equals(that.initialNode.getContent().getTextFromData())){
				return false;
			}
			return this.finalNode.getContent().getTextFromData().equals(that.finalNode.getContent().getTextFromData());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return hash;
	}



}
