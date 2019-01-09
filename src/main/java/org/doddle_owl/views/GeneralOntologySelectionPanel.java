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

import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.EDRDic;
import org.doddle_owl.models.JWODic;
import org.doddle_owl.models.JpnWordNetDic;
import org.doddle_owl.models.WordNetDic;
import org.doddle_owl.utils.Translator;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Takeshi Morita
 */
public class GeneralOntologySelectionPanel extends JPanel {
    private JCheckBox edrCheckBox;
    private JCheckBox edrtCheckBox;
    private JCheckBox wnCheckBox;
    private JCheckBox jpnWnCheckBox;
    private JCheckBox jwoCheckBox;

    private NameSpaceTable nameSpaceTable;

    public GeneralOntologySelectionPanel(NameSpaceTable nsTable) {
        nameSpaceTable = nsTable;
        wnCheckBox = new JCheckBox(Translator.getTerm("WordNetCheckBox"), false);
        wnCheckBox.addActionListener(e -> enableWordNetDic(wnCheckBox.isSelected()));
        edrCheckBox = new JCheckBox(Translator.getTerm("GenericEDRCheckBox"), false);
        edrCheckBox.addActionListener(e -> enableEDRDic(edrCheckBox.isSelected()));
        edrtCheckBox = new JCheckBox(Translator.getTerm("TechnicalEDRCheckBox"), false);
        edrtCheckBox.addActionListener(e -> enableEDRTDic(edrtCheckBox.isSelected()));
        jpnWnCheckBox = new JCheckBox(Translator.getTerm("JpnWordNetCheckBox"), false);
        jpnWnCheckBox.addActionListener(e -> enableJpnWordNetDic(jpnWnCheckBox.isSelected()));
        jwoCheckBox = new JCheckBox(Translator.getTerm("JWOCheckBox"), false);
        jwoCheckBox.addActionListener(e -> enableJWO(jwoCheckBox.isSelected()));
        JPanel checkPanel = new JPanel();
        checkPanel.add(wnCheckBox);
        checkPanel.add(jpnWnCheckBox);
        checkPanel.add(edrCheckBox);
        checkPanel.add(edrtCheckBox);
        checkPanel.add(jwoCheckBox);
        setLayout(new BorderLayout());
        add(checkPanel, BorderLayout.WEST);
    }

    public void saveGeneralOntologyInfo(File saveFile) {
        try {
            Properties properties = new Properties();
            properties.setProperty("EDR(general)", String.valueOf(isEDREnable()));
            properties.setProperty("EDR(technical)", String.valueOf(isEDRTEnable()));
            properties.setProperty("WordNet", String.valueOf(isWordNetEnable()));
            properties.setProperty("JPN WordNet", String.valueOf(isJpnWordNetEnable()));
            properties.setProperty("JWO", String.valueOf(isJWOEnable()));
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(saveFile.getAbsolutePath()), StandardCharsets.UTF_8);
            try (writer) {
                properties.store(writer, "Ontology Info");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGeneralOntologyInfo(File loadFile) {
        if (!loadFile.exists()) {
            return;
        }
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(loadFile.getAbsolutePath()), StandardCharsets.UTF_8);
            Properties properties = new Properties();
            try (reader) {
                properties.load(reader);
            }
            boolean t = Boolean.valueOf(properties.getProperty("EDR(general)"));
            edrCheckBox.setSelected(t);
            enableEDRDic(t);
            t = Boolean.valueOf(properties.getProperty("EDR(technical)"));
            edrtCheckBox.setSelected(t);
            enableEDRTDic(t);
            t = Boolean.valueOf(properties.getProperty("WordNet"));
            wnCheckBox.setSelected(t);
            enableWordNetDic(t);
            t = Boolean.valueOf(properties.getProperty("JPN WordNet"));
            jpnWnCheckBox.setSelected(t);
            enableJpnWordNetDic(t);
            t = Boolean.valueOf(properties.getProperty("JWO"));
            jwoCheckBox.setSelected(t);
            enableJWO(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEnableDicList() {
        StringBuilder builder = new StringBuilder();
        if (isEDREnable()) {
            builder.append("EDR一般辞書 ");
        }
        if (isEDRTEnable()) {
            builder.append("EDR専門辞書 ");
        }
        if (isWordNetEnable()) {
            builder.append("WordNet ");
        }
        if (isJpnWordNetEnable()) {
            builder.append("JPN WordNet ");
        }
        if (isJWOEnable()) {
            builder.append("JWO");
        }
        return builder.toString();
    }

    public boolean isEDREnable() {
        return edrCheckBox.isEnabled() && edrCheckBox.isSelected();
    }

    public boolean isEDRTEnable() {
        return edrtCheckBox.isEnabled() && edrtCheckBox.isSelected();
    }

    public boolean isWordNetEnable() {
        return wnCheckBox.isEnabled() && wnCheckBox.isSelected();
    }

    public boolean isJpnWordNetEnable() {
        return jpnWnCheckBox.isEnabled() && jpnWnCheckBox.isSelected();
    }

    public boolean isJWOEnable() {
        return jwoCheckBox.isEnabled() && jwoCheckBox.isSelected();
    }

    private void enableEDRDic(boolean isEnable) {
        if (isEnable) {
            edrCheckBox.setSelected(EDRDic.initEDRDic());
            edrCheckBox.setEnabled(EDRDic.isEDRAvailable);
            DODDLE_OWL.STATUS_BAR.addValue();
        } else {
            EDRDic.isEDRAvailable = false;
        }
        DODDLE_OWL.STATUS_BAR.setText("Init EDR: " + edrCheckBox.isSelected());
        DODDLE_OWL.getCurrentProject().addLog("Init EDR", edrCheckBox.isSelected());
    }

    private void enableEDRTDic(boolean isEnable) {
        if (isEnable) {
            edrtCheckBox.setSelected(EDRDic.initEDRTDic());
            edrtCheckBox.setEnabled(EDRDic.isEDRTAvailable);
        } else {
            EDRDic.isEDRTAvailable = false;
        }
        DODDLE_OWL.STATUS_BAR.setText("Init EDRT: " + edrtCheckBox.isSelected());
        DODDLE_OWL.getCurrentProject().addLog("Init EDRT", edrtCheckBox.isSelected());
    }

    private void enableWordNetDic(boolean isEnable) {
        if (isEnable) {
            wnCheckBox.setSelected(WordNetDic.initWordNetDictionary());
            wnCheckBox.setEnabled(WordNetDic.isAvailable);
        } else {
            WordNetDic.isAvailable = false;
        }
        DODDLE_OWL.STATUS_BAR.setText("Init WordNet: " + wnCheckBox.isSelected());
        DODDLE_OWL.getCurrentProject().addLog("Init WordNet", wnCheckBox.isSelected());
    }

    private void enableJpnWordNetDic(boolean isEnable) {
        if (isEnable) {
            jpnWnCheckBox.setSelected(JpnWordNetDic.initJPNWNDic());
            jpnWnCheckBox.setEnabled(JpnWordNetDic.isAvailable);
            DODDLE_OWL.STATUS_BAR.addValue();
        } else {
            JpnWordNetDic.isAvailable = false;
        }
        DODDLE_OWL.STATUS_BAR.setText("Init Japanese WordNet: " + jpnWnCheckBox.isSelected());
        DODDLE_OWL.getCurrentProject().addLog("Init Japanese WordNet", jpnWnCheckBox.isSelected());
    }

    private void enableJWO(boolean isEnable) {
        if (isEnable) {
            jwoCheckBox.setSelected(JWODic.initJWODic(nameSpaceTable));
            jwoCheckBox.setEnabled(JWODic.isAvailable);
        } else {
            JWODic.isAvailable = false;
        }
        DODDLE_OWL.STATUS_BAR.setText("Init JWO: " + jwoCheckBox.isSelected());
        DODDLE_OWL.getCurrentProject().addLog("Init JWO", jwoCheckBox.isSelected());
    }

    /**
     * オプションダイアログでパスを変更した場合は，再度，チェックできるようにする．
     */
    public void resetCheckBoxes() {
        edrCheckBox.setEnabled(true);
        edrtCheckBox.setEnabled(true);
        wnCheckBox.setEnabled(true);
        jpnWnCheckBox.setEnabled(true);
        jwoCheckBox.setEnabled(true);
    }
}
