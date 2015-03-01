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

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class LayoutDockingWindowAction extends AbstractAction{

    private String layoutType;
    public static String XGA_LAYOUT = "XGA_LAYOUT";
    public static String UXGA_LAYOUT = "UXGA_LAYOUT";
        
    public LayoutDockingWindowAction(String type, String title) {
        super(title);
        layoutType = type;
    }
    
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DODDLEProject currentProject = DODDLE.getCurrentProject();
                if (currentProject == null) {
                    return;
                }
                if (layoutType.equals(XGA_LAYOUT)) {
                    currentProject.setXGALayoutForAll();
                } else if (layoutType.equals(UXGA_LAYOUT)) {
                    currentProject.setUXGALayoutForAll();
                }
            }
        });
    }
}
