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
public class ConceptDefinition {

    private Map<String, Set<String>> agentMap;
    private Map<String, Set<String>> objectMap;
    private Map<String, Set<String>> goalMap;
    private Map<String, Set<String>> implementMap;
    private Map<String, Set<String>> a_objectMap;
    private Map<String, Set<String>> placeMap;
    private Map<String, Set<String>> sceneMap;
    private Map<String, Set<String>> causeMap;

    private Set<String> verbSet;

    private Map<String, Map<String, Set<String>>> relationMap;
    public static final String[] relationList = { "agent", "object", "goal", "implement", "a-object", "place", "scene",
            "cause"};
    private static ConceptDefinition conceptDefintion;

    public static ConceptDefinition getInstance() {
        if (conceptDefintion == null) {
            conceptDefintion = new ConceptDefinition();
        }
        return conceptDefintion;
    }

    private ConceptDefinition() {

        agentMap = new HashMap<String, Set<String>>();
        objectMap = new HashMap<String, Set<String>>();
        goalMap = new HashMap<String, Set<String>>();
        implementMap = new HashMap<String, Set<String>>();
        a_objectMap = new HashMap<String, Set<String>>();
        placeMap = new HashMap<String, Set<String>>();
        sceneMap = new HashMap<String, Set<String>>();
        causeMap = new HashMap<String, Set<String>>();
        verbSet = new TreeSet<String>();
        Map[] relationMapList = { agentMap, objectMap, goalMap, implementMap, a_objectMap, placeMap, sceneMap, causeMap};
        relationMap = new HashMap<String, Map<String, Set<String>>>();
        for (int i = 0; i < relationList.length; i++) {
            relationMap.put(relationList[i], relationMapList[i]);
        }
    }

    public void printMap() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("conceptDescription3.1ex.txt"));
            int n = 0;
            // System.out.println(verbSet.size());
            for (String verbID : verbSet) {
                StringBuffer buf = new StringBuffer("");
                buf.append(verbID + "||");
                for (String relation : relationList) {
                    buf.append(relation + "\t");
                    Map<String, Set<String>> map = relationMap.get(relation);
                    if (map.get(verbID) == null) {
                        buf.append("||");
                    } else {
                        Set<String> idSet = map.get(verbID);
                        for (String id : idSet) {
                            buf.append(id + "\t");
                        }
                    }
                }
                System.out.println(n++);
                buf.append("\n");
                writer.write(buf.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    private void putVerbID(Map<String, Set<String>> map, String verbID, Set<String> idSet) {
        if (idSet.size() == 0) { return; }
        map.put(verbID, idSet);
    }

    private void putExpandVerbID(Map<String, Set<String>> map, String verbID, Set<String> idSet) {
        if (idSet.size() == 0) { return; }
        Set<String> verbIDSet = getSubURISet(verbID);
        verbIDSet.add(verbID);
        for (String vid : verbIDSet) {
            verbSet.add(vid);
            // 下位動詞的概念を登録
            // 登録されていたら，さらにidSetを追加する
            if (map.get(vid) == null) {
                map.put(vid, idSet);
            } else {
                Set<String> set = map.get(vid);
                set.addAll(idSet);
                map.put(vid, set);
            }
        }
    }

    public Set<String> makeIDSet(String[] idArray) {
        Set<String> idSet = new HashSet<String>();
        if (1 < idArray.length) {
            for (String id : idArray) {
                idSet.add(id);
            }
        }
        return idSet;
    }

    public Set<String> makeExpandIDSet(String[] uriArray) {
        Set<String> uriSet = new HashSet<String>();
        if (1 < uriArray.length) {
            for (String uri : uriArray) {
                uriSet.add(uri);
                uriSet.addAll(getSubURISet(uri));
            }
        }
        return uriSet;
    }

    /**
     * IDと関係子を受け取り，関係の値としてIDを含む動詞的概念の セットを返す
     */
    public Set<String> getVerbIDSet(String id, String relation) {
        Set<String> verbIDSet = new HashSet<String>();
        Map<String, Set<String>> map = relationMap.get(relation);
        for (Entry<String, Set<String>> entry : map.entrySet()) {
            String verbID = entry.getKey();
            Set<String> idSet = entry.getValue();
            if (idSet.contains(id)) {
                verbIDSet.add(verbID);
                continue;
            }
            // マッチしない場合には，対象概念の上位概念に概念集合マッチするかどうかを調べていく
            Set<String> supURISet = getSupURISet(id);
            for (String tid : idSet) {
                if (supURISet.contains(tid)) {
                    verbIDSet.add(verbID);
                    break;
                }
            }
        }
        return verbIDSet;
    }

    /**
     * idを受け取り，そのIDのルートまでのパスに存在するURIのセットを返す
     */
    public Set<String> getSupURISet(String id) {
        Set<String> uriSet = new HashSet<String>();
        for (List<String> supIDList : EDRTree.getEDRTree().getURIPathToRootSet(id)) {
            uriSet.addAll(supIDList);
        }
        return uriSet;
    }

    /**
     * idを受け取り，そのIDの下位に存在するURIのセットを返す
     * 
     * @param uri
     */
    public Set<String> getSubURISet(String uri) {
        Set<String> uriSet = new HashSet<String>();
        for (Set<String> subURISet : EDRTree.getEDRTree().getSubURISet(uri)) {
            uriSet.addAll(subURISet);
        }
        return uriSet;
    }

    /**
     * 動詞的概念と関係子を引数として受け取り，動詞的概念と関係子を 通して関係のある名詞的概念のセットを返す
     * 
     * @param verbID
     * @param relation
     */
    public Set<String> getIDSet(String verbID, String relation) {
        Map<String, Set<String>> map = relationMap.get(relation);
        Set<String> idSet = map.get(verbID);
        return idSet;
    }

    public Set<String> getURISet(String relation, String verbID, List<List<Concept>> trimmedConceptList) {
        Map<String, Set<String>> map = relationMap.get(relation);
        // System.out.println(relation + " map key size: " +
        // map.keySet().size());
        Set<String> uriSet = new HashSet<String>();
        if (map.get(verbID) != null) {
            Set<String> idSet = map.get(verbID);
            for (String id : idSet) {
                uriSet.add(DODDLEConstants.EDR_URI + id);
            }
        }
        for (List<Concept> list : trimmedConceptList) {
            for (Concept trimmedConcept : list) {
                if (map.get(trimmedConcept.getLocalName()) != null) {
                    Set<String> idSet = map.get(trimmedConcept.getLocalName());
                    for (String id : idSet) {
                        uriSet.add(DODDLEConstants.EDR_URI + id);
                    }
                }
            }
        }
        if (0 < uriSet.size()) {
            System.out.println("org uriset size: " + uriSet.size());
        }
        return uriSet;
    }

    public static void main(String[] args) {
        // EDRTree.init();
        ConceptDefinition cd = ConceptDefinition.getInstance();
        // System.out.println("init done");
        Set<String> verbIDSet = cd.getVerbIDSet("ID0a78fb", "agent");
        // System.out.println(verbIDSet.size());
        for (String verbID : verbIDSet) {
            System.out.println("verb id: " + verbID);
        }
        Set<String> idSet = cd.getIDSet("ID061c7d", "agent");
        for (String id : idSet) {
            System.out.println("id: " + id);
        }
    }
}
