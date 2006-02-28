package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/*
 * @(#)  2005/07/17
 *
 */

/**
 * @author takeshi morita
 */
public class ConstructPropertyTreePanel extends JPanel implements ComplexConceptTreeInterface {

    private UndefinedWordListPanel undefinedWordListPanel;
    private ConceptTreePanel conceptTreePanel;
    private ConceptDescriptionPanel conceptDescriptionPanel;

    private ConceptDriftManagementPanel controlPanel;
    private EDRConceptDefinitionPanel conceptDefinitionPanel;
    private static ConceptTreeMaker treeMaker = ConceptTreeMaker.getInstance();

    private DODDLEProject project;

    public void setUndefinedWordListModel(ListModel model) {
        undefinedWordListPanel.setUndefinedWordListModel(model);
    }

    public Map getIDTypicalWordMap() {
        return conceptTreePanel.getConceptTypicalWordMap();
    }

    public void loadIDTypicalWord(Map idTypicalWordMap) {
        conceptTreePanel.loadConceptTypicalWord(idTypicalWordMap);
    }

    public ConstructPropertyTreePanel(DODDLEProject p) {
        project = p;
        undefinedWordListPanel = new UndefinedWordListPanel();
        conceptTreePanel = new ConceptTreePanel("ÉvÉçÉpÉeÉBäKëw", undefinedWordListPanel, project);
        conceptDefinitionPanel = new EDRConceptDefinitionPanel(p);
        controlPanel = new ConceptDriftManagementPanel(conceptTreePanel.getConceptTree());
        JTabbedPane tab = new JTabbedPane();
        tab.add(conceptDefinitionPanel, "äTîOíËã`");
        tab.add(controlPanel, "äTîOïœìÆä«óù");

        conceptDescriptionPanel = new ConceptDescriptionPanel(conceptTreePanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.VERB_CONCEPT_TREE), conceptDefinitionPanel);
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(conceptDescriptionPanel, BorderLayout.CENTER);
        // eastPanel.add(controlPanel, BorderLayout.SOUTH);
        eastPanel.add(tab, BorderLayout.SOUTH);

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

    public void addJPWord(String id, String word) {
        conceptTreePanel.addJPWord(id, word);
    }

    public void addSubConcept(String id, String word) {
        conceptTreePanel.addSubConcept(id, word);
    }

    public void addComplexWordConcept(Map matchedWordIDMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTreePanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        conceptTreePanel.addComplexWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode);
    }

    public void init() {
        treeMaker.init();
        conceptTreePanel.getConceptTree().setModel(new DefaultTreeModel(null));
        conceptDefinitionPanel.init();
    }

    private void removeNounConceptPath(Set<List<Concept>> pathSet) {
        Set<List<Concept>> removeSet = new HashSet<List<Concept>>();
        for (List<Concept> path : pathSet) {
            Concept c = path.get(1);
            if (!(c.getId().equals("30f801") || c.getId().equals("30f83e"))) {
                removeSet.add(path);
                // System.out.println("path: " + path);
            }
        }
        pathSet.removeAll(removeSet);
    }

    public TreeModel getTreeModel(Set nounIDSet, Set verbIDSet) {
        Set<List<Concept>> pathSet = treeMaker.getPathList(verbIDSet);
        removeNounConceptPath(pathSet); // à⁄ìÆÇ∆çsà◊à»äOÇÃÉpÉXÇçÌèúÇ∑ÇÈ
        TreeModel propertyTreeModel = treeMaker.getTrimmedTreeModel(pathSet, project);
        ConceptTreeNode rootNode = (ConceptTreeNode) propertyTreeModel.getRoot();
        VerbConcept propRoot = new VerbConcept(ConceptTreeMaker.EDR_PROPERTY_ROOT_ID, "ìÆéåìIäTîO");
        propRoot.setPrefix("keio");
        rootNode.setConcept(propRoot);
        setRegion(propertyTreeModel, nounIDSet);
        controlPanel.setDefaultValue();
        conceptTreePanel.checkAllMultipleInheritanceNode(propertyTreeModel);
        return propertyTreeModel;
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

    /**
     * 
     * ñºéåìIÇ»äTîOÇçÌèú
     * 
     * @param model
     * @param rootNode
     */
    public void removeNounNode() {
        DefaultTreeModel treeModel = (DefaultTreeModel) conceptTreePanel.getConceptTree().getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
        ConceptTreeNode eventNode = null;
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) rootNode.getChildAt(i);
            if (childNode.getConcept().getId().equals("30f7e4")) { // éñè€
                eventNode = childNode;
                // éñè€ÉmÅ[ÉhÇÃéqÉmÅ[ÉhÇÕÅCÇ∑Ç◊ÇƒìÆéåìIäTîOÇÃÉãÅ[ÉgäTîOÇÃâ∫à äTîOÇ…à⁄ìÆ
                for (int j = 0; j < eventNode.getChildCount(); j++) {
                    ConceptTreeNode eventChildNode = (ConceptTreeNode) eventNode.getChildAt(j);
                    treeModel.removeNodeFromParent(eventChildNode);
                    treeModel.insertNodeInto(eventChildNode, rootNode, 0);
                }
            }
        }
        if (eventNode != null) {
            treeModel.removeNodeFromParent(eventNode); // Ç±ÇÍÇÊÇËëOÇ≈è¡Ç∑Ç∆éñè€ÇÃéqóvëfÇ™Ç∑Ç◊ÇƒçÌèúÇ≥ÇÍÇƒÇµÇ‹Ç§
        }
    }

    /**
     * 
     * ÉNÉâÉXäKëwÇ…ë∂ç›ÇµÇ»Ç¢äTîOÇdomain, rangeÇ©ÇÁçÌèú
     * 
     * @param regionSet
     * @param nounIDSet
     */
    private void removeSet(Set regionSet, Set nounIDSet) {
        Set removeSet = new HashSet();
        for (Iterator i = regionSet.iterator(); i.hasNext();) {
            String id = (String) i.next();
            if (!nounIDSet.contains(id)) {
                removeSet.add(id);
            }
        }
        regionSet.removeAll(removeSet);
    }

    private Set abstractRegion(Set regionSet, Set nounIDSet) {
        Set abstractRegionSet = new HashSet();
        for (Iterator i = regionSet.iterator(); i.hasNext();) {
            String id = (String) i.next();
            if (nounIDSet.contains(id)) {
                abstractRegionSet.add(id);
            } else {
                String subID = EDRTree.getEDRTree().getSubID(id, nounIDSet);
                if (subID != null) {
                    // System.out.println(InputModule.getEDRConcept(id) + " Å® "
                    // + InputModule.getEDRConcept(subID));
                    abstractRegionSet.add(subID);
                }
            }
        }
        return abstractRegionSet;
    }

    private void setRegion(TreeNode node, ConceptDefinition conceptDefinition, Set nounIDSet) {
        if (node.getChildCount() == 0) { return; }
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.getConcept() instanceof VerbConcept) {
                VerbConcept c = (VerbConcept) childNode.getConcept();

                // System.out.println("concept: " + childNode.getConcept() +
                // "trimmed concept: "
                // + c.getTrimmedConceptSet());
                Set domainSet = conceptDefinition.getIDSet("agent", c.getId(), c.getTrimmedConceptSet());
                // System.out.println("verb concept: " + c);
                // System.out.println("domain: " + domainSet);
                domainSet = abstractRegion(domainSet, nounIDSet);
                // System.out.println("abstract domain: " + domainSet);
                // removeSet(domainSet, nounIDSet);
                Set rangeSet = conceptDefinition.getIDSet("object", c.getId(), c.getTrimmedConceptSet());
                // System.out.println("range: " + rangeSet);
                rangeSet = abstractRegion(rangeSet, nounIDSet);
                // System.out.println("abstract range: " + rangeSet);
                // removeSet(rangeSet, nounIDSet);

                c.addAllDomain(domainSet);
                c.addAllRange(rangeSet);
                childNode.setConcept(c);
            }
            setRegion(childNode, conceptDefinition, nounIDSet);
        }
    }

    private void setRegion(TreeModel propertyTreeModel, Set nounIDSet) {
        TreeNode rootNode = (TreeNode) propertyTreeModel.getRoot();
        setRegion(rootNode, ConceptDefinition.getInstance(), nounIDSet);
    }

    public ConceptTreeNode getTreeModelRoot() {
        JTree conceptTree = conceptTreePanel.getConceptTree();
        if (conceptTree.getModel().getRoot() instanceof ConceptTreeNode) { return (ConceptTreeNode) conceptTree
                .getModel().getRoot(); }
        return null;
    }

    public boolean isConceptContains(Concept c) {
        return conceptTreePanel.isConceptContains(c);
    }

    public JTree getConceptTree() {
        return conceptTreePanel.getConceptTree();
    }

    public void expandTree() {
        JTree conceptTree = conceptTreePanel.getConceptTree();
        for (int i = 0; i < conceptTree.getRowCount(); i++) {
            conceptTree.expandPath(conceptTree.getPathForRow(i));
        }
    }
    public Set getSupConceptSet(String id) {
        return conceptTreePanel.getSupConceptSet(id);
    }

    public void setVisibleConceptTree(boolean isVisible) {
        conceptTreePanel.getConceptTree().setVisible(isVisible);
    }
}
