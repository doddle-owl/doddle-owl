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

package org.doddle_owl.views;

import org.doddle_owl.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * @author Takeshi Morita
 */
public class VersionInfoDialog extends JDialog implements HyperlinkListener {

    public VersionInfoDialog(Frame root, String title, ImageIcon icon) {
        super(root, title);
        setIconImage(icon.getImage());
        JEditorPane htmlPane = new JEditorPane();
        htmlPane.addHyperlinkListener(this);
        htmlPane.setEditable(false);
        htmlPane.setContentType("text/html; charset=utf-8");
        URL versionInfoURL = Utils.class.getClassLoader().getResource("version_info.html");
        try {
            htmlPane.setPage(versionInfoURL);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> setVisible(false));
        setLayout(new BorderLayout());
        add(new JScrollPane(htmlPane), BorderLayout.CENTER);
        add(Utils.createEastPanel(okButton), BorderLayout.SOUTH);
        setSize(500, 400);
        setLocationRelativeTo(root);
        setVisible(true);
    }

    public void hyperlinkUpdate(HyperlinkEvent ae) {
        try {
            if (ae.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop.getDesktop().browse(ae.getURL().toURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
