package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class ConceptDefinition {

    private Map agentMap;
    private Map objectMap;
    private Map goalMap;
    private Map implementMap;
    private Map a_objectMap;
    private Map placeMap;
    private Map sceneMap;
    private Map causeMap;

    private Set<String> verbSet;

    private Map relationMap;
    public static final String[] relationList = { "agent", "object", "goal", "implement", "a-object", "place", "scene",
            "cause"};
    public static String CONCEPT_DEFINITION = DODDLE.DODDLE_DIC + "conceptDefinitionforEDR.txt";

    private static ConceptDefinition conceptDefintion;

    public static ConceptDefinition getInstance() {
        if (conceptDefintion == null) {
            conceptDefintion = new ConceptDefinition();
        }
        return conceptDefintion;
    }

    private ConceptDefinition() {
        agentMap = new HashMap();
        objectMap = new HashMap();
        goalMap = new HashMap();
        implementMap = new HashMap();
        a_objectMap = new HashMap();
        placeMap = new HashMap();
        sceneMap = new HashMap();
        causeMap = new HashMap();

        verbSet = new TreeSet<String>();

        Map[] relationMapList = { agentMap, objectMap, goalMap, implementMap, a_objectMap, placeMap, sceneMap, causeMap};
        relationMap = new HashMap();
        for (int i = 0; i < relationList.length; i++) {
            relationMap.put(relationList[i], relationMapList[i]);
        }

        try {
            InputStream inputStream = new FileInputStream(CONCEPT_DEFINITION);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "Shift_JIS"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] lines = line.replaceAll("\n", "").split("\\|");
                String verbID = lines[0];
                // for (int i = 0; i < relationMapList.length; i++) {
                for (int i = 0; i < 2; i++) { // “––ÊCƒƒ‚ƒŠß–ñ‚Ì‚½‚ß‚Éagent‚Æobject‚Ì‚İ‚ğŠi”[
                    putVerbID(relationMapList[i], verbID, makeIDSet(lines[i + 1].split("\t")));
                }
            }

            // System.out.println(agentMap.size());
            // Set idSet = (Set) agentMap.get("061c7d");
            // for (Iterator i = idSet.iterator(); i.hasNext();) {
            // System.out.println(i.next());
            // }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void printMap() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("conceptDescription3.1ex.txt"));
            int n = 0;
            // System.out.println(verbSet.size());
            for (Iterator i = verbSet.iterator(); i.hasNext();) {
                StringBuffer buf = new StringBuffer("");
                String verbID = (String) i.next();
                buf.append(verbID + "||");
                for (int j = 0; j < relationList.length; j++) {
                    String relation = relationList[j];
                    buf.append(relation + "\t");
                    Map map = (Map) relationMap.get(relation);
                    if (map.get(verbID) == null) {
                        buf.append("||");
                    } else {
                        Set idSet = (Set) map.get(verbID);
                        for (Iterator k = idSet.iterator(); k.hasNext();) {
                            String id = (String) k.next();
                            buf.append(id + "\t");
                        }
                    }
                }
                System.out.println(n++);
                buf.append("\n");
                writer.write(buf.toString());
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putVerbID(Map map, String verbID, Set idSet) {
        if (idSet.size() == 0) { return; }
        map.put(verbID, idSet);
    }

    private void putExpandVerbID(Map map, String verbID, Set idSet) {
        if (idSet.size() == 0) { return; }
        Set<String> verbIDSet = getSubIDSet(verbID);
        verbIDSet.add(verbID);
        for (Iterator i = verbIDSet.iterator(); i.hasNext();) {
            String vid = (String) i.next();
            verbSet.add(vid);
            // ‰ºˆÊ“®Œ“IŠT”O‚ğ“o˜^
            // “o˜^‚³‚ê‚Ä‚¢‚½‚çC‚³‚ç‚ÉidSet‚ğ’Ç‰Á‚·‚é
            if (map.get(vid) == null) {
                map.put(vid, idSet);
            } else {
                Set set = (Set) map.get(vid);
                set.addAll(idSet);
                map.put(vid, set);
            }
        }
    }

    public Set makeIDSet(String[] idArray) {
        Set idSet = new HashSet();
        if (1 < idArray.length) {
            for (int i = 1; i < idArray.length; i++) {
                String id = idArray[i];
                idSet.add(id);
            }
        }
        return idSet;
    }

    public Set makeExpandIDSet(String[] idArray) {
        Set idSet = new HashSet();
        if (1 < idArray.length) {
            for (int i = 1; i < idArray.length; i++) {
                String id = idArray[i];
                idSet.add(id);
                idSet.addAll(getSubIDSet(id));
            }
        }
        return idSet;
    }

    public Set getFromRelationSet(String id) {
        Set relationSet = new TreeSet();
        for (int i = 0; i < relationList.length; i++) {
            String relation = relationList[i];
            if (isVerbIDSet(id, relation)) {
                relationSet.add(relation);
            }
        }
        return relationSet;
    }

    public Set getToRelationSet(String verbID) {
        Set relationSet = new TreeSet();
        for (int i = 0; i < relationList.length; i++) {
            String relation = relationList[i];
            if (isIDSet(verbID, relation)) {
                relationSet.add(relation);
            }
        }
        return relationSet;
    }

    private boolean isVerbIDSet(String id, String relation) {
        Map map = (Map) relationMap.get(relation);
        for (Iterator i = map.values().iterator(); i.hasNext();) {
            Set idSet = (Set) i.next();
            if (idSet.contains(id)) { return true; }
            Set expandIDSet = new HashSet();
            for (Iterator j = idSet.iterator(); j.hasNext();) {
                String tid = (String) j.next();
                expandIDSet.add(tid);
                expandIDSet.addAll(getSubIDSet(tid));
            }
            if (expandIDSet.contains(id)) { return true; }
        }
        return false;
    }

    private boolean isIDSet(String verbID, String relation) {
        Map map = (Map) relationMap.get(relation);
        Set idSet = (Set) map.get(verbID);
        return 0 < idSet.size();
    }

    /**
     * 
     * “ü—ÍŠT”OW‡‚ğ“ü—Í‚Æ‚µ‚ÄC‚»‚Ì’†‚©‚ç“®Œ“IŠT”O‚ÌW‡‚ğ•Ô‚·
     * 
     */
    public Set<Concept> getVerbIDSet(Set<Concept> inputConceptSet) {
        Set<Concept> verbConceptSet = new HashSet<Concept>();
        Set<String> allVerbIDSet = new HashSet<String>();
        // for (Iterator i = inputIDSet.iterator(); i.hasNext();) {
        // String id = (String) i.next();
        //
        // agent‚Æobject‚Ìê‡‚Ì‚İ‚ğl—¶
        // if (agentMap.get(id) != null || objectMap.get(id) != null) {
        // verbIDSet.add(id);
        // allVerbIDSet.addAll(getSubIDSet(id));
        // }
        // }

        /*
         * ‚Æ‚è‚ ‚¦‚¸CˆÚ“®‚Æsˆ×ŠT”O‚Ì‰ºˆÊŠT”O‚É‚Â‚¢‚Ä‚ÍC“®Œ“IŠT”O‚Æ‚İ‚È‚·D‚Ù‚Æ‚ñ‚Ç‚ÌŠT”O‚ÍC
         * ‚±‚Ì‚Q‚Â‚ÌŠT”O‚Ì‰ºˆÊŠT”O‚Ì‚½‚ßD‚»‚êˆÈŠO‚ÌŠT”O‚É‚Â‚¢‚ÄŠT”O‹Lq‚ğQÆ‚µ‚Ä‚µ‚Ü‚¤‚ÆC
         * ƒNƒ‰ƒX‚ÆƒvƒƒpƒeƒB‚Ì‹æ•Ê‚ª‚Â‚©‚È‚­‚È‚Á‚Ä‚µ‚Ü‚¤‚½‚ßC“––Ê‚Í‚±‚ê‚¾‚¯‚ğl—¶‚·‚éD
         */
        allVerbIDSet.addAll(getSubIDSet("30f83e")); // sˆ×‚Ì‰ºˆÊŠT”O‚Í‚·‚×‚Ä“®Œ“IŠT”O‚Æ‚İ‚È‚·
        allVerbIDSet.addAll(getSubIDSet("30f801")); // ˆÚ“®‚Ì‰ºˆÊŠT”O‚Í‚·‚×‚Ä“®Œ“IŠT”O‚Æ‚İ‚È‚·

        for (Concept c : inputConceptSet) {
            if (allVerbIDSet.contains(c.getId())) {
                verbConceptSet.add(c);
            }
            // WordNet‚Ìê‡‚É‚Â‚¢‚Ä‚àC‚±‚±‚Å¯•Ê‚µ‚æ‚¤‚Æv‚¦‚Î‚Å‚«‚é‚Í‚¸D
        }
        return verbConceptSet;
    }

    /**
     * ID‚ÆŠÖŒWq‚ğó‚¯æ‚èCŠÖŒW‚Ì’l‚Æ‚µ‚ÄID‚ğŠÜ‚Ş“®Œ“IŠT”O‚Ì ƒZƒbƒg‚ğ•Ô‚·
     */
    public Set getVerbIDSet(String id, String relation) {
        Set verbIDSet = new HashSet();
        Map map = (Map) relationMap.get(relation);
        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();
            String verbID = (String) entry.getKey();
            Set idSet = (Set) entry.getValue();
            if (idSet.contains(id)) {
                verbIDSet.add(verbID);
                continue;
            }
            // ƒ}ƒbƒ`‚µ‚È‚¢ê‡‚É‚ÍC‘ÎÛŠT”O‚ÌãˆÊŠT”O‚ÉŠT”OW‡ƒ}ƒbƒ`‚·‚é‚©‚Ç‚¤‚©‚ğ’²‚×‚Ä‚¢‚­
            Set supIDSet = getSupIDSet(id);
            for (Iterator j = idSet.iterator(); j.hasNext();) {
                String tid = (String) j.next();
                if (supIDSet.contains(tid)) {
                    verbIDSet.add(verbID);
                    break;
                }
            }
        }
        return verbIDSet;
    }

    public Set getSupIDSet(String id) {
        Set idSet = new HashSet();
        Set supIDSet = EDRTree.getInstance().getPathToRootSet(id);
        for (Iterator i = supIDSet.iterator(); i.hasNext();) {
            idSet.addAll((List) i.next());
        }
        return idSet;
    }

    public Set<String> getSubIDSet(String id) {
        Set<String> idSet = new HashSet<String>();
        Set subIDSet = EDRTree.getInstance().getSubIDsSet(id);
        for (Iterator i = subIDSet.iterator(); i.hasNext();) {
            idSet.addAll((Set) i.next());
        }
        return idSet;
    }

    /**
     * “®Œ“IŠT”O‚ÆŠÖŒWq‚ğˆø”‚Æ‚µ‚Äó‚¯æ‚èC“®Œ“IŠT”O‚ÆŠÖŒWq‚ğ ’Ê‚µ‚ÄŠÖŒW‚Ì‚ ‚é–¼Œ“IŠT”O‚ÌƒZƒbƒg‚ğ•Ô‚·
     * 
     * @param verbID
     * @param relation
     * @return
     */
    public Set getIDSet(String verbID, String relation) {
        Map map = (Map) relationMap.get(relation);
        Set idSet = (Set) map.get(verbID);
        return idSet;
        // Set expandIDSet = new TreeSet();
        // for (Iterator i = idSet.iterator(); i.hasNext();) {
        // String id = (String) i.next();
        // expandIDSet.add(id);
        // expandIDSet.addAll(getSubIDSet(id));
        // }
        // return expandIDSet;
    }

    public Set getIDSet(String relation, String verbID, Set trimmedConceptSet) {
        Map map = (Map) relationMap.get(relation);
        Set idSet = new HashSet();
        if (map.get(verbID) != null) {
            idSet.addAll((Set) map.get(verbID));
        }
        for (Iterator i = trimmedConceptSet.iterator(); i.hasNext();) {
            Concept c = (Concept) i.next();
            if (map.get(c.getId()) != null) {
                idSet.addAll((Set) map.get(c.getId()));
            }
        }
        return idSet;
    }

    public static void main(String[] args) {
        // EDRTree.init();
        ConceptDefinition cd = ConceptDefinition.getInstance();
        // System.out.println("init done");
        Set verbIDSet = cd.getVerbIDSet("0a78fb", "agent");
        // System.out.println(verbIDSet.size());
        for (Iterator i = verbIDSet.iterator(); i.hasNext();) {
            String verbID = (String) i.next();
            System.out.println("verb id: " + verbID);
        }
        Set idSet = cd.getIDSet("061c7d", "agent");
        for (Iterator i = idSet.iterator(); i.hasNext();) {
            String id = (String) i.next();
            System.out.println("id: " + id);
        }
    }
}
