/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class SaveProjectAction extends AbstractAction {

    private String title;
    private DODDLE doddle;

    public String getTitle() {
        return title;
    }

    public SaveProjectAction(String title, DODDLE ddl) {
        super(title, Utils.getImageIcon("save.gif"));
        this.title = title;
        doddle = ddl;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        if (currentProject == null) { return; }
        File saveDir = null;
        if (!currentProject.getTitle().equals("新規プロジェクト")) {
            saveDir = new File(currentProject.getTitle());
        } else {
            JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int retval = chooser.showSaveDialog(DODDLE.rootPane);
            if (retval != JFileChooser.APPROVE_OPTION) { return; }
            saveDir = chooser.getSelectedFile();
        }
        doddle.saveProject(saveDir, currentProject);
    }
}
