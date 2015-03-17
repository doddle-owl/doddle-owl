package net.sourceforge.doddle_owl.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sourceforge.doddle_owl.ui.*;
import net.sourceforge.doddle_owl.utils.*;

public class ShowDODDLEDicConverterAction extends AbstractAction {

    private String title;

    public ShowDODDLEDicConverterAction(String title) {
        super(title, Utils.getImageIcon("plugin.png"));
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEDicConverterUI converter = new DODDLEDicConverterUI();
        converter.setVisible(true);
    }
}
