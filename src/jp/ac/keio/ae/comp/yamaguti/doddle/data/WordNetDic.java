/*
 * Created on 2003/11/20
 *  
 */
package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.data.list.*;
import net.didion.jwnl.dictionary.*;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.file_manager.*;
import net.didion.jwnl.dictionary.morph.*;
import net.didion.jwnl.princeton.data.*;

import org.apache.log4j.*;

/**
 * @author shigeta
 * 
 * 2004-05-16 modified by takeshi morita
 * 
 */
public class WordNetDic {

    private static WordNetDic wordnetDic;
    private static final String FILE_TYPE = "net.didion.jwnl.princeton.file.PrincetonRandomAccessDictionaryFile";
    private static final String RESOURCES = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";
    public static String JWNL_PROPERTIES_FILE = RESOURCES + "file_properties.xml";

    public WordNetDic() {
        try {
            InputStream inputStream = WordNetDic.class.getClassLoader().getResourceAsStream(JWNL_PROPERTIES_FILE);
            JWNL.initialize(inputStream);
        } catch (Exception ex) {
            DODDLE.getLogger().log(Level.INFO, "Initialize JWNL Error");
        }
        try {
            setWordNetPath();
        } catch (Exception ex2) {
            ex2.printStackTrace();
            DODDLE.getLogger().log(Level.INFO, Translator.getTerm("WordNetLoadErrorMessage"));
            JOptionPane.showMessageDialog(null, Translator.getTerm("WordNetLoadErrorMessage"));
        }
    }

    private void setWordNetPath() throws Exception {
        File file = new File(DODDLEConstants.WORDNET_HOME);
        Class file_type = Class.forName(FILE_TYPE);
        FileBackedDictionary.install(new FileManagerImpl(file.getAbsolutePath(), file_type),
                getMorphologicalProcessor(), new PrincetonWN17FileDictionaryElementFactory(), true);
    }

    private MorphologicalProcessor getMorphologicalProcessor() {
        if (FileBackedDictionary.getInstance() != null) { return FileBackedDictionary.getInstance().getMorphologicalProcessor(); }
        Map<POS, String[][]> suffixMap = new HashMap<POS, String[][]>();
        suffixMap.put(POS.NOUN, new String[][] { { "s", ""}, { "ses", "s"}, { "xes", "x"}, { "zes", "z"},
                { "ches", "ch"}, { "shes", "sh"}, { "men", "man"}, { "ies", "y"}});
        suffixMap.put(POS.VERB, new String[][] { { "s", ""}, { "ies", "y"}, { "es", "e"}, { "es", ""}, { "ed", "e"},
                { "ed", ""}, { "ing", "e"}, { "ing", ""}});
        suffixMap.put(POS.ADJECTIVE, new String[][] { { "er", ""}, { "est", ""}, { "er", "e"}, { "est", "e"}});
        DetachSuffixesOperation tokDso = new DetachSuffixesOperation(suffixMap);
        tokDso.addDelegate(DetachSuffixesOperation.OPERATIONS, new Operation[] { new LookupIndexWordOperation(),
                new LookupExceptionsOperation()});
        TokenizerOperation tokOp = new TokenizerOperation(new String[] { " ", "-"});
        tokOp.addDelegate(TokenizerOperation.TOKEN_OPERATIONS, new Operation[] { new LookupIndexWordOperation(),
                new LookupExceptionsOperation(), tokDso});
        DetachSuffixesOperation morphDso = new DetachSuffixesOperation(suffixMap);
        morphDso.addDelegate(DetachSuffixesOperation.OPERATIONS, new Operation[] { new LookupIndexWordOperation(),
                new LookupExceptionsOperation()});
        Operation[] operations = { new LookupExceptionsOperation(), morphDso, tokOp};
        return new DefaultMorphologicalProcessor(operations);
    }

    public static void resetWordNet() {
        wordnetDic = null;
    }
    
    public static WordNetDic getInstance() {
        if (wordnetDic == null) {
            wordnetDic = new WordNetDic();
        }
        return wordnetDic;
    }

    public IndexWord getIndexWord(POS pos, String word) {
        IndexWord indexWord = null;
        try {
            indexWord = Dictionary.getInstance().lookupIndexWord(pos, word);
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return indexWord;
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

    public static Set<List<Concept>> getSiblingConceptSet(Long offset) {
        Set<List<Concept>> siblingConceptSet = new HashSet<List<Concept>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetNodeList siblingNodeList = PointerUtils.getInstance().getCoordinateTerms(synset);
            List<Concept> conceptList = new ArrayList<Concept>();
            for (Iterator i = siblingNodeList.iterator(); i.hasNext();) {
                PointerTargetNode node = (PointerTargetNode) i.next();
                Concept c = getWNConcept(new Long(node.getSynset().getOffset()).toString());
                conceptList.add(c);
            }
            siblingConceptSet.add(conceptList);
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        }
        return siblingConceptSet;
    }

    public static Set<List<Concept>> getSubConceptSet(Long offset) {
        Set<List<Concept>> subConceptSet = new HashSet<List<Concept>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetTree hyponymTree = PointerUtils.getInstance().getHyponymTree(synset);
            // 多重継承を許すようにしている
            PointerTargetNodeList[] treeNodeLists = hyponymTree.reverse();
            for (int i = 0; i < treeNodeLists.length; i++) {
                PointerTargetNodeList nodeList = treeNodeLists[i];
                List<Concept> conceptList = new ArrayList<Concept>();
                for (Iterator j = nodeList.iterator(); j.hasNext();) {
                    PointerTargetNode node = (PointerTargetNode) j.next();
                    Concept c = getWNConcept(new Long(node.getSynset().getOffset()).toString());
                    conceptList.add(c);
                }
                subConceptSet.add(conceptList);
            }
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        }
        return subConceptSet;
    }

    public static Set<List<Concept>> getPathToRootSet(Long offset) {
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetTree hypernymTree = PointerUtils.getInstance().getHypernymTree(synset);
            // 多重継承を許すようにしている
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
        if (Dictionary.getInstance() == null) { return null; }
        String uri = DODDLEConstants.WN_URI + id;
        try {
            if (idConceptMap.get(uri) != null) { return idConceptMap.get(uri); }
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, new Long(id).longValue());
            // if (synset == null) {
            // synset = Dictionary.getInstance().getSynsetAt(POS.VERB, new
            // Long(id).longValue());
            // }
            Concept c = new Concept(uri, "");
            Word[] words = synset.getWords();
            for (int i = 0; i < words.length; i++) {
                c.addLabel(new DODDLELiteral("en", words[i].getLemma()));
            }
            c.addDescription(new DODDLELiteral("en", synset.getGloss()));
            idConceptMap.put(uri, c);
            return c;
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }

    public IndexWord getNounIndexWord(String word) {
        try {
            return Dictionary.getInstance().lookupIndexWord(POS.NOUN, word);
        } catch (JWNLException e) {
            return null;
        }
    }

    public IndexWord getVerbIndexWord(String word) {
        try {
            return Dictionary.getInstance().lookupIndexWord(POS.VERB, word);
        } catch (JWNLException e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            InputStream inputStream = WordNetDic.class.getClassLoader().getResourceAsStream(JWNL_PROPERTIES_FILE);
            JWNL.initialize(inputStream);
            File file = new File(DODDLEConstants.WORDNET_HOME);
            Class file_type = Class.forName(FILE_TYPE);
            Map<POS, String[][]> suffixMap = new HashMap<POS, String[][]>();
            suffixMap.put(POS.NOUN, new String[][] { { "s", ""}, { "ses", "s"}, { "xes", "x"}, { "zes", "z"},
                    { "ches", "ch"}, { "shes", "sh"}, { "men", "man"}, { "ies", "y"}});
            suffixMap.put(POS.VERB, new String[][] { { "s", ""}, { "ies", "y"}, { "es", "e"}, { "es", ""},
                    { "ed", "e"}, { "ed", ""}, { "ing", "e"}, { "ing", ""}});
            suffixMap.put(POS.ADJECTIVE, new String[][] { { "er", ""}, { "est", ""}, { "er", "e"}, { "est", "e"}});
            DetachSuffixesOperation tokDso = new DetachSuffixesOperation(suffixMap);
            tokDso.addDelegate(DetachSuffixesOperation.OPERATIONS, new Operation[] { new LookupIndexWordOperation(),
                    new LookupExceptionsOperation()});
            TokenizerOperation tokOp = new TokenizerOperation(new String[] { " ", "-"});
            tokOp.addDelegate(TokenizerOperation.TOKEN_OPERATIONS, new Operation[] { new LookupIndexWordOperation(),
                    new LookupExceptionsOperation(), tokDso});
            DetachSuffixesOperation morphDso = new DetachSuffixesOperation(suffixMap);
            morphDso.addDelegate(DetachSuffixesOperation.OPERATIONS, new Operation[] { new LookupIndexWordOperation(),
                    new LookupExceptionsOperation()});
            Operation[] operations = { new LookupExceptionsOperation(), morphDso, tokOp};
            FileBackedDictionary.install(new FileManagerImpl(file.getAbsolutePath(), file_type),
                    new DefaultMorphologicalProcessor(operations), new PrincetonWN17FileDictionaryElementFactory(),
                    true);
        } catch (Exception ex) {
            DODDLE.getLogger().log(Level.INFO, "Initialize JWNL Error");
            JOptionPane.showMessageDialog(null, "WordNet dictionary path is not valid.");
        }
        System.out.println(Dictionary.getInstance().lookupIndexWord(POS.NOUN, "facilities"));
        System.out.println(Dictionary.getInstance().lookupIndexWord(POS.NOUN, "has"));
        System.out.println(Dictionary.getInstance().lookupIndexWord(POS.NOUN, "had"));
        System.out.println(Dictionary.getInstance().lookupIndexWord(POS.VERB, "had"));
    }
}