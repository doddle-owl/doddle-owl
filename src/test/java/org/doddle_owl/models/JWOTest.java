package org.doddle_owl.models;

import org.doddle_owl.models.common.DODDLEConstants;
import org.doddle_owl.models.ontology_api.JWO;
import org.doddle_owl.utils.OWLOntologyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JWOTest {

    @BeforeEach
    void setUp() {
        DODDLEConstants.JWO_HOME = "/Users/t_morita/DODDLE-OWL/jwo";
        JWO.initJWODic(null);
    }

    @Test
    void initJWODic() {
        assertTrue(JWO.isAvailable);
    }

    @Test
    void getJWOConcept() {
        String expected = "大学";
        String actual = null;
        try {
            actual = OWLOntologyManager.getConcept(DODDLEConstants.JWO_URI + URLEncoder.encode("大学", "UTF-8")).getWord();
            assertEquals(expected, actual);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
