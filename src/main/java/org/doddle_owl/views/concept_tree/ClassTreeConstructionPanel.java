/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 *
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.doddle_owl.views.concept_tree;


import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.concept_selection.Concept;
import org.doddle_owl.models.concept_tree.ConceptTreeCellRenderer;
import org.doddle_owl.models.concept_tree.ConceptTreeNode;
import org.doddle_owl.utils.ConceptTreeMaker;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.views.DODDLEProjectPanel;
import org.doddle_owl.views.common.UndefinedTermListPanel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Takeshi Morita
 */
public class ClassTreeConstructionPanel extends ConceptTreeConstructionPanel {

    @Override
    public void initialize() {
        super.initialize();
    }

    public ClassTreeConstructionPanel(DODDLEProjectPanel p) {
        project = p;
        undefinedTermListPanel = new UndefinedTermListPanel();
        isaTreePanel = new ConceptTreePanel(Translator.getTerm("IsaTreeBorder"), ConceptTreePanel.CLASS_ISA_TREE,
                undefinedTermListPanel, p);
        hasaTreePanel = new ConceptTreePanel(Translator.getTerm("HasaTreeBorder"),
                ConceptTreePanel.CLASS_HASA_TREE, undefinedTermListPanel, project);
        isaTreePanel.setHasaTree(hasaTreePanel.getConceptTree());

        var treeTabbedPane = new JTabbedPane();
        treeTabbedPane.addTab(Translator.getTerm("IsaConceptTreePanel"), null, isaTreePanel);
        treeTabbedPane.addTab(Translator.getTerm("HasaConceptTreePanel"), null, hasaTreePanel);

        conceptDriftManagementPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE,
                isaTreePanel.getConceptTree(), project);
        isaTreePanel.setConceptDriftManagementPanel(conceptDriftManagementPanel);

        conceptInfoPanel = new ConceptInformationPanel(isaTreePanel.getConceptTree(), hasaTreePanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE), conceptDriftManagementPanel);

        var leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setOneTouchExpandable(true);
        leftSplitPane.add(undefinedTermListPanel);
        leftSplitPane.add(treeTabbedPane);

        var rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
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

        DODDLE_OWL.getLogger().log(Level.INFO, Translator.getTerm("ClassSINCountMessage") + ": " + addedSINNum);
        DODDLE_OWL.getLogger().log(Level.INFO, Translator.getTerm("BeforeTrimmingClassCountMessage") + ": " + beforeTrimmingConceptNum);
        DODDLE_OWL.getLogger().log(Level.INFO, Translator.getTerm("TrimmedClassCountMessage") + ": " + trimmedConceptNum);
        DODDLE_OWL.getLogger().log(Level.INFO, Translator.getTerm("AfterTrimmingClassCountMessage") + ": " + getAfterTrimmingConceptNum());
        isaTreePanel.checkAllMultipleInheritanceNode(model);
        return model;
    }

    public void addCompoundWordConcept(Map matchedWordIDMap, Map abstractNodeLabelMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) isaTreePanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        isaTreePanel.addCompoundWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode, abstractNodeLabelMap);
        //DODDLE.getLogger().log(Level.INFO, "追加した抽象中間ノード数: " + isaTreePanel.getAbstractNodeCnt());
        addedAbstractCompoundConceptCnt = isaTreePanel.getAbstractConceptCnt();
        DODDLE_OWL.getLogger().log(Level.INFO, Translator.getTerm("AbstractInternalClassCountMessage") + ": " + addedAbstractCompoundConceptCnt);

        if (addedAbstractCompoundConceptCnt == 0) {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = 0;
        } else {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = isaTreePanel.getTotalAbstractNodeGroupSiblingNodeCnt()
                    / addedAbstractCompoundConceptCnt;
        }
        DODDLE_OWL.getLogger().log(Level.INFO,
                Translator.getTerm("AverageAbstractSiblingConceptCountInClassesMessage") + ": " + averageAbstracCompoundConceptGroupSiblingConceptCnt);
    }

}