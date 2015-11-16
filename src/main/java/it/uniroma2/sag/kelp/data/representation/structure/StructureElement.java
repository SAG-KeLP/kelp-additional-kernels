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

package it.uniroma2.sag.kelp.data.representation.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class represent the atomic element of a discrete structure. It has been
 * designed to represent the "content" of each basic element of a structure.
 * 
 * @author Simone Filice, Danilo Croce
 * 
 */
public abstract class StructureElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4570173314652208985L;

	protected HashMap<String, Object> additionalInformation = new HashMap<String, Object>();

	/**
	 * Initializes a StructureElement using its textual description provided in
	 * <code>structureElementDescription</code>
	 * 
	 * @param structureElementDescription
	 *            the textual description of the structureElement to be
	 *            initialized
	 */
	public abstract void setDataFromText(String structureElementDescription)
			throws Exception;

	/**
	 * Returns a textual representation of the data stored in this
	 * structureElement
	 * 
	 * @return a textual representation of the data stored in this
	 *         structureElement
	 */
	public abstract String getTextFromData();
	
	/**
	 * Adds an additional information identified by <code>infoName</code>
	 * 
	 * @param infoName the identifier of the additional information
	 * @param infoContent the additional information
	 */
	public void addAdditionalInformation(String infoName, Object infoContent){
		this.additionalInformation.put(infoName, infoContent);
	}
	
	/**
	 * Returns the additional information identified by <code>infoName</code>
	 * 
	 * @param infoName the identifier of the additional information
	 * @return the additional information identified by <code>infoName</code>;
	 * <code>null</node> if that information is not contained
	 */
	public Object getAdditionalInformation(String infoName){
		return this.additionalInformation.get(infoName);
	}
	
	/**
	 * Verifies whether this element contains the additional information
	 * identified by <code>infoName</code>
	 * 
	 * @param infoName the identifier of the additional information
	 * @return <code>true</code> if this element contains the additional information
	 * identified by <code>infoName</code>;
	 * <code>false</code> otherwise
	 */
	public boolean containsAdditionalInfo(String infoName){
		return this.additionalInformation.containsKey(infoName);
	}
	
	public Object removeAdditionalInformation(String infoName){
		return this.additionalInformation.remove(infoName);
	}
	
	/**
	 * Returns the textual format of the content, concatenated with all the 
	 * additional information added to this element
	 * 
	 * @return the textual format of the content, concatenated with all the 
	 * additional information added to this element
	 */
	public String getTextFromDataWithAdditionalInfo(){
		String output = this.getTextFromData();
		if(this.additionalInformation.size()==0){
			return output;
		}
		
		output+= "{";
		for(Entry<String, Object> entry: this.additionalInformation.entrySet()){
			output+=entry.getKey();
			if(entry.getValue()!=null){
				output += entry.getValue().toString();
			}
		}
		return output+"}";
	}
	

	
}
