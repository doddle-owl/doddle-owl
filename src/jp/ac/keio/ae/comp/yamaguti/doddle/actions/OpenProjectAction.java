/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class OpenProjectAction extends AbstractAction {

    private File openDir;
    private String title;
    private DODDLE doddle;

    public OpenProjectAction(String title, DODDLE ddl) {
        super(title, Utils.getImageIcon("open.gif"));
        this.title = title;
        doddle = ddl;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
    }

    public String getTitle() {
        return title;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) { return; }
        openDir = chooser.getSelectedFile();
        new Thread() {
            public void run() {
                try {
                    DODDLE.STATUS_BAR.setLastMessage("プロジェクト復元完了");
                    DODDLE.STATUS_BAR.startTime();
                    DODDLE.STATUS_BAR.initNormal(17);
                    DODDLE.STATUS_BAR.lock();

                    DODDLEProject project = new DODDLEProject(openDir.getAbsolutePath(), DODDLE.projectMenu);
                    DODDLE.desktop.add(project);
                    project.toFront();
                    DODDLE.desktop.setSelectedFrame(project);
                    DODDLE.STATUS_BAR.addProjectValue();
                    DODDLEProject currentProject = (DODDLEProject) DODDLE.desktop.getSelectedFrame();
                    InputModuleUI inputModuleUI = currentProject.getInputModuleUI();
                    DocumentSelectionPanel docSelectionPanelI = currentProject.getDocumentSelectionPanel();
                    InputWordSelectionPanel inputWordSelectionPanel = currentProject.getInputWordSelectionPanel();
                    openDir.mkdir();
                    DODDLE.STATUS_BAR.addProjectValue();
                    doddle.loadBaseURI(new File(openDir, "projectInfo.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    doddle.getOptionDialog().loadOption(new File(openDir, "option.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    docSelectionPanelI.loadDocuments(openDir);
                    DODDLE.STATUS_BAR.addProjectValue();
                    inputWordSelectionPanel.loadWordInfoTable(new File(openDir, "WordInfoTable.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    File inputWordSetFile = new File(openDir, "InputWordSet.txt");
                    inputModuleUI.loadInputWordSet(inputWordSetFile);
                    DODDLE.STATUS_BAR.addProjectValue();
                    inputModuleUI.loadWordEvalConceptSet(new File(openDir, "wordEvalConceptSet.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    if (inputWordSetFile.exists()) {
                        inputModuleUI.loadWordConceptMap(new File(openDir, "InputWord_ID.txt"));

                        DODDLE.STATUS_BAR.addProjectValue();
                    }
                    inputModuleUI.loadConstructTreeOptionSet(new File(openDir, "InputWord_ConstructTreeOption.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    inputModuleUI.loadInputConceptSet(new File(openDir, "InputIDSet.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    inputModuleUI.loadUndefinedWordSet(new File(openDir, "UndefinedWordSet.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    doddle.loadOntology(currentProject, new File(openDir, "Ontology.owl"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    doddle.loadIDTypicalWord(currentProject, new File(openDir, "ID_TypicalWord.txt"));
                    DODDLE.STATUS_BAR.addProjectValue();
                    inputModuleUI.selectTopList();
                    DODDLE.STATUS_BAR.addProjectValue();
                    project.setVisible(true);
                    project.setMaximum(true);
                    DODDLE.STATUS_BAR.addProjectValue();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    DODDLE.STATUS_BAR.unLock();
                    DODDLE.STATUS_BAR.hideProgressBar();
                }
            }
        }.start();
    }
}
