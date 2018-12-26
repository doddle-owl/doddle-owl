/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 *
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.doddle_owl.utils;

import org.doddle_owl.models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Takeshi Morita
 */
public class UpperConceptManager {

    private static Map<String, String> upperConceptLabelURIMap;
    public static String UPPER_CONCEPT_LIST = "C:/DODDLE-OWL/upperConceptList.txt";

    public static void makeUpperOntologyList() {
        upperConceptLabelURIMap = new TreeMap<>();

        File file = new File(UPPER_CONCEPT_LIST);
        if (file.exists()) {
            try {
                BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
                try (reader) {
                    while (reader.ready()) {
                        String line = reader.readLine();
                        String[] labelAndURI = line.replaceAll("\n", "").split(",");
                        // System.out.println(labelAndURI[0] + ":" + labelAndURI[1]
                        // + ": " + labelAndURI[0].indexOf("//"));
                        if (!labelAndURI[0].contains("//")) {
                            upperConceptLabelURIMap.put(labelAndURI[0], labelAndURI[1]);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasUpperConceptLabelSet() {
        return upperConceptLabelURIMap.size() != 0;
    }

    public static Set<String> getUpperConceptLabelSet(String word) {
        Set<String> uriSet = DODDLEDic.getURISet(word);
        Set<String> upperURISet = new HashSet<>();
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
                for (List<String> path : WordNetDic.getURIPathToRootSet(Long.valueOf(id))) {
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
        Set<String> upperConceptLabelSet = new HashSet<>();
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
