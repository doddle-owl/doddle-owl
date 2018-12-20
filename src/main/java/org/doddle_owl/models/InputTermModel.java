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

package org.doddle_owl.models;

import org.doddle_owl.DODDLEProject;
import org.doddle_owl.task_analyzer.Morpheme;
import org.doddle_owl.utils.Translator;

import java.util.Iterator;
import java.util.List;

/**
 * @author Takeshi Morita
 */
public class InputTermModel implements Comparable {

	private int matchedPoint;
	private int ambiguousCnt;
	private String inputWord;
	private String matchedInputWord;
	private List<Morpheme> morphemeList;
	private String wordListStr;
	private boolean isSystemAdded;

	private DODDLEProject project;

	public InputTermModel(String w, List<Morpheme> mList, String miw, int ac, int mp,
			DODDLEProject p) {
		project = p;
		inputWord = w;
		morphemeList = mList;
		matchedInputWord = miw;
		StringBuffer buf = new StringBuffer("(");
		for (Iterator i = morphemeList.iterator(); i.hasNext();) {
			Morpheme m = (Morpheme) i.next();
			String word = m.getBasic();
			if (i.hasNext()) {
				buf.append(word + "+");
			} else {
				buf.append(word + ")");
			}
		}
		wordListStr = buf.toString();
		ambiguousCnt = ac;
		matchedPoint = mp;
	}

	public void setIsSystemAdded(boolean t) {
		isSystemAdded = t;
	}

	public boolean isSystemAdded() {
		return isSystemAdded;
	}

	// 部分照合かどうか
	public boolean isPartiallyMatchTerm() {
		// 1 < wordList.size()の条件を2006/10/5に追加
		// 「打合せ」が「打合す」と照合してしまうため
		return !inputWord.equals(matchedInputWord) && 1 < morphemeList.size();
	}

	// 完全照合かどうか
	public boolean isPerfectlyMatchWord() {
		return !isPartiallyMatchTerm();
	}

	public int compareTo(Object o) {
		InputTermModel oiwModel = (InputTermModel) o;
		int onum = oiwModel.getAmbiguousCnt();
		String oword = oiwModel.getTerm();
		if (this.ambiguousCnt < onum) {
			return 1;
		} else if (this.ambiguousCnt > onum) {
			return -1;
		} else {
			return oword.compareTo(inputWord);
		}
	}

	public String getTerm() {
		return inputWord;
	}

	public String getMatchedTerm() {
		return matchedInputWord;
	}

	public int getMatchedPoint() {
		return matchedPoint;
	}

	public int getAmbiguousCnt() {
		return ambiguousCnt;
	}

	public List<Morpheme> getMorphemeList() {
		return morphemeList;
	}

	public String getTopBasicWord() {
		return morphemeList.get(0).getBasic();
	}

	public String getBasicWordWithoutTopWord() {
		StringBuilder builder = new StringBuilder();
		for (Morpheme m : morphemeList) {
			builder.append(m.getBasic());
		}
		return builder.toString();
	}

	public int getCompoundWordLength() {
		return morphemeList.size();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(inputWord);
		if (isPartiallyMatchTerm() && project.isPartiallyMatchedCompoundWordCheckBox()) {
			buf.append(" " + wordListStr);
		}
		if (isPartiallyMatchTerm() && project.isPartiallyMatchedMatchedWordBox()) {
			buf.append(" (" + matchedInputWord + ") ");
		}
		if (isPartiallyMatchTerm() && project.isPartiallyMatchedAmbiguityCntCheckBox()) {
			buf.append(" (" + ambiguousCnt + ")");
		}
		if (isPerfectlyMatchWord() && project.isPerfectlyMatchedAmbiguityCntCheckBox()) {
			buf.append(" (" + ambiguousCnt + ")");
		}
		if (isSystemAdded() && project.isPerfectlyMatchedSystemAddedWordCheckBox()) {
			buf.append(" (" + Translator.getTerm("SystemAddedLabel") + ")");
		}
		return buf.toString();
	}
}
