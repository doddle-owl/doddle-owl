/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;

/**
 * @author takeshi morita
 */
public class LoadDescriptionsAction extends AbstractAction {

    public LoadDescriptionsAction(String title) {
        super(title);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) { return; }
        ConstructClassPanel classPanel = DODDLE.getCurrentProject().getConstructClassPanel();
        ConstructPropertyPanel propertyPanel = DODDLE.getCurrentProject().getConstructPropertyPanel();

        Map<String, DODDLELiteral> classWordDescriptionMap = new HashMap<String, DODDLELiteral>();
        Map<String, DODDLELiteral> propertyWordDescriptionMap = new HashMap<String, DODDLELiteral>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(chooser.getSelectedFile()), "UTF-8"));
            while (reader.ready()) {
                String line = reader.readLine();
                String[] lines = line.split("\t");
                if (lines.length == 4) {
                    String type = lines[0];
                    String lang = lines[1];
                    String word = lines[2];
                    String description = lines[3];
                    DODDLELiteral descriptionLiteral = new DODDLELiteral(lang, description);
                    if (type.equals("class")) {
                        classWordDescriptionMap.put(word, descriptionLiteral);
                    } else if (type.equals("property")) {
                        propertyWordDescriptionMap.put(word, descriptionLiteral);
                    }
                }
            }
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        classPanel.loadDescriptions(classWordDescriptionMap);
        propertyPanel.loadDescriptions(propertyWordDescriptionMap);
        DODDLE.STATUS_BAR.setText("Load Descriptions Done");
    }

}
