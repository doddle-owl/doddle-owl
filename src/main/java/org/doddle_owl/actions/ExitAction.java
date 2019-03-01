/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 *
 * Copyright (C) 2004-2019 Yamaguchi Laboratory, Keio University. All rights reserved.
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author Takeshi Morita
 */
public class ExitAction extends AbstractAction {

    private final DODDLE_OWL doddle;

    public ExitAction(String title, DODDLE_OWL ddl) {
        super(title, Utils.getImageIcon("ic_exit_to_app_18pt.png"));
        doddle = ddl;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }

    public void actionPerformed(ActionEvent e) {
        doddle.exit();
    }
}
