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

import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.UpperConceptManager;
import org.doddle_owl.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Takeshi Morita
 */
public class OptionDialog extends JDialog implements ActionListener {

    private static JCheckBox siblingDisambiguationCheckBox;
    private static JCheckBox supDisambiguationCheckBox;
    private static JCheckBox subDisambiguationCheckBox;

    private static JCheckBox isUsingSpreadActivatingAlgorithmForDisambiguationBox;
    private static JRadioButton shortestSpreadActivatingAlgorithmForDisambiguationButton;
    private static JRadioButton longestSpreadActivatingAlgorithmForDisambiguationButton;
    private static JRadioButton averageSpreadActivatingAlgorithmForDisambiguationButton;

    private static JRadioButton compoundWordSetSameConceptButton;
    private static JRadioButton compoundWordSetSubConceptButton;

    private static JCheckBox showQNameCheckBox;

    private JButton saveOptionToRegistryButton;
    private JButton applyButton;
    private JButton removeButton;
    private JButton cancelButton;

    private BasicOptionPanel basicOptionPanel;
    private DirectoryPanel directoryPanel;

    public OptionDialog(Frame owner) {
        super(owner);
        setIconImage(Utils.getImageIcon("cog.png").getImage());
        basicOptionPanel = new BasicOptionPanel();

        isUsingSpreadActivatingAlgorithmForDisambiguationBox = new JCheckBox(
                Translator.getTerm("UsingSpreadActivatingAlgorithmCheckBox"), true);
        isUsingSpreadActivatingAlgorithmForDisambiguationBox.addActionListener(this);
        shortestSpreadActivatingAlgorithmForDisambiguationButton = new JRadioButton(
                Translator.getTerm("ShortestRadioButton"), true);
        longestSpreadActivatingAlgorithmForDisambiguationButton = new JRadioButton(
                Translator.getTerm("LongestRadioButton"));
        averageSpreadActivatingAlgorithmForDisambiguationButton = new JRadioButton(
                Translator.getTerm("AverageRadioButton"));
        ButtonGroup spreadActivatingAlgorithmGroup = new ButtonGroup();
        spreadActivatingAlgorithmGroup
                .add(shortestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmGroup.add(longestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmGroup.add(averageSpreadActivatingAlgorithmForDisambiguationButton);
        JPanel spreadActivatingAlgorithmOptionPanel = new JPanel();
        spreadActivatingAlgorithmOptionPanel
                .add(isUsingSpreadActivatingAlgorithmForDisambiguationBox);
        spreadActivatingAlgorithmOptionPanel
                .add(shortestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmOptionPanel
                .add(longestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmOptionPanel
                .add(averageSpreadActivatingAlgorithmForDisambiguationButton);

        supDisambiguationCheckBox = new JCheckBox(Translator.getTerm("PathToRootConceptsCheckBox"));
        supDisambiguationCheckBox.setSelected(true);
        subDisambiguationCheckBox = new JCheckBox(Translator.getTerm("SubConceptCheckBox"));
        subDisambiguationCheckBox.setSelected(true);
        siblingDisambiguationCheckBox = new JCheckBox(Translator.getTerm("SiblingConceptCheckBox"));
        siblingDisambiguationCheckBox.setSelected(true);
        JPanel automaticDisambiguationCheckBoxOptionPanel = new JPanel();
        automaticDisambiguationCheckBoxOptionPanel.setLayout(new GridLayout(3, 1));
        automaticDisambiguationCheckBoxOptionPanel.add(supDisambiguationCheckBox);
        automaticDisambiguationCheckBoxOptionPanel.add(subDisambiguationCheckBox);
        automaticDisambiguationCheckBoxOptionPanel.add(siblingDisambiguationCheckBox);

        JPanel automaticDisambiguationOptionPanel = new JPanel();
        automaticDisambiguationOptionPanel.setLayout(new BorderLayout());
        automaticDisambiguationOptionPanel.add(
                Utils.createWestPanel(spreadActivatingAlgorithmOptionPanel), BorderLayout.NORTH);
        automaticDisambiguationOptionPanel.add(
                Utils.createNorthPanel(automaticDisambiguationCheckBoxOptionPanel),
                BorderLayout.CENTER);

        compoundWordSetSameConceptButton = new JRadioButton(
                Translator.getTerm("SameConceptRadioButton"));
        compoundWordSetSubConceptButton = new JRadioButton(
                Translator.getTerm("SubConceptRadioButton"));
        compoundWordSetSubConceptButton.setSelected(true);
        ButtonGroup compoundWordButtonGroup = new ButtonGroup();
        compoundWordButtonGroup.add(compoundWordSetSameConceptButton);
        compoundWordButtonGroup.add(compoundWordSetSubConceptButton);
        JPanel compoundWordOptionPanel = new JPanel();
        compoundWordOptionPanel.setLayout(new GridLayout(1, 2));
        compoundWordOptionPanel.add(compoundWordSetSameConceptButton);
        compoundWordOptionPanel.add(compoundWordSetSubConceptButton);

        showQNameCheckBox = new JCheckBox(Translator.getTerm("DisplayQNameCheckBox"));
        showQNameCheckBox.addActionListener(this);
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BorderLayout());
        viewPanel.add(showQNameCheckBox, BorderLayout.NORTH);

        directoryPanel = new DirectoryPanel();

        JTabbedPane optionTab = new JTabbedPane();
        optionTab.add(basicOptionPanel, Translator.getTerm("BaseOptionPanel"));
        optionTab.add(directoryPanel, Translator.getTerm("FolderOptionPanel"));
        optionTab.add(automaticDisambiguationOptionPanel,
                Translator.getTerm("InputConceptSelectionPanel"));
        optionTab.add(Utils.createNorthPanel(Utils.createWestPanel(compoundWordOptionPanel)),
                Translator.getTerm("CompoundWordOptionPanel"));
        optionTab.add(viewPanel, Translator.getTerm("DisplayOptionPanel"));
        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(optionTab, BorderLayout.CENTER);

        saveOptionToRegistryButton = new JButton(Translator.getTerm("SaveButton"));
        saveOptionToRegistryButton.addActionListener(this);
        applyButton = new JButton(Translator.getTerm("ApplyButton"));
        applyButton.addActionListener(this);
        removeButton = new JButton(Translator.getTerm("RemoveButton"));
        removeButton.addActionListener(this);
        cancelButton = new JButton(Translator.getTerm("CloseButton"));
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(saveOptionToRegistryButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);
        Container contentPane = getContentPane();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(Utils.createWestPanel(buttonPanel), BorderLayout.SOUTH);
        loadUserConfig();

        setLocationRelativeTo(owner);
        setTitle(Translator.getTerm("OptionDialog"));
        pack();
    }

    class BasicOptionPanel extends JPanel {

        private JLabel langLabel;
        private JLabel basePrefixLabel;
        private JLabel baseURILabel;

        private JTextField langField;
        private JTextField basePrefixField;
        private JTextField baseURIField;

        BasicOptionPanel() {
            langLabel = new JLabel(Translator.getTerm("LanguageLabel"));
            basePrefixLabel = new JLabel(Translator.getTerm("BasePrefixLabel"));
            baseURILabel = new JLabel(Translator.getTerm("BaseURILabel"));

            langField = new JTextField();
            langField.setText(DODDLEConstants.LANG);
            basePrefixField = new JTextField();
            basePrefixField.setText(DODDLEConstants.BASE_PREFIX);
            baseURIField = new JTextField();
            baseURIField.setText(DODDLEConstants.BASE_URI);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new GridLayout(3, 2));
            mainPanel.add(langLabel);
            mainPanel.add(langField);
            mainPanel.add(basePrefixLabel);
            mainPanel.add(basePrefixField);
            mainPanel.add(baseURILabel);
            mainPanel.add(baseURIField);

            setLayout(new BorderLayout());
            add(mainPanel, BorderLayout.NORTH);
        }

        public void setLang(String lang) {
            langField.setText(lang);
        }

        public String getLang() {
            return langField.getText();
        }

        public void setBasePrefix(String prefix) {
            basePrefixField.setText(prefix);
        }

        public String getBasePrefix() {
            return basePrefixField.getText();
        }

        public void setBaseURI(String uri) {
            baseURIField.setText(uri);
        }

        public String getBaseURI() {
            return baseURIField.getText();
        }
    }

    public static void setSiblingDisambiguation(boolean t) {
        siblingDisambiguationCheckBox.setSelected(t);
    }

    public static void setSupDisambiguation(boolean t) {
        supDisambiguationCheckBox.setSelected(t);
    }

    public static void setSubDisambiguation(boolean t) {
        subDisambiguationCheckBox.setSelected(t);
    }

    public static void setUsingSpreadActivationAlgorithmForDisambiguation(boolean t) {
        isUsingSpreadActivatingAlgorithmForDisambiguationBox.setSelected(t);
    }

    public static void setShortestSpreadActivatingAlgorithmforDisambiguation(boolean t) {
        shortestSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
    }

    public static void setLongestSpreadActivatingAlgorithmforDisambiguation(boolean t) {
        longestSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
    }

    public static void setAverageSpreadActivatingAlgorithmforDisambiguation(boolean t) {
        averageSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
    }

    public static boolean isCompoundWordSetSameConcept() {
        return compoundWordSetSameConceptButton.isSelected();
    }

    public static boolean isUsingSpreadActivatingAlgorithm() {
        return isUsingSpreadActivatingAlgorithmForDisambiguationBox.isSelected();
    }

    public static boolean isCheckShortestSpreadActivation() {
        return shortestSpreadActivatingAlgorithmForDisambiguationButton.isSelected();
    }

    public static boolean isCheckLongestSpreadActivation() {
        return longestSpreadActivatingAlgorithmForDisambiguationButton.isSelected();
    }

    public static boolean isCheckAverageSpreadActivation() {
        return averageSpreadActivatingAlgorithmForDisambiguationButton.isSelected();
    }

    public static boolean isCheckSupConcepts() {
        return supDisambiguationCheckBox.isSelected();
    }

    public static boolean isCheckSubConcepts() {
        return subDisambiguationCheckBox.isSelected();
    }

    public static boolean isCheckSiblingConcepts() {
        return siblingDisambiguationCheckBox.isSelected();
    }

    public static boolean isShowQName() {
        return showQNameCheckBox.isSelected();
    }

    private Properties getProperties() {
        Properties properties = new Properties();

        properties.setProperty("LANG", basicOptionPanel.getLang());
        properties.setProperty("BASE_PREFIX", basicOptionPanel.getBasePrefix());
        properties.setProperty("BASE_URI", basicOptionPanel.getBaseURI());

        properties.setProperty("EDR_HOME", directoryPanel.getEDRDicDir());
        properties.setProperty("EDRT_HOME", directoryPanel.getEDRTDicDir());

        properties.setProperty("JPWN_HOME", directoryPanel.getJpwnDicDir());
        properties.setProperty("JWO_HOME", directoryPanel.getJwoDicDir());

        properties.setProperty("PERL_EXE", directoryPanel.getPerlDir());
        properties.setProperty("CHASEN_EXE", directoryPanel.getJapaneseMorphologicalAnalyzer());
        properties.setProperty("PROJECT_DIR", directoryPanel.getProjectDir());
        properties.setProperty("STOP_WORD_LIST", directoryPanel.getStopWordList());
        properties.setProperty("UPPER_CONCEPT_LIST", directoryPanel.getUpperConceptList());

        properties.setProperty("STANFORD_PARSER_MODEL_DIR", directoryPanel.getStanfordParserModelDir());
        properties.setProperty("TERM_EXTRACT_SCRIPTS_DIR", directoryPanel.getTermExtractScriptDir());

        if (InputDocumentSelectionPanel.Japanese_Morphological_Analyzer != null) {
            properties.setProperty("Japanese_Morphological_Analyzer",
                    InputDocumentSelectionPanel.Japanese_Morphological_Analyzer);
        } else {
            // properties.setProperty("Japanese_Morphological_Analyzer","C:/Program
            // Files/Mecab/bin/mecab.exe -Ochasen");
            properties.setProperty("Japanese_Morphological_Analyzer",
                    "C:/Program Files/Chasen/bin/chasen.exe");
        }

        if (InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer != null) {
            properties.setProperty("Japanese_Dependency_Structure_Analyzer",
                    InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer);
        } else {
            properties.setProperty("Japanese_Dependency_Structure_Analyzer",
                    "C:/Program Files/CaboCha/bin/cabocha.exe");
        }

        properties.setProperty("AutomaticDisambiguation.useSiblingNodeCount",
                String.valueOf(siblingDisambiguationCheckBox.isSelected()));
        properties.setProperty("AutomaticDisambiguation.useChildNodeCount",
                String.valueOf(subDisambiguationCheckBox.isSelected()));
        properties.setProperty("AutomaticDisambiguation.usePathToRootNodeCount",
                String.valueOf(supDisambiguationCheckBox.isSelected()));

        properties.setProperty("AutomaticDisambiguation.isUsingSpreadActivationAlgorithm",
                String.valueOf(isUsingSpreadActivatingAlgorithm()));
        properties.setProperty("AutomaticDisambiguation.isCheckShortestSpreadActivation",
                String.valueOf(isCheckShortestSpreadActivation()));
        properties.setProperty("AutomaticDisambiguation.isCheckLongestSpreadActivation",
                String.valueOf(isCheckLongestSpreadActivation()));
        properties.setProperty("AutomaticDisambiguation.isCheckAverageSpreadActivation",
                String.valueOf(isCheckAverageSpreadActivation()));

        String isSameConceptOrSubConcept;
        if (compoundWordSetSameConceptButton.isSelected()) {
            isSameConceptOrSubConcept = "SAME";
        } else {
            isSameConceptOrSubConcept = "SUB";
        }
        properties.setProperty("MakeConceptTreeWithCompoundWord.isSameConceptOrSubConcept",
                isSameConceptOrSubConcept);
        properties.setProperty("DisplayQName", String.valueOf(showQNameCheckBox.isSelected()));

        return properties;
    }

    public void saveConfig(File file) {
        try {
            Properties properties = getProperties();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
            try (writer) {
                properties.store(writer, "DODDLE-OWL Option");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void applyConfig() {
        DODDLEConstants.LANG = basicOptionPanel.getLang();
        DODDLEConstants.BASE_PREFIX = basicOptionPanel.getBasePrefix();
        DODDLEConstants.BASE_URI = basicOptionPanel.getBaseURI();

        DODDLEConstants.EDR_HOME = directoryPanel.getEDRDicDir();
        DODDLEConstants.EDRT_HOME = directoryPanel.getEDRTDicDir();

        DODDLEConstants.JPWN_HOME = directoryPanel.getJpwnDicDir();
        DODDLEConstants.JWO_HOME = directoryPanel.getJwoDicDir();

        InputDocumentSelectionPanel.PERL_EXE = directoryPanel.getPerlDir();
        InputDocumentSelectionPanel.Japanese_Morphological_Analyzer = directoryPanel
                .getJapaneseMorphologicalAnalyzer();
        InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer = directoryPanel
                .getJapaneseDependencyStructureAnalyzer();
        DODDLEConstants.PROJECT_HOME = directoryPanel.getProjectDir();
        UpperConceptManager.UPPER_CONCEPT_LIST = directoryPanel.getUpperConceptList();
        InputDocumentSelectionPanel.STOP_WORD_LIST_FILE = directoryPanel.getStopWordList();

        InputDocumentSelectionPanel.STANFORD_PARSER_MODELS_HOME = directoryPanel.getStanfordParserModelDir();
        InputDocumentSelectionPanel.TERM_EXTRACT_SCRIPTS_DIR = directoryPanel.getTermExtractScriptDir();

        // 汎用オントロジーパネルのチェックボックスを有効化する
        DODDLEProject currentProject = (DODDLEProject) DODDLE_OWL.desktop.getSelectedFrame();
        if (currentProject != null) {
            currentProject.getOntologySelectionPanel().resetGeneralOntologiesCheckBoxes();
        }
    }

    public void removeConfig() {
        String msg = Translator.getTerm("RemoveButton") + ": config";
        int isYes = JOptionPane.showConfirmDialog(this, msg, msg, JOptionPane.YES_NO_OPTION);
        if (isYes == JOptionPane.YES_OPTION) {
            Preferences userPrefs = Preferences.userNodeForPackage(DODDLE_OWL.class);
            try {
                userPrefs.clear();
                clearFields();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            DODDLE_OWL.STATUS_BAR.setText(Translator.getTerm("ApplyButton"));
        }
    }

    private void clearFields() {
        directoryPanel.setJapaneseMorphologicalAnalyzer("");
        directoryPanel.setJapaneseDependencyStructureAnalyzer("");
        directoryPanel.setEDRDicDir("");
        directoryPanel.setEDRTDicDir("");
        directoryPanel.setJpwnDicDir("");
        directoryPanel.setJwoDicDir("");
        directoryPanel.setPerlDir("");
        directoryPanel.setProjectDir("");
        directoryPanel.setUpperCnceptList("");
        directoryPanel.setStopWordList("");
        directoryPanel.setStanfordParserModelDir("");
        directoryPanel.setTermExtractScriptDir("");
    }

    public void loadConfig(Properties properties) {
        DODDLEConstants.LANG = properties.getProperty("LANG");
        basicOptionPanel.setLang(DODDLEConstants.LANG);
        DODDLEConstants.BASE_PREFIX = properties.getProperty("BASE_PREFIX");
        basicOptionPanel.setBasePrefix(DODDLEConstants.BASE_PREFIX);
        DODDLEConstants.BASE_URI = properties.getProperty("BASE_URI");
        basicOptionPanel.setBaseURI(DODDLEConstants.BASE_URI);

        DODDLEConstants.EDR_HOME = properties.getProperty("EDR_HOME");
        directoryPanel.setEDRDicDir(DODDLEConstants.EDR_HOME);
        DODDLEConstants.EDRT_HOME = properties.getProperty("EDRT_HOME");
        directoryPanel.setEDRTDicDir(DODDLEConstants.EDRT_HOME);
        DODDLEConstants.JPWN_HOME = properties.getProperty("JPWN_HOME");
        directoryPanel.setJpwnDicDir(DODDLEConstants.JPWN_HOME);
        DODDLEConstants.JWO_HOME = properties.getProperty("JWO_HOME");
        directoryPanel.setJwoDicDir(DODDLEConstants.JWO_HOME);

        InputDocumentSelectionPanel.PERL_EXE = properties.getProperty("PERL_EXE");
        directoryPanel.setPerlDir(InputDocumentSelectionPanel.PERL_EXE);
        DODDLEConstants.PROJECT_HOME = properties.getProperty("PROJECT_DIR");
        directoryPanel.setProjectDir(DODDLEConstants.PROJECT_HOME);
        UpperConceptManager.UPPER_CONCEPT_LIST = properties.getProperty("UPPER_CONCEPT_LIST");
        directoryPanel.setUpperCnceptList(UpperConceptManager.UPPER_CONCEPT_LIST);
        InputDocumentSelectionPanel.STOP_WORD_LIST_FILE = properties.getProperty("STOP_WORD_LIST");
        directoryPanel.setStopWordList(InputDocumentSelectionPanel.STOP_WORD_LIST_FILE);

        InputDocumentSelectionPanel.STANFORD_PARSER_MODELS_HOME = properties.getProperty("STANFORD_PARSER_MODEL_DIR");
        directoryPanel.setStanfordParserModelDir(InputDocumentSelectionPanel.STANFORD_PARSER_MODELS_HOME);
        InputDocumentSelectionPanel.TERM_EXTRACT_SCRIPTS_DIR = properties.getProperty("TERM_EXTRACT_SCRIPTS_DIR");
        directoryPanel.setTermExtractScriptDir(InputDocumentSelectionPanel.TERM_EXTRACT_SCRIPTS_DIR);

        if (InputDocumentSelectionPanel.Japanese_Morphological_Analyzer != null) {
            properties.setProperty("Japanese_Morphological_Analyzer",
                    InputDocumentSelectionPanel.Japanese_Morphological_Analyzer);
        } else {
            properties.setProperty("Japanese_Morphological_Analyzer",
                    "C:/Program Files/Chasen/bin/chasen.exe");
        }
        InputDocumentSelectionPanel.Japanese_Morphological_Analyzer = properties
                .getProperty("Japanese_Morphological_Analyzer");

        if (InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer != null) {
            properties.setProperty("Japanese_Dependency_Structure_Analyzer",
                    InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer);
        } else {
            properties.setProperty("Japanese_Dependency_Structure_Analyzer",
                    "C:/Program Files/ChaboCha/bin/cabocha.exe");
        }
        InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer = properties
                .getProperty("Japanese_Dependency_Structure_Analyzer");

        boolean t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.useSiblingNodeCount"));
        siblingDisambiguationCheckBox.setSelected(t);
        t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.useChildNodeCount"));
        subDisambiguationCheckBox.setSelected(t);
        t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.usePathToRootNodeCount"));
        supDisambiguationCheckBox.setSelected(t);

        t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.isUsingSpreadActivationAlgorithm"));
        isUsingSpreadActivatingAlgorithmForDisambiguationBox.setSelected(t);
        t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.isCheckShortestSpreadActivation"));
        shortestSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
        t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.isCheckLongestSpreadActivation"));
        longestSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
        t = Boolean.valueOf(properties.getProperty("AutomaticDisambiguation.isCheckAverageSpreadActivation"));
        averageSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);

        String isSameConceptOrSubConcept = properties
                .getProperty("MakeConceptTreeWithCompoundWord.isSameConceptOrSubConcept");
        if (isSameConceptOrSubConcept != null) {
            compoundWordSetSameConceptButton.setSelected(isSameConceptOrSubConcept.equals("SAME"));
            compoundWordSetSubConceptButton.setSelected(isSameConceptOrSubConcept.equals("SUB"));
        }

        t = Boolean.valueOf(properties.getProperty("DisplayQName"));
        showQNameCheckBox.setSelected(t);
    }

    private void loadUserConfig() {
        try {
            Preferences userPrefs = Preferences.userNodeForPackage(DODDLE_OWL.class);
            String[] keys = userPrefs.keys();
            if (0 < keys.length) {
                Properties properties = new Properties();
                for (String key : keys) {
                    properties.put(key, userPrefs.get(key, ""));
                }
                loadConfig(properties);
            }
        } catch (BackingStoreException bse) {
            bse.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveOptionToRegistryButton) {
            applyConfig();
            Properties properties = getProperties();
            Preferences userPrefs = Preferences.userNodeForPackage(DODDLE_OWL.class);
            for (Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                Entry<String, String> entry = (Entry) objectObjectEntry;
                userPrefs.put(entry.getKey(), entry.getValue());
            }
            DODDLE_OWL.STATUS_BAR.setText(Translator.getTerm("SaveButton"));
        } else if (e.getSource() == applyButton) {
            applyConfig();
            DODDLE_OWL.STATUS_BAR.setText(Translator.getTerm("ApplyButton"));
        } else if (e.getSource() == removeButton) {
            removeConfig();
        } else if (e.getSource() == cancelButton) {
            setVisible(false);
        } else if (e.getSource() == isUsingSpreadActivatingAlgorithmForDisambiguationBox) {
            boolean t = isUsingSpreadActivatingAlgorithmForDisambiguationBox.isSelected();
            shortestSpreadActivatingAlgorithmForDisambiguationButton.setEnabled(t);
            longestSpreadActivatingAlgorithmForDisambiguationButton.setEnabled(t);
            averageSpreadActivatingAlgorithmForDisambiguationButton.setEnabled(t);
        } else if (e.getSource() == showQNameCheckBox) {
            DODDLE_OWL.getCurrentProject().getConstructClassPanel().getIsaTree().updateUI();
            DODDLE_OWL.getCurrentProject().getConstructClassPanel().getHasaTree().updateUI();
            DODDLE_OWL.getCurrentProject().getConstructPropertyPanel().getIsaTree().updateUI();
        }
    }

    class DirectoryPanel extends JPanel {
        private JTextField japaneseMorphologicalAnalyzerField;
        private JTextField japaneseDependencyStructureAnalyzerField;
        private JTextField edrDicDirField;
        private JTextField edrtDicDirField;
        private JTextField jpwnDicDirField;
        private JTextField jwoDicDirField;
        private JTextField projectDirField;
        private JTextField perlDirField;
        private JTextField upperConceptListField;
        private JTextField stopWordListField;
        private JTextField stanfordParserModelDirField;
        private JTextField termExtractScriptDirField;

        private JButton browseJapaneseMorphologicalAnalyzerButton;
        private JButton browseJapaneseDependencyStructureAnalyzerButton;
        private JButton browseEDRDicDirButton;
        private JButton browseEDRTDicDirButton;
        private JButton browseJpwnDicDirButton;
        private JButton browseJwoDicDirButton;
        private JButton browseProjectDirButton;
        private JButton browsePerlDirButton;
        private JButton browseUpperConceptListButton;
        private JButton browseStopWordListButton;
        private JButton browseStanfordParserModelDirButton;
        private JButton browseTermExtractScriptDirButton;

        public DirectoryPanel() {
            japaneseMorphologicalAnalyzerField = new JTextField(FIELD_SIZE);
            browseJapaneseMorphologicalAnalyzerButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(japaneseMorphologicalAnalyzerField,
                    browseJapaneseMorphologicalAnalyzerButton,
                    InputDocumentSelectionPanel.Japanese_Morphological_Analyzer);

            japaneseDependencyStructureAnalyzerField = new JTextField(FIELD_SIZE);
            browseJapaneseDependencyStructureAnalyzerButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(japaneseDependencyStructureAnalyzerField,
                    browseJapaneseDependencyStructureAnalyzerButton,
                    InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer);

            edrDicDirField = new JTextField(FIELD_SIZE);
            browseEDRDicDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(edrDicDirField, browseEDRDicDirButton, DODDLEConstants.EDR_HOME);

            edrtDicDirField = new JTextField(FIELD_SIZE);
            browseEDRTDicDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(edrtDicDirField, browseEDRTDicDirButton, DODDLEConstants.EDRT_HOME);

            jpwnDicDirField = new JTextField(FIELD_SIZE);
            browseJpwnDicDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(jpwnDicDirField, browseJpwnDicDirButton, DODDLEConstants.JPWN_HOME);

            jwoDicDirField = new JTextField(FIELD_SIZE);
            browseJwoDicDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(jwoDicDirField, browseJwoDicDirButton, DODDLEConstants.JWO_HOME);

            projectDirField = new JTextField(FIELD_SIZE);
            browseProjectDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(projectDirField, browseProjectDirButton, DODDLEConstants.PROJECT_HOME);

            upperConceptListField = new JTextField(FIELD_SIZE);
            browseUpperConceptListButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(upperConceptListField, browseUpperConceptListButton,
                    UpperConceptManager.UPPER_CONCEPT_LIST);

            perlDirField = new JTextField(FIELD_SIZE);
            browsePerlDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(perlDirField, browsePerlDirButton, InputDocumentSelectionPanel.PERL_EXE);

            stopWordListField = new JTextField(FIELD_SIZE);
            browseStopWordListButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(stopWordListField, browseStopWordListButton,
                    InputDocumentSelectionPanel.STOP_WORD_LIST_FILE);


            stanfordParserModelDirField = new JTextField(FIELD_SIZE);
            browseStanfordParserModelDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(stanfordParserModelDirField, browseStanfordParserModelDirButton,
                    InputDocumentSelectionPanel.STANFORD_PARSER_MODELS_HOME);

            termExtractScriptDirField = new JTextField(FIELD_SIZE);
            browseTermExtractScriptDirButton = new JButton(Translator.getTerm("ReferenceButton"));
            initComponent(termExtractScriptDirField, browseTermExtractScriptDirButton,
                    InputDocumentSelectionPanel.TERM_EXTRACT_SCRIPTS_DIR);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(7, 2));

            panel.add(getPanel(projectDirField, browseProjectDirButton,
                    Translator.getTerm("ProjectFolderTextField")));
            panel.add(getPanel(stopWordListField, browseStopWordListButton,
                    Translator.getTerm("StopWordsTextField")));

            panel.add(getPanel(jpwnDicDirField, browseJpwnDicDirButton,
                    Translator.getTerm("JPNWNFolderTextField")));
            panel.add(getPanel(jwoDicDirField, browseJwoDicDirButton,
                    Translator.getTerm("JWOFolderTextField")));

            panel.add(getPanel(edrDicDirField, browseEDRDicDirButton,
                    Translator.getTerm("EDRDicFolderTextField")));
            panel.add(getPanel(edrtDicDirField, browseEDRTDicDirButton,
                    Translator.getTerm("EDRTDicFolderTextField")));

            panel.add(getPanel(japaneseMorphologicalAnalyzerField,
                    browseJapaneseMorphologicalAnalyzerButton,
                    Translator.getTerm("JapaneseMorphologicalAnalyzerTextField")));
            panel.add(getPanel(japaneseDependencyStructureAnalyzerField,
                    browseJapaneseDependencyStructureAnalyzerButton,
                    Translator.getTerm("JapaneseDependencyStructureAnalyzerTextField")));

            panel.add(getPanel(perlDirField, browsePerlDirButton,
                    Translator.getTerm("PerlTextField")));
            panel.add(getPanel(upperConceptListField, browseUpperConceptListButton,
                    Translator.getTerm("UpperConceptListTextField")));

            panel.add(getPanel(stanfordParserModelDirField, browseStanfordParserModelDirButton,
                    Translator.getTerm("StanfordParserModelFolderTextField")));
            panel.add(getPanel(termExtractScriptDirField, browseTermExtractScriptDirButton,
                    Translator.getTerm("CompoundWordExtractionScriptFolderTextField")));

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEtchedBorder());
            add(Utils.createNorthPanel(panel), BorderLayout.CENTER);
        }

        public void setJapaneseMorphologicalAnalyzer(String dir) {
            japaneseMorphologicalAnalyzerField.setText(dir);
        }

        public String getJapaneseMorphologicalAnalyzer() {
            return japaneseMorphologicalAnalyzerField.getText();
        }

        public void setJapaneseDependencyStructureAnalyzer(String dir) {
            japaneseDependencyStructureAnalyzerField.setText(dir);
        }

        public String getJapaneseDependencyStructureAnalyzer() {
            return japaneseDependencyStructureAnalyzerField.getText();
        }

        public void setPerlDir(String dir) {
            perlDirField.setText(dir);
        }

        public String getPerlDir() {
            return perlDirField.getText();
        }

        public void setEDRDicDir(String dir) {
            edrDicDirField.setText(dir);
        }

        public String getEDRDicDir() {
            return edrDicDirField.getText();
        }

        public void setEDRTDicDir(String dir) {
            edrtDicDirField.setText(dir);
        }

        public String getEDRTDicDir() {
            return edrtDicDirField.getText();
        }

        public void setJpwnDicDir(String dir) {
            jpwnDicDirField.setText(dir);
        }

        public String getJpwnDicDir() {
            return jpwnDicDirField.getText();
        }

        public void setJwoDicDir(String dir) {
            jwoDicDirField.setText(dir);
        }

        public String getJwoDicDir() {
            return jwoDicDirField.getText();
        }

        public void setProjectDir(String dir) {
            projectDirField.setText(dir);
        }

        public String getProjectDir() {
            return projectDirField.getText();
        }

        public void setUpperCnceptList(String file) {
            upperConceptListField.setText(file);
        }

        public String getUpperConceptList() {
            return upperConceptListField.getText();
        }

        public void setStopWordList(String file) {
            stopWordListField.setText(file);
        }

        public String getStopWordList() {
            return stopWordListField.getText();
        }


        public void setStanfordParserModelDir(String file) {
            stanfordParserModelDirField.setText(file);
        }

        public String getStanfordParserModelDir() {
            return stanfordParserModelDirField.getText();
        }


        public void setTermExtractScriptDir(String file) {
            termExtractScriptDirField.setText(file);
        }

        public String getTermExtractScriptDir() {
            return termExtractScriptDirField.getText();
        }

        private static final int FIELD_SIZE = 20;

        private void initComponent(JTextField textField, JButton button, String value) {
            textField.setText(value);
            textField.setEditable(false);
            button.addActionListener(new BrowseDirectory(textField));
        }

        private JPanel getPanel(JTextField textField, JButton button, String borderTitle) {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(borderTitle));
            workDirectoryPanel.add(textField);
            workDirectoryPanel.add(button);
            return workDirectoryPanel;
        }

        class BrowseDirectory extends AbstractAction {
            private JTextField directoryField;

            BrowseDirectory(JTextField field) {
                directoryField = field;
            }

            private String getFileOrDirectoryName() {
                File currentDirectory = new File(directoryField.getText());
                JFileChooser jfc = new JFileChooser(currentDirectory);
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.setDialogTitle("Select Directory");
                int fd = jfc.showOpenDialog(DODDLE_OWL.getCurrentProject().getRootPane());
                if (fd == JFileChooser.APPROVE_OPTION) {
                    return jfc.getSelectedFile().toString();
                }
                return null;
            }

            public void actionPerformed(ActionEvent e) {
                String fileOrDirectoryName = getFileOrDirectoryName();
                if (fileOrDirectoryName != null) {
                    directoryField.setText(fileOrDirectoryName);
                    directoryField.setToolTipText(fileOrDirectoryName);
                    if (directoryField == japaneseMorphologicalAnalyzerField) {
                        InputDocumentSelectionPanel.Japanese_Morphological_Analyzer = fileOrDirectoryName;
                    } else if (directoryField == japaneseDependencyStructureAnalyzerField) {
                        InputDocumentSelectionPanel.Japanese_Dependency_Structure_Analyzer = fileOrDirectoryName;
                    } else if (directoryField == perlDirField) {
                        InputDocumentSelectionPanel.PERL_EXE = fileOrDirectoryName;
                    } else if (directoryField == edrDicDirField) {
                        DODDLEConstants.EDR_HOME = fileOrDirectoryName;
                    } else if (directoryField == edrtDicDirField) {
                        DODDLEConstants.EDRT_HOME = fileOrDirectoryName;
                    } else if (directoryField == jpwnDicDirField) {
                        DODDLEConstants.JPWN_HOME = fileOrDirectoryName;
                    } else if (directoryField == jwoDicDirField) {
                        DODDLEConstants.JWO_HOME = fileOrDirectoryName;
                    } else if (directoryField == projectDirField) {
                        DODDLEConstants.PROJECT_HOME = fileOrDirectoryName;
                    } else if (directoryField == upperConceptListField) {
                        UpperConceptManager.UPPER_CONCEPT_LIST = fileOrDirectoryName;
                    } else if (directoryField == stanfordParserModelDirField) {
                        InputDocumentSelectionPanel.STANFORD_PARSER_MODELS_HOME = fileOrDirectoryName;
                    } else if (directoryField == termExtractScriptDirField) {
                        InputDocumentSelectionPanel.TERM_EXTRACT_SCRIPTS_DIR = fileOrDirectoryName;
                    }
                }
            }
        }

    }
}
