package org.doddle_owl.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordNetDicTest {

    @Nested
    class WordNetDic30Test {
        @BeforeEach
        void setUp() {
            DODDLEConstants.ENWN_HOME = DODDLEConstants.ENWN_3_0_HOME;
            WordNetDic.initWordNetDictionary();
            WordNetDic.isAvailable = true;
        }

        @Test
        void getWN30Concept() {
            String expected = "03086983";
            String actual = WordNetDic.getWNConcept("03086983").getLocalName();
            assertEquals(expected, actual);
        }

        @Test
        void getWN30NounIndexWord() {
            String expected = "computer";
            String actual = WordNetDic.getNounIndexWord(expected).getLemma();
            assertEquals(expected, actual);
        }


        @Test
        void getWN30URISet() {
            int expected = 7;
            int actual = WordNetDic.getURISet("dog").size();
            assertEquals(expected, actual);
        }
    }

    @Nested
    class WordNetDic31Test {
        @BeforeEach
        void setUp() {
            DODDLEConstants.ENWN_HOME = DODDLEConstants.ENWN_3_1_HOME;
            WordNetDic.initWordNetDictionary();
            WordNetDic.isAvailable = true;
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
}