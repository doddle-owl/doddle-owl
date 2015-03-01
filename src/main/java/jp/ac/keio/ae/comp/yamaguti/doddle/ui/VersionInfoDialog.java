/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2009 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class VersionInfoDialog extends JDialog implements HyperlinkListener {

    public VersionInfoDialog(Frame root, String title, ImageIcon icon) {
        super(root, title);
        setIconImage(icon.getImage());
        JEditorPane htmlPane = new JEditorPane();
        htmlPane.addHyperlinkListener(this);
        htmlPane.setEditable(false);
        htmlPane.setContentType("text/html; charset=utf-8");
        URL versionInfoURL = Utils.class.getClassLoader().getResource(Translator.RESOURCE_DIR + "version_info.html");
        try {
            htmlPane.setPage(versionInfoURL);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
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
