/*
 * @(#)  2007/01/30
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class GeneralOntologySelectionPanel extends JPanel implements ActionListener {
    private JCheckBox edrCheckBox;
    private JCheckBox edrtCheckBox;
    private JCheckBox wnCheckBox;

    private static final String edrTestID = "ID3aa966"; // 概念
    private static final String edrtTestID = "ID2f3526"; // ルートノード
    private static final String wnTestID = "5498421"; // concept

    public GeneralOntologySelectionPanel() {
        edrCheckBox = new JCheckBox(Translator.getString("OntologySelectionPanel.EDR"), false);
        edrCheckBox.addActionListener(this);
        edrtCheckBox = new JCheckBox(Translator.getString("OntologySelectionPanel.EDRT"), false);
        edrtCheckBox.addActionListener(this);
        wnCheckBox = new JCheckBox(Translator.getString("OntologySelectionPanel.WordNet"), false);
        wnCheckBox.addActionListener(this);
        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new GridLayout(3, 1));
        checkPanel.add(edrCheckBox);
        checkPanel.add(edrtCheckBox);
        checkPanel.add(wnCheckBox);
        setLayout(new BorderLayout());
        add(checkPanel, BorderLayout.NORTH);
    }

    public void saveGeneralOntologyInfo(File saveFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8"));
            Properties properties = new Properties();
            properties.setProperty("EDR(general)", String.valueOf(isEDREnable()));
            properties.setProperty("EDR(technical)", String.valueOf(isEDRTEnable()));
            properties.setProperty("WordNet", String.valueOf(isWordNetEnable()));
            properties.store(writer, "Ontology Info");
            writer.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public void loadGeneralOntologyInfo(File loadFile) {
        if (!loadFile.exists()) { return; }
        try {
            FileInputStream fis = new FileInputStream(loadFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            Properties properties = new Properties();
            properties.load(reader);
            boolean t = new Boolean(properties.getProperty("EDR(general)"));
            edrCheckBox.setSelected(t);
            t = new Boolean(properties.getProperty("EDR(technical)"));
            edrtCheckBox.setSelected(t);
            t = new Boolean(properties.getProperty("WordNet"));
            wnCheckBox.setSelected(t);
            reader.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == edrCheckBox) {
            if (edrCheckBox.isSelected()) {
                EDRDic.initEDRDic();
                ConceptDefinition.getInstance().initConceptDescriptionDic();
                Concept c = EDRDic.getEDRConcept(edrTestID);
                edrCheckBox.setEnabled(c != null);
            }
        } else if (e.getSource() == edrtCheckBox) {
            if (edrtCheckBox.isSelected()) {
                EDRDic.initEDRTDic();
                Concept c = EDRDic.getEDRTConcept(edrtTestID);
                edrtCheckBox.setEnabled(c != null);
            }
        } else if (e.getSource() == wnCheckBox) {
            if (wnCheckBox.isSelected()) {
                WordNetDic wnDic = WordNetDic.getInstance();
                Concept c = null;
                if (wnDic != null) {
                    c = WordNetDic.getWNConcept(wnTestID);
                }
                wnCheckBox.setEnabled(wnDic != null && c != null);
            }
        }
    }
}
