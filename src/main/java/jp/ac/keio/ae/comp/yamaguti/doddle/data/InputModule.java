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

import java.beans.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.java.sen.*;
import net.java.sen.dictionary.*;

/**
 * 
 * @author takeshi morita
 * 
 */
public class InputModule {

    private boolean isLoadInputTermSet;
    private Set<String> undefinedTermSet;
    private Set<InputTermModel> inputTermModelSet;
    private Map<String, Set<Concept>> termConceptSetMap;

    public static int INIT_PROGRESS_VALUE = 887253;
    private DODDLEProject project;

    public InputModule(DODDLEProject p) {
        project = p;
        isLoadInputTermSet = false;
        undefinedTermSet = new TreeSet<String>();
        inputTermModelSet = new TreeSet<InputTermModel>();
        termConceptSetMap = new HashMap<String, Set<Concept>>();
    }

    static class WordIDsLinesComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            String w1 = o1.split("\t")[0];
            String w2 = o2.split("\t")[0];
            return w1.compareTo(w2);
        }
    }

    static class IDDefinitionLinesComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    private void clearData() {
        isLoadInputTermSet = false;
        inputTermModelSet.clear();
        undefinedTermSet.clear();
        termConceptSetMap.clear();
    }

    public boolean isLoadInputTermSet() {
        return isLoadInputTermSet;
    }

    public void setIsLoadInputTermSet() {
        isLoadInputTermSet = true;
    }

    /**
     * 
     * 複合語の先頭の形態素から除いていき照合を行う．
     */
    public InputTermModel makeInputTermModel(String inputTerm) {
        if (inputTerm.length() == 0) { return null; }
        List<Token> tokenList = getTokenList(inputTerm);
        StringBuilder subInputTermBuilder = null;
        Set<Concept> conceptSet = null;
        boolean isEnglish = isEnglish(inputTerm);
        int matchedPoint = 0;
        for (int i = 0; i < tokenList.size(); i++) {
            List<Token> subList = tokenList.subList(i, tokenList.size());
            subInputTermBuilder = new StringBuilder();
            for (Token morpheme : subList) {
                if (isEnglish) {
                    // subIW.append(morpheme.getBasicString() + " ");
                    subInputTermBuilder.append(morpheme.getMorpheme().getBasicForm() + " ");
                } else {
                    // subIW.append(morpheme.getBasicString());
                    subInputTermBuilder.append(morpheme.getMorpheme().getBasicForm());
                }
            }
            if (isEnglish) {
                subInputTermBuilder.deleteCharAt(subInputTermBuilder.length() - 1);
            }
            conceptSet = getConceptSet(subInputTermBuilder.toString());
            if (0 < conceptSet.size()) {
                matchedPoint = i;
                break;
            }
        }
        if (conceptSet.size() == 0) { return null; }
        InputTermModel itModel = new InputTermModel(inputTerm, tokenList, subInputTermBuilder.toString(), conceptSet
                .size(), matchedPoint, project);
        if (termConceptSetMap.get(itModel.getMatchedTerm()) == null) {
            termConceptSetMap.put(itModel.getMatchedTerm(), conceptSet);
        }
        return itModel;
    }

    private Set<Concept> getConceptSet(String subInputTerm) {
        Set<Concept> conceptSet = new HashSet<Concept>();
        setEDRConceptSet(subInputTerm, conceptSet);
        setEDRTConceptSet(subInputTerm, conceptSet);
        setWordNetConceptSet(subInputTerm.replaceAll(" ", "_"), conceptSet);
        setJpnWordNetConceptSet(subInputTerm.replaceAll(" ", "_"), conceptSet);
        OWLOntologyManager.setOWLConceptSet(subInputTerm, conceptSet);
        return conceptSet;
    }

    private void setWordNetConceptSet(String subIW, Set<Concept> conceptSet) {
        if (!project.getOntologySelectionPanel().isWordNetEnable()) { return; }
        if (!isEnglish(subIW)) { return; }
        try {
            // getAllIndexWordも使えそう
            IndexWord indexWord = WordNetDic.getInstance().getNounIndexWord(subIW);
            // if (indexWord == null) {
            // indexWord = WordNetDic.getInstance().getVerbIndexWord(subIW);
            // }
            if (indexWord == null) { return; }
            for (int i = 0; i < indexWord.getSenseCount(); i++) {
                Synset synset = indexWord.getSense(i + 1);
                if (synset.containsWord(subIW)) {
                    Concept c = WordNetDic.getWNConcept(new Long(synset.getOffset()).toString());
                    conceptSet.add(c);
                }
            }
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }

    private void setJpnWordNetConceptSet(String subIW, Set<Concept> conceptSet) {
        if (!project.getOntologySelectionPanel().isJpnWordNetEnable()) { return; }
        Set<String> idSet = JpnWordNetDic.getJPNWNSynsetSet(subIW);
        if (idSet == null) { return; }
        for (String id : idSet) {
            Concept c = JpnWordNetDic.getJPNWNConcept(id);
            if (c != null) {
                conceptSet.add(c);
            }
        }
    }

    private void setEDRConceptSet(String subInputTerm, Set<Concept> conceptSet) {
        if (!project.getOntologySelectionPanel().isEDREnable()) { return; }
        Set<String> idSet = EDRDic.getEDRIDSet(subInputTerm);
        if (idSet == null) { return; }
        for (String id : idSet) {
            Concept c = EDRDic.getEDRConcept(id);
            if (c != null) {
                conceptSet.add(c);
            }
        }
    }

    private void setEDRTConceptSet(String subIW, Set<Concept> conceptSet) {
        if (!project.getOntologySelectionPanel().isEDRTEnable()) { return; }
        Set<String> idSet = EDRDic.getEDRTIDSet(subIW);
        if (idSet == null) { return; }
        for (String id : idSet) {
            Concept c = EDRDic.getEDRTConcept(id);
            if (c != null) {
                conceptSet.add(c);
            }
        }
    }

    private boolean isEnglish(String iw) {
        return iw.matches("(\\w|\\s)*");
    }

    private List<Token> getTokenList(String iw) {
        if (isEnglish(iw)) { return getEnTokenList(iw); }
        return getJaTokenList(iw);
    }

    private List<Token> getEnTokenList(String iw) {
        if (iw.indexOf(" ") == -1) {
            Token token = new Token();
            token.setSurface(iw);
            // token.setBasicString(iw);
            token.getMorpheme().setBasicForm(iw);
            return Arrays.asList(new Token[] { token});
        }
        List<Token> tokenList = new ArrayList<Token>();
        for (String w : iw.split(" ")) {
            Token token = new Token();
            token.setSurface(w);
            // token.setBasicString(w);
            token.getMorpheme().setBasicForm(w);
            tokenList.add(token);
        }
        return tokenList;
    }

    /**
     * @param iw
     */
    private List<Token> getJaTokenList(String iw) {
        List<Token> tokenList = null;
        try {
            StringTagger tagger = SenFactory.getStringTagger(DODDLEConstants.GOSEN_CONFIGURATION_FILE);
            tokenList = tagger.analyze(iw);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return tokenList;
    }

    public class InputTermModelWorker extends SwingWorker implements PropertyChangeListener {

        private int division;
        private int initialTaskCnt;
        private int currentTaskCnt;
        private Set<String> termSet;

        public InputTermModelWorker(Set<String> termSet, int taskCnt) {
            initialTaskCnt = taskCnt;
            this.termSet = termSet;
            if (initialTaskCnt == 0) {
                addPropertyChangeListener(this);
                currentTaskCnt = taskCnt;
                int progressCountSize = 50;
                if (termSet.size() < progressCountSize) {
                    division = 1;
                } else {
                    division = termSet.size() / progressCountSize;
                }
                DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("SetInputTermSetButton"));
                DODDLE.STATUS_BAR.startTime();
                DODDLE.STATUS_BAR.initNormal(progressCountSize);
                DODDLE.STATUS_BAR.lock();
            }
        }

        public String doInBackground() {
            try {
                clearData();
                Set<String> matchedTermSet = new HashSet<String>();
                int i = 0;
                for (String term : termSet) {
                    i++;
                    DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("SetInputTermSetButton") + ": " + i + "/"
                            + termSet.size());
                    if (initialTaskCnt == 0 && i % division == 0) {
                        setProgress(currentTaskCnt++);
                    }
                    InputTermModel itModel = makeInputTermModel(term);

                    if (itModel != null) {
                        inputTermModelSet.add(itModel);
                        // 部分照合した複合語中で，完全照合単語リストに含まれない照合した単語を完全照合単語として追加
                        String matchedTerm = itModel.getMatchedTerm();
                        if (!(term.equals(matchedTerm) || matchedTermSet.contains(matchedTerm) || termSet
                                .contains(matchedTerm))) {
                            itModel = makeInputTermModel(matchedTerm);
                            if (itModel != null) {
                                matchedTermSet.add(matchedTerm);
                                itModel.setIsSystemAdded(true);
                                inputTermModelSet.add(itModel);
                            }
                        }
                    } else {
                        if (0 < term.length()) {
                            undefinedTermSet.add(term);
                        }
                    }
                }
                project.getInputConceptSelectionPanel().initTermList();
                project.getConceptDefinitionPanel().setInputConceptSet();
                DODDLE.setSelectedIndex(DODDLEConstants.DISAMBIGUATION_PANEL);
            } finally {
                if (initialTaskCnt == 0) {
                    DODDLE.STATUS_BAR.unLock();
                    DODDLE.STATUS_BAR.hideProgressBar();
                }
                isLoadInputTermSet = true;
                DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("SetInputTermSetDoneMessage"));
            }
            return "done";
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE.STATUS_BAR.setValue(currentTaskCnt);
            }
        }
    }

    public void initData(Set<String> termSet, int taskCnt) {
        InputTermModelWorker worker = new InputTermModelWorker(termSet, taskCnt);
        DODDLE.STATUS_BAR.setSwingWorker(worker);
        worker.execute();
    }

    public Set<InputTermModel> getInputTermModelSet() {
        return inputTermModelSet;
    }

    public Map<String, Set<Concept>> getTermConceptSetMap() {
        return termConceptSetMap;
    }

    public Set<String> getUndefinedTermSet() {
        return undefinedTermSet;
    }
}