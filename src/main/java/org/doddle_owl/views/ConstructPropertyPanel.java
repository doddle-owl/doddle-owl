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
import org.apache.log4j.Level;
import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.*;
import org.doddle_owl.utils.OWLOntologyManager;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;

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
        return isaTreePanel.isConceptContains(c);
    }

    public ConstructPropertyPanel(DODDLEProject p) {
        project = p;
        undefinedTermListPanel = new UndefinedTermListPanel();
        isaTreePanel = new ConceptTreePanel(Translator.getTerm("IsaTreeBorder"), ConceptTreePanel.PROPERTY_ISA_TREE,
                undefinedTermListPanel, project);
        hasaTreePanel = new ConceptTreePanel(Translator.getTerm("HasaTreeBorder"), ConceptTreePanel.PROPERTY_HASA_TREE,
                undefinedTermListPanel, p);
        isaTreePanel.setHasaTree(hasaTreePanel.getConceptTree());
        edrConceptDefinitionPanel = new EDRConceptDefinitionPanel(p);
        conceptDriftManagementPanel = new ConceptDriftManagementPanel(ConceptTreeCellRenderer.VERB_CONCEPT_TREE,
                isaTreePanel.getConceptTree(), project);
        isaTreePanel.setConceptDriftManagementPanel(conceptDriftManagementPanel);
        conceptInfoPanel = new ConceptInformationPanel(isaTreePanel.getConceptTree(), hasaTreePanel.getConceptTree(),
                new ConceptTreeCellRenderer(ConceptTreeCellRenderer.VERB_CONCEPT_TREE), edrConceptDefinitionPanel,
                conceptDriftManagementPanel);

        mainViews = new View[6];
        ViewMap viewMap = new ViewMap();

        mainViews[0] = new View(Translator.getTerm("UndefinedTermListPanel"), null, undefinedTermListPanel);
        mainViews[1] = new View(Translator.getTerm("IsaConceptTreePanel"), null, isaTreePanel);
        mainViews[2] = new View(Translator.getTerm("HasaConceptTreePanel"), null, hasaTreePanel);
        mainViews[3] = new View(Translator.getTerm("ConceptInformationPanel"), null, conceptInfoPanel);
        mainViews[4] = new View(Translator.getTerm("ConceptDefinitionPanel"), null, edrConceptDefinitionPanel);
        mainViews[5] = new View(Translator.getTerm("ConceptDriftManagementPanel"), null, conceptDriftManagementPanel);

        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        rootWindow = Utils.createDODDLERootWindow(viewMap);
        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
    }

    public void setXGALayout() {
        conceptDriftManagementPanel.setXGALayout();
        TabWindow t1 = new TabWindow(new DockingWindow[] { mainViews[1], mainViews[2]});
        SplitWindow sw1 = new SplitWindow(false, 0.3f, mainViews[0], t1);
        TabWindow t2 = new TabWindow(new DockingWindow[] { mainViews[4], mainViews[5]});
        SplitWindow sw2 = new SplitWindow(false, 0.5f, mainViews[3], t2);
        SplitWindow sw3 = new SplitWindow(true, 0.3f, sw1, sw2);
        rootWindow.setWindow(sw3);
        mainViews[1].restoreFocus();
        mainViews[4].restoreFocus();
    }

    public void setUXGALayout() {
        conceptDriftManagementPanel.setUXGALayout();
        TabWindow t1 = new TabWindow(new DockingWindow[] { mainViews[1], mainViews[2]});
        SplitWindow sw1 = new SplitWindow(false, 0.3f, mainViews[0], t1);
        SplitWindow sw2 = new SplitWindow(true, mainViews[4], mainViews[5]);
        SplitWindow sw3 = new SplitWindow(false, 0.5f, mainViews[3], sw2);
        SplitWindow sw4 = new SplitWindow(true, 0.3f, sw1, sw3);
        rootWindow.setWindow(sw4);
        mainViews[1].restoreFocus();
        mainViews[4].restoreFocus();
    }

    public void addCompoundWordConcept(Map matchedWordIDMap, Map abstractNodeLabelMap, TreeNode rootNode) {
        DefaultTreeModel model = (DefaultTreeModel) isaTreePanel.getConceptTree().getModel();
        ConceptTreeNode conceptTreeRootNode = (ConceptTreeNode) model.getRoot();
        isaTreePanel.addCompoundWordConcept(matchedWordIDMap, rootNode, conceptTreeRootNode, abstractNodeLabelMap);
        // DODDLE.getLogger().log(Level.INFO, "追加した抽象中間ノード数: " +
        // isaTreePanel.getAbstractNodeCnt());
        addedAbstractCompoundConceptCnt = isaTreePanel.getAbstractConceptCnt();
        DODDLE_OWL.getLogger().log(Level.DEBUG,
                Translator.getTerm("AbstractInternalPropertyCountMessage") + ": " + addedAbstractCompoundConceptCnt);
        if (addedAbstractCompoundConceptCnt == 0) {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = 0;
        } else {
            averageAbstracCompoundConceptGroupSiblingConceptCnt = isaTreePanel
                    .getTotalAbstractNodeGroupSiblingNodeCnt()
                    / addedAbstractCompoundConceptCnt;
        }
        DODDLE_OWL.getLogger().log(
                Level.INFO,
                Translator.getTerm("AverageAbstractSiblingConceptCountInPropertiesMessage") + ": "
                        + averageAbstracCompoundConceptGroupSiblingConceptCnt);
    }

    public void init() {
        addedAbstractCompoundConceptCnt = 0;
        averageAbstracCompoundConceptGroupSiblingConceptCnt = 0;
        treeMaker.init();
        isaTreePanel.getConceptTree().setModel(new DefaultTreeModel(null));
        edrConceptDefinitionPanel.init();
    }

    public TreeModel getTreeModel(Set<String> nounURISet, Set<Concept> verbConceptSet, String type) {
        Set<List<Concept>> pathSet = treeMaker.getPathListSet(verbConceptSet);
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
        DODDLE_OWL.getLogger().log(Level.INFO, Translator.getTerm("PropertySINCountMessage") + ": " + addedSINNum);
        DODDLE_OWL.getLogger().log(Level.INFO,
                Translator.getTerm("BeforeTrimmingPropertyCountMessage") + ": " + beforeTrimmingConceptNum);
        DODDLE_OWL.getLogger()
                .log(Level.INFO, Translator.getTerm("TrimmedPropertyCountMessage") + ": " + trimmedConceptNum);

        setRegion(propertyTreeModel, nounURISet);
        isaTreePanel.checkAllMultipleInheritanceNode(propertyTreeModel);
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
    private Set<String> refineRegion(Set<String> regionSet, Set<String> nounURISet) {
        Set<String> refineRegionSet = new HashSet<>();
        for (String uri : regionSet) {
            if (nounURISet.contains(uri)) {
                refineRegionSet.add(uri);
            } else if (Utils.getNameSpace(uri).equals(DODDLEConstants.EDR_URI)) { // EDRに関する定義域，値域
                if (project.getOntologySelectionPanel().isEDREnable()) {
                    Set<String> subURISet = new HashSet<>();
                    EDRTree.getEDRTree().getSubURISet(uri, nounURISet, subURISet);
                    if (0 < subURISet.size()) {
                        for (String subURI : subURISet) {
                            refineRegionSet.add(subURI);
                        }
                    }
                }
            } else { // EDR以外の定義域，値域
                Set<String> refineURISet = OWLOntologyManager.getSubURISet(uri, nounURISet);
                for (String refineURI : refineURISet) {
                    refineRegionSet.add(refineURI);
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
     * @param nounURISet
     */
    private void setRegion(TreeNode node, Set<String> nounURISet) {
        if (node.getChildCount() == 0) { return; }
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
        Set<String> domainSet = new HashSet<>();
        domainSet.addAll(OWLOntologyManager.getDomainSet(c, childNode.getTrimmedConceptList()));
        if (project.getOntologySelectionPanel().isEDREnable()) {
            domainSet.addAll(EDRDic.getRelationValueSet("agent", vid, childNode.getTrimmedConceptList()));
        }
        c.addAllDomain(refineRegion(domainSet, nounURISet));
    }

    private void setRangeSet(VerbConcept c, ConceptTreeNode childNode, Set<String> nounURISet) {
        String vid = c.getLocalName();
        Set<String> rangeSet = new HashSet<>();
        rangeSet.addAll(OWLOntologyManager.getRangeSet(c, childNode.getTrimmedConceptList()));
        if (project.getOntologySelectionPanel().isEDREnable()) {
            rangeSet.addAll(EDRDic.getRelationValueSet("object", vid, childNode.getTrimmedConceptList()));
        }
        c.addAllRange(refineRegion(rangeSet, nounURISet));
    }

    private void setRegion(TreeModel propertyTreeModel, Set<String> nounURISet) {
        TreeNode rootNode = (TreeNode) propertyTreeModel.getRoot();
        setRegion(rootNode, nounURISet);
    }
}
