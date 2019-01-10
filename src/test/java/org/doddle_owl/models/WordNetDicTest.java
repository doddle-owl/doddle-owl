package org.doddle_owl.models;

import org.doddle_owl.models.ontology_api.WordNetDic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordNetDicTest {

    @BeforeEach
    public void setUp() {
        WordNetDic.initWordNetDictionary();
    }

    @Test
    void getWN31Concept() {
        String expected = "03086983";
        String actual = WordNetDic.getWNConcept("03086983").getLocalName();
        assertEquals(expected, actual);
    }

    @Test
    void getWN31NounIndexWord() {
        String expected = "computer";
        String actual = WordNetDic.getNounIndexWord(expected).getLemma();
        assertEquals(expected, actual);
    }

    @Test
    void getWN31URISet() {
        int expected = 7;
        int actual = WordNetDic.getURISet("dog").size();
        assertEquals(expected, actual);
    }
}