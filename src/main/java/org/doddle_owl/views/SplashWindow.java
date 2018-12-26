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

import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.utils.Utils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * @author Takeshi Morita
 */
public class SplashWindow extends JWindow implements HyperlinkListener {

    public SplashWindow(Frame rootFrame) {
        ImageIcon logo = Utils.getImageIcon("doddle_splash.png");
        JLabel logoLabel = new JLabel(logo);
        JEditorPane editor = new JEditorPane("text/html", "");
        editor.addHyperlinkListener(this);
        editor.setEditable(false);
        StringBuilder builder = new StringBuilder();
        builder.append("<font face=TimesNewRoman>");
        builder.append("Name:  DODDLE-OWL  <br>");
        builder.append("Version: " + DODDLEConstants.VERSION + "<br>");
        builder.append("Copyright (C) 2004-2018 Yamaguchi Laboratory.<br>");
        builder.append("Contact: {t_morita, yamaguti}@ae.keio.ac.jp<br>");
        builder.append("License: GPL<br>");
        builder.append("Project Website: <a href=http://doddle-owl.org>http://doddle-owl.org/</a>");
        builder.append("</font>");
        editor.setText(builder.toString());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.black));
        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(editor, BorderLayout.EAST);
        getContentPane().add(panel);
        editor.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        pack();
        setLocationRelativeTo(rootFrame);
        setVisible(true);
    }

    public void hyperlinkUpdate(HyperlinkEvent ae) {
        try {
            if (ae.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop.getDesktop().browse(new URI("http://doddle-owl.org/"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SplashWindow(null);
    }
}
