package jp.ac.keio.ae.comp.yamaguti.doddle.taskanalyzer;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/*
 * @(#)  2007/07/25
 */

/**
 * @author takeshi morita
 */
public class CabochaDocument {

    private String docName;
    private Document document;
    private List<Sentence> sentenceList;
    private Set<Segment> segmentSet;
    private Map<String, Integer> compoundWordCountMap;
    private Map<String, Integer> compoundWordWithNokakuCountMap;
    private Map<Segment, Set<Segment>> segmentMap;
    private Process cabochaProcess;

    public CabochaDocument(Process cp) {
        cabochaProcess = cp;
        sentenceList = new ArrayList<Sentence>();
        segmentSet = new HashSet<Segment>();
        compoundWordCountMap = new HashMap<String, Integer>();
        compoundWordWithNokakuCountMap = new HashMap<String, Integer>();
        segmentMap = new HashMap<Segment, Set<Segment>>();
    }

    public CabochaDocument(String fname, Process cp) {
        this(cp);
        docName = fname;
        cabochaFileReader();
    }

    public CabochaDocument(Document doc, Process cp) {
        this(cp);
        document = doc;
        cabochaDocReader();
    }

    private void cabochaReader(BufferedReader reader) throws IOException {
        String line = "";
        Segment segment = null;
        Sentence sentence = new Sentence();
        while ((line = reader.readLine()) != null) {
            String[] elems = line.split("\\s");
            if (elems.length == 1) {
                sentence.mergeSegments();
                setSegmentMap(sentence);
                setCompoundWordCountMap(sentence);
                setCompoundWordWithNokakuCountMap(sentence);
                segmentSet.addAll(sentence.getSegmentList());
                sentenceList.add(sentence);
                sentence = new Sentence();
            } else if (elems.length == 5) {
                int num = Integer.parseInt(elems[2].replaceAll("D|O", ""));
                segment = new Segment(num);
                sentence.addSegment(segment);
            } else if (elems.length == 7) {
                Morpheme morpheme = new Morpheme(elems);
                segment.addMorpheme(morpheme);
            }
        }
    }

    private void cabochaFileReader() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(cabochaProcess.getInputStream()));
            cabochaReader(reader);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    private void cabochaDocReader() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(cabochaProcess.getInputStream()));
            cabochaReader(reader);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public String getDocName() {
        return docName;
    }

    public Set<Segment> getSegmentSet() {
        return segmentSet;
    }

    private void setSegmentMap(Sentence sentence) {
        Map<Segment, Set<Segment>> sentenceMap = sentence.getSegmentMap();
        for (Entry<Segment, Set<Segment>> entry : sentenceMap.entrySet()) {
            if (segmentMap.get(entry.getKey()) != null) {
                Set<Segment> segSet = segmentMap.get(entry.getKey());
                segSet.addAll(entry.getValue());
            } else {
                segmentMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void setCompoundWordCountMap(Sentence sentence) {
        Map<String, Integer> sentenceMap = sentence.getCompoundWordCountMap();
        for (Entry<String, Integer> entry : sentenceMap.entrySet()) {
            if (compoundWordCountMap.get(entry.getKey()) != null) {
                compoundWordCountMap.put(entry.getKey(), entry.getValue() + compoundWordCountMap.get(entry.getKey()));
            } else {
                compoundWordCountMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void setCompoundWordWithNokakuCountMap(Sentence sentence) {
        Map<String, Integer> sentenceMap = sentence.getCompoundWordWithNokakuCountMap();
        for (Entry<String, Integer> entry : sentenceMap.entrySet()) {
            if (compoundWordWithNokakuCountMap.get(entry.getKey()) != null) {
                compoundWordWithNokakuCountMap.put(entry.getKey(), entry.getValue()
                        + compoundWordWithNokakuCountMap.get(entry.getKey()));
            } else {
                compoundWordWithNokakuCountMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public List<Sentence> getSentenceList() {
        return sentenceList;
    }

    public Map<Segment, Set<Segment>> getSegmentMap() {
        return segmentMap;
    }

    public Set<String> getCompoundWordSet() {
        return compoundWordCountMap.keySet();
    }

    public Map<String, Integer> getCompoundWordCountMap() {
        return compoundWordCountMap;
    }

    public Set<String> getCompoundWordWithNokakuSet() {
        return compoundWordWithNokakuCountMap.keySet();
    }

    public Map<String, Integer> getCompoundWordWithNokakuCountMap() {
        return compoundWordWithNokakuCountMap;
    }

    public List<PrimitiveTask> getPrimitiveTaskList() {
        List<PrimitiveTask> primitiveTaskList = new ArrayList<PrimitiveTask>();
        for (Sentence sentence : sentenceList) {
            primitiveTaskList.addAll(sentence.getTaskDescriptionSet());
        }
        return primitiveTaskList;
    }

    public void printTaskDescriptions() {
        for (Sentence sentence : sentenceList) {
            System.out.println("(文): " + sentence);
            for (PrimitiveTask taskDescription : sentence.getTaskDescriptionSet()) {
                System.out.println(taskDescription);
            }
            System.out.println("");
        }
    }

    public String toString() {
        return document.getFile().getName() + " sentence size: " + sentenceList.size() + " segment size: "
                + segmentSet.size();
    }
}
