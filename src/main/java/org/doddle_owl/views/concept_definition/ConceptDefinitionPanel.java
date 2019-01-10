/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 * 
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.doddle_owl.views.concept_definition;

import net.infonode.docking.*;
import net.infonode.docking.util.ViewMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.doddle_owl.models.common.DODDLEConstants;
import org.doddle_owl.models.concept_definition.ConceptPair;
import org.doddle_owl.models.concept_selection.Concept;
import org.doddle_owl.models.document_selection.Document;
import org.doddle_owl.models.ontology_api.EDRDic;
import org.doddle_owl.models.ontology_api.WordNetDic;
import org.doddle_owl.models.term_selection.InputTermModel;
import org.doddle_owl.utils.OWLOntologyManager;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;
import org.doddle_owl.views.DODDLEProjectPanel;
import org.doddle_owl.views.concept_selection.InputConceptSelectionPanel;
import org.doddle_owl.views.document_selection.InputDocumentSelectionPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Statement;
import java.util.List;
import java.util.*;

/**
 * @author shigeta
 * @author Takeshi Morita
 */
public class ConceptDefinitionPanel extends JPanel implements ListSelectionListener {

	private Map<String, Set<Concept>> wordCorrespondConceptSetMap;
	private Map<String, Concept> compoundWordConceptMap;
	private Map<String, Set<Concept>> termConceptSetMap;

	public List<String> inputWordList;

	private JList inputConceptJList;
	private ConceptDefinitionResultPanel resultPanel;
	private ConceptDefinitionAlgorithmPanel algorithmPanel;
	private ConceptDefinitionResultPanel.ConceptDefinitionPanel conceptDefinitionPanel;

	private DODDLEProjectPanel doddleProjectPanel;
	private InputDocumentSelectionPanel docSelectionPanel;
	private InputConceptSelectionPanel inputConceptSelectionPanel;

	private View[] mainViews;
	private RootWindow rootWindow;

	public void initialize() {

	}

	public void setInputConceptJList() {
		inputWordList = getInputTermList();
		inputConceptJList.removeAll();
		DefaultListModel listModel = new DefaultListModel();
		for (String iw : inputWordList) {
			listModel.addElement(iw);
		}
		inputConceptJList.setModel(listModel);
	}

	public Concept getConcept(String word) {
		Concept c = null;
		if (compoundWordConceptMap.get(word) != null) {
			c = compoundWordConceptMap.get(word);
			// System.out.println("cid: " + id);
		} else if (wordCorrespondConceptSetMap.get(word) != null) {
			Set<Concept> correspondConceptSet = wordCorrespondConceptSetMap.get(word);
			c = (Concept) correspondConceptSet.toArray()[0];
			// System.out.println("id: " + id);
		} else {
			Set<Concept> wordConceptSet = termConceptSetMap.get(word);
			if (wordConceptSet != null) {
				c = (Concept) wordConceptSet.toArray()[0];
			}
		}
		if (c == null) {
			return null;
		}
		Concept concept = doddleProjectPanel.getConcept(c.getURI());
		if (concept != null) {
			return concept;
		}

		concept = OWLOntologyManager.getConcept(c.getURI());
		if (concept != null) {
			return concept;
		}
		if (c.getNameSpace().equals(DODDLEConstants.EDR_URI)) {
			concept = EDRDic.getEDRConcept(c.getLocalName());
		} else if (c.getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
			concept = EDRDic.getEDRTConcept(c.getLocalName());
		} else if (c.getNameSpace().equals(DODDLEConstants.WN_URI)) {
			concept = WordNetDic.getWNConcept(c.getLocalName());
		}
		return concept;
	}

	private Resource getResource(Concept c, Model ontology) {
		return ontology.getResource(c.getURI());
	}

	public Property getProperty(Concept c, Model ontology) {
		return ontology.getProperty(c.getURI());
	}

	public Model addConceptDefinition(Model ontology) {
		for (int i = 0; i < resultPanel.getRelationCount(); i++) {
			Object[] relation = resultPanel.getRelation(i);
			boolean isMetaProperty = Boolean.valueOf((String) relation[0]);
			String domainWord = (String) relation[1];
			String rangeWord = (String) relation[3];
			Concept property = (Concept) relation[2];
			Concept domainConcept = getConcept(domainWord);
			Concept rangeConcept = getConcept(rangeWord);

			// System.out.println("r: "+property+"d: "+domainConcept + "r:
			// "+rangeConcept);

			if (property.getLocalName().equals("DID0")) { // agent
				ontology.add(getResource(domainConcept, ontology), RDF.type, OWL.ObjectProperty);
				ontology.add(getResource(domainConcept, ontology), RDFS.domain,
						getResource(rangeConcept, ontology));
			} else if (property.getLocalName().equals("DID1")) {// object
				ontology.add(getResource(domainConcept, ontology), RDF.type, OWL.ObjectProperty);
				ontology.add(getResource(domainConcept, ontology), RDFS.range,
						getResource(rangeConcept, ontology));
			} else {
				if (isMetaProperty) {
					ontology.add(getResource(domainConcept, ontology),
							getProperty(property, ontology), getResource(rangeConcept, ontology));
					ontology.add(getResource(property, ontology), RDFS.domain, OWL.Class);
					ontology.add(getResource(property, ontology), RDFS.range, OWL.Class);
				} else {
					ontology.add(getResource(property, ontology), RDFS.domain,
							getResource(domainConcept, ontology));
					ontology.add(getResource(property, ontology), RDFS.range,
							getResource(rangeConcept, ontology));
				}
			}
		}
		return ontology;
	}

	public ConceptDefinitionPanel(DODDLEProjectPanel project) {
		doddleProjectPanel = project;
		docSelectionPanel = project.getDocumentSelectionPanel();
		inputConceptSelectionPanel = project.getInputConceptSelectionPanel();

		inputConceptJList = new JList(new DefaultListModel());
		inputConceptJList.addListSelectionListener(this);

		algorithmPanel = new ConceptDefinitionAlgorithmPanel(inputConceptJList, doddleProjectPanel);
		resultPanel = new ConceptDefinitionResultPanel(inputConceptJList, algorithmPanel,
                doddleProjectPanel);
		conceptDefinitionPanel = resultPanel.getDefinePanel();

		mainViews = new View[10];
		ViewMap viewMap = new ViewMap();

		mainViews[0] = new View(Translator.getTerm("WordSpaceParameterPanel"), null,
				algorithmPanel.getWordSpaceParamPanel());
		mainViews[1] = new View(Translator.getTerm("AprioriParameterPanel"), null,
				algorithmPanel.getAprioriParamPanel());
		mainViews[2] = new View(Translator.getTerm("InputConceptList"), null,
				resultPanel.getInputConceptPanel());
		mainViews[3] = new View(Translator.getTerm("InputDocumentList"), null,
				resultPanel.getInputDocPanel());
		mainViews[4] = new View("WordSpace", null, new JScrollPane(
				resultPanel.getWordSpaceResultTable()));
		mainViews[5] = new View("Apriori", null, new JScrollPane(
				resultPanel.getAprioriResultTable()));
		mainViews[6] = new View("WordSpace & Apriori", null, new JScrollPane(
				resultPanel.getWAResultTable()));
		mainViews[7] = new View(Translator.getTerm("CorrectConceptPairTable"), null,
				resultPanel.getAcceptedPairPanel());
		mainViews[8] = new View(Translator.getTerm("WrongConceptPairTable"), null,
				resultPanel.getWrongPairPanel());
		mainViews[9] = new View(Translator.getTerm("DefineConceptPairPanel"), null,
				conceptDefinitionPanel);

		for (int i = 0; i < mainViews.length; i++) {
			viewMap.addView(i, mainViews[i]);
		}
		rootWindow = Utils.createDODDLERootWindow(viewMap);
		setLayout(new BorderLayout());
		add(rootWindow, BorderLayout.CENTER);

		setTableAction();
	}

	public void setXGALayout() {
		TabWindow algorithmTabWindow = new TabWindow(new DockingWindow[] { mainViews[0],
				mainViews[1] });
		TabWindow parameterTabWindow = new TabWindow(new DockingWindow[] { mainViews[4],
				mainViews[5], mainViews[6] });
		TabWindow resultTabWindow = new TabWindow(
				new DockingWindow[] { mainViews[7], mainViews[8] });
		SplitWindow sw1 = new SplitWindow(false, 0.4f, mainViews[3], parameterTabWindow);
		SplitWindow sw2 = new SplitWindow(true, 0.3f, mainViews[2], sw1);
		SplitWindow sw3 = new SplitWindow(false, 0.4f, algorithmTabWindow, sw2);
		SplitWindow sw4 = new SplitWindow(false, 0.25f, mainViews[9], resultTabWindow);
		SplitWindow sw5 = new SplitWindow(false, 0.6f, sw3, sw4);
		rootWindow.setWindow(sw5);
		mainViews[0].restoreFocus();
		mainViews[4].restoreFocus();
		mainViews[7].restoreFocus();
	}

	public void setUXGALayout() {
		SplitWindow algorithmSw = new SplitWindow(true, mainViews[0], mainViews[1]);
		SplitWindow parameterSw1 = new SplitWindow(true, mainViews[4], mainViews[5]);
		SplitWindow parameterSw2 = new SplitWindow(true, 0.66f, parameterSw1, mainViews[6]);
		SplitWindow resultSw = new SplitWindow(true, mainViews[7], mainViews[8]);

		SplitWindow sw1 = new SplitWindow(false, 0.4f, mainViews[3], parameterSw2);
		SplitWindow sw2 = new SplitWindow(true, 0.3f, mainViews[2], sw1);
		SplitWindow sw3 = new SplitWindow(false, 0.3f, algorithmSw, sw2);
		SplitWindow sw4 = new SplitWindow(false, 0.25f, mainViews[9], resultSw);
		SplitWindow sw5 = new SplitWindow(false, 0.6f, sw3, sw4);
		rootWindow.setWindow(sw5);
		mainViews[0].restoreFocus();
		mainViews[4].restoreFocus();
		mainViews[7].restoreFocus();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (inputConceptJList.getSelectedValue() != null) {
			String selectedInputConcept = inputConceptJList.getSelectedValue().toString();
			resultPanel.calcWSandARValue(selectedInputConcept);
		}
	}

	private void setTableAction() {
		resultPanel.getWordSpaceSelectionModel().addListSelectionListener(
				e -> {
					if (e.getValueIsAdjusting())
						return;
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
					} else {
						int selectedRow = lsm.getMinSelectionIndex();
						// System.out.println(lsm.getMinSelectionIndex());
						String c1 = inputConceptJList.getSelectedValue().toString();
						String c2 = resultPanel.getWSTableRowConceptName(selectedRow);
						conceptDefinitionPanel.setCText(c1, c2);
					}
				});

		resultPanel.getAprioriSelectionModel().addListSelectionListener(
				e -> {
					if (e.getValueIsAdjusting())
						return;
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
					} else {
						int selectedRow = lsm.getMinSelectionIndex();
						String c1 = inputConceptJList.getSelectedValue().toString();
						String c2 = resultPanel.getARTableRowConceptName(selectedRow);
						conceptDefinitionPanel.setCText(c1, c2);
						// System.out.println("-----" + selectedRow);
					}
				});

		resultPanel.getWASelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
			} else {
				int selectedRow = lsm.getMinSelectionIndex();
				// String c1 = comboBox.getSelectedItem().toString();
				String c1 = inputConceptJList.getSelectedValue().toString();
				String c2 = resultPanel.getWATableRowConceptName(selectedRow);
				conceptDefinitionPanel.setCText(c1, c2);
				// System.out.println("-----" + selectedRow);
			}
		});

	}

	public int getWSResultTableSelectedRow() {
		return resultPanel.getWSTableSelectedRow();
	}

	public int getAprioriResultTableSelectedRow() {
		return resultPanel.getARTableSelectedRow();
	}

	public int getWAResultTableSelectedRow() {
		return resultPanel.getWATableSelectedRow();
	}

	public DefaultTableModel getWSResultTableModel() {
		return resultPanel.getWResultTableModel();
	}

	public DefaultTableModel getAprioriResultTableModel() {
		return resultPanel.getAResultTableModel();
	}

	public DefaultTableModel getWAResultTableModel() {
		return resultPanel.getWAResultTableModel();
	}

	public void saveWordSpaceResult(File dir) {
		algorithmPanel.saveResult(dir, ConceptDefinitionAlgorithmPanel.WORDSPACE);
	}

	public void saveWordSpaceResult(int projectID, java.sql.Statement stmt) {
		algorithmPanel.saveResult(projectID, stmt, ConceptDefinitionAlgorithmPanel.WORDSPACE);
	}

	public void loadWordSpaceResult(File dir) {
		algorithmPanel.loadResult(dir, ConceptDefinitionAlgorithmPanel.WORDSPACE);
	}

	public void loadWordSpaceResult(int projectID, Statement stmt) {
		algorithmPanel.loadResult(projectID, stmt, ConceptDefinitionAlgorithmPanel.WORDSPACE);
	}

	public void saveAprioriResult(File dir) {
		algorithmPanel.saveResult(dir, ConceptDefinitionAlgorithmPanel.APRIORI);
	}

	public void saveAprioriResult(int projectID, java.sql.Statement stmt) {
		algorithmPanel.saveResult(projectID, stmt, ConceptDefinitionAlgorithmPanel.APRIORI);
	}

	public void loadAprioriResult(File dir) {
		algorithmPanel.loadResult(dir, ConceptDefinitionAlgorithmPanel.APRIORI);
	}

	public void loadAprioriResult(int projectID, Statement stmt) {
		algorithmPanel.loadResult(projectID, stmt, ConceptDefinitionAlgorithmPanel.APRIORI);
	}

	public void saveConceptDefinition(File file) {
		resultPanel.saveConceptDefinition(file);
	}

	public void loadConceptDefinition(File file) {
		resultPanel.loadConceptDefinition(file);
	}

	public void saveWrongPairSet(File file) {
		resultPanel.saveWrongPairSet(file);
	}

	public void loadWrongPairSet(File file) {
		resultPanel.loadWrongPairSet(file);
	}

	public void loadWrongPairSet(int projectID, Statement stmt) {
		resultPanel.loadWrongPairSet(projectID, stmt);
	}

	public void saveConeptDefinitionParameters(File file) {
		algorithmPanel.saveConceptDefinitionParameters(file);
	}

	public void saveConeptDefinitionParameters(int projectID, Statement stmt) {
		algorithmPanel.saveConceptDefinitionParameters(projectID, stmt);
	}

	public void loadConceptDefinitionParameters(File file) {
		algorithmPanel.loadConceptDefinitionParameters(file);
	}

	public void loadConceptDefinitionParameters(int projectID, Statement stmt) {
		algorithmPanel.loadConceptDefinitionParameters(projectID, stmt);
	}

	public void setInputConceptSet() {
		algorithmPanel.setInputConcept();
	}

	public void setInputDocList() {
		resultPanel.setInputDocList();
		setInputConceptSet();
	}

	public ConceptPair getPair(String str, List list) {
		for (Object o : list) {
			if (((ConceptPair) o).getCombinationToString().equals(str)) {
				return (ConceptPair) o;
			}
		}
		return null;
	}

	public boolean contains(List list, ConceptPair pair) {
		for (Object o : list) {
			if (pair.isSameCombination((ConceptPair) o)) {
				return true;
			}
		}
		return false;
	}

	public List makeValidList(List list) {
		List returnList = new ArrayList();
		List resultA = (ArrayList) list.get(0);
		boolean flag = false;
		for (Object o : resultA) {
			ConceptPair pair = (ConceptPair) o;
			for (int i = 1; i < list.size(); i++) {
				List resultB = (List) list.get(i);
				flag = contains(resultB, pair);
			}
			if (flag) {
				returnList.add(pair.getCombinationToString());
			}
		}
		return returnList;
	}

	public ConceptPair getSameCombination(ConceptPair pair, List list) {
		for (Object o : list) {
			ConceptPair item = (ConceptPair) o;
			if (item.isSameCombination(pair)) {
				return item;
			}
		}
		return null;
	}

	public Set<Document> getDocSet() {
		return docSelectionPanel.getDocSet();
	}

	public List<String> getInputTermList() {
		List<String> inputTermList = new ArrayList<>();
		wordCorrespondConceptSetMap = inputConceptSelectionPanel.getTermCorrespondConceptSetMap();
		if (wordCorrespondConceptSetMap != null) {
			termConceptSetMap = inputConceptSelectionPanel.getTermConceptSetMap();
			compoundWordConceptMap = doddleProjectPanel.getConstructClassPanel()
					.getCompoundWordConceptMap();
			Set<InputTermModel> inputWordModelSet = inputConceptSelectionPanel
					.getInputTermModelSet();
			for (InputTermModel iwModel : inputWordModelSet) {
				if (!iwModel.isSystemAdded()) {
					inputTermList.add(iwModel.getTerm());
				}
			}
			DefaultListModel undefinedTermListModel = inputConceptSelectionPanel
					.getUndefinedTermListPanel().getModel();
			for (int i = 0; i < undefinedTermListModel.size(); i++) {
				String undefTerm = (String) undefinedTermListModel.getElementAt(i);
				inputTermList.add(undefTerm);
			}
			Collections.sort(inputTermList);
		}
		return inputTermList;
	}

	public Set<String> getCompoundWordSet() {
		Set<String> compoundWordSet = new HashSet<>();
		Set<String> termSet = termConceptSetMap.keySet();
		for (String w : termSet) {
			if (w.contains(" ")) {
				compoundWordSet.add(w);
			}
		}
		Set<String> partialMatchedTermSet = compoundWordConceptMap.keySet();
		if (partialMatchedTermSet != null) {
			compoundWordSet.addAll(partialMatchedTermSet);
		}
		return compoundWordSet;
	}
}