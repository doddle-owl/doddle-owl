/*
 * Created on 2003/10/29
 *  
 */
package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import net.java.sen.*;

/**
 * @author 2006-01-12: takeshi morita
 * 
 */
public class WordSpace {

    private List corpusTokenList;

    private Map gramNumMap;
    private Map wordSpaceResult;

    private WordSpaceData wsData;
    private List allConceptPairs;

    private List inputWordList;
    private Document document;
    private TextConceptDefinitionPanel conceptDefinitionPanel;

    public WordSpace(TextConceptDefinitionPanel tcdp, Document doc) {
        document = doc;
        conceptDefinitionPanel = tcdp;
        inputWordList = conceptDefinitionPanel.getInputWordList();

        gramNumMap = new HashMap();
        wordSpaceResult = new HashMap();
        allConceptPairs = new ArrayList();
        makeTokenList(doc);
    }

    public Document getDocument() {
        return document;
    }

    public void setWSData(WordSpaceData d) {
        wsData = d;
    }

    private void makeTokenList(Document doc) {
        if (doc.getLang().equals("ja")) {
            makeJaTokenList(DocumentSelectionPanel.getTextString(doc));
        } else if (doc.getLang().equals("en")) {
            makeEnTokenList(DocumentSelectionPanel.getTextString(doc));
        }
    }

    private void makeEnTokenList(String text) {
        if (text == null) { return; }
        corpusTokenList = Arrays.asList(text.split("\\s+"));

        // 以下，複合語の追加
        Set<String> complexWordSet = conceptDefinitionPanel.getComplexWordSet();
        Utils.addEnComplexWord(corpusTokenList, complexWordSet);
    }

    private void makeJaTokenList(String text) {
        corpusTokenList = new ArrayList();
        if (text == null) { return; }
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] token = tagger.analyze(text);
            if (token == null) { return; }
            for (int i = 0; i < token.length; i++) {
                String basicStr = token[i].getBasicString();
                corpusTokenList.add(basicStr);
                tagger.next();
            }

            // 以下，複合語の追加
            Set<String> complexWordSet = conceptDefinitionPanel.getComplexWordSet();
            Utils.addJaComplexWord(corpusTokenList, complexWordSet);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public Map calcWordSpaceResult() {
        allConceptPairs.clear();
        if (corpusTokenList.size() == 0) { return null; }

        if (0 < wsData.getGramNumber()) {
            for (int i = 1; i <= wsData.getGramNumber(); i++) {
                setGram(i, corpusTokenList);
            }
        }
        // System.out.println("all gram num" + gramNumMap.size());
        List gramText = makeGramText(corpusTokenList);
        // System.out.println("gram txt: " + gramText);
        int allGramNum = gramNumMap.size();
        gramNumMap.clear();
        int[][] matrix = getGramMatrix(allGramNum, gramText);
        wordSpaceResult = getWordSpaceResult(matrix, allGramNum);
        // System.out.println("result: " + wordSpaceResult);
        return wordSpaceResult;
    }

    public Map getWordSpaceResult() {
        return wordSpaceResult;
    }

    private int[][] getGramMatrix(int allGramNum, List gramText) {
        // System.out.println("All Gram Num: " + allGramNum);// +++
        // System.out.println("Gram Text Size: " + gramText.size());

        int new_gram_number = 1;
        int matrix[][] = new int[allGramNum][allGramNum];
        gramNumMap.put(gramText.get(0), new Integer(0));

        for (int i = 0; i < gramText.size(); i++) {
            int row = ((Integer) gramNumMap.get(gramText.get(i))).intValue();
            for (int j = 1; j <= wsData.getFrontScope(); j++) {
                int place = i - j;
                if (place < 0) {
                    break;
                }
                int col = ((Integer) gramNumMap.get(gramText.get(place))).intValue();
                matrix[row][col]++;
            }

            for (int j = 1; j <= wsData.getBehindScope(); j++) {
                int place = i + j;
                if (place >= gramText.size()) {
                    break;
                }
                int col;
                String gram = (String) gramText.get(place);
                if (gramNumMap.containsKey(gram)) {
                    col = ((Integer) gramNumMap.get(gram)).intValue();
                } else {
                    gramNumMap.put(gram, new Integer(new_gram_number));
                    col = new_gram_number;
                    new_gram_number++;
                }
                matrix[row][col]++;
            }
        }
        return matrix;
    }

    private Map getWordSpaceResult(int matrix[][], int allGramNum) {
        Map wordPairMap = new HashMap();

        for (int i = 0; i < inputWordList.size(); i++) {
            String w1 = (String) inputWordList.get(i);
            List pairList = new ArrayList();
            for (int j = 0; j < inputWordList.size(); j++) {
                String w2 = (String) inputWordList.get(j);
                if (i != j) {
                    Concept c1 = conceptDefinitionPanel.getConcept(w1);
                    Concept c2 = conceptDefinitionPanel.getConcept(w2);
                    Double similarity = getSimilarityValue(document.getLang(), c1, c2, matrix, allGramNum);
                    // System.out.println(c1 + "=>" + c2 + " = " + similarity);

                    if (wsData.getUnderValue() < similarity.doubleValue()) {
                        ConceptPair pair = new ConceptPair(w1, w2, similarity);
                        allConceptPairs.add(pair);
                        pairList.add(pair);
                    }
                }
            }
            wordPairMap.put(inputWordList.get(i), pairList);
            // if(hash_parent.containsKey(concept.get(i))) {
            // System.out.println("success" + concept.get(i));
            // }
        }

        // System.out.print("----" + hash_parent + "----");
        return wordPairMap;
    }

    private void setVec(int[] vec, List cLabelList, int[][] matrix, int allGramNum) {
        for (int i = 0; i < cLabelList.size(); i++) {
            String w1 = (String) cLabelList.get(i);
            // System.out.println("----" + w1);
            if (gramNumMap.containsKey(w1)) {
                for (int j = 0; j < allGramNum; j++) {
                    vec[j] += matrix[((Integer) gramNumMap.get(w1)).intValue()][j];
                }
            }
        }
    }

    private Double getSimilarityValue(String lang, Concept c1, Concept c2, int matrix[][], int allGramNum) {
        List c1LabelList = null;
        List c2LabelList = null;
        if (lang.equals("en")) {
            c1LabelList = Arrays.asList(c1.getEnWords());
            c2LabelList = Arrays.asList(c2.getEnWords());
        } else if (lang.equals("ja")) {
            c1LabelList = Arrays.asList(c1.getJaWords());
            c2LabelList = Arrays.asList(c2.getJaWords());
        }

        int[] vec1 = new int[allGramNum];
        int[] vec2 = new int[allGramNum];

        // System.out.println("C1::" + c1LabelList);
        // System.out.println("C2::" + c2LabelList);
        for (int i = 0; i < allGramNum; i++) {
            vec1[i] = 0;
            vec2[i] = 0;
        }

        setVec(vec1, c1LabelList, matrix, allGramNum);
        setVec(vec2, c2LabelList, matrix, allGramNum);

        double absVec1 = 0;
        double absVec2 = 0;
        double innerProduct = 0;
        for (int i = 0; i < allGramNum; i++) {
            // System.out.println(concept1[i] + "--" + concept2[i]);
            innerProduct += vec1[i] * vec2[i];
            absVec1 += Math.pow(vec1[i], 2);
            absVec2 += Math.pow(vec2[i], 2);
        }
        absVec1 = StrictMath.sqrt(absVec1);
        absVec2 = StrictMath.sqrt(absVec2);
        return new Double(innerProduct / (absVec1 * absVec2));
    }

    /*
     * tokenListは，入力文書の形態素の配列
     */
    private void setGram(int gramNum, List tokenList) {
        for (Iterator i = tokenList.iterator(); i.hasNext();) {
            StringBuffer gramBuf = new StringBuffer("");
            for (int j = 0; j < gramNum; j++) {
                if (i.hasNext()) {
                    gramBuf.append((String) i.next());
                    gramBuf.append("_");
                }
            }
            String gram = gramBuf.substring(0, gramBuf.length() - 1);
            if (gramNumMap.containsKey(gram)) {
                Integer num = (Integer) gramNumMap.get(gram);
                gramNumMap.put(gram, new Integer(num.intValue() + 1));
            } else {
                gramNumMap.put(gram, new Integer(1));
            }
        }
    }

    public List makeGramText(List tokenList) {
        List gramText = new ArrayList();
        for (Iterator i = tokenList.iterator(); i.hasNext();) {
            List gramList = new ArrayList();
            for (int j = 0; j < wsData.getGramNumber(); j++) {
                if (i.hasNext()) {
                    gramList.add(i.next());
                }
            }
            addUsableGramToList(gramList, gramText);
        }
        return gramText;
    }

    private boolean isInputWord(String key) {
        return inputWordList.contains(key);
    }

    private boolean isUsableGram(String key) {
        if (gramNumMap.containsKey(key)) {
            int num = ((Integer) (gramNumMap.get(key))).intValue();
            if (wsData.getGramCount() <= num || isInputWord(key)) { return true; }
            gramNumMap.remove(key);
        }
        return false;
    }

    private List addUsableGramToList(List gramList, List gramText) {
        for (int i = 1; i <= wsData.getGramNumber(); i++) {
            for (int j = 0; j <= wsData.getGramNumber() - i; j++) {
                StringBuffer gramBuf = new StringBuffer("");
                for (int k = 0; k < i; k++) {
                    if ((k + j) < gramList.size()) {
                        gramBuf.append(gramList.get(k + j));
                        gramBuf.append("_");
                    }
                }
                if (0 < gramBuf.length()) {
                    String gram = gramBuf.substring(0, gramBuf.length() - 1);
                    if (isUsableGram(gram)) {
                        gramText.add(gram);
                    }
                }
            }
        }
        return gramText;
    }

    public List getAllConceptPairs() {
        return allConceptPairs;
    }
}