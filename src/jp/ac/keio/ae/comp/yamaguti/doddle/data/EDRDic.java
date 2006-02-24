package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.InputModule.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class EDRDic {

    private static String[] idDefinitionLines;
    private static String[] allIDList;
    private static String[] definitionList;

    private String[] wordIDsLines;
    private static String[] allWordList;
    private static String[] wordIDsList;

    private static Map<String, Concept> idConceptMap;
    private static Map<String, Set<String>> wordIDsMap;
    private static DBManager dbManager;

    public static String ID_DEFINITION_MAP = DODDLE.DODDLE_DIC + "idDefinitionMapforEDR.txt";
    public static String WORD_IDs_MAP = DODDLE.DODDLE_DIC + "wordIDsMapforEDR.txt";

    public static void init() {
        if (DODDLE.IS_USING_DB) {
            try {
                dbManager = new DBManager(true);
            } catch (Exception e) {
                // If an exception reaches this point, the last transaction did
                // not
                // complete. If the exception is RunRecoveryException, follow
                // the Berkeley DB recovery procedures before running again.
                e.printStackTrace();
            }
        } else {
            // System.out.println(GregorianCalendar.getInstance().getTime());
            makeIDDefinitionMap();
            makeWordIDsMap();
            // System.out.println(GregorianCalendar.getInstance().getTime());
        }
    }

    public static Set<String> getIDSet(String word) {
        if (DODDLE.IS_USING_DB) {
            // System.out.println(dbManager.getWordIDsMap().get(word));
            if (dbManager.getWordIDsMap().get(word) == null) { return null; }
            String line = (String) dbManager.getWordIDsMap().get(word);
            return new HashSet<String>(Arrays.asList(line.split(" ")));
        }
        if (wordIDsMap.get(word) != null) { return wordIDsMap.get(word); }
        int index = Arrays.binarySearch(allWordList, word);
        if (index < 0) {
            // System.out.println("none: " + word);
            return null;
        }
        String line = wordIDsList[index];
        String[] attrs = line.split("\t");
        Set<String> idSet = new HashSet<String>();
        for (int j = 0; j < attrs.length; j++) {
            try {
                idSet.add(attrs[j]);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        wordIDsMap.put(word, idSet);
        return idSet;
    }

    public static String getWord(String id) {
        Concept c = getEDRConcept(id);
        if (c != null) { return c.getWord(); }
        return null;
    }

    public static Concept getEDRConcept(String id) {
        if (DODDLE.IS_USING_DB) {
            dbManager.setEDRConcept(id);
            return dbManager.getEDRConcept();
        }
        if (idConceptMap.get(id) != null) { return idConceptMap.get(id); }
        int index = Arrays.binarySearch(allIDList, id);
        if (index < 0) {
            DODDLE.getLogger().log(Level.DEBUG, "Ž«‘‚É‘¶Ý‚µ‚È‚¢ŠT”OID: " + id);
            return null;
        }
        Concept c = new Concept(id, definitionList[index].split("\\^"));
        c.setPrefix("edr");
        idConceptMap.put(id, c);
        return c;
    }

    private static void makeWordIDsMap() {
        wordIDsMap = new HashMap<String, Set<String>>();
        try {
            InputStream inputStream = new FileInputStream(WORD_IDs_MAP);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

            String line = reader.readLine().replaceAll("\n", "");
            // wordIDsLines = line.split("\\^");
            // sortWordIDsMap();
            allWordList = line.split("\t");
            // System.out.println("word size: " + allWordList.length);
            line = reader.readLine().replaceAll("\n", "");
            wordIDsList = line.split("\\|");
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void makeIDDefinitionMap() {
        idConceptMap = new HashMap<String, Concept>();
        try {
            InputStream inputStream = new FileInputStream(ID_DEFINITION_MAP);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF16"));

            String line = reader.readLine().replaceAll("\n", "");
            allIDList = line.split("\\|");
            // System.out.println("id size: " + allIDList.length);
            line = reader.readLine().replaceAll("\n", "");
            definitionList = line.split("\"");

            // System.out.println(definitionList.length);
            // idDefinitionLines = line.split("\"");
            // sortIDDefinitionMap();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortIDDefinitionMap() {
        Arrays.sort(idDefinitionLines, new IDDefinitionLinesComparator());
        try {
            System.out.println("sort");
            OutputStream os = new FileOutputStream("./idDefinitionMapforEDR.txt");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF16"));
            StringBuffer ids = new StringBuffer("");
            StringBuffer definitions = new StringBuffer("");
            for (int i = 0; i < idDefinitionLines.length; i++) {
                String l = idDefinitionLines[i];
                String[] attrs = l.split("\\^");
                ids.append(attrs[0] + "|");
                // System.out.println(attrs[0]);
                for (int j = 1; j < attrs.length; j++) {
                    definitions.append(attrs[j] + "^");
                }
                definitions.append("\"");
            }
            writer.write(ids.toString());
            writer.write("\n");
            writer.write(definitions.toString());
            writer.write("\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sortWordIDsMap() {
        Arrays.sort(wordIDsLines, new WordIDsLinesComparator());
        try {
            System.out.println("words ids lines sort");
            OutputStream os = new FileOutputStream("./wordIDsMapforEDR.txt");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
            StringBuffer words = new StringBuffer("");
            StringBuffer ids = new StringBuffer("");
            for (int i = 0; i < wordIDsLines.length; i++) {
                String l = wordIDsLines[i];
                String[] attrs = l.split("\t");
                words.append(attrs[0] + "\t");
                for (int j = 1; j < attrs.length; j++) {
                    ids.append(attrs[j] + "\t");
                }
                ids.append("|");
            }
            writer.write(words.toString());
            writer.write("\n");
            writer.write(ids.toString());
            writer.write("\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeDB() {
        if (dbManager != null) {
            try {
                // Always attempt to close the database cleanly.
                dbManager.close();
                System.out.println("Close DB");
            } catch (Exception e) {
                System.err.println("Exception during database close:");
                e.printStackTrace();
            }
        }
    }

    public static DBManager getDBManager() {
        return dbManager;
    }
}
