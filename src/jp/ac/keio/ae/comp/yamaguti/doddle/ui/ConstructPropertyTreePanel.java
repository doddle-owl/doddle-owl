package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import org.apache.log4j.*;

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
    private ConceptInformationPanel conceptInfoPanel;

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
        conceptTreePanel = new ConceptTreePanel(Translator.getString("PropertyTreePanel.ConceptTree"),
                undefinedWordListPanel, project);
        conceptDefinitionPanel = new EDRConceptDefinitionPanel(p);
        controlPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.VERB_CONCEPT_TREE, conceptTreePanel
                .getConceptTree(), project);
        JTabbedPane tab = new JTabbedPane();
        tab.add(conceptDefinitionPanel, Translator.getString("PropertyTreePanel.ConceptDefinition"));
        tab.add(controlPanel, Translator.getString("ConceptTreePanel.ConceptDriftManagement"));

        conceptInfoPanel = new ConceptInformationPanel(conceptTreePanel.getConceptTree(), new ConceptTreeCellRenderer(
                ConceptTreeCellRenderer.VERB_CONCEPT_TREE), conceptDefinitionPanel);
        JSplitPane eastSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, conceptInfoPanel, tab);
        eastSplitPane.setDividerSize(DODDLE.DIVIDER_SIZE);
        eastSplitPane.setOneTouchExpandable(true);

        JSplitPane westPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, undefinedWordListPanel, conceptTreePanel);
        westPane.setOneTouchExpandable(true);
        westPane.setDividerSize(10);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPane, eastSplitPane);
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

    private int addedAbstractComplexConceptCnt;
    private int averageAbstracComplexConceptGroupSiblingConceptCnt;

    public int getAddedAbstractComplexConceptCnt() {
        return addedAbstractComplexConceptCnt;
    }

    public int getAverageAbstracComplexConceptGroupSiblingConceptCnt() {
        return averageAbstracComplexConceptGroupSiblingConceptCnt;
    }

    public void addComplexWordConcept(Map matchedWordIDMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTreePanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        conceptTreePanel.addComplexWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode);
        addedAbstractComplexConceptCnt = conceptTreePanel.getAbstractNodeCnt();
        DODDLE.getLogger().log(Level.DEBUG, "追加した抽象中間プロパティ数: " + addedAbstractComplexConceptCnt);
        if (addedAbstractComplexConceptCnt == 0) {
            averageAbstracComplexConceptGroupSiblingConceptCnt = 0;
        } else {
            averageAbstracComplexConceptGroupSiblingConceptCnt = conceptTreePanel
                    .getTotalAbstractNodeGroupSiblingNodeCnt()
                    / addedAbstractComplexConceptCnt;
        }
        DODDLE.getLogger().log(Level.INFO,
                "抽象中間プロパティの平均兄弟プロパティグループ化数: " + averageAbstracComplexConceptGroupSiblingConceptCnt);
    }

    public void init() {
        addedAbstractComplexConceptCnt = 0;
        averageAbstracComplexConceptGroupSiblingConceptCnt = 0;
        treeMaker.init();
        conceptTreePanel.getConceptTree().setModel(new DefaultTreeModel(null));
        conceptDefinitionPanel.init();
    }

    private void removeNounConceptPath(Set<List<Concept>> pathSet) {
        Set<List<Concept>> removeSet = new HashSet<List<Concept>>();
        for (List<Concept> path : pathSet) {
            Concept c = path.get(2);
            if (!(c.getId().equals("30f801") || c.getId().equals("30f83e"))) {
                removeSet.add(path);
                // System.out.println("path: " + path);
            }
        }
        pathSet.removeAll(removeSet);
    }

    private int beforeTrimmingConceptNum;

    public int getBeforeTrimmingConceptNum() {
        return beforeTrimmingConceptNum;
    }

    private int addedSINNum;

    public int getAddedSINNum() {
        return addedSINNum;
    }

    private int trimmedConceptNum;

    public int getTrimmedConceptNum() {
        return trimmedConceptNum;
    }

    int afterTrimmingConceptNum;

    public int getAfterTrimmingConceptNum() {
        return afterTrimmingConceptNum;
    }

    public TreeModel getTreeModel(Set nounIDSet, Set<Concept> verbConceptSet) {
        Set<List<Concept>> pathSet = treeMaker.getPathList(verbConceptSet);
        removeNounConceptPath(pathSet); // 移動と行為以外のパスを削除する
        trimmedConceptNum = 0;
        TreeModel propertyTreeModel = treeMaker.getTrimmedTreeModel(pathSet, project);
        trimmedConceptNum = treeMaker.getTrimmedConceptNum();
        beforeTrimmingConceptNum = treeMaker.getBeforeTrimmingConceptNum();
        if (beforeTrimmingConceptNum != 1) {
            // 名詞的概念を削除する分．
            // beforeTrimmingConceptNumが１の場合は，名詞的概念は削除されない．
            trimmedConceptNum += 1;
        }
        addedSINNum = beforeTrimmingConceptNum - verbConceptSet.size();
        DODDLE.getLogger().log(Level.INFO, "プロパティ階層構築における追加SIN数: " + addedSINNum);
        DODDLE.getLogger().log(Level.INFO, "剪定前プロパティ数: " + beforeTrimmingConceptNum);
        DODDLE.getLogger().log(Level.INFO, "剪定プロパティ数: " + trimmedConceptNum);
        ConceptTreeNode rootNode = (ConceptTreeNode) propertyTreeModel.getRoot();
        VerbConcept propRoot = new VerbConcept(ConceptTreeMaker.DODDLE_PROPERTY_ROOT_ID, "動詞的概念");
        propRoot.addEnWord("Root Property");
        propRoot.setPrefix(DODDLE.BASE_PREFIX);
        rootNode.setConcept(propRoot);
        rootNode.setIsUserConcept(true);
        setRegion(propertyTreeModel, nounIDSet);
        setConceptDriftManagementResult();
        conceptTreePanel.checkAllMultipleInheritanceNode(propertyTreeModel);
        return propertyTreeModel;
    }

    public void setConceptDriftManagementResult() {
        controlPanel.setConceptDriftManagementResult();
    }

    public void setTreeModel(TreeModel model) {
        conceptTreePanel.getConceptTree().setModel(model);
    }

    public void checkMultipleInheritance(TreeModel model) {
        conceptTreePanel.checkAllMultipleInheritanceNode(model);
    }

    public double getChildCntAverage() {
        return Utils.getChildCntAverage(conceptTreePanel.getConceptTree().getModel());
    }

    public int getAllConceptCnt() {
        return Utils.getAllConcept(conceptTreePanel.getConceptTree().getModel()).size();
    }

    public Set getAllConceptID() {
        return conceptTreePanel.getAllConceptID();
    }

    /**
     * 
     * 名詞的な概念を削除
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
            if (childNode.getConcept().getId().equals("30f7e4")) { // 事象
                eventNode = childNode;
                // 事象ノードの子ノードは，すべて動詞的概念のルート概念の下位概念に移動
                for (int j = 0; j < eventNode.getChildCount(); j++) {
                    ConceptTreeNode eventChildNode = (ConceptTreeNode) eventNode.getChildAt(j);
                    treeModel.removeNodeFromParent(eventChildNode);
                    treeModel.insertNodeInto(eventChildNode, rootNode, 0);
                }
            }
        }
        if (eventNode != null) {
            treeModel.removeNodeFromParent(eventNode); // これより前で消すと事象の子要素がすべて削除されてしまう
        }
        DODDLE.getLogger().log(Level.INFO, "動詞的概念階層から名詞的概念階層を削除");
        afterTrimmingConceptNum = Utils.getAllConcept(treeModel).size();
        DODDLE.getLogger().log(Level.INFO, "剪定後プロパティ数: " + afterTrimmingConceptNum);
    }

    /**
     * 
     * クラス階層に存在しない概念をdomain, rangeから削除
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
                    // System.out.println(InputModule.getEDRConcept(id) + " → "
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
                Set<String> domainSet = conceptDefinition.getIDSet("agent", c.getId(), childNode
                        .getTrimmedConceptList());
                // System.out.println("verb concept: " + c);
                // System.out.println("domain: " + domainSet);
                domainSet = abstractRegion(domainSet, nounIDSet);
                // System.out.println("abstract domain: " + domainSet);
                Set<String> rangeSet = conceptDefinition.getIDSet("object", c.getId(), childNode
                        .getTrimmedConceptList());
                // System.out.println("range: " + rangeSet);
                rangeSet = abstractRegion(rangeSet, nounIDSet);
                // System.out.println("abstract range: " + rangeSet);

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
