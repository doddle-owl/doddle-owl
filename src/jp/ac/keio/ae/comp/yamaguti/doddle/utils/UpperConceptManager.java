package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class UpperConceptManager {

    private static Map<String, String> upperConceptLabelURIMap;
    public static String UPPER_CONCEPT_LIST = "C:/DODDLE-OWL/upperConceptList.txt";

    public static void makeUpperOntologyList() {
        upperConceptLabelURIMap = new TreeMap<String, String>();

        File file = new File(UPPER_CONCEPT_LIST);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] labelAndURI = line.replaceAll("\n", "").split(",");
                    // System.out.println(labelAndURI[0] + ":" + labelAndURI[1]
                    // + ": " + labelAndURI[0].indexOf("//"));
                    if (labelAndURI[0].indexOf("//") == -1) {
                        upperConceptLabelURIMap.put(labelAndURI[0], labelAndURI[1]);
                    }
                }
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public static boolean hasUpperConceptLabelSet() {
        if (upperConceptLabelURIMap.size() == 0) { return false; }
        return true;
    }

    public static Set<String> getUpperConceptLabelSet(String word) {
        Set<String> uriSet = DODDLEDic.getURISet(word);
        Set<String> upperURISet = new HashSet<String>();
        for (String uri : uriSet) {
            String ns = Utils.getNameSpace(uri);
            String id = Utils.getLocalName(uri);
            upperURISet.add(uri);
            if (ns.equals(DODDLEConstants.EDR_URI)) {
                for (List<String> path : EDRTree.getEDRTree().getURIPathToRootSet(id)) {
                    upperURISet.addAll(path);
                }
            } else if (ns.equals(DODDLEConstants.EDRT_URI)) {
                for (List<String> path : EDRTree.getEDRTTree().getURIPathToRootSet(id)) {
                    upperURISet.addAll(path);
                }
            } else if (ns.equals(DODDLEConstants.WN_URI)) {
                for (List<String> path : WordNetDic.getURIPathToRootSet(new Long(id))) {
                    upperURISet.addAll(path);
                }
            } else {
                for (List<String> path : OWLOntologyManager.getURIPathToRootSet(uri)) {
                    upperURISet.addAll(path);
                }
            }
        }
        Set<String> upperConceptLabelSet = new HashSet<String>();
        for (Entry<String, String> entry : upperConceptLabelURIMap.entrySet()) {
            String ucLabel = entry.getKey();
            String ucURI = entry.getValue();
            if (upperURISet.contains(ucURI)) {
                upperConceptLabelSet.add(ucLabel);
            }
        }
        return upperConceptLabelSet;
    }
}
