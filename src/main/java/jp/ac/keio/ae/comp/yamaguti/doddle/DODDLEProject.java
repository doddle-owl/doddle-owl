/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)

 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2009 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package jp.ac.keio.ae.comp.yamaguti.doddle;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.infonode.docking.*;
import net.infonode.docking.util.*;

/**
 * @author Takeshi Morita
 */
public class DODDLEProject extends JInternalFrame implements ActionListener {

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
    private VisualizationPanel visualizationPanel;

    private int userIDCount;
    private Map<String, Concept> uriConceptMap;
    private List<String> logList;
    private UndoManager undoManager;

    private JCheckBoxMenuItem projectMenuItem;

    private View visualizationPanelView;

    class NewProjectWorker extends SwingWorker<String, String> implements PropertyChangeListener {

        private int taskCnt;
        private int currentTaskCnt;
        private DODDLEProject project;

        public NewProjectWorker(int taskCnt, DODDLEProject project) {
            this.taskCnt = taskCnt;
            currentTaskCnt = 1;
            this.project = project;
            addPropertyChangeListener(this);
            DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("NewProjectAction"));
            DODDLE.STATUS_BAR.startTime();
            DODDLE.STATUS_BAR.initNormal(taskCnt);
            DODDLE.STATUS_BAR.lock();
        }

        @Override
        protected String doInBackground() {
            try {
                ToolTipManager.sharedInstance().setEnabled(false);
                projectMenuItem = new JCheckBoxMenuItem(title);
                projectMenuItem.addActionListener(project);
                undoManager = new UndoManager(project);

                userIDCount = 0;
                uriConceptMap = new HashMap<String, Concept>();
                logList = new ArrayList<String>();
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
                if (DODDLE.getDODDLEPlugin() != null) {
                    visualizationPanel = new VisualizationPanel(project);
                }
                setProgress(currentTaskCnt++);
                inputConceptSelectionPanel.setDocumentSelectionPanel(docSelectionPanel);

                views = new View[7];
                ViewMap viewMap = new ViewMap();

                views[0] = new View(Translator.getTerm("OntologySelectionPanel"), Utils.getImageIcon("ontology.png"),
                        ontSelectionPanel);
                views[1] = new View(Translator.getTerm("DocumentSelectionPanel"), Utils
                        .getImageIcon("page_white_text.png"), docSelectionPanel);
                views[2] = new View(Translator.getTerm("InputTermSelectionPanel"), Utils
                        .getImageIcon("input_term_selection.png"), inputTermSelectinPanel);
                views[3] = new View(Translator.getTerm("InputConceptSelectionPanel"), Utils
                        .getImageIcon("disambiguation.png"), inputConceptSelectionPanel);
                views[4] = new View(Translator.getTerm("ClassTreeConstructionPanel"), Utils
                        .getImageIcon("class_tree.png"), constructClassPanel);
                views[5] = new View(Translator.getTerm("PropertyTreeConstructionPanel"), Utils
                        .getImageIcon("property_tree.png"), constructPropertyPanel);
                views[6] = new View(Translator.getTerm("ConceptDefinitionPanel"), Utils
                        .getImageIcon("non-taxonomic.png"), conceptDefinitionPanel);

                for (int i = 0; i < views.length; i++) {
                    viewMap.addView(i, views[i]);
                }
                if (DODDLE.getDODDLEPlugin() != null) {
                    visualizationPanelView = new View(Translator.getTerm("VisualizationPanel"), Utils
                            .getImageIcon("mr3_logo.png"), visualizationPanel);
                    viewMap.addView(7, visualizationPanelView);
                }

                rootWindow = Utils.createDODDLERootWindow(viewMap);
                getContentPane().add(rootWindow, BorderLayout.CENTER);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                addInternalFrameListener(new InternalFrameAdapter() {
                    public void internalFrameClosing(InternalFrameEvent e) {
                        int messageType = JOptionPane.showConfirmDialog(rootWindow, getTitle() + "\n"
                                + Translator.getTerm("ExitProjectMessage"), getTitle(), JOptionPane.YES_NO_OPTION);
                        if (messageType == JOptionPane.YES_OPTION) {
                            DODDLE.removeProjectMenuItem(projectMenuItem);
                            dispose();
                        }
                    }
                });
                setSize(800, 600);
                DODDLE.desktop.add(project);
                project.toFront();
                DODDLE.desktop.setSelectedFrame(project);
                DODDLE.addProjectMenuItem(projectMenuItem);
                setProgress(currentTaskCnt++);
                setXGALayoutForAll();
            } catch (NullPointerException npe) {
                System.out.println("Reset XGA Layout");
                setXGALayoutForAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                setProgress(currentTaskCnt++);
                isInitialized = true;
                if (taskCnt == 11) {
                    try {
                        project.setVisible(true); // かならず表示させるため
                        project.setMaximum(true); // setVisibleより前にしてしまうと，初期サイズ(800x600)で最大化されてしまう
                    } catch (PropertyVetoException pve) {
                        pve.printStackTrace();
                    }
                    DODDLE.STATUS_BAR.unLock();
                    DODDLE.STATUS_BAR.hideProgressBar();
                }
                ToolTipManager.sharedInstance().setEnabled(true);
            }
            return "done";
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE.STATUS_BAR.setValue(currentTaskCnt);
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
        if (logList == null) { return; }
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            for (String log : logList) {
                writer.write(log);
                writer.write("\n");
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

    public void loadLog(File file) {
        if (!file.exists()) { return; }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (reader.ready()) {
                String line = reader.readLine();
                logList.add(line);
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
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public DODDLEProject(String title, int taskCnt) {
        super(title, true, true, true, true);
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

    public JMenuItem getProjectMenuItem() {
        return projectMenuItem;
    }

    public void setXGALayout() {
        if (visualizationPanelView != null) {
            rootWindow.setWindow(new TabWindow(new DockingWindow[] { views[0], views[1], views[2], views[3], views[4],
                    views[5], views[6], visualizationPanelView}));
        } else {
            rootWindow.setWindow(new TabWindow(new DockingWindow[] { views[0], views[1], views[2], views[3], views[4],
                    views[5], views[6]}));
        }
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

    public void setProjectName(String name) {
        projectMenuItem.setText(name);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == projectMenuItem) {
            JMenu projectMenu = DODDLE.projectMenu;
            for (int i = 0; i < projectMenu.getItemCount(); i++) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) projectMenu.getItem(i);
                item.setSelected(false);
            }
            projectMenuItem.setSelected(true);
            toFront();
            try {
                setSelected(true);
            } catch (PropertyVetoException pve) {
                pve.printStackTrace();
            }
        }
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
        return "UID" + Integer.toString(userIDCount++);
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
