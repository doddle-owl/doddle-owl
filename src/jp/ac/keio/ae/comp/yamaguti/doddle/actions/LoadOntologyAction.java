/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class LoadOntologyAction extends AbstractAction {

    private DODDLE doddle;

    public LoadOntologyAction(String title, DODDLE ddl) {
        super(title);
        doddle = ddl;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            DODDLEProject currentProject = DODDLE.getCurrentProject();
            doddle.loadOntology(currentProject, chooser.getSelectedFile());
        }
    }
}
