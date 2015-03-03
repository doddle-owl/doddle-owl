/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2009 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class GeneralOntologySelectionPanel extends JPanel implements ActionListener, ChangeListener {
	private JCheckBox edrCheckBox;
	private JCheckBox edrtCheckBox;
	private JCheckBox wnCheckBox;
	private JCheckBox jpnWnCheckBox;

	private JRadioButton wn30RadioButton;
	private JRadioButton wn31RadioButton;
	private JPanel wnVersionSelectionPanel;

	public GeneralOntologySelectionPanel() {
		edrCheckBox = new JCheckBox(Translator.getTerm("GenericEDRCheckBox"), false);
		edrCheckBox.addActionListener(this);
		edrtCheckBox = new JCheckBox(Translator.getTerm("TechnicalEDRCheckBox"), false);
		edrtCheckBox.addActionListener(this);
		wnCheckBox = new JCheckBox(Translator.getTerm("WordNetCheckBox"), false);
		wnCheckBox.addActionListener(this);
		wnVersionSelectionPanel = new JPanel();
		wn30RadioButton = new JRadioButton("3.0");
		wn30RadioButton.addChangeListener(this);
		wn31RadioButton = new JRadioButton("3.1");
		wn31RadioButton.setSelected(true);
		wn31RadioButton.addChangeListener(this);
		ButtonGroup group = new ButtonGroup();
		group.add(wn30RadioButton);
		group.add(wn31RadioButton);
		wnVersionSelectionPanel.add(wnCheckBox);
		wnVersionSelectionPanel.add(wn30RadioButton);
		wnVersionSelectionPanel.add(wn31RadioButton);
		JPanel borderPanel = new JPanel();
		borderPanel.setLayout(new BorderLayout());
		borderPanel.add(wnVersionSelectionPanel, BorderLayout.WEST);

		jpnWnCheckBox = new JCheckBox(Translator.getTerm("JpnWordNetCheckBox"), false);
		jpnWnCheckBox.addActionListener(this);
		JPanel checkPanel = new JPanel();
		checkPanel.add(borderPanel);
		checkPanel.add(jpnWnCheckBox);
		checkPanel.add(edrCheckBox);
		checkPanel.add(edrtCheckBox);
		setLayout(new BorderLayout());
		add(checkPanel, BorderLayout.WEST);
	}

	public void saveGeneralOntologyInfo(File saveFile) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile),
					"UTF-8"));
			Properties properties = new Properties();
			properties.setProperty("EDR(general)", String.valueOf(isEDREnable()));
			properties.setProperty("EDR(technical)", String.valueOf(isEDRTEnable()));
			properties.setProperty("WordNet", String.valueOf(isWordNetEnable()));
			properties.setProperty("JPN WordNet", String.valueOf(isJpnWordNetEnable()));
			properties.store(writer, "Ontology Info");
		} catch (IOException ioex) {
			ioex.printStackTrace();
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

	public void insertGeneralOntologyInfo(int projectID, Statement stmt) {
		try {
			String sql = "INSERT INTO general_ontology_info (Project_ID,EDR_General,EDR_Technical,WordNet) "
					+ "VALUES("
					+ projectID
					+ ",'"
					+ DBManagerDialog.getMySQLBoolean(isEDREnable())
					+ "','"
					+ DBManagerDialog.getMySQLBoolean(isEDRTEnable())
					+ "','"
					+ DBManagerDialog.getMySQLBoolean(isWordNetEnable()) + "')";
			stmt.executeUpdate(sql);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public void saveGeneralOntologyInfoToDB(int projectID, Statement stmt) {
		DBManagerDialog.deleteTableContents(projectID, stmt, "general_ontology_info");
		insertGeneralOntologyInfo(projectID, stmt);
	}

	public void loadGeneralOntologyInfo(File loadFile) {
		if (!loadFile.exists()) {
			return;
		}
		BufferedReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(loadFile);
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			Properties properties = new Properties();
			properties.load(reader);
			boolean t = new Boolean(properties.getProperty("EDR(general)"));
			edrCheckBox.setSelected(t);
			enableEDRDic(t);
			t = new Boolean(properties.getProperty("EDR(technical)"));
			edrtCheckBox.setSelected(t);
			enableEDRTDic(t);
			t = new Boolean(properties.getProperty("WordNet"));
			wnCheckBox.setSelected(t);
			enableWordNetDic(t);
			t = new Boolean(properties.getProperty("JPN WordNet"));
			jpnWnCheckBox.setSelected(t);
			enableJpnWordNetDic(t);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ioe2) {
				ioe2.printStackTrace();
			}
		}
	}

	public void loadGeneralOntologyInfo(int projectID, Statement stmt) {
		try {
			String sql = "SELECT * from general_ontology_info where Project_ID=" + projectID;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				boolean t = DBManagerDialog.getMySQLBoolean(rs.getInt("EDR_General"));
				edrCheckBox.setSelected(t);
				enableEDRDic(t);
				t = DBManagerDialog.getMySQLBoolean(rs.getInt("EDR_Technical"));
				edrtCheckBox.setSelected(t);
				enableEDRTDic(t);
				t = DBManagerDialog.getMySQLBoolean(rs.getInt("WordNet"));
				wnCheckBox.setSelected(t);
				enableWordNetDic(t);
				t = DBManagerDialog.getMySQLBoolean(rs.getInt("JPN_WordNet"));
				jpnWnCheckBox.setSelected(t);
				enableJpnWordNetDic(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getEnableDicList() {
		StringBuilder builder = new StringBuilder();
		if (isEDREnable()) {
			builder.append("EDR一般辞書 ");
		}
		if (isEDRTEnable()) {
			builder.append("EDR専門辞書 ");
		}
		if (isWordNetEnable()) {
			builder.append("WordNet ");
		}
		if (isJpnWordNetEnable()) {
			builder.append("JPN WordNet ");
		}
		return builder.toString();
	}

	public boolean isEDREnable() {
		return edrCheckBox.isEnabled() && edrCheckBox.isSelected();
	}

	public boolean isEDRTEnable() {
		return edrtCheckBox.isEnabled() && edrtCheckBox.isSelected();
	}

	public boolean isWordNetEnable() {
		return wnCheckBox.isEnabled() && wnCheckBox.isSelected();
	}

	public boolean isJpnWordNetEnable() {
		return jpnWnCheckBox.isEnabled() && jpnWnCheckBox.isSelected();
	}

	private void enableEDRDic(boolean t) {
		if (t) {
			boolean isInitEDRDic = EDRDic.initEDRDic();
			EDRTree edrTree = EDRTree.getEDRTree();
			boolean isInitEDRTree = (edrTree != null);
			edrCheckBox.setEnabled(isInitEDRDic && isInitEDRTree);
			if (!edrCheckBox.isEnabled()) {
				edrCheckBox.setSelected(false);
			}
			DODDLE.STATUS_BAR.addValue();
		}
	}

	private void enableEDRTDic(boolean t) {
		if (t) {
			edrtCheckBox.setEnabled(EDRDic.initEDRTDic());
			if (!edrtCheckBox.isEnabled()) {
				edrtCheckBox.setSelected(false);
			}
		}
	}

	private void enableWordNetDic(boolean t) {
		if (t) {
			WordNetDic wnDic = WordNetDic.getInstance();
			wnCheckBox.setEnabled(wnDic != null);
			if (!wnCheckBox.isEnabled()) {
				wnCheckBox.setSelected(false);
				WordNetDic.resetWordNet();
			}
		}
	}

	private void enableJpnWordNetDic(boolean t) {
		if (t) {
			boolean isInitJPNWNDic = JpnWordNetDic.initJPNWNDic();
			JPNWNTree jpnWnTree = JPNWNTree.getJPNWNTree();
			boolean isInitJPNWNTree = (jpnWnTree != null);
			jpnWnCheckBox.setEnabled(isInitJPNWNDic && isInitJPNWNTree);
			if (!jpnWnCheckBox.isEnabled()) {
				jpnWnCheckBox.setSelected(false);
			}
			DODDLE.STATUS_BAR.addValue();
		}
	}

	/**
	 * オプションダイアログでパスを変更した場合は，再度，チェックできるようにする．
	 */
	public void resetCheckBoxes() {
		edrCheckBox.setEnabled(true);
		edrtCheckBox.setEnabled(true);
		wnCheckBox.setEnabled(true);
		jpnWnCheckBox.setEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		DODDLEProject project = DODDLE.getCurrentProject();
		if (e.getSource() == edrCheckBox) {
			enableEDRDic(edrCheckBox.isSelected());
			project.addLog("GenericEDRCheckBox", edrCheckBox.isSelected());
		} else if (e.getSource() == edrtCheckBox) {
			enableEDRTDic(edrtCheckBox.isSelected());
			project.addLog("TechnicalEDRCheckBox", edrtCheckBox.isSelected());
		} else if (e.getSource() == wnCheckBox) {
			enableWordNetDic(wnCheckBox.isSelected());
			wnVersionSelectionPanel.setEnabled(wnCheckBox.isSelected());
			project.addLog("WordNetCheckBox", wnCheckBox.isSelected());
		} else if (e.getSource() == jpnWnCheckBox) {
			enableJpnWordNetDic(jpnWnCheckBox.isSelected());
			project.addLog("JpnWordNetCheckBox", jpnWnCheckBox.isSelected());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == wn30RadioButton) {
			DODDLEConstants.ENWN_HOME = DODDLEConstants.ENWN_3_0_HOME;
			WordNetDic.resetWordNet();
		} else if (e.getSource() == wn31RadioButton) {
			DODDLEConstants.ENWN_HOME = DODDLEConstants.ENWN_3_1_HOME;
			WordNetDic.resetWordNet();
		}
	}
}
