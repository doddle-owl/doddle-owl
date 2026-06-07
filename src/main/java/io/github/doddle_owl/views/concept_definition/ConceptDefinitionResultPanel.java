/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: https://doddle-owl.github.io/
 *
 * Copyright (C) 2004-2026 Takeshi Morita. All rights reserved.
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

package io.github.doddle_owl.views.concept_definition;

import io.github.doddle_owl.models.concept_definition.ConceptPair;
import io.github.doddle_owl.models.concept_definition.NonTaxonomicRelation;
import io.github.doddle_owl.models.concept_definition.NegativeConceptPair;
import io.github.doddle_owl.models.concept_selection.Concept;
import io.github.doddle_owl.models.concept_hierarchy.ConceptTreeCellRenderer;
import io.github.doddle_owl.models.concept_hierarchy.ConceptTreeNode;
import io.github.doddle_owl.models.document_selection.Document;
import io.github.doddle_owl.utils.Translator;
import io.github.doddle_owl.utils.Utils;
import io.github.doddle_owl.views.common.ConceptSelectionDialog;
import io.github.doddle_owl.views.concept_hierarchy.PropertyHierarchyConstructionPanel;
import io.github.doddle_owl.views.DODDLEProjectPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.*;

/**
 * @author shigeta
 * @author Takeshi Morita
 */
public class ConceptDefinitionResultPanel extends JPanel implements ActionListener,
        ListSelectionListener, TableModelListener {

    private final JList inputConceptJList;
    private final ConceptDefinitionAlgorithmPanel algorithmPanel;

    private final Set<NonTaxonomicRelation> nonTaxRelSet;
    private final Set<NegativeConceptPair> negativeConceptPairSet;

    private final DefaultListModel<Document> documentListModel;
    private final JList<Document> documentJList;

    private final JTable wsResultTable;
    private final JTable arResultTable;
    private final JTable waResultTable;

    private final JTable conceptDefinitionTable;
    private final JButton setRelationButton;
    private final JButton deletePositiveConceptPairButton;

    private final JTable negativeConceptPairTable;
    private final JButton deleteNegativeConceptPairButton;

    private final String[] definedColumnNames = {Translator.getTerm("MetaPropertyLabel"),
            Translator.getTerm("DomainLabel"), Translator.getTerm("RelationLabel"),
            Translator.getTerm("RangeLabel")};
    private final String[] negativeDefinedColumnNames = {Translator.getTerm("DomainLabel"), Translator.getTerm("RangeLabel")};

    private final DODDLEProjectPanel doddleProjectPanel;
    private final PropertyHierarchyConstructionPanel propertyHierarchyConstructionPanel;

    private static final String[] WS_COLUMN_NAMES = {Translator.getTerm("RelatedConceptList"),
            Translator.getTerm("WordSpaceValueLabel")};
    private static final String[] AR_COLUMN_NAMES = {Translator.getTerm("RelatedConceptList"),
            Translator.getTerm("AprioriValueLabel")};
    private static final String[] WA_COLUMN_NAMES = {Translator.getTerm("RelatedConceptList"),
            Translator.getTerm("WordSpaceValueLabel"), Translator.getTerm("AprioriValueLabel")};

    private final JPanel inputConceptPanel;
    private final JPanel inputDocPanel;
    private final JPanel positiveConceptPairPanel;
    private final JPanel negativeConceptPairPanel;

    public JPanel getInputConceptPanel() {
        return inputConceptPanel;
    }

    public JPanel getPositiveConceptPairPanel() {
        return positiveConceptPairPanel;
    }

    public JPanel getNegativeConceptPairPanel() {
        return negativeConceptPairPanel;
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

    public void initialize() {
        documentListModel.clear();
        DefaultTableModel resultModel = new DefaultTableModel(null, WS_COLUMN_NAMES);
        wsResultTable.setModel(resultModel);
        resultModel = new DefaultTableModel(null, AR_COLUMN_NAMES);
        arResultTable.setModel(resultModel);
        resultModel = new DefaultTableModel(null, WA_COLUMN_NAMES);
        waResultTable.setModel(resultModel);
        var resultTableModel = new ResultTableModel(null, definedColumnNames);
        resultTableModel.addTableModelListener(this);
        conceptDefinitionTable.setModel(resultTableModel);
        resultTableModel = new ResultTableModel(null, negativeDefinedColumnNames);
        resultTableModel.addTableModelListener(this);
        negativeConceptPairTable.setModel(resultTableModel);
    }

    public ConceptDefinitionResultPanel(JList icList, ConceptDefinitionAlgorithmPanel ap,
                                        DODDLEProjectPanel project) {
        algorithmPanel = ap;
        doddleProjectPanel = project;
        propertyHierarchyConstructionPanel = project.getConstructPropertyPanel();
        inputConceptJList = icList;

        nonTaxRelSet = new HashSet<>();
        negativeConceptPairSet = new HashSet<>();

        definePanel = new ConceptDefinitionPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        documentListModel = new DefaultListModel<>();
        documentJList = new JList(documentListModel);
        documentJList.addListSelectionListener(this);
        JScrollPane inputDocJListScroll = new JScrollPane(documentJList);
        inputDocJListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("InputDocumentList")));
        inputDocJListScroll.setPreferredSize(new Dimension(80, 80));
        inputDocJListScroll.setMinimumSize(new Dimension(80, 80));
        inputDocPanel = new JPanel();
        inputDocPanel.setLayout(new BorderLayout());
        inputDocPanel.add(inputDocJListScroll, BorderLayout.CENTER);

        wsResultTable = new JTable();
        arResultTable = new JTable();
        waResultTable = new JTable();

        conceptDefinitionTable = new JTable();
        JScrollPane conceptDefinitionTableScroll = new JScrollPane(conceptDefinitionTable);
        setRelationButton = new JButton(Translator.getTerm("SetPropertyButton"));
        setRelationButton.addActionListener(this);
        deletePositiveConceptPairButton = new JButton(Translator.getTerm("RemovePositiveConceptPairButton"));
        deletePositiveConceptPairButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(setRelationButton);
        buttonPanel.add(deletePositiveConceptPairButton);
        positiveConceptPairPanel = new JPanel();
        positiveConceptPairPanel.setLayout(new BorderLayout());
        positiveConceptPairPanel.add(conceptDefinitionTableScroll, BorderLayout.CENTER);
        positiveConceptPairPanel.add(getEastComponent(buttonPanel), BorderLayout.SOUTH);

        negativeConceptPairTable = new JTable();
        JScrollPane negativeConceptPairTableScroll = new JScrollPane(negativeConceptPairTable);
        deleteNegativeConceptPairButton = new JButton(Translator.getTerm("RemoveNegativeConceptPairButton"));
        deleteNegativeConceptPairButton.addActionListener(this);
        negativeConceptPairPanel = new JPanel();
        negativeConceptPairPanel.setLayout(new BorderLayout());
        negativeConceptPairPanel.add(negativeConceptPairTableScroll, BorderLayout.CENTER);
        negativeConceptPairPanel.add(getEastComponent(deleteNegativeConceptPairButton), BorderLayout.SOUTH);

        JScrollPane inputConceptJListScroll = new JScrollPane(inputConceptJList);
        inputConceptJListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("InputConceptList")));
        inputConceptJListScroll.setPreferredSize(new Dimension(200, 100));

        inputConceptPanel = new JPanel();
        inputConceptPanel.setLayout(new BorderLayout());
        inputConceptPanel.add(inputConceptJListScroll, BorderLayout.CENTER);
        initialize();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == documentJList) {
            reCalcWSandARValue();
        }
    }

    public void saveConceptDefinition(File file) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            try (writer) {
                StringBuilder builder = new StringBuilder();
                for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
                    builder.append(nonTaxRel);
                    builder.append(System.lineSeparator());
                }
                writer.write(builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConceptDefinition(File file) {
        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            try (reader) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] lines = line.split("\t");
                    boolean isMetaProperty = Boolean.valueOf(lines[0]);
                    String domain = lines[1];
                    Concept relation = doddleProjectPanel.getConstructPropertyPanel().getConcept(lines[2]);
                    String range = lines[3];
                    NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(domain, relation, range);
                    nonTaxRel.setMetaProperty(isMetaProperty);
                    addNonTaxonomicRelation(nonTaxRel);
                }
            }
        } catch (IOException uee) {
            uee.printStackTrace();
        }
    }

    public void saveNegativeConceptPairSet(File file) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            try (writer) {
                StringBuilder builder = new StringBuilder();
                for (NegativeConceptPair wp : negativeConceptPairSet) {
                    builder.append(wp.getDomain());
                    builder.append("\t");
                    builder.append(wp.getRange());
                    builder.append(System.lineSeparator());
                }
                writer.write(builder.toString());
            }
        } catch (IOException uee) {
            uee.printStackTrace();
        }
    }

    public void loadNegativeConceptPairSet(File file) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            try (reader) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] lines = line.split("\t");
                    NegativeConceptPair wp = new NegativeConceptPair(lines[0], lines[1]);
                    addNegativeConceptPair(wp);
                }
            }
        } catch (IOException uee) {
            uee.printStackTrace();
        }
    }

    public void loadNegativeConceptPairSet(int projectID, Statement stmt) {
        try {
            String sql = "SELECT * from wrong_pair where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String term1 = URLDecoder.decode(rs.getString("Term1"), StandardCharsets.UTF_8);
                String term2 = URLDecoder.decode(rs.getString("Term2"), StandardCharsets.UTF_8);
                NegativeConceptPair wp = new NegativeConceptPair(term1, term2);
                addNegativeConceptPair(wp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reCalcWSandARValue() {
        calcWSandARValue((String) inputConceptJList.getSelectedValue());
    }

    public void setInputDocList() {
        documentListModel.clear();
        documentListModel.addAll(doddleProjectPanel.getDocumentSelectionPanel().getDocSet());
    }

    public void calcWSandARValue(String selectedInputConcept) {
        Document currentDoc = documentJList.getSelectedValue();
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
        Set<String[]> validDataSet = new HashSet<>();
        for (ConceptPair wsPair : validWSPairSet) {
            for (ConceptPair arPair : validARPairSet) {
                if (wsPair.getToConceptLabel().equals(arPair.getToConceptLabel())) {
                    validDataSet.add(new String[]{wsPair.getTableData()[0],
                            wsPair.getTableData()[1], arPair.getTableData()[1]});
                }
            }
        }

        String[][] data = new String[validDataSet.size()][3];
        int cnt = 0;
        for (String[] strings : validDataSet) {
            data[cnt++] = strings;
        }
        setWAResultTable(new DefaultTableModel(data, ConceptDefinitionResultPanel.WA_COLUMN_NAMES));
    }

    private void setWAResultTable(DefaultTableModel dtm) {
        waResultTable.setModel(Objects.requireNonNullElseGet(dtm, () -> new DefaultTableModel(null, WA_COLUMN_NAMES)));
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

    private void addNegativeConceptPair(NegativeConceptPair wp) {
        DefaultTableModel model = (DefaultTableModel) negativeConceptPairTable.getModel();
        negativeConceptPairSet.add(wp);
        reCalcWSandARValue();
        // System.out.println(negativeConceptPairSet);
        model.addRow(new Object[]{wp.getDomain(), wp.getRange()});
    }

    private void addNonTaxonomicRelation(NonTaxonomicRelation nonTaxRel) {
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
    private Set<ConceptPair> setWSResultTable(String selectedInputConcept,
                                              List<ConceptPair> wsConceptPairList) {
        Set<ConceptPair> validPairSet = new TreeSet<>();
        for (ConceptPair pair : wsConceptPairList) {
            String[] data = pair.getTableData();
            NegativeConceptPair wp = new NegativeConceptPair(selectedInputConcept, data[0]);
            if (!negativeConceptPairSet.contains(wp)) {
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

    private void setWSResultTable(DefaultTableModel dtm) {
        wsResultTable.setModel(Objects.requireNonNullElseGet(dtm, () -> new DefaultTableModel(null, WS_COLUMN_NAMES)));
    }

    private Set<ConceptPair> setARResultTable(String inputConcept, List<ConceptPair> arConceptPairSet) {
        Set<ConceptPair> validPairSet = new TreeSet<>();
        for (ConceptPair pair : arConceptPairSet) {
            String[] data = pair.getTableData();
            NegativeConceptPair wp = new NegativeConceptPair(inputConcept, data[0]);
            if (!negativeConceptPairSet.contains(wp)) {
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

    private void setARResultTable(DefaultTableModel dtm) {
        arResultTable.setModel(Objects.requireNonNullElseGet(dtm, () -> new DefaultTableModel(null, AR_COLUMN_NAMES)));
    }

    public String getWSTableRowConceptName(int row) {
        return (String) wsResultTable.getModel().getValueAt(row, 0);
    }

    public String getARTableRowConceptName(int row) {
        return (String) arResultTable.getModel().getValueAt(row, 0);
    }

    public String getWATableRowConceptName(int row) {
        return (String) waResultTable.getModel().getValueAt(row, 0);
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

    private final ConceptDefinitionPanel definePanel;

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
        TreeModel treeModel = propertyHierarchyConstructionPanel.getIsaTree().getModel();
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
            TreeModel treeModel = propertyHierarchyConstructionPanel.getIsaTree().getModel();
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
            doddleProjectPanel.addLog("SetPropertyButton");
        } else if (e.getSource() == deletePositiveConceptPairButton) {
            deleteConceptDefinition();
            doddleProjectPanel.addLog("RemovePositiveConceptPairButton");
        } else if (e.getSource() == deleteNegativeConceptPairButton) {
            deleteNegativeConceptPair();
            doddleProjectPanel.addLog("RemoveNegativeConceptPairButton");
        }
    }

    class ResultTableModel extends DefaultTableModel {

        ResultTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        public Class getColumnClass(int column) {
            Vector v = dataVector.elementAt(0);
            return v.elementAt(column).getClass();
        }
    }

    private void deleteConceptDefinition() {
        DefaultTableModel definedTableModel = (DefaultTableModel) conceptDefinitionTable.getModel();
        Set<NonTaxonomicRelation> deleteNonTaxRelSet = new HashSet<>();
        int[] row = conceptDefinitionTable.getSelectedRows();

        if (row != null) {
            for (int i1 : row) {
                String domain = (String) definedTableModel.getValueAt(i1, 1);
                Concept rel = (Concept) definedTableModel.getValueAt(i1, 2);
                String range = (String) definedTableModel.getValueAt(i1, 3);
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

    private void deleteNegativeConceptPair() {
        DefaultTableModel negativeConceptPairTableModel = (DefaultTableModel) negativeConceptPairTable.getModel();
        Set<NegativeConceptPair> deleteNegativeConceptPairSet = new HashSet<>();
        int[] row = negativeConceptPairTable.getSelectedRows();

        if (row != null) {
            for (int i1 : row) {
                String c1 = (String) negativeConceptPairTableModel.getValueAt(i1, 0);
                String c2 = (String) negativeConceptPairTableModel.getValueAt(i1, 1);
                NegativeConceptPair delWp = new NegativeConceptPair(c1, c2);
                deleteNegativeConceptPairSet.add(delWp);
                negativeConceptPairSet.remove(delWp);
            }
            reCalcWSandARValue();

            for (int i = 0; i < negativeConceptPairTableModel.getRowCount(); i++) {
                String c1 = (String) negativeConceptPairTableModel.getValueAt(i, 0);
                String c2 = (String) negativeConceptPairTableModel.getValueAt(i, 1);
                NegativeConceptPair wp = new NegativeConceptPair(c1, c2);
                if (deleteNegativeConceptPairSet.contains(wp)) {
                    negativeConceptPairTableModel.removeRow(i);
                    --i;
                }
            }
        }
        // System.out.println(negativeConceptPairSet);
    }

    public class ConceptDefinitionPanel extends JPanel implements ActionListener {

        private final JLabel c1Label;
        private final JLabel c2Label;
        private final JLabel allowLabel;
        private final JButton reverseButton;
        private final JButton addPositiveConceptPairButton;
        private final JButton addNegativeConceptPairButton;

        private final ImageIcon rightIcon = Utils.getImageIcon("baseline_arrow_forward_black_18dp.png");
        private final ImageIcon leftIcon = Utils.getImageIcon("baseline_arrow_back_black_18dp.png");

        ConceptDefinitionPanel() {
            c1Label = new JLabel();
            c2Label = new JLabel();
            reverseButton = new JButton(Translator.getTerm("ReverseButton"));
            reverseButton.addActionListener(this);
            addPositiveConceptPairButton = new JButton(Translator.getTerm("AddPositiveConceptPairButton"));
            addPositiveConceptPairButton.addActionListener(this);
            addNegativeConceptPairButton = new JButton(Translator.getTerm("AddNegativeConceptPairButton"));
            addNegativeConceptPairButton.addActionListener(this);
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
            buttonPanel.add(addPositiveConceptPairButton);
            buttonPanel.add(addNegativeConceptPairButton);

            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());
            add(pairPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == reverseButton) {
                reverseAction();
            } else if (e.getSource() == addPositiveConceptPairButton) {
                addNonTaxonomicRelation(getNonTaxonomicRelation());
                doddleProjectPanel.addLog("AddPositiveConceptPairButton");
            } else if (e.getSource() == addNegativeConceptPairButton) {
                String c1 = c1Label.getText();
                String c2 = c2Label.getText();
                if (c1.equals("") || c2.equals("")) {
                    return;
                }
                NegativeConceptPair wp = new NegativeConceptPair(c1, c2);
                addNegativeConceptPair(wp);
                doddleProjectPanel.addLog("AddNegativeConceptPairButton");
            }
        }

        private void reverseAction() {
            if (allowLabel.getIcon().equals(rightIcon)) {
                allowLabel.setIcon(leftIcon);
            } else {
                allowLabel.setIcon(rightIcon);
            }
        }

        NonTaxonomicRelation getNonTaxonomicRelation() {
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