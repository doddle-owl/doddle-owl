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
            } else if (ns.equals(DODDLEConstants.JPN_WN_URI)) {
                for (List<String> path : JPNWNTree.getJPNWNTree().getURIPathToRootSet(id)) {
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
