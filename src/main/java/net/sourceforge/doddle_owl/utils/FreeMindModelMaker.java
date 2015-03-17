/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2015 Yamaguchi Laboratory, Keio University. All rights reserved.
 *
 * This file is part of DODDLE-OWL.
 *
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.sourceforge.doddle_owl.utils;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import net.sourceforge.doddle_owl.*;
import net.sourceforge.doddle_owl.data.*;

import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.xml.sax.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author Takeshi Morita
 */
public class FreeMindModelMaker {

    public static final String FREEMIND_URI = "http://freemind.sourceforge.net/wiki/index.php/Main_Page#";

    public static Element getFreeMindElement(ConceptTreeNode node, Document doc) {
        Element freeMindNode = doc.createElement("node");
        freeMindNode.setAttribute("ID", node.getConcept().getURI());
        freeMindNode.setAttribute("TEXT", node.getConcept().getWord());
        Element uriAttr = doc.createElement("attribute");
        uriAttr.setAttribute("NAME", "URI");
        uriAttr.setAttribute("VALUE", node.getConcept().getURI());
        freeMindNode.appendChild(uriAttr);

        Map<String, List<DODDLELiteral>> langLabelListMap = node.getConcept().getLangLabelListMap();
        for (String lang : langLabelListMap.keySet()) {
            for (DODDLELiteral label : langLabelListMap.get(lang)) {
                Element wordAttr = doc.createElement("attribute");
                wordAttr.setAttribute("NAME", lang + "_LABEL");
                wordAttr.setAttribute("VALUE", label.getString());
                freeMindNode.appendChild(wordAttr);
            }
        }

        Map<String, List<DODDLELiteral>> langDescriptionListMap = node.getConcept().getLangDescriptionListMap();
        for (String lang : langDescriptionListMap.keySet()) {
            for (DODDLELiteral description : langDescriptionListMap.get(lang)) {
                Element descriptionAttr = doc.createElement("attribute");
                descriptionAttr.setAttribute("NAME", lang + "_DESCRIPTION");
                descriptionAttr.setAttribute("VALUE", description.getString());
                freeMindNode.appendChild(descriptionAttr);
            }
        }

        return freeMindNode;
    }

    public static void makeFreeMindModel(Document document, ConceptTreeNode node, Element freeMindNode) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            Element freeMindChildNode = getFreeMindElement(childNode, document);
            freeMindNode.appendChild(freeMindChildNode);
            makeFreeMindModel(document, childNode, freeMindChildNode);
        }
    }

    public static Element getDocumentElement(File file) {
        Element docElement = null;
        try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
            Document document = docbuilder.parse(file);
            docElement = document.getDocumentElement();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return docElement;
    }

    public static Model getOWLModel(File file) {
        Model ontModel = ModelFactory.createDefaultModel();
        Element docElement = getDocumentElement(file);
        Element rootNode = null;
        Element nounRootNode = null;
        Element verbRootNode = null;
        NodeList rootNodeList = docElement.getChildNodes();
        for (int i = 0; i < rootNodeList.getLength(); i++) {
            if (rootNodeList.item(i).getNodeName().equals("node")) {
                rootNode = (Element) rootNodeList.item(i);
            }
        }
        rootNodeList = rootNode.getChildNodes();
        for (int i = 0; i < rootNodeList.getLength(); i++) {
            if (rootNodeList.item(i).getNodeName().equals("node")) {
                rootNode = (Element) rootNodeList.item(i);
                if (rootNode.getAttribute("ID").equals(DODDLEConstants.BASE_URI + "CLASS_ROOT")) {
                    nounRootNode = rootNode;
                } else if (rootNode.getAttribute("ID").equals(DODDLEConstants.BASE_URI + "PROP_ROOT")) {
                    verbRootNode = rootNode;
                }
            }
        }
        setOWLModel(DODDLEConstants.BASE_URI + "CLASS_ROOT", ontModel, nounRootNode, OWL.Class);
        setOWLModel(DODDLEConstants.BASE_URI + "PROP_ROOT", ontModel, verbRootNode, OWL.ObjectProperty);
        return ontModel;
    }

    public static void setOWLModel(String upperURI, Model ontModel, Element element, Resource type) {
        NodeList childNodeList = element.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node childNode = childNodeList.item(i);
            Element childElement = null;
            if (childNode instanceof Element) {
                childElement = (Element) childNode;
            } else {
                continue;
            }
            if (childElement.getNodeName().equals("node")) {
                String inputWord = childElement.getAttribute("TEXT");
                String uri = childElement.getAttribute("ID");
                if (uri.matches("Freemind_Link.*")) {
                    uri = FREEMIND_URI + uri;
                }
                if (uri.equals("_")) {
                    uri = FREEMIND_URI + DODDLE_OWL.getCurrentProject().getUserIDStr();
                }
                Literal literal = ontModel.createLiteral(inputWord, "ja");
                ontModel.add(ResourceFactory.createResource(uri), RDFS.label, literal);
                ontModel.add(ResourceFactory.createResource(uri), RDF.type, type);
                if (type == OWL.Class) {
                    ontModel.add(ResourceFactory.createResource(uri), RDFS.subClassOf, ResourceFactory
                            .createResource(upperURI));
                } else if (type == OWL.ObjectProperty) {
                    ontModel.add(ResourceFactory.createResource(uri), RDFS.subPropertyOf, ResourceFactory
                            .createResource(upperURI));
                }
                NodeList attrList = childElement.getChildNodes();
                for (int j = 0; j < attrList.getLength(); j++) {
                    Node attrNode = attrList.item(j);
                    if (attrNode instanceof Element && attrNode.getNodeName().equals("attribute")) {
                        Element attrElement = (Element) attrNode;
                        String attrName = attrElement.getAttribute("NAME");
                        if (attrName.equals("URI")) {
                            uri = attrElement.getAttribute("VALUE");
                            ontModel.add(ResourceFactory.createResource(uri), RDF.type, type);
                        } else if (attrName.equals(".*_LABEL")) {
                            String lang = attrName.split("_")[0];
                            if (lang.equals("default")) {
                                lang = "";
                            }
                            String word = attrElement.getAttribute("VALUE");
                            literal = ontModel.createLiteral(word.replaceAll("\t", ""), lang);
                            ontModel.add(ResourceFactory.createResource(uri), RDFS.label, literal);
                        } else if (attrName.equals(".*_DESCRIPTION")) {
                            String lang = attrName.split("_")[0];
                            if (lang.equals("default")) {
                                lang = "";
                            }
                            String description = attrElement.getAttribute("VALUE");
                            literal = ontModel.createLiteral(description.replaceAll("\t", ""), lang);
                            ontModel.add(ResourceFactory.createResource(uri), RDFS.comment, literal);
                        }
                    }
                }
                setOWLModel(uri, ontModel, childElement, type);
            }
        }
    }

    public static void setConceptTreeModel(ConceptTreeNode treeNode, Element element) {
        NodeList childNodeList = element.getChildNodes();
        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node childNode = childNodeList.item(i);
            Element childElement = null;
            if (childNode instanceof Element) {
                childElement = (Element) childNode;
            } else {
                continue;
            }
            if (childElement.getNodeName().equals("node")) {
                String inputWord = childElement.getAttribute("TEXT");
                VerbConcept concept = new VerbConcept("", "");
                String uri = childElement.getAttribute("ID");
                if (uri.matches("Freemind_Link.*")) {
                    uri = FREEMIND_URI + uri;
                    concept.addLabel(new DODDLELiteral(DODDLEConstants.LANG, inputWord));
                }
                if (uri.equals("_")) {
                    uri = FREEMIND_URI + DODDLE_OWL.getCurrentProject().getUserIDStr();
                    concept.addLabel(new DODDLELiteral(DODDLEConstants.LANG, inputWord));
                }
                concept.setInputLabel(new DODDLELiteral("", inputWord));
                concept.setURI(uri);
                NodeList attrList = childElement.getChildNodes();
                for (int j = 0; j < attrList.getLength(); j++) {
                    Node attrNode = attrList.item(j);
                    if (attrNode instanceof Element && attrNode.getNodeName().equals("attribute")) {
                        Element attrElement = (Element) attrNode;
                        String attrName = attrElement.getAttribute("NAME");
                        if (attrName.equals("URI")) {
                            uri = attrElement.getAttribute("VALUE");
                            concept.setURI(uri);
                        } else if (attrName.matches(".*_LABEL")) {
                            String lang = attrName.split("_")[0];
                            if (lang.equals("default")) {
                                lang = "";
                            }
                            String word = attrElement.getAttribute("VALUE");
                            concept.addLabel(new DODDLELiteral(lang, word));
                            if (word.indexOf(inputWord) == -1) {
                                concept.addLabel(new DODDLELiteral(lang, inputWord));
                            }
                        } else if (attrName.matches(".*_DESCRIPTION")) {
                            String lang = attrName.split("_")[0];
                            if (lang.equals("default")) {
                                lang = "";
                            }
                            String description = attrElement.getAttribute("VALUE");
                            concept.addDescription(new DODDLELiteral(lang, description));
                        }
                    }
                }
                ConceptTreeNode childTreeNode = new ConceptTreeNode(concept, DODDLE_OWL.getCurrentProject());
                treeNode.add(childTreeNode);
                setConceptTreeModel(childTreeNode, childElement);
            }
        }
    }
}
