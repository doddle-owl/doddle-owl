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
import io.github.doddle_owl.models.concept_selection.Concept;
import io.github.doddle_owl.models.concept_hierarchy.ConceptTreeCellRenderer;
import io.github.doddle_owl.models.concept_hierarchy.ConceptTreeNode;
import io.github.doddle_owl.utils.ConceptTreeMaker;
import io.github.doddle_owl.utils.Translator;
import io.github.doddle_owl.utils.Utils;
import io.github.doddle_owl.views.DODDLEProjectPanel;
import io.github.doddle_owl.views.common.UnmatchedTermListPanel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Takeshi Morita
 */
public class ClassHierarchyConstructionPanel extends ConceptHierarchyConstructionPanel {

    @Override
    public void initialize() {
        super.initialize();
    }

    public ClassHierarchyConstructionPanel(DODDLEProjectPanel p) {
        project = p;
        unmatchedTermListPanel = new UnmatchedTermListPanel();
        isaHierarchyPanel = new ConceptHierarcyPanel(Translator.getTerm("IsaHierarchyBorder"), ConceptHierarcyPanel.ISA_CLASS_HIERARCHY,
                unmatchedTermListPanel, p);
        hasaHierarchyPanel = new ConceptHierarcyPanel(Translator.getTerm("HasaHierarchyBorder"),
                ConceptHierarcyPanel.HASA_CLASS_HIERARCHY, unmatchedTermListPanel, project);
        isaHierarchyPanel.setHasaTree(hasaHierarchyPanel.getConceptTree());

        var treeTabbedPane = new JTabbedPane();
        treeTabbedPane.addTab(Translator.getTerm("IsaConceptHierarchyPanel"), null, isaHierarchyPanel);
        treeTabbedPane.addTab(Translator.getTerm("HasaConceptHierarchyPanel"), null, hasaHierarchyPanel);

        conceptDriftManagementPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE,
                isaHierarchyPanel.getConceptTree(), project);
        isaHierarchyPanel.setConceptDriftManagementPanel(conceptDriftManagementPanel);

        conceptInfoPanel = new ConceptInformationPanel(isaHierarchyPanel.getConceptTree(), hasaHierarchyPanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE), conceptDriftManagementPanel);

        var leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setBorder(Utils.createEmptyBorder());
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.add(unmatchedTermListPanel);
        leftSplitPane.add(treeTabbedPane);

        var rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setBorder(Utils.createEmptyBorder());
        rightSplitPane.setOneTouchExpandable(true);
        rightSplitPane.add(conceptInfoPanel);
        rightSplitPane.add(conceptDriftManagementPanel);
        rightSplitPane.setDividerLocation(0.6);

        var mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.add(leftSplitPane);
        mainSplitPane.add(rightSplitPane);
        mainSplitPane.setDividerLocation(0.4);

        setLayout(new BorderLayout());
        add(mainSplitPane, BorderLayout.CENTER);
    }

    public TreeModel getTreeModel(Set<Concept> conceptSet) {
        Set<List<Concept>> pathSet = treeMaker.getPathListSet(conceptSet);
        trimmedConceptNum = 0;
        TreeModel model = treeMaker.getTrimmedTreeModel(pathSet, project, ConceptTreeMaker.DODDLE_CLASS_ROOT_URI);
        trimmedConceptNum = treeMaker.getTrimmedConceptNum();
        beforeTrimmingConceptNum = treeMaker.getBeforeTrimmingConceptNum();
        addedSINNum = beforeTrimmingConceptNum - conceptSet.size();

        DODDLE_OWL.getLogger().info(Translator.getTerm("ClassSINCountMessage") + ": " + addedSINNum);
        DODDLE_OWL.getLogger().info(Translator.getTerm("BeforeTrimmingClassCountMessage") + ": " + beforeTrimmingConceptNum);
        DODDLE_OWL.getLogger().info(Translator.getTerm("TrimmedClassCountMessage") + ": " + trimmedConceptNum);
        DODDLE_OWL.getLogger().info(Translator.getTerm("AfterTrimmingClassCountMessage") + ": " + getAfterTrimmingConceptNum());
        isaHierarchyPanel.checkAllMultipleInheritanceNode(model);
        return model;
    }

    public void addCompoundWordConcept(Map matchedWordIDMap, Map abstractNodeLabelMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) isaHierarchyPanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        isaHierarchyPanel.addCompoundWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode, abstractNodeLabelMap);
        //DODDLE.getLogger().info("追加した抽象中間ノード数: " + isaTreePanel.getAbstractNodeCnt());
        addedAbstractCompoundConceptCnt = isaHierarchyPanel.getAbstractConceptCnt();
        DODDLE_OWL.getLogger().info(Translator.getTerm("AbstractInternalClassCountMessage") + ": " + addedAbstractCompoundConceptCnt);

        if (addedAbstractCompoundConceptCnt == 0) {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = 0;
        } else {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = isaHierarchyPanel.getTotalAbstractNodeGroupSiblingNodeCnt()
                    / addedAbstractCompoundConceptCnt;
        }
        DODDLE_OWL.getLogger().info(Translator.getTerm("AverageAbstractSiblingConceptCountInClassesMessage") + ": " + averageAbstracCompoundConceptGroupSiblingConceptCnt);
    }

}