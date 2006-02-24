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
public class ExitAction extends AbstractAction {
    
    private DODDLE doddle;
    
    public ExitAction(String title, DODDLE ddl) {
        super(title);
        doddle = ddl;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
    }
    public void actionPerformed(ActionEvent e) {
        doddle.exit();
    }
}
