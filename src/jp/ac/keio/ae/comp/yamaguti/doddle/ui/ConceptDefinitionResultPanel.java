package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author shigeta
 * 
 * modified by takeshi morita
 */
public class ConceptDefinitionResultPanel extends JPanel implements ActionListener, ListSelectionListener,
        TableModelListener {

    private JList inputConceptJList;
    private ConceptDefinitionAlgorithmPanel algorithmPanel;

    private Set<NonTaxonomicRelation> nonTaxRelSet;
    private Set<NonTaxonomicRelation> wrongPairSet;

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

    private static final String[] WS_COLUMN_NAMES = {
            Translator.getString("ConceptDefinitionPanel.RelevantConceptList"),
            Translator.getString("ConceptDefinitionPanel.WordSpaceValue")};
    private static final String[] AR_COLUMN_NAMES = {
            Translator.getString("ConceptDefinitionPanel.RelevantConceptList"),
            Translator.getString("ConceptDefinitionPanel.AprioriValue")};
    private static final String[] WA_COLUMN_NAMES = {
            Translator.getString("ConceptDefinitionPanel.RelevantConceptList"),
            Translator.getString("ConceptDefinitionPanel.WordSpaceValue"),
            Translator.getString("ConceptDefinitionPanel.AprioriValue")};

    public ConceptDefinitionResultPanel(JList icList, ConceptDefinitionAlgorithmPanel ap, DODDLEProject project) {
        algorithmPanel = ap;
        doddleProject = project;
        constructPropertyTreePanel = project.getConstructPropertyPanel();
        inputConceptJList = icList;

        nonTaxRelSet = new HashSet<NonTaxonomicRelation>();
        wrongPairSet = new HashSet<NonTaxonomicRelation>();

        definePanel = new ConceptDefinitionPanel();
        JTabbedPane resultTabpane = new JTabbedPane();
        JScrollPane scroll;
        DefaultTableModel resultModel = new DefaultTableModel(null, WS_COLUMN_NAMES);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        inputDocJList = new JList();        
        inputDocJList.addListSelectionListener(this);
        JScrollPane inputDocJListScroll = new JScrollPane(inputDocJList);
        inputDocJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("ConceptDefinitionPanel.InputDocumentList")));
        inputDocJListScroll.setPreferredSize(new Dimension(80, 80));
        inputDocJListScroll.setMinimumSize(new Dimension(80, 80));
        JPanel inputDocPanel = new JPanel();
        inputDocPanel.setLayout(new BorderLayout());
        inputDocPanel.add(inputDocJListScroll, BorderLayout.CENTER);

        wsResultTable = new JTable(resultModel);
        scroll = new JScrollPane(wsResultTable);
        scroll.getViewport().setView(wsResultTable);
        // WresultTable.setBackground(Color.BLUE);
        resultTabpane.addTab("WordSpace", scroll);

        resultModel = new DefaultTableModel(null, AR_COLUMN_NAMES);
        arResultTable = new JTable(resultModel);
        scroll = new JScrollPane(arResultTable);
        resultTabpane.addTab("Apriori", scroll);

        resultModel = new DefaultTableModel(null, WA_COLUMN_NAMES);
        waResultTable = new JTable(resultModel);
        scroll = new JScrollPane(waResultTable);
        resultTabpane.addTab("WordSpace & Apriori", scroll);

        resultTabpane.setFont(new Font("Dialog", Font.PLAIN, 14));
        resultTabpane.setTabPlacement(JTabbedPane.TOP);

        JSplitPane resultTabpanePanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputDocPanel, resultTabpane);
        resultTabpanePanel.setDividerSize(DODDLE.DIVIDER_SIZE);
        resultTabpanePanel.setOneTouchExpandable(true);

        String[] definedColumnNames = { Translator.getString("ConceptDefinitionPanel.MetaProperty"),
                Translator.getString("ConceptDefinitionPanel.Domain"),
                Translator.getString("ConceptDefinitionPanel.Relation"),
                Translator.getString("ConceptDefinitionPanel.Range")};
        ResultTableModel resultTableModel = new ResultTableModel(null, definedColumnNames);
        resultTableModel.addTableModelListener(this);
        conceptDefinitionTable = new JTable(resultTableModel);
        JScrollPane conceptDefinitionTableScroll = new JScrollPane(conceptDefinitionTable);
        setRelationButton = new JButton(Translator.getString("ConceptDefinitionPanel.SetRelation"));
        setRelationButton.addActionListener(this);
        deleteAcceptedPairButton = new JButton(Translator.getString("ConceptDefinitionPanel.RemoveCorrectConceptPair"));
        deleteAcceptedPairButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(setRelationButton);
        buttonPanel.add(deleteAcceptedPairButton);
        JPanel acceptedPairPanel = new JPanel();
        acceptedPairPanel.setLayout(new BorderLayout());
        acceptedPairPanel.add(conceptDefinitionTableScroll, BorderLayout.CENTER);
        acceptedPairPanel.add(getEastComponent(buttonPanel), BorderLayout.SOUTH);

        String[] wrongDefinedColumnNames = { Translator.getString("ConceptDefinitionPanel.Concept1"),
                Translator.getString("ConceptDefinitionPanel.Concept2")};
        wrongPairTable = new JTable(new ResultTableModel(null, wrongDefinedColumnNames));
        JScrollPane wrongConceptPairTableScroll = new JScrollPane(wrongPairTable);
        deleteWrongPairButton = new JButton(Translator.getString("ConceptDefinitionPanel.RemoveWrongConceptPair"));
        deleteWrongPairButton.addActionListener(this);
        JPanel wrongPairPanel = new JPanel();
        wrongPairPanel.setLayout(new BorderLayout());
        wrongPairPanel.add(wrongConceptPairTableScroll, BorderLayout.CENTER);
        wrongPairPanel.add(getEastComponent(deleteWrongPairButton), BorderLayout.SOUTH);

        JPanel resultCandidatePanel = new JPanel();
        resultCandidatePanel.setLayout(new BorderLayout());
        resultCandidatePanel.add(resultTabpanePanel, BorderLayout.CENTER);
        JScrollPane inputConceptJListScroll = new JScrollPane(inputConceptJList);
        inputConceptJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("ConceptDefinitionPanel.InputConceptList")));
        inputConceptJListScroll.setPreferredSize(new Dimension(200, 100));

        JPanel inputConceptPanel = new JPanel();
        inputConceptPanel.setLayout(new BorderLayout());
        inputConceptPanel.add(inputConceptJListScroll, BorderLayout.CENTER);

        resultCandidatePanel.add(inputConceptPanel, BorderLayout.WEST);
        resultCandidatePanel.add(definePanel, BorderLayout.SOUTH);

        JTabbedPane resultTableTab = new JTabbedPane();
        resultTableTab.add(Translator.getString("ConceptDefinitionPanel.CorrectConceptPairs"), acceptedPairPanel);
        resultTableTab.add(Translator.getString("ConceptDefinitionPanel.WrongConceptPairs"), wrongPairPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultCandidatePanel, resultTableTab);
        splitPane.setDividerSize(DODDLE.DIVIDER_SIZE);
        splitPane.setOneTouchExpandable(true);
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        // this.setLayout(new GridLayout(2, 1));
        // this.add(resultCandidatePanel);
        // this.add(resultTableTab);
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
                builder.append(nonTaxRel + "\n");
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

    public void loadConceptDefinition(File file) {
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
        }finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void saveWrongPairSet(File file) {
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (NonTaxonomicRelation nonTaxRel : wrongPairSet) {
                builder.append(nonTaxRel.getDomain());
                builder.append("\t");
                builder.append(nonTaxRel.getRange());
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

    public void loadWrongPairSet(File file) {
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (reader.ready()) {
                String line = reader.readLine();
                String[] lines = line.split("\t");
                NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(lines[0], lines[1]);
                addWrongPair(nonTaxRel);
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

    private void reCalcWSandARValue() {
        calcWSandARValue((String) inputConceptJList.getSelectedValue());
    }

    public void setInputDocList() {
        inputDocJList.setListData(doddleProject.getDocumentSelectionPanel().getDocSet().toArray());
    }

    public void calcWSandARValue(String selectedInputConcept) {
        Document currentDoc = (Document) inputDocJList.getSelectedValue();
        if (currentDoc == null) { return; }
        Set<ConceptPair> wsValidSet = null;
        Set<ConceptPair> arValidSet = null;
        Map<String, List<ConceptPair>> wsResult = algorithmPanel.getDocWordSpaceResult().get(currentDoc);
        Map<String, List<ConceptPair>> arResult = algorithmPanel.getDocAprioriResult().get(currentDoc);
        if (wsResult != null || arResult != null) {
            if (wsResult != null && wsResult.containsKey(selectedInputConcept)) {
                List<ConceptPair> wsConceptPairList = wsResult.get(selectedInputConcept);
                wsValidSet = setWSResultTable(selectedInputConcept, wsConceptPairList);
            } else {
                setWSResultTable(new DefaultTableModel(null, ConceptDefinitionResultPanel.WS_COLUMN_NAMES));
            }

            if (arResult != null && arResult.containsKey(selectedInputConcept)) {
                List<ConceptPair> arConceptPairList = arResult.get(selectedInputConcept);
                arValidSet = setARResultTable(selectedInputConcept, arConceptPairList);
            } else {
                setARResultTable(new DefaultTableModel(null, ConceptDefinitionResultPanel.AR_COLUMN_NAMES));
            }

            if (wsValidSet != null && arValidSet != null) {
                setWAResultTable(selectedInputConcept, wsValidSet, arValidSet);
            } else {
                setWAResultTable(new DefaultTableModel(null, ConceptDefinitionResultPanel.WA_COLUMN_NAMES));
            }
        }
    }

    private void setWAResultTable(String inputConcept, Set<ConceptPair> validWSPairSet, Set<ConceptPair> validARPairSet) {
        Set<String[]> validDataSet = new HashSet<String[]>();
        for (ConceptPair wsPair : validWSPairSet) {
            for (ConceptPair arPair : validARPairSet) {
                if (wsPair.getToConceptLabel().equals(arPair.getToConceptLabel())) {
                    validDataSet.add(new String[] { wsPair.getTableData()[0], wsPair.getTableData()[1],
                            arPair.getTableData()[1]});
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

    public void addWrongPair(NonTaxonomicRelation nonTaxRel) {
        DefaultTableModel model = (DefaultTableModel) wrongPairTable.getModel();
        if (!wrongPairSet.contains(nonTaxRel)) {
            wrongPairSet.add(nonTaxRel);
        }
        reCalcWSandARValue();
        // System.out.println(wrongPairSet);
        model.addRow(new Object[] { nonTaxRel.getDomain(), nonTaxRel.getRange()});
    }

    public void addNonTaxonomicRelation(NonTaxonomicRelation nonTaxRel) {
        if (nonTaxRel == null || !nonTaxRel.isValid()) { return; }
        if (nonTaxRelSet.contains(nonTaxRel)) { return; }
        nonTaxRelSet.add(nonTaxRel);
        reCalcWSandARValue();
        // System.out.println(acceptedPairSet);
        DefaultTableModel model = (DefaultTableModel) conceptDefinitionTable.getModel();
        model.addRow(nonTaxRel.getAcceptedTableData());
    }

    /**
     * Set WordSpace Result to Table
     */
    public Set<ConceptPair> setWSResultTable(String selectedInputConcept, List<ConceptPair> wsConceptPairList) {
        Set<ConceptPair> validPairSet = new TreeSet<ConceptPair>();
        for (ConceptPair pair : wsConceptPairList) {
            String[] data = pair.getTableData();
            NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(selectedInputConcept, data[0]);
            if (!nonTaxRelSet.contains(nonTaxRel) && !wrongPairSet.contains(nonTaxRel)) {
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
            NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(inputConcept, data[0]);
            if (!nonTaxRelSet.contains(nonTaxRel) && !wrongPairSet.contains(nonTaxRel)) {
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
        TreeModel treeModel = constructPropertyTreePanel.getConceptTree().getModel();
        if (treeModel.getRoot() instanceof ConceptTreeNode) { return ((ConceptTreeNode) treeModel.getRoot())
                .getConcept(); }
        JOptionPane.showInternalMessageDialog(this, "概念定義には，プロパティ階層の構築が必要です．");
        return null;
    }

    private void setRelation() {
        if (conceptDefinitionTable.getSelectedRowCount() == 1) {
            ConceptSelectionDialog dialog = new ConceptSelectionDialog(ConceptTreeCellRenderer.VERB_CONCEPT_TREE,
                    "Relation Selection Dialog");
            TreeModel treeModel = constructPropertyTreePanel.getConceptTree().getModel();
            if (treeModel.getRoot() instanceof ConceptTreeNode) {
                dialog.setTreeModel(treeModel);
                dialog.setSingleSelection();
                dialog.setVisible(true);
                Concept propConcept = dialog.getConcept();
                if (propConcept != null) {
                    int row = conceptDefinitionTable.getSelectedRow();
                    int column = 2; // 関係を示すcolumnの番号を指定．column名から動的に取得すべき．
                    conceptDefinitionTable.setValueAt(propConcept, row, column);
                    String domain = (String) conceptDefinitionTable.getValueAt(row, column - 1);
                    String range = (String) conceptDefinitionTable.getValueAt(row, column + 1);
                    for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
                        if (nonTaxRel.getDomain().equals(domain) && nonTaxRel.getRange().equals(range)) {
                            nonTaxRel.setRelation(propConcept);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == setRelationButton) {
            setRelation();
        } else if (e.getSource() == deleteAcceptedPairButton) {
            deleteConceptDefinition();
        } else if (e.getSource() == deleteWrongPairButton) {
            deleteWrongPair();
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
                String range = (String) definedTableModel.getValueAt(row[i], 3);
                NonTaxonomicRelation delNonTaxRel = new NonTaxonomicRelation(domain, range);
                deleteNonTaxRelSet.add(delNonTaxRel);
                nonTaxRelSet.remove(delNonTaxRel);
            }
            reCalcWSandARValue();
            for (int i = 0; i < definedTableModel.getRowCount(); i++) {
                String domain = (String) definedTableModel.getValueAt(i, 1);
                String range = (String) definedTableModel.getValueAt(i, 3);
                NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(domain, range);
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
        Set<NonTaxonomicRelation> deleteWrongPairSet = new HashSet<NonTaxonomicRelation>();
        int row[] = wrongPairTable.getSelectedRows();

        if (row != null) {
            for (int i = 0; i < row.length; i++) {
                String c1 = (String) wrongPairTableModel.getValueAt(row[i], 0);
                String c2 = (String) wrongPairTableModel.getValueAt(row[i], 1);
                NonTaxonomicRelation delNonTaxRel = new NonTaxonomicRelation(c1, c2);
                deleteWrongPairSet.add(delNonTaxRel);
                wrongPairSet.remove(delNonTaxRel);
            }
            reCalcWSandARValue();
            for (int i = 0; i < wrongPairTableModel.getRowCount(); i++) {
                String c1 = (String) wrongPairTableModel.getValueAt(i, 0);
                String c2 = (String) wrongPairTableModel.getValueAt(i, 1);
                NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(c1, c2);
                if (deleteWrongPairSet.contains(nonTaxRel)) {
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

        private ImageIcon rightIcon = Utils.getImageIcon("right_arrow.gif");
        private ImageIcon leftIcon = Utils.getImageIcon("left_arrow.gif");

        public ConceptDefinitionPanel() {
            c1Label = new JLabel();
            c2Label = new JLabel();
            reverseButton = new JButton(Translator.getString("ConceptDefinitionPanel.Reverse"));
            reverseButton.addActionListener(this);
            addAcceptedPairButton = new JButton(Translator.getString("ConceptDefinitionPanel.AddCorrectConceptPair"));
            addAcceptedPairButton.addActionListener(this);
            addWrongPairButton = new JButton(Translator.getString("ConceptDefinitionPanel.AddWrongConceptPair"));
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
            } else if (e.getSource() == addWrongPairButton) {
                String c1 = c1Label.getText();
                String c2 = c2Label.getText();
                if (c1.equals("") || c2.equals("")) { return; }
                NonTaxonomicRelation nonTaxRel = new NonTaxonomicRelation(c1, c2);
                addWrongPair(nonTaxRel);
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
                    nonTaxRel = new NonTaxonomicRelation(c1Label.getText(), propRootConcept, c2Label.getText());
                } else {
                    nonTaxRel = new NonTaxonomicRelation(c2Label.getText(), propRootConcept, c1Label.getText());
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
        if (!(0 <= row && 0 < conceptDefinitionTable.getModel().getColumnCount())) { return; }
        Boolean isMetaProperty = (Boolean) conceptDefinitionTable.getModel().getValueAt(row, 0);
        String domain = (String) conceptDefinitionTable.getModel().getValueAt(row, 1);
        String range = (String) conceptDefinitionTable.getModel().getValueAt(row, 3);

        for (NonTaxonomicRelation nonTaxRel : nonTaxRelSet) {
            if (nonTaxRel.getDomain().equals(domain) && nonTaxRel.getRange().equals(range)) {
                nonTaxRel.setMetaProperty(isMetaProperty);
                break;
            }
        }
    }
}