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
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.Concept;
import org.doddle_owl.models.InputTermModel;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.UndoManager;
import org.doddle_owl.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * @author Takeshi Morita
 */
public class DODDLEProjectPanel extends JPanel {

    private boolean isInitialized;

    private View[] views;
    private RootWindow rootWindow;
    private ReferenceOntologySelectionPanel ontSelectionPanel;
    private InputDocumentSelectionPanel docSelectionPanel;
    private InputTermSelectionPanel inputTermSelectinPanel;
    private InputConceptSelectionPanel inputConceptSelectionPanel;
    private ConstructClassPanel constructClassPanel;
    private ConstructPropertyPanel constructPropertyPanel;
    private ConceptDefinitionPanel conceptDefinitionPanel;

    private int userIDCount;
    private Map<String, Concept> uriConceptMap;
    private List<String> logList;
    private UndoManager undoManager;

    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;

    public void initProject() {
        ontSelectionPanel.initialize();
        docSelectionPanel.initialize();
        inputTermSelectinPanel.initialize();
        inputConceptSelectionPanel.initialize();
        constructClassPanel.initialize();
        constructPropertyPanel.initialize();
        conceptDefinitionPanel.initialize();
    }

    class NewProjectWorker extends SwingWorker<String, String> implements PropertyChangeListener {

        private int taskCnt;
        private int currentTaskCnt;
        private DODDLEProjectPanel project;

        public NewProjectWorker(int taskCnt, DODDLEProjectPanel project) {
            this.taskCnt = taskCnt;
            currentTaskCnt = 1;
            this.project = project;
            addPropertyChangeListener(this);
            DODDLE_OWL.STATUS_BAR.setLastMessage(Translator.getTerm("NewProjectAction"));
            DODDLE_OWL.STATUS_BAR.startTime();
            DODDLE_OWL.STATUS_BAR.initNormal(taskCnt);
            DODDLE_OWL.STATUS_BAR.lock();
        }

        @Override
        protected String doInBackground() {
            try {
                ToolTipManager.sharedInstance().setEnabled(false);
                undoManager = new UndoManager(project);

                userIDCount = 0;
                uriConceptMap = new HashMap<>();
                logList = new ArrayList<>();

                addLog("NewProjectAction");
                constructClassPanel = new ConstructClassPanel(project);
                setProgress(currentTaskCnt++);
                ontSelectionPanel = new ReferenceOntologySelectionPanel();
                setProgress(currentTaskCnt++);
                constructPropertyPanel = new ConstructPropertyPanel(project);
                setProgress(currentTaskCnt++);
                inputConceptSelectionPanel = new InputConceptSelectionPanel(constructClassPanel,
                        constructPropertyPanel, project);
                setProgress(currentTaskCnt++);

                inputTermSelectinPanel = new InputTermSelectionPanel(inputConceptSelectionPanel);
                setProgress(currentTaskCnt++);
                docSelectionPanel = new InputDocumentSelectionPanel(inputTermSelectinPanel, project);
                setProgress(currentTaskCnt++);
                conceptDefinitionPanel = new ConceptDefinitionPanel(project);
                setProgress(currentTaskCnt++);
                inputConceptSelectionPanel.setDocumentSelectionPanel(docSelectionPanel);

                views = new View[7];
                ViewMap viewMap = new ViewMap();

                views[0] = new View(Translator.getTerm("OntologySelectionPanel"),
                        Utils.getImageIcon("reference_ontology_selection.png"), ontSelectionPanel);
                views[1] = new View(Translator.getTerm("DocumentSelectionPanel"),
                        Utils.getImageIcon("input_document_selection.png"), docSelectionPanel);
                views[2] = new View(Translator.getTerm("InputTermSelectionPanel"),
                        Utils.getImageIcon("input_term_selection.png"), inputTermSelectinPanel);
                views[3] = new View(Translator.getTerm("InputConceptSelectionPanel"),
                        Utils.getImageIcon("input_concept_selection.png"), inputConceptSelectionPanel);
                views[4] = new View(Translator.getTerm("ClassTreeConstructionPanel"),
                        Utils.getImageIcon("constructing_class_hierarchy.png"), constructClassPanel);
                views[5] = new View(Translator.getTerm("PropertyTreeConstructionPanel"),
                        Utils.getImageIcon("constructing_property_hierarchy.png"), constructPropertyPanel);
                views[6] = new View(Translator.getTerm("ConceptDefinitionPanel"),
                        Utils.getImageIcon("concept_definition.png"), conceptDefinitionPanel);

                for (int i = 0; i < views.length; i++) {
                    viewMap.addView(i, views[i]);
                }

                rootWindow = Utils.createDODDLERootWindow(viewMap);

                setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                setProgress(currentTaskCnt++);
                setXGALayoutForAll();
                DODDLE_OWL.rootPane.getContentPane().add(rootWindow, BorderLayout.CENTER);
            } catch (NullPointerException npe) {
                setXGALayoutForAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setProgress(currentTaskCnt++);
                isInitialized = true;
                if (taskCnt == 11) {
                    project.setVisible(true); // かならず表示させるため
                    DODDLE_OWL.STATUS_BAR.unLock();
                    DODDLE_OWL.STATUS_BAR.hideProgressBar();
                }
                ToolTipManager.sharedInstance().setEnabled(true);
            }
            return "done";
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE_OWL.STATUS_BAR.setValue(currentTaskCnt);
            }
        }
    }

    private String getTerm(String msg) {
        String term = Translator.getTerm(msg);
        if (term == null) {
            term = msg;
        }
        return term;
    }

    public void addLog(String msg) {
        String log = Calendar.getInstance().getTime() + ": " + getTerm(msg);
        logList.add(log);
    }

    public void addLog(String msg, Object option) {
        String log = Calendar.getInstance().getTime() + ": " + getTerm(msg) + ": " + option;
        logList.add(log);
    }

    public void saveLog(File file) {
        if (logList == null) {
            return;
        }
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            try (writer) {
                for (String log : logList) {
                    writer.write(log);
                    writer.newLine();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadLog(File file) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            try (reader) {
                while (reader.ready()) {
                    String line = reader.readLine();
                    logList.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public DODDLEProjectPanel(int taskCnt) {
        NewProjectWorker worker = new NewProjectWorker(taskCnt, this);
        worker.execute();
    }

    public void initUndoManager() {
        undoManager.initUndoManager();
    }

    public void addCommand(Concept parentConcept, Concept targetConcept, String treeType) {
        undoManager.addCommand(parentConcept, targetConcept, treeType);
    }

    public void undo() {
        undoManager.undo();
    }

    public void redo() {
        undoManager.redo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public void setXGALayout() {
        rootWindow.setWindow(new TabWindow(new DockingWindow[]{views[0], views[1], views[2],
                views[3], views[4], views[5], views[6]}));
        views[0].restoreFocus();
    }

    public void setXGALayoutForAll() {
        setXGALayout();
        ontSelectionPanel.setXGALayout();
        docSelectionPanel.setXGALayout();
        inputTermSelectinPanel.setXGALayout();
        inputConceptSelectionPanel.setXGALayout();
        constructClassPanel.setXGALayout();
        constructPropertyPanel.setXGALayout();
        conceptDefinitionPanel.setXGALayout();
    }

    public void setUXGALayoutForAll() {
        setXGALayout();
        ontSelectionPanel.setUXGALayout();
        docSelectionPanel.setXGALayout();
        inputTermSelectinPanel.setUXGALayout();
        inputConceptSelectionPanel.setUXGALayout();
        constructClassPanel.setUXGALayout();
        constructPropertyPanel.setUXGALayout();
        conceptDefinitionPanel.setUXGALayout();
    }

    public void resetURIConceptMap() {
        uriConceptMap.clear();
    }

    public Set getAllConcept() {
        return uriConceptMap.keySet();
    }

    public void putConcept(String uri, Concept c) {
        uriConceptMap.put(uri, c);
    }

    public Concept getConcept(String uri) {
        return uriConceptMap.get(uri);
    }

    public void initUserIDCount() {
        userIDCount = 0;
    }

    public int getUserIDCount() {
        return userIDCount;
    }

    public String getUserIDStr() {
        return "UID" + (userIDCount++);
    }

    public void setUserIDCount(int id) {
        if (userIDCount < id) {
            userIDCount = id;
        }
    }

    public ReferenceOntologySelectionPanel getOntologySelectionPanel() {
        return ontSelectionPanel;
    }

    public InputDocumentSelectionPanel getDocumentSelectionPanel() {
        return docSelectionPanel;
    }

    public InputTermSelectionPanel getInputTermSelectionPanel() {
        return inputTermSelectinPanel;
    }

    public InputConceptSelectionPanel getInputConceptSelectionPanel() {
        return inputConceptSelectionPanel;
    }

    public InputTermModel makeInputTermModel(String iw) {
        return inputConceptSelectionPanel.makeInputTermModel(iw);
    }

    public ConstructPropertyPanel getConstructPropertyPanel() {
        return constructPropertyPanel;
    }

    public ConstructClassPanel getConstructClassPanel() {
        return constructClassPanel;
    }

    public ConceptDefinitionPanel getConceptDefinitionPanel() {
        return conceptDefinitionPanel;
    }

    public void setSelectedIndex(int i) {
        views[i].restoreFocus();
    }

    public boolean isPerfectlyMatchedAmbiguityCntCheckBox() {
        return inputConceptSelectionPanel.isPerfectlyMatchedAmbiguityCntCheckBox();
    }

    public boolean isPerfectlyMatchedSystemAddedWordCheckBox() {
        return inputConceptSelectionPanel.isPerfectlyMatchedSystemAddedTermCheckBox();
    }

    public boolean isPartiallyMatchedAmbiguityCntCheckBox() {
        return inputConceptSelectionPanel.isPartiallyMatchedAmbiguityCntCheckBox();
    }

    public boolean isPartiallyMatchedCompoundWordCheckBox() {
        return inputConceptSelectionPanel.isPartiallyMatchedCompoundWordCheckBox();
    }

    public boolean isPartiallyMatchedMatchedWordBox() {
        return inputConceptSelectionPanel.isPartiallyMatchedMatchedTermBox();
    }

}
