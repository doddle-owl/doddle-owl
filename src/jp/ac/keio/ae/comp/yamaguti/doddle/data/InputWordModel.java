package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;

/*
 * @(#)  2005/03/16
 *
 */

/**
 * @author takeshi morita
 */
public class InputWordModel implements Comparable {

    private int ambiguousCnt;
    private String inputWord;
    private String matchedInputWord;
    private List wordList;
    private String wordListStr;
    private boolean isPerfectMatch;

    private DODDLEProject project;

    public InputWordModel(String w, List wl, String miw, boolean im, int ac, DODDLEProject p) {
        project = p;
        inputWord = w;
        wordList = wl;
        matchedInputWord = miw;
        StringBuffer buf = new StringBuffer("(");
        for (Iterator i = wordList.iterator(); i.hasNext();) {
            String word = (String) i.next();
            if (i.hasNext()) {
                buf.append(word + "+");
            } else {
                buf.append(word + ")");
            }
        }
        wordListStr = buf.toString();
        isPerfectMatch = im;
        ambiguousCnt = ac;
    }

    // ïîï™è∆çáÇ≈É}ÉbÉ`Ç∑ÇÈÇ©Ç«Ç§Ç©
    public boolean isPartialMatchWord() {
        return !inputWord.equals(matchedInputWord);
    }

    public int compareTo(Object o) {
        InputWordModel oiwModel = (InputWordModel)o;
        int onum = oiwModel.getAmbiguousCnt();
        String oword = oiwModel.getWord();
        if (this.ambiguousCnt < onum) {
            return 1;
        } else if (this.ambiguousCnt > onum) {
            return -1;
        } else {
            return oword.compareTo(inputWord);
        }
    }

    public String getWord() {
        return inputWord;
    }

    public String getMatchedWord() {
        return matchedInputWord;
    }

    public int getAmbiguousCnt() {
        return ambiguousCnt;
    }

    public List getWordList() {
        return wordList;
    }

    public String getTopWord() {
        return (String) wordList.get(0);
    }

    public String getWordWithoutTopWord() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < wordList.size(); i++) {
            builder.append(wordList.get(i));
        }
        return builder.toString();
    }

    public int getComplexWordLength() {
        return wordList.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(inputWord);
        if (isPartialMatchWord() && project.isPartialMatchedComplexWordCheckBox()) {
            buf.append(" " + wordListStr);
        }
        if (isPartialMatchWord() && project.isPartialMatchedMatchedWordBox()) {
            buf.append(" (" + matchedInputWord + ") ");
        }
        if (isPartialMatchWord() && project.isPartialMatchedAmbiguityCntCheckBox()) {
            buf.append(" (" + ambiguousCnt + ")");
        }
        if (!isPartialMatchWord() && project.isPerfectMatchedAmbiguityCntCheckBox()) {
            buf.append(" (" + ambiguousCnt + ")");
        }
        return buf.toString();
    }
}
