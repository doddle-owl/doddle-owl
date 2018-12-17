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

package net.sourceforge.doddle_owl.actions;

import net.sourceforge.doddle_owl.DODDLEProject;
import net.sourceforge.doddle_owl.DODDLE_OWL;
import net.sourceforge.doddle_owl.data.ConceptTreeNode;
import net.sourceforge.doddle_owl.data.DODDLEConstants;
import net.sourceforge.doddle_owl.data.FreeMindFileFilter;
import net.sourceforge.doddle_owl.data.OWLFileFilter;
import net.sourceforge.doddle_owl.ui.ConceptDefinitionPanel;
import net.sourceforge.doddle_owl.ui.ConceptTreePanel;
import net.sourceforge.doddle_owl.ui.ConstructClassPanel;
import net.sourceforge.doddle_owl.ui.ConstructPropertyPanel;
import net.sourceforge.doddle_owl.utils.FreeMindModelMaker;
import net.sourceforge.doddle_owl.utils.JenaModelMaker;
import net.sourceforge.doddle_owl.utils.Translator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * @author Takeshi Morita
 */
public class SaveOntologyAction extends AbstractAction {

    private String conversionType;
    private FileFilter owlFileFilter;
    private FileFilter freeMindFileFilter;
    public static final String OWL_ONTOLOGY = "OWL";
    public static final String FREEMIND_ONTOLOGY = "FREEMIND";

    public SaveOntologyAction(String title, String type) {
        super(title);
        conversionType = type;
        owlFileFilter = new OWLFileFilter();
        freeMindFileFilter = new FreeMindFileFilter();
    }

    public static Model getOntology(DODDLEProject currentProject) {
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
        ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();

        Model ontology = JenaModelMaker.makeClassModel(constructClassPanel.getIsaTreeModelRoot(),
                ModelFactory.createDefaultModel(), ConceptTreePanel.CLASS_ISA_TREE);
        JenaModelMaker.makeClassModel(constructClassPanel.getHasaTreeModelRoot(), ontology,
                ConceptTreePanel.CLASS_HASA_TREE);
        JenaModelMaker.makePropertyModel(constructPropertyPanel.getIsaTreeModelRoot(), ontology,
                ConceptTreePanel.PROPERTY_ISA_TREE);
        JenaModelMaker.makePropertyModel(constructPropertyPanel.getHasaTreeModelRoot(), ontology,
                ConceptTreePanel.PROPERTY_HASA_TREE);
        conceptDefinitionPanel.addConceptDefinition(ontology);
        return ontology;
    }

    public void saveFreeMindOntology(DODDLEProject project, File file) {
        try {
            ConstructClassPanel constructClassPanel = project.getConstructClassPanel();
            ConstructPropertyPanel constructPropertyPanel = project.getConstructPropertyPanel();

            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
            Document document = docbuilder.newDocument();

            Element freeMindNode = document.createElement("map");
            freeMindNode.setAttribute("version", "0.9.0 Beta14");
            document.appendChild(freeMindNode);

            Element freeMindRootNode = document.createElement("node");
            freeMindRootNode.setAttribute("ID", FreeMindModelMaker.FREEMIND_URI + "RootConcept");
            freeMindRootNode.setAttribute("TEXT", "ルート概念");
            freeMindNode.appendChild(freeMindRootNode);

            ConceptTreeNode rootNode = constructClassPanel.getIsaTreeModelRoot();
            Element freeMindNounConceptRootNode = FreeMindModelMaker.getFreeMindElement(rootNode, document);
            freeMindRootNode.appendChild(freeMindNounConceptRootNode);
            FreeMindModelMaker.makeFreeMindModel(document, rootNode, freeMindNounConceptRootNode);

            rootNode = constructPropertyPanel.getIsaTreeModelRoot();
            Element freeMindVerbConceptRootNode = FreeMindModelMaker.getFreeMindElement(rootNode, document);
            freeMindRootNode.appendChild(freeMindVerbConceptRootNode);
            FreeMindModelMaker.makeFreeMindModel(document, rootNode, freeMindVerbConceptRootNode);

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer transformer = tfactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(file));
            transformer.reset();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException tfe) {
            tfe.printStackTrace();
        } catch (TransformerException te) {
            te.printStackTrace();
        }
    }

    public void saveOWLOntology(DODDLEProject project, File file) {
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Model ontModel = getOntology(project);
            RDFWriter rdfWriter = ontModel.getWriter("RDF/XML");
            rdfWriter.setProperty("xmlbase", DODDLEConstants.BASE_URI);
            rdfWriter.setProperty("showXmlDeclaration", Boolean.TRUE);
            rdfWriter.write(ontModel, writer, DODDLEConstants.BASE_URI);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(DODDLEConstants.PROJECT_HOME);
        if (conversionType.equals(OWL_ONTOLOGY)) {
            chooser.addChoosableFileFilter(owlFileFilter);
        } else if (conversionType.equals(FREEMIND_ONTOLOGY)) {
            chooser.addChoosableFileFilter(freeMindFileFilter);
        }
        int retval = chooser.showSaveDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            DODDLEProject currentProject = DODDLE_OWL.getCurrentProject();
            if (conversionType.equals(OWL_ONTOLOGY)) {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".owl")) {
                    file = new File(file.getAbsolutePath() + ".owl");
                }
                saveOWLOntology(currentProject, file);
                DODDLE_OWL.STATUS_BAR.setText(Translator.getTerm("SaveOWLOntologyAction"));
            } else if (conversionType.equals(FREEMIND_ONTOLOGY)) {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".mm")) {
                    file = new File(file.getAbsolutePath() + ".mm");
                }
                saveFreeMindOntology(currentProject, file);
                DODDLE_OWL.STATUS_BAR.setText(Translator.getTerm("SaveFreeMindOntologyAction"));
            }
        }
    }
}
