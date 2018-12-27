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

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.log4j.Level;
import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.*;
import org.doddle_owl.utils.OWLOntologyManager;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class GeneralOntologySelectionPanel extends JPanel implements ActionListener, ChangeListener {
    private JCheckBox edrCheckBox;
    private JCheckBox edrtCheckBox;
    private JCheckBox wnCheckBox;
    private JCheckBox jpnWnCheckBox;
    private JCheckBox jwoCheckBox;

    private JRadioButton wn30RadioButton;
    private JRadioButton wn31RadioButton;
    private JPanel wnVersionSelectionPanel;

    private JLabel generalOntologyDirLabel;

    private NameSpaceTable nameSpaceTable;

    private Dataset dataset;

    public GeneralOntologySelectionPanel(NameSpaceTable nsTable) {
        nameSpaceTable = nsTable;
        edrCheckBox = new JCheckBox(Translator.getTerm("GenericEDRCheckBox"), false);
        edrCheckBox.addActionListener(this);
        edrtCheckBox = new JCheckBox(Translator.getTerm("TechnicalEDRCheckBox"), false);
        edrtCheckBox.addActionListener(this);
        wnCheckBox = new JCheckBox(Translator.getTerm("WordNetCheckBox"), false);
        wnCheckBox.addActionListener(this);
        wnVersionSelectionPanel = new JPanel();
        wn30RadioButton = new JRadioButton("3.0");
        wn30RadioButton.addChangeListener(this);
        wn31RadioButton = new JRadioButton("3.1");
        wn31RadioButton.setSelected(true);
        wn31RadioButton.addChangeListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(wn30RadioButton);
        group.add(wn31RadioButton);
        wnVersionSelectionPanel.add(wnCheckBox);
        wnVersionSelectionPanel.add(wn30RadioButton);
        wnVersionSelectionPanel.add(wn31RadioButton);
        JPanel borderPanel = new JPanel();
        borderPanel.setLayout(new BorderLayout());
        borderPanel.add(wnVersionSelectionPanel, BorderLayout.WEST);

        jpnWnCheckBox = new JCheckBox(Translator.getTerm("JpnWordNetCheckBox"), false);
        jpnWnCheckBox.addActionListener(this);
        jwoCheckBox = new JCheckBox(Translator.getTerm("JWOCheckBox"), false);
        jwoCheckBox.addActionListener(this);
        JPanel checkPanel = new JPanel();
        checkPanel.add(borderPanel);
        checkPanel.add(jpnWnCheckBox);
        checkPanel.add(jwoCheckBox);
        checkPanel.add(edrCheckBox);
        checkPanel.add(edrtCheckBox);
        setLayout(new BorderLayout());
        add(checkPanel, BorderLayout.WEST);
    }

    public void closeDataSet() {
        if (dataset != null) {
            dataset.close();
        }
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

    private void enableEDRDic(boolean t) {
        if (t) {
            boolean isInitEDRDic = EDRDic.initEDRDic();
            EDRTree edrTree = EDRTree.getEDRTree();
            boolean isInitEDRTree = (edrTree != null);
            edrCheckBox.setEnabled(isInitEDRDic && isInitEDRTree);
            if (!edrCheckBox.isEnabled()) {
                edrCheckBox.setSelected(false);
            }
            DODDLE_OWL.STATUS_BAR.addValue();
        }
    }

    private void enableEDRTDic(boolean t) {
        if (t) {
            edrtCheckBox.setEnabled(EDRDic.initEDRTDic());
            if (!edrtCheckBox.isEnabled()) {
                edrtCheckBox.setSelected(false);
            }
        }
    }

    private void enableWordNetDic(boolean t) {
        if (t) {
            wnCheckBox.setEnabled(true);
            if (!wnCheckBox.isEnabled()) {
                wnCheckBox.setSelected(false);
                WordNetDic.initWordNetDictionary();
            }
        }
    }

    private void enableJpnWordNetDic(boolean t) {
        if (t) {
            boolean isInitJPNWNDic = JpnWordNetDic.initJPNWNDic();
            JPNWNTree jpnWnTree = JPNWNTree.getJPNWNTree();
            boolean isInitJPNWNTree = (jpnWnTree != null);
            jpnWnCheckBox.setEnabled(isInitJPNWNDic && isInitJPNWNTree);
            if (!jpnWnCheckBox.isEnabled()) {
                jpnWnCheckBox.setSelected(false);
            }
            DODDLE_OWL.STATUS_BAR.addValue();
        }
    }

    private void enableJWO(boolean t) {
        if (t) {
            jwoCheckBox.setSelected(t);
        }
    }

    /**
     * オプションダイアログでパスを変更した場合は，再度，チェックできるようにする．
     */
    public void resetCheckBoxes() {
        edrCheckBox.setEnabled(true);
        edrtCheckBox.setEnabled(true);
        wnCheckBox.setEnabled(true);
        jpnWnCheckBox.setEnabled(true);
    }

    // 取り扱い注意メソッド
    private void deleteFile(File f) {
        if (!f.exists()) {
            return;
        }

        if (f.isFile()) {
            DODDLE_OWL.getLogger().log(Level.INFO, "Delete: " + f.getAbsolutePath());
            f.delete();
        }

        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                deleteFile(file);
            }
            DODDLE_OWL.getLogger().log(Level.INFO, "Delete: " + f.getAbsolutePath());
            f.delete();
        }
    }

    public void actionPerformed(ActionEvent e) {
        DODDLEProject project = DODDLE_OWL.getCurrentProject();
        if (e.getSource() == edrCheckBox) {
            enableEDRDic(edrCheckBox.isSelected());
            project.addLog("GenericEDRCheckBox", edrCheckBox.isSelected());
        } else if (e.getSource() == edrtCheckBox) {
            enableEDRTDic(edrtCheckBox.isSelected());
            project.addLog("TechnicalEDRCheckBox", edrtCheckBox.isSelected());
        } else if (e.getSource() == wnCheckBox) {
            enableWordNetDic(wnCheckBox.isSelected());
            wnVersionSelectionPanel.setEnabled(wnCheckBox.isSelected());
            project.addLog("WordNetCheckBox", wnCheckBox.isSelected());
        } else if (e.getSource() == jpnWnCheckBox) {
            enableJpnWordNetDic(jpnWnCheckBox.isSelected());
            project.addLog("JpnWordNetCheckBox", jpnWnCheckBox.isSelected());
        } else if (e.getSource() == jwoCheckBox) {
            if (jwoCheckBox.isSelected()) {
                File jwoDir = new File(DODDLEConstants.JWO_HOME);
                if (OWLOntologyManager.getRefOntology(jwoDir.getAbsolutePath()) == null) {
                    dataset = TDBFactory.createDataset(jwoDir.getAbsolutePath());
                    Model ontModel = dataset.getDefaultModel();
                    ReferenceOWLOntology refOnt = new ReferenceOWLOntology(ontModel, jwoDir.getAbsolutePath(),
                            nameSpaceTable);
                    OWLOntologyManager.addRefOntology(refOnt.getURI(), refOnt);
                }
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == wn30RadioButton) {
            DODDLEConstants.ENWN_HOME = DODDLEConstants.ENWN_3_0_HOME;
            WordNetDic.initWordNetDictionary();
        } else if (e.getSource() == wn31RadioButton) {
            DODDLEConstants.ENWN_HOME = DODDLEConstants.ENWN_3_1_HOME;
            WordNetDic.initWordNetDictionary();
        }
    }
}
