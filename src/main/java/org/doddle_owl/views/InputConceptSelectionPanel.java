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

import net.infonode.docking.*;
import net.infonode.docking.util.ViewMap;
import org.apache.jena.rdf.model.ResourceFactory;
import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.actions.ConstructNounAndVerbTreeAction;
import org.doddle_owl.actions.ConstructNounTreeAction;
import org.doddle_owl.actions.ConstructTreeAction;
import org.doddle_owl.models.*;
import org.doddle_owl.utils.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Takeshi Morita
 */
public class InputConceptSelectionPanel extends JPanel implements ListSelectionListener,
        ActionListener, TreeSelectionListener {

    private File inputFile;
    private Set<String> termSet;
    private Set<String> systemAddedTermSet;
    private Set<Concept> inputConceptSet; // 入力概念のセット
    private Set<Concept> systemAddedInputConceptSet; // システムが追加した入力概念のセット
    private Set<Concept> inputNounConceptSet; // 入力名詞的概念のセット
    private Set<Concept> inputVerbConceptSet; // 入力動詞的概念のセット

    private Set<InputTermModel> inputTermModelSet; // 入力単語モデルのセット
    private Map<String, Set<Concept>> termConceptSetMap; // 入力単語と入力単語を見出しとして含む概念のマッピング
    private Map<String, Set<Concept>> termCorrespondConceptSetMap; // 入力単語と適切に対応する概念のマッピング
    private Map<String, Set<EvalConcept>> termEvalConceptSetMap;
    private Map<InputTermModel, ConstructTreeOption> compoundConstructTreeOptionMap;

    private TitledBorder perfectlyMatchedTermJListTitle;
    private TitledBorder partiallyMatchedTermJListTitle;
    private JPanel perfectlyMatchedTermListPanel;
    private JPanel partiallyMatchedTermListPanel;

    private View[] termListViews;
    private RootWindow termListRootWindow;

    private JTextField searchTermField;
    private JButton searchTermButton;

    private JList perfectlyMatchedTermJList; // 完全照合した単語リスト
    private Set<InputTermModel> perfectlyMatchedTermModelSet;
    private JList partiallyMatchedTermJList; // 部分照合した単語リスト
    private Set<InputTermModel> partiallyMatchedTermModelSet;
    private JList conceptSetJList;
    private UndefinedTermListPanel undefinedTermListPanel;

    private JCheckBox perfectlyMatchedAmbiguityCntCheckBox;
    private JCheckBox perfectlyMatchedIsSyncCheckBox;
    private JCheckBox perfectlyMatchedIsSystemAddedTermCheckBox;

    private JCheckBox partiallyMatchedCompoundWordCheckBox;
    private JCheckBox partiallyMatchedMatchedTermBox;
    private JCheckBox partiallyMatchedAmbiguityCntCheckBox;
    private JCheckBox partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox;

    private Concept selectedConcept;
    private LiteralPanel labelPanel;
    private LiteralPanel descriptionPanel;

    private JPanel constructTreeOptionPanel;
    private JPanel partiallyMatchedConstructTreeOptionPanel;
    private JPanel perfectlyMatchedConstructTreeOptionPanel;
    private JPanel systemAddedPerfectlyMatchedConstructTreeOptionPanel;

    private JCheckBox replaceSubClassesCheckBox;

    private JRadioButton addAsSubConceptRadioButton;
    private JRadioButton addAsSameConceptRadioButton;

    private JList highlightPartJList;
    private JEditorPane documentArea;
    // private JTextArea documentArea;
    private JCheckBox highlightInputTermCheckBox;
    private JCheckBox showAroundConceptTreeCheckBox;
    private JTree aroundConceptTree;
    private TreeModel aroundConceptTreeModel;

    private InputModule inputModule;
    private ConstructClassPanel constructClassPanel;
    private ConstructPropertyPanel constructPropertyPanel;

    private JButton constructNounTreeButton;
    private JButton constructNounAndVerbTreeButton;
    private ConstructionTypePanel constructionTypePanel;
    private PerfectlyMatchedOptionPanel perfectlyMatchedOptionPanel;
    private PartiallyMatchedOptionPanel partiallyMatchedOptionPanel;
    // private JButton showConceptDescriptionButton;

    private AutomaticDisAmbiguationAction automaticDisAmbiguationAction;
    private ConstructTreeAction constructNounTreeAction;
    private ConstructTreeAction constructNounAndVerbTreeAction;

    private Action savePerfectlyMatchedTermAction;
    private Action savePerfectlyMatchedTermWithCompoundWordAcion;

    // private ConceptDescriptionFrame conceptDescriptionFrame;

    private InputDocumentSelectionPanel docSelectionPanel;

    private DODDLEProject project;

    private boolean isConstructNounAndVerbTree;

    private View[] mainViews;
    private RootWindow rootWindow;

    public static Concept nullConcept;
    public static EvalConcept nullEvalConcept = new EvalConcept(null, -1);

    public InputConceptSelectionPanel(ConstructClassPanel tp, ConstructPropertyPanel pp,
                                      DODDLEProject p) {
        project = p;
        constructClassPanel = tp;
        constructPropertyPanel = pp;
        inputConceptSet = new HashSet<>();
        systemAddedInputConceptSet = new HashSet<>();
        nullConcept = new Concept("null", Translator.getTerm("NotAvailableLabel"));
        inputModule = new InputModule(project);
        termCorrespondConceptSetMap = new HashMap<>();
        compoundConstructTreeOptionMap = new HashMap<>();

        // conceptDescriptionFrame = new ConceptDescriptionFrame();

        conceptSetJList = new JList();
        conceptSetJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        conceptSetJList.addListSelectionListener(this);
        JScrollPane conceptJListScroll = new JScrollPane(conceptSetJList);

        labelPanel = new LiteralPanel(Translator.getTerm("LanguageLabel"),
                Translator.getTerm("LabelList"), LiteralPanel.LABEL);
        descriptionPanel = new LiteralPanel(Translator.getTerm("LanguageLabel"),
                Translator.getTerm("DescriptionList"), LiteralPanel.DESCRIPTION);

        addAsSameConceptRadioButton = new JRadioButton(
                Translator.getTerm("SameConceptRadioButton"), true);
        addAsSameConceptRadioButton.addActionListener(this);
        addAsSubConceptRadioButton = new JRadioButton(Translator.getTerm("SubConceptRadioButton"));
        addAsSubConceptRadioButton.addActionListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(addAsSameConceptRadioButton);
        group.add(addAsSubConceptRadioButton);
        constructTreeOptionPanel = new JPanel();
        constructTreeOptionPanel.setLayout(new BorderLayout());
        constructTreeOptionPanel.setBorder(BorderFactory.createTitledBorder(Translator
                .getTerm("TreeConstructionOptionBorder")));
        partiallyMatchedConstructTreeOptionPanel = new JPanel();
        partiallyMatchedConstructTreeOptionPanel.setLayout(new GridLayout(1, 2));
        partiallyMatchedConstructTreeOptionPanel.add(addAsSameConceptRadioButton);
        partiallyMatchedConstructTreeOptionPanel.add(addAsSubConceptRadioButton);

        perfectlyMatchedConstructTreeOptionPanel = new JPanel();
        systemAddedPerfectlyMatchedConstructTreeOptionPanel = new JPanel();
        systemAddedPerfectlyMatchedConstructTreeOptionPanel.setLayout(new BorderLayout());
        replaceSubClassesCheckBox = new JCheckBox("下位概念に置換");
        replaceSubClassesCheckBox.addActionListener(this);
        systemAddedPerfectlyMatchedConstructTreeOptionPanel.add(replaceSubClassesCheckBox,
                BorderLayout.CENTER);

        constructTreeOptionPanel.add(partiallyMatchedConstructTreeOptionPanel, BorderLayout.CENTER);

        JPanel labelAndDescriptionPanel = new JPanel();
        labelAndDescriptionPanel.setLayout(new GridLayout(2, 1));
        labelAndDescriptionPanel.add(labelPanel);
        labelAndDescriptionPanel.add(descriptionPanel);

        JPanel conceptInfoPanel = new JPanel();
        conceptInfoPanel.setLayout(new BorderLayout());
        conceptInfoPanel.add(labelAndDescriptionPanel, BorderLayout.CENTER);
        conceptInfoPanel.add(constructTreeOptionPanel, BorderLayout.SOUTH);

        undefinedTermListPanel = new UndefinedTermListPanel();

        highlightPartJList = new JList();
        highlightPartJList.addListSelectionListener(this);
        JScrollPane highlightPartJListScroll = new JScrollPane(highlightPartJList);
        highlightPartJListScroll.setBorder(BorderFactory.createTitledBorder("行番号"));
        highlightPartJListScroll.setPreferredSize(new Dimension(100, 100));
        documentArea = new JEditorPane("text/html", "");
        // documentArea = new JTextArea();
        documentArea.setEditable(false);
        // documentArea.setLineWrap(true);
        JScrollPane documentAreaScroll = new JScrollPane(documentArea);
        highlightInputTermCheckBox = new JCheckBox(
                Translator.getTerm("HighlightInputTermCheckBox"), true);
        highlightInputTermCheckBox.addActionListener(this);

        showAroundConceptTreeCheckBox = new JCheckBox(
                Translator.getTerm("ShowConceptTreeCheckBox"), true);
        showAroundConceptTreeCheckBox.addActionListener(this);

        aroundConceptTreeModel = new DefaultTreeModel(null);
        aroundConceptTree = new JTree(aroundConceptTreeModel);
        aroundConceptTree.addTreeSelectionListener(this);
        aroundConceptTree.setEditable(false);
        aroundConceptTree.setCellRenderer(new AroundTreeCellRenderer());
        JScrollPane aroundConceptTreeScroll = new JScrollPane(aroundConceptTree);

        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());
        treePanel.add(aroundConceptTreeScroll, BorderLayout.CENTER);
        treePanel.add(showAroundConceptTreeCheckBox, BorderLayout.SOUTH);

        JPanel documentPanel = new JPanel();
        documentPanel.setLayout(new BorderLayout());
        documentPanel.add(documentAreaScroll, BorderLayout.CENTER);
        documentPanel.add(highlightInputTermCheckBox, BorderLayout.SOUTH);
        // documentPanel.add(hilightPartJListScroll, BorderLayout.WEST);

        automaticDisAmbiguationAction = new AutomaticDisAmbiguationAction(
                Translator.getTerm("AutomaticInputConceptSelectionAction"));
        // showConceptDescriptionButton = new JButton(new
        // ShowConceptDescriptionAction("概念記述を表示"));

        // JPanel p1 = new JPanel();
        // p1.add(automaticDisAmbiguationButton);
        // p1.add(showConceptDescriptionButton);

        constructionTypePanel = new ConstructionTypePanel();
        perfectlyMatchedOptionPanel = new PerfectlyMatchedOptionPanel();
        partiallyMatchedOptionPanel = new PartiallyMatchedOptionPanel();

        constructNounTreeButton = new JButton(new ConstructNounTreeAction());
        constructNounAndVerbTreeButton = new JButton(new ConstructNounAndVerbTreeAction());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(constructNounTreeButton);
        buttonPanel.add(constructNounAndVerbTreeButton);
        JPanel buttonBorderPanel = new JPanel();
        buttonBorderPanel.setLayout(new BorderLayout());
        buttonBorderPanel.add(buttonPanel, BorderLayout.SOUTH);

        JTabbedPane optionTab = new JTabbedPane();
        optionTab.add(perfectlyMatchedOptionPanel,
                Translator.getTerm("PerfectlyMatchedOptionBorder"));
        optionTab.add(partiallyMatchedOptionPanel,
                Translator.getTerm("PartiallyMatchedOptionBorder"));
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        // optionPanel.add(constructionTypePanel);
        optionPanel.add(optionTab, BorderLayout.CENTER);
        optionPanel.add(buttonBorderPanel, BorderLayout.EAST);

        mainViews = new View[7];
        ViewMap viewMap = new ViewMap();

        mainViews[0] = new View(Translator.getTerm("TermListPanel"), null, getTermListPanel());
        mainViews[1] = new View(Translator.getTerm("ConceptList"), null, conceptJListScroll);
        mainViews[2] = new View(Translator.getTerm("ConceptInformationPanel"), null,
                conceptInfoPanel);
        mainViews[3] = new View(Translator.getTerm("UndefinedTermListPanel"), null,
                undefinedTermListPanel);
        mainViews[4] = new View(Translator.getTerm("ConceptTreePanel"), null, treePanel);
        mainViews[5] = new View(Translator.getTerm("InputDocumentArea"), null, documentPanel);
        mainViews[6] = new View(Translator.getTerm("TreeConstructionOptionPanel"), null,
                optionPanel);
        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        rootWindow = Utils.createDODDLERootWindow(viewMap);
        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
    }

    public void setXGALayout() {
        termListRootWindow.setWindow(new TabWindow(new DockingWindow[]{termListViews[0],
                termListViews[1]}));
        termListViews[0].restoreFocus();

        SplitWindow sw1 = new SplitWindow(true, 0.5f, mainViews[2], mainViews[3]);
        SplitWindow sw2 = new SplitWindow(true, 0.3f, mainViews[1], sw1);
        SplitWindow sw3 = new SplitWindow(true, mainViews[4], mainViews[5]);
        SplitWindow sw4 = new SplitWindow(false, 0.6f, sw2, sw3);
        SplitWindow sw5 = new SplitWindow(true, 0.3f, mainViews[0], sw4);
        SplitWindow sw6 = new SplitWindow(false, 0.8f, sw5, mainViews[6]);
        rootWindow.setWindow(sw6);
    }

    public void setUXGALayout() {
        termListRootWindow.setWindow(new SplitWindow(false, termListViews[0], termListViews[1]));
        termListViews[0].restoreFocus();

        SplitWindow sw1 = new SplitWindow(true, 0.5f, mainViews[2], mainViews[3]);
        SplitWindow sw2 = new SplitWindow(true, 0.3f, mainViews[1], sw1);
        SplitWindow sw3 = new SplitWindow(true, mainViews[4], mainViews[5]);
        SplitWindow sw4 = new SplitWindow(false, 0.6f, sw2, sw3);
        SplitWindow sw5 = new SplitWindow(true, 0.3f, mainViews[0], sw4);
        SplitWindow sw6 = new SplitWindow(false, 0.85f, sw5, mainViews[6]);
        rootWindow.setWindow(sw6);
    }

    public void removeRefOntConceptLabel(Concept c, boolean isInputConcept) {
        if (!isInputConcept || perfectlyMatchedOptionPanel.isIncludeRefOntConceptLabel()) {
            return;
        }
        Set<DODDLELiteral> removedLabelSet = new HashSet<>();
        for (String lang : c.getLangLabelListMap().keySet()) {
            for (DODDLELiteral label : c.getLangLabelListMap().get(lang)) {
                if (!(termSet.contains(label.getString()) || systemAddedTermSet.contains(label
                        .getString()))) {
                    removedLabelSet.add(label);
                }
            }
        }
        for (DODDLELiteral removedLabel : removedLabelSet) {
            c.removeLabel(removedLabel);
        }
    }

    public void setConstructNounAndVerbTree(boolean t) {
        isConstructNounAndVerbTree = t;
    }

    public boolean isConstructNounAndVerbTree() {
        return isConstructNounAndVerbTree;
    }

    class EditPanel extends JPanel implements ActionListener {
        private JTextField inputTermField;
        private JButton addInputTermButton;
        private JButton removeInputTermButton;

        EditPanel() {
            inputTermField = new JTextField();
            addInputTermButton = new JButton(Translator.getTerm("AddButton"));
            addInputTermButton.addActionListener(this);
            removeInputTermButton = new JButton(Translator.getTerm("RemoveButton"));
            removeInputTermButton.addActionListener(this);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addInputTermButton);
            buttonPanel.add(removeInputTermButton);

            setLayout(new BorderLayout());
            add(inputTermField, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);
        }

        public void actionPerformed(ActionEvent e) {
            JList termJList = getTargetTermJList();
            Set<InputTermModel> termModelSet = getTargetTermModelSet();

            if (e.getSource() == addInputTermButton) {
                Set<String> inputTermSet = new HashSet<>();
                if (0 < inputTermField.getText().length()) {
                    inputTermSet.add(inputTermField.getText());
                    addInputTermSet(inputTermSet, 0);
                    inputTermField.setText("");
                }
            } else if (e.getSource() == removeInputTermButton) {
                List<InputTermModel> values = termJList.getSelectedValuesList();
                for (InputTermModel removeTermModel : values) {
                    termSet.remove(removeTermModel.getTerm());
                    termModelSet.remove(removeTermModel);
                    inputTermModelSet.remove(removeTermModel);
                    termCorrespondConceptSetMap.remove(removeTermModel.getTerm());
                    compoundConstructTreeOptionMap.remove(removeTermModel);
                }
                termJList.setListData(termModelSet.toArray());
                termJList.setSelectedIndex(0);
                project.getConceptDefinitionPanel().setInputConceptSet();
            }
        }
    }

    public Set<String> getURISetForReplaceSubConcepts() {
        Set<String> uriSet = new HashSet<>();
        for (ConstructTreeOption option : compoundConstructTreeOptionMap.values()) {
            if (option.isReplaceSubConcepts()) {
                uriSet.add(option.getConcept().getURI());
            }
        }
        return uriSet;
    }

    public ConstructionTypePanel getConstructionTypePanel() {
        return constructionTypePanel;
    }

    public PerfectlyMatchedOptionPanel getPerfectlyMatchedOptionPanel() {
        return perfectlyMatchedOptionPanel;
    }

    public PartiallyMatchedOptionPanel getPartiallyMatchedOptionPanel() {
        return partiallyMatchedOptionPanel;
    }

    public class ConstructionTypePanel extends JPanel {
        private JRadioButton newButton;
        private JRadioButton addButton;

        ConstructionTypePanel() {
            newButton = new JRadioButton(Translator.getTerm("NewRadioButton"), true);
            addButton = new JRadioButton(Translator.getTerm("AddRadioButton"));
            ButtonGroup group = new ButtonGroup();
            group.add(newButton);
            group.add(addButton);
            add(newButton);
            add(addButton);
            setBorder(BorderFactory.createTitledBorder(Translator
                    .getTerm("TreeConstructionOptionBorder")));
        }

        public boolean isNewConstruction() {
            return newButton.isSelected();
        }
    }

    public class PerfectlyMatchedOptionPanel extends JPanel implements ActionListener {
        private JCheckBox constructionBox;
        private JCheckBox trimmingBox;
        private JCheckBox includeRefOntConceptLabelBox;

        PerfectlyMatchedOptionPanel() {
            constructionBox = new JCheckBox(Translator.getTerm("ConstructionCheckBox"), true);
            constructionBox.addActionListener(this);
            trimmingBox = new JCheckBox(Translator.getTerm("TrimmingCheckBox"), true);
            includeRefOntConceptLabelBox = new JCheckBox(
                    Translator.getTerm("AddLabelsFromReferenceOntology"), true);
            add(constructionBox);
            add(trimmingBox);
            add(includeRefOntConceptLabelBox);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == constructionBox) {
                trimmingBox.setEnabled(constructionBox.isSelected());
                includeRefOntConceptLabelBox.setEnabled(constructionBox.isSelected());
            }
        }

        public void setConstruction(boolean t) {
            constructionBox.setSelected(t);
        }

        public void setTrimming(boolean t) {
            trimmingBox.setSelected(t);
        }

        public void setIncludeRefOntConceptLabel(boolean t) {
            includeRefOntConceptLabelBox.setSelected(t);
        }

        public boolean isConstruction() {
            return constructionBox.isSelected();
        }

        public boolean isTrimming() {
            return trimmingBox.isSelected();
        }

        public boolean isIncludeRefOntConceptLabel() {
            return includeRefOntConceptLabelBox.isSelected();
        }
    }

    public class PartiallyMatchedOptionPanel extends JPanel implements ActionListener {
        private JCheckBox constructionBox;
        private JCheckBox trimmingBox;
        private JCheckBox addAbstractConceptBox;
        private JTextField abstractConceptChildNodeNumField;

        PartiallyMatchedOptionPanel() {
            constructionBox = new JCheckBox(Translator.getTerm("ConstructionCheckBox"), true);
            constructionBox.addActionListener(this);
            trimmingBox = new JCheckBox(Translator.getTerm("TrimmingCheckBox"), true);
            addAbstractConceptBox = new JCheckBox(
                    Translator.getTerm("AddAbstractInternalNodeCheckBox"), false);
            abstractConceptChildNodeNumField = new JTextField(2);
            abstractConceptChildNodeNumField.setText("2");
            add(constructionBox);
            add(trimmingBox);
            add(addAbstractConceptBox);
            add(abstractConceptChildNodeNumField);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == constructionBox) {
                constructionBoxAction(constructionBox.isSelected());
            }
        }

        private void constructionBoxAction(boolean t) {
            trimmingBox.setEnabled(t);
            addAbstractConceptBox.setEnabled(t);
        }

        public void setConstruction(boolean t) {
            constructionBox.setSelected(t);
            constructionBoxAction(t);
        }

        public void setTrimming(boolean t) {
            trimmingBox.setSelected(t);
        }

        public void setAddAbstractConcept(boolean t) {
            addAbstractConceptBox.setSelected(t);
        }

        public boolean isConstruction() {
            return constructionBox.isSelected();
        }

        public boolean isTrimming() {
            return trimmingBox.isSelected();
        }

        public boolean isAddAbstractConcept() {
            return addAbstractConceptBox.isSelected();
        }

        public int getAbstractConceptChildNodeNum() {
            int num = 2;
            String numStr = abstractConceptChildNodeNumField.getText();
            try {
                num = Integer.parseInt(numStr);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            return num;
        }
    }

    public Map<InputTermModel, ConstructTreeOption> getCompoundConstructTreeOptionMap() {
        return compoundConstructTreeOptionMap;
    }

    private JPanel getTermListPanel() {
        perfectlyMatchedTermListPanel = getPerfectlyMatchedTermListPanel();
        partiallyMatchedTermListPanel = getPartiallyMatchedTermListPanel();

        termListViews = new View[2];
        termListViews[0] = new View(Translator.getTerm("PerfectlyMatchedTermListPanel"), null,
                perfectlyMatchedTermListPanel);
        termListViews[1] = new View(Translator.getTerm("PartiallyMatchedTermListPanel"), null,
                partiallyMatchedTermListPanel);

        ViewMap viewMap = new ViewMap();
        viewMap.addView(0, termListViews[0]);
        viewMap.addView(1, termListViews[1]);

        termListRootWindow = Utils.createDODDLERootWindow(viewMap);

        JPanel termListPanel = new JPanel();
        termListPanel.setLayout(new BorderLayout());
        termListPanel.add(getSearchTermPanel(), BorderLayout.NORTH);
        termListPanel.add(termListRootWindow, BorderLayout.CENTER);
        termListPanel.add(new EditPanel(), BorderLayout.SOUTH);
        termListPanel.setPreferredSize(new Dimension(300, 100));
        termListPanel.setMinimumSize(new Dimension(300, 100));

        return termListPanel;
    }

    private JPanel getSearchTermPanel() {
        searchTermField = new JTextField();
        searchTermField.addActionListener(this);
        searchTermButton = new JButton(Translator.getTerm("SearchButton"));
        searchTermButton.addActionListener(this);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchTermField, BorderLayout.CENTER);
        searchPanel.add(searchTermButton, BorderLayout.EAST);
        return searchPanel;
    }

    private JPanel getPerfectlyMatchedTermListPanel() {
        perfectlyMatchedTermJList = new JList();
        perfectlyMatchedTermJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        perfectlyMatchedTermJList.addListSelectionListener(this);
        JScrollPane perfectlyMatchedTermListScroll = new JScrollPane(perfectlyMatchedTermJList);
        perfectlyMatchedTermJListTitle = BorderFactory.createTitledBorder(Translator
                .getTerm("PerfectlyMatchedTermList"));
        // perfectMatchedWordListScroll.setBorder(perfectMatchedWordJListTitle);

        perfectlyMatchedAmbiguityCntCheckBox = new JCheckBox(
                Translator.getTerm("SenseCountCheckBox"), true);
        perfectlyMatchedAmbiguityCntCheckBox.addActionListener(this);
        perfectlyMatchedIsSyncCheckBox = new JCheckBox(
                Translator.getTerm("SyncPartiallyMatchedTermListCheckBox"), true);
        perfectlyMatchedIsSystemAddedTermCheckBox = new JCheckBox(
                Translator.getTerm("SystemAddedInputTermCheckBox"), true);
        perfectlyMatchedIsSystemAddedTermCheckBox.addActionListener(this);
        JPanel perfectlyMatchedFilterPanel = new JPanel();
        perfectlyMatchedFilterPanel.setLayout(new GridLayout(3, 1));
        perfectlyMatchedFilterPanel.add(perfectlyMatchedAmbiguityCntCheckBox);
        perfectlyMatchedFilterPanel.add(perfectlyMatchedIsSystemAddedTermCheckBox);
        perfectlyMatchedFilterPanel.add(perfectlyMatchedIsSyncCheckBox);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(perfectlyMatchedTermListScroll, BorderLayout.CENTER);
        panel.add(perfectlyMatchedFilterPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel getPartiallyMatchedTermListPanel() {
        partiallyMatchedTermJList = new JList();
        partiallyMatchedTermJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        partiallyMatchedTermJList.addListSelectionListener(this);
        JScrollPane partiallyMatchedTermListScroll = new JScrollPane(partiallyMatchedTermJList);
        partiallyMatchedTermJListTitle = BorderFactory.createTitledBorder(Translator
                .getTerm("PartiallyMatchedTermList"));
        // partialMatchedWordListScroll.setBorder(partialMatchedWordJListTitle);

        partiallyMatchedCompoundWordCheckBox = new JCheckBox(
                Translator.getTerm("ShowMorphemeListCheckBox"), true);
        partiallyMatchedCompoundWordCheckBox.addActionListener(this);
        partiallyMatchedMatchedTermBox = new JCheckBox(Translator.getTerm("MatchResultCheckBox"),
                true);
        partiallyMatchedMatchedTermBox.addActionListener(this);
        partiallyMatchedAmbiguityCntCheckBox = new JCheckBox(
                Translator.getTerm("SenseCountCheckBox"), true);
        partiallyMatchedAmbiguityCntCheckBox.addActionListener(this);
        partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox = new JCheckBox(
                Translator.getTerm("ShowOnlyCorrespondCompoundWordsCheckBox"), false);
        partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox.addActionListener(this);
        JPanel partialMatchedFilterPanel = new JPanel();
        partialMatchedFilterPanel.setLayout(new GridLayout(2, 2));
        partialMatchedFilterPanel.add(partiallyMatchedCompoundWordCheckBox);
        partialMatchedFilterPanel.add(partiallyMatchedMatchedTermBox);
        partialMatchedFilterPanel.add(partiallyMatchedAmbiguityCntCheckBox);
        JPanel partialMatchedOptionPanel = new JPanel();
        partialMatchedOptionPanel.setLayout(new BorderLayout());
        partialMatchedOptionPanel.add(partialMatchedFilterPanel, BorderLayout.CENTER);
        partialMatchedOptionPanel.add(partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox,
                BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(partiallyMatchedTermListScroll, BorderLayout.CENTER);
        panel.add(partialMatchedOptionPanel, BorderLayout.SOUTH);

        return panel;
    }

    public int getPartiallyMatchedTermCnt() {
        if (partiallyMatchedTermModelSet == null) {
            return 0;
        }
        return partiallyMatchedTermModelSet.size();
    }

    public int getPerfectlyMatchedTermCnt(boolean isSystemAdded) {
        if (perfectlyMatchedTermModelSet == null) {
            return 0;
        }
        int num = 0;
        for (InputTermModel iwModel : perfectlyMatchedTermModelSet) {
            if (isSystemAdded) {
                if (iwModel.isSystemAdded()) {
                    num++;
                }
            } else {
                if (!iwModel.isSystemAdded()) {
                    num++;
                }
            }
        }
        return num;
    }

    public int getInputTermCnt() {
        return getPartiallyMatchedTermCnt() + getPerfectlyMatchedTermCnt() + getUndefinedTermCnt();
    }

    public int getPerfectlyMatchedTermCnt() {
        return getPerfectlyMatchedTermCnt(false);
    }

    public int getSystemAddedPerfectlyMatchedTermCnt() {
        return getPerfectlyMatchedTermCnt(true);
    }

    public int getMatchedTermCnt() {
        return getPartiallyMatchedTermCnt() + getPerfectlyMatchedTermCnt()
                + getSystemAddedPerfectlyMatchedTermCnt();
    }

    public int getUndefinedTermCnt() {
        return undefinedTermListPanel.getModel().getSize();
    }

    public UndefinedTermListPanel getUndefinedTermListPanel() {
        return undefinedTermListPanel;
    }

    public void setDocumentSelectionPanel(InputDocumentSelectionPanel p) {
        docSelectionPanel = p;
    }

    public void selectTopList() {
        if (perfectlyMatchedTermJList.getModel().getSize() != 0) {
            termListRootWindow.getWindow().getChildWindow(0).restoreFocus();
            perfectlyMatchedTermJList.setSelectedIndex(0);
        } else {
            clearConceptInfoPanel();
        }
    }

    private void clearConceptInfoPanel() {
        labelPanel.clearData();
        descriptionPanel.clearData();
        setPartiallyMatchedOptionButton(false);
    }

    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = aroundConceptTree.getSelectionPath();
        if (path != null) {
            ConceptTreeNode node = (ConceptTreeNode) path.getLastPathComponent();
            selectedConcept = node.getConcept();
            labelPanel.setSelectedConcept(selectedConcept);
            descriptionPanel.setSelectedConcept(selectedConcept);
            labelPanel.setLabelLangList();
            descriptionPanel.setDescriptionLangList();
        }
    }

    private void saveCompoundOption(String option) {
        JList termJList = getTargetTermJList();
        InputTermModel iwModel = (InputTermModel) termJList.getSelectedValue();
        if (iwModel != null && iwModel.isPartiallyMatchTerm()) {
            ConstructTreeOption ctOption = compoundConstructTreeOptionMap.get(iwModel);
            ctOption.setOption(option);
            compoundConstructTreeOptionMap.put(iwModel, ctOption);
        }
    }

    private void saveReplaceSubConceptsOption() {
        JList termJList = getTargetTermJList();
        InputTermModel iwModel = (InputTermModel) termJList.getSelectedValue();
        if (iwModel != null && iwModel.isSystemAdded()) {
            ConstructTreeOption ctOption = compoundConstructTreeOptionMap.get(iwModel);
            ctOption.setIsReplaceSubConcepts(replaceSubClassesCheckBox.isSelected());
            compoundConstructTreeOptionMap.put(iwModel, ctOption);
        }
    }

    private void showOnlyRelatedCompoundWords() {
        if (partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox.isSelected()) {
            InputTermModel targetIWModel = (InputTermModel) perfectlyMatchedTermJList
                    .getSelectedValue();
            if (targetIWModel == null) {
                return;
            }
            Set searchedPartiallyMatchedTermModelSet = new TreeSet();
            for (InputTermModel iwModel : partiallyMatchedTermModelSet) {
                if (iwModel.getMatchedTerm().equals(targetIWModel.getMatchedTerm())) {
                    searchedPartiallyMatchedTermModelSet.add(iwModel);
                }
            }
            partiallyMatchedTermJList.setListData(searchedPartiallyMatchedTermModelSet.toArray());
            partiallyMatchedTermJListTitle.setTitle(Translator.getTerm("PartiallyMatchTermList")
                    + " (" + searchedPartiallyMatchedTermModelSet.size() + "/"
                    + partiallyMatchedTermModelSet.size() + ")");
        } else {
            partiallyMatchedTermJList.setListData(partiallyMatchedTermModelSet.toArray());
            partiallyMatchedTermJListTitle.setTitle(Translator.getTerm("PartiallyMatchTermList")
                    + " (" + partiallyMatchedTermModelSet.size() + ")");
        }
        perfectlyMatchedTermJList.repaint();
        partiallyMatchedTermJList.repaint();
        termListRootWindow.getWindow().repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == perfectlyMatchedTermJList
                || e.getSource() == partiallyMatchedTermJList) {
            perfectlyMatchedTermJList.repaint();
            partiallyMatchedTermJList.repaint();
        } else if (e.getSource() == highlightInputTermCheckBox) {
            highlightInputTerm();
        } else if (e.getSource() == showAroundConceptTreeCheckBox) {
            showAroundConceptTree();
        } else if (e.getSource() == addAsSameConceptRadioButton) {
            saveCompoundOption("SAME");
        } else if (e.getSource() == addAsSubConceptRadioButton) {
            saveCompoundOption("SUB");
        } else if (e.getSource() == replaceSubClassesCheckBox) {
            saveReplaceSubConceptsOption();
        } else if (e.getSource() == perfectlyMatchedAmbiguityCntCheckBox
                || e.getSource() == partiallyMatchedAmbiguityCntCheckBox
                || e.getSource() == partiallyMatchedCompoundWordCheckBox
                || e.getSource() == partiallyMatchedMatchedTermBox
                || e.getSource() == perfectlyMatchedIsSystemAddedTermCheckBox) {
            perfectlyMatchedTermJList.repaint();
            partiallyMatchedTermJList.repaint();
        } else if (e.getSource() == partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox) {
            showOnlyRelatedCompoundWords();
        } else if (e.getSource() == searchTermButton || e.getSource() == searchTermField) {
            String keyWord = searchTermField.getText();
            if (keyWord.length() == 0) {
                perfectlyMatchedTermJList.setListData(perfectlyMatchedTermModelSet.toArray());
                perfectlyMatchedTermJListTitle.setTitle(Translator
                        .getTerm("PerfectlyMatchedTermList")
                        + " ("
                        + perfectlyMatchedTermModelSet.size() + ")");
                termListViews[0].getViewProperties().setTitle(
                        perfectlyMatchedTermJListTitle.getTitle());

                partiallyMatchedTermJList.setListData(partiallyMatchedTermModelSet.toArray());
                partiallyMatchedTermJListTitle.setTitle(Translator
                        .getTerm("PartiallyMatchedTermList")
                        + " ("
                        + partiallyMatchedTermModelSet.size() + ")");
                termListViews[1].getViewProperties().setTitle(
                        partiallyMatchedTermJListTitle.getTitle());

            } else {
                Set searchedPerfectlyMatchedTermModelSet = new TreeSet();
                Set searchedPartiallyMatchedTermModelSet = new TreeSet();
                for (InputTermModel iwModel : perfectlyMatchedTermModelSet) {
                    if (iwModel.getTerm().contains(keyWord)) {
                        searchedPerfectlyMatchedTermModelSet.add(iwModel);
                    }
                }
                perfectlyMatchedTermJList.setListData(searchedPerfectlyMatchedTermModelSet
                        .toArray());
                perfectlyMatchedTermJListTitle.setTitle(Translator
                        .getTerm("PerfectlyMatchedTermList")
                        + " ("
                        + searchedPerfectlyMatchedTermModelSet.size()
                        + "/"
                        + perfectlyMatchedTermModelSet.size() + ")");
                termListViews[0].getViewProperties().setTitle(
                        perfectlyMatchedTermJListTitle.getTitle());

                InputTermModel targetIWModel = (InputTermModel) perfectlyMatchedTermJList
                        .getSelectedValue();
                if (targetIWModel == null && 0 < perfectlyMatchedTermJList.getModel().getSize()) {
                    targetIWModel = (InputTermModel) perfectlyMatchedTermJList.getModel()
                            .getElementAt(0);
                    perfectlyMatchedTermJList.setSelectedValue(targetIWModel, true);
                }
                for (InputTermModel iwModel : partiallyMatchedTermModelSet) {
                    if (iwModel.getTerm().contains(keyWord)) {
                        if (partiallyMatchedShowOnlyRelatedCompoundWordsCheckBox.isSelected()) {
                            if (targetIWModel != null
                                    && iwModel.getMatchedTerm().equals(
                                    targetIWModel.getMatchedTerm())) {
                                searchedPartiallyMatchedTermModelSet.add(iwModel);
                            }
                        } else {
                            searchedPartiallyMatchedTermModelSet.add(iwModel);
                        }
                    }
                }

                partiallyMatchedTermJList.setListData(searchedPartiallyMatchedTermModelSet
                        .toArray());
                partiallyMatchedTermJListTitle.setTitle(Translator
                        .getTerm("PartiallyMatchedTermList")
                        + " ("
                        + searchedPartiallyMatchedTermModelSet.size()
                        + "/"
                        + partiallyMatchedTermModelSet.size() + ")");
                termListViews[1].getViewProperties().setTitle(
                        partiallyMatchedTermJListTitle.getTitle());

            }
            termListRootWindow.getWindow().repaint();
        }
    }

    /**
     * 入力文書中の入力単語を強調表示する
     */
    private void highlightInputTerm() {
        if (highlightInputTermCheckBox.isSelected()) {
            JList termJList = getTargetTermJList();
            InputTermModel iwModel = (InputTermModel) termJList.getSelectedValue();
            if (iwModel != null) {
                // String targetLines =
                // docSelectionPanel.getTargetTextLines(iwModel.getWord());
                String targetLines = docSelectionPanel.getTargetHtmlLines(iwModel.getTerm());
                documentArea.setText(targetLines);
            }
        } else {
            documentArea.setText("");
        }
    }

    public boolean isPerfectlyMatchedAmbiguityCntCheckBox() {
        return perfectlyMatchedAmbiguityCntCheckBox.isSelected();
    }

    public boolean isPerfectlyMatchedSystemAddedTermCheckBox() {
        return perfectlyMatchedIsSystemAddedTermCheckBox.isSelected();
    }

    public boolean isPartiallyMatchedAmbiguityCntCheckBox() {
        return partiallyMatchedAmbiguityCntCheckBox.isSelected();
    }

    public boolean isPartiallyMatchedCompoundWordCheckBox() {
        return partiallyMatchedCompoundWordCheckBox.isSelected();
    }

    public boolean isPartiallyMatchedMatchedTermBox() {
        return partiallyMatchedMatchedTermBox.isSelected();
    }

    public ConstructTreeAction getConstructNounTreeAction() {
        return constructNounTreeAction;
    }

    public ConstructTreeAction getConstructNounAndVerbTreeAction() {
        return constructNounAndVerbTreeAction;
    }

    public Action getSavePerfectlyMatchedTermAction() {
        return savePerfectlyMatchedTermAction;
    }

    /*
     * 特に現状では使っていない
     */
    public Action getSavePerfectlyMatchedTermWithCompoundWordAction() {
        return savePerfectlyMatchedTermWithCompoundWordAcion;
    }

    public Map<String, Set<Concept>> getTermCorrespondConceptSetMap() {
        return termCorrespondConceptSetMap;
    }

    public Map<String, Set<Concept>> getTermConceptSetMap() {
        return termConceptSetMap;
    }

    public void loadTermConceptMap() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showOpenDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            loadTermCorrespondConceptSetMap(chooser.getSelectedFile());
        }
    }

    public void loadTermCorrespondConceptSetMap(File file) {
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            Set<String> inputTermSet = new HashSet<>();
            while (inputTermModelSet == null) {
                try {
                    Thread.sleep(1000);
                    // System.out.println("sleep");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (InputTermModel iwModel : inputTermModelSet) {
                inputTermSet.add(iwModel.getTerm());
            }

            while (reader.ready()) {
                String line = reader.readLine();
                String[] termURI = line.replaceAll("\n", "").split(",");
                if (0 < termURI[0].length()) {
                    String term = termURI[0];
                    InputTermModel iwModel = inputModule.makeInputTermModel(term);
                    if (iwModel != null && inputTermSet.contains(iwModel.getTerm())) {
                        Set<Concept> correspondConceptSet = new HashSet<>();
                        for (int i = 1; i < termURI.length; i++) {
                            String uri = termURI[i];
                            Concept c = DODDLEDic.getConcept(uri);
                            if (c != null) { // 参照していないオントロジーの概念と対応づけようとした場合にnullとなる
                                correspondConceptSet.add(c);
                            } else if (uri.equals("null")) {
                                correspondConceptSet.add(nullConcept);
                                break;
                            }
                        }
                        if (0 < correspondConceptSet.size()) {
                            termCorrespondConceptSetMap
                                    .put(iwModel.getTerm(), correspondConceptSet);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void loadTermCorrespondConceptSetMap(int projectID, Statement stmt) {
        try {
            Set<String> inputTermSet = new HashSet<>();
            while (inputTermModelSet == null) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (InputTermModel iwModel : inputTermModelSet) {
                inputTermSet.add(iwModel.getTerm());
            }

            String sql = "SELECT * from input_term_concept_map where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String inputTerm = URLDecoder.decode(rs.getString("Input_Term"), StandardCharsets.UTF_8);
                String inputConcept = rs.getString("Input_Concept");

                InputTermModel iwModel = inputModule.makeInputTermModel(inputTerm);
                if (iwModel != null && inputTermSet.contains(iwModel.getTerm())) {
                    Set<Concept> correspondConceptSet;
                    if (termCorrespondConceptSetMap.get(iwModel.getTerm()) != null) {
                        correspondConceptSet = termCorrespondConceptSetMap.get(iwModel.getTerm());
                    } else {
                        correspondConceptSet = new HashSet<>();
                    }
                    Concept c = DODDLEDic.getConcept(inputConcept);
                    if (c != null) { // 参照していないオントロジーの概念と対応づけようとした場合にnullとなる
                        correspondConceptSet.add(c);
                    } else if (inputConcept.equals("null")) {
                        correspondConceptSet.add(nullConcept);
                        break;
                    }
                    if (0 < correspondConceptSet.size()) {
                        termCorrespondConceptSetMap.put(iwModel.getTerm(), correspondConceptSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInputConceptSet() {
        inputConceptSet.clear();
        systemAddedInputConceptSet.clear();
        if (inputTermModelSet == null) {
            return;
        }
        for (InputTermModel iwModel : inputTermModelSet) {
            Set<Concept> correspondConceptSet = termCorrespondConceptSetMap.get(iwModel.getTerm());
            if (correspondConceptSet == null) {
                correspondConceptSet = new HashSet<>();
            }
            if (iwModel.isPartiallyMatchTerm()) {
                termCorrespondConceptSetMap.put(iwModel.getTerm(), correspondConceptSet);
            }
            if (correspondConceptSet.size() == 0) {
                Set<Concept> conceptSet = termConceptSetMap.get(iwModel.getMatchedTerm());
                if (conceptSet != null) {
                    Concept c = (Concept) conceptSet.toArray()[0];
                    correspondConceptSet.add(c);
                    termCorrespondConceptSetMap.put(iwModel.getTerm(), correspondConceptSet);
                }
            }
            if (correspondConceptSet.size() == 1 && correspondConceptSet.contains(nullConcept)) {
                compoundConstructTreeOptionMap.remove(iwModel);
                continue;
            }
            if (iwModel.isPartiallyMatchTerm()) {
                for (Concept c : correspondConceptSet) { // 最初の概念だけを扱っても良い．
                    ConstructTreeOption ctOption = new ConstructTreeOption(c);
                    compoundConstructTreeOptionMap.put(iwModel, ctOption);
                }
            }
            for (Concept c : correspondConceptSet) {
                if (c != null) {
                    c.setInputLabel(new DODDLELiteral("", iwModel.getMatchedTerm())); // メインとなる見出しを設定する
                    inputConceptSet.add(c);
                    if (iwModel.isSystemAdded()) {
                        systemAddedInputConceptSet.add(c);
                    }
                }
            }
        }
    }

    public void addInputConcept(Concept c) {
        inputConceptSet.add(c);
    }

    public void deleteSystemAddedConcept(Concept c) {
        systemAddedInputConceptSet.remove(c);
    }

    private void setPartiallyMatchedOptionButton(boolean t) {
        addAsSameConceptRadioButton.setEnabled(t);
        addAsSubConceptRadioButton.setEnabled(t);
    }

    private void selectAmbiguousConcept(JList termJList) {
        if (!termJList.isSelectionEmpty()) {
            String orgTerm = ((InputTermModel) termJList.getSelectedValue()).getTerm();
            String selectedTerm = ((InputTermModel) termJList.getSelectedValue()).getMatchedTerm();
            Set<Concept> conceptSet = termConceptSetMap.get(selectedTerm);

            Set<EvalConcept> evalConceptSet;

            if (!(termEvalConceptSetMap == null || termEvalConceptSetMap.get(selectedTerm) == null)) {
                evalConceptSet = termEvalConceptSetMap.get(selectedTerm);
            } else {
                evalConceptSet = getEvalConceptSet(conceptSet);
                evalConceptSet.add(nullEvalConcept);
            }

            conceptSetJList.setListData(evalConceptSet.toArray());
            Set<Concept> correspondConceptSet = termCorrespondConceptSetMap.get(orgTerm);
            if (correspondConceptSet != null) {
                if (correspondConceptSet.size() == 1 && correspondConceptSet.contains(nullConcept)) {
                    conceptSetJList.setSelectedValue(nullEvalConcept, true);
                    setPartiallyMatchedOptionButton(false);
                    return;
                }

                int[] selectedIndices = new int[correspondConceptSet.size()];
                int index = 0;
                ListModel model = conceptSetJList.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    EvalConcept evalConcept = (EvalConcept) model.getElementAt(i);
                    if (evalConcept.getConcept() == null) {
                        continue;
                    }
                    if (correspondConceptSet.contains(evalConcept.getConcept())) {
                        selectedIndices[index++] = i;
                    }
                }
                conceptSetJList.setSelectedIndices(selectedIndices);
            } else {
                conceptSetJList.setSelectedIndex(0);
                EvalConcept evalConcept = (EvalConcept) conceptSetJList.getSelectedValue();
                correspondConceptSet = new HashSet<>();
                correspondConceptSet.add(evalConcept.getConcept());
                termCorrespondConceptSetMap.put(orgTerm, correspondConceptSet);
            }
            highlightInputTerm();

            // 完全照合単語，システムが追加した完全照合単語，部分照合単語に応じて
            // 階層構築オプションパネルを切り替える
            InputTermModel iwModel = (InputTermModel) termJList.getSelectedValue();
            if (iwModel.isPartiallyMatchTerm()) {
                switchConstructTreeOptionPanel(partiallyMatchedConstructTreeOptionPanel);
                setPartiallyMatchedOptionButton(true);
                ConstructTreeOption ctOption = compoundConstructTreeOptionMap.get(iwModel);
                if (ctOption != null) {
                    if (ctOption.getOption().equals("SAME")) {
                        addAsSameConceptRadioButton.setSelected(true);
                    } else {
                        addAsSubConceptRadioButton.setSelected(true);
                    }
                } else {
                    EvalConcept evalConcept = (EvalConcept) conceptSetJList.getSelectedValue();
                    if (evalConcept != null) {
                        ctOption = new ConstructTreeOption(evalConcept.getConcept());
                        compoundConstructTreeOptionMap.put(iwModel, ctOption);
                        addAsSameConceptRadioButton.setSelected(true);
                    }
                }
            } else if (iwModel.isSystemAdded()) {
                switchConstructTreeOptionPanel(systemAddedPerfectlyMatchedConstructTreeOptionPanel);
                if (compoundConstructTreeOptionMap.get(iwModel) == null) {
                    replaceSubClassesCheckBox.setSelected(false);
                    EvalConcept evalConcept = (EvalConcept) conceptSetJList.getSelectedValue();
                    ConstructTreeOption ctOption = new ConstructTreeOption(evalConcept.getConcept());
                    compoundConstructTreeOptionMap.put(iwModel, ctOption);
                } else {
                    ConstructTreeOption ctOption = compoundConstructTreeOptionMap.get(iwModel);
                    replaceSubClassesCheckBox.setSelected(ctOption.isReplaceSubConcepts());
                }
            } else {
                switchConstructTreeOptionPanel(perfectlyMatchedConstructTreeOptionPanel);
            }
        }
    }

    private void switchConstructTreeOptionPanel(JPanel optionPanel) {
        constructTreeOptionPanel.remove(partiallyMatchedConstructTreeOptionPanel);
        constructTreeOptionPanel.remove(perfectlyMatchedConstructTreeOptionPanel);
        constructTreeOptionPanel.remove(systemAddedPerfectlyMatchedConstructTreeOptionPanel);
        constructTreeOptionPanel.add(optionPanel);
        constructTreeOptionPanel.validate();
        constructTreeOptionPanel.repaint();
    }

    /**
     * @param conceptSet
     * @return
     */
    private Set<EvalConcept> getEvalConceptSet(Set<Concept> conceptSet) {
        Set<EvalConcept> evalConceptSet = new TreeSet<>();
        for (Concept c : conceptSet) {
            evalConceptSet.add(new EvalConcept(c, 0));
        }
        return evalConceptSet;
    }

    private JList getTargetTermJList() {
        if (termListRootWindow.getWindow().getLastFocusedChildWindow() == null) {
            return perfectlyMatchedTermJList;
        }
        DockingWindow lastFocusedWindow = termListRootWindow.getWindow()
                .getLastFocusedChildWindow();
        if (lastFocusedWindow.getTitle().equals(termListViews[0].getViewProperties().getTitle())) {
            return perfectlyMatchedTermJList;
        } else if (lastFocusedWindow.getTitle().equals(
                termListViews[1].getViewProperties().getTitle())) {
            return partiallyMatchedTermJList;
        }
        return perfectlyMatchedTermJList;
    }

    private Set<InputTermModel> getTargetTermModelSet() {
        if (getTargetTermJList() == perfectlyMatchedTermJList) {
            return perfectlyMatchedTermModelSet;
        } else if (getTargetTermJList() == partiallyMatchedTermJList) {
            return partiallyMatchedTermModelSet;
        }
        return perfectlyMatchedTermModelSet;
    }

    private void syncPartiallyMatchedAmbiguousConceptSet(String orgTerm,
                                                         Set<Concept> correspondConceptSet) {
        if (!perfectlyMatchedIsSyncCheckBox.isSelected()) {
            return;
        }
        for (InputTermModel iwModel : partiallyMatchedTermModelSet) {
            if (iwModel.getMatchedTerm().equals(orgTerm)) {
                termCorrespondConceptSetMap.put(iwModel.getTerm(), correspondConceptSet);
            }
        }
    }

    public Concept getSelectedConcept() {
        return selectedConcept;
    }

    private void selectCorrectConcept(JList termJList) {
        if (!termJList.isSelectionEmpty() && !conceptSetJList.isSelectionEmpty()) {
            InputTermModel iwModel = (InputTermModel) termJList.getSelectedValue();
            List<EvalConcept> evalConcepts = conceptSetJList.getSelectedValuesList();
            String term = iwModel.getTerm();
            for (EvalConcept evalConcept : evalConcepts) {
                if (evalConcept == nullEvalConcept) {
                    Set<Concept> correspondConceptSet = new HashSet<>();
                    correspondConceptSet.add(nullConcept);
                    termCorrespondConceptSetMap.put(term, correspondConceptSet);
                    syncPartiallyMatchedAmbiguousConceptSet(term, correspondConceptSet);
                    labelPanel.clearData();
                    descriptionPanel.clearData();
                    aroundConceptTree.setModel(new DefaultTreeModel(null));
                    switchConstructTreeOptionPanel(perfectlyMatchedConstructTreeOptionPanel);
                    return;
                }
            }

            if (getTargetTermJList() == perfectlyMatchedTermJList) {
                switchConstructTreeOptionPanel(perfectlyMatchedConstructTreeOptionPanel);
            } else {
                switchConstructTreeOptionPanel(partiallyMatchedConstructTreeOptionPanel);
            }

            Set<Concept> correspondConceptSet = new HashSet<>();

            for (EvalConcept evalConcept : evalConcepts) {
                correspondConceptSet.add(evalConcept.getConcept());
            }
            termCorrespondConceptSetMap.put(term, correspondConceptSet);
            syncPartiallyMatchedAmbiguousConceptSet(term, correspondConceptSet);
            selectedConcept = evalConcepts.get(0).getConcept();
            if (selectedConcept != null) {
                labelPanel.setSelectedConcept(selectedConcept);
                descriptionPanel.setSelectedConcept(selectedConcept);
                labelPanel.setLabelLangList();
                labelPanel.setLabelList();
                descriptionPanel.setDescriptionLangList();
                descriptionPanel.setDescriptionList();
            }

            showAroundConceptTree();
            for (Concept c : correspondConceptSet) {
                ConstructTreeOption ctOption = compoundConstructTreeOptionMap.get(iwModel);
                if (ctOption != null) {
                    ctOption.setConcept(c);
                    compoundConstructTreeOptionMap.put(iwModel, ctOption);
                }
            }
            project.addLog("Concept Selection", selectedConcept);
        }
    }

    /**
     * 選択されている概念のオントロジー中のルートまでのパスを表示
     */
    private void showAroundConceptTree() {
        if (showAroundConceptTreeCheckBox.isSelected()) {
            List<EvalConcept> evalConceptList = conceptSetJList.getSelectedValuesList();
            Set<List<Concept>> pathToRootSet = new HashSet<>();
            for (EvalConcept ec : evalConceptList) {
                pathToRootSet.addAll(OWLOntologyManager.getPathToRootSet(ec.getConcept().getURI()));
                int pathSize = 0;
                for (List<Concept> pathToRoot : pathToRootSet) {
                    if (pathSize < pathToRoot.size()) {
                        pathSize = pathToRoot.size();
                    }
                }
                if (pathSize <= 1
                        && DODDLE_OWL.GENERAL_ONTOLOGY_NAMESPACE_SET.contains(ec.getConcept()
                        .getNameSpace())) {
                    pathToRootSet.clear();
                    if (ec.getConcept().getNameSpace().equals(DODDLEConstants.EDR_URI)) {
                        pathToRootSet.addAll(EDRTree.getEDRTree().getConceptPathToRootSet(
                                ec.getConcept().getLocalName()));
                    } else if (ec.getConcept().getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
                        pathToRootSet.addAll(EDRTree.getEDRTTree().getConceptPathToRootSet(
                                ec.getConcept().getLocalName()));
                    } else if (ec.getConcept().getNameSpace().equals(DODDLEConstants.WN_URI)) {
                        pathToRootSet.addAll(WordNetDic.getPathToRootSet(Long.valueOf(ec.getConcept()
                                .getLocalName())));
                    } else if (ec.getConcept().getNameSpace().equals(DODDLEConstants.JPN_WN_URI)) {
                        pathToRootSet.addAll(JPNWNTree.getJPNWNTree().getConceptPathToRootSet(
                                ec.getConcept().getLocalName()));
                    }
                }
            }
            TreeModel model = constructClassPanel.getDefaultConceptTreeModel(pathToRootSet,
                    ConceptTreeMaker.DODDLE_CLASS_ROOT_URI);
            aroundConceptTree.setModel(model);
            for (int i = 0; i < aroundConceptTree.getRowCount(); i++) {
                aroundConceptTree.expandPath(aroundConceptTree.getPathForRow(i));
            }
        } else {
            aroundConceptTree.setModel(new DefaultTreeModel(null));
        }
    }

    private final ImageIcon bestMatchIcon = Utils.getImageIcon("class_best_match_icon.png");
    private final ImageIcon ConceptNodeIcon = Utils.getImageIcon("class_sin_icon.png");

    public class AroundTreeCellRenderer extends DefaultTreeCellRenderer {

        public AroundTreeCellRenderer() {
            setOpaque(true);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component component = super.getTreeCellRendererComponent(tree, value, selected,
                    expanded, leaf, row, hasFocus);

            setText(value.toString());

            if (selected) {
                setBackground(new Color(0, 0, 128));
                setForeground(Color.white);
            } else {
                setBackground(Color.white);
                setForeground(Color.black);
            }

            if (value.getClass().equals(ConceptTreeNode.class)) {
                ConceptTreeNode node = (ConceptTreeNode) value;
                if (node.isLeaf()) {
                    setIcon(bestMatchIcon);
                } else {
                    setIcon(ConceptNodeIcon);
                }
            }
            return component;
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == perfectlyMatchedTermJList) {
            selectAmbiguousConcept(perfectlyMatchedTermJList);
            showOnlyRelatedCompoundWords();
        } else if (e.getSource() == partiallyMatchedTermJList) {
            selectAmbiguousConcept(partiallyMatchedTermJList);
        } else if (e.getSource() == conceptSetJList) {
            selectCorrectConcept(getTargetTermJList());
        } else if (e.getSource() == highlightPartJList) {
            jumpHilightPart();
        }
    }

    private void jumpHilightPart() {
        Integer lineNum = (Integer) highlightPartJList.getSelectedValue();
        Rectangle rect = documentArea.getVisibleRect();
        rect.y = 0;
        documentArea.scrollRectToVisible(rect);
        int lineHeight = documentArea.getFontMetrics(documentArea.getFont()).getHeight();
        // System.out.println(lineHeight);
        rect.y = (lineNum + 1) * lineHeight;
        documentArea.scrollRectToVisible(rect);
    }

    public void initTermList() {
        systemAddedTermSet = new HashSet<>();
        inputTermModelSet = inputModule.getInputTermModelSet();
        perfectlyMatchedTermModelSet = new TreeSet<>();
        partiallyMatchedTermModelSet = new TreeSet<>();

        for (InputTermModel itModel : inputTermModelSet) {
            if (itModel.isPartiallyMatchTerm()) {
                partiallyMatchedTermModelSet.add(itModel);
            } else {
                perfectlyMatchedTermModelSet.add(itModel);
                if (itModel.isSystemAdded()) {
                    systemAddedTermSet.add(itModel.getTerm());
                }
            }
        }
        perfectlyMatchedTermJList.setListData(perfectlyMatchedTermModelSet.toArray());
        perfectlyMatchedTermJListTitle.setTitle(Translator.getTerm("PerfectlyMatchedTermList")
                + " (" + perfectlyMatchedTermModelSet.size() + ")");
        termListViews[0].getViewProperties().setTitle(perfectlyMatchedTermJListTitle.getTitle());

        partiallyMatchedTermJList.setListData(partiallyMatchedTermModelSet.toArray());
        partiallyMatchedTermJListTitle.setTitle(Translator.getTerm("PartiallyMatchedTermList")
                + " (" + partiallyMatchedTermModelSet.size() + ")");
        termListViews[1].getViewProperties().setTitle(partiallyMatchedTermJListTitle.getTitle());

        termConceptSetMap = inputModule.getTermConceptSetMap();

        Set<String> undefinedTermSet = inputModule.getUndefinedTermSet();
        DefaultListModel listModel = undefinedTermListPanel.getModel();
        listModel.clear();
        for (String term : undefinedTermSet) {
            if (0 < term.length()) {
                listModel.addElement(term);
            }
        }
        undefinedTermListPanel.setTitleWithSize();
        perfectlyMatchedTermJList.repaint();
        partiallyMatchedTermJList.repaint();
        termListRootWindow.getWindow().repaint();
    }

    public boolean isLoadInputTermSet() {
        return inputModule.isLoadInputTermSet();
    }

    public void loadInputTermSet(File file, int taskCnt) {
        if (!file.exists()) {
            inputModule.setIsLoadInputTermSet();
            return;
        }
        inputFile = file;
        Set<String> termSet = new HashSet<>();
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(inputFile);
            reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            while (reader.ready()) {
                String line = reader.readLine();
                String term = line.replaceAll("\n", "");
                termSet.add(term);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        loadInputTermSet(termSet, taskCnt);
    }

    public void loadInputTermSet(int projectID, Statement stmt, int taskCnt) {
        Set<String> inputTermSet = new HashSet<>();
        try {
            String sql = "SELECT * from input_term_set where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String inputTerm = URLDecoder.decode(rs.getString("Input_Term"), StandardCharsets.UTF_8);
                inputTermSet.add(inputTerm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadInputTermSet(inputTermSet, taskCnt);
    }

    private void clearPanel() {
        conceptSetJList.setListData(new Object[0]);
        clearConceptInfoPanel();
        documentArea.setText("");
        aroundConceptTree.setModel(new DefaultTreeModel(null));
    }

    public void loadInputTermSet(Set<String> termSet, int taskCnt) {
        this.termSet = termSet;
        perfectlyMatchedTermJList.clearSelection();
        partiallyMatchedTermJList.clearSelection();
        undefinedTermListPanel.clearSelection();
        compoundConstructTreeOptionMap.clear();
        EDRTree.getEDRTree().clear();
        EDRTree.getEDRTTree().clear();
        clearPanel();
        inputModule.initData(termSet, taskCnt);
    }

    public void makeEDRTree() {
        EDRTree.getEDRTree().makeEDRTree(getIDSet(DODDLEConstants.EDR_URI));
        EDRTree.getEDRTTree().makeEDRTree(getIDSet(DODDLEConstants.EDRT_URI));
    }

    private Set<String> getIDSet(String ns) {
        Set<String> idSet = new HashSet<>();
        for (InputTermModel itModel : perfectlyMatchedTermModelSet) {
            String term = itModel.getTerm();
            Set<Concept> conceptSet = termConceptSetMap.get(term);
            if (conceptSet == null) {
                continue;
            }
            for (Concept c : termConceptSetMap.get(term)) {
                if (c.getNameSpace().equals(ns)) {
                    idSet.add(c.getLocalName());
                }
            }
        }
        return idSet;
    }

    public void addInputTermSet(Set<String> addedTermSet, int taskCnt) {
        clearPanel();
        if (termSet == null) {
            termSet = new HashSet<>();
        }
        termSet.addAll(addedTermSet);
        inputModule.initData(termSet, taskCnt);
    }

    public Set<Concept> getInputConceptSet() {
        return inputConceptSet;
    }

    public Set<Concept> getSystemAddedInputConceptSet() {
        return systemAddedInputConceptSet;
    }

    public Set<Concept> getInputNounConceptSet() {
        return inputNounConceptSet;
    }

    public Set<Concept> getInputVerbConceptSet() {
        return inputVerbConceptSet;
    }

    public Set<InputTermModel> getInputTermModelSet() {
        return inputModule.getInputTermModelSet();
    }

    public AutomaticDisAmbiguationAction getAutomaticDisAmbiguationAction() {
        return automaticDisAmbiguationAction;
    }

    // public class ShowConceptDescriptionAction extends AbstractAction {
    //
    // public ShowConceptDescriptionAction(String title) {
    // super(title);
    // }
    //
    // public void actionPerformed(ActionEvent e) {
    // EvalConcept evalConcept = (EvalConcept)
    // conceptSetJList.getSelectedValue();
    // if (evalConcept != null) {
    // conceptDescriptionFrame.setConcept(evalConcept.getConcept().getURI());
    // setInputConceptSet();
    // conceptDescriptionFrame.setInputConceptSet();
    // conceptDescriptionFrame.setVisible(true);
    // }
    // }
    // }

    class ConceptDescriptionFrame extends JFrame {

        private ConceptDescriptionUI conceptDescrptionPanel;

        ConceptDescriptionFrame() {
            setBounds(50, 50, 800, 600);
            conceptDescrptionPanel = new ConceptDescriptionUI();
            Container contentPane = getContentPane();
            contentPane.add(conceptDescrptionPanel);
        }

        public void setConcept(String id) {
            conceptDescrptionPanel.setConcept(id);
        }

        public void setInputConceptSet() {
            conceptDescrptionPanel.setInputConceptSet(inputConceptSet);
        }
    }

    public class AutomaticDisAmbiguationAction extends AbstractAction {

        private Set<String> termSet;

        public AutomaticDisAmbiguationAction(String title) {
            super(title);
        }

        private Map<Concept, EvalConcept> getConceptEvalConceptMap() {
            Map<Concept, EvalConcept> conceptEvalConceptMap = new HashMap<>();
            for (InputTermModel inputTermModel : perfectlyMatchedTermModelSet) {
                String inputTerm = inputTermModel.getMatchedTerm();
                for (Concept c : termConceptSetMap.get(inputTerm)) {
                    if (conceptEvalConceptMap.get(c) == null) {
                        conceptEvalConceptMap.put(c, new EvalConcept(c, 0));
                    }
                }
            }
            return conceptEvalConceptMap;
        }

        private void calcEvalValueUsingSpreadActivatingAlgorithm(int i, Concept c1,
                                                                 EvalConcept ec1, Object[] allDisambiguationCandidate,
                                                                 Map<Concept, EvalConcept> conceptEvalConceptMap) {
            for (int j = i + 1; j < allDisambiguationCandidate.length; j++) {
                c1 = (Concept) allDisambiguationCandidate[i];
                ec1 = conceptEvalConceptMap.get(c1);
                Concept c2 = (Concept) allDisambiguationCandidate[j];
                EvalConcept ec2 = conceptEvalConceptMap.get(c2);
                double ev = 0;
                if (OptionDialog.isCheckShortestSpreadActivation()) {
                    ev = CalcConceptDistanceUtil.getShortestConceptDistance(c1, c2);
                } else if (OptionDialog.isCheckLongestSpreadActivation()) {
                    ev = CalcConceptDistanceUtil.getLongestConceptDistance(c1, c2);
                } else if (OptionDialog.isCheckAverageSpreadActivation()) {
                    ev = CalcConceptDistanceUtil.getAverageConceptDistance(c1, c2);
                }
                if (0 < ev) {
                    ec1.setEvalValue(ec1.getEvalValue() + (1 / ev));
                    ec2.setEvalValue(ec2.getEvalValue() + (1 / ev));
                }
            }
        }

        private void calcEvalValueUsingSupSubSibConcepts(Concept c, EvalConcept ec) {
            int evalValue = 0;
            if (OptionDialog.isCheckSupConcepts()) {
                evalValue += cntRelevantSupConcepts(c);
            }
            if (OptionDialog.isCheckSubConcepts()) {
                evalValue += cntRelevantSubConcepts(c);
            }
            if (OptionDialog.isCheckSiblingConcepts()) {
                evalValue += cntRelevantSiblingConcepts(c);
            }
            ec.setEvalValue(ec.getEvalValue() + evalValue);
        }

        /**
         * 多義性のある概念リストと入力語彙を入力として，評価値つき概念リストを返すメソッド
         */
        public void setTermEvalConceptSetMap() {
            if (inputTermModelSet == null) {
                return;
            }
            termSet = new HashSet<>();
            for (InputTermModel iwModel : perfectlyMatchedTermModelSet) {
                termSet.add(iwModel.getTerm());
            }
            termEvalConceptSetMap = new HashMap<>();
            Map<Concept, EvalConcept> conceptEvalConceptMap = getConceptEvalConceptMap();

            DODDLE_OWL.STATUS_BAR.setLastMessage(Translator
                    .getTerm("AutomaticInputConceptSelectionAction"));
            DODDLE_OWL.STATUS_BAR.startTime();
            DODDLE_OWL.STATUS_BAR.initNormal(conceptEvalConceptMap.keySet().size());
            Object[] allDisambiguationCandidate = conceptEvalConceptMap.keySet().toArray();
            for (int i = 0; i < allDisambiguationCandidate.length; i++) {
                Concept c = (Concept) allDisambiguationCandidate[i];
                EvalConcept ec = conceptEvalConceptMap.get(c);
                if (OptionDialog.isUsingSpreadActivatingAlgorithm()) {
                    calcEvalValueUsingSpreadActivatingAlgorithm(i, c, ec,
                            allDisambiguationCandidate, conceptEvalConceptMap);
                }
                calcEvalValueUsingSupSubSibConcepts(c, ec);
                DODDLE_OWL.STATUS_BAR.setLastMessage(Translator
                        .getTerm("AutomaticInputConceptSelectionAction")
                        + " ("
                        + (i + 1)
                        + "/"
                        + allDisambiguationCandidate.length + ")");
                DODDLE_OWL.STATUS_BAR.addValue();
            }

            for (InputTermModel inputTermModel : perfectlyMatchedTermModelSet) {
                String inputTerm = inputTermModel.getMatchedTerm();
                Set<Concept> conceptSet = termConceptSetMap.get(inputTerm);
                Set<EvalConcept> evalConceptSet = new TreeSet<>();
                for (Concept c : conceptSet) {
                    if (conceptEvalConceptMap.get(c) != null) {
                        evalConceptSet.add(conceptEvalConceptMap.get(c));
                    }
                }
                evalConceptSet.add(nullEvalConcept);
                termEvalConceptSetMap.put(inputTerm, evalConceptSet);
            }
            DODDLE_OWL.STATUS_BAR.hideProgressBar();
        }

        private Set<Set<String>> getSiblingConceptSet(Concept c) {
            Set<Set<String>> siblingConceptSet = null;
            if (c.getNameSpace().equals(DODDLEConstants.EDR_URI)) {
                siblingConceptSet = EDRTree.getEDRTree().getSiblingURISet(c.getURI());
            } else if (c.getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
                siblingConceptSet = EDRTree.getEDRTTree().getSiblingURISet(c.getURI());
            } else if (c.getNameSpace().equals(DODDLEConstants.WN_URI)) {
                siblingConceptSet = WordNetDic.getSiblingConceptSet(Long.valueOf(c.getLocalName()));
            }
            return siblingConceptSet;
        }

        private Set<Set<String>> getSubConceptSet(Concept c) {
            Set<Set<String>> subConceptSet = null;
            if (c.getNameSpace().equals(DODDLEConstants.EDR_URI)) {
                subConceptSet = EDRTree.getEDRTree().getSubURISet(c.getURI());
            } else if (c.getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
                subConceptSet = EDRTree.getEDRTTree().getSubURISet(c.getURI());
            } else if (c.getNameSpace().equals(DODDLEConstants.WN_URI)) {
                subConceptSet = WordNetDic.getSubIDSet(Long.valueOf(c.getLocalName()));
            }
            return subConceptSet;
        }

        private Set<List<String>> getPathToRootSet(Concept c) {
            Set<List<String>> pathSet = null;
            if (c.getNameSpace().equals(DODDLEConstants.EDR_URI)) {
                pathSet = EDRTree.getEDRTree().getURIPathToRootSet(c.getLocalName());
            } else if (c.getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
                pathSet = EDRTree.getEDRTTree().getURIPathToRootSet(c.getLocalName());
            } else if (c.getNameSpace().equals(DODDLEConstants.WN_URI)) {
                pathSet = WordNetDic.getURIPathToRootSet(Long.valueOf(c.getLocalName()));
            } else if (c.getNameSpace().equals(DODDLEConstants.JPN_WN_URI)) {
                pathSet = JPNWNTree.getJPNWNTree().getURIPathToRootSet(c.getLocalName());
            }
            return pathSet;
        }

        private int getMaxEvalValue(Set<Collection<String>> pathSet, String cid) {
            if (pathSet == null) {
                return 0;
            }
            int maxEvalValue = 0;
            for (Collection<String> path : pathSet) {
                int evalValue = 0;
                for (String uri : path) {
                    if (uri == null) {
                        continue;
                    }
                    String id = Utils.getLocalName(ResourceFactory.createResource(uri));
                    if (id.equals(cid)) {
                        continue;
                    }
                    Concept c = DODDLEDic.getConcept(uri);
                    // System.out.println(c);
                    if (isIncludeInputTerms(termSet, c)) {
                        evalValue++;
                    }
                }
                if (maxEvalValue < evalValue) {
                    maxEvalValue = evalValue;
                }
            }
            return maxEvalValue;
        }

        private int cntRelevantSiblingConcepts(Concept c) {
            Set pathSet = getSiblingConceptSet(c);
            return getMaxEvalValue(pathSet, c.getLocalName());
        }

        private int cntRelevantSupConcepts(Concept c) {
            Set pathSet = getPathToRootSet(c);
            return getMaxEvalValue(pathSet, c.getLocalName());
        }

        private int cntRelevantSubConcepts(Concept c) {
            Set pathSet = getSubConceptSet(c);
            return getMaxEvalValue(pathSet, c.getLocalName());
        }

        private boolean isIncludeInputTerms(Set<String> termSet, Concept c) {
            if (c == null) {
                return false;
            }
            Map<String, List<DODDLELiteral>> langLabelListMap = c.getLangLabelListMap();
            for (List<DODDLELiteral> labelList : langLabelListMap.values()) {
                for (DODDLELiteral label : labelList) {
                    if (termSet.contains(label.getString())) {
                        return true;
                    }
                }
            }
            Map<String, List<DODDLELiteral>> langDescriptionListMap = c.getLangDescriptionListMap();
            for (List<DODDLELiteral> descriptionList : langDescriptionListMap.values()) {
                for (DODDLELiteral description : descriptionList) {
                    if (termSet.contains(description.getString())) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void doAutomaticDisambiguation() {
            SwingWorker<String, String> worker = new SwingWorker<>() {
                public String doInBackground() {
                    makeEDRTree();
                    setTermEvalConceptSetMap();
                    return "done";
                }
            };
            DODDLE_OWL.STATUS_BAR.setSwingWorker(worker);
            worker.execute();
        }

        public void doDisambiguationTest() {
            new Thread(() -> {
                OptionDialog.setUsingSpreadActivationAlgorithmForDisambiguation(false);
                boolean[][] pattern = {{true, false, false}, {false, true, false},
                        {false, false, true}, {true, true, false}, {true, false, true},
                        {false, true, true}, {true, true, true}};
                String[] patternName = {"Sup.txt", "Sub.txt", "Sib.txt", "Sup_Sub.txt",
                        "Sup_Sib.txt", "Sib_Sub.txt", "Sup_Sub_Sib.txt"};

                for (int i = 0; i < pattern.length; i++) {
                    System.out.println(pattern[i][0] + " " + pattern[i][1] + " "
                            + pattern[i][2]);
                    OptionDialog.setSupDisambiguation(pattern[i][0]);
                    OptionDialog.setSubDisambiguation(pattern[i][1]);
                    OptionDialog.setSiblingDisambiguation(pattern[i][2]);
                    setTermEvalConceptSetMap();
                    saveTermEvalConceptSet(new File(patternName[i]));
                }
                OptionDialog.setSupDisambiguation(false);
                OptionDialog.setSubDisambiguation(false);
                OptionDialog.setSiblingDisambiguation(false);
                OptionDialog.setUsingSpreadActivationAlgorithmForDisambiguation(true);

                OptionDialog.setShortestSpreadActivatingAlgorithmforDisambiguation(true);
                System.out.println("shortest");
                setTermEvalConceptSetMap();
                saveTermEvalConceptSet(new File("shortest.txt"));

                OptionDialog.setLongestSpreadActivatingAlgorithmforDisambiguation(true);
                System.out.println("longest");
                setTermEvalConceptSetMap();
                saveTermEvalConceptSet(new File("longest.txt"));

                OptionDialog.setAverageSpreadActivatingAlgorithmforDisambiguation(true);
                System.out.println("average");
                setTermEvalConceptSetMap();
                saveTermEvalConceptSet(new File("average.txt"));

            }).start();
        }

        public void actionPerformed(ActionEvent e) {
            doAutomaticDisambiguation();
        }
    }

    public void showAllTerm() {
        JFrame frame = new JFrame();
        Set termSet = new TreeSet();
        if (inputTermModelSet != null) {
            for (InputTermModel iwModel : inputTermModelSet) {
                termSet.add(iwModel.getTerm());
            }
            termSet.addAll(inputModule.getUndefinedTermSet());
            JList list = new JList();
            list.setBorder(BorderFactory.createTitledBorder("入力されたすべての単語(" + termSet.size() + ")"));
            list.setListData(termSet.toArray());
            JScrollPane listScroll = new JScrollPane(list);
            frame.getContentPane().add(listScroll);
            frame.setBounds(50, 50, 200, 600);
            frame.setVisible(true);
        }
    }

    public void saveConstructTreeOption(File file) {
        BufferedWriter writer = null;
        try {
            Properties properties = new Properties();
            properties.setProperty("ConstructTree.isTreeConstruction",
                    String.valueOf(perfectlyMatchedOptionPanel.isConstruction()));
            properties.setProperty("ConstructTree.isTrimmingInternalNode",
                    String.valueOf(perfectlyMatchedOptionPanel.isTrimming()));
            properties.setProperty("ConstructTree.isConstructionWithCompoundWordTree",
                    String.valueOf(partiallyMatchedOptionPanel.isConstruction()));
            properties.setProperty("ConstructTree.isTrimmingInternalNodeWithCompoundWordTree",
                    String.valueOf(partiallyMatchedOptionPanel.isTrimming()));
            properties.setProperty("ConstructTree.isAddAbstractConceptWithCompoundWordTree",
                    String.valueOf(partiallyMatchedOptionPanel.isAddAbstractConcept()));
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            properties.store(writer, "Construct Tree Option");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void insertConstructTreeOption() {

    }

    public void loadConstructTreeOption(File file) {
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            Properties properties = new Properties();
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            properties.load(reader);
            boolean t = Boolean.valueOf(properties.getProperty("ConstructTree.isTreeConstruction"));
            perfectlyMatchedOptionPanel.setConstruction(t);
            t = Boolean.valueOf(properties.getProperty("ConstructTree.isTrimmingInternalNode"));
            perfectlyMatchedOptionPanel.setTrimming(t);
            t = Boolean.valueOf(properties.getProperty("ConstructTree.isConstructionWithCompoundWordTree"));
            partiallyMatchedOptionPanel.setConstruction(t);
            t = Boolean.valueOf(properties.getProperty("ConstructTree.isTrimmingInternalNodeWithCompoundWordTree"));
            partiallyMatchedOptionPanel.setTrimming(t);
            t = Boolean.valueOf(properties.getProperty("ConstructTree.isAddAbstractConceptWithCompoundWordTree"));
            partiallyMatchedOptionPanel.setAddAbstractConcept(t);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void saveInputTermConstructTreeOptionSet(File file) {
        if (compoundConstructTreeOptionMap == null) {
            return;
        }
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            StringBuilder buf = new StringBuilder();
            for (InputTermModel iwModel : compoundConstructTreeOptionMap.keySet()) {
                ConstructTreeOption ctOption = compoundConstructTreeOptionMap.get(iwModel);
                if (iwModel != null && ctOption != null && ctOption.getConcept() != null) {
                    buf.append(iwModel.getTerm() + "\t" + ctOption.getConcept().getURI() + "\t"
                            + ctOption.getOption() + "\n");
                }
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void insertInputTermConstructTreeOptionSet(int projectID, Statement stmt,
                                                      String inputTerm, String concept, String option) {
        try {
            String sql = "INSERT INTO input_term_construct_tree_option (Project_ID,Input_Term,Input_Concept,Tree_Option) "
                    + "VALUES("
                    + projectID
                    + ",'"
                    + URLEncoder.encode(inputTerm, StandardCharsets.UTF_8)
                    + "','"
                    + concept + "','" + option + "')";
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void loadInputTermConstructTreeOptionSet(File file) {
        if (!file.exists()) {
            return;
        }
        compoundConstructTreeOptionMap = new HashMap<>();
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while (reader.ready()) {
                String line = reader.readLine();
                String[] strs = line.split("\t");
                String iw = strs[0];
                String uri = strs[1];
                String opt = strs[2];
                if (0 < iw.length()) {
                    InputTermModel iwModel = inputModule.makeInputTermModel(iw);
                    compoundConstructTreeOptionMap.put(iwModel,
                            new ConstructTreeOption(DODDLEDic.getConcept(uri), opt));
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void loadInputTermConstructTreeOptionSet(int projectID, Statement stmt) {
        compoundConstructTreeOptionMap = new HashMap<>();
        try {
            String sql = "SELECT * from input_term_construct_tree_option where Project_ID="
                    + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String inputTerm = URLDecoder.decode(rs.getString("Input_Term"), StandardCharsets.UTF_8);
                String inputConcept = rs.getString("Input_Concept");
                String treeOption = rs.getString("Tree_Option");
                if (0 < inputTerm.length()) {
                    InputTermModel iwModel = inputModule.makeInputTermModel(inputTerm);
                    compoundConstructTreeOptionMap
                            .put(iwModel,
                                    new ConstructTreeOption(DODDLEDic.getConcept(inputConcept),
                                            treeOption));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public InputTermModel makeInputTermModel(String iw) {
        return inputModule.makeInputTermModel(iw.replaceAll("_", " "));
    }

    public void saveInputTermSet(File file) {
        if (inputTermModelSet == null) {
            return;
        }
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            StringBuilder buf = new StringBuilder();
            for (InputTermModel iwModel : inputTermModelSet) {
                if (!iwModel.isSystemAdded()) {
                    buf.append(iwModel.getTerm());
                    buf.append("\n");
                }
            }
            for (String inputTerm : inputModule.getUndefinedTermSet()) {
                buf.append(inputTerm);
                buf.append("\n");
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void insertInputTerm(int projectID, Statement stmt, String inputTerm) {
        try {
            String sql = "INSERT INTO input_term_set (Project_ID,Input_Term) " + "VALUES("
                    + projectID + ",'" + URLEncoder.encode(inputTerm, StandardCharsets.UTF_8) + "')";
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void loadUndefinedTermSet(File file) {
        if (!file.exists()) {
            return;
        }
        DefaultListModel undefinedTermListModel = undefinedTermListPanel.getModel();
        undefinedTermListModel.clear();
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line != null) {
                    undefinedTermListModel.addElement(line);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        constructClassPanel.setUndefinedTermListModel(undefinedTermListModel);
        constructPropertyPanel.setUndefinedTermListModel(undefinedTermListModel);
    }

    public void loadUndefinedTermSet(int projectID, Statement stmt) {
        DefaultListModel undefinedTermListModel = undefinedTermListPanel.getModel();
        undefinedTermListModel.clear();
        try {
            String sql = "SELECT * from undefined_term_set where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String term = URLDecoder.decode(rs.getString("Term"), StandardCharsets.UTF_8);
                if (term != null) {
                    undefinedTermListModel.addElement(term);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        constructClassPanel.setUndefinedTermListModel(undefinedTermListModel);
        constructPropertyPanel.setUndefinedTermListModel(undefinedTermListModel);
    }

    public void loadInputConceptSet(File file) {
        if (!file.exists()) {
            return;
        }
        inputConceptSet = new HashSet<>();
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while (reader.ready()) {
                String uri = reader.readLine();
                Concept c = DODDLEDic.getConcept(uri);
                if (c != null) {
                    inputConceptSet.add(c);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void loadInputConceptSet(int projectID, Statement stmt) {
        inputConceptSet = new HashSet<>();
        try {
            String sql = "SELECT * from input_concept_set where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String inputConcept = rs.getString("Input_Concept");
                Concept c = DODDLEDic.getConcept(inputConcept);
                if (c != null) {
                    inputConceptSet.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUndefinedTermSet(File file) {
        DefaultListModel undefinedTermListModel = undefinedTermListPanel.getModel();
        if (undefinedTermListModel == null) {
            return;
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < undefinedTermListModel.getSize(); i++) {
                String undefinedTerm = (String) undefinedTermListModel.getElementAt(i);
                buf.append(undefinedTerm);
                buf.append("\n");
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void saveInputConceptSet(File file) {
        if (inputConceptSet == null) {
            return;
        }
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            StringBuilder buf = new StringBuilder();
            for (Concept c : inputConceptSet) {
                if (!systemAddedInputConceptSet.contains(c)) {
                    buf.append(c.getURI());
                    buf.append("\n");
                }
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void insertInputConcept(int projectID, Statement stmt) {
        try {
            for (Concept c : inputConceptSet) {
                String sql = "INSERT INTO input_concept_set (Project_ID,Input_Concept) "
                        + "VALUES(" + projectID + ",'" + c.getURI() + "')";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void saveTermCorrespondConceptSetMap(File file) {
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            StringBuilder buf = new StringBuilder();
            for (Entry<String, Set<Concept>> stringSetEntry : getTermCorrespondConceptSetMap().entrySet()) {
                Entry entry = (Entry) stringSetEntry;
                String term = (String) entry.getKey();
                Set<Concept> conceptSet = (Set<Concept>) entry.getValue();
                buf.append(term + ",");
                for (Concept c : conceptSet) {
                    buf.append(c.getURI() + ",");
                }
                buf.append("\n");
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void insertInputTermConceptSetMap(int projectID, Statement stmt, String inputTerm,
                                             Set<Concept> conceptSet) {
        try {
            for (Concept c : conceptSet) {
                String sql = "INSERT INTO input_term_concept_map (Project_ID,Input_Term,Input_Concept) "
                        + "VALUES("
                        + projectID
                        + ",'"
                        + URLEncoder.encode(inputTerm, StandardCharsets.UTF_8)
                        + "','" + c.getURI() + "')";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void saveTermConceptMap() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showSaveDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            saveTermCorrespondConceptSetMap(file);
        }
    }

    public void saveTermEvalConceptSet(File file) {
        if (termEvalConceptSetMap == null || inputTermModelSet == null) {
            return;
        }
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            StringBuilder buf = new StringBuilder();
            for (InputTermModel iwModel : inputTermModelSet) {
                Set<EvalConcept> evalConceptSet = termEvalConceptSetMap.get(iwModel
                        .getMatchedTerm());
                if (evalConceptSet == null) {
                    continue;
                }
                buf.append(iwModel.getTerm());
                double evalValue = -1;
                for (EvalConcept ec : evalConceptSet) {
                    if (evalValue == ec.getEvalValue()) {
                        buf.append("\t" + ec.getConcept().getURI());
                    } else {
                        if (ec.getConcept() != null) {
                            buf.append("||" + ec.getEvalValue() + "\t" + ec.getConcept().getURI());
                            evalValue = ec.getEvalValue();
                        }
                    }
                }
                buf.append("\n");
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void savePerfectlyMatchedTerm() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showSaveDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            savePerfectlyMatchedTerm(file);
        }
    }

    private void savePerfectlyMatchedTerm(File file) {
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            for (InputTermModel iwModel : perfectlyMatchedTermModelSet) {
                writer.write(iwModel.getTerm() + "\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void savePerfectlyMatchedTermWithCompoundWord() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showSaveDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            savePerfectlyMatchedTermWithCompoundWord(file);
        }
    }

    private void savePerfectlyMatchedTermWithCompoundWord(File file) {
        BufferedWriter writer = null;
        try {
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            Map perfectlyMatchedTermWithCompoundWordMap = new TreeMap();
            for (InputTermModel iwModel : inputTermModelSet) {
                if (perfectlyMatchedTermWithCompoundWordMap.get(iwModel.getMatchedTerm()) != null) {
                    Set compoundWordSet = (Set) perfectlyMatchedTermWithCompoundWordMap.get(iwModel
                            .getMatchedTerm());
                    compoundWordSet.add(iwModel.getTerm());
                    perfectlyMatchedTermWithCompoundWordMap.put(iwModel.getMatchedTerm(),
                            compoundWordSet);
                } else {
                    Set compoundWordSet = new TreeSet();
                    compoundWordSet.add(iwModel.getTerm());
                    perfectlyMatchedTermWithCompoundWordMap.put(iwModel.getMatchedTerm(),
                            compoundWordSet);
                }
            }

            StringBuilder buf = new StringBuilder();
            for (Object o1 : perfectlyMatchedTermWithCompoundWordMap.keySet()) {
                String matchedTerm = (String) o1;
                buf.append(matchedTerm + "=>");
                Set compoundWordSet = (Set) perfectlyMatchedTermWithCompoundWordMap
                        .get(matchedTerm);
                for (Object o : compoundWordSet) {
                    String compoundWord = (String) o;
                    buf.append(compoundWord + ",");
                }
                buf.append("\n");
            }
            writer.write(buf.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void saveTermEvalConceptSet() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showSaveDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            saveTermEvalConceptSet(file);
        }
    }

    public void loadTermEvalConceptSet() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showOpenDialog(DODDLE_OWL.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadTermEvalConceptSet(file);
        }
    }

    public void loadTermEvalConceptSet(File file) {
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            termEvalConceptSetMap = new HashMap<>();
            while (reader.ready()) {
                String line = reader.readLine();
                String[] termAndResults = line.split("\\|\\|");
                String term = termAndResults[0];
                Set<EvalConcept> evalConceptSet = new TreeSet<>();
                for (int i = 1; i < termAndResults.length; i++) {
                    String[] valueAndURIs = termAndResults[i].split("\t");
                    double value = Double.parseDouble(valueAndURIs[0]);
                    for (int j = 1; j < valueAndURIs.length; j++) {
                        String uri = valueAndURIs[j];
                        Concept c = DODDLEDic.getConcept(uri);
                        evalConceptSet.add(new EvalConcept(c, value));
                    }
                }
                evalConceptSet.add(nullEvalConcept);
                termEvalConceptSetMap.put(term, evalConceptSet);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void loadTermEvalConceptSet(int projectID, java.sql.Statement stmt) {
        try {
            termEvalConceptSetMap = new HashMap<>();
            String sql = "SELECT * from term_eval_concept_set where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            Map<Integer, String> termIDMap = new HashMap<>();
            while (rs.next()) {
                String term = URLDecoder.decode(rs.getString("Term"), StandardCharsets.UTF_8);
                int termID = rs.getInt("Term_ID");
                termIDMap.put(termID, term);
            }

            for (Entry<Integer, String> entry : termIDMap.entrySet()) {
                int termID = entry.getKey();
                String term = entry.getValue();
                sql = "SELECT * from eval_concept_set where Project_ID=" + projectID
                        + " and Term_ID=" + termID;
                rs = stmt.executeQuery(sql);
                Set<EvalConcept> evalConceptSet = new TreeSet<>();
                while (rs.next()) {
                    String concept = rs.getString("Concept");
                    double evalValue = rs.getDouble("Eval_Value");
                    Concept c = DODDLEDic.getConcept(concept);
                    evalConceptSet.add(new EvalConcept(c, evalValue));
                }
                evalConceptSet.add(nullEvalConcept);
                termEvalConceptSetMap.put(term, evalConceptSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}