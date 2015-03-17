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

import java.awt.*;
import java.awt.event.*;
import java.lang.ref.*;

import javax.swing.*;

import net.sourceforge.doddle_owl.ui.*;
import net.sourceforge.doddle_owl.utils.*;

/**
 * @author Takeshi Morita
 */
public class ShowVersionInfoAction extends AbstractAction {

    private Frame rootFrame;
    private WeakReference<VersionInfoDialog> versionInfoDialogRef;
    private static final String TITLE = Translator.getTerm("VersionMenu");
    private static final ImageIcon ICON = Utils.getImageIcon("help.png");

    public ShowVersionInfoAction(Frame frame) {
        super(TITLE, ICON);
        rootFrame = frame;
        versionInfoDialogRef = new WeakReference<VersionInfoDialog>(null);
        setValues();
    }

    public String getTitle() {
        return TITLE;
    }

    private void setValues() {
        putValue(SHORT_DESCRIPTION, TITLE);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
    }

    public VersionInfoDialog getVersionInfoDialog() {
        VersionInfoDialog result = versionInfoDialogRef.get();
        if (result == null) {
            result = new VersionInfoDialog(rootFrame, TITLE, ICON);
            versionInfoDialogRef = new WeakReference<VersionInfoDialog>(result);
        }
        return result;
    }

    public void actionPerformed(ActionEvent e) {
        getVersionInfoDialog().setVisible(true);
    }
}
