/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: https://doddle-owl.github.io/
 *
 * Copyright (C) 2004-2026 Takeshi Morita. All rights reserved.
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

package io.github.doddle_owl.views.concept_hierarchy;

import io.github.doddle_owl.DODDLE_OWL;
import io.github.doddle_owl.models.common.DODDLEConstants;
import io.github.doddle_owl.models.concept_selection.Concept;
import io.github.doddle_owl.models.concept_hierarchy.ConceptTreeCellRenderer;
import io.github.doddle_owl.models.concept_hierarchy.ConceptTreeNode;
import io.github.doddle_owl.models.concept_hierarchy.VerbConcept;
import io.github.doddle_owl.models.ontology_api.EDR;
import io.github.doddle_owl.models.ontology_api.EDRTree;
import io.github.doddle_owl.utils.WebOntologyManager;
import io.github.doddle_owl.utils.Translator;
import io.github.doddle_owl.utils.Utils;
import io.github.doddle_owl.views.DODDLEProjectPanel;
import io.github.doddle_owl.views.common.UnmatchedTermListPanel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Takeshi Morita
 */
public class PropertyHierarchyConstructionPanel extends ConceptHierarchyConstructionPanel {

    private final EDRConceptDefinitionPanel edrConceptDefinitionPanel;

    public void clearPanel() {
        super.clearPanel();
        edrConceptDefinitionPanel.init();
    }

    public void initialize() {
        super.initialize();
        treeMaker.init();
        edrConceptDefinitionPanel.init();
    }

    public PropertyHierarchyConstructionPanel(DODDLEProjectPanel p) {
        project = p;
        unmatchedTermListPanel = new UnmatchedTermListPanel();
        isaHierarchyPanel = new ConceptHierarcyPanel(Translator.getTerm("IsaHierarchyBorder"), ConceptHierarcyPanel.ISA_PROPERTY_HIERARCHY,
                unmatchedTermListPanel, project);
        hasaHierarchyPanel = new ConceptHierarcyPanel(Translator.getTerm("HasaHierarchyBorder"), ConceptHierarcyPanel.HASA_PROPERTY_HIERARCHY,
                unmatchedTermListPanel, p);
        isaHierarchyPanel.setHasaTree(hasaHierarchyPanel.getConceptTree());
        edrConceptDefinitionPanel = new EDRConceptDefinitionPanel(p);
        conceptDriftManagementPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.VERB_CONCEPT_TREE,
                isaHierarchyPanel.getConceptTree(), project);
        isaHierarchyPanel.setConceptDriftManagementPanel(conceptDriftManagementPanel);
        conceptInfoPanel = new ConceptInformationPanel(isaHierarchyPanel.getConceptTree(), hasaHierarchyPanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.VERB_CONCEPT_TREE), edrConceptDefinitionPanel,
                conceptDriftManagementPanel);

        var treeTabbedPane = new JTabbedPane();
        treeTabbedPane.addTab(Translator.getTerm("IsaConceptHierarchyPanel"), null, isaHierarchyPanel);
        treeTabbedPane.addTab(Translator.getTerm("HasaConceptHierarchyPanel"), null, hasaHierarchyPanel);

        var leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setBorder(Utils.createEmptyBorder());
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.add(unmatchedTermListPanel);
        leftSplitPane.add(treeTabbedPane);

        var southTabbedPane = new JTabbedPane();
        southTabbedPane.addTab(Translator.getTerm("ConceptDefinitionPanel"), null, edrConceptDefinitionPanel);
        southTabbedPane.addTab(Translator.getTerm("ConceptDriftManagementPanel"), null, conceptDriftManagementPanel);

        var rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setBorder(Utils.createEmptyBorder());
        rightSplitPane.setOneTouchExpandable(true);
        rightSplitPane.add(conceptInfoPanel);
        rightSplitPane.add(southTabbedPane);
        rightSplitPane.setDividerLocation(0.6);

        var mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.add(leftSplitPane);
        mainSplitPane.add(rightSplitPane);
        mainSplitPane.setDividerLocation(0.4);

        setLayout(new BorderLayout());
        add(mainSplitPane, BorderLayout.CENTER);
    }

    public void addCompoundWordConcept(Map matchedWordIDMap, Map abstractNodeLabelMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) isaHierarchyPanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        isaHierarchyPanel.addCompoundWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode, abstractNodeLabelMap);
        // DODDLE.getLogger().info("追加した抽象中間ノード数: " +
        // isaTreePanel.getAbstractNodeCnt());
        addedAbstractCompoundConceptCnt = isaHierarchyPanel.getAbstractConceptCnt();
        DODDLE_OWL.getLogger().info(Translator.getTerm("AbstractInternalPropertyCountMessage") + ": " + addedAbstractCompoundConceptCnt);
        if (addedAbstractCompoundConceptCnt == 0) {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = 0;
        } else {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = isaHierarchyPanel
                    .getTotalAbstractNodeGroupSiblingNodeCnt()
                    / addedAbstractCompoundConceptCnt;
        }
        DODDLE_OWL.getLogger().info(Translator.getTerm("AverageAbstractSiblingConceptCountInPropertiesMessage") + ": "
                + averageAbstracCompoundConceptGroupSiblingConceptCnt);
    }


    public TreeModel getTreeModel(Set<String> nounURISet, Set<Concept> verbConceptSet, String type) {
        Set<List<Concept>> pathSet = treeMaker.getPathListSet(verbConceptSet);
        trimmedConceptNum = 0;
        TreeModel propertyHierarchyModel = treeMaker.getTrimmedTreeModel(pathSet, project, type);
        trimmedConceptNum = treeMaker.getTrimmedConceptNum();
        beforeTrimmingConceptNum = treeMaker.getBeforeTrimmingConceptNum();
        if (beforeTrimmingConceptNum != 1) {
            // 削除した名詞的概念数
            // beforeTrimmingConceptNumが１の場合は，名詞的概念は削除されない．
            trimmedConceptNum += 1;
        }
        addedSINNum = beforeTrimmingConceptNum - verbConceptSet.size();
        DODDLE_OWL.getLogger().info(Translator.getTerm("PropertySINCountMessage") + ": " + addedSINNum);
        DODDLE_OWL.getLogger().info(Translator.getTerm("BeforeTrimmingPropertyCountMessage") + ": " + beforeTrimmingConceptNum);
        DODDLE_OWL.getLogger().info(Translator.getTerm("TrimmedPropertyCountMessage") + ": " + trimmedConceptNum);

        setRegion(propertyHierarchyModel, nounURISet);
        isaHierarchyPanel.checkAllMultipleInheritanceNode(propertyHierarchyModel);
        return propertyHierarchyModel;
    }

    /**
     * EDR概念ついて 獲得した定義域または値域を洗練する
     * <p>
     * １．名詞的概念階層に含まれる定義域，値域の値はそのまま利用する
     * ２．名詞的概念階層に含まれていない定義域，値域の値は対象概念の下位概念が存在すればその概念と置換
     *
     * @param regionSet
     * @param nounURISet
     * @return
     */
    private Set<String> refineRegion(Set<String> regionSet, Set<String> nounURISet) {
        Set<String> refineRegionSet = new HashSet<>();
        for (String uri : regionSet) {
            if (nounURISet.contains(uri)) {
                refineRegionSet.add(uri);
            } else if (Utils.getNamespace(uri).equals(DODDLEConstants.EDR_URI)) { // EDRに関する定義域，値域
                if (project.getOntologySelectionPanel().isEDREnable()) {
                    Set<String> subURISet = new HashSet<>();
                    EDRTree.getEDRTree().getSubURISet(uri, nounURISet, subURISet);
                    if (0 < subURISet.size()) {
                        refineRegionSet.addAll(subURISet);
                    }
                }
            } else { // EDR以外の定義域，値域
                Set<String> refineURISet = WebOntologyManager.getSubURISet(uri, nounURISet);
                refineRegionSet.addAll(refineURISet);
            }
        }
        return refineRegionSet;
    }

    /**
     * プロパティの定義域と値域をEDR概念記述辞書，OWLオントロジーのプロパティを 参照して定義する
     *
     * @param node
     * @param nounURISet
     */
    private void setRegion(TreeNode node, Set<String> nounURISet) {
        if (node.getChildCount() == 0) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.getConcept() instanceof VerbConcept) {
                VerbConcept c = (VerbConcept) childNode.getConcept();
                setDomainSet(c, childNode, nounURISet);
                setRangeSet(c, childNode, nounURISet);
                childNode.setConcept(c);
            }
            setRegion(childNode, nounURISet);
        }
    }

    private void setDomainSet(VerbConcept c, ConceptTreeNode childNode, Set<String> nounURISet) {
        String vid = c.getLocalName();
        Set<String> domainSet = new HashSet<>(WebOntologyManager.getDomainSet(c, childNode.getTrimmedConceptList()));
        if (project.getOntologySelectionPanel().isEDREnable()) {
            domainSet.addAll(EDR.getRelationValueSet("agent", vid, childNode.getTrimmedConceptList()));
        }
        c.addAllDomain(refineRegion(domainSet, nounURISet));
    }

    private void setRangeSet(VerbConcept c, ConceptTreeNode childNode, Set<String> nounURISet) {
        String vid = c.getLocalName();
        Set<String> rangeSet = new HashSet<>(WebOntologyManager.getRangeSet(c, childNode.getTrimmedConceptList()));
        if (project.getOntologySelectionPanel().isEDREnable()) {
            rangeSet.addAll(EDR.getRelationValueSet("object", vid, childNode.getTrimmedConceptList()));
        }
        c.addAllRange(refineRegion(rangeSet, nounURISet));
    }

    private void setRegion(TreeModel propertyHierarchyModel, Set<String> nounURISet) {
        TreeNode rootNode = (TreeNode) propertyHierarchyModel.getRoot();
        setRegion(rootNode, nounURISet);
    }
}
