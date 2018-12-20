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

package org.doddle_owl.actions;

import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.models.DODDLEProjectFileFilter;
import org.doddle_owl.models.DODDLEProjectFolderFilter;
import org.doddle_owl.models.ProjectFileNames;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;
import org.doddle_owl.views.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Takeshi Morita
 */
public class SaveProjectAction extends AbstractAction {

    private String title;
    private DODDLE_OWL doddle;
    private FileFilter doddleProjectFileFilter;
    private FileFilter doddleProjectFolderFilter;

    public String getTitle() {
        return title;
    }

    public SaveProjectAction(String title, DODDLE_OWL ddl) {
        super(title, Utils.getImageIcon("disk.png"));
        this.title = title;
        doddle = ddl;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        doddleProjectFileFilter = new DODDLEProjectFileFilter();
        doddleProjectFolderFilter = new DODDLEProjectFolderFilter();
    }

    public SaveProjectAction(String title, ImageIcon icon, DODDLE_OWL ddl) {
        super(title, icon);
        this.title = title;
        doddle = ddl;
        doddleProjectFileFilter = new DODDLEProjectFileFilter();
        doddleProjectFolderFilter = new DODDLEProjectFolderFilter();
    }

    private void removeFiles(File dir) {
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    public void saveProject(File saveFile, DODDLEProject currentProject) {
        File saveDir = null;
        if (saveFile.isDirectory()) {
            saveDir = saveFile;
        } else {
            saveDir = new File(saveFile.getAbsolutePath() + ".dir");
            saveDir.mkdir();
        }
        ReferenceOntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        InputConceptSelectionPanel inputConceptSelectionPanel = currentProject.getInputConceptSelectionPanel();
        InputDocumentSelectionPanel docSelectionPanel = currentProject.getDocumentSelectionPanel();
        InputTermSelectionPanel inputTermSelectionPanel = currentProject.getInputTermSelectionPanel();
        ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();
        currentProject.setTitle(saveFile.getAbsolutePath());
        currentProject.setProjectName(saveFile.getAbsolutePath());
        ontSelectionPanel.saveGeneralOntologyInfo(new File(saveDir, ProjectFileNames.GENERAL_ONTOLOGY_INFO_FILE));
        File owlMetaDataSetDir = new File(saveDir, ProjectFileNames.OWL_META_DATA_SET_DIR);
        owlMetaDataSetDir.mkdir();
        removeFiles(owlMetaDataSetDir);
        ontSelectionPanel.saveOWLMetaDataSet(owlMetaDataSetDir);
        // docSelectionPanel.saveDocuments(saveDir); // ファイルの内容のコピーはしないようにした
        docSelectionPanel.saveDocumentInfo(saveDir);
        inputTermSelectionPanel.saveInputTermInfoTable(new File(saveDir, ProjectFileNames.TERM_INFO_TABLE_FILE),
                new File(saveDir, ProjectFileNames.REMOVED_TERM_INFO_TABLE_FILE));
        inputConceptSelectionPanel.saveInputTermSet(new File(saveDir, ProjectFileNames.INPUT_TERM_SET_FILE));
        inputConceptSelectionPanel
                .saveTermEvalConceptSet(new File(saveDir, ProjectFileNames.TERM_EVAL_CONCEPT_SET_FILE));
        inputConceptSelectionPanel.saveTermCorrespondConceptSetMap(new File(saveDir,
                ProjectFileNames.INPUT_TERM_CONCEPT_MAP_FILE));
        inputConceptSelectionPanel.saveConstructTreeOption(new File(saveDir,
                ProjectFileNames.CONSTRUCT_TREE_OPTION_FILE));
        inputConceptSelectionPanel.saveInputTermConstructTreeOptionSet(new File(saveDir,
                ProjectFileNames.INPUT_TERM_CONSTRUCT_TREE_OPTION_SET_FILE));
        inputConceptSelectionPanel.saveInputConceptSet(new File(saveDir, ProjectFileNames.INPUT_CONCEPT_SET_FILE));
        inputConceptSelectionPanel.saveUndefinedTermSet(new File(saveDir, ProjectFileNames.UNDEFINED_TERM_SET_FILE));
        doddle.saveOntology(currentProject, new File(saveDir, ProjectFileNames.ONTOLOGY_FILE));

        currentProject.getConstructClassPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(
                new File(saveDir, ProjectFileNames.CLASS_TRIMMED_RESULT_ANALYSIS_FILE));
        currentProject.getConstructPropertyPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(
                new File(saveDir, ProjectFileNames.PROPERTY_TRIMMED_RESULT_ANALYSIS_FILE));
        saveProjectInfo(currentProject, new File(saveDir, ProjectFileNames.PROJECT_INFO_FILE));
        conceptDefinitionPanel.saveConeptDefinitionParameters(new File(saveDir,
                ProjectFileNames.CONCEPT_DEFINITION_PARAMETERS_FILE));
        conceptDefinitionPanel.saveConceptDefinition(new File(saveDir, ProjectFileNames.CONCEPT_DEFINITION_FILE));
        conceptDefinitionPanel.saveWrongPairSet(new File(saveDir, ProjectFileNames.WRONG_PAIR_SET_FILE));
        File conceptDefinitionResultDir = new File(saveDir, ProjectFileNames.WORDSPACE_RESULTS_DIR);
        conceptDefinitionResultDir.mkdir();
        removeFiles(conceptDefinitionResultDir);
        conceptDefinitionPanel.saveWordSpaceResult(conceptDefinitionResultDir);
        conceptDefinitionResultDir = new File(saveDir, ProjectFileNames.APRIORI_RESULTS_DIR);
        conceptDefinitionResultDir.mkdir();
        removeFiles(conceptDefinitionResultDir);
        conceptDefinitionPanel.saveAprioriResult(conceptDefinitionResultDir);
        currentProject.addLog("SaveProjectAction");
        currentProject.saveLog(new File(saveDir, ProjectFileNames.LOG_FILE));
        if (!saveFile.isDirectory()) {
            zipProjectDir(saveFile, saveDir);
        }
        DODDLE_OWL.STATUS_BAR.setText(Translator.getTerm("SaveProjectDoneMessage") + " ----- "
                + java.util.Calendar.getInstance().getTime() + ": " + currentProject.getTitle());
    }

    public void saveProjectInfo(DODDLEProject currentProject, File file) {
        ReferenceOntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        InputConceptSelectionPanel inputConceptSelectionPanel = currentProject.getInputConceptSelectionPanel();
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

            StringBuffer buf = new StringBuffer();
            buf.append(DODDLEConstants.BASE_URI + "\n");

            buf.append(Translator.getTerm("AvailableGeneralOntologiesMessage") + ": "
                    + ontSelectionPanel.getEnableDicList() + "\n");
            if (inputConceptSelectionPanel.getInputTermModelSet() != null) {
                buf.append(Translator.getTerm("InputTermCountMessage") + ": "
                        + inputConceptSelectionPanel.getInputTermCnt() + "\n");
            }
            buf.append(Translator.getTerm("PerfectlyMatchedTermCountMessage") + ": "
                    + inputConceptSelectionPanel.getPerfectlyMatchedTermCnt() + "\n");
            buf.append(Translator.getTerm("SystemAddedPerfectlyMatchedTermCountMessage") + ": "
                    + inputConceptSelectionPanel.getSystemAddedPerfectlyMatchedTermCnt() + "\n");
            buf.append(Translator.getTerm("PartiallyMatchedTermCountMessage") + ": "
                    + inputConceptSelectionPanel.getPartiallyMatchedTermCnt() + "\n");
            buf.append(Translator.getTerm("MatchedTermCountMessage") + ": "
                    + inputConceptSelectionPanel.getMatchedTermCnt() + "\n");
            buf.append(Translator.getTerm("UndefinedTermCountMessage") + ": "
                    + inputConceptSelectionPanel.getUndefinedTermCnt() + "\n");

            if (inputConceptSelectionPanel.getInputConceptSet() != null) {
                buf.append(Translator.getTerm("InputConceptCountMessage") + ": "
                        + inputConceptSelectionPanel.getInputConceptSet().size() + "\n");
            }
            if (inputConceptSelectionPanel.getInputNounConceptSet() != null) {
                buf.append(Translator.getTerm("InputNounConceptCountMessage") + ": "
                        + inputConceptSelectionPanel.getInputNounConceptSet().size() + "\n");
            }
            if (inputConceptSelectionPanel.getInputVerbConceptSet() != null) {
                buf.append(Translator.getTerm("InputVerbConceptCountMessage") + ": "
                        + inputConceptSelectionPanel.getInputVerbConceptSet().size() + "\n");
            }

            buf.append(Translator.getTerm("ClassSINCountMessage") + ": " + constructClassPanel.getAddedSINNum() + "\n");
            buf.append(Translator.getTerm("BeforeTrimmingClassCountMessage") + ": "
                    + constructClassPanel.getBeforeTrimmingConceptNum() + "\n");
            buf.append(Translator.getTerm("TrimmedClassCountMessage") + ": "
                    + constructClassPanel.getTrimmedConceptNum() + "\n");
            int afterTrimmingConceptNum = constructClassPanel.getAfterTrimmingConceptNum();
            buf.append(Translator.getTerm("AfterTrimmingClassCountMessage") + ": " + afterTrimmingConceptNum + "\n");

            buf.append(Translator.getTerm("PropertySINCountMessage") + ": " + constructPropertyPanel.getAddedSINNum()
                    + "\n");
            buf.append(Translator.getTerm("BeforeTrimmingPropertyCountMessage") + ": "
                    + constructPropertyPanel.getBeforeTrimmingConceptNum() + "\n");
            buf.append(Translator.getTerm("TrimmedPropertyCountMessage") + ": "
                    + constructPropertyPanel.getTrimmedConceptNum() + "\n");
            int afterTrimmingPropertyNum = constructPropertyPanel.getAfterTrimmingConceptNum();
            buf
                    .append(Translator.getTerm("AfterTrimmingPropertyCountMessage") + ": " + afterTrimmingPropertyNum
                            + "\n");

            buf.append(Translator.getTerm("AbstractInternalClassCountMessage") + ": "
                    + constructClassPanel.getAddedAbstractCompoundConceptCnt() + "\n");
            buf.append(Translator.getTerm("AverageAbstractSiblingConceptCountInClassesMessage") + ": "
                    + constructClassPanel.getAverageAbstracCompoundConceptGroupSiblingConceptCnt() + "\n");

            buf.append(Translator.getTerm("AbstractInternalPropertyCountMessage") + ": "
                    + constructPropertyPanel.getAddedAbstractCompoundConceptCnt() + "\n");
            buf.append(Translator.getTerm("AverageAbstractSiblingConceptCountInPropertiesMessage") + ": "
                    + constructPropertyPanel.getAverageAbstracCompoundConceptGroupSiblingConceptCnt() + "\n");

            int lastClassNum = constructClassPanel.getAllConceptCnt();
            int lastPropertyNum = constructPropertyPanel.getAllConceptCnt();

            buf.append(Translator.getTerm("ClassFromCompoundWordCountMessage") + ": "
                    + (lastClassNum - afterTrimmingConceptNum) + "\n");
            buf.append(Translator.getTerm("PropertyFromCompoundWordCountMessage") + ": "
                    + (lastPropertyNum - afterTrimmingPropertyNum) + "\n");

            buf.append(Translator.getTerm("TotalClassCountMessage") + ": " + lastClassNum + "\n");
            buf.append(Translator.getTerm("TotalPropertyCountMessage") + ": " + lastPropertyNum + "\n");

            buf.append(Translator.getTerm("AverageSiblingClassesMessage") + ": "
                    + constructClassPanel.getChildCntAverage() + "\n");
            buf.append(Translator.getTerm("AverageSiblingPropertiesMessage") + ": "
                    + constructPropertyPanel.getChildCntAverage() + "\n");

            writer.write(buf.toString());
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
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

    private static final int EOF = -1;

    private void zipProjectDir(File saveFile, File saveDir) {
        ZipOutputStream zos = null;
        BufferedInputStream bis = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(saveFile));
            List<File> allFile = new ArrayList<File>();
            getAllProjectFile(saveDir, allFile);
            for (File file : allFile) {
                ZipEntry entry = new ZipEntry(file.getPath());
                zos.putNextEntry(entry);
                bis = new BufferedInputStream(new FileInputStream(file));
                int count;
                byte buf[] = new byte[1024];
                while ((count = bis.read(buf, 0, 104)) != EOF) {
                    zos.write(buf, 0, count);
                }
                bis.close();
                zos.closeEntry();
                file.delete();
            }
            for (File dir : saveDir.listFiles()) {
                dir.delete();
            }
            saveDir.delete();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (zos != null) {
                    zos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllProjectFile(File dir, List<File> allFile) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                getAllProjectFile(f, allFile);
            } else {
                allFile.add(f);
            }
        }
    }

    public File getSaveFile() {
        JFileChooser fileChooser = new JFileChooser(DODDLEConstants.PROJECT_HOME);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.addChoosableFileFilter(doddleProjectFileFilter);
        fileChooser.addChoosableFileFilter(doddleProjectFolderFilter);
        int retval = fileChooser.showSaveDialog(DODDLE_OWL.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) { return null; }
        String selectedFilterDescription = fileChooser.getFileFilter().getDescription();
        File saveFile = fileChooser.getSelectedFile();
        if (saveFile.isFile() && !saveFile.getName().endsWith(".ddl")) {
            saveFile = new File(saveFile.getAbsolutePath() + ".ddl");
        } else if (!saveFile.exists()) {
            if (selectedFilterDescription.equals(Translator.getTerm("DODDLEProjectFolderFilter"))) {
                saveFile.mkdir();
            } else if (selectedFilterDescription.equals(Translator.getTerm("DODDLEProjectFileFilter"))) {
                if (!saveFile.getName().endsWith(".ddl")) {
                    saveFile = new File(saveFile.getAbsolutePath() + ".ddl");
                }
            }
        }
        return saveFile;
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE_OWL.getCurrentProject();
        if (currentProject == null) { return; }
        File saveFile = null;
        if (!currentProject.getTitle().equals(Translator.getTerm("NewProjectAction"))) {
            saveFile = new File(currentProject.getTitle());
        } else {
            saveFile = getSaveFile();
        }
        if (saveFile != null) {
            saveProject(saveFile, currentProject);
        }
    }
}
