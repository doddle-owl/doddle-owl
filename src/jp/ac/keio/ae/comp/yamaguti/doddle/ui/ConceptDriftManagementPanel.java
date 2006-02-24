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
    private List mraList;
    private JList traJList;
    private List traList;
    private TitledBorder mraPanelBorder;
    private TitledBorder traPanelBorder;
    private JButton traButton;

    private JTree conceptTree;

    private JTextField trimmedNumField;
    private ConceptTreeMaker maker = ConceptTreeMaker.getInstance();

    public ConceptDriftManagementPanel(JTree tree) {
        conceptTree = tree;
        mraList = new ArrayList();
        mraJList = new JList();
        mraJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mraJList.addListSelectionListener(this);
        traList = new ArrayList();
        traJList = new JList();
        traJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        traJList.addListSelectionListener(this);
        traButton = new JButton("剪定結果分析の実行");
        traButton.addActionListener(this);
        trimmedNumField = new JTextField(5);
        trimmedNumField.setText("3");

        this.setLayout(new GridLayout(1, 2));
        this.add(setMRAPanel());
        this.add(setTRAPanel());
        this.setBorder(BorderFactory.createTitledBorder("概念変動管理"));
    }

    public void setResultList() {
        mraList = new ArrayList(maker.getMRAresult());
        traList = new ArrayList(maker.getTRAresult());
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
            maker.TRAnalysis((ConceptTreeNode) conceptTree.getModel().getRoot(), trimmedNum);
            traList = new ArrayList(maker.getTRAresult());
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
        // if (maker.getTRAresult() != null && !maker.getTRAresult().isEmpty())
        // {
        if (traList != null && !traList.isEmpty()) {
            // List list = maker.getTRAresult();
            int index = traJList.getSelectedIndex();
            if (index == -1) { return; }
            if (DODDLE.doddlePlugin != null) {
                Set set = new HashSet();
                // TreeNode node = (TreeNode) list.get(index);
                TreeNode node = (TreeNode) traList.get(index);
                set.add(node);
                if (node.getParent() != null) {
                    set.add(node.getParent());
                }
                DODDLE.doddlePlugin.selectNodes(changeToURISet(set));
            }
            DefaultTreeModel treeModel = (DefaultTreeModel) conceptTree.getModel();
            // TreeNode[] nodes = treeModel.getPathToRoot((TreeNode)
            // list.get(index));
            TreeNode[] nodes = treeModel.getPathToRoot((TreeNode) traList.get(index));
            TreePath path = new TreePath(nodes);
            conceptTree.scrollPathToVisible(path);
            conceptTree.setSelectionPath(path);
        }
    }

    /**
     * 
     * リストで選択されているMRAで分析された箇所の選択，グループ化を行う
     * 
     */
    private void mraAction() {
        // if (maker.getMRAresult() != null && !maker.getMRAresult().isEmpty())
        // {
        if (mraList != null && !mraList.isEmpty()) {
            int index = mraJList.getSelectedIndex();
            // List list = maker.getMRAresult();

            if (index == -1) { return; }
            // Set set = (Set) list.get(index);
            Set set = (Set) mraList.get(index);
            if (DODDLE.doddlePlugin != null) {
                DODDLE.doddlePlugin.selectNodes(changeToURISet(set));
            }
            DefaultTreeModel treeModel = (DefaultTreeModel) conceptTree.getModel();
            TreePath[] paths = new TreePath[set.size()];
            int num = 0;
            for (Iterator i = set.iterator(); i.hasNext();) {
                TreeNode[] nodes = treeModel.getPathToRoot((TreeNode) i.next());
                paths[num++] = new TreePath(nodes);
            }
            conceptTree.scrollPathToVisible(paths[0]);
            conceptTree.setSelectionPaths(paths);
        }
    }

    /**
     * http://mmm.semanticweb.org/doddle#をノードに付ける
     */
    private Set changeToURISet(Set set) {
        Set uri = new HashSet();
        for (Iterator i = set.iterator(); i.hasNext();) {
            ConceptTreeNode node = (ConceptTreeNode) i.next();
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
        mraPanelBorder = BorderFactory.createTitledBorder("照合結果分析の結果");
        panel.setBorder(mraPanelBorder);
        return panel;
    }

    /**
     * trimmed result analysis を制御するパネルを作る
     */
    private JPanel setTRAPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel southPanel = new JPanel();
        southPanel.add(new JSeparator());
        southPanel.add(trimmedNumField);
        southPanel.add(traButton);
        panel.add(new JScrollPane(traJList), BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
        traPanelBorder = BorderFactory.createTitledBorder("剪定結果分析の結果");
        panel.setBorder(traPanelBorder);
        return panel;
    }

    /**
     * 初期値をセットする
     */
    public void setDefaultValue() {
        setResultList();
        setMRADefaultValue();
        setTRADefaultValue();
        repaint();
    }

    private void setMRADefaultValue() {
        List list = new ArrayList();
        // for (int i = 0; i < maker.getMRAresult().size(); i++) {
        for (int i = 0; i < mraList.size(); i++) {
            list.add("MRA Result " + (i + 1));
        }
        mraJList.setListData(list.toArray());
        mraPanelBorder.setTitle("照合結果分析の結果 (" + list.size() + ")");
    }

    private void setTRADefaultValue() {
        // List list = maker.getTRAresult();
        // traJList.setListData(list.toArray());
        // traPanelBorder.setTitle("剪定結果分析の結果 (" + list.size() + ")");
        traJList.setListData(traList.toArray());
        traPanelBorder.setTitle("剪定結果分析の結果 (" + traList.size() + ")");
    }
}