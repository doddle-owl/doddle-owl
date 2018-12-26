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

package org.doddle_owl.views;

import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.Concept;
import org.doddle_owl.models.ReferenceOWLOntology;
import org.doddle_owl.models.SwoogleWebServiceData;
import org.doddle_owl.utils.SwoogleWebServiceWrapper;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author Takeshi Morita
 */
public class SwoogleWebServiceWrapperPanel extends JPanel implements ActionListener,
		ListSelectionListener {

	private JLabel termRankLabel;

	private JTextArea inputWordArea;
	private JButton acquireOntologiesButton;

	private JList acquiredOntologyJList;
	private DefaultListModel acquiredOntologyModel;
	private JList removedOntologyJList;
	private DefaultListModel removedOntologyModel;
	private JButton setOWLOntologiesButton;
	private JButton removeOntologyButton;
	private JButton returnOntologyButton;

	private OWLMetaDataTablePanel owlMetaDataTablePanel;
	private LiteralPanel labelPanel;
	private LiteralPanel descriptionPanel;
	private JList classJList;
	private DefaultListModel classListModel;
	private JList propertyJList;
	private DefaultListModel propertyListModel;
	private JList domainJList;
	private JList rangeJList;

	private OWLOntologySelectionPanel owlOntologySelectionPanel;

	public SwoogleWebServiceWrapperPanel(NameSpaceTable nsTable, OWLOntologySelectionPanel owlPanel) {
		owlOntologySelectionPanel = owlPanel;

		SwoogleWebServiceWrapper.setNameSpaceTable(nsTable);
		SwoogleWebServiceWrapper.initSwoogleWebServiceWrapper();

		inputWordArea = new JTextArea();
		inputWordArea.setPreferredSize(new Dimension(100, 100));
		inputWordArea.setMinimumSize(new Dimension(100, 100));
		JScrollPane inputWordAreaScroll = new JScrollPane(inputWordArea);
		inputWordAreaScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("InputTermListArea")));
		acquireOntologiesButton = new JButton(Translator.getTerm("ExtractOntologiesButton"));
		acquireOntologiesButton.addActionListener(this);

		JPanel inputWordAreaPanel = new JPanel();
		inputWordAreaPanel.setLayout(new BorderLayout());
		inputWordAreaPanel.add(inputWordAreaScroll, BorderLayout.CENTER);
		inputWordAreaPanel.add(Utils.createEastPanel(acquireOntologiesButton), BorderLayout.SOUTH);

		acquiredOntologyModel = new DefaultListModel();
		acquiredOntologyJList = new JList(acquiredOntologyModel);
		acquiredOntologyJList.addListSelectionListener(this);
		JScrollPane acquiredOntologyJListScroll = new JScrollPane(acquiredOntologyJList);
		acquiredOntologyJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("ExtractedOntologyList")));

		setOWLOntologiesButton = new JButton(Translator.getTerm("SetOWLOntologiesButton"));
		setOWLOntologiesButton.addActionListener(this);
		removeOntologyButton = new JButton(Translator.getTerm("RemoveButton"));
		removeOntologyButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(setOWLOntologiesButton);
		buttonPanel.add(removeOntologyButton);
		JPanel acquiredOntologyPanel = new JPanel();
		acquiredOntologyPanel.setLayout(new BorderLayout());
		acquiredOntologyPanel.add(acquiredOntologyJListScroll, BorderLayout.CENTER);
		acquiredOntologyPanel.add(Utils.createEastPanel(buttonPanel), BorderLayout.SOUTH);

		removedOntologyModel = new DefaultListModel();
		removedOntologyJList = new JList(removedOntologyModel);
		JScrollPane removedOntologyJListScroll = new JScrollPane(removedOntologyJList);
		removedOntologyJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("UnnecessaryOntologyList")));
		returnOntologyButton = new JButton(Translator.getTerm("ReturnButton"));
		returnOntologyButton.addActionListener(this);
		JPanel removedOntologyPanel = new JPanel();
		removedOntologyPanel.setLayout(new BorderLayout());
		removedOntologyPanel.add(removedOntologyJListScroll, BorderLayout.CENTER);
		removedOntologyPanel.add(Utils.createEastPanel(returnOntologyButton), BorderLayout.SOUTH);

		JPanel ontologyListPanel = new JPanel();
		ontologyListPanel.setLayout(new GridLayout(2, 1));
		ontologyListPanel.add(acquiredOntologyPanel);
		ontologyListPanel.add(removedOntologyPanel);

		owlMetaDataTablePanel = new OWLMetaDataTablePanel();
		owlMetaDataTablePanel.setPreferredSize(new Dimension(200, 100));
		owlMetaDataTablePanel.setMinimumSize(new Dimension(200, 100));

		termRankLabel = new JLabel(Translator.getTerm("TermRankLabel") + ": ");

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(owlMetaDataTablePanel, BorderLayout.CENTER);
		northPanel.add(termRankLabel, BorderLayout.SOUTH);

		labelPanel = new LiteralPanel(Translator.getTerm("LanguageLabel"),
				Translator.getTerm("LabelList"), LiteralPanel.LABEL);
		descriptionPanel = new LiteralPanel(Translator.getTerm("LanguageLabel"),
				Translator.getTerm("DescriptionList"), LiteralPanel.DESCRIPTION);
		JPanel labelAndDescriptionPanel = new JPanel();
		labelAndDescriptionPanel.setLayout(new GridLayout(1, 2));
		labelAndDescriptionPanel.add(labelPanel);
		labelAndDescriptionPanel.add(descriptionPanel);

		classListModel = new DefaultListModel();
		classJList = new JList(classListModel);
		classJList.addListSelectionListener(this);
		JScrollPane classJListScroll = new JScrollPane(classJList);
		classJListScroll
				.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("ClassList")));
		propertyListModel = new DefaultListModel();
		propertyJList = new JList(propertyListModel);
		propertyJList.addListSelectionListener(this);
		JScrollPane propertyJListScroll = new JScrollPane(propertyJList);
		propertyJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("PropertyList")));

		JPanel conceptPanel = new JPanel();
		conceptPanel.setLayout(new GridLayout(1, 2));
		conceptPanel.add(classJListScroll);
		conceptPanel.add(propertyJListScroll);

		domainJList = new JList();
		JScrollPane domainJListScroll = new JScrollPane(domainJList);
		domainJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("DomainList")));
		rangeJList = new JList();
		JScrollPane rangeJListScroll = new JScrollPane(rangeJList);
		rangeJListScroll
				.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("RangeList")));

		JPanel regionPanel = new JPanel();
		regionPanel.setLayout(new GridLayout(1, 2));
		regionPanel.add(domainJListScroll);
		regionPanel.add(rangeJListScroll);

		JPanel conceptInfoPanel = new JPanel();
		conceptInfoPanel.setLayout(new GridLayout(2, 1));
		conceptInfoPanel.add(conceptPanel);
		conceptInfoPanel.add(labelAndDescriptionPanel);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(northPanel, BorderLayout.NORTH);
		centerPanel.add(conceptInfoPanel, BorderLayout.CENTER);
		centerPanel.add(regionPanel, BorderLayout.SOUTH);

		JPanel westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(1, 2));
		westPanel.add(inputWordAreaPanel);
		westPanel.add(ontologyListPanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPanel, centerPanel);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	public void initListData() {
		acquiredOntologyModel.clear();
		removedOntologyModel.clear();
		classListModel.clear();
		propertyListModel.clear();
		domainJList.setListData(new Object[0]);
		rangeJList.setListData(new Object[0]);
	}

	public void setInputWordArea(String inputWordText) {
		inputWordArea.setText(inputWordText);
	}

	private void acquireOntologies() {
		initListData();
		Set<String> inputWordSet = new HashSet<>();
		String[] inputWords = inputWordArea.getText().split("\n");
		Collections.addAll(inputWordSet, inputWords);
		SwoogleWebServiceWrapper.acquireRelevantOWLOntologies(inputWordSet, true);
		SwoogleWebServiceData swServiceData = SwoogleWebServiceWrapper.getSwoogleWebServiceData();

		Object[] refOntologies = swServiceData.getRefOntologies().toArray();
		Arrays.sort(refOntologies);
		for (Object refOntology : refOntologies) {
			acquiredOntologyModel.addElement(refOntology);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == acquireOntologiesButton) {
			SwingWorker<String, String> worker = new SwingWorker<>() {
				public String doInBackground() {
					acquireOntologies();
					return "done";
				}
			};
			DODDLE_OWL.STATUS_BAR.setSwingWorker(worker);
			worker.execute();
		} else if (e.getSource() == removeOntologyButton) {
			List selectedOntologies = acquiredOntologyJList.getSelectedValuesList();
			for (Object ont: selectedOntologies) {
				removedOntologyModel.addElement(ont);
				acquiredOntologyModel.removeElement(ont);
			}
		} else if (e.getSource() == returnOntologyButton) {
			List selectedOntologies = removedOntologyJList.getSelectedValuesList();
			for (Object ont: selectedOntologies) {
				acquiredOntologyModel.addElement(ont);
				removedOntologyModel.removeElement(ont);
			}
		} else if (e.getSource() == setOWLOntologiesButton) {
			for (int i = 0; i < acquiredOntologyModel.getSize(); i++) {
				ReferenceOWLOntology refOnto = (ReferenceOWLOntology) acquiredOntologyModel.get(i);
				owlOntologySelectionPanel.addOWLOntology(refOnto);
			}
		}
	}

	private void setLabelAndDescriptionPanelData(Concept concept) {
		labelPanel.setSelectedConcept(concept);
		descriptionPanel.setSelectedConcept(concept);
		labelPanel.setLabelLangList();
		labelPanel.setLabelList();
		descriptionPanel.setDescriptionLangList();
		descriptionPanel.setDescriptionList();
	}

	public void valueChanged(ListSelectionEvent e) {
		ReferenceOWLOntology refOnto = (ReferenceOWLOntology) acquiredOntologyJList
				.getSelectedValue();
		if (refOnto == null) {
			return;
		}
		if (e.getSource() == acquiredOntologyJList) {
			termRankLabel.setText(Translator.getTerm("TermRankLabel") + ": ");
			classListModel.clear();
			propertyListModel.clear();
			labelPanel.clearData();
			descriptionPanel.clearData();

			owlMetaDataTablePanel.setModel(refOnto.getOWLMetaDataTableModel());
			SwoogleWebServiceData swServiceData = SwoogleWebServiceWrapper
					.getSwoogleWebServiceData();
			Set<Resource> relevantClassSet = swServiceData.getClassSet();
			for (String uri : refOnto.getClassSet()) {
				Resource classRes = ResourceFactory.createResource(uri);
				if (relevantClassSet.contains(classRes)) {
					classListModel.addElement(classRes);
				}
			}
			Set<Resource> relevantPropertySet = swServiceData.getPropertySet();
			for (String uri : refOnto.getPropertySet()) {
				Resource propRes = ResourceFactory.createResource(uri);
				if (relevantPropertySet.contains(propRes)) {
					propertyListModel.addElement(propRes);
				}
			}
		} else if (e.getSource() == classJList) {
			Resource classResource = (Resource) classJList.getSelectedValue();
			if (classResource != null) {
				Concept concept = refOnto.getConcept(classResource.getURI());
				setLabelAndDescriptionPanelData(concept);
				Double termRank = SwoogleWebServiceWrapper.getSwoogleWebServiceData().getTermRank(
						classResource.getURI());
				if (termRank != null) {
					termRankLabel.setText(Translator.getTerm("TermRankLabel") + ": " + termRank);
				}
			}
		} else if (e.getSource() == propertyJList) {
			Resource propertyResource = (Resource) propertyJList.getSelectedValue();
			if (propertyResource != null) {
				Concept concept = refOnto.getConcept(propertyResource.getURI());
				setLabelAndDescriptionPanelData(concept);
				domainJList.setListData(refOnto.getDomainSet(propertyResource.getURI()).toArray());
				rangeJList.setListData(refOnto.getRangeSet(propertyResource.getURI()).toArray());
				Double termRank = SwoogleWebServiceWrapper.getSwoogleWebServiceData().getTermRank(
						propertyResource.getURI());
				if (termRank != null) {
					termRankLabel.setText(Translator.getTerm("TermRankLabel") + ": " + termRank);
				}
			}
		}
	}
}
