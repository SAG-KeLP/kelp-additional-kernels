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
import java.util.List;

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner.PruneNodeIfLeaf;
import it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner.PruneNodeLeafNumber;
import it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner.PruneNodeLowerThanThreshold;
import it.uniroma2.sag.kelp.data.representation.tree.node.nodePruner.PruneNodeNumberOfChildren;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectRepresentationFromExample;
import it.uniroma2.sag.kelp.data.representation.tree.utils.SelectRepresentationFromExample.representationSelectorInExample;
import it.uniroma2.sag.kelp.data.representation.tree.utils.TreeNodeSelectorAllChildren;
import it.uniroma2.sag.kelp.data.representation.tree.utils.TreeNodeSelectorAllLeaves;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Giovanni Da San Martino
 *
 */
public class TreePrunerEvaluator {
		
	@Test
	public void testMaxSentenceLengthPruning() {
		//String treepair = "Relevant |<| |BT:tree| (ROOT (S (NP (NNP (good::n))(NNP (bank::n))))(S (NP (WDT (which::w)))(VP (VBZ (be::v)))(NP (DT (a::d))(JJ (good::j))(NN (bank::n)))(PP (IN (as::i)))(PP (IN (per::i)))(NP (PRP$ (your::p))(NN (experience::n)))(PP (IN (in::i)))(NP (NNP (doha::n))))) |ET| |,| |BT:tree| (ROOT (S (NP (JJS (best::j))(NNP (bank::n))))(S (NP (UH (hus::u))(NNP (guy::n)))(NP (PRP (i::p)))(VP (VBP (need::v))(TO (to::t))(VB (open::v)))(NP (DT (a::d))(JJ (new::j))(NN (bank::n))(NN (accoount::n))))(S (NP (WDT (which::w)))(VP (VBZ (be::v)))(NP (DT (the::d))(JJS (best::j))(NN (bank::n)))(PP (IN (in::i)))(NP (NNP (qatar::n))))(S (NP (PRP (i::p)))(VP (VBP (assume::v)))(NP (DT (all::d)))(PP (IN (of::i)))(NP (PRP (them::p)))(VP (MD (will::m))(RB (roughly::r))(VB (be::v)))(NP (DT (the::d))(JJ (same::j)))(NP (NN (stll::n)))(NP (WDT (which::w)))(VP (VBZ (have::v)))(NP (DT (a::d))(JJ (slight::j))(NN (edge::n))(-LRB- ({::-))(NN (money::n))(NN (transfer::n)))(NP (JJ (benifit::j))(NN (etc::n))(-RRB- (}::-))(NNS (thanks::n))))) |ET| |>|";
		String tree1String = "(NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##good::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##as::i)))(NOTYPE##PP(NOTYPE##IN(LEX##per::i)))(NOTYPE##NP(NOTYPE##PRP$(LEX##your::p))(NOTYPE##NN(LEX##experience::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##doha::n)))))";
		String tree2String = "(NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##JJS(LEX##best::j))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##UH(LEX##hus::u))(NOTYPE##NNP(LEX##guy::n)))(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##need::v))(NOTYPE##TO(LEX##to::t))(NOTYPE##VB(LEX##open::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##new::j))(NOTYPE##NN(LEX##bank::n))(NOTYPE##NN(LEX##accoount::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJS(LEX##best::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##qatar::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##assume::v)))(NOTYPE##NP(NOTYPE##DT(LEX##all::d)))(NOTYPE##PP(NOTYPE##IN(LEX##of::i)))(NOTYPE##NP(NOTYPE##PRP(LEX##them::p)))(NOTYPE##VP(NOTYPE##MD(LEX##will::m))(NOTYPE##RB(LEX##roughly::r))(NOTYPE##VB(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJ(LEX##same::j)))(NOTYPE##NP(NOTYPE##NN(LEX##stll::n)))(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##have::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##slight::j))(NOTYPE##NN(LEX##edge::n))(NOTYPE##-LRB-(LEX##{::-))(NOTYPE##NN(LEX##money::n))(NOTYPE##NN(LEX##transfer::n)))(NOTYPE##NP(NOTYPE##JJ(LEX##benifit::j))(NOTYPE##NN(LEX##etc::n))(NOTYPE##-RRB-(LEX##}::-))(NOTYPE##NNS(LEX##thanks::n)))))";
		String exampleString = "Relevant |<||BT:tree|" + tree1String + " |ET| |,||BT:tree| " + tree2String + "|ET| |>|";
		SimpleDataset dataset;
		SelectRepresentationFromExample treeSelector;
		SelectRepresentationFromExample treeSelector2;
		String treepairAfter;
		
		//TEST 1
		System.out.println("\n\n------\nEnsuring that all sentences in the parse tree have no more than 8 words");
		dataset = getDatasetFromString(exampleString);
		treeSelector = new SelectRepresentationFromExample("tree", representationSelectorInExample.LEFT);
		pruningMaxSentenceLength(dataset, 8, treeSelector);
		treepairAfter = "Relevant |<||BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##good::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##as::i)))(NOTYPE##PP(NOTYPE##IN(LEX##per::i)))(NOTYPE##NP(NOTYPE##PRP$(LEX##your::p)))))|ET| |,||BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##JJS(LEX##best::j))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##UH(LEX##hus::u))(NOTYPE##NNP(LEX##guy::n)))(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##need::v))(NOTYPE##TO(LEX##to::t))(NOTYPE##VB(LEX##open::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##new::j))(NOTYPE##NN(LEX##bank::n))(NOTYPE##NN(LEX##accoount::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJS(LEX##best::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##qatar::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##assume::v)))(NOTYPE##NP(NOTYPE##DT(LEX##all::d)))(NOTYPE##PP(NOTYPE##IN(LEX##of::i)))(NOTYPE##NP(NOTYPE##PRP(LEX##them::p)))(NOTYPE##VP(NOTYPE##MD(LEX##will::m))(NOTYPE##RB(LEX##roughly::r))(NOTYPE##VB(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJ(LEX##same::j)))(NOTYPE##NP(NOTYPE##NN(LEX##stll::n)))(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##have::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##slight::j))(NOTYPE##NN(LEX##edge::n))(NOTYPE##-LRB-(LEX##{::-))(NOTYPE##NN(LEX##money::n))(NOTYPE##NN(LEX##transfer::n)))(NOTYPE##NP(NOTYPE##JJ(LEX##benifit::j))(NOTYPE##NN(LEX##etc::n))(NOTYPE##-RRB-(LEX##}::-))(NOTYPE##NNS(LEX##thanks::n)))))|ET| |>|";
		System.out.println("Pruned sentence:" + getStringFromDataset(dataset));
		System.out.println("Expected result:" + treepairAfter);
		Assert.assertEquals(getStringFromDataset(dataset), treepairAfter);

		//TEST 2
		dataset = getDatasetFromString(exampleString);
		treeSelector = new SelectRepresentationFromExample("tree", representationSelectorInExample.RIGHT);
		pruningMaxSentenceLength(dataset, 7, treeSelector);
		treepairAfter = "Relevant |<||BT:tree| " + tree1String + "|ET| |,||BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##JJS(LEX##best::j))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##UH(LEX##hus::u))(NOTYPE##NNP(LEX##guy::n)))(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##need::v))(NOTYPE##TO(LEX##to::t))(NOTYPE##VB(LEX##open::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJS(LEX##best::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##qatar::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##assume::v)))(NOTYPE##NP(NOTYPE##DT(LEX##all::d)))(NOTYPE##PP(NOTYPE##IN(LEX##of::i)))(NOTYPE##NP(NOTYPE##PRP(LEX##them::p)))(NOTYPE##VP(NOTYPE##MD(LEX##will::m))(NOTYPE##RB(LEX##roughly::r)))))|ET| |>|";
//		treepairAfter = "Relevant |<||BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##good::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##as::i)))(NOTYPE##PP(NOTYPE##IN(LEX##per::i)))(NOTYPE##NP(NOTYPE##PRP$(LEX##your::p)))))|ET| |,||BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##JJS(LEX##best::j))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##UH(LEX##hus::u))(NOTYPE##NNP(LEX##guy::n)))(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##need::v))(NOTYPE##TO(LEX##to::t))(NOTYPE##VB(LEX##open::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##new::j))(NOTYPE##NN(LEX##bank::n))(NOTYPE##NN(LEX##accoount::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJS(LEX##best::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##qatar::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##PRP(LEX##i::p)))(NOTYPE##VP(NOTYPE##VBP(LEX##assume::v)))(NOTYPE##NP(NOTYPE##DT(LEX##all::d)))(NOTYPE##PP(NOTYPE##IN(LEX##of::i)))(NOTYPE##NP(NOTYPE##PRP(LEX##them::p)))(NOTYPE##VP(NOTYPE##MD(LEX##will::m))(NOTYPE##RB(LEX##roughly::r))(NOTYPE##VB(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##the::d))(NOTYPE##JJ(LEX##same::j)))(NOTYPE##NP(NOTYPE##NN(LEX##stll::n)))(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##have::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##slight::j))(NOTYPE##NN(LEX##edge::n))(NOTYPE##-LRB-(LEX##{::-))(NOTYPE##NN(LEX##money::n))(NOTYPE##NN(LEX##transfer::n)))(NOTYPE##NP(NOTYPE##JJ(LEX##benifit::j))(NOTYPE##NN(LEX##etc::n))(NOTYPE##-RRB-(LEX##}::-))(NOTYPE##NNS(LEX##thanks::n)))))|ET| |>|";
		System.out.println("Pruned sentence:" + getStringFromDataset(dataset));
		System.out.println("Expected result:" + treepairAfter);
		Assert.assertEquals(getStringFromDataset(dataset), treepairAfter);		

		//TEST 3
		exampleString = "Relevant |BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##good::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##as::i)))(NOTYPE##PP(NOTYPE##IN(LEX##per::i)))(NOTYPE##NP(NOTYPE##PRP$(LEX##your::p))(NOTYPE##NN(LEX##experience::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##doha::n)))))|ET|";
		dataset = getDatasetFromString(exampleString);
		treeSelector2 = new SelectRepresentationFromExample("tree");
		pruningMaxSentenceLength(dataset, 1, treeSelector2);
		treepairAfter = "Relevant |BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))))|ET| ";
		System.out.println("Pruned sentence:" + getStringFromDataset(dataset));
		System.out.println("Expected result:" + treepairAfter);
		Assert.assertEquals(getStringFromDataset(dataset), treepairAfter);
		
	}
	
	@Test
	public void testMaxNumberOfSentencesPruning() {
		SimpleDataset dataset;
		String exampleString = "Relevant |BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n))))(NOTYPE##S(NOTYPE##NP(NOTYPE##WDT(LEX##which::w)))(NOTYPE##VP(NOTYPE##VBZ(LEX##be::v)))(NOTYPE##NP(NOTYPE##DT(LEX##a::d))(NOTYPE##JJ(LEX##good::j))(NOTYPE##NN(LEX##bank::n)))(NOTYPE##PP(NOTYPE##IN(LEX##as::i)))(NOTYPE##PP(NOTYPE##IN(LEX##per::i)))(NOTYPE##NP(NOTYPE##PRP$(LEX##your::p))(NOTYPE##NN(LEX##experience::n)))(NOTYPE##PP(NOTYPE##IN(LEX##in::i)))(NOTYPE##NP(NOTYPE##NNP(LEX##doha::n)))))|ET|";
		dataset = getDatasetFromString(exampleString);
		PruneNodeNumberOfChildren childrenPruner = new PruneNodeNumberOfChildren(1);
		TreeNodePruner treePruner = new TreeNodePruner(childrenPruner, "tree", null, 0);
		System.out.println("Removing all sentences but the first one");
		System.out.println(treePruner.describe());
		dataset.manipulate(treePruner);
		String treepairAfter = "Relevant |BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n)))))|ET| ";
		System.out.println("Pruned sentence:" + getStringFromDataset(dataset));
		System.out.println("Expected result:" + treepairAfter);
		Assert.assertEquals(getStringFromDataset(dataset), treepairAfter);
	}		
	
	@Test
	public void testPruningThreshold() {
		SimpleDataset dataset;
		String exampleString = "Relevant |BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP(LEX##good::n))(NOTYPE##NNP(LEX##bank::n)))))|ET| ";
		dataset = getDatasetFromString(exampleString);
		List<List<Double>> info = new ArrayList<List<Double>>();
		ArrayList<Double> weights = new ArrayList<Double>();
		weights.add(0.1);weights.add(0.4); 
		info.add(weights);
		TreeAddAdditionalInfoFromArray weigthLoader = 
				new TreeAddAdditionalInfoFromArray(new TreeNodeSelectorAllLeaves(), info, "weight", "tree");
		PruneNodeLowerThanThreshold nodePrunerChecker = 
				new PruneNodeLowerThanThreshold(0.3, "weight", true, 1.0);
		TreeNodePruner treePruner = new TreeNodePruner(nodePrunerChecker, "tree");
		System.out.println("Removing all nodes whose value of the field -weight- is lower than 0.3");
		System.out.println(weigthLoader.describe());
		System.out.println(treePruner.describe());
		dataset.manipulate(weigthLoader);
		dataset.manipulate(treePruner);
		String treepairAfter = "Relevant |BT:tree| (NOTYPE##ROOT(NOTYPE##S(NOTYPE##NP(NOTYPE##NNP)(NOTYPE##NNP(LEX##bank::n)))))|ET| ";
		System.out.println("Pruned sentence:" + getStringFromDataset(dataset));
		System.out.println("Expected result:" + treepairAfter);
		Assert.assertEquals(getStringFromDataset(dataset), treepairAfter);
	}		
	
	
	public static SimpleDataset getDatasetFromString(String exampleString) {
		SimpleDataset dataset = new SimpleDataset();
		Example ex = new SimpleExample();
		try {
			ex = ExampleFactory.parseExample(exampleString);
		} catch (Exception e) {
			try {
				throw new ParsingExampleException(e, exampleString);
			} catch (ParsingExampleException e1) {
				System.out.println("ERROR");
				e1.printStackTrace();
			}
		}
		dataset.addExample(ex);
		return dataset;
	}
	
	public static String getStringFromDataset(SimpleDataset dataset) {
		return dataset.getExamples().get(0).toString();
	}
		
	public static void pruningMaxSentenceLength(SimpleDataset dataset, 
			int maxNumberOfLeavesPerSubtree, SelectRepresentationFromExample treeSelector) {
		PruneNodeLeafNumber sentenceLengthPruner = 
				new PruneNodeLeafNumber(maxNumberOfLeavesPerSubtree);
		PruneNodeIfLeaf internalNodePruner = new PruneNodeIfLeaf();
		TreeNodeSelectorAllChildren sentenceRoots = new TreeNodeSelectorAllChildren();
		TreeNodePruner sentencePrunerClass = new TreeNodePruner(sentenceLengthPruner, 
				treeSelector, internalNodePruner, sentenceRoots, TreeNodePruner.UNLIMITED_RECURSION);
		System.out.println(sentencePrunerClass.describe());
		dataset.manipulate(sentencePrunerClass);
	}
	
	public static void testMaxNumberOfSentences(SimpleDataset dataset, int maxNumberOfSentences, 
			SelectRepresentationFromExample treeSelector) {
		PruneNodeLeafNumber sentenceLengthPruner = 
				new PruneNodeLeafNumber(maxNumberOfSentences);
		PruneNodeIfLeaf internalNodePruner = new PruneNodeIfLeaf();
		TreeNodeSelectorAllChildren sentenceRoots = new TreeNodeSelectorAllChildren(); 
		TreeNodePruner sentencePrunerClass = new TreeNodePruner(sentenceLengthPruner,
				 treeSelector, internalNodePruner, sentenceRoots, 1);
		dataset.manipulate(sentencePrunerClass);
	}

}
