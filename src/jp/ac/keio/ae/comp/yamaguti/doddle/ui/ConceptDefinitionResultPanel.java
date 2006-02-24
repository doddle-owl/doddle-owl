package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import org.semanticweb.mmm.mr3.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author shigeta
 * 
 * modified by takeshi morita
 */
public class ConceptDefinitionResultPanel extends JPanel implements ActionListener, ListSelectionListener {

    private JList inputConceptJList;
    private JButton setInputConceptButton;
    private ConceptDefinitionAlgorithmPanel algorithmPanel;

    private Set acceptedPairSet;
    private Set wrongPairSet;

    private JList inputDocJList;
    private JButton setInputDocButton;

    private JTable wsResultTable;
    private JTable arResultTable;
    private JTable waResultTable;

    private JTable conceptDefinitionTable;
    private JButton setRelationButton;
    private JButton deleteAcceptedPairButton;

    private JTable wrongPairTable;
    private JButton deleteWrongPairButton;

    private DODDLEProject doddleProject;
    private ConstructPropertyTreePanel constructPropertyTreePanel;

    private static final String[] WS_COLUMN_NAMES = { "関連概念リスト", "WordSpace値"};
    private static final String[] AR_COLUMN_NAMES = { "関連概念リスト", "Apriori値"};
    private static final String[] WA_COLUMN_NAMES = { "関連概念リスト", "WordSpace値", "Apriori値"};

    public ConceptDefinitionResultPanel(JList icList, ConceptDefinitionAlgorithmPanel ap, DODDLEProject project) {
        algorithmPanel = ap;
        doddleProject = project;
        constructPropertyTreePanel = project.getConstructPropertyTreePanel();
        inputConceptJList = icList;
        setInputConceptButton = new JButton("入力概念をセット");
        setInputConceptButton.addActionListener(this);

        acceptedPairSet = new HashSet();
        wrongPairSet = new HashSet();

        definePanel = new ConceptDefinitionPanel();
        JTabbedPane resultTabpane = new JTabbedPane();
        JScrollPane scroll;
        DefaultTableModel resultModel = new DefaultTableModel(null, WS_COLUMN_NAMES);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        inputDocJList = new JList();
        inputDocJList.addListSelectionListener(this);
        JScrollPane inputDocJListScroll = new JScrollPane(inputDocJList);
        inputDocJListScroll.setBorder(BorderFactory.createTitledBorder("入力文書リスト"));
        setInputDocButton = new JButton("入力文書の設定");
        setInputDocButton.addActionListener(this);
        JPanel inputDocPanel = new JPanel();
        inputDocPanel.setLayout(new BorderLayout());
        inputDocPanel.add(inputDocJListScroll, BorderLayout.CENTER);
        JPanel inputDocButtonPanel = new JPanel();
        inputDocButtonPanel.setLayout(new BorderLayout());
        inputDocButtonPanel.add(setInputDocButton, BorderLayout.EAST);
        inputDocPanel.add(inputDocButtonPanel, BorderLayout.SOUTH);

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
        resultTabpane.addTab("WS & AR", scroll);

        resultTabpane.setFont(new Font("Dialog", Font.PLAIN, 14));
        resultTabpane.setTabPlacement(JTabbedPane.RIGHT);

        JPanel resultTabpanePanel = new JPanel();
        resultTabpanePanel.setLayout(new BorderLayout());
        resultTabpanePanel.add(inputDocPanel, BorderLayout.NORTH);
        resultTabpanePanel.add(resultTabpane, BorderLayout.CENTER);

        String[] definedColumnNames = { "メタプロパティ？", "定義域", "関係（プロパティ）", "値域"};
        conceptDefinitionTable = new JTable(new ResultTableModel(null, definedColumnNames));
        JScrollPane conceptDefinitionTableScroll = new JScrollPane(conceptDefinitionTable);
        setRelationButton = new JButton("関係（プロパティ）の設定");
        setRelationButton.addActionListener(this);
        deleteAcceptedPairButton = new JButton("正解ペアを削除");
        deleteAcceptedPairButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(setRelationButton);
        buttonPanel.add(deleteAcceptedPairButton);
        JPanel acceptedPairPanel = new JPanel();
        acceptedPairPanel.setLayout(new BorderLayout());
        acceptedPairPanel.add(conceptDefinitionTableScroll, BorderLayout.CENTER);
        acceptedPairPanel.add(getEastComponent(buttonPanel), BorderLayout.SOUTH);

        String[] wrongDefinedColumnNames = { "概念 1", "概念 2"};
        wrongPairTable = new JTable(new ResultTableModel(null, wrongDefinedColumnNames));
        JScrollPane wrongConceptPairTableScroll = new JScrollPane(wrongPairTable);
        deleteWrongPairButton = new JButton("不正解ペアを削除");
        deleteWrongPairButton.addActionListener(this);
        JPanel wrongPairPanel = new JPanel();
        wrongPairPanel.setLayout(new BorderLayout());
        wrongPairPanel.add(wrongConceptPairTableScroll, BorderLayout.CENTER);
        wrongPairPanel.add(getEastComponent(deleteWrongPairButton), BorderLayout.SOUTH);

        JPanel resultCandidatePanel = new JPanel();
        resultCandidatePanel.setLayout(new BorderLayout());
        resultCandidatePanel.add(resultTabpanePanel, BorderLayout.CENTER);
        JScrollPane inputConceptJListScroll = new JScrollPane(inputConceptJList);
        inputConceptJListScroll.setBorder(BorderFactory.createTitledBorder("入力概念リスト"));
        inputConceptJListScroll.setPreferredSize(new Dimension(200, 100));

        JPanel inputConceptPanel = new JPanel();
        inputConceptPanel.setLayout(new BorderLayout());
        inputConceptPanel.add(inputConceptJListScroll, BorderLayout.CENTER);
        inputConceptPanel.add(setInputConceptButton, BorderLayout.SOUTH);

        resultCandidatePanel.add(inputConceptPanel, BorderLayout.WEST);
        resultCandidatePanel.add(definePanel, BorderLayout.SOUTH);

        JTabbedPane resultTableTab = new JTabbedPane();
        resultTableTab.add("正解概念対", acceptedPairPanel);
        resultTableTab.add("不正解概念対", wrongPairPanel);

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

    public void loadAcceptedandWrongPairSet(Set acceptedSet, Set wrongSet) {
        for (Iterator i = acceptedSet.iterator(); i.hasNext();) {
            String pair = (String) i.next();
            String[] concepts = pair.split("-");
            addConceptDefinition(new Object[] { new Boolean(false), concepts[0], getPropertyRootConcept(), concepts[1]});
        }
        for (Iterator i = wrongSet.iterator(); i.hasNext();) {
            String pair = (String) i.next();
            String[] concepts = pair.split("-");
            addWrongPair(new Object[] { concepts[0], concepts[1]});
        }
    }

    public String getAcceptedandWrongPairText() {
        StringBuffer buf = new StringBuffer("");
        for (Iterator i = acceptedPairSet.iterator(); i.hasNext();) {
            buf.append(i.next() + ",true\n");
        }
        for (Iterator i = wrongPairSet.iterator(); i.hasNext();) {
            buf.append(i.next() + ",false\n");
        }
        return buf.toString();
    }

    private void reCalcWSandARValue() {
        calcWSandARValue((String) inputConceptJList.getSelectedValue());
    }

    public void setInputDocList(Set docSet) {
        inputDocJList.setListData(docSet.toArray());
    }

    public void calcWSandARValue(String selectedInputConcept) {
        Document currentDoc = (Document) inputDocJList.getSelectedValue();
        if (currentDoc == null) { return; }
        Set wsValidSet = null;
        Set arValidSet = null;
        Map wsResult = algorithmPanel.getDocWordSpaceResult().get(currentDoc);
        Map arResult = algorithmPanel.getDocAprioriResult().get(currentDoc);
        if (wsResult != null || arResult != null) {
            if (wsResult != null && wsResult.containsKey(selectedInputConcept)) {
                List wsList = (List) (wsResult.get(selectedInputConcept));
                wsValidSet = setWSResultTable(selectedInputConcept, wsList);
            } else {
                setWSResultTable(new DefaultTableModel(null, ConceptDefinitionResultPanel.WS_COLUMN_NAMES));
            }

            if (arResult != null && arResult.containsKey(selectedInputConcept)) {
                List arList = (List) (arResult.get(selectedInputConcept));
                arValidSet = setARResultTable(selectedInputConcept, arList);
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

    private void setWAResultTable(String inputConcept, Set validWSPairSet, Set validARPairSet) {
        Set validDataSet = new HashSet();
        for (Iterator i = validWSPairSet.iterator(); i.hasNext();) {
            ConceptPair wsPair = (ConceptPair) i.next();
            for (Iterator j = validARPairSet.iterator(); j.hasNext();) {
                ConceptPair arPair = (ConceptPair) j.next();
                if (wsPair.getToConceptString().equals(arPair.getToConceptString())) {
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

    public void addWrongPair(Object[] data) {
        DefaultTableModel model = (DefaultTableModel) wrongPairTable.getModel();
        if (data[0].equals("") || data[1].equals("")) { return; }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(data[0]) && model.getValueAt(i, 1).equals(data[1])) { return; }
        }
        wrongPairSet.add(data[0] + "-" + data[1]);
        reCalcWSandARValue();
        // System.out.println(wrongPairSet);
        model.addRow(data);
    }

    public void addConceptDefinition(Object[] data) {
        if (data == null) { return; }
        DefaultTableModel model = (DefaultTableModel) conceptDefinitionTable.getModel();
        if (data[1].equals("") || data[3].equals("")) { return; }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(data[1]) && model.getValueAt(i, 3).equals(data[3])) { return; }
        }
        acceptedPairSet.add(data[1] + "-" + data[3]);
        reCalcWSandARValue();
        // System.out.println(acceptedPairSet);
        model.addRow(data);
    }

    /**
     * Set WordSpace Result to Table
     */
    public Set setWSResultTable(String selectedInputConcept, List wordResult) {
        Set validPairSet = new TreeSet();
        for (Iterator i = wordResult.iterator(); i.hasNext();) {
            ConceptPair pair = (ConceptPair) i.next();
            String[] data = pair.getTableData();
            String pairStr = selectedInputConcept + "-" + data[0];
            // System.out.println(pairStr);
            if (!acceptedPairSet.contains(pairStr) && !wrongPairSet.contains(pairStr)) {
                validPairSet.add(pair);
            }
        }

        String[][] data = new String[validPairSet.size()][2];
        int cnt = 0;
        for (Iterator i = validPairSet.iterator(); i.hasNext();) {
            ConceptPair rp = (ConceptPair) i.next();
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

    public Set setARResultTable(String inputConcept, List arResult) {
        Set validPairSet = new TreeSet();
        for (Iterator i = arResult.iterator(); i.hasNext();) {
            ConceptPair pair = (ConceptPair) i.next();
            String[] data = pair.getTableData();
            String pairStr = inputConcept + "-" + data[0];
            // System.out.println(pairStr);
            if (!acceptedPairSet.contains(pairStr) && !wrongPairSet.contains(pairStr)) {
                validPairSet.add(pair);
            }
        }

        String[][] data = new String[validPairSet.size()][2];
        int cnt = 0;
        for (Iterator i = validPairSet.iterator(); i.hasNext();) {
            ConceptPair rp = (ConceptPair) i.next();
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
            ConceptSelectionDialog dialog = new ConceptSelectionDialog(ConceptTreeCellRenderer.VERB_CONCEPT_TREE);
            TreeModel treeModel = constructPropertyTreePanel.getConceptTree().getModel();
            if (treeModel.getRoot() instanceof ConceptTreeNode) {
                dialog.setTreeModel(treeModel);
                dialog.setSingleSelection();
                dialog.setVisible(true);
                Concept propConcept = dialog.getConcept();
                if (propConcept != null) {
                    int row = conceptDefinitionTable.getSelectedRow();
                    int column = conceptDefinitionTable.getSelectedColumn();
                    conceptDefinitionTable.setValueAt(propConcept, row, column);
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
        } else if (e.getSource() == setInputConceptButton) {
            algorithmPanel.setInputConcept();
        } else if (e.getSource() == setInputDocButton) {
            setInputDocList(doddleProject.getDocumentSelectionPanel().getDocSet());
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
        List domainList = new ArrayList();
        List rangeList = new ArrayList();
        int row[] = conceptDefinitionTable.getSelectedRows();

        if (row != null) {
            for (int i = 0; i < row.length; i++) {
                Object domain = definedTableModel.getValueAt(row[i], 1);
                Object range = definedTableModel.getValueAt(row[i], 3);
                domainList.add(domain);
                rangeList.add(range);
                acceptedPairSet.remove(domain + "-" + range);
            }
            reCalcWSandARValue();
            for (int i = 0; i < definedTableModel.getRowCount(); i++) {
                if (domainList.contains(definedTableModel.getValueAt(i, 1))
                        && rangeList.contains((definedTableModel.getValueAt(i, 3)))) {
                    definedTableModel.removeRow(i);
                    --i;
                }
            }
        }
        // System.out.println(acceptedPairSet);
    }

    private void deleteWrongPair() {
        DefaultTableModel wrongPairTableModel = (DefaultTableModel) wrongPairTable.getModel();
        List c1List = new ArrayList();
        List c2List = new ArrayList();
        int row[] = wrongPairTable.getSelectedRows();

        if (row != null) {
            for (int i = 0; i < row.length; i++) {
                Object c1 = wrongPairTableModel.getValueAt(row[i], 0);
                Object c2 = wrongPairTableModel.getValueAt(row[i], 1);
                c1List.add(c1);
                c2List.add(c2);
                wrongPairSet.remove(c1 + "-" + c2);
            }
            reCalcWSandARValue();
            for (int i = 0; i < wrongPairTableModel.getRowCount(); i++) {
                if (c1List.contains(wrongPairTableModel.getValueAt(i, 0))
                        && c2List.contains((wrongPairTableModel.getValueAt(i, 1)))) {
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
            reverseButton = new JButton("反転");
            reverseButton.addActionListener(this);
            addAcceptedPairButton = new JButton("正解ペアを追加");
            addAcceptedPairButton.addActionListener(this);
            addWrongPairButton = new JButton("不正解ペアを追加");
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
                addConceptDefinition(getConceptDefinitionData());
            } else if (e.getSource() == addWrongPairButton) {
                addWrongPair(new Object[] { c1Label.getText(), c2Label.getText()});
            }
        }

        private void reverseAction() {
            if (allowLabel.getIcon().equals(rightIcon)) {
                allowLabel.setIcon(leftIcon);
            } else {
                allowLabel.setIcon(rightIcon);
            }
        }

        public Object[] getConceptDefinitionData() {
            Concept propRootConcept = getPropertyRootConcept();
            Object[] data = null;
            if (propRootConcept != null) {
                if (allowLabel.getIcon().equals(rightIcon)) {
                    data = new Object[] { new Boolean(false), c1Label.getText(), propRootConcept, c2Label.getText()};
                } else {
                    data = new Object[] { new Boolean(false), c2Label.getText(), propRootConcept, c1Label.getText()};
                }
                allowLabel.setIcon(rightIcon);
            }
            return data;
        }

        public void setCText(String c1, String c2) {
            c1Label.setText(c1);
            c2Label.setText(c2);
        }
    }
}