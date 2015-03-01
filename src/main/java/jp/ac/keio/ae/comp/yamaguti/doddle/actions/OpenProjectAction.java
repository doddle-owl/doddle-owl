/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class OpenProjectAction extends AbstractAction {

    protected String title;
    protected DODDLE doddle;
    protected File openFile;
    protected DODDLEProject newProject;
    protected FileFilter doddleProjectFileFilter;
    protected FileFilter doddleProjectFolderFilter;

    public OpenProjectAction() {
    }

    public OpenProjectAction(String title, DODDLE ddl) {
        super(title, Utils.getImageIcon("folder_page_white.png"));
        this.title = title;
        doddle = ddl;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        doddleProjectFileFilter = new DODDLEProjectFileFilter();
        doddleProjectFolderFilter = new DODDLEProjectFolderFilter();
    }

    public String getTitle() {
        return title;
    }

    protected static final int EOF = -1;

    public static void getEntry(ZipFile zipFile, ZipEntry target) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(target.getName());
            if (target.isDirectory()) {
                file.mkdirs();
            } else {
                bis = new BufferedInputStream(zipFile.getInputStream(target));
                String parentName;
                if ((parentName = file.getParent()) != null) {
                    File dir = new File(parentName);
                    dir.mkdirs();
                }
                bos = new BufferedOutputStream(new FileOutputStream(file));
                int c;
                while ((c = bis.read()) != EOF) {
                    bos.write((byte) c);
                }
                bis.close();
                bos.close();
            }
        } catch (ZipException ze) {
            ze.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File unzipProjectDir(File openFile) {
        BufferedInputStream bis = null;
        File openDir = null;
        try {
            ZipFile projectFile = new ZipFile(openFile);
            ZipEntry entry = null;
            for (Enumeration enumeration = projectFile.entries(); enumeration.hasMoreElements();) {
                entry = (ZipEntry) enumeration.nextElement();
                getEntry(projectFile, entry);
            }
            openDir = new File(entry.getName()).getParentFile();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return openDir;
    }

    private void getAllProjectFile(File dir, List<File> allFile) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                getAllProjectFile(file, allFile);
            } else {
                allFile.add(file);
            }
        }
    }

    class OpenProjectWorker extends SwingWorker<String, String> implements PropertyChangeListener {

        private int currentTaskCnt;

        public OpenProjectWorker(int taskCnt) {
            currentTaskCnt = taskCnt;
            addPropertyChangeListener(this);
        }

        public String doInBackground() {
            while (!newProject.isInitialized()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            DODDLE.STATUS_BAR.setLastMessage(title);
            setProgress(currentTaskCnt++);
            try {
                DODDLEProject currentProject = newProject;

                ReferenceOntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
                InputConceptSelectionPanel inputConceptSelectionPanel = currentProject.getInputConceptSelectionPanel();
                InputDocumentSelectionPanel docSelectionPanelI = currentProject.getDocumentSelectionPanel();
                InputTermSelectionPanel inputTermSelectionPanel = currentProject.getInputTermSelectionPanel();
                ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();
                File openDir = null;
                List<String> recentProjects = doddle.loadRecentProject();
                recentProjects.remove(openFile.getAbsolutePath());
                recentProjects.add(0, openFile.getAbsolutePath());
                doddle.saveRecentProject(recentProjects);
                if (openFile.isDirectory()) {
                    openDir = openFile;
                } else {
                    openDir = unzipProjectDir(openFile);
                }
                DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenProjectAction") + ": " + openFile.toString());

                currentProject.loadLog(new File(openDir, ProjectFileNames.LOG_FILE));
                currentProject.addLog("OpenProjectAction");
                setProgress(currentTaskCnt++);
                doddle.loadBaseURI(new File(openDir, ProjectFileNames.PROJECT_INFO_FILE));
                setProgress(currentTaskCnt++);
                docSelectionPanelI.loadDocuments(openDir);
                setProgress(currentTaskCnt++);
                ontSelectionPanel
                        .loadGeneralOntologyInfo(new File(openDir, ProjectFileNames.GENERAL_ONTOLOGY_INFO_FILE));
                setProgress(currentTaskCnt++);
                ontSelectionPanel.loadOWLMetaDataSet(new File(openDir, ProjectFileNames.OWL_META_DATA_SET_DIR));
                setProgress(currentTaskCnt++);
                inputTermSelectionPanel.loadInputTermInfoTable(
                        new File(openDir, ProjectFileNames.TERM_INFO_TABLE_FILE), new File(openDir,
                                ProjectFileNames.REMOVED_TERM_INFO_TABLE_FILE));
                setProgress(currentTaskCnt++);
                File inputTermSetFile = new File(openDir, ProjectFileNames.INPUT_TERM_SET_FILE);
                inputConceptSelectionPanel.loadInputTermSet(inputTermSetFile, currentTaskCnt);
                setProgress(currentTaskCnt++);
                while (!inputConceptSelectionPanel.isLoadInputTermSet()) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenInputConceptSelectionResultAction"));
                inputConceptSelectionPanel.loadTermEvalConceptSet(new File(openDir,
                        ProjectFileNames.TERM_EVAL_CONCEPT_SET_FILE));
                setProgress(currentTaskCnt++);
                if (inputTermSetFile.exists()) {
                    DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenInputTermConceptMapAction"));
                    inputConceptSelectionPanel.loadTermCorrespondConceptSetMap(new File(openDir,
                            ProjectFileNames.INPUT_TERM_CONCEPT_MAP_FILE));
                }
                setProgress(currentTaskCnt++);
                inputConceptSelectionPanel.loadConstructTreeOption(new File(openDir,
                        ProjectFileNames.CONSTRUCT_TREE_OPTION_FILE));
                setProgress(currentTaskCnt++);
                inputConceptSelectionPanel.loadInputTermConstructTreeOptionSet(new File(openDir,
                        ProjectFileNames.INPUT_TERM_CONSTRUCT_TREE_OPTION_SET_FILE));
                setProgress(currentTaskCnt++);
                DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenInputConceptSetAction"));
                inputConceptSelectionPanel.loadInputConceptSet(new File(openDir,
                        ProjectFileNames.INPUT_CONCEPT_SET_FILE));
                setProgress(currentTaskCnt++);
                inputConceptSelectionPanel.loadUndefinedTermSet(new File(openDir,
                        ProjectFileNames.UNDEFINED_TERM_SET_FILE));
                setProgress(currentTaskCnt++);
                DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenOWLOntologyAction"));
                doddle.loadOntology(currentProject, new File(openDir, ProjectFileNames.ONTOLOGY_FILE));
                setProgress(currentTaskCnt++);

                ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
                ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
                constructClassPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(
                        new File(openDir, ProjectFileNames.CLASS_TRIMMED_RESULT_ANALYSIS_FILE));
                constructPropertyPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(
                        new File(openDir, ProjectFileNames.PROPERTY_TRIMMED_RESULT_ANALYSIS_FILE));

                conceptDefinitionPanel.setInputDocList();
                conceptDefinitionPanel.loadConceptDefinitionParameters(new File(openDir,
                        ProjectFileNames.CONCEPT_DEFINITION_PARAMETERS_FILE));
                setProgress(currentTaskCnt++);
                File conceptDefinitionResultDir = new File(openDir, ProjectFileNames.WORDSPACE_RESULTS_DIR);
                conceptDefinitionResultDir.mkdir();
                conceptDefinitionPanel.loadWordSpaceResult(conceptDefinitionResultDir);
                setProgress(currentTaskCnt++);
                conceptDefinitionResultDir = new File(openDir, ProjectFileNames.APRIORI_RESULTS_DIR);
                conceptDefinitionResultDir.mkdir();
                conceptDefinitionPanel.loadAprioriResult(conceptDefinitionResultDir);
                setProgress(currentTaskCnt++);

                conceptDefinitionPanel
                        .loadConceptDefinition(new File(openDir, ProjectFileNames.CONCEPT_DEFINITION_FILE));
                setProgress(currentTaskCnt++);
                conceptDefinitionPanel.loadWrongPairSet(new File(openDir, ProjectFileNames.WRONG_PAIR_SET_FILE));
                setProgress(currentTaskCnt++);

                inputConceptSelectionPanel.selectTopList();
                constructClassPanel.expandIsaTree();
                constructClassPanel.expandHasaTree();
                constructPropertyPanel.expandIsaTree();

                if (!openFile.isDirectory()) {
                    List<File> allFile = new ArrayList<File>();
                    getAllProjectFile(openDir, allFile);
                    for (File file : allFile) {
                        file.delete();
                    }
                    for (File dir : openDir.listFiles()) {
                        dir.delete();
                    }
                    openDir.delete();
                }
                setProgress(currentTaskCnt++);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    newProject.setXGALayout();
                    newProject.setVisible(true);
                    newProject.setMaximum(true);
                } catch (PropertyVetoException pve) {
                    pve.printStackTrace();
                }
                setProgress(currentTaskCnt++);
                DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("OpenProjectDoneMessage"));
                DODDLE.STATUS_BAR.setCurrentTime();
                DODDLE.STATUS_BAR.unLock();
                DODDLE.STATUS_BAR.hideProgressBar();
            }
            return "done";
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE.STATUS_BAR.setValue(currentTaskCnt);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFileChooser chooser = new JFileChooser(DODDLEConstants.PROJECT_HOME);
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.addChoosableFileFilter(doddleProjectFileFilter);
                chooser.addChoosableFileFilter(doddleProjectFolderFilter);
                int retval = chooser.showOpenDialog(DODDLE.rootPane);
                if (retval != JFileChooser.APPROVE_OPTION) { return; }
                openFile = chooser.getSelectedFile();
                newProject = new DODDLEProject(openFile.getAbsolutePath(), 32);
                OpenProjectWorker worker = new OpenProjectWorker(11);
                DODDLE.STATUS_BAR.setSwingWorker(worker);
                worker.execute();
            }
        });
    }
}
