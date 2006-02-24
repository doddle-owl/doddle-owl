/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;

/**
 * @author takeshi morita
 */
public class SaveMatchedWordListAction extends AbstractAction {

    public SaveMatchedWordListAction(String title) {
        super(title);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        InputModuleUI inputModuleUI = currentProject.getInputModuleUI();

        JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
        int retval = chooser.showSaveDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                OutputStream os = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "SJIS"));
                for (Iterator i = inputModuleUI.getInputWordModelSet().iterator(); i.hasNext();) {
                    InputWordModel iwModel = (InputWordModel) i.next();
                    writer.write(iwModel.getWord() + "\n");
                }
                writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
