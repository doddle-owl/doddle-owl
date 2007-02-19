/*
 * @(#)  2007/02/18
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

import org.w3c.dom.*;
import org.w3c.dom.Document;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author takeshi morita
 */
public class FreeMindModelMaker {
    
    public static Element getFreeMindElement(ConceptTreeNode node, Document doc) {
        Element freeMindNode = doc.createElement("node");
        freeMindNode.setAttribute("ID", node.getConcept().getURI());
        freeMindNode.setAttribute("TEXT", node.getConcept().getWord());
        Element uriAttr = doc.createElement("attribute");
        uriAttr.setAttribute("NAME", "URI");
        uriAttr.setAttribute("VALUE", node.getConcept().getURI());
        freeMindNode.appendChild(uriAttr);
        Element jaWordAttr = doc.createElement("attribute");
        jaWordAttr.setAttribute("NAME", "JA_WORD");
        jaWordAttr.setAttribute("VALUE", node.getConcept().getJaWord());
        freeMindNode.appendChild(jaWordAttr);
        Element enWordAttr = doc.createElement("attribute");
        enWordAttr.setAttribute("NAME", "EN_WORD");
        enWordAttr.setAttribute("VALUE", node.getConcept().getEnWord());
        freeMindNode.appendChild(enWordAttr);
        Element jaExplanationAttr = doc.createElement("attribute");
        jaExplanationAttr.setAttribute("NAME", "JA_EXPLANATION");
        jaExplanationAttr.setAttribute("VALUE", node.getConcept().getJaExplanation());
        freeMindNode.appendChild(jaExplanationAttr);
        Element enExplanationAttr = doc.createElement("attribute");
        enExplanationAttr.setAttribute("NAME", "EN_EXPLANATION");
        enExplanationAttr.setAttribute("VALUE", node.getConcept().getEnExplanation());
        freeMindNode.appendChild(enExplanationAttr);
        return freeMindNode;
    }
    
    public static void makeFreeMindModel(Document document, ConceptTreeNode node, Element freeMindNode) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode =(ConceptTreeNode) node.getChildAt(i);
            Element freeMindChildNode = getFreeMindElement(childNode, document);
            freeMindNode.appendChild(freeMindChildNode);
            makeFreeMindModel(document, childNode, freeMindChildNode);
        }
    }
    
    public static void setConceptTreeModel(ConceptTreeNode treeNode, Element element) {
        NodeList childNodeList = element.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node childNode = childNodeList.item(i);
            Element childElement = null;
            if (childNode instanceof Element) {
                childElement = (Element)childNode;
            } else {
                continue;
            }            
            if (childElement.getNodeName().equals("node")) {                
                String uri = childElement.getAttribute("ID");
                String inputWord = childElement.getAttribute("TEXT");
                VerbConcept concept = new VerbConcept(uri, inputWord);                
                NodeList attrList = childElement.getChildNodes();
                for (int j = 0; j < attrList.getLength(); j++) {
                    Node attrNode = attrList.item(j);
                    if (attrNode instanceof Element && attrNode.getNodeName().equals("attribute")) {
                        Element attrElement = (Element)attrNode;
                        String attrName = attrElement.getAttribute("NAME");
                        if (attrName.equals("URI")) {
                            uri = attrElement.getAttribute("VALUE");
                            concept.setURI(uri);
                        } else if (attrName.equals("JA_WORD")) {
                            String jaWord = attrElement.getAttribute("VALUE");
                            concept.setJaWord(jaWord);    
                        } else if (attrName.equals("EN_WORD")) {
                            String enWord = attrElement.getAttribute("VALUE");
                            concept.setEnWord(enWord);    
                        } else if (attrName.equals("JA_EXPLANATION")) {
                            String jaExplanation = attrElement.getAttribute("VALUE");
                            concept.setJaExplanation(jaExplanation);
                        } else if (attrName.equals("EN_EXPLANATION")) {
                            String enExplanation = attrElement.getAttribute("VALUE");
                            concept.setEnExplanation(enExplanation);
                        }
                    }
                }
                ConceptTreeNode childTreeNode = new ConceptTreeNode(concept, DODDLE.getCurrentProject());
                treeNode.add(childTreeNode);
                setConceptTreeModel(childTreeNode, childElement);
            }             
        }
    }
}
