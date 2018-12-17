/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2015 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package net.sourceforge.doddle_owl.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;
import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sourceforge.doddle_owl.*;
import net.sourceforge.doddle_owl.data.*;
import net.sourceforge.doddle_owl.taskanalyzer.*;
import net.sourceforge.doddle_owl.utils.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * @author Takeshi Morita
 */
public class InputDocumentSelectionPanel extends JPanel implements ListSelectionListener,
        ActionListener {

    private Set<String> stopWordSet;

    private JList docList;
    private JList inputDocList;

    private JComboBox docLangBox;
    private JButton addDocButton;
    private JButton removeDocButton;
    private JComboBox inputDocLangBox;
    private JButton addInputDocButton;
    private JButton removeInputDocButton;

    private JButton termExtractionButton;
    private JCheckBox genSenCheckBox;
    private JCheckBox cabochaCheckBox;
    private JCheckBox showImportanceCheckBox;
    private JCheckBox nounCheckBox;
    private JCheckBox verbCheckBox;
    private JCheckBox otherCheckBox;
    private JCheckBox oneWordCheckBox;

    private JTextField punctuationField;
    private JButton setPunctuationButton;

    public static String PUNCTUATION_CHARS = "．|。|\\.";
    public static final String COMPOUND_WORD_JA = "複合語";
    public static final String COMPOUND_WORD_EN = "Compound Word";

    private ImageIcon addDocIcon = Utils.getImageIcon("page_white_add.png");
    private ImageIcon removeDocIcon = Utils.getImageIcon("page_white_delete.png");

    private TaskAnalyzer taskAnalyzer;

    private JTextArea inputDocArea;
    private Map<String, TermInfo> termInfoMap;

    private InputTermSelectionPanel inputTermSelectionPanel;
    private DODDLEProject project;

    private View[] mainViews;
    private RootWindow rootWindow;

    public InputDocumentSelectionPanel(InputTermSelectionPanel iwsPanel, DODDLEProject p) {
        project = p;
        inputTermSelectionPanel = iwsPanel;
        termInfoMap = new HashMap<String, TermInfo>();
        stopWordSet = new HashSet<String>();
        docList = new JList(new DefaultListModel());
        docList.addListSelectionListener(this);
        JScrollPane docListScroll = new JScrollPane(docList);
        inputDocList = new JList(new DefaultListModel());
        inputDocList.addListSelectionListener(this);
        JScrollPane inputDocListScroll = new JScrollPane(inputDocList);

        DefaultComboBoxModel docLangBoxModel = new DefaultComboBoxModel(new Object[]{"en", "ja"});
        docLangBox = new JComboBox(docLangBoxModel);
        docLangBox.addActionListener(this);
        addDocButton = new JButton(new AddDocAction(Translator.getTerm("AddDocumentButton")));
        removeDocButton = new JButton(new RemoveDocAction(
                Translator.getTerm("RemoveDocumentButton")));
        DefaultComboBoxModel inputDocLangBoxModel = new DefaultComboBoxModel(new Object[]{"en",
                "ja"});
        inputDocLangBox = new JComboBox(inputDocLangBoxModel);
        inputDocLangBox.addActionListener(this);
        addInputDocButton = new JButton(new AddInputDocAction(
                Translator.getTerm("AddInputDocumentButton")));
        removeInputDocButton = new JButton(new RemoveInputDocAction(
                Translator.getTerm("RemoveInputDocumentButton")));

        inputDocArea = new JTextArea();
        inputDocArea.setLineWrap(true);
        JScrollPane inputDocAreaScroll = new JScrollPane(inputDocArea);

        JPanel docButtonPanel = new JPanel();
        docButtonPanel.setLayout(new BorderLayout());
        docButtonPanel.setLayout(new GridLayout(1, 3));
        docButtonPanel.add(docLangBox);
        docButtonPanel.add(addDocButton);
        docButtonPanel.add(removeDocButton);
        JPanel docPanel = new JPanel();
        docPanel.setLayout(new BorderLayout());
        docPanel.add(docListScroll, BorderLayout.CENTER);
        docPanel.add(docButtonPanel, BorderLayout.SOUTH);

        punctuationField = new JTextField(10);
        punctuationField.setText(PUNCTUATION_CHARS);
        setPunctuationButton = new JButton(Translator.getTerm("SetPunctuationCharacterButton"));
        setPunctuationButton.addActionListener(this);

        JPanel punctuationPanel = new JPanel();
        punctuationPanel.add(punctuationField);
        punctuationPanel.add(setPunctuationButton);

        JPanel inputDocButtonPanel = new JPanel();
        inputDocButtonPanel.setLayout(new GridLayout(1, 3));
        inputDocButtonPanel.add(inputDocLangBox);
        inputDocButtonPanel.add(addInputDocButton);
        inputDocButtonPanel.add(removeInputDocButton);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(inputDocButtonPanel, BorderLayout.WEST);
        southPanel.add(punctuationPanel, BorderLayout.EAST);

        JPanel inputDocPanel = new JPanel();
        inputDocPanel.setLayout(new BorderLayout());
        inputDocPanel.add(inputDocListScroll, BorderLayout.CENTER);
        inputDocPanel.add(southPanel, BorderLayout.SOUTH);

        termExtractionButton = new JButton(Translator.getTerm("InputTermExtractionButton"),
                Utils.getImageIcon("input_term_selection.png"));
        termExtractionButton.addActionListener(this);

        genSenCheckBox = new JCheckBox(Translator.getTerm("GensenCheckBox"));
        genSenCheckBox.setSelected(false);
        cabochaCheckBox = new JCheckBox(Translator.getTerm("CabochaCheckBox"));
        cabochaCheckBox.setSelected(true);
        showImportanceCheckBox = new JCheckBox("重要度");
        nounCheckBox = new JCheckBox(Translator.getTerm("NounCheckBox"));
        nounCheckBox.setSelected(true);
        verbCheckBox = new JCheckBox(Translator.getTerm("VerbCheckBox"));
        verbCheckBox.setSelected(false);
        otherCheckBox = new JCheckBox(Translator.getTerm("OtherPOSCheckBox"));
        oneWordCheckBox = new JCheckBox(Translator.getTerm("OneCharacterCheckBox"));

        JPanel morphemeAnalysisPanel = new JPanel();
        morphemeAnalysisPanel.add(genSenCheckBox);
        morphemeAnalysisPanel.add(cabochaCheckBox);
        morphemeAnalysisPanel.add(nounCheckBox);
        morphemeAnalysisPanel.add(verbCheckBox);
        morphemeAnalysisPanel.add(otherCheckBox);
        morphemeAnalysisPanel.add(oneWordCheckBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(morphemeAnalysisPanel, BorderLayout.WEST);
        buttonPanel.add(termExtractionButton, BorderLayout.EAST);

        mainViews = new View[2];
        ViewMap viewMap = new ViewMap();
        // mainViews[0] = new View(Translator.getTerm("DocumentList"), null,
        // docPanel);
        mainViews[0] = new View(Translator.getTerm("InputDocumentList"), null, inputDocPanel);
        mainViews[1] = new View(Translator.getTerm("InputDocumentArea"), null, inputDocAreaScroll);

        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        rootWindow = Utils.createDODDLERootWindow(viewMap);
        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setXGALayout() {
        // SplitWindow sw1 = new SplitWindow(true, 0.5f, mainViews[0],
        // mainViews[1]);
        SplitWindow sw2 = new SplitWindow(false, 0.4f, mainViews[0], mainViews[1]);
        rootWindow.setWindow(sw2);
    }

    private void setStopWordSet() {
        stopWordSet.clear();
        BufferedReader reader = null;
        try {
            File file = new File(STOP_WORD_LIST_FILE);
            if (!file.exists()) {
                return;
            }
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (reader.ready()) {
                String line = reader.readLine();
                stopWordSet.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public boolean isOneWordChecked() {
        return oneWordCheckBox.isSelected();
    }

    public boolean isStopWord(String w) {
        return stopWordSet.contains(w);
    }

    private void deleteFiles(File file) {
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    private String getTextFileName(String fileName) {
        if (!fileName.endsWith("txt")) {
            fileName += ".txt";
        }
        return fileName;
    }

    private void saveFiles(Map<File, String> fileTextStringMap, File saveDir) {
        BufferedWriter writer = null;
        try {
            for (Entry<File, String> entrySet : fileTextStringMap.entrySet()) {
                File file = entrySet.getKey();
                String text = entrySet.getValue();
                File saveFile = new File(saveDir, getTextFileName(file.getName()));
                FileOutputStream fos = new FileOutputStream(saveFile);
                writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                writer.write(text);
                writer.close();
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
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

    private Map<File, String> getFileTextStringMap(ListModel listModel) {
        Map<File, String> fileTextStringMap = new HashMap<File, String>();
        for (int i = 0; i < listModel.getSize(); i++) {
            Document doc = (Document) listModel.getElementAt(i);
            fileTextStringMap.put(doc.getFile(), doc.getText());
        }
        return fileTextStringMap;
    }

    /**
     * 同名のファイルが複数ある場合には，上書きされる
     */
    public void saveDocuments(File saveDir) {
        // File docs = new File(saveDir, "docs");
        // Map<File, String> fileTextStringMap =
        // getFileTextStringMap(docList.getModel());
        // if (!docs.mkdir()) {
        // deleteFiles(docs);
        // }
        // saveFiles(fileTextStringMap, docs);
        File inputDocs = new File(saveDir, "inputDocs");
        Map<File, String> fileTextStringMap = getFileTextStringMap(inputDocList.getModel());
        if (!inputDocs.mkdir()) {
            deleteFiles(inputDocs);
        }
        saveFiles(fileTextStringMap, inputDocs);
        saveDocumentInfo(saveDir);
    }

    public void saveDocumentInfo(File saveDir) {
        File docInfo = new File(saveDir, ProjectFileNames.DOC_INFO_FILE);
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(docInfo);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            // for (int i = 0; i < docList.getModel().getSize(); i++) {
            // Document doc = (Document) docList.getModel().getElementAt(i);
            // writer.write("doc," +
            // getTextFileName(doc.getFile().getAbsolutePath()) + "," +
            // doc.getLang() + "\n");
            // }
            for (int i = 0; i < inputDocList.getModel().getSize(); i++) {
                Document doc = (Document) inputDocList.getModel().getElementAt(i);
                writer.write("inputDoc," + doc.getFile().getAbsolutePath() + "," + doc.getLang()
                        + "\n");
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
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

    public void saveDocumentInfo(int projectID, Statement stmt) {
        int docID = 1;
        try {
            for (int i = 0; i < inputDocList.getModel().getSize(); i++) {
                Document doc = (Document) inputDocList.getModel().getElementAt(i);
                String path = URLEncoder.encode(doc.getFile().getAbsolutePath(), "UTF8");
                String lang = doc.getLang();
                String text = URLEncoder.encode(doc.getText(), "UTF8");
                String sql = "INSERT INTO doc_info (Project_ID,Doc_ID,Doc_Path,Language,Text) "
                        + "VALUES(" + projectID + "," + docID + ",'" + path + "','" + lang + "','"
                        + text + "')";
                stmt.executeUpdate(sql);
                docID++;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void openDocuments(File openDir) {
        File docs = new File(openDir, ProjectFileNames.DOC_DIR);
        if (docs.listFiles() != null) {
            Set fileSet = new TreeSet();
            getFiles(docs.listFiles(), fileSet);
            if (fileSet == null) {
                return;
            }
            addDocuments(docList, fileSet);
        }
    }

    public void loadDocuments(File openDir) {
        File docInfo = new File(openDir, ProjectFileNames.DOC_INFO_FILE);
        if (!docInfo.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(docInfo);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (reader.ready()) {
                String line = reader.readLine();
                String[] info = line.split(",");
                if (info.length != 3) {
                    continue;
                }
                String type = info[0];
                String fileName = info[1];
                String lang = info[2];
                if (type.equals("doc")) {
                    DefaultListModel model = (DefaultListModel) docList.getModel();
                    model.addElement(new Document(lang, new File(fileName)));
                } else if (type.equals("inputDoc")) {
                    DefaultListModel model = (DefaultListModel) inputDocList.getModel();
                    model.addElement(new Document(lang, new File(fileName)));
                }
            }
            inputTermSelectionPanel.setInputDocumentListModel(inputDocList.getModel());
        } catch (IOException ioex) {
            ioex.printStackTrace();
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

    public void loadDocuments(int projectID, Statement stmt) {
        try {
            String sql = "SELECT * from doc_info where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String docPath = URLDecoder.decode(rs.getString("Doc_Path"), "UTF8");
                String lang = rs.getString("Language");
                String text = URLDecoder.decode(rs.getString("Text"), "UTF8");
                DefaultListModel model = (DefaultListModel) inputDocList.getModel();
                model.addElement(new Document(lang, new File(docPath), text));
            }
            inputTermSelectionPanel.setInputDocumentListModel(inputDocList.getModel());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void openInputDocuments(File openDir) {
        File inputDocs = new File(openDir, ProjectFileNames.INPUT_DOC_DIR);
        if (inputDocs.listFiles() != null) {
            Set fileSet = new TreeSet();
            getFiles(inputDocs.listFiles(), fileSet);
            if (fileSet == null) {
                return;
            }
            addDocuments(inputDocList, fileSet);
        }
    }

    public int getDocNum() {
        return docList.getModel().getSize() + inputDocList.getModel().getSize();
    }

    private void setTermInfoMap(String term, String pos, File doc, boolean isInputDoc) {
        TermInfo info = null;
        if (termInfoMap.get(term) != null) {
            info = termInfoMap.get(term);
        } else {
            int docNum = docList.getModel().getSize() + inputDocList.getModel().getSize();
            info = new TermInfo(term, docNum);
        }
        if (!(pos.equals(COMPOUND_WORD_EN) || pos.equals(COMPOUND_WORD_JA))) {
            info.addPos(pos);
        } else if (info.getPosSet().size() == 0) {
            info.addPos(pos);
        }
        if (isInputDoc) {
            info.putInputDoc(doc);
        } else {
            info.putDoc(doc);
        }
        termInfoMap.put(term, info);
    }

    private String runStanfordParser(File docFile) {
        File dir = new File(STANFORD_PARSER_MODELS_HOME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        BufferedWriter bw = null;
        StringBuilder builder = new StringBuilder();
        try {
            String modelName = "english-left3words-distsim.tagger";
            String modelPath = STANFORD_PARSER_MODELS_HOME + File.separator + modelName;
            File modelFile = new File(modelPath);
            if (!modelFile.exists()) {
                URL url = DODDLE_OWL.class.getClassLoader().getResource(
                        Utils.RESOURCE_DIR + "stanford_parser_models/" + modelName);
                if (url != null) {
                    FileUtils.copyURLToFile(url, modelFile);
                    // System.out.println("copy: " +
                    // modelFile.getAbsolutePath());
                }
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    STANFORD_PARSER_MODELS_HOME + File.separator + "tmpTagger.txt"), "UTF-8"));
            MaxentTagger tagger = new MaxentTagger(modelFile.getAbsolutePath());
            List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(
                    new FileReader(docFile)));
            for (List<HasWord> sentence : sentences) {
                List<TaggedWord> tSentence = tagger.tagSentence(sentence);
                bw.write(Sentence.listToString(tSentence, false));
                builder.append(Sentence.listToString(tSentence, false));
            }
            bw.close();
        } catch (IOException ioe) {
            DODDLE_OWL.getLogger().log(Level.DEBUG, "Stanford Parser can not be executed.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        return builder.toString();
    }

    private void setTermInfo(String term, String pos, String basicStr, File file, boolean isInputDoc) {
        WordNetDic wordNetAPI = WordNetDic.getInstance();
        if (nounCheckBox.isSelected() && isEnNoun(pos)) {
            IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, term.toLowerCase());
            if (indexWord != null && indexWord.getLemma() != null) {
                basicStr = indexWord.getLemma();
                // System.out.println("n: " + basicStr);
            }
            setTermInfoMap(basicStr, pos, file, isInputDoc);
        } else if (verbCheckBox.isSelected() && isEnVerb(pos)) {
            IndexWord indexWord = wordNetAPI.getIndexWord(POS.VERB, term.toLowerCase());
            if (indexWord != null && indexWord.getLemma() != null) {
                basicStr = indexWord.getLemma();
                // System.out.println("v: " + basicStr);
            }
            setTermInfoMap(basicStr, pos, file, isInputDoc);
        } else if (otherCheckBox.isSelected() && isEnOther(pos)) {
            setTermInfoMap(basicStr, pos, file, isInputDoc);
        }
    }

    private void setTermInfo(String word, String basicStr, File file, boolean isInputDoc) {
        WordNetDic wordNetAPI = WordNetDic.getInstance();
        if (nounCheckBox.isSelected()) {
            IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, word.toLowerCase());
            if (indexWord != null && indexWord.getLemma() != null) {
                basicStr = indexWord.getLemma();
                setTermInfoMap(basicStr, "noun", file, isInputDoc);
                // System.out.println("n: " + basicStr);
            }
        }
        if (verbCheckBox.isSelected()) {
            IndexWord indexWord = wordNetAPI.getIndexWord(POS.VERB, word.toLowerCase());
            if (indexWord != null && indexWord.getLemma() != null) {
                basicStr = indexWord.getLemma();
                setTermInfoMap(basicStr, "verb", file, isInputDoc);
                // System.out.println("v: " + basicStr);
            }
        }
        if (otherCheckBox.isSelected()) {
            setTermInfoMap(basicStr, "", file, isInputDoc);
        }
    }

    private void enTermExtraction(Document doc, boolean isInputDoc) {
        File file = doc.getFile();
        String taggedText = runStanfordParser(file);
        if (taggedText.length() != 0) {
            String[] token = taggedText.split("\\s");
            if (token == null) {
                return;
            }
            for (int i = 0; i < token.length; i++) {
                String[] info = token[i].split("/");
                if (info.length != 2) {
                    continue;
                }
                String word = info[0];
                String pos = info[1];
                String basicStr = word.toLowerCase();

                if (!oneWordCheckBox.isSelected() && basicStr.length() == 1) {
                    continue;
                }
                if (isStopWord(basicStr)) {
                    continue;
                }
                setTermInfo(word, pos, basicStr, file, isInputDoc);
            }
        } else {
            String[] words = doc.getText().split("\\s");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                String basicStr = word.toLowerCase();

                if (!oneWordCheckBox.isSelected() && basicStr.length() == 1) {
                    continue;
                }

                if (isStopWord(basicStr)) {
                    continue;
                }
                setTermInfo(word, basicStr, file, isInputDoc);
            }
        }
        if (genSenCheckBox.isSelected()) {
            Set<String> comoundWordSet = getGensenCompoundWordSet(doc);
            for (String compoundWord : comoundWordSet) {
                setTermInfoMap(compoundWord, COMPOUND_WORD_EN, file, isInputDoc);
            }
        }
    }

    private void jaTermExtraction(Document doc, boolean isInputDoc) {
        File file = doc.getFile();
        try {
            StringTagger tagger = SenFactory.getStringTagger(null);
            List<Token> tokenList = new ArrayList<Token>();
            tagger.analyze(doc.getText(), tokenList);
            for (Token token : tokenList) {
                String pos = token.getMorpheme().getPartOfSpeech();
                String basicStr = token.getMorpheme().getBasicForm();
                if (basicStr.equals("*")) {
                    basicStr = token.getSurface();
                }
                if (!oneWordCheckBox.isSelected() && basicStr.length() == 1) {
                    continue;
                }
                if (isStopWord(basicStr)) {
                    continue;
                }
                if (nounCheckBox.isSelected() && isJaNoun(pos)) {
                    setTermInfoMap(basicStr, pos, file, isInputDoc);
                } else if (verbCheckBox.isSelected() && isJaVerb(pos)) {
                    setTermInfoMap(basicStr, pos, file, isInputDoc);
                } else if (otherCheckBox.isSelected() && isJaOther(pos)) {
                    setTermInfoMap(basicStr, pos, file, isInputDoc);
                }
            }
            if (genSenCheckBox.isSelected()) {
                Set<String> compoundWordSet = getGensenCompoundWordSet(doc);
                for (String compoundWord : compoundWordSet) {
                    setTermInfoMap(compoundWord, COMPOUND_WORD_JA, file, isInputDoc);
                }
            }
            if (cabochaCheckBox.isSelected()) {
                CabochaDocument cabochaDoc = taskAnalyzer.loadUseCaseTask(doc.getFile());
                setWordInfoForCabochaDoc(cabochaDoc.getCompoundWordCountMap(), doc);
                setWordInfoForCabochaDoc(cabochaDoc.getCompoundWordWithNokakuCountMap(), doc);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setWordInfoForCabochaDoc(Map<String, Integer> map, Document doc) {
        for (Entry<String, Integer> entry : map.entrySet()) {
            String compoundWord = entry.getKey();
            int count = entry.getValue();
            setTermInfoMap(compoundWord, COMPOUND_WORD_JA, doc.getFile(), true);
            TermInfo wordInfo = termInfoMap.get(compoundWord);
            wordInfo.putInputDoc(doc.getFile(), count);
        }
    }

    private void termExtraction(Document doc, boolean isInputDoc) {
        if (doc.getLang().equals("ja")) {
            jaTermExtraction(doc, isInputDoc);
        } else if (doc.getLang().equals("en")) {
            enTermExtraction(doc, isInputDoc);
        }
    }

    private Process jaMorphologicalAnalyzerProcess;
    private Process termExtractProcess;
    public static String Japanese_Morphological_Analyzer = "C:/Program Files/Chasen/chasen.exe";
    public static String Japanese_Morphological_Analyzer_CharacterSet = "UTF-8";
    public static String Japanese_Dependency_Structure_Analyzer = "C:/Program Files/CaboCha/bin/cabocha.exe";
    public static String PERL_EXE = "C:/Perl/bin/perl.exe";
    public static String STANFORD_PARSER_MODELS_HOME = Utils.TEMP_DIR + "stanford_parser_models";
    private static String TERM_EXTRACT_CHASEN_PL = "ex_chasen.pl";
    private static String TERM_EXTRACT_MECAB_PL = "ex_mecab.pl";
    private static String TERM_EXTRACT_TAGGER_PL = "ex_brillstagger.pl";
    public static String TERM_EXTRACT_SCRIPTS_DIR = Utils.TEMP_DIR + "TermExtractScripts"
            + File.separator;
    public static String STOP_WORD_LIST_FILE = "C:/DODDLE-OWL/stop_word_list.txt";

    public void destroyProcesses() {
        if (jaMorphologicalAnalyzerProcess != null) {
            jaMorphologicalAnalyzerProcess.destroy();
            // System.out.println("Destroy Japanese Morphological Analyzer
            // Process");
        }
        if (termExtractProcess != null) {
            termExtractProcess.destroy();
            // System.out.println("Term Extract Process");
        }
        if (taskAnalyzer != null) {
            taskAnalyzer.destroyProcess();
        }
    }

    public Set<String> getGensenCompoundWordSet(Document doc) {
        String lang = doc.getLang();
        Set<String> wordSet = new HashSet<String>();
        BufferedReader reader = null;
        try {
            if (lang.equals("ja")) {
                reader = getJaGenSenReader(doc.getText());
            } else if (lang.equals("en")) {
                reader = getEnGensenReader();
            }
            String line = "";
            String splitStr = "\\s+";
            if (lang.equals("en")) {
                splitStr = "\t";
            }
            while ((line = reader.readLine()) != null) { // reader.ready()は使えない
                String[] lines = line.split(splitStr);
                if (lines.length < 2) {
                    continue;
                }
                String word = lines[0].toLowerCase();
                if (lang.equals("en")) {
                    word = word.replaceAll("\\s+", " ");
                }
                String importance = lines[1];
                if (lang.equals("en") && word.split("\\s+").length == 1) {
                    continue;
                }
                if (!oneWordCheckBox.isSelected() && word.length() == 1) {
                    continue;
                }
                if (showImportanceCheckBox.isSelected()) {
                    wordSet.add(word + "(" + importance + ")");
                } else {
                    wordSet.add(word);
                }
            }
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
            deleteTempFiles();
        }
        return wordSet;
    }

    private BufferedReader getEnGensenReader() throws IOException {
        File dir = new File(TERM_EXTRACT_SCRIPTS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String taggerPath = TERM_EXTRACT_SCRIPTS_DIR + File.separator + TERM_EXTRACT_TAGGER_PL;
        File scriptFile = new File(taggerPath);
        if (!scriptFile.exists()) {
            URL url = DODDLE_OWL.class.getClassLoader().getResource(
                    Utils.RESOURCE_DIR + "TermExtractScripts/" + TERM_EXTRACT_TAGGER_PL);
            if (url != null) {
                FileUtils.copyURLToFile(url, scriptFile);
                // System.out.println("copy: " + scriptFile.getAbsolutePath());
            }
        }
        ProcessBuilder processBuilder = new ProcessBuilder(PERL_EXE, taggerPath,
                STANFORD_PARSER_MODELS_HOME + File.separator + "tmpTagger.txt");
        termExtractProcess = processBuilder.start();
        return new BufferedReader(new InputStreamReader(termExtractProcess.getInputStream(),
                "UTF-8"));
    }

    private File tmpFile;
    private File tmpJapaneseMorphologicalAnalyzerFile;

    private void deleteTempFiles() {
        if (tmpFile != null) {
            tmpFile.deleteOnExit();
        }
        if (tmpJapaneseMorphologicalAnalyzerFile != null) {
            tmpJapaneseMorphologicalAnalyzerFile.deleteOnExit();
        }
    }

    /**
     * @param text
     * @return
     * @throws IOException
     */
    private BufferedReader getJaGenSenReader(String text) throws IOException {
        tmpFile = File.createTempFile("tmp", null);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(tmpFile), Japanese_Morphological_Analyzer_CharacterSet));
        bw.write(text);
        bw.close();

        tmpJapaneseMorphologicalAnalyzerFile = File.createTempFile("tmpJpMorphologicalAnalyzer",
                null);
        ProcessBuilder processBuilder = null;
        if (Japanese_Morphological_Analyzer.matches(".*mecab.*")) {
            processBuilder = new ProcessBuilder(Japanese_Morphological_Analyzer, "-o",
                    tmpJapaneseMorphologicalAnalyzerFile.getAbsolutePath(),
                    tmpFile.getAbsolutePath());
        } else {
            processBuilder = new ProcessBuilder(Japanese_Morphological_Analyzer, "-i", "w", "-o",
                    tmpJapaneseMorphologicalAnalyzerFile.getAbsolutePath(),
                    tmpFile.getAbsolutePath());
        }

        jaMorphologicalAnalyzerProcess = processBuilder.start();
        String path = "";
        String TERM_EXTRACT_EXE = TERM_EXTRACT_CHASEN_PL;
        if (Japanese_Morphological_Analyzer.matches(".*mecab.*")) {
            TERM_EXTRACT_EXE = TERM_EXTRACT_MECAB_PL;
        }
        File dir = new File(TERM_EXTRACT_SCRIPTS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        path = TERM_EXTRACT_SCRIPTS_DIR + File.separator + TERM_EXTRACT_EXE;
        File scriptFile = new File(path);
        if (!scriptFile.exists()) {
            URL url = DODDLE_OWL.class.getClassLoader().getResource(
                    Utils.RESOURCE_DIR + "TermExtractScripts/" + TERM_EXTRACT_EXE);
            if (url != null) {
                FileUtils.copyURLToFile(url, scriptFile);
                // System.out.println("copy: " + scriptFile.getAbsolutePath());
            }
        }
        processBuilder = new ProcessBuilder(PERL_EXE, path,
                tmpJapaneseMorphologicalAnalyzerFile.getAbsolutePath());
        termExtractProcess = processBuilder.start();
        return new BufferedReader(new InputStreamReader(termExtractProcess.getInputStream(),
                Japanese_Morphological_Analyzer_CharacterSet));
    }

    private boolean isJaNoun(String pos) {
        return pos.indexOf("名詞") == 0;
    }

    private boolean isJaVerb(String pos) {
        return pos.indexOf("動詞") == 0;
    }

    private boolean isJaOther(String pos) {
        return !(isJaNoun(pos) || isJaVerb(pos));
    }

    private boolean isEnNoun(String pos) {
        return pos.indexOf("NN") != -1;
    }

    private boolean isEnVerb(String pos) {
        return pos.indexOf("VB") != -1;
    }

    private boolean isEnOther(String pos) {
        return !(isEnNoun(pos) || isEnVerb(pos));
    }

    private void setUpperConcept() {
        if (!UpperConceptManager.hasUpperConceptLabelSet()) {
            return;
        }
        for (Entry<String, TermInfo> entry : termInfoMap.entrySet()) {
            String term = entry.getKey();
            TermInfo info = entry.getValue();
            Set<String> upperConceptLabelSet = UpperConceptManager.getUpperConceptLabelSet(term);
            if (info != null) {
                for (String ucLabel : upperConceptLabelSet) {
                    info.addUpperConcept(ucLabel);
                }
            }
        }
    }

    class TermExtractionWorker extends SwingWorker<String, String> implements
            PropertyChangeListener {

        private int currentTaskCnt;

        public TermExtractionWorker(int taskCnt) {
            currentTaskCnt = 1;
            DODDLE_OWL.STATUS_BAR.setLastMessage(Translator.getTerm("InputTermExtractionButton"));
            DODDLE_OWL.STATUS_BAR.startTime();
            DODDLE_OWL.STATUS_BAR.initNormal(taskCnt);
            DODDLE_OWL.STATUS_BAR.lock();
            addPropertyChangeListener(this);
            setStopWordSet();
        }

        @Override
        protected String doInBackground() throws Exception {
            try {
                taskAnalyzer = new TaskAnalyzer();
                UpperConceptManager.makeUpperOntologyList();
                setProgress(currentTaskCnt++);
                termInfoMap.clear();
                ListModel listModel = inputDocList.getModel();
                for (int i = 0; i < listModel.getSize(); i++) {
                    Document doc = (Document) listModel.getElementAt(i);
                    DODDLE_OWL.STATUS_BAR.setLastMessage(doc.getFile().getName());
                    termExtraction(doc, true);
                }
                listModel = docList.getModel();
                for (int i = 0; i < listModel.getSize(); i++) {
                    Document doc = (Document) listModel.getElementAt(i);
                    DODDLE_OWL.STATUS_BAR.setLastMessage(doc.getFile().getName());
                    termExtraction(doc, false);
                }

                setProgress(currentTaskCnt++);
                setUpperConcept();
                removeDocWordSet();
                int docNum = docList.getModel().getSize() + inputDocList.getModel().getSize();
                inputTermSelectionPanel.setInputTermInfoTableModel(termInfoMap, docNum);
                inputTermSelectionPanel.setInputDocumentListModel(inputDocList.getModel());
                setProgress(currentTaskCnt++);
                DODDLE_OWL.setSelectedIndex(DODDLEConstants.INPUT_WORD_SELECTION_PANEL);
                setProgress(currentTaskCnt++);
            } finally {
                DODDLE_OWL.STATUS_BAR.setLastMessage(Translator
                        .getTerm("InputTermExtractionDoneMessage"));
                DODDLE_OWL.STATUS_BAR.unLock();
                DODDLE_OWL.STATUS_BAR.hideProgressBar();
                project.addLog("TermExtractionButton");
            }
            return "done";
        }

        @Override
        public void done() {
            destroyProcesses();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE_OWL.STATUS_BAR.setValue(currentTaskCnt);
            }
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == termExtractionButton) {
            destroyProcesses(); // 連続でボタンを押された場合の処理
            TermExtractionWorker worker = new TermExtractionWorker(4);
            DODDLE_OWL.STATUS_BAR.setSwingWorker(worker);
            worker.execute();
        } else if (e.getSource() == docLangBox) {
            if (docList.getSelectedValuesList().size() == 1) {
                String lang = (String) docLangBox.getSelectedItem();
                Document doc = (Document) docList.getSelectedValue();
                doc.setLang(lang);
                updateUI();
            }
        } else if (e.getSource() == inputDocLangBox) {
            if (inputDocList.getSelectedValuesList().size() == 1) {
                String lang = (String) inputDocLangBox.getSelectedItem();
                Document doc = (Document) inputDocList.getSelectedValue();
                doc.setLang(lang);
                updateUI();
            }
        } else if (e.getSource() == setPunctuationButton) {
            PUNCTUATION_CHARS = punctuationField.getText();
            ListModel inputDocModel = inputDocList.getModel();
            for (int i = 0; i < inputDocModel.getSize(); i++) {
                Document doc = (Document) inputDocModel.getElementAt(i);
                doc.resetText();
            }
            Document doc = (Document) inputDocList.getSelectedValue();
            if (doc != null) {
                inputDocArea.setText(doc.getText());
                inputDocArea.setCaretPosition(0);
                docLangBox.setSelectedItem(doc.getLang());
            }
            updateUI();
        }
    }

    private void removeDocWordSet() {
        Set<String> docWordSet = new HashSet<String>();
        for (TermInfo info : termInfoMap.values()) {
            if (!info.isInputWord()) {
                docWordSet.add(info.getTerm());
            }
        }
        for (String dw : docWordSet) {
            termInfoMap.remove(dw);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == inputDocList && inputDocList.getSelectedValuesList().size() == 1) {
            Document doc = (Document) inputDocList.getSelectedValue();
            inputDocArea.setText(doc.getText());
            inputDocArea.setCaretPosition(0);
            inputDocLangBox.setSelectedItem(doc.getLang());
        } else if (e.getSource() == docList && docList.getSelectedValuesList().size() == 1) {
            Document doc = (Document) docList.getSelectedValue();
            inputDocArea.setText(doc.getText());
            inputDocArea.setCaretPosition(0);
            docLangBox.setSelectedItem(doc.getLang());
        }
    }

    private Set getFiles(File[] files, Set fileSet) {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                fileSet.add(file);
            } else if (file.isDirectory()) {
                getFiles(file.listFiles(), fileSet);
            }
        }
        return fileSet;
    }

    private Set getFiles() {
        JFileChooser chooser = new JFileChooser(DODDLEConstants.PROJECT_HOME);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int retval = chooser.showOpenDialog(DODDLE_OWL.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File[] files = chooser.getSelectedFiles();
        Set fileSet = new TreeSet();
        getFiles(files, fileSet);
        return fileSet;
    }

    public String getTargetTextLines(String word) {
        StringWriter writer = new StringWriter();
        ListModel listModel = inputDocList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            Document doc = (Document) listModel.getElementAt(i);
            String text = doc.getText();
            if (text != null) {
                writer.write("[ " + doc.getFile().getAbsolutePath() + " ]\n");
                String[] lines = text.split("\n");
                for (int j = 0; j < lines.length; j++) {
                    String line = lines[j];
                    if (line.indexOf(word) != -1) {
                        writer.write((j + 1) + ": " + line + "\n");
                    }
                }
                writer.write("\n");
            }
        }
        return writer.toString();
    }

    public String getTargetHtmlLines(String word) {
        StringWriter writer = new StringWriter();
        writer.write("<html><body>");
        ListModel listModel = inputDocList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            Document doc = (Document) listModel.getElementAt(i);
            String text = doc.getText();
            StringBuilder buf = new StringBuilder();
            if (text != null) {
                String[] lines = text.split("\n");
                for (int j = 0; j < lines.length; j++) {
                    String line = lines[j];
                    if (line.matches(".*" + word + ".*")) {
                        line = line.replaceAll(word, "<b><font size=3 color=red>" + word
                                + "</font></b>");
                        buf.append("<b><font size=3 color=navy>");
                        if (DODDLEConstants.LANG.equals("en")) {
                            buf.append(Translator.getTerm("LineMessage"));
                            buf.append(" ");
                            buf.append((j + 1));
                        } else {
                            buf.append((j + 1));
                            buf.append(Translator.getTerm("LineMessage"));
                        }
                        buf.append(": </font></b>");
                        buf.append("<font size=3>");
                        buf.append(line);
                        buf.append("</font>");
                        buf.append("<br>");
                    }
                }
            }
            if (0 < buf.toString().length()) {
                writer.write("<font size=3><b>" + doc.getFile().getAbsolutePath()
                        + "</b></font><br>");
                writer.write(buf.toString());
            }
        }
        writer.write("</body></html>");
        return writer.toString();
    }

    private void addDocuments(JList list, Set fileSet) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        for (Iterator i = fileSet.iterator(); i.hasNext(); ) {
            File file = (File) i.next();
            Document doc = new Document(file);
            String text = doc.getText();
            if (30 < text.split(" ").length) { // 適当．スペース数が一定以上あれば英文とみなす
                doc.setLang("en");
            }
            model.addElement(doc);
        }
    }

    class AddDocAction extends AbstractAction {

        public AddDocAction(String title) {
            super(title);
        }

        public void actionPerformed(ActionEvent e) {
            Set fileSet = getFiles();
            if (fileSet == null) {
                return;
            }
            addDocuments(docList, fileSet);
        }
    }

    class AddInputDocAction extends AbstractAction {
        public AddInputDocAction(String title) {
            super(title, addDocIcon);
        }

        public void actionPerformed(ActionEvent e) {
            Set fileSet = getFiles();
            if (fileSet == null) {
                return;
            }
            addDocuments(inputDocList, fileSet);
            project.getConceptDefinitionPanel().setInputDocList();
            project.addLog("AddInputDocumentButton");
        }
    }

    class RemoveDocAction extends AbstractAction {
        public RemoveDocAction(String title) {
            super(title);
        }

        public void actionPerformed(ActionEvent e) {
            List removeElements = docList.getSelectedValuesList();
            DefaultListModel model = (DefaultListModel) docList.getModel();
            for (int i = 0; i < removeElements.size(); i++) {
                model.removeElement(removeElements.get(i));
            }
            inputDocArea.setText("");
        }
    }

    class RemoveInputDocAction extends AbstractAction {
        public RemoveInputDocAction(String title) {
            super(title, removeDocIcon);
        }

        public void actionPerformed(ActionEvent e) {
            List removeElements = inputDocList.getSelectedValuesList();
            DefaultListModel model = (DefaultListModel) inputDocList.getModel();
            for (int i = 0; i < removeElements.size(); i++) {
                model.removeElement(removeElements.get(i));
            }
            inputDocArea.setText("");
            project.getConceptDefinitionPanel().setInputDocList();
            project.addLog("RemoveInputDocumentButton");
        }
    }

    class EditInputDocAction extends AbstractAction {
        public EditInputDocAction(String title) {
            super(title);
        }

        public void actionPerformed(ActionEvent e) {
            Document doc = (Document) inputDocList.getSelectedValue();
            doc.setText(inputDocArea.getText());
            project.addLog("Edit");
        }
    }

    public Set<Document> getDocSet() {
        TreeSet<Document> docSet = new TreeSet<Document>();
        ListModel listModel = inputDocList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            Document doc = (Document) listModel.getElementAt(i);
            docSet.add(doc);
        }
        return docSet;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        DODDLEConstants.EDR_HOME = "C:/usr/eclipse_workspace/DODDLE_DIC/";
        frame.getContentPane()
                .add(new InputDocumentSelectionPanel(null, null), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
