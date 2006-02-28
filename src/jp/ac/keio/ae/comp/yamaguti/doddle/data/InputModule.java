package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.java.sen.*;

/**
 * 
 * @author takeshi morita
 * 
 */
public class InputModule {

    private Set<InputWordModel> inputWordModelSet;
    private Map<String, Set<Concept>> wordConceptSetMap;
    private Set<String> undefinedWordSet;

    public static int INIT_PROGRESS_VALUE = 887253;
    private DODDLEProject project;

    public InputModule(DODDLEProject p) {
        project = p;
        inputWordModelSet = new TreeSet<InputWordModel>();
        wordConceptSetMap = new HashMap<String, Set<Concept>>();
        undefinedWordSet = new TreeSet<String>();
    }

    static class WordIDsLinesComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String l1 = (String) o1;
            String l2 = (String) o2;
            String w1 = l1.split("\t")[0];
            String w2 = l2.split("\t")[0];
            return w1.compareTo(w2);
        }
    }

    static class IDDefinitionLinesComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String l1 = (String) o1;
            String l2 = (String) o2;
            return l1.compareTo(l2);
        }
    }

    private void clearData() {
        inputWordModelSet.clear();
        undefinedWordSet.clear();
        wordConceptSetMap.clear();
    }

    public InputWordModel makeInputWordModel(String iw) {
        Map<String, Set<Concept>> wcSetMap = null;
        if (DODDLE.IS_USING_DB) {
            wcSetMap = EDRDic.getEDRDBManager().getWordConceptSetMap();
        } else {
            wcSetMap = wordConceptSetMap;
        }
        return makeInputWordModel(iw, wcSetMap);
    }

    public InputWordModel makeInputWordModel(String iw, Map<String, Set<Concept>> wcSetMap) {
        if (iw.length() == 0) { return null; }
        List wordList = getWordList(iw);
        int matchedPosition = 0;
        StringBuffer subIW = null;
        Set<Concept> conceptSet = null;
        for (int i = 0; i < wordList.size(); i++) {
            matchedPosition = i;
            List subList = wordList.subList(i, wordList.size());
            subIW = new StringBuffer();
            for (Iterator j = subList.iterator(); j.hasNext();) {
                subIW.append(j.next());
            }
            conceptSet = getConceptSet(subIW.toString());
            if (0 < conceptSet.size()) {
                break;
            }
        }
        if (conceptSet.size() == 0) { return null; }
        InputWordModel iwModel = new InputWordModel(iw, wordList, subIW.toString(), matchedPosition == 0, conceptSet
                .size(), project);
        if (wcSetMap.get(iwModel.getMatchedWord()) == null) {
            wcSetMap.put(iwModel.getMatchedWord(), conceptSet);
        }
        return iwModel;
    }

    private Set<Concept> getConceptSet(String subIW) {
        Set<Concept> conceptSet = new HashSet<Concept>();
        setEDRConceptSet(subIW, conceptSet);
        setEDRTConceptSet(subIW, conceptSet);
        setWordNetConceptSet(subIW, conceptSet);
        return conceptSet;
    }

    private void setWordNetConceptSet(String subIW, Set<Concept> conceptSet) {
        if (!isEnglish(subIW)) { return; }
        try {
            // ここで，POS.NOUNのみを扱っているが，POS.VERBなどは扱わなくてもいいのか？
            IndexWord indexWord = WordNetDic.getInstance().getNounIndexWord(subIW);
            if (indexWord == null) { return; }
            for (int i = 0; i < indexWord.getSenseCount(); i++) {
                Synset synset = indexWord.getSense(i + 1);
                Concept c = WordNetDic.getWNConcept(new Long(synset.getOffset()).toString());
                conceptSet.add(c);
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    private void setEDRConceptSet(String subIW, Set<Concept> conceptSet) {
        Set<String> idSet = null;
        if (DODDLE.IS_USING_DB) {
            idSet = EDRDic.getEDRDBManager().getEDRIDSet(subIW);
        } else {
            idSet = EDRDic.getEDRIDSet(subIW);
        }
        if (idSet == null) { return; }
        for (String id : idSet) {
            Concept c = EDRDic.getEDRConcept(id);
            if (c != null) {
                conceptSet.add(c);
            }
        }
    }

    private void setEDRTConceptSet(String subIW, Set<Concept> conceptSet) {
        Set<String> idSet = null;
        if (DODDLE.IS_USING_DB) {
            idSet = EDRDic.getEDRTDBManager().getEDRIDSet(subIW);
        } else {
            System.out.println(subIW);
            idSet = EDRDic.getEDRTIDSet(subIW);
            System.out.println(idSet);
        }
        if (idSet == null) { return; }
        for (String id : idSet) {
            Concept c = EDRDic.getEDRTConcept(id);
            if (c != null) {
                conceptSet.add(c);
            }
        }
    }

    private boolean isEnglish(String iw) {
        return iw.matches("(\\w|\\d)*");
    }

    private List<String> getWordList(String iw) {
        if (isEnglish(iw)) { return getEnWordList(iw); }
        return getJaWordList(iw);
    }

    private List<String> getEnWordList(String iw) {
        return Arrays.asList(iw.split("\\s*"));
    }

    /**
     * @param iw
     * @param wordList
     */
    private List<String> getJaWordList(String iw) {
        List<String> wordList = new ArrayList<String>();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] token = tagger.analyze(iw);
            for (int i = 0; i < token.length; i++) {
                wordList.add(token[i].getBasicString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return wordList;
    }

    private InputWordModel setInputWord(String iw, DODDLEProject p) {
        InputWordModel iwModel = makeInputWordModel(iw, wordConceptSetMap);
        if (iwModel != null) {
            inputWordModelSet.add(iwModel);
        } else {
            undefinedWordSet.add(iw);
        }
        return iwModel;
    }

    public void initDataWithMem(Set<String> wordSet) {
        clearData();
        Set<String> matchedWordSet = new HashSet<String>();
        for (String word : wordSet) {
            InputWordModel iwModel = setInputWord(word, project);
            if (iwModel != null) {
                matchedWordSet.add(iwModel.getMatchedWord());
            }
        }
        // 部分照合した複合語中で，完全照合単語リストに含まれない照合した単語を完全照合単語として追加
        matchedWordSet.removeAll(wordSet);
        for (String matchedWord : matchedWordSet) {
            InputWordModel iwModel = makeInputWordModel(matchedWord, wordConceptSetMap);
            if (iwModel != null) {
                inputWordModelSet.add(iwModel);
            }
        }
    }

    public void initDataWithDB(Set<String> iwSet) {
        DBManager dbManager = EDRDic.getEDRDBManager();
        dbManager.initDataWithDB(iwSet, project);
        inputWordModelSet = new TreeSet<InputWordModel>(dbManager.getInputWordModelSet());
        wordConceptSetMap = new HashMap<String, Set<Concept>>(dbManager.getWordConceptSetMap());
        undefinedWordSet = new TreeSet<String>(dbManager.getUndefinedWordSet());
    }

    public Set<InputWordModel> getInputWordModelSet() {
        return inputWordModelSet;
    }

    public Map<String, Set<Concept>> getWordConceptSetMap() {
        return wordConceptSetMap;
    }

    public Set getUndefinedWordSet() {
        return undefinedWordSet;
    }
}