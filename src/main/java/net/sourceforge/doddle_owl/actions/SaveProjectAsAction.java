/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2015 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package net.sourceforge.doddle_owl.actions;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import net.sourceforge.doddle_owl.*;
import net.sourceforge.doddle_owl.utils.*;

/**
 * @author Takeshi Morita
 */
public class SaveProjectAsAction extends SaveProjectAction {

    public SaveProjectAsAction(String title, DODDLE_OWL ddl) {
        super(title, Utils.getImageIcon("page_save.png"), ddl);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK));
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE_OWL.getCurrentProject();
        if (currentProject == null) { return; }
        File saveFile = getSaveFile();
        if (saveFile != null) {
            saveProject(saveFile, currentProject);
        }
    }
}
