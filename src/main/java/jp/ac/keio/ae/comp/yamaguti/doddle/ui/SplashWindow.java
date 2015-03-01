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
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
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
		builder.append("Last Update: " + DODDLEConstants.LAST_UPDATE + "<br>");
		builder.append("Copyright (C) 2004-2015 Yamaguchi Laboratory.<br>");
		builder.append("Contact: {t_morita, yamaguti}@ae.keio.ac.jp<br>");
		builder.append("License: GPL<br>");
		builder.append("Project Website: <a href=http://doddle-owl.sourceforge.net>http://doddle-owl.sourceforge.net/</a>");
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
				Desktop.getDesktop().browse(new URI("http://doddle-owl.sourceforge.net/"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new SplashWindow(null);
	}
}
