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

import net.infonode.docking.*;
import net.infonode.docking.util.ViewMap;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.models.JWODic;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Takeshi Morita
 */
public class ReferenceOntologySelectionPanel extends JPanel implements ActionListener {

	private JButton nextTabButton;
	private NameSpaceTable nsTable;

	private SwoogleWebServiceWrapperPanel swoogleWebServiceWrapperPanel;
	private GeneralOntologySelectionPanel generalOntologySelectionPanel;
	private OWLOntologySelectionPanel owlOntologySelectionPanel;

	private View[] mainViews;
	private RootWindow rootWindow;

	public ReferenceOntologySelectionPanel() {
		nsTable = new NameSpaceTable();
		owlOntologySelectionPanel = new OWLOntologySelectionPanel(nsTable);
		generalOntologySelectionPanel = new GeneralOntologySelectionPanel(nsTable);
		if (DODDLEConstants.IS_INTEGRATING_SWOOGLE) {
			swoogleWebServiceWrapperPanel = new SwoogleWebServiceWrapperPanel(nsTable,
					owlOntologySelectionPanel);
		}

		nextTabButton = new JButton(Translator.getTerm("DocumentSelectionPanel"));
		nextTabButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(nextTabButton, BorderLayout.EAST);

		mainViews = new View[4];
		ViewMap viewMap = new ViewMap();
		mainViews[0] = new View(Translator.getTerm("GenericOntologySelectionPanel"), null,
				generalOntologySelectionPanel);
		mainViews[1] = new View(Translator.getTerm("OWLOntologySelectionPanel"), null,
				owlOntologySelectionPanel);
		mainViews[2] = new View(Translator.getTerm("NameSpaceTable"), null, nsTable);
		if (DODDLEConstants.IS_INTEGRATING_SWOOGLE) {
			mainViews[3] = new View(Translator.getTerm("SwoogleWebServiceWrapperPanel"), null,
					swoogleWebServiceWrapperPanel);
		} else {
			mainViews[3] = new View(Translator.getTerm("SwoogleWebServiceWrapperPanel"), null,
					new JPanel());
		}

		for (int i = 0; i < mainViews.length; i++) {
			viewMap.addView(i, mainViews[i]);
		}
		rootWindow = Utils.createDODDLERootWindow(viewMap);
		setLayout(new BorderLayout());
		add(rootWindow, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void setInputWordArea(String inputWordText) {
		swoogleWebServiceWrapperPanel.setInputWordArea(inputWordText);
	}

	public void setXGALayout() {
		SplitWindow sw1 = new SplitWindow(false, 0.2f, mainViews[0], mainViews[2]);
		TabWindow tabWindow = new TabWindow(new DockingWindow[] { sw1, mainViews[3], mainViews[1] });
		rootWindow.setWindow(tabWindow);
		mainViews[0].restoreFocus();
	}

	public void setUXGALayout() {
		SplitWindow sw1 = new SplitWindow(false, 0.25f, mainViews[0], mainViews[1]);
		SplitWindow sw2 = new SplitWindow(false, 0.75f, sw1, mainViews[2]);
		TabWindow tabWindow = new TabWindow(new DockingWindow[] { mainViews[3], sw2 });
		rootWindow.setWindow(tabWindow);
	}

	public NameSpaceTable getNSTable() {
		return nsTable;
	}

	public String getPrefix(String ns) {
		return nsTable.getPrefix(ns);
	}

	public void actionPerformed(ActionEvent e) {
		DODDLE_OWL.setSelectedIndex(DODDLEConstants.INPUT_DOCUMENT_SELECTION_PANEL);
	}

	public void saveOWLMetaDataSet(File saveDir) {
		owlOntologySelectionPanel.saveOWLMetaDataSet(saveDir);
	}

	public void loadOWLMetaDataSet(File loadDir) {
		owlOntologySelectionPanel.loadOWLMetaDataSet(loadDir);
	}

	public void saveGeneralOntologyInfo(File saveFile) {
		generalOntologySelectionPanel.saveGeneralOntologyInfo(saveFile);
	}

	public void loadGeneralOntologyInfo(File loadFile) {
		generalOntologySelectionPanel.loadGeneralOntologyInfo(loadFile);
	}

	public String getEnableDicList() {
		return generalOntologySelectionPanel.getEnableDicList();
	}

	public boolean isEDREnable() {
		return generalOntologySelectionPanel.isEDREnable();
	}

	public boolean isEDRTEnable() {
		return generalOntologySelectionPanel.isEDRTEnable();
	}

	public boolean isWordNetEnable() {
		return generalOntologySelectionPanel.isWordNetEnable();
	}

	public boolean isJpnWordNetEnable() {
		return generalOntologySelectionPanel.isJpnWordNetEnable();
	}

	public boolean isJWOEnable() {
		return generalOntologySelectionPanel.isJWOEnable();
	}

	public void resetGeneralOntologiesCheckBoxes() {
		generalOntologySelectionPanel.resetCheckBoxes();
	}

}
