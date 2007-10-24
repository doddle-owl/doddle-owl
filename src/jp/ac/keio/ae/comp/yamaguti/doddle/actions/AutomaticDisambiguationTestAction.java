/*
 * @(#)  2006/10/06
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class AutomaticDisambiguationTestAction extends AbstractAction{
    public AutomaticDisambiguationTestAction(String title) {
        super(title);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        currentProject.getInputConceptSelectionPanel().getAutomaticDisAmbiguationAction().doDisambiguationTest();
    }
}
