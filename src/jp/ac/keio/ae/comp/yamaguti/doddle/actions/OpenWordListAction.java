/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;

/**
 * @author takeshi morita
 */
public class OpenWordListAction extends AbstractAction {

    public OpenWordListAction(String title) {
        super(title);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        InputModuleUI inputModuleUI = currentProject.getInputModuleUI();

        JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            inputModuleUI.loadInputWordSet(chooser.getSelectedFile());
            DODDLE.setSelectedIndex(DODDLE.INPUT_MODULE);
        }
    }
}

