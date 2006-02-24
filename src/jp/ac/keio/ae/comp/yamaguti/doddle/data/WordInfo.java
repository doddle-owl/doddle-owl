package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import javax.swing.*;

/**
 * @author takeshi morita
 */
public class WordInfo {

    private int docNum;
    private String word;
    private Set posSet;
    private boolean isInputword;
    private Map docTermFreqMap;
    private Map inputDocTermFreqMap;
    private Set upperConceptSet;

    public WordInfo(String w, int dn) {
        docNum = dn;
        word = w;
        posSet = new HashSet();
        docTermFreqMap = new HashMap();
        inputDocTermFreqMap = new HashMap();
        upperConceptSet = new HashSet();
    }

    public void setPosSet(Set pset) {
        posSet = pset;
    }

    public void setUpperConceptSet(Set ucSet) {
        upperConceptSet = ucSet;
    }

    public void addUpperConcept(String upperConcept) {
        upperConceptSet.add(upperConcept);
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
            Integer freq = (Integer) docTermFreqMap.get(doc);
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
            Integer freq = (Integer) inputDocTermFreqMap.get(doc);
            inputDocTermFreqMap.put(doc, new Integer(freq.intValue() + 1));
        } else {
            inputDocTermFreqMap.put(doc, new Integer(1));
        }
    }

    public Set getDocumentSet() {
        return docTermFreqMap.keySet();
    }

    public Set getInputDocumentSet() {
        return inputDocTermFreqMap.keySet();
    }

    public Set getUpperConceptSet() {
        return upperConceptSet;
    }

    public Set getPosSet() {
        return posSet;
    }

    public String getWord() {
        return word;
    }

    public int getTF() {
        int termFreq = 0;
        for (Iterator i = docTermFreqMap.keySet().iterator(); i.hasNext();) {
            File doc = (File) i.next();
            Integer freq = (Integer) docTermFreqMap.get(doc);
            termFreq += freq.intValue();
        }
        for (Iterator i = inputDocTermFreqMap.keySet().iterator(); i.hasNext();) {
            File doc = (File) i.next();
            Integer freq = (Integer) inputDocTermFreqMap.get(doc);
            termFreq += freq.intValue();
        }
        return termFreq;
    }

    /**
     * log(N/Ni) N: ëSï∂èëêî, Ni: tiÇä‹Çﬁï∂èëêî
     */
    public double getIDF() {
        double ni = docTermFreqMap.size() + inputDocTermFreqMap.size();
        return Math.log(docNum / ni);
    }

    /**
     * tfidf = fti log (N/Ni)
     */
    public double getTFIDF() {
        return getTF() * getIDF();
    }

    public Vector getRowData() {
        Vector rowData = new Vector();
        rowData.add(word);
        StringBuffer buf = new StringBuffer("");
        for (Iterator i = posSet.iterator(); i.hasNext();) {
            String pos = (String) i.next();
            buf.append(pos + ":");
        }
        rowData.add(buf.toString());
        rowData.add(new Integer(getTF()));
        rowData.add(new Double(getIDF()));
        rowData.add(new Double(getTFIDF()));
        buf = new StringBuffer("");
        for (Iterator i = docTermFreqMap.keySet().iterator(); i.hasNext();) {
            File doc = (File) i.next();
            Integer num = (Integer) docTermFreqMap.get(doc);
            buf.append(doc.getName() + "=" + num + ":");
        }
        rowData.add(buf.toString());
        buf = new StringBuffer("");
        for (Iterator i = inputDocTermFreqMap.keySet().iterator(); i.hasNext();) {
            File doc = (File) i.next();
            Integer num = (Integer) inputDocTermFreqMap.get(doc);
            buf.append(doc.getName() + "=" + num + ":");
        }
        rowData.add(buf.toString());
        buf = new StringBuffer("");
        for (Iterator i = upperConceptSet.iterator(); i.hasNext();) {
            String concept = (String) i.next();
            buf.append(concept + ":");
        }
        rowData.add(buf.toString());
        return rowData;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(word + "\t");
        for (Iterator i = posSet.iterator(); i.hasNext();) {
            String pos = (String) i.next();
            buf.append(pos + ":");
        }
        buf.append("\t");
        buf.append(getTF() + "\t");
        buf.append(getIDF() + "\t");
        buf.append(getTFIDF() + "\t");
        for (Iterator i = docTermFreqMap.keySet().iterator(); i.hasNext();) {
            File file = (File) i.next();
            Integer num = (Integer) docTermFreqMap.get(file);
            buf.append(file.getName() + "=" + num + ":");
        }
        buf.append("\t");
        for (Iterator i = inputDocTermFreqMap.keySet().iterator(); i.hasNext();) {
            File doc = (File) i.next();
            Integer num = (Integer) inputDocTermFreqMap.get(doc);
            buf.append(doc.getName() + "=" + num + ":");
        }
        buf.append("\t");
        for (Iterator i = upperConceptSet.iterator(); i.hasNext();) {
            String concept = (String) i.next();
            buf.append(concept + ":");
        }
        return buf.toString();
    }
}
