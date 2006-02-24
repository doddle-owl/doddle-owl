package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;

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
    private String jpWord;
    private String enWord;
    private String jpExplanation;
    private String enExplanation;

    public Concept() {
    }

    public Concept(Concept c) {
        id = c.getId();
        prefix = c.getPrefix();
        jpWord = c.getJpWord();
        enWord = c.getEnWord();
        jpExplanation = c.getJpExplanation();
        enExplanation = c.getEnExplanation();
        if (c.getInputWord() != null && !c.getInputWord().equals("")) {
            inputWord = c.getInputWord();
        }
    }

    public Concept(String id, String word) {
        this.id = id;
        enWord = "";
        jpWord = word;
        enExplanation = "";
        jpExplanation = "";
    }

    public Concept(String id, String[] items) {
        this.id = id;
        jpWord = removeNullWords(items[0]);
        enWord = removeNullWords(items[1]);
        jpExplanation = removeNullWords(items[2]);
        enExplanation = removeNullWords(items[3]);

        String[] jpWords = jpWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (0 < jpWords.length) {
            inputWord = jpWords[0];
        } else if (0 < enWords.length) {
            inputWord = enWords[0];
        }
    }

    public Concept(String[] items) {
        id = items[0];
        jpWord = removeNullWords(items[1]);
        enWord = removeNullWords(items[2]);
        jpExplanation = removeNullWords(items[3]);
        enExplanation = removeNullWords(items[4]);

        String[] jpWords = jpWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (0 < jpWords.length) {
            inputWord = jpWords[0];
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
        String[] jpWords = jpWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (0 < jpWords.length) {
            inputWord = jpWords[0];
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

    public String getEnWord() {
        return enWord;
    }

    public String[] getEnWords() {
        return enWord.split("\t");
    }

    public String getId() {
        return id;
    }

    public void setJpExplanation(String str) {
        jpExplanation = str;
    }

    public String getJpExplanation() {
        return jpExplanation;
    }

    public String getJpWord() {
        return jpWord;
    }

    public String[] getJpWords() {
        return jpWord.split("\t");
    }

    public void setJpWord(String word) {
        jpWord = word;
    }

    public String getWord() {
        String[] jpWords = jpWord.split("\t");
        String[] enWords = enWord.split("\t");
        if (inputWord != null && 0 < inputWord.length()) {
            return inputWord;
        } else if (0 < jpWord.length() && 0 < jpWords.length) {
            return jpWord.split("\t")[0];
        } else if (0 < enWord.length() && 0 < enWords.length) {
            return enWord.split("\t")[0];
        } else if (0 < jpExplanation.length()) {
            return jpExplanation;
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
