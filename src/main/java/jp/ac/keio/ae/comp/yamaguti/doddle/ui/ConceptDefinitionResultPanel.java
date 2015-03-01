/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;

import jp.ac.keio.ae.comp.yamaguti.doddle.DODDLEProject;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.Concept;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.ConceptPair;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.ConceptTreeCellRenderer;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.ConceptTreeNode;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.Document;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.NonTaxonomicRelation;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.WrongPair;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.Translator;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.Utils;

/**
 * @author shigeta
 * @author Takeshi Morita
 */
public class ConceptDefinitionResultPanel extends JPanel implements ActionListener,
		ListSelectionListener, TableModelListener {

	private JList inputConceptJList;
	private ConceptDefinitionAlgorithmPanel algorithmPanel;

	private Set<NonTaxonomicRelation> nonTaxRelSet;
	private Set<WrongPair> wrongPairSet;

	private JList inputDocJList;

	private JTable wsResultTable;
	private JTable arResultTable;
	private JTable waResultTable;

	private JTable conceptDefinitionTable;
	private JButton setRelationButton;
	private JButton deleteAcceptedPairButton;

	private JTable wrongPairTable;
	private JButton deleteWrongPairButton;

	private DODDLEProject doddleProject;
	private ConstructPropertyPanel constructPropertyTreePanel;

	private static final String[] WS_COLUMN_NAMES = { Translator.getTerm("RelatedConceptList"),
			Translator.getTerm("WordSpaceValueLabel") };
	private static final String[] AR_COLUMN_NAMES = { Translator.getTerm("RelatedConceptList"),
			Translator.getTerm("AprioriValueLabel") };
	private static final String[] WA_COLUMN_NAMES = { Translator.getTerm("RelatedConceptList"),
			Translator.getTerm("WordSpaceValueLabel"), Translator.getTerm("AprioriValueLabel") };

	private JPanel inputConceptPanel;
	private JPanel inputDocPanel;
	private JPanel acceptedPairPanel;
	private JPanel wrongPairPanel;

	public JPanel getInputConceptPanel() {
		return inputConceptPanel;
	}

	public JPanel getAcceptedPairPanel() {
		return acceptedPairPanel;
	}

	public JPanel getWrongPairPanel() {
		return wrongPairPanel;
	}

	public JPanel getInputDocPanel() {
		return inputDocPanel;
	}

	public JTable getWordSpaceResultTable() {
		return wsResultTable;
	}

	public JTable getAprioriResultTable() {
		return arResultTable;
	}

	public JTable getWAResultTable() {
		return waResultTable;
	}

	public ConceptDefinitionResultPanel(JList icList, ConceptDefinitionAlgorithmPanel ap,
			DODDLEProject project) {
		algorithmPanel = ap;
		doddleProject = project;
		constructPropertyTreePanel = project.getConstructPropertyPanel();
		inputConceptJList = icList;

		nonTaxRelSet = new HashSet<NonTaxonomicRelation>();
		wrongPairSet = new HashSet<WrongPair>();

		definePanel = new ConceptDefinitionPanel();
		DefaultTableModel resultModel = new DefaultTableModel(null, WS_COLUMN_NAMES);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		inputDocJList = new JList();
		inputDocJList.addListSelectionListener(this);
		JScrollPane inputDocJListScroll = new JScrollPane(inputDocJList);
		inputDocJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("InputDocumentList")));
		inputDocJListScroll.setPreferredSize(new Dimension(80, 80));
		inputDocJListScroll.setMinimumSize(new Dimension(80, 80));
		inputDocPanel = new JPanel();
		inputDocPanel.setLayout(new BorderLayout());
		inputDocPanel.add(inputDocJListScroll, BorderLayout.CENTER);

		wsResultTable = new JTable(resultModel);
		// WresultTable.setBackground(Color.BLUE);

		resultModel = new DefaultTableModel(null, AR_COLUMN_NAMES);
		arResultTable = new JTable(resultModel);

		resultModel = new DefaultTableModel(null, WA_COLUMN_NAMES);
		waResultTable = new JTable(resultModel);

		String[] definedColumnNames = { Translator.getTerm("MetaPropertyLabel"),
				Translator.getTerm("DomainLabel"), Translator.getTerm("RelationLabel"),
				Translator.getTerm("RangeLabel") };
		ResultTableModel resultTableModel = new ResultTableModel(null, definedColumnNames);
		resultTableModel.addTableModelListener(this);
		conceptDefinitionTable = new JTable(resultTableModel);
		JScrollPane conceptDefinitionTableScroll = new JScrollPane(conceptDefinitionTable);
		setRelationButton = new JButton(Translator.getTerm("SetPropertyButton"));
		setRelationButton.addActionListener(this);
		deleteAcceptedPairButton = new JButton(Translator.getTerm("RemoveCorrectConceptPairButton"));
		deleteAcceptedPairButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(setRelationButton);
		buttonPanel.add(deleteAcceptedPairButton);
		acceptedPairPanel = new JPanel();
		acceptedPairPanel.setLayout(new BorderLayout());
		acceptedPairPanel.add(conceptDefinitionTableScroll, BorderLayout.CENTER);
		acceptedPairPanel.add(getEastComponent(buttonPanel), BorderLayout.SOUTH);

		String[] wrongDefinedColumnNames = { Translator.getTerm("DomainLabel"),
				Translator.getTerm("RangeLabel") };
		wrongPairTable = new JTable(new ResultTableModel(null, wrongDefinedColumnNames));
		JScrollPane wrongConceptPairTableScroll = new JScrollPane(wrongPairTable);
		deleteWrongPairButton = new JButton(Translator.getTerm("RemoveWrongConceptPairButton"));
		deleteWrongPairButton.addActionListener(this);
		wrongPairPanel = new JPanel();
		wrongPairPanel.setLayout(new BorderLayout());
		wrongPairPanel.add(wrongConceptPairTableScroll, BorderLayout.CENTER);
		wrongPairPanel.add(getEastComponent(deleteWrongPairButton), BorderLayout.SOUTH);

		JScrollPane inputConceptJListScroll = new JScrollPane(inputConceptJList);
		inputConceptJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
				.getTerm("InputConceptList")));
		inputConceptJListScroll.setPreferredSize(new Dimension(200, 100));

		inputConceptPanel = new JPanel();
		inputConceptPanel.setLayout(new BorderLayout());
		inputConceptPanel.add(inputConceptJListScroll, BorderLayout.CENTER);
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == inputDocJList) {
			reCalcWSandARValue();
		}
	}

	public void saveConceptDefinition(File file) {
		BufferedWriter writer = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
				builder.append(nonTaxRel);
				builder.append("\n");
			}
			writer.write(builder.toString());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
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

	public void saveConceptDefinition(int projectID, Statement stmt) {
		DBManagerDialog.deleteTableContents(projectID, stmt, "concept_definition");
		try {
			for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
				int isMetaProperty = DBManagerDialog.getMySQLBoolean(nonTaxRel.isMetaProperty());
				String domain = URLEncoder.encode(nonTaxRel.getDomain(), "UTF8");
				String relation = nonTaxRel.getRelation().getURI();
				String range = URLEncoder.encode(nonTaxRel.getRange(), "UTF8");
				String sql = "INSERT INTO concept_definition (Project_ID,is_Meta_Property,Term1,Relation,Term2) "
						+ "VALUES("
						+ projectID
						+ ","
						+ isMetaProperty
						+ ",'"
						+ domain
						+ "','"
						+ relation + "','" + range + "')";
				stmt.executeUpdate(sql);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void loadConceptDefinition(File file) {
		if (!file.exists()) {
			return;
		}
		BufferedReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			while (reader.ready()) {
				String line = reader.readLine();
				String[] lines = line.split("\t");
				boolean isMetaProperty = new Boolean(lines[0]);
				String domain = lines[1];
				Concept relation = doddleProject.getConstructPropertyPanel().getConcept(lines[2]);
				String range = lines[3];
				NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(domain, relation, range);
				nonTaxRel.setMetaProperty(isMetaProperty);
				addNonTaxonomicRelation(nonTaxRel);
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
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

	public void loadConceptDefinition(int projectID, Statement stmt) {
		try {
			String sql = "SELECT * from concept_definition where Project_ID=" + projectID;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				boolean isMetaProperty = DBManagerDialog.getMySQLBoolean(rs
						.getInt("is_Meta_Property"));
				String term1 = URLDecoder.decode(rs.getString("Term1"), "UTF8");
				Concept relation = doddleProject.getConstructPropertyPanel().getConcept(
						rs.getString("Relation"));
				String term2 = URLDecoder.decode(rs.getString("Term2"), "UTF8");
				NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(term1, relation, term2);
				nonTaxRel.setMetaProperty(isMetaProperty);
				addNonTaxonomicRelation(nonTaxRel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void saveWrongPairSet(File file) {
		BufferedWriter writer = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (WrongPair wp : wrongPairSet) {
				builder.append(wp.getDomain());
				builder.append("\t");
				builder.append(wp.getRange());
				builder.append("\n");
			}
			writer.write(builder.toString());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
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

	public void saveWrongPairSet(int projectID, Statement stmt) {
		DBManagerDialog.deleteTableContents(projectID, stmt, "wrong_pair");
		try {
			for (WrongPair wp : wrongPairSet) {
				String sql = "INSERT INTO wrong_pair (Project_ID,Term1,Term2) " + "VALUES("
						+ projectID + ",'" + URLEncoder.encode(wp.getDomain(), "UTF8") + "','"
						+ URLEncoder.encode(wp.getRange(), "UTF8") + "')";
				stmt.executeUpdate(sql);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void loadWrongPairSet(File file) {
		if (!file.exists()) {
			return;
		}
		BufferedReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			while (reader.ready()) {
				String line = reader.readLine();
				String[] lines = line.split("\t");
				WrongPair wp = new WrongPair(lines[0], lines[1]);
				addWrongPair(wp);
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
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

	public void loadWrongPairSet(int projectID, Statement stmt) {
		try {
			String sql = "SELECT * from wrong_pair where Project_ID=" + projectID;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String term1 = URLDecoder.decode(rs.getString("Term1"), "UTF8");
				String term2 = URLDecoder.decode(rs.getString("Term2"), "UTF8");
				WrongPair wp = new WrongPair(term1, term2);
				addWrongPair(wp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void reCalcWSandARValue() {
		calcWSandARValue((String) inputConceptJList.getSelectedValue());
	}

	public void setInputDocList() {
		inputDocJList.setListData(doddleProject.getDocumentSelectionPanel().getDocSet().toArray());
	}

	public void calcWSandARValue(String selectedInputConcept) {
		Document currentDoc = (Document) inputDocJList.getSelectedValue();
		if (currentDoc == null) {
			return;
		}
		Set<ConceptPair> wsValidSet = null;
		Set<ConceptPair> arValidSet = null;
		Map<String, List<ConceptPair>> wsResult = algorithmPanel.getDocWordSpaceResult().get(
				currentDoc);
		Map<String, List<ConceptPair>> arResult = algorithmPanel.getDocAprioriResult().get(
				currentDoc);
		if (wsResult != null || arResult != null) {
			if (wsResult != null && wsResult.containsKey(selectedInputConcept)) {
				List<ConceptPair> wsConceptPairList = wsResult.get(selectedInputConcept);
				wsValidSet = setWSResultTable(selectedInputConcept, wsConceptPairList);
			} else {
				setWSResultTable(new DefaultTableModel(null,
						ConceptDefinitionResultPanel.WS_COLUMN_NAMES));
			}

			if (arResult != null && arResult.containsKey(selectedInputConcept)) {
				List<ConceptPair> arConceptPairList = arResult.get(selectedInputConcept);
				arValidSet = setARResultTable(selectedInputConcept, arConceptPairList);
			} else {
				setARResultTable(new DefaultTableModel(null,
						ConceptDefinitionResultPanel.AR_COLUMN_NAMES));
			}

			if (wsValidSet != null && arValidSet != null) {
				setWAResultTable(wsValidSet, arValidSet);
			} else {
				setWAResultTable(new DefaultTableModel(null,
						ConceptDefinitionResultPanel.WA_COLUMN_NAMES));
			}
		}
	}

	private void setWAResultTable(Set<ConceptPair> validWSPairSet, Set<ConceptPair> validARPairSet) {
		Set<String[]> validDataSet = new HashSet<String[]>();
		for (ConceptPair wsPair : validWSPairSet) {
			for (ConceptPair arPair : validARPairSet) {
				if (wsPair.getToConceptLabel().equals(arPair.getToConceptLabel())) {
					validDataSet.add(new String[] { wsPair.getTableData()[0],
							wsPair.getTableData()[1], arPair.getTableData()[1] });
				}
			}
		}

		String[][] data = new String[validDataSet.size()][3];
		int cnt = 0;
		for (Iterator i = validDataSet.iterator(); i.hasNext();) {
			data[cnt++] = (String[]) i.next();
		}
		setWAResultTable(new DefaultTableModel(data, ConceptDefinitionResultPanel.WA_COLUMN_NAMES));
	}

	public void setWAResultTable(DefaultTableModel dtm) {
		if (dtm != null) {
			waResultTable.setModel(dtm);
		} else {
			waResultTable.setModel(new DefaultTableModel(null, WA_COLUMN_NAMES));
		}
	}

	public int getRelationCount() {
		return conceptDefinitionTable.getModel().getRowCount();
	}

	public Object[] getRelation(int row) {
		Object[] relation = new Object[4];
		relation[0] = conceptDefinitionTable.getModel().getValueAt(row, 0).toString();
		relation[1] = (conceptDefinitionTable.getModel().getValueAt(row, 1));
		relation[2] = (conceptDefinitionTable.getModel().getValueAt(row, 2));
		relation[3] = (conceptDefinitionTable.getModel().getValueAt(row, 3));

		return relation;
	}

	public void addWrongPair(WrongPair wp) {
		DefaultTableModel model = (DefaultTableModel) wrongPairTable.getModel();
		if (!wrongPairSet.contains(wp)) {
			wrongPairSet.add(wp);
		}
		reCalcWSandARValue();
		// System.out.println(wrongPairSet);
		model.addRow(new Object[] { wp.getDomain(), wp.getRange() });
	}

	public void addNonTaxonomicRelation(NonTaxonomicRelation nonTaxRel) {
		if (nonTaxRel == null || !nonTaxRel.isValid()) {
			return;
		}
		if (nonTaxRelSet.contains(nonTaxRel)) {
			return;
		} // 定義域，値域，関係すべてが同じ定義は追加できない
		nonTaxRelSet.add(nonTaxRel);
		reCalcWSandARValue();
		// System.out.println(acceptedPairSet);
		DefaultTableModel model = (DefaultTableModel) conceptDefinitionTable.getModel();
		model.addRow(nonTaxRel.getAcceptedTableData());
	}

	/**
	 * Set WordSpace Result to Table
	 */
	public Set<ConceptPair> setWSResultTable(String selectedInputConcept,
			List<ConceptPair> wsConceptPairList) {
		Set<ConceptPair> validPairSet = new TreeSet<ConceptPair>();
		for (ConceptPair pair : wsConceptPairList) {
			String[] data = pair.getTableData();
			WrongPair wp = new WrongPair(selectedInputConcept, data[0]);
			if (!wrongPairSet.contains(wp)) {
				validPairSet.add(pair);
			}
		}
		String[][] data = new String[validPairSet.size()][2];
		int cnt = 0;
		for (ConceptPair rp : validPairSet) {
			data[cnt++] = rp.getTableData();
		}
		setWSResultTable(new DefaultTableModel(data, ConceptDefinitionResultPanel.WS_COLUMN_NAMES));
		return validPairSet;
	}

	public void setWSResultTable(DefaultTableModel dtm) {
		if (dtm != null) {
			wsResultTable.setModel(dtm);
		} else {
			wsResultTable.setModel(new DefaultTableModel(null, WS_COLUMN_NAMES));
		}
	}

	public Set<ConceptPair> setARResultTable(String inputConcept, List<ConceptPair> arConceptPairSet) {
		Set<ConceptPair> validPairSet = new TreeSet<ConceptPair>();
		for (ConceptPair pair : arConceptPairSet) {
			String[] data = pair.getTableData();
			WrongPair wp = new WrongPair(inputConcept, data[0]);
			if (!wrongPairSet.contains(wp)) {
				validPairSet.add(pair);
			}
		}
		String[][] data = new String[validPairSet.size()][2];
		int cnt = 0;
		for (ConceptPair rp : validPairSet) {
			data[cnt++] = rp.getTableData();
		}
		setARResultTable(new DefaultTableModel(data, ConceptDefinitionResultPanel.AR_COLUMN_NAMES));
		return validPairSet;
	}

	public void setARResultTable(DefaultTableModel dtm) {
		if (dtm != null) {
			arResultTable.setModel(dtm);
		} else {
			arResultTable.setModel(new DefaultTableModel(null, AR_COLUMN_NAMES));
		}
	}

	public DefaultTableModel getWResultTableModel() {
		return (DefaultTableModel) wsResultTable.getModel();
	}

	public DefaultTableModel getAResultTableModel() {
		return (DefaultTableModel) arResultTable.getModel();
	}

	public DefaultTableModel getWAResultTableModel() {
		return (DefaultTableModel) waResultTable.getModel();
	}

	public String getWSTableRowConceptName(int row) {
		return (String) ((DefaultTableModel) wsResultTable.getModel()).getValueAt(row, 0);
	}

	public String getARTableRowConceptName(int row) {
		return (String) ((DefaultTableModel) arResultTable.getModel()).getValueAt(row, 0);
	}

	public String getWATableRowConceptName(int row) {
		return (String) ((DefaultTableModel) waResultTable.getModel()).getValueAt(row, 0);
	}

	public int getWSTableSelectedRow() {
		return wsResultTable.getSelectedRow();
	}

	public int getARTableSelectedRow() {
		return arResultTable.getSelectedRow();
	}

	public int getWATableSelectedRow() {
		return waResultTable.getSelectedRow();
	}

	public ListSelectionModel getWordSpaceSelectionModel() {
		return wsResultTable.getSelectionModel();
	}

	public ListSelectionModel getAprioriSelectionModel() {
		return arResultTable.getSelectionModel();
	}

	public ListSelectionModel getWASelectionModel() {
		return waResultTable.getSelectionModel();
	}

	private ConceptDefinitionPanel definePanel;

	public ConceptDefinitionPanel getDefinePanel() {
		return definePanel;
	}

	private JComponent getEastComponent(JComponent c) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(c, BorderLayout.EAST);
		return p;
	}

	private Concept getPropertyRootConcept() {
		TreeModel treeModel = constructPropertyTreePanel.getIsaTree().getModel();
		if (treeModel.getRoot() instanceof ConceptTreeNode) {
			return ((ConceptTreeNode) treeModel.getRoot()).getConcept();
		}
		JOptionPane.showMessageDialog(this, "概念定義には，プロパティ階層の構築が必要です．", "Error",
				JOptionPane.ERROR_MESSAGE);
		return null;
	}

	private void setRelation() {
		if (conceptDefinitionTable.getSelectedRowCount() == 1) {
			ConceptSelectionDialog dialog = new ConceptSelectionDialog(
					ConceptTreeCellRenderer.VERB_CONCEPT_TREE, "Relation Selection Dialog");
			TreeModel treeModel = constructPropertyTreePanel.getIsaTree().getModel();
			if (treeModel.getRoot() instanceof ConceptTreeNode) {
				dialog.setTreeModel(treeModel);
				dialog.setSingleSelection();
				dialog.setVisible(true);
				Concept newRelation = dialog.getConcept();
				if (newRelation != null) {
					int row = conceptDefinitionTable.getSelectedRow();
					// Modelでアクセスする場合には，1,2,3で良い
					String domain = (String) conceptDefinitionTable.getModel().getValueAt(row, 1);
					Concept prevRelation = (Concept) conceptDefinitionTable.getModel().getValueAt(
							row, 2);
					String range = (String) conceptDefinitionTable.getModel().getValueAt(row, 3);
					conceptDefinitionTable.setValueAt(newRelation, row, 2);
					NonTaxonomicRelation prevNonTaxRel = new NonTaxonomicRelation(domain,
							prevRelation, range);
					for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
						if (nonTaxRel.equals(prevNonTaxRel)) { // 置換前の定義におけるプロパティを置換語のプロパティに変換
							nonTaxRel.setRelation(newRelation);
							return;
						}
					}
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == setRelationButton) {
			setRelation();
			doddleProject.addLog("SetPropertyButton");
		} else if (e.getSource() == deleteAcceptedPairButton) {
			deleteConceptDefinition();
			doddleProject.addLog("RemoveCorrectConceptPairButton");
		} else if (e.getSource() == deleteWrongPairButton) {
			deleteWrongPair();
			doddleProject.addLog("RemoveWrongConceptPairButton");
		}
	}

	class ResultTableModel extends DefaultTableModel {

		ResultTableModel(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}

		public Class getColumnClass(int column) {
			Vector v = (Vector) dataVector.elementAt(0);
			return v.elementAt(column).getClass();
		}
	}

	private void deleteConceptDefinition() {
		DefaultTableModel definedTableModel = (DefaultTableModel) conceptDefinitionTable.getModel();
		Set<NonTaxonomicRelation> deleteNonTaxRelSet = new HashSet<NonTaxonomicRelation>();
		int row[] = conceptDefinitionTable.getSelectedRows();

		if (row != null) {
			for (int i = 0; i < row.length; i++) {
				String domain = (String) definedTableModel.getValueAt(row[i], 1);
				Concept rel = (Concept) definedTableModel.getValueAt(row[i], 2);
				String range = (String) definedTableModel.getValueAt(row[i], 3);
				NonTaxonomicRelation delNonTaxRel = new NonTaxonomicRelation(domain, rel, range);
				deleteNonTaxRelSet.add(delNonTaxRel);
				nonTaxRelSet.remove(delNonTaxRel);
			}
			reCalcWSandARValue();
			for (int i = 0; i < definedTableModel.getRowCount(); i++) {
				String domain = (String) definedTableModel.getValueAt(i, 1);
				Concept rel = (Concept) definedTableModel.getValueAt(i, 2);
				String range = (String) definedTableModel.getValueAt(i, 3);
				NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(domain, rel, range);
				if (deleteNonTaxRelSet.contains(nonTaxRel)) {
					definedTableModel.removeRow(i);
					--i;
				}
			}
		}
		// System.out.println(acceptedPairSet);
	}

	private void deleteWrongPair() {
		DefaultTableModel wrongPairTableModel = (DefaultTableModel) wrongPairTable.getModel();
		Set<WrongPair> deleteWrongPairSet = new HashSet<WrongPair>();
		int row[] = wrongPairTable.getSelectedRows();

		if (row != null) {
			for (int i = 0; i < row.length; i++) {
				String c1 = (String) wrongPairTableModel.getValueAt(row[i], 0);
				String c2 = (String) wrongPairTableModel.getValueAt(row[i], 1);
				WrongPair delWp = new WrongPair(c1, c2);
				deleteWrongPairSet.add(delWp);
				wrongPairSet.remove(delWp);
			}
			reCalcWSandARValue();

			for (int i = 0; i < wrongPairTableModel.getRowCount(); i++) {
				String c1 = (String) wrongPairTableModel.getValueAt(i, 0);
				String c2 = (String) wrongPairTableModel.getValueAt(i, 1);
				WrongPair wp = new WrongPair(c1, c2);
				if (deleteWrongPairSet.contains(wp)) {
					wrongPairTableModel.removeRow(i);
					--i;
				}
			}
		}
		// System.out.println(wrongPairSet);
	}

	public class ConceptDefinitionPanel extends JPanel implements ActionListener {

		private JLabel c1Label;
		private JLabel c2Label;
		private JLabel allowLabel;
		private JButton reverseButton;
		private JButton addAcceptedPairButton;
		private JButton addWrongPairButton;

		private ImageIcon rightIcon = Utils.getImageIcon("arrow_right.png");
		private ImageIcon leftIcon = Utils.getImageIcon("arrow_left.png");

		public ConceptDefinitionPanel() {
			c1Label = new JLabel();
			c2Label = new JLabel();
			reverseButton = new JButton(Translator.getTerm("ReverseButton"));
			reverseButton.addActionListener(this);
			addAcceptedPairButton = new JButton(Translator.getTerm("AddCorrectConceptPairButton"));
			addAcceptedPairButton.addActionListener(this);
			addWrongPairButton = new JButton(Translator.getTerm("AddWrongConceptPairButton"));
			addWrongPairButton.addActionListener(this);
			allowLabel = new JLabel(rightIcon, JLabel.CENTER);

			c1Label.setFont(new Font("Dialog", Font.PLAIN, 14));
			c2Label.setFont(new Font("Dialog", Font.PLAIN, 14));

			JPanel pairPanel = new JPanel();
			pairPanel.setLayout(new GridLayout(1, 3, 5, 5));
			pairPanel.add(c1Label);
			pairPanel.add(allowLabel);
			pairPanel.add(c2Label);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 3, 5, 5));
			buttonPanel.add(reverseButton);
			buttonPanel.add(addAcceptedPairButton);
			buttonPanel.add(addWrongPairButton);

			setBorder(BorderFactory.createEtchedBorder());
			setLayout(new BorderLayout());
			add(pairPanel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.EAST);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == reverseButton) {
				reverseAction();
			} else if (e.getSource() == addAcceptedPairButton) {
				addNonTaxonomicRelation(getNonTaxonomicRelation());
				doddleProject.addLog("AddCorrectConceptPairButton");
			} else if (e.getSource() == addWrongPairButton) {
				String c1 = c1Label.getText();
				String c2 = c2Label.getText();
				if (c1.equals("") || c2.equals("")) {
					return;
				}
				WrongPair wp = new WrongPair(c1, c2);
				addWrongPair(wp);
				doddleProject.addLog("AddWrongConceptPairButton");
			}
		}

		private void reverseAction() {
			if (allowLabel.getIcon().equals(rightIcon)) {
				allowLabel.setIcon(leftIcon);
			} else {
				allowLabel.setIcon(rightIcon);
			}
		}

		public NonTaxonomicRelation getNonTaxonomicRelation() {
			NonTaxonomicRelation nonTaxRel = null;
			Concept propRootConcept = getPropertyRootConcept();
			if (propRootConcept != null) {
				if (allowLabel.getIcon().equals(rightIcon)) {
					nonTaxRel = new NonTaxonomicRelation(c1Label.getText(), propRootConcept,
							c2Label.getText());
				} else {
					nonTaxRel = new NonTaxonomicRelation(c2Label.getText(), propRootConcept,
							c1Label.getText());
				}
				allowLabel.setIcon(rightIcon);
			}
			return nonTaxRel;
		}

		public void setCText(String c1, String c2) {
			c1Label.setText(c1);
			c2Label.setText(c2);
		}
	}

	/**
	 * メタプロパティチェックの処理
	 */
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		if (row < 0) {
			return;
		}
		if (conceptDefinitionTable.getRowCount() <= row) {
			return;
		}
		// 行の入れ替えが発生していると以下では例外が発生する
		Boolean isMetaProperty = (Boolean) conceptDefinitionTable.getModel().getValueAt(row, 0);
		String domain = (String) conceptDefinitionTable.getModel().getValueAt(row, 1);
		Concept rel = (Concept) conceptDefinitionTable.getModel().getValueAt(row, 2);
		String range = (String) conceptDefinitionTable.getModel().getValueAt(row, 3);

		for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
			if (nonTaxRel.getDomain().equals(domain) && nonTaxRel.getRange().equals(range)
					&& nonTaxRel.isSameRelation(rel)) {
				nonTaxRel.setMetaProperty(isMetaProperty);
				break;
			}
		}
	}
}