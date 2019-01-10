package org.doddle_owl.models.ontology_api;

import org.doddle_owl.models.common.DODDLEConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReferenceOntologyTest {

    @BeforeEach
    public void setUp() {
        DODDLEConstants.EDR_HOME = "/Users/t_morita/DODDLE-OWL/EDR-DIC/";
        DODDLEConstants.EDRT_HOME = "/Users/t_morita/DODDLE-OWL/EDRT-DIC/";
        DODDLEConstants.JPWN_HOME = "/Users/t_morita/DODDLE-OWL/jpwn_dict_1.1/";
        DODDLEConstants.JWO_HOME = "/Users/t_morita/DODDLE-OWL/jwo";

        EDR.initEDRDic();
        EDR.initEDRTDic();
        JaWordNet.initJPNWNDic();
        WordNet.initWordNetDictionary();
        JWO.initJWODic(null);
    }

    @Test
    @DisplayName("DODDLEDicからEDRのConceptを取得")
    public void getEDRConcept() {
        String expected = "dog";
        String actual = ReferenceOntology.getConcept(DODDLEConstants.EDR_URI + "ID3bdc67").getWord();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("DODDLEDicからEDRのConceptを取得")
    public void getEDRTConcept() {
        String expected = "ツリー検索";
        String actual = ReferenceOntology.getConcept(DODDLEConstants.EDRT_URI + "ID2deac6").getWord();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("DODDLEDicからWordNetのConceptを取得")
    public void getWordNetConcept() {
        String expected = "computer";
        String actual = ReferenceOntology.getConcept(DODDLEConstants.WN_URI + "03086983").getWord();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("DODDLEDicから日本語WordNetのConceptを取得")
    public void getJpWordNetConcept() {
        String expected = "urban_area";
        String actual = ReferenceOntology.getConcept(DODDLEConstants.JPN_WN_URI + "08675967-n").getWord();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("DODDLEDicから日本語WikipediaオントロジーのConceptを取得")
    public void getJWOConcept() {
        String expected = "大学";
        String actual = null;
        try {
            actual = ReferenceOntology.getConcept(DODDLEConstants.JWO_URI + URLEncoder.encode("大学", "UTF-8")).getWord();
            assertEquals(expected, actual);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}