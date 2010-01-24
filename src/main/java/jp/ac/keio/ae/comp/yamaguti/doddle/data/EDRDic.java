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

import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class EDRDic {
    private static String TREE_DATA_FILE = "tree.data";
    private static String WORD_DATA_FILE = "word.data";
    private static String CONCEPT_DATA_FILE = "concept.data";
    private static String RELATION_DATA_FILE = "relation.data";

    private static String TREE_INDEX_FILE = "tree.index";
    private static String WORD_INDEX_FILE = "word.index";
    private static String CONCEPT_INDEX_FILE = "concept.index";
    private static String RELATION_INDEX_FILE = "relation.index";

    private static RandomAccessFile edrTreeDataFile;
    private static RandomAccessFile edrWordDataFile;
    private static RandomAccessFile edrConceptDataFile;
    private static RandomAccessFile edrRelationDataFile;

    private static RandomAccessFile edrTreeIndexFile;
    private static RandomAccessFile edrWordIndexFile;
    private static RandomAccessFile edrConceptIndexFile;
    private static RandomAccessFile edrRelationIndexFile;

    private static RandomAccessFile edrtTreeDataFile;
    private static RandomAccessFile edrtWordDataFile;
    private static RandomAccessFile edrtConceptDataFile;

    private static RandomAccessFile edrtTreeIndexFile;
    private static RandomAccessFile edrtWordIndexFile;
    private static RandomAccessFile edrtConceptIndexFile;

    private static Map<String, Concept> edrURIConceptMap; // キャッシュ用
    private static Map<String, Concept> edrtURIConceptMap; // キャッシュ用
    private static Map<String, Set<String>> edrWordIDSetMap; // キャッシュ用
    private static Map<String, Set<String>> edrtWordIDSetMap; // キャッシュ用

    public static boolean initEDRDic() {
        if (edrURIConceptMap != null) { return true; }
        edrURIConceptMap = new HashMap<String, Concept>();
        edrWordIDSetMap = new HashMap<String, Set<String>>();
        String baseDir = DODDLEConstants.EDR_HOME + File.separator;
        try {
            edrTreeDataFile = new RandomAccessFile(baseDir + TREE_DATA_FILE, "r");
            edrWordDataFile = new RandomAccessFile(baseDir + WORD_DATA_FILE, "r");
            edrConceptDataFile = new RandomAccessFile(baseDir + CONCEPT_DATA_FILE, "r");
            edrRelationDataFile = new RandomAccessFile(baseDir + RELATION_DATA_FILE, "r");

            edrTreeIndexFile = new RandomAccessFile(baseDir + TREE_INDEX_FILE, "r");
            edrWordIndexFile = new RandomAccessFile(baseDir + WORD_INDEX_FILE, "r");
            edrConceptIndexFile = new RandomAccessFile(baseDir + CONCEPT_INDEX_FILE, "r");
            edrRelationIndexFile = new RandomAccessFile(baseDir + RELATION_INDEX_FILE, "r");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean initEDRTDic() {
        if (edrtURIConceptMap != null) { return true; }
        edrtURIConceptMap = new HashMap<String, Concept>();
        edrtWordIDSetMap = new HashMap<String, Set<String>>();
        String baseDir = DODDLEConstants.EDRT_HOME + File.separator;
        try {
            edrtTreeDataFile = new RandomAccessFile(baseDir + TREE_DATA_FILE, "r");
            edrtWordDataFile = new RandomAccessFile(baseDir + WORD_DATA_FILE, "r");
            edrtConceptDataFile = new RandomAccessFile(baseDir + CONCEPT_DATA_FILE, "r");

            edrtTreeIndexFile = new RandomAccessFile(baseDir + TREE_INDEX_FILE, "r");
            edrtWordIndexFile = new RandomAccessFile(baseDir + WORD_INDEX_FILE, "r");
            edrtConceptIndexFile = new RandomAccessFile(baseDir + CONCEPT_INDEX_FILE, "r");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    private static long getIndexFpListSize(boolean isSpecial) {
        RandomAccessFile indexFpListFile = null;
        if (isSpecial) {
            indexFpListFile = edrtWordIndexFile;
        } else {
            indexFpListFile = edrWordIndexFile;
        }
        try {
            return indexFpListFile.length() / 10;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    private static long getIndexFp(long fp, boolean isSpecial) {
        RandomAccessFile indexFpListFile = null;
        if (isSpecial) {
            indexFpListFile = edrtWordIndexFile;
        } else {
            indexFpListFile = edrWordIndexFile;
        }
        try {
            indexFpListFile.seek(fp);
            String fpStr = indexFpListFile.readLine();
            if (fpStr == null) { return -1; }
            return Long.valueOf(fpStr);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    private static long getIndexFileSize(RandomAccessFile indexFile) {
        try {
            return indexFile.length() / 10;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    private static long getConceptIndexFileSize(boolean isSpecial) {
        RandomAccessFile indexFile = null;
        if (isSpecial) {
            indexFile = edrtConceptIndexFile;
        } else {
            indexFile = edrConceptIndexFile;
        }
        return getIndexFileSize(indexFile);
    }

    private static long getTreeIndexFileSize(boolean isSpecial) {
        RandomAccessFile indexFile = null;
        if (isSpecial) {
            indexFile = edrtTreeIndexFile;
        } else {
            indexFile = edrTreeIndexFile;
        }
        return getIndexFileSize(indexFile);
    }

    private static long getRelationIndexFileSize() {
        return getIndexFileSize(edrRelationIndexFile);
    }

    private static long getDataFp(long fp, RandomAccessFile indexFile) {
        try {
            indexFile.seek(fp);
            return Long.valueOf(indexFile.readLine());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    private static long getConceptDataFp(long fp, boolean isSpecial) {
        RandomAccessFile indexFile = null;
        if (isSpecial) {
            indexFile = edrtConceptIndexFile;
        } else {
            indexFile = edrConceptIndexFile;
        }
        return getDataFp(fp, indexFile);
    }

    private static long getTreeDataFp(long fp, boolean isSpecial) {
        RandomAccessFile indexFile = null;
        if (isSpecial) {
            indexFile = edrtTreeIndexFile;
        } else {
            indexFile = edrTreeIndexFile;
        }
        return getDataFp(fp, indexFile);
    }

    private static long getRelationDataFp(long fp) {
        return getDataFp(fp, edrRelationIndexFile);
    }

    private static String getTermAndIndexFpSet(long ifp, boolean isSpecial) {
        RandomAccessFile indexFile = null;
        if (isSpecial) {
            indexFile = edrtWordDataFile;
        } else {
            indexFile = edrWordDataFile;
        }
        try {
            // System.out.println("ifp: " + ifp);
            indexFile.seek(ifp);
            return new String(indexFile.readLine().getBytes("ISO8859_1"), "UTF-8");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private static String getData(long dfp, RandomAccessFile dataFile, String encoding) {
        try {
            // System.out.println("dfp: " + dfp);
            dataFile.seek(dfp);
            return new String(dataFile.readLine().getBytes("ISO8859_1"), encoding);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private static String getConceptData(long dfp, boolean isSpecial) {
        RandomAccessFile dataFile = null;
        if (isSpecial) {
            dataFile = edrtConceptDataFile;
        } else {
            dataFile = edrConceptDataFile;
        }
        return getData(dfp, dataFile, "UTF-8");
    }

    private static String getTreeData(long dfp, boolean isSpecial) {
        RandomAccessFile dataFile = null;
        if (isSpecial) {
            dataFile = edrtTreeDataFile;
        } else {
            dataFile = edrTreeDataFile;
        }
        return getData(dfp, dataFile, "ISO8859_1");
    }

    private static String getRelationData(long dfp) {
        return getData(dfp, edrRelationDataFile, "ISO8859_1");
    }

    public static String getConceptData(boolean isSpecial, String id) {
        long low = 0;
        long conceptIndexFileSize = getConceptIndexFileSize(isSpecial);
        long high = conceptIndexFileSize;
        while (low <= high) {
            long mid = (low + high) / 2;
            if (conceptIndexFileSize - 1 <= mid) { return null; }
            long conceptDataFP = getConceptDataFp(mid * 10, isSpecial);
            if (conceptDataFP == -1) { return null; }
            // System.out.println("mid: " + mid);
            String conceptData = getConceptData(conceptDataFP, isSpecial);
            if (conceptData == null) { return null; }
            String[] lines = conceptData.split("\t");
            String searchedID = lines[0];
            // System.out.println(searchedID.compareTo(id));
            if (searchedID.compareTo(id) == 0) {
                // System.out.println(conceptData);
                return conceptData;
            } else if (0 < searchedID.compareTo(id)) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return null;
    }

    public static String getTreeData(boolean isSpecial, String id) {
        long low = 0;
        long treeIndexFileSize = getTreeIndexFileSize(isSpecial);
        long high = treeIndexFileSize;
        while (low <= high) {
            long mid = (low + high) / 2;
            // System.out.println("mid: " + mid);
            if (treeIndexFileSize - 1 <= mid) { return null; }
            long treeDataFP = getTreeDataFp(mid * 10, isSpecial);
            if (treeDataFP == -1) { return null; }
            String treeData = getTreeData(treeDataFP, isSpecial);
            if (treeData == null) { return null; }
            String[] lines = treeData.split("\t");
            String searchedID = lines[0];
            // System.out.println(searchedID.compareTo(id));
            if (searchedID.compareTo(id) == 0) {
                // System.out.println(conceptData);
                return treeData;
            } else if (0 < searchedID.compareTo(id)) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return null;
    }

    public static String getRelationData(String id) {
        long low = 0;
        long relationIndexFileSize = getRelationIndexFileSize();
        long high = relationIndexFileSize;

        while (low <= high) {
            long mid = (low + high) / 2;
            if (relationIndexFileSize - 1 <= mid) { return null; }
            long relationDataFP = getRelationDataFp(mid * 10);
            if (relationDataFP == -1) { return null; }
            String relationData = getRelationData(relationDataFP);
            if (relationData == null) { return null; }
            String[] lines = relationData.split("\t");
            String searchedID = lines[0];
            // System.out.println(searchedID.compareTo(id));
            if (searchedID.compareTo(id) == 0) {
                // System.out.println(conceptData);
                return relationData;
            } else if (0 < searchedID.compareTo(id)) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return null;
    }

    private static Concept getConcept(long dfp, boolean isSpecial) {
        RandomAccessFile dataFile = null;
        try {
            if (isSpecial) {
                dataFile = edrtConceptDataFile;
            } else {
                dataFile = edrConceptDataFile;
            }
            dataFile.seek(dfp);
            String data = new String(dataFile.readLine().getBytes("ISO8859_1"), "UTF-8");
            // System.out.println(data);
            String[] dataArray = data.split("\\^");
            String[] conceptData = new String[4];
            String id = dataArray[0].replaceAll("\t", "");
            System.arraycopy(dataArray, 1, conceptData, 0, conceptData.length);

            String uri = "";
            Concept c = null;
            if (isSpecial) {
                uri = DODDLEConstants.EDRT_URI + id;
                c = new Concept(uri, conceptData);
                edrtURIConceptMap.put(uri, c);
            } else {
                uri = DODDLEConstants.EDR_URI + id;
                c = new Concept(uri, conceptData);
                edrURIConceptMap.put(uri, c);
            }
            return c;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private static Set<Long> getdataFpSet(boolean isSpecial, long high, String term) {
        long low = 0;
        Set<Long> dataFpSet = new HashSet<Long>();
        while (low <= high) {
            long mid = (low + high) / 2;
            // System.out.println("mid: " + mid);
            long indexFP = getIndexFp(mid * 10, isSpecial);
            if (indexFP == -1) { return dataFpSet; }
            String line = getTermAndIndexFpSet(indexFP, isSpecial);
            String[] lines = line.split("\t");
            String searchedTerm = lines[0];
            // System.out.println(searchedTerm.compareTo(term));
            if (searchedTerm.compareTo(term) == 0) {
                for (int i = 1; i < lines.length; i++) {
                    dataFpSet.add(Long.valueOf(lines[i]));
                }
                // System.out.println(searchedTerm);
                return dataFpSet;
            } else if (0 < searchedTerm.compareTo(term)) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return dataFpSet;
    }

    public static Set<String> getIDSet(String word, boolean isSpecial) {
        Map<String, Set<String>> wordIDSetMap = null;
        if (isSpecial) {
            wordIDSetMap = edrtWordIDSetMap;
        } else {
            wordIDSetMap = edrWordIDSetMap;
        }

        if (wordIDSetMap.get(word) != null) { return wordIDSetMap.get(word); }
        Set<Long> dataFpSet = getdataFpSet(isSpecial, getIndexFpListSize(isSpecial), word);
        // System.out.println(dataFpSet);
        Set<String> idSet = new HashSet<String>();
        for (Long dfp : dataFpSet) {
            // System.out.println(dfp);
            Concept c = getConcept(dfp, isSpecial);
            // System.out.println(c.getLocalName());
            idSet.add(c.getLocalName());
        }
        wordIDSetMap.put(word, idSet);

        return idSet;
    }

    public static Set<String> getEDRTIDSet(String word) {
        return getIDSet(word, true);
    }

    public static Set<String> getEDRIDSet(String word) {
        return getIDSet(word, false);
    }

    private static void addURISet(String data, String relation, Set<String> uriSet) {
        String[] idSet = data.split("\\|" + relation)[1].split("\t");
        for (String id : idSet) {
            if (id.indexOf("|") != -1) {
                break;
            }
            if (!id.equals("")) {
                uriSet.add(DODDLEConstants.EDR_URI + id);
            }
        }
    }

    /**
     * 
     * 入力概念集合を入力として，その中から動詞的概念の集合を返す
     * 
     */
    public static Set<Concept> getVerbConceptSet(Set<Concept> inputConceptSet) {
        Set<Concept> verbConceptSet = new HashSet<Concept>();
        for (Concept c : inputConceptSet) {
            String id = c.getLocalName();
            String data = getRelationData(id);
            if (data != null && (data.indexOf("|agent") != -1 || data.indexOf("|object") != -1)) { // agentとobjectの場合のみを考慮
                verbConceptSet.add(c);
            }
        }
        return verbConceptSet;
    }

    public static Set<String> getRelationValueSet(String relation, String vid, List<List<Concept>> trimmedConceptList) {
        Set<String> uriSet = new HashSet<String>();
        String data = getRelationData(vid);
        if (data != null) {
            if (data.indexOf("|" + relation) == -1) { return uriSet; }
            addURISet(data, relation, uriSet);
        }
        for (List<Concept> conceptList : trimmedConceptList) {
            for (Concept c : conceptList) {
                String tid = c.getLocalName();
                data = getRelationData(tid);
                if (data == null) {
                    continue;
                }
                if (data.indexOf("|" + relation) == -1) {
                    continue;
                }
                addURISet(data, relation, uriSet);
            }
        }
        return uriSet;
    }

    public static Concept getConcept(String id, boolean isSpecial) {
        String ns = "";
        Map<String, Concept> uriConceptMap = null;
        if (isSpecial) {
            ns = DODDLEConstants.EDRT_URI;
            uriConceptMap = edrtURIConceptMap;
        } else {
            ns = DODDLEConstants.EDR_URI;
            uriConceptMap = edrURIConceptMap;
        }

        String uri = ns + id;
        if (uriConceptMap.get(uri) != null) { return uriConceptMap.get(uri); }
        String data = getConceptData(isSpecial, id);
        // System.out.println(id+": "+data);
        String[] dataArray = data.split("\\^");
        String[] conceptData = new String[4];
        System.arraycopy(dataArray, 1, conceptData, 0, conceptData.length);

        Concept c = new Concept(uri, conceptData);
        uriConceptMap.put(uri, c);
        return c;
    }

    public static Concept getEDRTConcept(String id) {
        return getConcept(id, true);
    }

    public static Concept getEDRConcept(String id) {
        return getConcept(id, false);
    }

    public static void main(String[] args) throws Exception {
        DODDLEConstants.WORDNET_HOME = "C:/program files (x86)/wordnet/2.0/dict/";
        WordNetDic dic = WordNetDic.getInstance();
        Set<String> idSet = new HashSet<String>();
        Set<String> uriSet = dic.getURISet("operation");
        for (String u : uriSet) {
            String id = Utils.getLocalName(u);
            Set<Set<String>> set = dic.getSubIDSet(Long.parseLong(id));
            for (Set s : set) {
                idSet.addAll(s);
            }
        }
        System.out.println(idSet.size());
        Set<Concept> conceptSet = new HashSet<Concept>();
        for (String id : idSet) {
            Set<List<Concept>> pathSet = dic.getPathToRootSet(Long.parseLong(id));
            for (List<Concept> cSet : pathSet) {
                conceptSet.addAll(cSet);
            }
            // System.out.println(dic.getPathToRootSet(Long.parseLong(id)));
        }
        for (Concept c : conceptSet) {
            // System.out.println(c.getWord()+","+ c.getURI());
            System.out.println(c.getWord());
        }

        // System.out.println(uriSet.size());
        // EDRDic.initEDRDic();
        // String work1 = "ID201db6";
        // String work2 = "ID3ce800";
        // String work3 = "ID3cf224";
        // String work4 = "ID444b43";
        //        
        // Concept c = EDRDic.getConcept("ID201db6", false);
        // System.out.println(c);
        // Set<String> idSet = new HashSet<String>();
        // BufferedReader reader = new BufferedReader(new InputStreamReader(new
        // FileInputStream(DODDLEConstants.EDR_HOME
        // + "tree.data"), "UTF-8"));
        // while (reader.ready()) {
        // String line = reader.readLine();
        // if (line.indexOf(work4) != -1) {
        // String id = line.split("\t\\|")[0];
        // idSet.add(id);
        // }
        // }
        // System.out.println(idSet);
        // for (String id : idSet) {
        // c = EDRDic.getConcept(id, false);
        // System.out.println(c);
        // }
    }
}
