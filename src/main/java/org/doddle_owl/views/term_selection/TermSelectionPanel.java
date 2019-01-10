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

package org.doddle_owl.views.term_selection;

import net.infonode.docking.*;
import net.infonode.docking.util.ViewMap;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.term_selection.TermInfo;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;
import org.doddle_owl.views.DODDLEProjectPanel;
import org.doddle_owl.views.concept_selection.ConceptSelectionPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Statement;
import java.util.*;

/**
 * @author Takeshi Morita
 */
public class TermSelectionPanel extends JPanel implements ActionListener, KeyListener {

    private JTextArea inputTermArea;
    private TermInfoTablePanel inputTermInfoTablePanel;
    private TermInfoTablePanel removedTermInfoTablePanel;
    private TermsInDocumentViewer documentViewer;

    private JButton addInputTermListButton;
    private JButton deleteTableItemButton;
    private JButton returnTableItemButton;
    private JButton reloadDocumentAreaButton;
    private JButton completelyDeleteTableItemButton;
    private JButton setInputTermSetButton;
    private JButton addInputTermSetButton;

    private View[] mainViews;
    private RootWindow rootWindow;
    private ConceptSelectionPanel conceptSelectionPanel;

    public void initialize() {
        inputTermArea.setText("");
        inputTermInfoTablePanel.initialize();
        removedTermInfoTablePanel.initialize();
        documentViewer.initialize();
    }

    public TermSelectionPanel(ConceptSelectionPanel ui) {
        inputTermInfoTablePanel = new TermInfoTablePanel();
        inputTermInfoTablePanel.getTable().addKeyListener(this);
        removedTermInfoTablePanel = new TermInfoTablePanel();
        removedTermInfoTablePanel.getTable().addKeyListener(this);
        documentViewer = new TermsInDocumentViewer();
        conceptSelectionPanel = ui;

        inputTermArea = new JTextArea(10, 15);
        JScrollPane inputTermsAreaScroll = new JScrollPane(inputTermArea);

        addInputTermListButton = new JButton(Translator.getTerm("AddInputTermListButton"));
        addInputTermListButton.addActionListener(this);
        deleteTableItemButton = new JButton(Translator.getTerm("RemoveButton"));
        deleteTableItemButton.addActionListener(this);
        returnTableItemButton = new JButton(Translator.getTerm("ReturnButton"));
        returnTableItemButton.addActionListener(this);
        reloadDocumentAreaButton = new JButton(Translator.getTerm("ReloadButton"));
        reloadDocumentAreaButton.addActionListener(this);
        completelyDeleteTableItemButton = new JButton(Translator.getTerm("CompletelyDeleteTermButton"));
        completelyDeleteTableItemButton.addActionListener(this);

        JPanel tableButtonPanel = new JPanel();
        tableButtonPanel.add(addInputTermListButton);
        tableButtonPanel.add(deleteTableItemButton);
        tableButtonPanel.add(returnTableItemButton);
        tableButtonPanel.add(reloadDocumentAreaButton);
        tableButtonPanel.add(completelyDeleteTableItemButton);

        setInputTermSetButton = new JButton(Translator.getTerm("SetInputTermSetButton"));
        setInputTermSetButton.addActionListener(this);
        addInputTermSetButton = new JButton(Translator.getTerm("AddInputTermSetButton"));
        addInputTermSetButton.addActionListener(this);
        JPanel inputTermsButtonPanel = new JPanel();
        inputTermsButtonPanel.add(setInputTermSetButton);
        inputTermsButtonPanel.add(addInputTermSetButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(tableButtonPanel, BorderLayout.WEST);
        buttonPanel.add(inputTermsButtonPanel, BorderLayout.EAST);

        DockingWindowAction action = new DockingWindowAction();
        mainViews = new View[4];
        ViewMap viewMap = new ViewMap();
        mainViews[0] = new View(Translator.getTerm("InputDocumentViewerPanel"), null, documentViewer);
        mainViews[1] = new View(Translator.getTerm("InputTermInfoTablePanel"), null, inputTermInfoTablePanel);
        mainViews[2] = new View(Translator.getTerm("RemovedTermInfoTablePanel"), null, removedTermInfoTablePanel);
        mainViews[3] = new View(Translator.getTerm("InputTermListArea"), null, inputTermsAreaScroll);
        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        rootWindow = Utils.createDODDLERootWindow(viewMap);
        rootWindow.addListener(action);
        action.viewFocusChanged(mainViews[2], mainViews[0]);

        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setXGALayout() {
        TabWindow tabWindow = new TabWindow(new DockingWindow[]{mainViews[0], mainViews[1], mainViews[2]});
        SplitWindow sw1 = new SplitWindow(true, 0.8f, tabWindow, mainViews[3]);
        rootWindow.setWindow(sw1);
        mainViews[0].restoreFocus();
    }

    public void setUXGALayout() {
        SplitWindow sw1 = new SplitWindow(true, mainViews[1], mainViews[2]);
        SplitWindow sw2 = new SplitWindow(false, mainViews[0], sw1);
        SplitWindow sw3 = new SplitWindow(true, 0.8f, sw2, mainViews[3]);
        rootWindow.setWindow(sw3);
        mainViews[0].restoreFocus();
    }

    public void setWindowTitle() {
        mainViews[1].getViewProperties().setTitle(
                Translator.getTerm("InputTermInfoTablePanel") + "（" + inputTermInfoTablePanel.getTableSize() + "）");
        mainViews[2].getViewProperties().setTitle(
                Translator.getTerm("RemovedTermInfoTablePanel") + "（" + removedTermInfoTablePanel.getTableSize() + "）");
        rootWindow.repaint();
    }

    public void setInputTermInfoTableModel(Map<String, TermInfo> termInfoMap, int docNum) {
        inputTermInfoTablePanel.setTermInfoTableModel(termInfoMap, docNum);
        removedTermInfoTablePanel.setTermInfoTableModel(new HashMap<>(), docNum);
        setWindowTitle();
    }

    public void setInputDocumentListModel(ListModel listModel) {
        documentViewer.setDocumentList(listModel);
    }

    public void loadInputTermInfoTable() {
        inputTermInfoTablePanel.loadTermInfoTable();
        removedTermInfoTablePanel.loadTermInfoTable();
        setWindowTitle();
    }

    public void loadInputTermInfoTable(File file, File removedFile) {
        inputTermInfoTablePanel.loadTermInfoTable(file);
        removedTermInfoTablePanel.loadTermInfoTable(removedFile);
        setWindowTitle();
    }

    public void loadInputTermInfoTable(int projectID, Statement stmt, int docNum) {
        inputTermInfoTablePanel.loadTermInfoTable(projectID, stmt, "term_info", docNum);
        removedTermInfoTablePanel.loadTermInfoTable(projectID, stmt, "removed_term_info", docNum);
        setWindowTitle();
    }

    public void saveInputTermInfoTable() {
        inputTermInfoTablePanel.saveTermInfoTable();
        removedTermInfoTablePanel.saveTermInfoTable();
    }

    public void saveInputTermInfoTable(File file, File removedFile) {
        inputTermInfoTablePanel.saveTermInfoTable(file);
        removedTermInfoTablePanel.saveTermInfoTable(removedFile);
    }

    public void saveTermInfoTable(int projectID, Statement stmt) {
        inputTermInfoTablePanel.saveTermInfoTable(projectID, stmt, "term_info");
        removedTermInfoTablePanel.saveTermInfoTable(projectID, stmt, "removed_term_info");
    }

    private void setInputTermSet(int taskCnt) {
        DODDLE_OWL.STATUS_BAR.setLastMessage(Translator.getTerm("SetInputTermListButton"));
        String[] inputTerms = inputTermArea.getText().split("\n");
        Set<String> inputTermSet = new HashSet<>(Arrays.asList(inputTerms));
        conceptSelectionPanel.loadInputTermSet(inputTermSet, taskCnt);
    }

    private void addInputTermSet(int taskCnt) {
        DODDLE_OWL.STATUS_BAR.setLastMessage(Translator.getTerm("AddInputTermListButton"));
        String[] inputTerms = inputTermArea.getText().split("\n");
        Set<String> inputTermSet = new HashSet<>(Arrays.asList(inputTerms));
        conceptSelectionPanel.addInputTermSet(inputTermSet, taskCnt);
    }

    private void addInputTerms() {
        JTable termInfoTable = inputTermInfoTablePanel.getTermInfoTable();
        int[] rows = termInfoTable.getSelectedRows();
        StringBuilder inputTerms = new StringBuilder();
        for (int row : rows) {
            String term = (String) termInfoTable.getValueAt(row, getColumnNamePosition(termInfoTable, Translator
                    .getTerm("TermLabel"))); // 現在選択されている行のデータがほしいのでTableを使う
            inputTerms.append(term).append(System.lineSeparator());
        }
        inputTermArea.setText(inputTermArea.getText() + inputTerms.toString());
    }

    private void deleteTableItems() {
        inputTermInfoTablePanel.setIsDeletingTableItems(true);
        removedTermInfoTablePanel.setIsDeletingTableItems(true);
        JTable inputTermInfoTable = inputTermInfoTablePanel.getTermInfoTable();
        JTable removedTermInfoTable = removedTermInfoTablePanel.getTermInfoTable();
        DefaultTableModel inputTermInfoTableModel = (DefaultTableModel) inputTermInfoTable.getModel();
        DefaultTableModel removedTermInfoTableModel = (DefaultTableModel) removedTermInfoTable.getModel();

        int[] selectedRows = inputTermInfoTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length <= 0) return;

        Map<String, Integer> columnNamePositionMap = getColumnNamePositionMap(inputTermInfoTable);
        for (int i = selectedRows.length - 1; 0 <= i; i--) {
            String deleteTerm = (String) inputTermInfoTable.getValueAt(selectedRows[i], getColumnNamePosition(
                    inputTermInfoTable, Translator.getTerm("TermLabel"))); // 現在選択されている行のデータがほしいのでTableを使う

            TermInfo info = inputTermInfoTablePanel.getTermInfo(deleteTerm);
            removedTermInfoTablePanel.addTermInfoMapKey(deleteTerm, info);
            inputTermInfoTablePanel.removeTermInfoMapKey(deleteTerm);

            // Vector rowData = (Vector)
            // inputTermInfoTableModel.getDataVector().get(selectedRows[i]);
            // // ソートしたり列を入れ替えたりする場合は，このメソッドは利用できない
            // Vector rowData = getRowData(selectedRows[i], inputTermInfoTable);
            Vector rowData = getRowData(selectedRows[i], inputTermInfoTable, columnNamePositionMap);
            removedTermInfoTableModel.insertRow(0, rowData);
            inputTermInfoTableModel.removeRow(inputTermInfoTable.convertRowIndexToModel(selectedRows[i]));
        }
        setWindowTitle();
        documentViewer.setDocumentAndLinkArea();
        inputTermInfoTablePanel.setIsDeletingTableItems(false);
        removedTermInfoTablePanel.setIsDeletingTableItems(false);
    }

    private int getColumnNamePosition(JTable table, String columnName) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return 0;
    }

    private Map<String, Integer> getColumnNamePositionMap(JTable table) {
        Map<String, Integer> columnNamePositionMap = new HashMap<>();
        String columnName = Translator.getTerm("TermLabel");
        columnNamePositionMap.put(columnName, getColumnNamePosition(table, columnName));
        columnName = Translator.getTerm("POSLabel");
        columnNamePositionMap.put(columnName, getColumnNamePosition(table, columnName));
        columnName = Translator.getTerm("TFLabel");
        columnNamePositionMap.put(columnName, getColumnNamePosition(table, columnName));
        columnName = Translator.getTerm("IDFLabel");
        columnNamePositionMap.put(columnName, getColumnNamePosition(table, columnName));
        columnName = Translator.getTerm("TFIDFLabel");
        columnNamePositionMap.put(columnName, getColumnNamePosition(table, columnName));
        columnName = Translator.getTerm("UpperConceptLabel");
        columnNamePositionMap.put(columnName, getColumnNamePosition(table, columnName));
        return columnNamePositionMap;
    }

    private Vector getRowData(int row, JTable table, Map<String, Integer> columnNamePositionMap) {
        Vector rowData = new Vector();
        rowData.add(table.getValueAt(row, columnNamePositionMap.get(Translator.getTerm("TermLabel"))));
        rowData.add(table.getValueAt(row, columnNamePositionMap.get(Translator.getTerm("POSLabel"))));
        rowData.add(table.getValueAt(row, columnNamePositionMap.get(Translator.getTerm("TFLabel"))));
        rowData.add(table.getValueAt(row, columnNamePositionMap.get(Translator.getTerm("IDFLabel"))));
        rowData.add(table.getValueAt(row, columnNamePositionMap.get(Translator.getTerm("TFIDFLabel"))));
        rowData.add(table.getValueAt(row, columnNamePositionMap.get(Translator.getTerm("UpperConceptLabel"))));
        return rowData;
    }

    // private Vector getRowData(int row, JTable table) {
    // Vector rowData = new Vector();
    // rowData.add(table.getValueAt(row, getColumnNamePosition(table,
    // Translator.getTerm("TermLabel"))));
    // rowData.add(table.getValueAt(row, getColumnNamePosition(table,
    // Translator.getTerm("POSLabel"))));
    // rowData.add(table.getValueAt(row, getColumnNamePosition(table,
    // Translator.getTerm("TFLabel"))));
    // rowData.add(table.getValueAt(row, getColumnNamePosition(table,
    // Translator.getTerm("IDFLabel"))));
    // rowData.add(table.getValueAt(row, getColumnNamePosition(table,
    // Translator.getTerm("TFIDFLabel"))));
    // rowData.add(table.getValueAt(row, getColumnNamePosition(table,
    // Translator.getTerm("UpperConceptLabel"))));
    // return rowData;
    // }

    public void removeTerm(String rmTerm) {
        JTable inputTermInfoTable = inputTermInfoTablePanel.getTermInfoTable();
        JTable removedTermInfoTable = removedTermInfoTablePanel.getTermInfoTable();
        DefaultTableModel inputTermInfoTableModel = (DefaultTableModel) inputTermInfoTable.getModel();
        DefaultTableModel removedTermInfoTableModel = (DefaultTableModel) removedTermInfoTable.getModel();

        for (int i = 0; i < inputTermInfoTableModel.getRowCount(); i++) {
            // 全体をパースするだけなのでTableModelでよい．また，Modelの場合は絶対位置となるので0でよい．
            String term = (String) inputTermInfoTableModel.getValueAt(i, 0);
            if (term.equals(rmTerm)) {
                TermInfo info = inputTermInfoTablePanel.getTermInfo(rmTerm);
                removedTermInfoTableModel.insertRow(0, info.getRowData());
                removedTermInfoTablePanel.addTermInfoMapKey(rmTerm, info);
                inputTermInfoTablePanel.removeTermInfoMapKey(rmTerm);
                inputTermInfoTableModel.removeRow(i);
                break;
            }
        }
        setWindowTitle();
    }

    public void addInputTermInfo(TermInfo info) {
        JTable inputTermInfoTable = inputTermInfoTablePanel.getTermInfoTable();
        if (inputTermInfoTablePanel.getTermInfo(info.getTerm()) == null) {
            DefaultTableModel inputTermInfoTableModel = (DefaultTableModel) inputTermInfoTable.getModel();
            inputTermInfoTablePanel.addTermInfoMapKey(info.getTerm(), info);
            inputTermInfoTableModel.insertRow(0, info.getRowData());
            setWindowTitle();
        }
    }

    public void addTerm(String addTerm) {
        JTable inputTermInfoTable = inputTermInfoTablePanel.getTermInfoTable();
        JTable removedTermInfoTable = removedTermInfoTablePanel.getTermInfoTable();
        DefaultTableModel inputTermInfoTableModel = (DefaultTableModel) inputTermInfoTable.getModel();
        DefaultTableModel removedTermInfoTableModel = (DefaultTableModel) removedTermInfoTable.getModel();

        for (int i = 0; i < removedTermInfoTableModel.getRowCount(); i++) {
            // 全体をパースするだけなのでTableModelでよい．また，Modelの場合は絶対位置となるので0でよい．
            String term = (String) removedTermInfoTableModel.getValueAt(i, 0);
            if (term.equals(addTerm)) {
                TermInfo info = removedTermInfoTablePanel.getTermInfo(addTerm);
                inputTermInfoTableModel.insertRow(0, info.getRowData());
                inputTermInfoTablePanel.addTermInfoMapKey(addTerm, info);
                removedTermInfoTablePanel.removeTermInfoMapKey(addTerm);
                removedTermInfoTableModel.removeRow(i);
                break;
            }
        }
        setWindowTitle();
    }

    private void returnTableItems(boolean isCompleteDelete) {
        inputTermInfoTablePanel.setIsDeletingTableItems(true);
        removedTermInfoTablePanel.setIsDeletingTableItems(true);
        JTable inputTermInfoTable = inputTermInfoTablePanel.getTermInfoTable();
        JTable removedTermInfoTable = removedTermInfoTablePanel.getTermInfoTable();
        DefaultTableModel inputTermInfoTableModel = (DefaultTableModel) inputTermInfoTable.getModel();
        DefaultTableModel removedTermInfoTableModel = (DefaultTableModel) removedTermInfoTable.getModel();

        int[] selectedRows = removedTermInfoTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length <= 0) return;
        Map<String, Integer> columnNamePositionMap = getColumnNamePositionMap(removedTermInfoTable);
        for (int i = selectedRows.length - 1; 0 <= i; i--) {
            String returnTerm = (String) removedTermInfoTable.getValueAt(selectedRows[i], getColumnNamePosition(
                    removedTermInfoTable, Translator.getTerm("TermLabel"))); // 現在選択されている行のデータがほしいのでTableを使う
            TermInfo info = removedTermInfoTablePanel.getTermInfo(returnTerm);
            if (!isCompleteDelete) {
                inputTermInfoTablePanel.addTermInfoMapKey(returnTerm, info);
            }
            removedTermInfoTablePanel.removeTermInfoMapKey(returnTerm);

            // Vector rowData = (Vector)
            // removedTermInfoTableModel.getDataVector().get(selectedRows[i]);
            // // ソートしたり列を入れ替えたりする場合は，このメソッドは利用できない
            Vector rowData = getRowData(selectedRows[i], removedTermInfoTable, columnNamePositionMap); // ソートしたり，列をいれかえたりしても対応できるようにするためJTableを用いる
            inputTermInfoTableModel.insertRow(0, rowData);
            removedTermInfoTableModel.removeRow(removedTermInfoTable.convertRowIndexToModel(selectedRows[i])); // viewのindexをmodelのindexに変換．insert後に削除する
            // System.out.println(Translator.getTerm("ReturnButton") + "(" +
            // (selectedRows.length - i) + "/"
            // + selectedRows.length + "): " + returnTerm);
        }
        setWindowTitle();
        documentViewer.setDocumentAndLinkArea();
        inputTermInfoTablePanel.setIsDeletingTableItems(false);
        removedTermInfoTablePanel.setIsDeletingTableItems(false);
    }

    public Collection<TermInfo> getTermInfoSet() {
        return inputTermInfoTablePanel.getTermInfoSet();
    }

    public Collection<TermInfo> getRemovedTermInfoSet() {
        return removedTermInfoTablePanel.getTermInfoSet();
    }

    public TermInfo getInputTermInfo(String term) {
        return inputTermInfoTablePanel.getTermInfo(term);
    }

    public TermInfo getRemovedTermInfo(String term) {
        return removedTermInfoTablePanel.getTermInfo(term);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProjectPanel project = DODDLE_OWL.getCurrentProject();

        if (e.getSource() == setInputTermSetButton) {
            setInputTermSet(0);
            project.addLog("SetInputTermSetButton");
        } else if (e.getSource() == addInputTermSetButton) {
            addInputTermSet(0);
            project.addLog("AddInputTermSetButton");
        } else if (e.getSource() == addInputTermListButton) {
            addInputTerms();
            project.addLog("AddInputTermListButton");
        } else if (e.getSource() == deleteTableItemButton) {
            deleteTableItems();
            project.addLog("RemoveButton", "TermSelectionPanel");
        } else if (e.getSource() == returnTableItemButton) {
            returnTableItems(false);
            project.addLog("ReturnButton", "TermSelectionPanel");
        } else if (e.getSource() == completelyDeleteTableItemButton) {
            returnTableItems(true);
            project.addLog("CompletelyDeleteTermButtonButton", "TermSelectionPanel");
        } else if (e.getSource() == reloadDocumentAreaButton) {
            documentViewer.setDocumentAndLinkArea();
        }
    }

    class DockingWindowAction extends DockingWindowAdapter {
        public void viewFocusChanged(View previouslyFocusedView, View focusedView) {
            if (focusedView == mainViews[0]) {
                addInputTermListButton.setVisible(false);
                deleteTableItemButton.setVisible(false);
                returnTableItemButton.setVisible(false);
                completelyDeleteTableItemButton.setVisible(false);
                reloadDocumentAreaButton.setVisible(true);
            } else if (focusedView == mainViews[1]) {
                addInputTermListButton.setVisible(true);
                deleteTableItemButton.setVisible(true);
                returnTableItemButton.setVisible(false);
                completelyDeleteTableItemButton.setVisible(false);
                reloadDocumentAreaButton.setVisible(false);
            } else if (focusedView == mainViews[2]) {
                addInputTermListButton.setVisible(false);
                deleteTableItemButton.setVisible(false);
                returnTableItemButton.setVisible(true);
                completelyDeleteTableItemButton.setVisible(true);
                reloadDocumentAreaButton.setVisible(false);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            if (e.getSource() == inputTermInfoTablePanel.getTable()) {
                deleteTableItems();
                DODDLE_OWL.getCurrentProject().addLog("RemoveButton", "TermSelectionPanel");
            } else if (e.getSource() == removedTermInfoTablePanel.getTable()) {
                returnTableItems(false);
                DODDLE_OWL.getCurrentProject().addLog("ReturnButton", "TermSelectionPanel");
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
