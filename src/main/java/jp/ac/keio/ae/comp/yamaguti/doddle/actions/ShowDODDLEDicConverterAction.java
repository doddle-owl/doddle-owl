package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jp.ac.keio.ae.comp.yamaguti.doddle.ui.DODDLEDicConverterUI;

public class ShowDODDLEDicConverterAction extends AbstractAction {

	public ShowDODDLEDicConverterAction(String title) {
		super(title);
	}

	public void actionPerformed(ActionEvent e) {
		DODDLEDicConverterUI converter = new DODDLEDicConverterUI();
		converter.setVisible(true);
	}
}
