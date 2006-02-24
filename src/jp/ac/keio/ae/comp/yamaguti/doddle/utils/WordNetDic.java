/*
 * Created on 2003/11/20
 *  
 */
package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.*;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.file_manager.*;
import net.didion.jwnl.princeton.data.*;

import org.apache.log4j.*;

/**
 * @author shigeta
 * 
 * 2004-05-16 modified by takeshi morita
 * 
 */
public class WordNetDic {

    private static WordNetDic wordnetData;
    private static final String FILE_TYPE = "net.didion.jwnl.princeton.file.PrincetonRandomAccessDictionaryFile";

    public WordNetDic() {
        try {
            InputStream inputStream = WordNetDic.class.getClassLoader().getResourceAsStream(
                    DODDLE.JWNL_PROPERTIES_FILE);
            JWNL.initialize(inputStream);
        } catch (Exception ex) {
            DODDLE.getLogger().log(Level.INFO, "Initialize JWNL Error");
            JOptionPane.showMessageDialog(null, "WordNet dictionary path is not valid.");
        }
        try {
            setWordNetPath();
        } catch (Exception ex2) {
            DODDLE.getLogger().log(Level.INFO, "WordNet dictionary path is not valid.");
            JOptionPane.showMessageDialog(null, "WordNet dictionary path is not valid.");
        }
    }

    private void setWordNetPath() {
        try {
            File file = new File(DODDLE.WORDNET_PATH);
            Class file_type = Class.forName(FILE_TYPE);
            FileBackedDictionary.install(new FileManagerImpl(file.getAbsolutePath(), file_type),
                    new PrincetonWN17FileDictionaryElementFactory());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static WordNetDic getInstance() {
        if (wordnetData == null) {
            wordnetData = new WordNetDic();
        }
        return wordnetData;
    }

    public IndexWord getIndexWord(POS pos, String word) {
        IndexWord indexWord = null;
        try {
            indexWord = Dictionary.getInstance().lookupIndexWord(POS.NOUN, word);
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return indexWord;
    }

    public IndexWord getProperty(String concept) {
        IndexWord retiw = null;
        try {
            retiw = Dictionary.getInstance().lookupIndexWord(POS.NOUN, concept);
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return retiw;
    }

    private Set adverbConceptSet = new HashSet();

    private boolean isAdverb(String concept) {
        if (adverbConceptSet.contains(concept)) { return true; }
        IndexWord retiw;
        try {
            retiw = Dictionary.getInstance().lookupIndexWord(POS.ADVERB, concept);
        } catch (JWNLException je) {
            retiw = null;
        }
        if (retiw != null) {
            adverbConceptSet.add(concept);
        }
        return (retiw != null);
    }

    private boolean isNumber(String concept) {
        if (concept.startsWith("1") || concept.startsWith("2") || concept.startsWith("3") || concept.startsWith("4")
                || concept.startsWith("5") || concept.startsWith("6") || concept.startsWith("7")
                || concept.startsWith("8") || concept.startsWith("9") || concept.startsWith("0")) {
            return true;
        } else if (concept.equals("one") || concept.equals("two") || concept.equals("three") || concept.equals("four")
                || concept.equals("five") || concept.equals("six") || concept.equals("seven")
                || concept.equals("eight") || concept.equals("nine") || concept.equals("ten")
                || concept.equals("eleven") || concept.equals("twelve") || concept.equals("thirteen")
                || concept.equals("fourteen") || concept.equals("fifteen") || concept.equals("sixteen")
                || concept.equals("seventeen") || concept.equals("eighteen") || concept.equals("nineteen")
                || concept.equals("twenty") || concept.equals("thirty") || concept.equals("forty")
                || concept.equals("fifty") || concept.equals("sixty") || concept.equals("seventy")
                || concept.equals("eighty") || concept.equals("ninety") || concept.equals("hundred")
                || concept.equals("thousand")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isVerb(String concept) {
        return (concept.equals("be") || concept.equals("are") || concept.equals("take") || concept.equals("make")
                || concept.equals("do") || concept.equals("have") || concept.equals("give") || concept.equals("given"));
    }

    private boolean isNogood(String concept) {
        return (concept.equals("ha") || concept.equals("or") || concept.equals("he") || concept.equals("at")
                || concept.equals("who") || concept.equals("wa") || concept.equals("ii") || concept.equals("iii"));
    }

    public boolean isInvalid(String concept) {
        return (isVerb(concept) || isAdverb(concept) || isNumber(concept) || isNogood(concept));
    }

    public static Set<List<Concept>> getPathToRootSet(Long offset) {
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetTree hypernymTree = PointerUtils.getInstance().getHypernymTree(synset);
            // ëΩèdåpè≥ÇãñÇ∑ÇÊÇ§Ç…ÇµÇƒÇ¢ÇÈ
            PointerTargetNodeList[] treeNodeLists = hypernymTree.reverse();
            for (int i = 0; i < treeNodeLists.length; i++) {
                PointerTargetNodeList nodeList = treeNodeLists[i];
                List<Concept> conceptList = new ArrayList<Concept>();
                for (Iterator j = nodeList.iterator(); j.hasNext();) {
                    PointerTargetNode node = (PointerTargetNode) j.next();
                    Concept c = getWNConcept(new Long(node.getSynset().getOffset()).toString());
                    conceptList.add(c);
                }
                pathToRootSet.add(conceptList);
            }
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        }
        return pathToRootSet;
    }

    private static Map<String, Concept> idConceptMap = new HashMap<String, Concept>();

    public static Concept getWNConcept(String id) {
        try {
            if (idConceptMap.get(id) != null) { return idConceptMap.get(id); }
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, new Long(id).longValue());
            Concept c = new Concept(id, "");
            c.setPrefix("wn");
            Word[] words = synset.getWords();
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < words.length; j++) {
                builder.append(words[j].getLemma() + "\t");
            }
            c.setEnWord(builder.toString());
            c.setEnExplanation(synset.getGloss());
            idConceptMap.put(id, c);
            return c;
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        }
        return null;
    }

    public IndexWord getNounIndexWord(String word) {
        try {
            return Dictionary.getInstance().lookupIndexWord(POS.NOUN, word);
        } catch (Exception e) {
            return null;
        }
    }
    /*
     * public void setInputConceptData(List inputConcepts, Map synsetNumberMap,
     * InputConceptData data) { Map offsetLabelMap = new HashMap(); Map
     * offsetSynsetMap = new HashMap();
     * 
     * for (Iterator i = inputConcepts.iterator(); i.hasNext();) { try { String
     * conceptLabel = (String) i.next(); IndexWord indexWord =
     * corpus.getIndexData().getIndexWord(conceptLabel); if (indexWord != null) { //
     * int n = 1; // Integer synsetNum = (Integer) //
     * synsetNumberMap.get(indexWord.getLemma()); // if (synsetNum != null) { //
     * n = synsetNum.intValue(); // System.out.println("sn:" + n + " word:" + //
     * indexWord.getLemma()); // }
     * 
     * Synset synset = indexWord.getSense(1); Long offset = (Long)
     * synsetNumberMap.get(indexWord.getLemma()); if (offset != null) { synset =
     * Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue()); }
     * else { offset = new Long(synset.getOffset()); } // Synset synset =
     * indexWord.getSense(n); // System.out.println(synset); // Long offset =
     * new Long(synset.getOffset());
     * 
     * offsetSynsetMap.put(offset, synset); offsetLabelMap.put(offset,
     * conceptLabel); } } catch (JWNLException je) { je.printStackTrace(); } }
     * data.setData(offsetLabelMap, offsetSynsetMap); }
     */
}