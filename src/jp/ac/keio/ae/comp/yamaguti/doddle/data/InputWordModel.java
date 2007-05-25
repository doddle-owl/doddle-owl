package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import net.java.sen.*;

/*
 * @(#)  2005/03/16
 *
 */

/**
 * @author takeshi morita
 */
public class InputWordModel implements Comparable {

    private int matchedPoint;
    private int ambiguousCnt;
    private String inputWord;
    private String matchedInputWord;
    private List<Token> tokenList;
    private String wordListStr;
    private boolean isSystemAdded;

    private DODDLEProject project;

    public InputWordModel(String w, List<Token> tList, String miw, int ac, int mp, DODDLEProject p) {
        project = p;
        inputWord = w;
        tokenList = tList;
        matchedInputWord = miw;
        StringBuffer buf = new StringBuffer("(");
        for (Iterator i = tokenList.iterator(); i.hasNext();) {
            Token token = (Token) i.next();
            String word = token.getBasicString();
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
    public boolean isPartiallyMatchWord() {
        // 1 < wordList.size()の条件を2006/10/5に追加
        // 「打合せ」が「打合す」と照合してしまうため
        return !inputWord.equals(matchedInputWord) && 1 < tokenList.size();
    }

    // 完全照合かどうか
    public boolean isPerfectlyMatchWord() {
        return !isPartiallyMatchWord();
    }

    public int compareTo(Object o) {
        InputWordModel oiwModel = (InputWordModel) o;
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

    public int getMatchedPoint() {
        return matchedPoint;
    }

    public int getAmbiguousCnt() {
        return ambiguousCnt;
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public String getTopBasicWord() {
        return tokenList.get(0).getBasicString();
    }

    public String getBasicWordWithoutTopWord() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < tokenList.size(); i++) {
            builder.append(tokenList.get(i).getBasicString());
        }
        return builder.toString();
    }

    public int getComplexWordLength() {
        return tokenList.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(inputWord);
        if (isPartiallyMatchWord() && project.isPartiallyMatchedComplexWordCheckBox()) {
            buf.append(" " + wordListStr);
        }
        if (isPartiallyMatchWord() && project.isPartiallyMatchedMatchedWordBox()) {
            buf.append(" (" + matchedInputWord + ") ");
        }
        if (isPartiallyMatchWord() && project.isPartiallyMatchedAmbiguityCntCheckBox()) {
            buf.append(" (" + ambiguousCnt + ")");
        }
        if (isPerfectlyMatchWord() && project.isPerfectlyMatchedAmbiguityCntCheckBox()) {
            buf.append(" (" + ambiguousCnt + ")");
        }
        if (isSystemAdded() && project.isPerfectlyMatchedSystemAddedWordCheckBox()) {
            buf.append(" (added)");
        }
        return buf.toString();
    }
}
