/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class SaveOntologyAction extends AbstractAction {

    private DODDLE doddle;

    public SaveOntologyAction(String title, DODDLE ddl) {
        super(title);
        doddle = ddl;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
        int retval = chooser.showSaveDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            DODDLEProject currentProject = DODDLE.getCurrentProject();
            doddle.saveOntology(currentProject, file);
        }
    }
}
