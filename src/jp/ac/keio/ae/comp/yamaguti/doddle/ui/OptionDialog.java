package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class OptionDialog extends JDialog implements ActionListener {

    private static JCheckBox siblingDisambiguationCheckBox;
    private static JCheckBox supDisambiguationCheckBox;
    private static JCheckBox subDisambiguationCheckBox;

    private static JCheckBox isUsingSpreadActivatingAlgorithmForDisambiguationBox;
    private static JRadioButton shortestSpreadActivatingAlgorithmForDisambiguationButton;
    private static JRadioButton longestSpreadActivatingAlgorithmForDisambiguationButton;
    private static JRadioButton averageSpreadActivatingAlgorithmForDisambiguationButton;

    private static JRadioButton complexWordSetSameConceptButton;
    private static JRadioButton complexWordSetSubConceptButton;

    private static JCheckBox showPrefixCheckBox;

    private JButton applyButton;
    private JButton saveOptionButton;
    private JButton cancelButton;
    
    private BasicOptionPanel basicOptionPanel;
    private DirectoryPanel directoryPanel;

    public OptionDialog(Frame owner) {
        super(owner);
        
        basicOptionPanel = new BasicOptionPanel();
        
        isUsingSpreadActivatingAlgorithmForDisambiguationBox = new JCheckBox("isUsingSpreadActivatingAlgorithm", true);
        isUsingSpreadActivatingAlgorithmForDisambiguationBox.addActionListener(this);
        shortestSpreadActivatingAlgorithmForDisambiguationButton = new JRadioButton("Shortest", true);
        longestSpreadActivatingAlgorithmForDisambiguationButton = new JRadioButton("Longest");
        averageSpreadActivatingAlgorithmForDisambiguationButton = new JRadioButton("Average");
        ButtonGroup spreadActivatingAlgorithmGroup = new ButtonGroup();
        spreadActivatingAlgorithmGroup.add(shortestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmGroup.add(longestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmGroup.add(averageSpreadActivatingAlgorithmForDisambiguationButton);
        JPanel spreadActivatingAlgorithmOptionPanel = new JPanel();
        spreadActivatingAlgorithmOptionPanel.add(isUsingSpreadActivatingAlgorithmForDisambiguationBox);
        spreadActivatingAlgorithmOptionPanel.add(shortestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmOptionPanel.add(longestSpreadActivatingAlgorithmForDisambiguationButton);
        spreadActivatingAlgorithmOptionPanel.add(averageSpreadActivatingAlgorithmForDisambiguationButton);

        supDisambiguationCheckBox = new JCheckBox(Translator
                .getString("Component.Tool.Option.Disambiguation.ConceptsInPathToRoot"));
        supDisambiguationCheckBox.setSelected(true);
        subDisambiguationCheckBox = new JCheckBox(Translator
                .getString("Component.Tool.Option.Disambiguation.SubConcept"));
        subDisambiguationCheckBox.setSelected(true);
        siblingDisambiguationCheckBox = new JCheckBox(Translator
                .getString("Component.Tool.Option.Disambiguation.SiblingConcept"));
        siblingDisambiguationCheckBox.setSelected(true);
        JPanel automaticDisambiguationOptionPanel = new JPanel();
        automaticDisambiguationOptionPanel.setLayout(new GridLayout(4, 1, 5, 5));
        automaticDisambiguationOptionPanel.add(spreadActivatingAlgorithmOptionPanel);
        automaticDisambiguationOptionPanel.add(supDisambiguationCheckBox);
        automaticDisambiguationOptionPanel.add(subDisambiguationCheckBox);
        automaticDisambiguationOptionPanel.add(siblingDisambiguationCheckBox);

        complexWordSetSameConceptButton = new JRadioButton(Translator
                .getString("Component.Tool.Option.ComplexWord.SameConcept"));
        complexWordSetSubConceptButton = new JRadioButton(Translator
                .getString("Component.Tool.Option.ComplexWord.SubConcept"));
        complexWordSetSubConceptButton.setSelected(true);
        ButtonGroup complexWordButtonGroup = new ButtonGroup();
        complexWordButtonGroup.add(complexWordSetSameConceptButton);
        complexWordButtonGroup.add(complexWordSetSubConceptButton);
        JPanel complexWordOptionPanel = new JPanel();
        complexWordOptionPanel.setLayout(new GridLayout(2, 1, 5, 5));
        complexWordOptionPanel.add(complexWordSetSameConceptButton);
        complexWordOptionPanel.add(complexWordSetSubConceptButton);

        showPrefixCheckBox = new JCheckBox(Translator.getString("Component.Tool.Option.View.ShowPrefix"));
        showPrefixCheckBox.addActionListener(this);
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BorderLayout());
        viewPanel.add(showPrefixCheckBox, BorderLayout.CENTER);

        directoryPanel = new DirectoryPanel();

        JTabbedPane optionTab = new JTabbedPane();
        optionTab.add(basicOptionPanel, Translator.getString("Component.Tool.Option.Basic"));
        optionTab.add(directoryPanel, Translator.getString("Component.Tool.Option.Directory"));
        optionTab.add(automaticDisambiguationOptionPanel, Translator.getString("Component.Tool.Option.Disambiguation"));
        optionTab.add(complexWordOptionPanel, Translator.getString("Component.Tool.Option.ComplexWord"));
        optionTab.add(viewPanel, Translator.getString("Component.Tool.Option.View"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(optionTab, BorderLayout.CENTER);

        saveOptionButton = new JButton(Translator.getString("Component.Tool.Option.SaveOption"));
        saveOptionButton.addActionListener(this);
        applyButton = new JButton(Translator.getString("Apply"));
        applyButton.addActionListener(this);
        cancelButton = new JButton(Translator.getString("Close"));
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveOptionButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(buttonPanel, BorderLayout.EAST);
        Container contentPane = getContentPane();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(eastPanel, BorderLayout.SOUTH);

        loadConfig(new File(ProjectFileNames.CONFIG_FILE));

        setLocationRelativeTo(owner);
        setTitle(Translator.getString("Component.Tool.Option"));
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
            langLabel = new JLabel(Translator.getString("Component.Tool.Option.Basic.Lang"));
            basePrefixLabel = new JLabel(Translator.getString("Component.Tool.Option.Basic.BasePrefix"));
            baseURILabel = new JLabel(Translator.getString("Component.Tool.Option.Basic.BaseURI"));
            
            langField = new JTextField();
            basePrefixField = new JTextField();
            baseURIField = new JTextField();
            
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

    public static boolean isComplexWordSetSameConcept() {
        return complexWordSetSameConceptButton.isSelected();
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

    public static boolean isShowPrefix() {
        return showPrefixCheckBox.isSelected();
    }

        
    public void saveConfig(File file) {
        applyConfig();
        BufferedWriter writer = null;
        try {
            Properties properties = new Properties();
            
            properties.setProperty("LANG", basicOptionPanel.getLang());
            properties.setProperty("BASE_PREFIX", basicOptionPanel.getBasePrefix());
            properties.setProperty("BASE_URI", basicOptionPanel.getBaseURI());
            
            properties.setProperty("SEN_HOME", directoryPanel.getSenDicDir());
            properties.setProperty("EDR_HOME", directoryPanel.getEDRDicDir());
            properties.setProperty("EDRT_HOME", directoryPanel.getEDRTDicDir());
            properties.setProperty("PERL_EXE", directoryPanel.getPerlDir());
            properties.setProperty("CHASEN_EXE", directoryPanel.getChasenDir());
            properties.setProperty("PROJECT_DIR", directoryPanel.getProjectDir());
            properties.setProperty("UPPER_CONCEPT_LIST", directoryPanel.getUpperConceptList());
            
            if (DocumentSelectionPanel.Japanese_Morphological_Analyzer != null) {
                properties.setProperty("Japanese_Morphological_Analyzer",
                        DocumentSelectionPanel.Japanese_Morphological_Analyzer);
            } else {
                properties.setProperty("Japanese_Morphological_Analyzer","C:/Program Files/Mecab/bin/mecab.exe -Ochasen");
            }
            properties.setProperty("SSTAGGER_HOME", directoryPanel.getSSTaggerDir());
            properties.setProperty("XDOC2TXT_EXE", directoryPanel.getXdoc2txtDir());
            properties.setProperty("WORDNET_HOME", directoryPanel.getWNDicDir());

            properties.setProperty("AutomaticDisambiguation.useSiblingNodeCount", String
                    .valueOf(siblingDisambiguationCheckBox.isSelected()));
            properties.setProperty("AutomaticDisambiguation.useChildNodeCount", String
                    .valueOf(subDisambiguationCheckBox.isSelected()));
            properties.setProperty("AutomaticDisambiguation.usePathToRootNodeCount", String
                    .valueOf(supDisambiguationCheckBox.isSelected()));

            properties.setProperty("AutomaticDisambiguation.isUsingSpreadActivationAlgorithm", String
                    .valueOf(isUsingSpreadActivatingAlgorithm()));
            properties.setProperty("AutomaticDisambiguation.isCheckShortestSpreadActivation", String
                    .valueOf(isCheckShortestSpreadActivation()));
            properties.setProperty("AutomaticDisambiguation.isCheckLongestSpreadActivation", String
                    .valueOf(isCheckLongestSpreadActivation()));
            properties.setProperty("AutomaticDisambiguation.isCheckAverageSpreadActivation", String
                    .valueOf(isCheckAverageSpreadActivation()));

            String isSameConceptOrSubConcept = "";
            if (complexWordSetSameConceptButton.isSelected()) {
                isSameConceptOrSubConcept = "SAME";
            } else {
                isSameConceptOrSubConcept = "SUB";
            }
            properties.setProperty("MakeConceptTreeWithComplexWord.isSameConceptOrSubConcept",
                    isSameConceptOrSubConcept);

            properties.setProperty("DisplayPrefix", String.valueOf(showPrefixCheckBox.isSelected()));

            // 以下はオプションダイアログでの設定は未実装                       
            properties.setProperty("USING_DB", String.valueOf(DODDLE.IS_USING_DB));
    
            OutputStream os = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            properties.store(writer, "DODDLE-OWL Option");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void applyConfig() {
        DODDLE.LANG = basicOptionPanel.getLang();
        DODDLE.BASE_PREFIX = basicOptionPanel.getBasePrefix();
        DODDLE.BASE_URI = basicOptionPanel.getBaseURI();
        
        DODDLE.SEN_HOME = directoryPanel.getSenDicDir();
        
        DODDLE.EDR_HOME = directoryPanel.getEDRDicDir();
        EDRDic.ID_DEFINITION_MAP = DODDLE.EDR_HOME + "idDefinitionMapforEDR.txt";
        EDRDic.WORD_IDSET_MAP = DODDLE.EDR_HOME + "wordIDSetMapforEDR.txt";
        EDRTree.ID_SUBIDSET_MAP = DODDLE.EDR_HOME + "idSubIDSetMapforEDR.txt";
        ConceptDefinition.CONCEPT_DEFINITION = DODDLE.EDR_HOME + "conceptDefinitionforEDR.txt";
        
        DODDLE.EDRT_HOME = directoryPanel.getEDRTDicDir();
        EDRDic.EDRT_ID_DEFINITION_MAP = DODDLE.EDRT_HOME + "idDefinitionMapforEDR.txt";
        EDRDic.EDRT_WORD_IDSET_MAP = DODDLE.EDRT_HOME + "wordIDSetMapforEDR.txt";
        EDRTree.EDRT_ID_SUBIDSET_MAP = DODDLE.EDRT_HOME + "idSubIDSetMapforEDR.txt";
        DODDLE.WORDNET_HOME = directoryPanel.getWNDicDir();
        
        DocumentSelectionPanel.PERL_EXE = directoryPanel.getPerlDir(); 
        DocumentSelectionPanel.CHASEN_EXE = directoryPanel.getChasenDir(); 
        DODDLE.PROJECT_HOME = directoryPanel.getProjectDir();
        UpperConceptManager.UPPER_CONCEPT_LIST = directoryPanel.getUpperConceptList();
        DocumentSelectionPanel.SS_TAGGER_HOME = directoryPanel.getSSTaggerDir();
        DocumentSelectionPanel.XDOC2TXT_EXE = directoryPanel.getXdoc2txtDir(); 
    }
    
    public void loadConfig(File file) {
        if (!file.exists()) { return; }
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            Properties properties = new Properties();
            properties.load(reader);                              
            
            DODDLE.LANG = properties.getProperty("LANG");
            basicOptionPanel.setLang(DODDLE.LANG);
            DODDLE.BASE_PREFIX = properties.getProperty("BASE_PREFIX");
            basicOptionPanel.setBasePrefix(DODDLE.BASE_PREFIX);
            DODDLE.BASE_URI = properties.getProperty("BASE_URI");
            basicOptionPanel.setBaseURI(DODDLE.BASE_URI);

            DODDLE.SEN_HOME = properties.getProperty("SEN_HOME");
            directoryPanel.setSenDir(DODDLE.SEN_HOME);
            DODDLE.EDR_HOME = properties.getProperty("EDR_HOME");
            EDRDic.ID_DEFINITION_MAP = DODDLE.EDR_HOME + "idDefinitionMapforEDR.txt";
            EDRDic.WORD_IDSET_MAP = DODDLE.EDR_HOME + "wordIDSetMapforEDR.txt";
            EDRTree.ID_SUBIDSET_MAP = DODDLE.EDR_HOME + "idSubIDSetMapforEDR.txt";            
            ConceptDefinition.CONCEPT_DEFINITION = DODDLE.EDR_HOME + "conceptDefinitionforEDR.txt";
            directoryPanel.setEDRDicDir(DODDLE.EDR_HOME);
            DODDLE.EDRT_HOME = properties.getProperty("EDRT_HOME");
            EDRDic.EDRT_ID_DEFINITION_MAP = DODDLE.EDRT_HOME + "idDefinitionMapforEDR.txt";
            EDRDic.EDRT_WORD_IDSET_MAP = DODDLE.EDRT_HOME + "wordIDSetMapforEDR.txt";
            EDRTree.EDRT_ID_SUBIDSET_MAP = DODDLE.EDRT_HOME + "idSubIDSetMapforEDR.txt";
            directoryPanel.setEDRTDicDir(DODDLE.EDRT_HOME);
            DODDLE.WORDNET_HOME = properties.getProperty("WORDNET_HOME");
            directoryPanel.setWNDicDir(DODDLE.WORDNET_HOME);
            
            DocumentSelectionPanel.PERL_EXE = properties.getProperty("PERL_EXE");
            directoryPanel.setPerlDir(DocumentSelectionPanel.PERL_EXE);
            DocumentSelectionPanel.CHASEN_EXE = properties.getProperty("CHASEN_EXE");
            directoryPanel.setChasenDir(DocumentSelectionPanel.CHASEN_EXE);
            DODDLE.PROJECT_HOME = properties.getProperty("PROJECT_DIR");
            directoryPanel.setProjectDir(DODDLE.PROJECT_HOME);
            UpperConceptManager.UPPER_CONCEPT_LIST = properties.getProperty("UPPER_CONCEPT_LIST");
            directoryPanel.setUpperCnceptList(UpperConceptManager.UPPER_CONCEPT_LIST);
    
            DocumentSelectionPanel.SS_TAGGER_HOME = properties.getProperty("SSTAGGER_HOME");
            directoryPanel.setSSTaggerDir(DocumentSelectionPanel.SS_TAGGER_HOME);
            DocumentSelectionPanel.XDOC2TXT_EXE = properties.getProperty("XDOC2TXT_EXE");
            directoryPanel.setXdoc2txtDir(DocumentSelectionPanel.XDOC2TXT_EXE);
            
            
            if (properties.getProperty("USING_DB").equals("true")) {
                DODDLE.IS_USING_DB = true;
            }
            
            if (DocumentSelectionPanel.Japanese_Morphological_Analyzer != null) {
                properties.setProperty("Japanese_Morphological_Analyzer",
                        DocumentSelectionPanel.Japanese_Morphological_Analyzer);
            } else {
                properties.setProperty("Japanese_Morphological_Analyzer","C:/Program Files/Mecab/bin/mecab.exe -Ochasen");
            }
            DocumentSelectionPanel.Japanese_Morphological_Analyzer = properties
            .getProperty("Japanese_Morphological_Analyzer");
            
            boolean t = new Boolean(properties.getProperty("AutomaticDisambiguation.useSiblingNodeCount"));
            siblingDisambiguationCheckBox.setSelected(t);
            t = new Boolean(properties.getProperty("AutomaticDisambiguation.useChildNodeCount"));
            subDisambiguationCheckBox.setSelected(t);
            t = new Boolean(properties.getProperty("AutomaticDisambiguation.usePathToRootNodeCount"));
            supDisambiguationCheckBox.setSelected(t);

            t = new Boolean(properties.getProperty("AutomaticDisambiguation.isUsingSpreadActivationAlgorithm"));
            isUsingSpreadActivatingAlgorithmForDisambiguationBox.setSelected(t);
            t = new Boolean(properties.getProperty("AutomaticDisambiguation.isCheckShortestSpreadActivation"));
            shortestSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
            t = new Boolean(properties.getProperty("AutomaticDisambiguation.isCheckLongestSpreadActivation"));
            longestSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);
            t = new Boolean(properties.getProperty("AutomaticDisambiguation.isCheckAverageSpreadActivation"));
            averageSpreadActivatingAlgorithmForDisambiguationButton.setSelected(t);

            String isSameConceptOrSubConcept = properties
                    .getProperty("MakeConceptTreeWithComplexWord.isSameConceptOrSubConcept");
            if (isSameConceptOrSubConcept != null) {
                complexWordSetSameConceptButton.setSelected(isSameConceptOrSubConcept.equals("SAME"));
                complexWordSetSubConceptButton.setSelected(isSameConceptOrSubConcept.equals("SUB"));
            }

            t = new Boolean(properties.getProperty("DisplayPrefix"));
            showPrefixCheckBox.setSelected(t);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveOptionButton) {
            JFileChooser chooser = new JFileChooser();
            int retval = chooser.showSaveDialog(DODDLE.rootPane);
            if (retval != JFileChooser.APPROVE_OPTION) { return; }
            saveConfig(chooser.getSelectedFile());
        } else if (e.getSource() == applyButton) {
            applyConfig();
        } else if (e.getSource() == cancelButton) {
            setVisible(false);
        } else if (e.getSource() == isUsingSpreadActivatingAlgorithmForDisambiguationBox) {
            boolean t = isUsingSpreadActivatingAlgorithmForDisambiguationBox.isSelected();
            shortestSpreadActivatingAlgorithmForDisambiguationButton.setEnabled(t);
            longestSpreadActivatingAlgorithmForDisambiguationButton.setEnabled(t);
            averageSpreadActivatingAlgorithmForDisambiguationButton.setEnabled(t);
        } else if (e.getSource() == showPrefixCheckBox) {
            DODDLE.getCurrentProject().getConstructClassPanel().getConceptTree().updateUI();
            DODDLE.getCurrentProject().getConstructPropertyPanel().getConceptTree().updateUI();
        }
    }

    class DirectoryPanel extends JPanel {
        private JTextField chasenDirField;
        private JTextField ssTaggerDirField;
        private JTextField perlDirField;
        private JTextField xdoc2txtDirField;
        private JTextField senDicDirField;
        private JTextField edrDicDirField;
        private JTextField edrtDicDirField;
        private JTextField wnDicDirField;
        private JTextField projectDirField;
        private JTextField upperConceptListField;
        

        private JButton browseChasenDirButton;
        private JButton browseSSTaggerDirButton;
        private JButton browsePerlDirButton;
        private JButton browseXdoc2txtDirButton;
        private JButton browseSenDicDirButton;
        private JButton browseEDRDicDirButton;
        private JButton browseEDRTDicDirButton;
        private JButton browseWNDicDirButton;
        private JButton browseProjectDirButton;
        private JButton browseUpperConceptListButton;

        public DirectoryPanel() {
            initChasenDirField();
            initSSTaggerDirField();
            initPerlDirField();
            initXdoc2txtDirField();
            initSenDicDirField();
            initEDRDicDirField();
            initEDRTDirField();
            initWNDicDirField();
            initProjectDirField();
            initUpperConceptListField();

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(5, 2, 10, 5));
            panel.add(getChasenDirPanel());
            panel.add(getSSTaggerDirPanel());
            panel.add(getPerlDirPanel());
            panel.add(getXdoc2txtDirPanel());
            panel.add(getSenDicDirPanel());
            panel.add(getEDRDicDirPanel());
            panel.add(getEDRTDicDirPanel());
            panel.add(getWNDicDirPanel());
            panel.add(getProjectDirPanel());
            panel.add(getUpperConceptListPanel());
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEtchedBorder());
            add(panel, BorderLayout.CENTER);
        }

        public void setChasenDir(String dir) {
            chasenDirField.setText(dir);
        }
        
        public String getChasenDir() {
            return chasenDirField.getText();
        }

        public void setSenDir(String dir) {
            senDicDirField.setText(dir);
        }
        
        public String getSenDicDir() {
            return senDicDirField.getText();
        }

        public void setPerlDir(String dir) {
            perlDirField.setText(dir);
        }
        
        public String getPerlDir() {
            return perlDirField.getText();
        }
        
        public void setXdoc2txtDir(String dir) {
            xdoc2txtDirField.setText(dir);
        }
        
        public String getXdoc2txtDir() {
            return xdoc2txtDirField.getText();
        }

        public void setSSTaggerDir(String dir) {
            ssTaggerDirField.setText(dir);
        }
        
        public String getSSTaggerDir() {
            return ssTaggerDirField.getText();
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

        public void setWNDicDir(String dir) {
            wnDicDirField.setText(dir);
        }
        
        public String getWNDicDir() {
            return wnDicDirField.getText();
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
        
        private static final int FIELD_SIZE = 20;

        private void initChasenDirField() {
            chasenDirField = new JTextField(FIELD_SIZE);
            chasenDirField.setText(DocumentSelectionPanel.CHASEN_EXE);
            chasenDirField.setEditable(false);
            browseChasenDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseChasenDirButton.addActionListener(new BrowseDirectory(chasenDirField));
        }

        private void initSSTaggerDirField() {
            ssTaggerDirField = new JTextField(FIELD_SIZE);
            ssTaggerDirField.setText(DocumentSelectionPanel.SS_TAGGER_HOME);
            ssTaggerDirField.setEditable(false);
            browseSSTaggerDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseSSTaggerDirButton.addActionListener(new BrowseDirectory(ssTaggerDirField));
        }

        private void initPerlDirField() {
            perlDirField = new JTextField(FIELD_SIZE);
            perlDirField.setText(DocumentSelectionPanel.PERL_EXE);
            perlDirField.setEditable(false);
            browsePerlDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browsePerlDirButton.addActionListener(new BrowseDirectory(perlDirField));
        }

        private void initXdoc2txtDirField() {
            xdoc2txtDirField = new JTextField(FIELD_SIZE);
            xdoc2txtDirField.setText(DocumentSelectionPanel.XDOC2TXT_EXE);
            xdoc2txtDirField.setEditable(false);
            browseXdoc2txtDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseXdoc2txtDirButton.addActionListener(new BrowseDirectory(xdoc2txtDirField));
        }

        private void initSenDicDirField() {
            senDicDirField = new JTextField(FIELD_SIZE);
            senDicDirField.setText(DODDLE.SEN_HOME);
            senDicDirField.setEditable(false);
            browseSenDicDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseSenDicDirButton.addActionListener(new BrowseDirectory(senDicDirField));
        }

        private void initEDRDicDirField() {
            edrDicDirField = new JTextField(FIELD_SIZE);
            edrDicDirField.setText(DODDLE.EDR_HOME);
            edrDicDirField.setEditable(false);
            browseEDRDicDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseEDRDicDirButton.addActionListener(new BrowseDirectory(edrDicDirField));
        }
        private void initEDRTDirField() {
            edrtDicDirField = new JTextField(FIELD_SIZE);
            edrtDicDirField.setText(DODDLE.EDRT_HOME);
            edrtDicDirField.setEditable(false);
            browseEDRTDicDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseEDRTDicDirButton.addActionListener(new BrowseDirectory(edrtDicDirField));
        }
        
        private void initWNDicDirField() {
            wnDicDirField = new JTextField(FIELD_SIZE);
            wnDicDirField.setText(DODDLE.WORDNET_HOME);
            wnDicDirField.setEditable(false);
            browseWNDicDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseWNDicDirButton.addActionListener(new BrowseDirectory(wnDicDirField));
        }
        
        private void initProjectDirField() {
            projectDirField = new JTextField(FIELD_SIZE);
            projectDirField.setText(DODDLE.PROJECT_HOME);
            projectDirField.setEditable(false);
            browseProjectDirButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseProjectDirButton.addActionListener(new BrowseDirectory(projectDirField));
        }
        
        private void initUpperConceptListField() {
            upperConceptListField = new JTextField(FIELD_SIZE);
            upperConceptListField.setText(UpperConceptManager.UPPER_CONCEPT_LIST);
            upperConceptListField.setEditable(false);
            browseUpperConceptListButton = new JButton(Translator.getString("OptionDialog.DirectoryTab.Browse"));
            // browseChasenDirButton.setMnemonic('r');
            browseUpperConceptListButton.addActionListener(new BrowseDirectory(upperConceptListField));
        }

        class BrowseDirectory extends AbstractAction {
            private JTextField directoryField;

            BrowseDirectory(JTextField field) {
                directoryField = field;
            }

            private String getDirectoryName() {
                File currentDirectory = new File(directoryField.getText());
                JFileChooser jfc = new JFileChooser(currentDirectory);
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.setDialogTitle("Select Directory");
                int fd = jfc.showOpenDialog(DODDLE.getCurrentProject().getRootPane());
                if (fd == JFileChooser.APPROVE_OPTION) { return jfc.getSelectedFile().toString(); }
                return null;
            }

            public void actionPerformed(ActionEvent e) {
                String directoryName = getDirectoryName();
                if (directoryName != null) {
                    directoryField.setText(directoryName);
                    directoryField.setToolTipText(directoryName);
                    if (directoryField == chasenDirField) {
                        DocumentSelectionPanel.CHASEN_EXE = directoryName;
                    } else if (directoryField == ssTaggerDirField) {
                        DocumentSelectionPanel.SS_TAGGER_HOME = directoryName;
                    } else if (directoryField == perlDirField) {
                        DocumentSelectionPanel.PERL_EXE = directoryName;
                    } else if (directoryField == senDicDirField) {
                        DODDLE.SEN_HOME = directoryName;
                    } else if (directoryField == edrDicDirField) {
                        DODDLE.EDR_HOME = directoryName;
                    } else if (directoryField == edrtDicDirField) {
                        DODDLE.EDRT_HOME = directoryName;
                    } else if (directoryField == wnDicDirField) {
                        DODDLE.WORDNET_HOME = directoryName;
                    } else if (directoryField == projectDirField) {
                        DODDLE.PROJECT_HOME = directoryName;
                    } else if (directoryField == upperConceptListField) {
                        UpperConceptManager.UPPER_CONCEPT_LIST = directoryName;
                    }
                }
            }
        }

        private JPanel getChasenDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.ChasenDirectory")));
            workDirectoryPanel.add(chasenDirField);
            workDirectoryPanel.add(browseChasenDirButton);

            return workDirectoryPanel;
        }

        private JPanel getSSTaggerDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.SSTaggerDirectory")));
            workDirectoryPanel.add(ssTaggerDirField);
            workDirectoryPanel.add(browseSSTaggerDirButton);

            return workDirectoryPanel;
        }

        private JPanel getPerlDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.PerlDirectory")));
            workDirectoryPanel.add(perlDirField);
            workDirectoryPanel.add(browsePerlDirButton);

            return workDirectoryPanel;
        }

        private JPanel getXdoc2txtDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.Xdoc2txtDirectory")));
            workDirectoryPanel.add(xdoc2txtDirField);
            workDirectoryPanel.add(browseXdoc2txtDirButton);

            return workDirectoryPanel;
        }

        private JPanel getSenDicDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.SenDicDirectory")));
            workDirectoryPanel.add(senDicDirField);
            workDirectoryPanel.add(browseSenDicDirButton);

            return workDirectoryPanel;
        }

        private JPanel getEDRDicDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.EDRDicDirectory")));
            workDirectoryPanel.add(edrDicDirField);
            workDirectoryPanel.add(browseEDRDicDirButton);

            return workDirectoryPanel;
        }

        private JPanel getEDRTDicDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.EDRTDicDirectory")));
            workDirectoryPanel.add(edrtDicDirField);
            workDirectoryPanel.add(browseEDRTDicDirButton);

            return workDirectoryPanel;
        }

        private JPanel getWNDicDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.WNDicDirectory")));
            workDirectoryPanel.add(wnDicDirField);
            workDirectoryPanel.add(browseWNDicDirButton);

            return workDirectoryPanel;
        }
        
        private JPanel getProjectDirPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.ProjectDirectory")));
            workDirectoryPanel.add(projectDirField);
            workDirectoryPanel.add(browseProjectDirButton);

            return workDirectoryPanel;
        }
        
        private JPanel getUpperConceptListPanel() {
            JPanel workDirectoryPanel = new JPanel();
            workDirectoryPanel.setLayout(new BoxLayout(workDirectoryPanel, BoxLayout.X_AXIS));
            workDirectoryPanel.setBorder(BorderFactory.createTitledBorder(Translator
                    .getString("OptionDialog.DirectoryTab.UpperConceptList")));
            workDirectoryPanel.add(upperConceptListField);
            workDirectoryPanel.add(browseUpperConceptListButton);

            return workDirectoryPanel;
        }
    }
}
