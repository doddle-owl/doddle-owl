/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 * 
 * Copyright (C) 2004-2020 Takeshi Morita. All rights reserved.
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
import org.doddle_owl.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Takeshi Morita
 */
public class ShowOptionDialogAction extends AbstractAction {

    private final DODDLE_OWL doddle;

    public ShowOptionDialogAction(String title, DODDLE_OWL ddl) {
        super(title, Utils.getImageIcon("baseline_settings_black_18dp.png"));
        doddle = ddl;
    }

    public void actionPerformed(ActionEvent e) {
        doddle.getOptionDialog().setLocationRelativeTo(doddle);
        doddle.getOptionDialog().setVisible(true);
    }
}
