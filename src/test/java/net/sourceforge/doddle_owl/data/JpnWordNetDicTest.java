package net.sourceforge.doddle_owl.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpnWordNetDicTest {

    @BeforeEach
    void setUp() {
        JpnWordNetDic.initJPNWNDic();
    }

    @Test
    void getSynsetSet() {
        int expected = 4;
        int actual = JpnWordNetDic.getJPNWNSynsetSet("食べる").size();
        assertEquals(expected, actual);
    }

    @Test
    void getConcept() {
        String expected = "urban_area";
        String actual = JpnWordNetDic.getConcept("08675967-n").getWord();
        assertEquals(expected, actual);
    }

}