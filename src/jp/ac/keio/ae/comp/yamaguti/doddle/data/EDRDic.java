package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import org.apache.log4j.*;

/**
 * @author takeshi morita
 */
public class EDRDic {

    private static RandomAccessFile edrIndexFile;
    private static RandomAccessFile edrDataFile;
    private static RandomAccessFile edrIndexFpListFile;
    private static RandomAccessFile edrDataFpListFile;

    private static RandomAccessFile edrtIndexFile;
    private static RandomAccessFile edrtDataFile;
    private static RandomAccessFile edrtIndexFpListFile;
    private static RandomAccessFile edrtDataFpListFile;

    private static DBManager edrDBManager;
    private static DBManager edrtDBManager;

    private static Map<String, Concept> edrURIConceptMap; // キャッシュ用
    private static Map<String, Concept> edrtURIConceptMap; // キャッシュ用
    private static Map<String, Set<String>> edrWordIDSetMap; // キャッシュ用
    private static Map<String, Set<String>> edrtWordIDSetMap; // キャッシュ用

    public static String EDR_INDEX_FILE = DODDLEConstants.EDR_HOME + "index.edr";
    public static String EDR_DATA_FILE = DODDLEConstants.EDR_HOME + "data.edr";
    public static String EDR_INDEX_FP_LIST_FILE = DODDLEConstants.EDR_HOME + "index_fp_list.edr";
    public static String EDR_DATA_FP_LIST_FILE = DODDLEConstants.EDR_HOME + "data_fp_list.edr";

    public static String EDRT_INDEX_FILE = DODDLEConstants.EDRT_HOME + "index.edr";
    public static String EDRT_DATA_FILE = DODDLEConstants.EDRT_HOME + "data.edr";
    public static String EDRT_INDEX_FP_LIST_FILE = DODDLEConstants.EDRT_HOME + "index_fp_list.edr";
    public static String EDRT_DATA_FP_LIST_FILE = DODDLEConstants.EDRT_HOME + "data_fp_list.edr";

    public static void resetEDRDicPath() {
        EDRDic.EDR_INDEX_FILE = DODDLEConstants.EDR_HOME + "index.edr";
        EDRDic.EDR_DATA_FILE = DODDLEConstants.EDR_HOME + "data.edr";
        EDRDic.EDR_INDEX_FP_LIST_FILE = DODDLEConstants.EDR_HOME + "index_fp_list.edr";
        EDRDic.EDR_DATA_FP_LIST_FILE = DODDLEConstants.EDR_HOME + "data_fp_list.edr";
    }

    public static void resetEDRTDicPath() {
        EDRDic.EDRT_INDEX_FILE = DODDLEConstants.EDRT_HOME + "index.edr";
        EDRDic.EDRT_DATA_FILE = DODDLEConstants.EDRT_HOME + "data.edr";
        EDRDic.EDRT_INDEX_FP_LIST_FILE = DODDLEConstants.EDRT_HOME + "index_fp_list.edr";
        EDRDic.EDRT_DATA_FP_LIST_FILE = DODDLEConstants.EDRT_HOME + "data_fp_list.edr";
    }

    public static boolean initEDRDic() {
        if (edrURIConceptMap != null) { return true; }
        edrURIConceptMap = new HashMap<String, Concept>();
        if (DODDLEConstants.IS_USING_DB) {
            try {
                edrDBManager = new DBManager(true, DODDLEConstants.EDR_HOME);
                DODDLE.getLogger().log(Level.INFO, "init EDR Concept Classification Dictionary on DB");
            } catch (Exception e) {
                // If an exception reaches this point, the last transaction did
                // not
                // complete. If the exception is RunRecoveryException, follow
                // the Berkeley DB recovery procedures before running again.
                edrURIConceptMap = null;
                DODDLE.getLogger().log(Level.INFO, "cannot open EDR Dic");
                return false;
            }
        } else {
            edrWordIDSetMap = new HashMap<String, Set<String>>();
            try {
                edrIndexFile = new RandomAccessFile(EDR_INDEX_FILE, "r");
                edrDataFile = new RandomAccessFile(EDR_DATA_FILE, "r");
                edrIndexFpListFile = new RandomAccessFile(EDR_INDEX_FP_LIST_FILE, "r");
                edrDataFpListFile = new RandomAccessFile(EDR_DATA_FP_LIST_FILE, "r");
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean initEDRTDic() {
        if (edrtURIConceptMap != null) { return true; }
        edrtURIConceptMap = new HashMap<String, Concept>();
        if (DODDLEConstants.IS_USING_DB) {
            try {
                edrtDBManager = new DBManager(true, DODDLEConstants.EDRT_HOME);
            } catch (Exception e) {
                edrtURIConceptMap = null;
                DODDLE.getLogger().log(Level.INFO, "cannot open EDRT Dic");
                return false;
            }
        } else {
            edrtWordIDSetMap = new HashMap<String, Set<String>>();
            try {
                edrtIndexFile = new RandomAccessFile(EDRT_INDEX_FILE, "r");
                edrtDataFile = new RandomAccessFile(EDRT_DATA_FILE, "r");
                edrtIndexFpListFile = new RandomAccessFile(EDRT_INDEX_FP_LIST_FILE, "r");
                edrtDataFpListFile = new RandomAccessFile(EDRT_DATA_FP_LIST_FILE, "r");
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private static long getIndexFpListSize(boolean isSpecial) {
        RandomAccessFile indexFpListFile = null;
        if (isSpecial) {
            indexFpListFile = edrtIndexFpListFile;
        } else {
            indexFpListFile = edrIndexFpListFile;
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
            indexFpListFile = edrtIndexFpListFile;
        } else {
            indexFpListFile = edrIndexFpListFile;
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

    private static long getDataFpListSize(boolean isSpecial) {
        RandomAccessFile dataFpListFile = null;
        if (isSpecial) {
            dataFpListFile = edrtDataFpListFile;
        } else {
            dataFpListFile = edrDataFpListFile;
        }
        try {
            return dataFpListFile.length() / 10;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    private static long getDataFp(long fp, boolean isSpecial) {
        RandomAccessFile dataFpListFile = null;
        if (isSpecial) {
            dataFpListFile = edrtDataFpListFile;
        } else {
            dataFpListFile = edrDataFpListFile;
        }
        try {
            dataFpListFile.seek(fp);
            return Long.valueOf(dataFpListFile.readLine());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
    }

    private static String getTermAndIndexFpSet(long ifp, boolean isSpecial) {
        RandomAccessFile indexFile = null;
        if (isSpecial) {
            indexFile = edrtIndexFile;
        } else {
            indexFile = edrIndexFile;
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

    private static String getConceptData(long dfp, boolean isSpecial) {
        RandomAccessFile dataFile = null;
        if (isSpecial) {
            dataFile = edrtDataFile;
        } else {
            dataFile = edrDataFile;
        }
        try {
            // System.out.println("dfp: " + dfp);
            dataFile.seek(dfp);
            return new String(dataFile.readLine().getBytes("ISO8859_1"), "UTF-8");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static String getConceptData(boolean isSpecial, long high, String id) {
        long low = 0;
        while (low <= high) {
            long mid = (low + high) / 2;
            // System.out.println("mid: " + mid);
            String conceptData = getConceptData(getDataFp(mid * 10, isSpecial), isSpecial);
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

    private static Concept getConcept(long dfp, boolean isSpecial) {
        RandomAccessFile dataFile = null;
        try {
            if (isSpecial) {
                dataFile = edrtDataFile;
            } else {
                dataFile = edrDataFile;
            }
            dataFile.seek(dfp);
            String data = new String(dataFile.readLine().getBytes("ISO8859_1"), "UTF-8");
            // System.out.println(data);
            String[] dataArray = data.split("\\^");
            String[] conceptData = new String[4];
            String id = dataArray[0].replaceAll("\t", "");
            System.arraycopy(dataArray, 1, conceptData, 0, dataArray.length - 1);

            String uri = "";
            Concept c = null;
            if (isSpecial) {
                uri = DODDLEConstants.EDRT_URI + id;
                c = new Concept(uri, conceptData);
                // edrtURIConceptMap.put(uri, c);
            } else {
                uri = DODDLEConstants.EDR_URI + id;
                c = new Concept(uri, conceptData);
                // edrURIConceptMap.put(uri, c);
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
        DBManager dbManager = null;
        Map<String, Set<String>> wordIDSetMap = null;
        if (isSpecial) {
            wordIDSetMap = edrtWordIDSetMap;
            dbManager = edrtDBManager;
        } else {
            wordIDSetMap = edrWordIDSetMap;
            dbManager = edrDBManager;
        }

        if (DODDLEConstants.IS_USING_DB) {
            if (dbManager.getEDRIDSet(word) == null) { return null; }
            return dbManager.getEDRIDSet(word);
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
        // wordIDSetMap.put(word, idSet);

        return idSet;
    }

    public static Set<String> getEDRTIDSet(String word) {
        return getIDSet(word, true);
    }

    public static Set<String> getEDRIDSet(String word) {
        return getIDSet(word, false);
    }

    public static Concept getConcept(String id, boolean isSpecial) {
        String ns = "";
        DBManager dbManager = null;
        Map<String, Concept> uriConceptMap = null;
        if (isSpecial) {
            ns = DODDLEConstants.EDRT_URI;
            dbManager = edrtDBManager;
            uriConceptMap = edrtURIConceptMap;
        } else {
            ns = DODDLEConstants.EDR_URI;
            dbManager = edrDBManager;
            uriConceptMap = edrURIConceptMap;
        }

        if (DODDLEConstants.IS_USING_DB) {
            String uri = ns + id;
            if (uriConceptMap.get(uri) != null) { return uriConceptMap.get(uri); }
            if (dbManager == null) { return null; }
            dbManager.setEDRConcept(uri);
            Concept c = dbManager.getEDRConcept();
            // uriConceptMap.put(uri, c);
            return c;
        }
        String uri = ns + id;
        if (uriConceptMap.get(uri) != null) { return uriConceptMap.get(uri); }
        String data = getConceptData(isSpecial, getDataFpListSize(isSpecial), id);
        // System.out.println(data);
        String[] dataArray = data.split("\\^");
        String[] conceptData = new String[4];
        System.arraycopy(dataArray, 1, conceptData, 0, dataArray.length - 1);

        Concept c = new Concept(uri, conceptData);
        // uriConceptMap.put(uri, c);
        return c;
    }

    public static Concept getEDRTConcept(String id) {
        return getConcept(id, true);
    }

    public static Concept getEDRConcept(String id) {
        return getConcept(id, false);
    }

    private static void closeDB(DBManager dbManager, String msg) {
        if (dbManager != null) {
            try {
                // Always attempt to close the database cleanly.
                dbManager.close();
                DODDLE.getLogger().log(Level.INFO, "Close " + msg);
            } catch (Exception e) {
                System.err.println("Exception during database close:");
                e.printStackTrace();
            }
        }
    }

    public static void closeDB() {
        closeDB(edrDBManager, "EDR DB");
        closeDB(edrtDBManager, "EDRT DB");
    }

    public static DBManager getEDRDBManager() {
        return edrDBManager;
    }

    public static DBManager getEDRTDBManager() {
        return edrtDBManager;
    }
}
