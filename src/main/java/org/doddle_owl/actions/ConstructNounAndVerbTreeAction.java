/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 * 
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
 * 
 * This file is part of DODDLE-OWL.
 * 
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.doddle_owl.actions;

import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.views.InputConceptSelectionPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Takeshi Morita
 */
public class ConstructNounAndVerbTreeAction extends AbstractAction {

    public ConstructNounAndVerbTreeAction() {
        super(Translator.getTerm("ClassAndPropertyTreeConstructionAction"));
    }

    public void actionPerformed(ActionEvent e) {
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {
            public String doInBackground() {
                DODDLE_OWL.STATUS_BAR.initNormal(10);
                DODDLE_OWL.STATUS_BAR.startTime();
                DODDLE_OWL.STATUS_BAR.printMessage(Translator.getTerm("ClassAndPropertyTreeConstructionAction"));
                InputConceptSelectionPanel inputConceptSelectionPanel = DODDLE_OWL.getCurrentProject()
                        .getInputConceptSelectionPanel();
                inputConceptSelectionPanel.makeEDRTree();
                DODDLE_OWL.STATUS_BAR.addValue();
                new ConstructTreeAction(true, DODDLE_OWL.getCurrentProject()).constructTree();
                DODDLE_OWL.getCurrentProject().addLog("ClassAndPropertyTreeConstructionAction");
                return "done";
            }
        };
        DODDLE_OWL.STATUS_BAR.setSwingWorker(worker);
        worker.execute();
    }
}
