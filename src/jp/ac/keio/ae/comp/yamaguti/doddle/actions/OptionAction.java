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
public class OptionAction extends AbstractAction {

    private DODDLE doddle;

    public OptionAction(String title, DODDLE ddl) {
        super(title);
        doddle = ddl;
    }

    public void actionPerformed(ActionEvent e) {
        doddle.getOptionDialog().setVisible(true);
    }
}
