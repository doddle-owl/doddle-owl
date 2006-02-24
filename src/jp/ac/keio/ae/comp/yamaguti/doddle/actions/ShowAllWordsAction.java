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

public class ShowAllWordsAction extends AbstractAction {

    public ShowAllWordsAction(String title) {
        super(title);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        currentProject.getInputModuleUI().showAllWords();
    }
}
