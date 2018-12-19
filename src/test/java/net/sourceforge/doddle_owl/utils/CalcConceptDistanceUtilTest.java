package net.sourceforge.doddle_owl.utils;

import net.sourceforge.doddle_owl.data.*;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static net.sourceforge.doddle_owl.utils.CalcConceptDistanceUtil.*;

public class CalcConceptDistanceUtilTest {

    @Nested
    @DisplayName("EDRにおける概念距離計算のテスト")
    class CalcConceptDistanceInEDRTest {
        @BeforeEach
        void setup() {
            EDRDic.initEDRDic();
        }

        @Test
        @DisplayName("EDRにおける最短概念間距離計算のテスト")
        void getShortestConceptDistanceInEDR() {
            int expected = 1;
            int actual = getShortestConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c"));
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("EDRにおける最長概念間距離計算のテスト")
        void getLongestConceptDistanceInEDR() {
            int expected = 1;
            int actual = getLongestConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c"));
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("EDRにおける平均概念間距離計算のテスト")
        void getAverageConceptDistanceInEDR() {
            int expected = 1;
            int actual = getAverageConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c"));
            assertEquals(expected, actual);
        }

    }

    @Nested
    @DisplayName("EDRTにおける概念距離計算のテスト")
    class CalcConceptDistanceInEDRTTest {
        @BeforeEach
        void setup() {
            EDRDic.initEDRTDic();
        }

        @Test
        @DisplayName("EDRTにおける最短概念間距離計算のテスト")
        void getShortestConceptDistanceInEDR() {

        }

        @Test
        @DisplayName("EDRTにおける最長概念間距離計算のテスト")
        void getLongestConceptDistanceInEDR() {

        }

        @Test
        @DisplayName("EDRTにおける平均概念間距離計算のテスト")
        void getAverageConceptDistanceInEDR() {

        }

    }

    @Nested
    @DisplayName("WordNetにおける概念距離計算のテスト")
    class CalcConceptDistanceInWordNetTest {
        @BeforeEach
        void setup() {

        }

        @Test
        @DisplayName("WordNetにおける最短概念間距離計算のテスト")
        void getShortestConceptDistanceInWordNet() {
            assertTrue(false);
        }

        @Test
        @DisplayName("WordNetにおける最長概念間距離計算のテスト")
        void getLongestConceptDistanceInWordNet() {
            assertTrue(false);
        }

        @Test
        @DisplayName("WordNetにおける平均概念間距離計算のテスト")
        void getAverageConceptDistanceInWordNet() {
            assertTrue(false);
        }

    }


    @Nested
    @DisplayName("日本語WordNetにおける概念距離計算のテスト")
    class CalcConceptDistanceInJpWordNetTest {
        @BeforeEach
        void setup() {
            assertTrue(false);
        }

        @Test
        @DisplayName("日本語WordNetにおける最短概念間距離計算のテスト")
        void getShortestConceptDistanceInJpWordNet() {
            assertTrue(false);
        }

        @Test
        @DisplayName("日本語WordNetにおける最長概念間距離計算のテスト")
        void getLongestConceptDistanceInJpWordNet() {

        }

        @Test
        @DisplayName("日本語WordNetにおける平均概念間距離計算のテスト")
        void getAverageConceptDistanceInJpWordNet() {
            int expected = 1;
            int actual = getAverageConceptDistance(WordNetDic.getInstance().getWNConcept("2001223"), WordNetDic
                    .getInstance().getWNConcept("2037721"));
            assertEquals(expected, actual);
        }
    }

    public static void getOWLConceptDistance(String fname, String uri1, String uri2) {
        OWLOntologyManager.addRefOntology(new File(fname));
        Concept c1 = OWLOntologyManager.getConcept(uri1);
        Concept c2 = OWLOntologyManager.getConcept(uri2);

        ConceptDistanceModel resultModel = null;
        List<ConceptDistanceModel> cdModelList = getConceptDistanceModelList(c1, c2);
        for (ConceptDistanceModel cdModel : cdModelList) {
            if (resultModel == null) {
                resultModel = cdModel;
                continue;
            }
            if (resultModel.getShortestCommonAncestorDepth() < cdModel.getShortestCommonAncestorDepth()) {
                resultModel = cdModel;
            }
        }
        System.err.println(resultModel);
    }

    public static void getOWLLongestDepth(String fname) {
        OWLOntologyManager.addRefOntology(new File(fname));
        ReferenceOWLOntology refOnt = OWLOntologyManager.getRefOntology(new File(fname).getAbsolutePath());
        int depth = 0;
        Set<String> classSet = refOnt.getClassSet();
        for (String cls : classSet) {
            Set<List<Concept>> pathToRootSet = refOnt.getPathToRootSet(cls);
            for (List<Concept> path : pathToRootSet) {
                if (depth < path.size()) {
                    depth = path.size();
                    System.out.println(path);
                }
            }
        }
        System.out.println("depth: " + depth);
    }


    public static void testWNConceptDistance() {
        System.out.println(getAverageConceptDistance(WordNetDic.getInstance().getWNConcept("2001223"), WordNetDic
                .getInstance().getWNConcept("2037721")));
    }

    private static void testOwlLongestDepth(String[] args) {
        if (args.length != 2) {
            return;
        }
        Translator.loadDODDLEComponentOntology(DODDLEConstants.LANG);
        String ontFileName = args[0];
        String inputFileName = args[1];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName),
                    "UTF-8"));
            // while (reader.ready()) {
            // String line = reader.readLine();
            // String[] uris = line.split("\\s+");
            // getOWLConceptDistance(ontFileName, uris[0], uris[1]);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getOWLLongestDepth(ontFileName);
    }
}
