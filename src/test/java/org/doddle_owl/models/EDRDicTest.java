package org.doddle_owl.models;

import org.doddle_owl.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EDRDicTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getEDRTConcept() {
        assertTrue(false);
    }

    @Test
    void getEDRConcept() {
        assertTrue(false);
    }

    public static void main(String[] args) throws Exception {
        DODDLEConstants.ENWN_HOME = "C:/program files (x86)/wordnet/2.0/dict/";
        Set<String> idSet = new HashSet<String>();
        Set<String> uriSet = WordNetDic.getURISet("operation");
        for (String u : uriSet) {
            String id = Utils.getLocalName(u);
            Set<Set<String>> set = WordNetDic.getSubIDSet(Long.parseLong(id));
            for (Set s : set) {
                idSet.addAll(s);
            }
        }
        // System.out.println(idSet.size());
        Set<Concept> conceptSet = new HashSet<>();
        for (String id : idSet) {
            Set<List<Concept>> pathSet = WordNetDic.getPathToRootSet(Long.parseLong(id));
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