/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved. 
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
 * @author Takeshi Morita
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
        if (FileBackedDictionary.getInstance() != null) { return FileBackedDictionary.getInstance()
                .getMorphologicalProcessor(); }
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

    public static Set<Set<String>> getSiblingConceptSet(Long offset) {
        Set<Set<String>> siblingIDSet = new HashSet<Set<String>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetNodeList siblingNodeList = PointerUtils.getInstance().getCoordinateTerms(synset);
            Set<String> idList = new HashSet<String>();
            for (Iterator i = siblingNodeList.iterator(); i.hasNext();) {
                PointerTargetNode node = (PointerTargetNode) i.next();
                idList.add(new Long(node.getSynset().getOffset()).toString());
            }
            siblingIDSet.add(idList);
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        }
        return siblingIDSet;
    }

    public static Set<Set<String>> getSubIDSet(Long offset) {
        Set<Set<String>> subIDSet = new HashSet<Set<String>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetTree hyponymTree = PointerUtils.getInstance().getHyponymTree(synset);
            // 多重継承を許すようにしている
            PointerTargetNodeList[] treeNodeLists = hyponymTree.reverse();
            for (PointerTargetNodeList nodeList : treeNodeLists) {
                Set<String> idList = new HashSet<String>();
                for (Iterator j = nodeList.iterator(); j.hasNext();) {
                    PointerTargetNode node = (PointerTargetNode) j.next();
                    idList.add(new Long(node.getSynset().getOffset()).toString());
                }
                subIDSet.add(idList);
            }
        } catch (JWNLException jwnle) {
            jwnle.printStackTrace();
        }
        return subIDSet;
    }

    public static Set<List<Concept>> getPathToRootSet(Long offset) {
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetTree hypernymTree = PointerUtils.getInstance().getHypernymTree(synset);
            // 多重継承を許すようにしている
            PointerTargetNodeList[] treeNodeLists = hypernymTree.reverse();
            for (PointerTargetNodeList nodeList : treeNodeLists) {
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

    private static boolean isEnglish(String iw) {
        return iw.matches("(\\w|\\s)*");
    }

    public static Set<String> getURISet(String word) {
        Set<String> uriSet = new HashSet<String>();
        if (!DODDLE.getCurrentProject().getOntologySelectionPanel().isWordNetEnable()) { return uriSet; }
        if (!isEnglish(word)) { return uriSet; }
        try {
            IndexWord indexWord = WordNetDic.getInstance().getNounIndexWord(word);
            if (indexWord == null) { return uriSet; }
            for (int i = 0; i < indexWord.getSenseCount(); i++) {
                Synset synset = indexWord.getSense(i + 1);
                if (synset.containsWord(word)) {
                    uriSet.add(DODDLEConstants.WN_URI + new Long(synset.getOffset()).toString());
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return uriSet;
    }

    public static Set<List<String>> getURIPathToRootSet(Long offset) {
        Set<List<String>> pathToRootSet = new HashSet<List<String>>();
        try {
            Synset synset = Dictionary.getInstance().getSynsetAt(POS.NOUN, offset.longValue());
            PointerTargetTree hypernymTree = PointerUtils.getInstance().getHypernymTree(synset);
            // 多重継承を許すようにしている
            PointerTargetNodeList[] treeNodeLists = hypernymTree.reverse();
            for (PointerTargetNodeList nodeList : treeNodeLists) {
                List<String> uriList = new ArrayList<String>();
                for (Iterator j = nodeList.iterator(); j.hasNext();) {
                    PointerTargetNode node = (PointerTargetNode) j.next();
                    String uri = DODDLEConstants.WN_URI + new Long(node.getSynset().getOffset()).toString();
                    uriList.add(uri);
                }
                pathToRootSet.add(uriList);
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
            for (Word word : synset.getWords()) {
                c.addLabel(new DODDLELiteral("en", word.getLemma()));
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