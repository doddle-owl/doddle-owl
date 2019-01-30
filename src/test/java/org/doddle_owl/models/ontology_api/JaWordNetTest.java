package org.doddle_owl.models.ontology_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JaWordNetTest {

    @BeforeEach
    void setUp() {
        JaWordNet.initJPNWNDic();
    }

    @Test
    void getSynsetSet() {
        int expected = 4;
        int actual = JaWordNet.getJPNWNSynsetSet("食べる").size();
        assertEquals(expected, actual);
    }

    @Test
    void getConcept() {
        String expected = "urban_area";
        String actual = JaWordNet.getConcept("08675967-n").getWord();
        assertEquals(expected, actual);
    }

}