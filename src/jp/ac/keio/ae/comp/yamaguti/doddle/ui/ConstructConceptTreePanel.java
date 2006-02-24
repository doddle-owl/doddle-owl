package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

/*
 * 2005/03/01
 *  
 */

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 * 
 */
public class ConstructConceptTreePanel extends JPanel implements ComplexConceptTreeInterface {

    private UndefinedWordListPanel undefinedWordListPanel;
    private ConceptTreePanel conceptTreePanel;
    private ConceptDescriptionPanel conceptDescriptionPanel;

    private ConceptDriftManagementPanel controlPanel;
    private ConceptTreeMaker treeMaker = ConceptTreeMaker.getInstance();

    private DODDLEProject project;

    public void setUndefinedWordListModel(ListModel model) {
        undefinedWordListPanel.setUndefinedWordListModel(model);
        repaint();
    }

    public Map getIDTypicalWordMap() {
        return conceptTreePanel.getConceptTypicalWordMap();
    }

    public void loadIDTypicalWord(Map idTypicalWordMap) {
        conceptTreePanel.loadConceptTypicalWord(idTypicalWordMap);
    }

    public TreeModel getConceptTreeModel() {
        return conceptTreePanel.getConceptTree().getModel();
    }

    public JTree getConceptTree() {
        return conceptTreePanel.getConceptTree();
    }

    public ConstructConceptTreePanel(DODDLEProject p) {
        project = p;
        undefinedWordListPanel = new UndefinedWordListPanel();
        conceptTreePanel = new ConceptTreePanel("ŠT”OŠK‘w", undefinedWordListPanel, p);
        controlPanel = new ConceptDriftManagementPanel(conceptTreePanel.getConceptTree());

        conceptDescriptionPanel = new ConceptDescriptionPanel(conceptTreePanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE));
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(conceptDescriptionPanel, BorderLayout.CENTER);
        eastPanel.add(controlPanel, BorderLayout.SOUTH);

        JSplitPane westPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, undefinedWordListPanel, conceptTreePanel);
        westPane.setOneTouchExpandable(true);
        westPane.setDividerSize(10);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPane, eastPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(DODDLE.DIVIDER_SIZE);
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
    }

    public TreeModel getDefaultConceptTreeModel(Set pathSet) {
        return treeMaker.getDefaultConceptTreeModel(pathSet, project);
    }

    public void addJPWord(String identity, String word) {
        conceptTreePanel.addJPWord(identity, word);
    }

    public void addSubConcept(String identity, String word) {
        conceptTreePanel.addSubConcept(identity, word);
    }

    public void addComplexWordConcept(Map matchedWordIDMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTreePanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        conceptTreePanel.addComplexWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode);
    }

    public void init() {
        ConceptTreeMaker.getInstance().init();
        conceptTreePanel.getConceptTree().setModel(new DefaultTreeModel(null));
    }

    public TreeModel getTreeModel(Set conceptSet) {
        Set pathSet = treeMaker.getPathList(conceptSet);
        TreeModel model = treeMaker.getTrimmedTreeModel(pathSet, project);
        controlPanel.setDefaultValue();
        conceptTreePanel.checkAllMultipleInheritanceNode(model);
        return model;
    }

    public void setTreeModel(TreeModel model) {
        conceptTreePanel.getConceptTree().setModel(model);
    }

    public void checkMultipleInheritance(TreeModel model) {
        conceptTreePanel.checkAllMultipleInheritanceNode(model);
    }

    public Set getAllConceptID() {
        return conceptTreePanel.getAllConceptID();
    }

    public ConceptTreeNode getTreeModelRoot() {
        JTree conceptTree = conceptTreePanel.getConceptTree();
        if (conceptTree.getModel().getRoot() instanceof ConceptTreeNode) { return (ConceptTreeNode) conceptTree
                .getModel().getRoot(); }
        return null;
    }

    public void expandTree() {
        JTree conceptTree = conceptTreePanel.getConceptTree();
        for (int i = 0; i < conceptTree.getRowCount(); i++) {
            conceptTree.expandPath(conceptTree.getPathForRow(i));
        }
    }

    public Map<String, Concept> getComplexWordConceptMap() {
        return conceptTreePanel.getComplexWordConceptMap();
    }

    public Set getSupConceptSet(String id) {
        return conceptTreePanel.getSupConceptSet(id);
    }

    public void setVisibleConceptTree(boolean isVisible) {
        conceptTreePanel.getConceptTree().setVisible(isVisible);
    }
}