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

import com.hp.hpl.jena.rdf.model.*;

/*
 * @(#)  2005/07/17
 *
 */

/**
 * @author takeshi morita
 */
public class ConstructPropertyPanel extends ConstructConceptTreePanel {

    private int afterTrimmingConceptCnt;
    private EDRConceptDefinitionPanel edrConceptDefinitionPanel;

    public void clearPanel() {
        super.clearPanel();
        edrConceptDefinitionPanel.init();
    }

    public int getAfterTrimmingConceptCnt() {
        return afterTrimmingConceptCnt;
    }

    public boolean isConceptContains(Concept c) {
        return conceptTreePanel.isConceptContains(c);
    }

    public ConstructPropertyPanel(DODDLEProject p) {
        project = p;
        undefinedWordListPanel = new UndefinedWordListPanel();
        conceptTreePanel = new ConceptTreePanel(Translator.getString("PropertyTreePanel.ConceptTree"),
                undefinedWordListPanel, project);
        edrConceptDefinitionPanel = new EDRConceptDefinitionPanel(p);
        conceptDriftManagementPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.VERB_CONCEPT_TREE,
                conceptTreePanel.getConceptTree(), project);
        conceptTreePanel.setConceptDriftManagementPanel(conceptDriftManagementPanel);
        JTabbedPane tab = new JTabbedPane();
        tab.add(edrConceptDefinitionPanel, Translator.getString("PropertyTreePanel.ConceptDefinition"));
        tab.add(conceptDriftManagementPanel, Translator.getString("ConceptTreePanel.ConceptDriftManagement"));

        conceptInfoPanel = new ConceptInformationPanel(conceptTreePanel.getConceptTree(), new ConceptTreeCellRenderer(
                ConceptTreeCellRenderer.VERB_CONCEPT_TREE), edrConceptDefinitionPanel, conceptDriftManagementPanel);
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

    public void addComplexWordConcept(Map matchedWordIDMap, Map abstractNodeLabelMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTreePanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        conceptTreePanel.addComplexWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode, abstractNodeLabelMap);
        DODDLE.getLogger().log(Level.INFO, "追加した抽象中間ノード数: " + conceptTreePanel.getAbstractNodeCnt());
        addedAbstractComplexConceptCnt = conceptTreePanel.getAbstractConceptCnt();
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
        edrConceptDefinitionPanel.init();
    }

    public TreeModel getTreeModel(Set<String> nounURISet, Set<Concept> verbConceptSet, String type) {
        Set<List<Concept>> pathSet = treeMaker.getPathList(verbConceptSet);
        trimmedConceptNum = 0;
        TreeModel propertyTreeModel = treeMaker.getTrimmedTreeModel(pathSet, project, type);
        trimmedConceptNum = treeMaker.getTrimmedConceptNum();
        beforeTrimmingConceptNum = treeMaker.getBeforeTrimmingConceptNum();
        if (beforeTrimmingConceptNum != 1) {
            // 削除した名詞的概念数
            // beforeTrimmingConceptNumが１の場合は，名詞的概念は削除されない．
            trimmedConceptNum += 1;
        }
        addedSINNum = beforeTrimmingConceptNum - verbConceptSet.size();
        DODDLE.getLogger().log(Level.INFO, "プロパティ階層構築における追加SIN数: " + addedSINNum);
        DODDLE.getLogger().log(Level.INFO, "剪定前プロパティ数: " + beforeTrimmingConceptNum);
        DODDLE.getLogger().log(Level.INFO, "剪定プロパティ数: " + trimmedConceptNum);

        setRegion(propertyTreeModel, nounURISet);
        conceptTreePanel.checkAllMultipleInheritanceNode(propertyTreeModel);
        treeMaker.conceptDriftManagement(propertyTreeModel);
        setConceptDriftManagementResult();
        return propertyTreeModel;
    }

    /**
     * EDR概念ついて 獲得した定義域または値域を洗練する
     * 
     * １．名詞的概念階層に含まれる定義域，値域の値はそのまま利用する
     * ２．名詞的概念階層に含まれていない定義域，値域の値は対象概念の下位概念が存在すればその概念と置換
     * 
     * @param regionSet
     * @param nounURISet
     * @return
     */
    private Set<String> refineEDRRegion(Set<String> regionSet, Set<String> nounURISet) {
        Set<String> refineRegionSet = new HashSet<String>();
        for (String uri : regionSet) {
            if (nounURISet.contains(uri)) {
                refineRegionSet.add(uri);
            } else if (!Utils.getNameSpace(ResourceFactory.createResource(uri)).equals(DODDLE.EDR_URI)) { // EDR以外の定義域，値域はrefineOWLRegion()で洗練する
                refineRegionSet.add(uri);
            } else {
                Set<String> nounIDSet = new HashSet<String>();
                for (String luri : nounURISet) {
                    nounIDSet.add(Utils.getLocalName(ResourceFactory.createResource(luri)));
                }
                String id = Utils.getLocalName(ResourceFactory.createResource(uri));
                Set<String> subIDSet = new HashSet<String>();
                EDRTree.getEDRTree().getSubIDSet(id, nounIDSet, subIDSet);
                if (0 < subIDSet.size()) {
                    DODDLE.getLogger().log(Level.DEBUG,
                            "Refine Region: " + DODDLEDic.getConcept(uri) + " =>  (" + subIDSet.size() + ") ");
                    for (String subID : subIDSet) {
                        String refineURI = DODDLE.EDR_URI + subID;
                        DODDLE.getLogger().log(Level.DEBUG, DODDLEDic.getConcept(refineURI));
                        refineRegionSet.add(refineURI);
                    }
                }
            }
        }
        return refineRegionSet;
    }

    /**
     * 
     * プロパティの定義域と値域をEDR概念記述辞書，OWLオントロジーのプロパティを 参照して定義する
     * 
     * @param node
     * @param conceptDefinition
     * @param nounURISet
     */
    private void setRegion(TreeNode node, ConceptDefinition conceptDefinition, Set<String> nounURISet) {
        if (node.getChildCount() == 0) { return; }
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.getConcept() instanceof VerbConcept) {
                VerbConcept c = (VerbConcept) childNode.getConcept();
                Set<String> domainSet = new HashSet<String>();
                if (project.getOntologySelectionPanel().isEDREnable()) {
                    domainSet.addAll(conceptDefinition.getURISet("agent", c.getLocalName(), childNode
                            .getTrimmedConceptList()));
                    domainSet = refineEDRRegion(domainSet, nounURISet);
                }
                domainSet.addAll(OWLOntologyManager.getDomainSet(c, childNode.getTrimmedConceptList()));
                domainSet = refineOWLOntologyRegion(domainSet, nounURISet);

                Set<String> rangeSet = new HashSet<String>();
                if (project.getOntologySelectionPanel().isEDREnable()) {
                    rangeSet.addAll(conceptDefinition.getURISet("object", c.getLocalName(), childNode
                            .getTrimmedConceptList()));
                    rangeSet = refineEDRRegion(rangeSet, nounURISet);
                }
                rangeSet.addAll(OWLOntologyManager.getRangeSet(c, childNode.getTrimmedConceptList()));
                rangeSet = refineOWLOntologyRegion(rangeSet, nounURISet);

                c.addAllDomain(domainSet);
                c.addAllRange(rangeSet);
                childNode.setConcept(c);
            }
            setRegion(childNode, conceptDefinition, nounURISet);
        }
    }

    /**
     * OWLオントロジー中の概念ついて 獲得した定義域または値域を洗練する
     * 
     * １．名詞的概念階層に含まれる定義域，値域の値はそのまま利用する
     * ２．名詞的概念階層に含まれていない定義域，値域の値は対象概念の下位概念が存在すればその概念と置換(複数あればすべてを採用する)
     * 
     * @param regionSet
     * @param nounURISet
     * @return
     */
    private Set<String> refineOWLOntologyRegion(Set<String> regionSet, Set<String> nounURISet) {
        Set<String> refineRegionSet = new HashSet<String>();
        for (String uri : regionSet) {
            if (nounURISet.contains(uri)) {
                refineRegionSet.add(uri);
            } else if (project.getOntologySelectionPanel().isEDREnable()
                    && Utils.getNameSpace(ResourceFactory.createResource(uri)).equals(DODDLE.EDR_URI)) { // EDRを参照している場合は，EDR概念ついてには洗練済とする
                refineRegionSet.add(uri);
            } else {
                Set<String> refineURISet = OWLOntologyManager.getSubURISet(uri, nounURISet);
                DODDLE.getLogger().log(Level.DEBUG,
                        "Refine Region: " + DODDLEDic.getConcept(uri) + " => " + refineURISet);
                for (String refineURI : refineURISet) {
                    refineRegionSet.add(refineURI);
                }
            }
        }
        return refineRegionSet;
    }

    private void setRegion(TreeModel propertyTreeModel, Set<String> nounURISet) {
        TreeNode rootNode = (TreeNode) propertyTreeModel.getRoot();
        setRegion(rootNode, ConceptDefinition.getInstance(), nounURISet);
    }
}
