/*
 * @(#)  2007/02/18
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

import org.w3c.dom.*;
import org.w3c.dom.Document;

/**
 * @author takeshi morita
 */
public class FreeMindModelMaker {
    public static void makeFreeMindModel(Document document, ConceptTreeNode node, Element freeMindNode) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode =(ConceptTreeNode) node.getChildAt(i);
            Element freeMindChildNode = document.createElement("node");
            freeMindChildNode.setAttribute("ID", childNode.getConcept().getURI());
            freeMindChildNode.setAttribute("TEXT", childNode.getConcept().getWord());
            freeMindNode.appendChild(freeMindChildNode);
            makeFreeMindModel(document, childNode, freeMindChildNode);
        }
    }
}
