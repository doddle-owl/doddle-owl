package io.github.doddle_owl.actions;

import io.github.doddle_owl.utils.Utils;
import io.github.doddle_owl.views.DODDLEDictConverterUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowDODDLEDictConverterAction extends AbstractAction {

    private final String title;

    public ShowDODDLEDictConverterAction(String title) {
        super(title, Utils.getImageIcon("baseline_build_black_18dp.png"));
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEDictConverterUI converter = new DODDLEDictConverterUI();
        converter.setVisible(true);
    }
}
