package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

/*
 * Created on 2004/02/05
 *  
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author shigeta
 * 
 * 2004-07-15 modified by takeshi morita
 * 
 */
public class ConceptDriftManagementPanel extends JPanel implements ActionListener, ListSelectionListener {

    private JList mraJList;
    private List<List<ConceptTreeNode>> mraList;
    private JList traJList;
    private JTree trimmedNodeTree;
    private List<ConceptTreeNode> traList;
    private TitledBorder mraPanelBorder;
    private TitledBorder traPanelBorder;
    private JButton traButton;

    private JTree conceptTree;

    private JTextField trimmedNumField;
    private ConceptTreeMaker maker = ConceptTreeMaker.getInstance();

    private DODDLEProject project;

    public ConceptDriftManagementPanel(String type, JTree tree, DODDLEProject p) {
        project = p;
        conceptTree = tree;
        mraList = new ArrayList<List<ConceptTreeNode>>();
        mraJList = new JList();
        mraJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mraJList.addListSelectionListener(this);
        traList = new ArrayList<ConceptTreeNode>();
        traJList = new JList();
        traJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        traJList.addListSelectionListener(this);
        trimmedNodeTree = new JTree();
        trimmedNodeTree.setCellRenderer(new ConceptTreeCellRenderer(type));
        trimmedNodeTree.setModel(new DefaultTreeModel(null));
        trimmedNodeTree.setEditable(false);
        traButton = new JButton(Translator.getString("ConceptTreePanel.TrimmedResultAnalysis"));
        traButton.addActionListener(this);
        trimmedNumField = new JTextField(5);
        trimmedNumField.setText("3");

        this.setLayout(new GridLayout(1, 2));
        this.add(setMRAPanel());
        this.add(setTRAPanel());
        this.setBorder(BorderFactory
                .createTitledBorder(Translator.getString("ConceptTreePanel.ConceptDriftManagement")));
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == traJList) {
            traAction();
        } else if (e.getSource() == mraJList) {
            mraAction();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == traButton) {
            int trimmedNum = 3;
            try {
                trimmedNum = Integer.parseInt(trimmedNumField.getText());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            maker.resetTRA();
            maker.trimmedResultAnalysis((ConceptTreeNode) conceptTree.getModel().getRoot(), trimmedNum);
            traList = new ArrayList<ConceptTreeNode>(maker.getTRAresult());
            setTRADefaultValue();
            repaint();
        }
    }

    /**
     * 
     * リストで選択されているTRAで分析された箇所の選択，グループ化を行う
     * 
     */
    private void traAction() {
        if (traList != null && !traList.isEmpty()) {
            int index = traJList.getSelectedIndex();
            if (index == -1) { return; }
            ConceptTreeNode traNode = traList.get(index);
            if (DODDLE.doddlePlugin != null) {
                List set = new ArrayList();
                set.add(traNode);
                if (traNode.getParent() != null) {
                    set.add(traNode.getParent());
                }
                DODDLE.doddlePlugin.selectNodes(changeToURISet(set));
            }
            DefaultTreeModel treeModel = (DefaultTreeModel) conceptTree.getModel();
            TreeNode[] nodes = treeModel.getPathToRoot(traNode);
            TreePath path = new TreePath(nodes);
            conceptTree.scrollPathToVisible(path);
            conceptTree.setSelectionPath(path);
            Concept trimmedTreeRootConcept = ((ConceptTreeNode) traNode.getParent()).getConcept();
            trimmedNodeTree.setModel(getTrimmedTreeModel(trimmedTreeRootConcept, traNode.getTrimmedConceptList()));
            for (int i = 0; i < trimmedNodeTree.getRowCount(); i++) {
                trimmedNodeTree.expandPath(trimmedNodeTree.getPathForRow(i));
            }
        }
    }

    private TreeModel getTrimmedTreeModel(Concept trimmedTreeRootConcept, List<List<Concept>> trimmedConceptList) {
        ConceptTreeNode rootNode = new ConceptTreeNode(trimmedTreeRootConcept, project);
        TreeModel trimmedTreeModel = new DefaultTreeModel(rootNode);
        for (List<Concept> list : trimmedConceptList) {
            DefaultMutableTreeNode parentNode = rootNode;
            for (Concept tc : list) {
                ConceptTreeNode childNode = new ConceptTreeNode(tc, project);
                parentNode.insert(childNode, 0);
                parentNode = childNode;
            }
        }
        return trimmedTreeModel;
    }

    /**
     * 
     * リストで選択されているMRAで分析された箇所の選択，グループ化を行う
     * 
     */
    private void mraAction() {
        if (mraList != null && !mraList.isEmpty()) {
            int index = mraJList.getSelectedIndex();

            if (index == -1) { return; }
            List<ConceptTreeNode> stm = mraList.get(index);
            if (DODDLE.doddlePlugin != null) {
                DODDLE.doddlePlugin.selectNodes(changeToURISet(stm));
            }
            DefaultTreeModel treeModel = (DefaultTreeModel) conceptTree.getModel();
            TreePath[] paths = new TreePath[stm.size()];
            int num = 0;
            for (TreeNode node : stm) {
                TreeNode[] nodes = treeModel.getPathToRoot(node);
                paths[num++] = new TreePath(nodes);
            }
            conceptTree.scrollPathToVisible(paths[0]);
            conceptTree.setSelectionPaths(paths);
        }
    }

    /**
     * http://mmm.semanticweb.org/doddle#をノードに付ける
     */
    private Set changeToURISet(List<ConceptTreeNode> stm) {
        Set uri = new HashSet();
        for (ConceptTreeNode node : stm) {
            uri.add(DODDLE.BASE_URI + node.getIdStr());
        }
        return uri;
    }

    /**
     * matched result analysis を制御するパネルを作る
     */
    private JPanel setMRAPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(mraJList), BorderLayout.CENTER);
        mraPanelBorder = BorderFactory.createTitledBorder(Translator
                .getString("ConceptTreePanel.MatchedResultAnalysisResult"));
        panel.setBorder(mraPanelBorder);
        return panel;
    }

    /**
     * trimmed result analysis を制御するパネルを作る
     */
    private JPanel setTRAPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JPanel southPanel = new JPanel();
        southPanel.add(new JSeparator());
        southPanel.add(trimmedNumField);
        southPanel.add(traButton);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(2, 1));
        listPanel.add(new JScrollPane(traJList));
        JScrollPane trimmedNodeJListScroll = new JScrollPane(trimmedNodeTree);
        trimmedNodeJListScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("ConceptDriftManagementPanel.TrimmedNodeList")));
        listPanel.add(trimmedNodeJListScroll);
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        traPanelBorder = BorderFactory.createTitledBorder(Translator
                .getString("ConceptTreePanel.TrimmedResultAnalysisResult"));
        mainPanel.setBorder(traPanelBorder);
        return mainPanel;
    }

    /**
     * 初期値をセットする
     */
    public void setConceptDriftManagementResult() {
        mraList = new ArrayList<List<ConceptTreeNode>>(maker.getMRAresult());
        traList = new ArrayList<ConceptTreeNode>(maker.getTRAresult());
        setMRADefaultValue();
        setTRADefaultValue();
        repaint();
    }

    private void setMRADefaultValue() {
        List list = new ArrayList();
        for (int i = 0; i < mraList.size(); i++) {
            List<ConceptTreeNode> nodeList = mraList.get(i);
            ConceptTreeNode sinNode = nodeList.get(0); // 0番目にSINノードが格納されている
            list.add(i + 1 + ": " + sinNode.getConcept());
        }
        mraJList.setListData(list.toArray());
        mraPanelBorder.setTitle(Translator.getString("ConceptTreePanel.MatchedResultAnalysisResult") + " ("
                + list.size() + ")");
    }

    private void setTRADefaultValue() {
        List list = new ArrayList();
        for (int i = 0; i < traList.size(); i++) {
            ConceptTreeNode traNode = traList.get(i);
            list.add(i + 1 + ": " + traNode.getConcept() + " (" + traNode.getTrimmedCountList() + ")");
        }
        traJList.setListData(list.toArray());
        traPanelBorder.setTitle(Translator.getString("ConceptTreePanel.TrimmedResultAnalysisResult") + " ("
                + traList.size() + ")");
    }
}