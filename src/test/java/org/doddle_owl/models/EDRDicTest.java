package org.doddle_owl.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class EDRDicTest {

    @BeforeEach
    void setUp() {
        DODDLEConstants.EDR_HOME = "/Users/t_morita/DODDLE-OWL/EDR-DIC/";
        DODDLEConstants.EDRT_HOME = "/Users/t_morita/DODDLE-OWL/EDRT-DIC/";
        EDRDic.initEDRDic();
        EDRDic.initEDRTDic();
    }

    @Test
    void getEDRConcept() {
        String expected = "dog";
        String actual = EDRDic.getEDRConcept("ID3bdc67").getWord();
        assertEquals(expected, actual);
    }

    @Test
    void getEDRTConcept() {
        String expected = "ツリー検索";
        String actual = EDRDic.getEDRTConcept("ID2deac6").getWord();
        assertEquals(expected, actual);
    }

}