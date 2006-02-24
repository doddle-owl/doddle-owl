/*
 * @(#)  2005/09/15
 *
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class ConceptSelectionDialog extends JDialog implements ActionListener {

    private Set selectedConceptSet;
    private JTree conceptTree;
    private JButton expandButton;
    private JButton applyButton;
    private JButton cancelButton;

    public ConceptSelectionDialog(String type) {
        selectedConceptSet = new HashSet();

        conceptTree = new JTree(new DefaultTreeModel(null));
        conceptTree.setCellRenderer(new ConceptTreeCellRenderer(type));
        JScrollPane conceptTreeScroll = new JScrollPane(conceptTree);

        expandButton = new JButton("すべてのパスを展開");
        expandButton.addActionListener(this);
        applyButton = new JButton("OK");
        applyButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(expandButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        JPanel eastButtonPanel = new JPanel();
        eastButtonPanel.setLayout(new BorderLayout());
        eastButtonPanel.add(buttonPanel, BorderLayout.EAST);

        getContentPane().add(conceptTreeScroll, BorderLayout.CENTER);
        getContentPane().add(eastButtonPanel, BorderLayout.SOUTH);

        setTitle("概念選択ダイアログ");
        setSize(800, 600);
        setModal(true);
        setLocationRelativeTo(DODDLE.rootPane);
    }

    public void setSingleSelection() {
        conceptTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void setTreeModel(TreeModel model) {
        conceptTree.setModel(model);
        expandAllPath();
    }

    private void expandAllPath() {
        for (int i = 0; i < conceptTree.getRowCount(); i++) {
            conceptTree.expandPath(conceptTree.getPathForRow(i));
        }
    }

    public void setVisible(boolean t) {
        conceptTree.clearSelection();
        super.setVisible(t);
    }

    public Set getConceptSet() {
        return selectedConceptSet;
    }

    public Concept getConcept() {
        if (selectedConceptSet.size() == 1) { return (Concept) selectedConceptSet.toArray()[0]; }
        return null;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == expandButton) {
            expandAllPath();
        } else if (e.getSource() == applyButton) {
            selectedConceptSet.clear();
            TreePath[] paths = conceptTree.getSelectionPaths();
            for (int i = 0; i < paths.length; i++) {
                ConceptTreeNode node = (ConceptTreeNode) paths[i].getLastPathComponent();
                selectedConceptSet.add(node.getConcept());
            }
            setVisible(false);
        } else if (e.getSource() == cancelButton) {
            setVisible(false);
        }
    }
}
