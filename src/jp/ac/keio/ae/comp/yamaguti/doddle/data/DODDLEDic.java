/*
 * @(#)  2006/04/02
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class DODDLEDic {

    private static boolean isEDREnable() {
        return DODDLE.getCurrentProject().getOntologySelectionPanel().isEDREnable();
    }

    private static boolean isEDRTEnable() {
        return DODDLE.getCurrentProject().getOntologySelectionPanel().isEDRTEnable();
    }

    private static boolean isWordNetEnable() {
        return DODDLE.getCurrentProject().getOntologySelectionPanel().isWordNetEnable();
    }

    public static Concept getConcept(String uri) {
        Resource res = ResourceFactory.createResource(uri);
        String id = Utils.getLocalName(res);
        String ns = Utils.getNameSpace(res);
        Concept c = OWLOntologyManager.getConcept(uri);
        if (c != null) { return c; }
        if (isEDREnable() && ns.equals(DODDLEConstants.EDR_URI)) {
            return EDRDic.getEDRConcept(id);
        } else if (isEDRTEnable() && ns.equals(DODDLEConstants.EDRT_URI)) {
            return EDRDic.getEDRTConcept(id);
        } else if (isWordNetEnable() && ns.equals(DODDLEConstants.WN_URI)) { return WordNetDic.getWNConcept(id); }
        return null;
    }

    public static Set<String> getURISet(String word) {
        Set<String> uriSet = new HashSet<String>();
        if (isEDREnable()) {
            Set<String> idSet = EDRDic.getEDRIDSet(word);
            for (String id : idSet) {
                uriSet.add(DODDLEConstants.EDR_URI + id);
            }
        }
        if (isEDRTEnable()) {
            Set<String> idSet = EDRDic.getEDRTIDSet(word);
            for (String id : idSet) {
                uriSet.add(DODDLEConstants.EDRT_URI + id);
            }
        }
        uriSet.addAll(WordNetDic.getURISet(word));
        uriSet.addAll(OWLOntologyManager.getURISet(word));
        return uriSet;
    }
}
