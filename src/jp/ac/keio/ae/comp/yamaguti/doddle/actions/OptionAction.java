/*
 * @(#)  2006/02/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class OptionAction extends AbstractAction {

    private DODDLE doddle;

    public OptionAction(String title, DODDLE ddl) {
        super(title, Utils.getImageIcon("cog.png"));
        doddle = ddl;
    }

    public void actionPerformed(ActionEvent e) {
        doddle.getOptionDialog().setLocationRelativeTo(doddle);
        doddle.getOptionDialog().setVisible(true);
    }
}
