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
import java.util.Map.*;

/**
 * @author takeshi morita
 */
public class TermInfo {

    private double docNum;
    private String word;
    private Set<String> posSet;
    private boolean isInputword;
    private Map<File, Integer> docTermFreqMap;
    private Map<File, Integer> inputDocTermFreqMap;
    private Set<String> upperConceptLabelSet;

    public TermInfo(String w, int dn) {
        docNum = dn;
        word = w;
        posSet = new HashSet<String>();
        docTermFreqMap = new HashMap<File, Integer>();
        inputDocTermFreqMap = new HashMap<File, Integer>();
        upperConceptLabelSet = new HashSet<String>();
    }

    public void setPosSet(Set<String> pset) {
        posSet = pset;
    }

    public void setUpperConceptSet(Set<String> ucSet) {
        upperConceptLabelSet = ucSet;
    }

    public void addUpperConcept(String upperConcept) {
        upperConceptLabelSet.add(upperConcept);
    }

    public void addPos(String pos) {
        posSet.add(pos);
    }

    public boolean isInputWord() {
        return isInputword;
    }

    public void putDoc(File doc, Integer num) {
        docTermFreqMap.put(doc, num);
    }

    public void putDoc(File doc) {
        if (docTermFreqMap.get(doc) != null) {
            Integer freq = docTermFreqMap.get(doc);
            docTermFreqMap.put(doc, new Integer(freq.intValue() + 1));
        } else {
            docTermFreqMap.put(doc, new Integer(1));
        }
    }

    public void putInputDoc(File doc, Integer num) {
        inputDocTermFreqMap.put(doc, num);
    }

    public void putInputDoc(File doc) {
        isInputword = true;
        if (inputDocTermFreqMap.get(doc) != null) {
            Integer freq = inputDocTermFreqMap.get(doc);
            inputDocTermFreqMap.put(doc, new Integer(freq.intValue() + 1));
        } else {
            inputDocTermFreqMap.put(doc, new Integer(1));
        }
    }

    public Set<File> getDocumentSet() {
        return docTermFreqMap.keySet();
    }

    public Set<File> getInputDocumentSet() {
        return inputDocTermFreqMap.keySet();
    }

    public int getInputDocumentTF(File doc) {
        return inputDocTermFreqMap.get(doc);
    }

    public Set<String> getUpperConceptLabelSet() {
        return upperConceptLabelSet;
    }

    public Set<String> getPosSet() {
        return posSet;
    }

    public String getTerm() {
        return word;
    }

    public int getTF() {
        int termFreq = 0;
        for (File doc : docTermFreqMap.keySet()) {
            Integer freq = docTermFreqMap.get(doc);
            termFreq += freq.intValue();
        }
        for (File doc : inputDocTermFreqMap.keySet()) {
            Integer freq = inputDocTermFreqMap.get(doc);
            termFreq += freq.intValue();
        }
        return termFreq;
    }

    /**
     * log(N/Ni) N: 全文書数, Ni: tiを含む文書数
     */
    public double getIDF() {
        double ni = docTermFreqMap.size() + inputDocTermFreqMap.size();
        return Math.log(docNum / ni);
    }

    public String getIDFString() {
        return String.format("%.3f", getIDF());
    }

    /**
     * tfidf = fti log (N/Ni)
     */
    public double getTFIDF() {
        return getTF() * getIDF();
    }

    public String getTFIDFString() {
        return String.format("%.3f", getTFIDF());
    }

    public Vector getRowData() {
        Vector rowData = new Vector();
        rowData.add(word);
        StringBuffer buf = new StringBuffer("");
        for (String pos : posSet) {
            buf.append(pos + ":");
        }
        rowData.add(buf.toString());
        rowData.add(getTF());
        rowData.add(new Double(getIDFString()));
        rowData.add(new Double(getTFIDFString()));
        // buf = new StringBuffer("");
        // for (File doc : docTermFreqMap.keySet()) {
        // Integer num = docTermFreqMap.get(doc);
        // buf.append(doc.getName() + "=" + num + ":");
        // }
        // rowData.add(buf.toString());
        buf = new StringBuffer("");
        for (String concept : upperConceptLabelSet) {
            buf.append("[");
            buf.append(concept);
            buf.append("] ");
        }
        rowData.add(buf.toString());
        return rowData;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(word + "\t");
        for (String pos : posSet) {
            buf.append(pos + ":");
        }
        buf.append("\t");
        buf.append(getTF() + "\t");
        buf.append(getIDFString() + "\t");
        buf.append(getTFIDFString() + "\t");
        for (Entry<File, Integer> entry : inputDocTermFreqMap.entrySet()) {
            File doc = entry.getKey();
            Integer num = entry.getValue();
            buf.append(doc.getName() + "=" + num + ":");
        }
        buf.append("\t");
        for (String ucLabel : upperConceptLabelSet) {
            buf.append(ucLabel + ":");
        }
        return buf.toString();
    }
}
