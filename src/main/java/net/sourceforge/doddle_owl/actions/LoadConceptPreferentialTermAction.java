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
import java.util.*;

import javax.swing.*;

import net.sourceforge.doddle_owl.*;
import net.sourceforge.doddle_owl.data.*;
import net.sourceforge.doddle_owl.ui.*;

/**
 * @author Takeshi Morita
 */
public class LoadConceptPreferentialTermAction extends AbstractAction {

    public LoadConceptPreferentialTermAction(String title) {
        super(title);
    }

    public void loadIDPreferentialTerm(DODDLEProject currentProject, File file) {
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

        if (!file.exists()) { return; }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            Map<String, String> idPreferentialTermMap = new HashMap<String, String>();
            while (reader.ready()) {
                String line = reader.readLine();
                String[] idInputWord = line.replaceAll("\n", "").split("\t");
                if (idInputWord.length == 2) {
                    idPreferentialTermMap.put(idInputWord[0], idInputWord[1]);
                }
            }
            constructClassPanel.loadIDPreferentialTerm(idPreferentialTermMap);
            constructPropertyPanel.loadIDPreferentialTerm(idPreferentialTermMap);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(DODDLEConstants.PROJECT_HOME);
        int retval = chooser.showOpenDialog(DODDLE_OWL.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) { return; }
        DODDLEProject currentProject = DODDLE_OWL.getCurrentProject();
        loadIDPreferentialTerm(currentProject, chooser.getSelectedFile());
    }
}
