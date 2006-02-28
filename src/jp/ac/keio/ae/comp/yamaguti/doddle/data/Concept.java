package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

/** 
 *  このクラスを編集した場合，DBを作成しなおす必要あり
 */

/**
 * @author takeshi morita
 */
public class Concept implements Serializable {

    private String id;
    private String prefix;
    private String inputWord;
    private String jaWord;
    private String enWord;
    private String jaExplanation;
    private String enExplanation;

    public Concept() {
    }

    public Concept(Concept c) {
        id = c.getId();
        prefix = c.getPrefix();
        jaWord = c.getJaWord();
        enWord = c.getEnWord();
        jaExplanation = c.getJaExplanation();
        enExplanation = c.getEnExplanation();
        if (c.getInputWord() != null && !c.getInputWord().equals("")) {
            inputWord = c.getInputWord();
        }
    }

    public Concept(String id, String word) {
        this.id = id;
        enWord = "";
        jaWord = word;
        enExplanation = "";
        jaExplanation = "";
    }

    public Concept(String id, String[] items) {
        this.id = id;
        jaWord = removeNullWords(items[0]);
        enWord = removeNullWords(items[1]);
        jaExplanation = removeNullWords(items[2]);
        enExplanation = removeNullWords(items[3]);

        String[] jaWords = jaWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (0 < jaWords.length) {
            inputWord = jaWords[0];
        } else if (0 < enWords.length) {
            inputWord = enWords[0];
        }
    }

    public Concept(String[] items) {
        id = items[0];
        jaWord = removeNullWords(items[1]);
        enWord = removeNullWords(items[2]);
        jaExplanation = removeNullWords(items[3]);
        enExplanation = removeNullWords(items[4]);

        String[] jaWords = jaWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (0 < jaWords.length) {
            inputWord = jaWords[0];
        } else if (0 < enWords.length) {
            inputWord = enWords[0];
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String pfx) {
        prefix = pfx;
    }

    private String removeNullWords(String str) {
        return str.replaceAll("\\*\\*\\*", "");
    }

    public void setInputWord() {
        String[] jaWords = jaWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (0 < jaWords.length) {
            inputWord = jaWords[0];
        } else if (0 < enWords.length) {
            inputWord = enWords[0];
        } else {
            inputWord = null;
        }
    }

    public void setInputWord(String iw) {
        inputWord = iw;
    }

    public String getInputWord() {
        return inputWord;
    }

    public String getEnExplanation() {
        return enExplanation;
    }

    public void setEnExplanation(String str) {
        enExplanation = str;
    }

    public void setEnWord(String word) {
        enWord = word;
    }

    public void addEnWord(String word) {
        if (!Arrays.asList(enWord.split("\t")).contains(word)) {
            enWord += word + "\t";
        }
    }

    public String getEnWord() {
        return enWord;
    }

    public String[] getEnWords() {
        return enWord.split("\t");
    }

    public String getId() {
        return id;
    }

    public void setJaExplanation(String str) {
        jaExplanation = str;
    }

    public String getJaExplanation() {
        return jaExplanation;
    }

    public String getJaWord() {
        return jaWord;
    }

    public String[] getJaWords() {
        return jaWord.split("\t");
    }

    public void setJaWord(String word) {
        jaWord = word;
    }

    public void addJaWord(String word) {
        if (!Arrays.asList(jaWord.split("\t")).contains(word)) {
            jaWord += word + "\t";
        }
    }

    public String getWord() {
        String[] jaWords = jaWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (inputWord != null && 0 < inputWord.length()) {
            return inputWord;
        } else if (0 < jaWord.length() && 0 < jaWords.length) {
            return jaWord.split("\t")[0];
        } else if (0 < enWord.length() && 0 < enWords.length) {
            return enWord.split("\t")[0];
        } else if (0 < jaExplanation.length()) {
            return jaExplanation;
        } else if (0 < enExplanation.length()) {
            return enExplanation;
        } else {
            return id;
        }
    }

    public String getIdentity() {
        return prefix + ":" + id;
    }

    public String toString() {
        return getWord() + "[" + getIdentity() + "]";
    }

    public boolean equals(Object c) {
        return getIdentity().equals(((Concept) c).getIdentity());
    }
}
