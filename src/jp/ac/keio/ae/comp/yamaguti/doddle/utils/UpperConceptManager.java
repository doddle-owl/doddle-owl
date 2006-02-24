package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class UpperConceptManager {

    private static Map upperConceptLabelIDMap;
    public static String UPPER_CONCEPT_LIST = "./upperConceptList.txt";

    public static Set getUpperConceptLabelSet() {
        return upperConceptLabelIDMap.keySet();
    }

    public static Set getWordSet(String ucLabel) {
        return (Set) upperConceptLabelIDMap.get(ucLabel);
    }

    public static void makeUpperOntologyList() {
        if (upperConceptLabelIDMap != null) { return; }
        upperConceptLabelIDMap = new TreeMap();

        File file = new File(UPPER_CONCEPT_LIST);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "JISAutoDetect"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    String[] labelAndID = line.replaceAll("\n", "").split(",");
                    if (labelAndID[0].indexOf('#') == -1) {
                        upperConceptLabelIDMap.put(labelAndID[0], getSubWordSet(labelAndID[1]));
                    }
                }
                reader.close();
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static Set getSubWordSet(String upperID) {
        Set idSet = new HashSet();
        Set subIDSet = EDRTree.getInstance().getSubIDsSet(upperID);
        for (Iterator i = subIDSet.iterator(); i.hasNext();) {
            idSet.addAll((Set) i.next());
        }
        idSet.add(upperID);
        Set wordSet = new HashSet();
        for (Iterator i = idSet.iterator(); i.hasNext();) {
            String id = (String) i.next();
            Concept c = EDRDic.getEDRConcept(id);
            if (c == null) {
                continue;
            }
            if (c.getJpWord().length() != 0) {
                wordSet.addAll(new HashSet(Arrays.asList(c.getJpWords())));
            }
            if (c.getEnWord().length() != 0) {
                wordSet.addAll(new HashSet(Arrays.asList(c.getEnWords())));
            }
        }
        return wordSet;
    }

}
