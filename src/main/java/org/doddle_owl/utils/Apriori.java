/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 * 
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
 * 
 * This file is part of DODDLE-OWL.
 * 
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.doddle_owl.utils;

import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.ConceptPair;
import org.doddle_owl.models.Document;
import org.doddle_owl.views.ConceptDefinitionPanel;

import java.io.IOException;
import java.util.*;

/**
 * @author Yoshihiro Shigeta
 * @author Takeshi Morita
 * 
 */
public class Apriori {

	private List<List<String>> lineList;

	private Set<List<Integer>> pairSet;
	private List<ConceptPair> allRelation;
	private Map<String, List<ConceptPair>> aprioriResult;
	private Map<List<Integer>, Integer> indexPairAppearence;
	private double minSupport;
	private double minConfidence;
	private List<String> inputWordList;

	private Document document;
	private ConceptDefinitionPanel conceptDefinitionPanel;

	public Apriori(ConceptDefinitionPanel cdp, Document doc) {
		document = doc;
		conceptDefinitionPanel = cdp;
		pairSet = new HashSet<>();
		aprioriResult = new HashMap<>();
		indexPairAppearence = new HashMap<>();
		allRelation = new ArrayList<>();
		inputWordList = conceptDefinitionPanel.getInputTermList();
		makeLineList();
	}

	public Document getDocument() {
		return document;
	}

	public void setParameters(double mins, double minc) {
		minSupport = mins;
		minConfidence = minc;
	}

	// 1行単位での形態素解析
	private List<String> getJaLineWordList(String line) {
		List<String> lineWordList = new ArrayList<>();
		try {
			StringTagger tagger = SenFactory.getStringTagger(null);
			List<Token> tokenList = new ArrayList<>();
			tagger.analyze(line, tokenList);
			for (Token token : tokenList) {
				String basicStr = token.getMorpheme().getBasicForm();
				if (basicStr.equals("*")) {
					basicStr = token.getSurface();
				}
				lineWordList.add(basicStr);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		Utils.addJaCompoundWord(lineWordList, inputWordList); // 複合語の追加

		return lineWordList;
	}

	// 1行単位での形態素解析
	private List<String> getEnLineWordList(String line) {
		line = line.replaceAll("\\.|．", "");
		List<String> lineWordList = new ArrayList<>();
		for (String lineWord : line.split("\\s+")) {
			lineWordList.add(lineWord.toLowerCase());
		}
		Utils.addEnCompoundWord(lineWordList, inputWordList); // 複合語の追加
		return lineWordList;
	}

	/**
	 * 入力概念がセットされた時に一度だけ実行され， 入力単語リストのリストを生成する
	 */
	private void makeLineList() {
		lineList = new ArrayList<>();
		// String corpusString = DocumentSelectionPanel.getTextString(document);
		String corpusString = document.getText();
		if (corpusString == null) {
			return;
		}
		String[] lines = corpusString.split("\n");
		for (String line : lines) {
			if (document.getLang().equals("en")) {
				lineList.add(getEnLineWordList(line));
			} else if (document.getLang().equals("ja")) {
				lineList.add(getJaLineWordList(line));
			}
		}
	}

	/**
	 * corpusStringは，あらかじめピリオドまたは丸で改行されているものとする
	 * 
	 */
	public Map<String, List<ConceptPair>> calcAprioriResult(List<String> targetInputWordList) {
		pairSet.clear();
		allRelation.clear();
		aprioriResult.clear();
		if (targetInputWordList == null) {
			return aprioriResult;
		}
		int[] conceptAppearence = new int[targetInputWordList.size()];

		List<Integer> itemList = new ArrayList<>();
		int lineNum = lineList.size();
		// System.out.println("line_num: " + lineNum);
		for (List<String> lineWordList : lineList) {
			for (String lineWord : lineWordList) {
				for (int k = 0; k < targetInputWordList.size(); k++) {
					String word = targetInputWordList.get(k);
					if (word.equals(lineWord)) {
						itemList.add(k);
						++conceptAppearence[k];
						break;
					}
				}
			}
			// System.out.println(itemList);
			culAprioriPair(itemList);
			itemList.clear();
		}
		DODDLE_OWL.STATUS_BAR.addProjectValue();
		makePair(conceptAppearence, lineNum, targetInputWordList);
		DODDLE_OWL.STATUS_BAR.addProjectValue();
		return aprioriResult;
	}

	private void culAprioriPair(List<Integer> itemList) {
		for (int i = 0; i < itemList.size(); i++) {
			for (int j = 0; j < itemList.size(); j++) {
				if (i != j) {
					List<Integer> pair = new ArrayList<>();
					pair.add(itemList.get(i));
					pair.add(itemList.get(j));
					pairSet.add(pair);
					if (indexPairAppearence.containsKey(pair)) {
						indexPairAppearence.put(pair, indexPairAppearence.get(pair) + 1);
					} else {
						indexPairAppearence.put(pair, 1);
					}
				}
			}
		}
	}

	public void setConfidence(double dou) {
		minConfidence = dou;
	}

	/**
	 * 
	 * conceptAppearance ...
	 * 全文の中にconceptData.getConcepts()リストの番号に対応する概念がいくつ出現したかを保存
	 * concept[A|B]Support ... ある概念の出現回数/全文の数(いくつの文に含まれていたか．一回でも出現すればよい）
	 * pairAppearance ... 全文の中である概念対が出現する回数
	 * 
	 * @param conceptAppearence
	 * @param lineNum
	 */
	private void makePair(int[] conceptAppearence, double lineNum, List<String> inputWordList) {
		List<ConceptPair> rpList;
		for (List<Integer> pair : pairSet) {
			int conceptAIndex = pair.get(0);
			String word1 = inputWordList.get(conceptAIndex);
			int conceptBIndex = pair.get(1);
			String word2 = inputWordList.get(conceptBIndex);
			// System.out.println(conceptAIndex + ":" + conceptBIndex);
			double conceptASupport = conceptAppearence[conceptAIndex] / lineNum;
			double conceptBSupport = conceptAppearence[conceptBIndex] / lineNum;

			// System.out.println(conceptAppearence[conceptAIndex]+"/"+lines+
			// "=" +conceptASupport);
			// System.out.println(conceptAppearence[conceptBIndex]+"/"+lines+
			// "=" +conceptBSupport);

			double aprioriValue = 0;
			if (conceptASupport >= minSupport && conceptBSupport >= minSupport) {
				int pairAppearence = indexPairAppearence.get(pair);
				aprioriValue = (double) pairAppearence / (double) conceptAppearence[conceptAIndex];
			}

			if (aprioriValue > minConfidence) {
				ConceptPair rp = new ConceptPair(word1, word2, aprioriValue);
				// rp.setrelationValue(value);
				// System.out.println(conceptA + "<>" + rp.toString());
				// System.out.println("Apriori:" + rp.toString());
				allRelation.add(rp);

				if (aprioriResult.containsKey(word1)) {
					rpList = aprioriResult.get(word1);
					rpList.add(rp);
					aprioriResult.put(word1, rpList);
				} else {
					rpList = new ArrayList<>();
					rpList.add(rp);
					aprioriResult.put(word1, rpList);
				}
			}
		}
	}

	public Map<String, List<ConceptPair>> getAprioriResult() {
		return aprioriResult;
	}

	public List<ConceptPair> getAllRelation() {
		return allRelation;
	}
}