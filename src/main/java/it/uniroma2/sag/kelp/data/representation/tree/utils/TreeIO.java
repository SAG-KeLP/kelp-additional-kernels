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

package it.uniroma2.sag.kelp.data.representation.tree.utils;

import it.uniroma2.sag.kelp.data.representation.structure.StructureElement;
import it.uniroma2.sag.kelp.data.representation.structure.StructureElementFactory;
import it.uniroma2.sag.kelp.data.representation.tree.node.TreeNode;

/**
 * Parse a tree in a string format.
 * 
 * @author Danilo Croce, Giuseppe Castellucci
 * 
 */
public class TreeIO {

	public static final String INDENTIFIER = "T";
	/**
	 * The left parenthesis character within the tree
	 */
	public static final char LRB = '(';
	/**
	 * The right parenthesis character within the tree
	 */
	public static final char RRB = ')';

	/**
	 * This method allows to read a tree in the form (S(NP)(VP)) and returns the TreeNode
	 * corresponding to the root of the tree
	 * 
	 * @param sentence
	 *            The input String
	 * @return The TreeNode corresponding to the root of the tree
	 * @throws TreeIOException
	 *             his exception is thrown if any problem in the tree IO phase is
	 *             experimented
	 */
	public TreeNode parseCharniakSentence(String sentence)
			throws TreeIOException {
		String inputString = sentence.trim();
		inputString = _preprocess(inputString);
		inputString = inputString.replaceAll(" ", "");
		return _parseCharniakSentence(inputString, null, 1);
		
	}

	/**
	 * This recursive function allows to read the tree encoded in a string in
	 * the parentheses form, such as (S(NP)(VP))
	 * 
	 * @param sentence
	 * @param father
	 * @param nodeCounter
	 * @return
	 * @throws TreeIOException
	 */
	protected TreeNode _parseCharniakSentence(String sentence, TreeNode father,
			Integer nodeCounter) throws TreeIOException {
		String inputString = sentence.trim();
		String nodeString = "";
		inputString = inputString.substring(1, inputString.length() - 1);
		String nodeStr;
		int firstParentId = inputString.indexOf(LRB);

		// it is a leaf
		if (firstParentId == -1) {
			nodeStr = inputString;
		}
		// it has children
		else {
			nodeStr = inputString.substring(0, inputString.indexOf(LRB));
		}

		TreeNode node;
		try {
			node = parseNode(nodeCounter, nodeStr, father);
		} catch (InstantiationException e) {
			throw new TreeIOException(e.getMessage());
		}
		// if the node label contains a "::", it contains also a suffix
		// TODO
		// if (nodeStr.contains("::")) {
		// String suffix = nodeStr.substring(nodeStr.indexOf("::") + 2,
		// nodeStr.length());
		// node = new TreeNode(nodeCounter, nodeStr, suffix, father);
		// } else {
		// node = new TreeNode(nodeCounter, nodeStr, father);
		// }

		// parentheses counter
		int pCounter = 0;
		int startIndex = 0;
		int endIndex = 0;

		for (int i = 0; i < inputString.length(); i++) {
			char ptr = inputString.charAt(i);
			if (ptr == LRB) {
				if (pCounter == 0) {
					startIndex = i;
				}
				pCounter++;
			} else if (ptr == RRB) {
				pCounter--;
				if (pCounter == 0) {
					endIndex = i + 1;
				}
			}

			if (pCounter == 0 && startIndex != endIndex) {
				nodeString = inputString.substring(startIndex, endIndex);
				TreeNode child = _parseCharniakSentence(nodeString, node,
						++nodeCounter);
				node.getChildren().add(child);
				nodeCounter = child.getMaxId();
			}
		}
		if (pCounter != 0) {
			throw new TreeIOException(
					"Error in analyzing: "
							+ sentence
							+ ".\nThe number of opened and close parentheses are different.");
		}
		return node;
	}

	protected TreeNode parseNode(int nodeCounter, String nodeStr,
			TreeNode father) throws InstantiationException {
		StructureElement content = StructureElementFactory.getInstance().parseStructureElement(nodeStr);
		return new TreeNode(nodeCounter, content, father);
	}

	/**
	 * This function cleans the input string, such as from spaces, before the
	 * parsing phase.
	 * 
	 * @param inputString
	 *            The input String to be cleaned
	 * @return The cleaned String
	 */
	protected String _preprocess(String inputString) {
		int novelParentOpened = 0;
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < inputString.length(); i++) {
			if (inputString.charAt(i) == ' ' && i < inputString.length() - 1
					&& inputString.charAt(i + 1) != LRB
					&& inputString.charAt(i + 1) != RRB) {
				stringBuffer.append(inputString.charAt(i));
				stringBuffer.append(LRB);
				novelParentOpened++;
			} else {
				if (inputString.charAt(i) == RRB && novelParentOpened > 0) {
					stringBuffer.append(RRB);
					novelParentOpened--;
				}
				stringBuffer.append(inputString.charAt(i));
			}
		}
		return stringBuffer.toString();
	}
}
