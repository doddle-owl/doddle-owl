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

package org.doddle_owl.views;


import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.Concept;
import org.doddle_owl.models.ConceptTreeCellRenderer;
import org.doddle_owl.models.ConceptTreeNode;
import org.doddle_owl.utils.ConceptTreeMaker;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;

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
public class ConstructClassPanel extends ConstructConceptTreePanel {

    @Override
    public void initialize() {
        super.initialize();
    }

    public ConstructClassPanel(DODDLEProjectPanel p) {
        project = p;
        undefinedTermListPanel = new UndefinedTermListPanel();
        isaTreePanel = new ConceptTreePanel(Translator.getTerm("IsaTreeBorder"), ConceptTreePanel.CLASS_ISA_TREE,
                undefinedTermListPanel, p);
        hasaTreePanel = new ConceptTreePanel(Translator.getTerm("HasaTreeBorder"),
                ConceptTreePanel.CLASS_HASA_TREE, undefinedTermListPanel, project);
        isaTreePanel.setHasaTree(hasaTreePanel.getConceptTree());
        conceptDriftManagementPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE,
                isaTreePanel.getConceptTree(), project);
        isaTreePanel.setConceptDriftManagementPanel(conceptDriftManagementPanel);

        conceptInfoPanel = new ConceptInformationPanel(isaTreePanel.getConceptTree(), hasaTreePanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.NOUN_CONCEPT_TREE), conceptDriftManagementPanel);

        mainViews = new View[5];
        ViewMap viewMap = new ViewMap();
        mainViews[0] = new View(Translator.getTerm("UndefinedTermListPanel"), null, undefinedTermListPanel);
        mainViews[1] = new View(Translator.getTerm("IsaConceptTreePanel"), null, isaTreePanel);
        mainViews[2] = new View(Translator.getTerm("HasaConceptTreePanel"), null, hasaTreePanel);
        mainViews[3] = new View(Translator.getTerm("ConceptInformationPanel"), null, conceptInfoPanel);
        mainViews[4] = new View(Translator.getTerm("ConceptDriftManagementPanel"), null, conceptDriftManagementPanel);

        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        rootWindow = Utils.createDODDLERootWindow(viewMap);
        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
    }

    public void setXGALayout() {
        conceptDriftManagementPanel.setXGALayout();
        TabWindow t1 = new TabWindow(new DockingWindow[]{mainViews[1], mainViews[2]});
        SplitWindow sw1 = new SplitWindow(false, 0.3f, mainViews[0], t1);
        SplitWindow sw2 = new SplitWindow(false, 0.5f, mainViews[3], mainViews[4]);
        SplitWindow sw3 = new SplitWindow(true, 0.3f, sw1, sw2);
        rootWindow.setWindow(sw3);
        mainViews[1].restoreFocus();
    }

    public void setUXGALayout() {
        conceptDriftManagementPanel.setUXGALayout();
        TabWindow t1 = new TabWindow(new DockingWindow[]{mainViews[1], mainViews[2]});
        SplitWindow sw1 = new SplitWindow(false, 0.3f, mainViews[0], t1);
        SplitWindow sw2 = new SplitWindow(false, 0.5f, mainViews[3], mainViews[4]);
        SplitWindow sw3 = new SplitWindow(true, 0.3f, sw1, sw2);
        rootWindow.setWindow(sw3);
        mainViews[1].restoreFocus();
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