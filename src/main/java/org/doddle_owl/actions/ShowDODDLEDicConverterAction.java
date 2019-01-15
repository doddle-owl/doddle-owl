package org.doddle_owl.actions;

import org.doddle_owl.utils.Utils;
import org.doddle_owl.views.DODDLEDicConverterUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowDODDLEDicConverterAction extends AbstractAction {

    private final String title;

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
