package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.didion.jwnl.data.*;
import net.java.sen.*;

import org.pdfbox.pdfparser.*;
import org.pdfbox.pdmodel.*;
import org.pdfbox.util.*;

/**
 * @author takeshi morita
 */
public class DocumentSelectionPanel extends JPanel implements ListSelectionListener, ActionListener {

    private Set<String> stopWordSet;

    private JList docList;
    private JList inputDocList;

    private JComboBox docLangBox;
    private JButton addDocButton;
    private JButton removeDocButton;
    private JComboBox inputDocLangBox;
    private JButton addInputDocButton;
    private JButton removeInputDocButton;

    private JButton analyzeMorphemeButton;
    private JCheckBox genSenCheckBox;
    private JCheckBox showImportanceCheckBox;
    private JCheckBox nounCheckBox;
    private JCheckBox verbCheckBox;
    private JCheckBox otherCheckBox;
    private JCheckBox oneWordCheckBox;

    private JTextArea inputDocArea;
    private Map<String, WordInfo> wordInfoMap;

    private InputWordSelectionPanel inputWordSelectionPanel;
    private DODDLEProject project;
    
    public DocumentSelectionPanel(InputWordSelectionPanel iwsPanel, DODDLEProject p) {
        project = p;
        inputWordSelectionPanel = iwsPanel;
        System.setProperty("sen.home", DODDLE.SEN_HOME);
        setStopWordSet();
        wordInfoMap = new HashMap<String, WordInfo>();
        docList = new JList(new DefaultListModel());
        docList.addListSelectionListener(this);
        JScrollPane docListScroll = new JScrollPane(docList);
        docListScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("DocumentSelectionPanel.DocumentList")));
        inputDocList = new JList(new DefaultListModel());
        inputDocList.addListSelectionListener(this);
        JScrollPane inputDocListScroll = new JScrollPane(inputDocList);
        inputDocListScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("DocumentSelectionPanel.InputDocumentList")));

        DefaultComboBoxModel docLangBoxModel = new DefaultComboBoxModel(new Object[] { "en", "ja"});
        docLangBox = new JComboBox(docLangBoxModel);
        docLangBox.addActionListener(this);
        addDocButton = new JButton(new AddDocAction(Translator.getString("DocumentSelectionPanel.AddDocument")));
        removeDocButton = new JButton(
                new RemoveDocAction(Translator.getString("DocumentSelectionPanel.RemoveDocument")));
        DefaultComboBoxModel inputDocLangBoxModel = new DefaultComboBoxModel(new Object[] { "en", "ja"});
        inputDocLangBox = new JComboBox(inputDocLangBoxModel);
        inputDocLangBox.addActionListener(this);
        addInputDocButton = new JButton(new AddInputDocAction(Translator
                .getString("DocumentSelectionPanel.AddInputDocument")));
        removeInputDocButton = new JButton(new RemoveInputDocAction(Translator
                .getString("DocumentSelectionPanel.RemoveInputDocument")));
        inputDocArea = new JTextArea();
        JScrollPane inputDocAreaScroll = new JScrollPane(inputDocArea);
        inputDocAreaScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("DocumentSelectionPanel.InputDocument")));

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

        JPanel inputDocButtonPanel = new JPanel();
        inputDocButtonPanel.setLayout(new BorderLayout());
        inputDocButtonPanel.setLayout(new GridLayout(1, 3));
        inputDocButtonPanel.add(inputDocLangBox);
        inputDocButtonPanel.add(addInputDocButton);
        inputDocButtonPanel.add(removeInputDocButton);
        JPanel inputDocPanel = new JPanel();
        inputDocPanel.setLayout(new BorderLayout());
        inputDocPanel.add(inputDocListScroll, BorderLayout.CENTER);
        inputDocPanel.add(inputDocButtonPanel, BorderLayout.SOUTH);

        analyzeMorphemeButton = new JButton(Translator.getString("DocumentSelectionPanel.ExtractWords"));
        analyzeMorphemeButton.addActionListener(this);

        genSenCheckBox = new JCheckBox(Translator.getString("DocumentSelectionPanel.GenSen"));
        genSenCheckBox.setSelected(true);
        showImportanceCheckBox = new JCheckBox("重要度");
        nounCheckBox = new JCheckBox(Translator.getString("DocumentSelectionPanel.Noun"));
        nounCheckBox.setSelected(true);
        verbCheckBox = new JCheckBox(Translator.getString("DocumentSelectionPanel.Verb"));
        verbCheckBox.setSelected(true);
        otherCheckBox = new JCheckBox(Translator.getString("DocumentSelectionPanel.Other"));
        oneWordCheckBox = new JCheckBox(Translator.getString("DocumentSelectionPanel.OneChar"));

        JPanel morphemeAnalysisPanel = new JPanel();
        morphemeAnalysisPanel.add(genSenCheckBox);
        morphemeAnalysisPanel.add(nounCheckBox);
        morphemeAnalysisPanel.add(verbCheckBox);
        morphemeAnalysisPanel.add(otherCheckBox);
        morphemeAnalysisPanel.add(oneWordCheckBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(morphemeAnalysisPanel, BorderLayout.WEST);
        buttonPanel.add(analyzeMorphemeButton, BorderLayout.EAST);

        JPanel docsPanel = new JPanel();
        docsPanel.setLayout(new GridLayout(1, 2));
        docsPanel.add(docPanel);
        docsPanel.add(inputDocPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(docsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, inputDocAreaScroll);
        splitPane.setDividerSize(DODDLE.DIVIDER_SIZE);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.5);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private void setStopWordSet() {
        stopWordSet = new HashSet<String>();
        try {
            FileInputStream fis = new FileInputStream(DODDLE.getExecPath() + ProjectFileNames.STOP_WORD_LIST_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (reader.ready()) {
                String line = reader.readLine();
                stopWordSet.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isStopWord(String w) {
        return w.length() == 1 || stopWordSet.contains(w);
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

    private void saveFiles(JList list, File saveDir) {
        try {
            ListModel listModel = list.getModel();
            for (int i = 0; i < listModel.getSize(); i++) {
                Document doc = (Document) listModel.getElementAt(i);
                File saveFile = new File(saveDir, getTextFileName(doc.getFile().getName()));
                FileOutputStream fos = new FileOutputStream(saveFile);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                writer.write(getTextString(doc));
                writer.close();
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    /**
     * 同名のファイルが複数ある場合には，上書きされる
     */
    public void saveDocuments(File saveDir) {
        File docs = new File(saveDir, "docs");
        if (!docs.mkdir()) {
            deleteFiles(docs);
        }
        saveFiles(docList, docs);
        File inputDocs = new File(saveDir, "inputDocs");
        if (!inputDocs.mkdir()) {
            deleteFiles(inputDocs);
        }
        saveFiles(inputDocList, inputDocs);
        saveDocumentInfo(saveDir);
    }

    public void saveDocumentInfo(File saveDir) {
        File docInfo = new File(saveDir, ProjectFileNames.DOC_INFO_FILE);
        try {
            FileOutputStream fos = new FileOutputStream(docInfo);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            for (int i = 0; i < docList.getModel().getSize(); i++) {
                Document doc = (Document) docList.getModel().getElementAt(i);
                writer.write("doc," + getTextFileName(doc.getFile().getName()) + "," + doc.getLang() + "\n");
            }
            for (int i = 0; i < inputDocList.getModel().getSize(); i++) {
                Document doc = (Document) inputDocList.getModel().getElementAt(i);
                writer.write("inputDoc," + getTextFileName(doc.getFile().getName()) + "," + doc.getLang() + "\n");
            }
            writer.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public void openDocuments(File openDir) {
        File docs = new File(openDir, ProjectFileNames.DOC_DIR);
        if (docs.listFiles() != null) {
            Set fileSet = new TreeSet();
            getFiles(docs.listFiles(), fileSet);
            if (fileSet == null) { return; }
            addDocuments(docList, fileSet);
        }
    }

    public void loadDocuments(File openDir) {
        File docInfo = new File(openDir, ProjectFileNames.DOC_INFO_FILE);
        if (!docInfo.exists()) { return; }
        try {
            FileInputStream fis = new FileInputStream(docInfo);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
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
                    model.addElement(new Document(lang, new File(openDir + "/docs", fileName)));
                } else if (type.equals("inputDoc")) {
                    DefaultListModel model = (DefaultListModel) inputDocList.getModel();
                    model.addElement(new Document(lang, new File(openDir + "/inputDocs", fileName)));
                }
            }
            reader.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public void openInputDocuments(File openDir) {
        File inputDocs = new File(openDir, ProjectFileNames.INPUT_DOC_DIR);
        if (inputDocs.listFiles() != null) {
            Set fileSet = new TreeSet();
            getFiles(inputDocs.listFiles(), fileSet);
            if (fileSet == null) { return; }
            addDocuments(inputDocList, fileSet);
        }
    }

    private void setWordInfoMap(String word, String pos, File doc, boolean isInputDoc) {
        WordInfo info = null;
        if (wordInfoMap.get(word) != null) {
            info = wordInfoMap.get(word);
        } else {
            int docNum = docList.getModel().getSize() + inputDocList.getModel().getSize();
            info = new WordInfo(word, docNum);
        }
        if (!(pos.equals("Complex Word") || pos.equals("複合語"))) {
            info.addPos(pos);
        } else if (info.getPosSet().size() == 0) {
            info.addPos(pos);
        }
        if (isInputDoc) {
            info.putInputDoc(doc);
        } else {
            info.putDoc(doc);
        }
        wordInfoMap.put(word, info);
    }

    private String runSSTagger(String text) {
        StringBuffer buf = new StringBuffer("");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(SS_TAGGER_HOME + "tmp.txt"));
            text = text.replaceAll("．|\\*", " ");
            bw.write(text);
            bw.close();
            ProcessBuilder processBuilder = new ProcessBuilder(SS_TAGGER_HOME + "tagger.exe", "-i", "tmp.txt");
            processBuilder.directory(new File(SS_TAGGER_HOME));
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while (reader.ready()) {
                String line = reader.readLine();
                System.out.println(line);
                if (line.matches(".*15")) {
                    break;
                }
            }
            reader.close();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (reader.ready()) {
                String line = reader.readLine();
                buf.append(line + "\n");
            }
            bw = new BufferedWriter(new FileWriter(SS_TAGGER_HOME + "tmpTagger.txt"));
            bw.write(buf.toString());
            bw.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    private void analyzeEnMorpheme(String text, Document doc, boolean isInputDoc) {
        File file = doc.getFile();
        String taggedText = runSSTagger(text);
        String[] token = taggedText.split("\\s");
        if (token == null) { return; }
        for (int i = 0; i < token.length; i++) {
            String[] info = token[i].split("/");
            if (info.length != 2) {
                continue;
            }
            String word = info[0];
            String pos = info[1];
            String basicStr = word.toLowerCase();

            if (!oneWordCheckBox.isSelected() && isStopWord(basicStr)) {
                continue;
            }
            WordNetDic wordNetAPI = WordNetDic.getInstance();
            if (nounCheckBox.isSelected() && isEnNoun(pos)) {
                IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basicStr = indexWord.getLemma();
                    // System.out.println("n: " + basicStr);
                }
                setWordInfoMap(basicStr, pos, file, isInputDoc);
            } else if (verbCheckBox.isSelected() && isEnVerb(pos)) {
                IndexWord indexWord = wordNetAPI.getIndexWord(POS.VERB, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basicStr = indexWord.getLemma();
                    // System.out.println("v: " + basicStr);
                }
                setWordInfoMap(basicStr, pos, file, isInputDoc);
            } else if (otherCheckBox.isSelected() && isEnOther(pos)) {
                setWordInfoMap(basicStr, pos, file, isInputDoc);
            }
        }
        if (genSenCheckBox.isSelected()) {
            Set complexWordSet = getComplexWordSet(text, doc.getLang());
            for (Iterator i = complexWordSet.iterator(); i.hasNext();) {
                String complexWord = (String) i.next();
                setWordInfoMap(complexWord, "Complex Word", file, isInputDoc);
            }
        }
    }

    private void analyzeJaMorpheme(String text, Document doc, boolean isInputDoc) {
        File file = doc.getFile();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] tokenList = tagger.analyze(text);
            for (int i = 0; i < tokenList.length; i++) {
                String pos = tokenList[i].getPos();
                String basicStr = tokenList[i].getBasicString();
                // System.out.println(token[i].getPos());
                if (!oneWordCheckBox.isSelected() && basicStr.length() == 1) {
                    continue;
                }
                if (nounCheckBox.isSelected() && isJaNoun(pos)) {
                    setWordInfoMap(basicStr, pos, file, isInputDoc);
                } else if (verbCheckBox.isSelected() && isJaVerb(pos)) {
                    setWordInfoMap(basicStr, pos, file, isInputDoc);
                } else if (otherCheckBox.isSelected() && isJaOther(pos)) {
                    setWordInfoMap(basicStr, pos, file, isInputDoc);
                }
            }
            if (genSenCheckBox.isSelected()) {
                Set<String> complexWordSet = getComplexWordSet(text, doc.getLang());
                for (String complexWord : complexWordSet) {
                    setWordInfoMap(complexWord, "複合語", file, isInputDoc);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void analyzeMorphem(Document doc, boolean isInputDoc) {
        if (doc.getLang().equals("ja")) {
            analyzeJaMorpheme(getTextString(doc), doc, isInputDoc);
        } else if (doc.getLang().equals("en")) {
            analyzeEnMorpheme(getTextString(doc), doc, isInputDoc);
        }
    }

    public static String Japanese_Morphological_Analyzer = "C:/Program Files/Chasen/chasen.exe";
    public static String CHASEN_EXE = "C:/Program Files/ChaSen/chasen.exe";
    public static String PERL_EXE = "C:/Perl/bin/perl.exe";
    public static String SS_TAGGER_HOME = "C:/usr/postagger-1.0/";
    private String TERM_EXTRACT_CHASEN_EXE = "./TermExtract/ex_chasen.pl";
    private String TERM_EXTRACT_TAGGER_EXE = "./TermExtract/ex_brillstagger.pl";
    public static String XDOC2TXT_EXE = "C:/usr/d2txt123/xdoc2txt.exe";

    public Set<String> getComplexWordSet(String text, String lang) {
        Set<String> wordSet = new HashSet<String>();
        try {
            BufferedReader reader = null;
            if (lang.equals("ja")) {
                reader = getGenSenReader(text);
            } else if (lang.equals("en")) {
                reader = getSSTaggerReader();
            }

            while (reader.ready()) {
                String line = reader.readLine();
                String[] lines = line.split("\t");
                String word = lines[0].replaceAll("\\s+", " ").toLowerCase();
                word = word.substring(0, word.length() - 1);
                // System.out.println(word + "---");
                String importance = lines[1].replaceAll(" +", "");
                if (!oneWordCheckBox.isSelected() && word.length() == 1) {
                    continue;
                }
                if (showImportanceCheckBox.isSelected()) {
                    wordSet.add(word + "(" + importance + ")");
                } else {
                    wordSet.add(word);
                }
            }
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return wordSet;
    }

    private BufferedReader getSSTaggerReader() throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(PERL_EXE + " " + TERM_EXTRACT_TAGGER_EXE + " " + SS_TAGGER_HOME + "tmpTagger.txt");
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    /**
     * @param text
     * @param rt
     * @return
     * @throws IOException
     */
    private BufferedReader getGenSenReader(String text) throws IOException {
        Runtime rt = Runtime.getRuntime();
        File tmpFile = File.createTempFile("tmp", null);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
        bw.write(text);
        bw.close();
        Process process = rt.exec(Japanese_Morphological_Analyzer + " " + tmpFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        File tmpJapaneseMorphologicalAnalyzerFile = File.createTempFile("tmpJpMorphologicalAnalyzer", null);
        bw = new BufferedWriter(new FileWriter(tmpJapaneseMorphologicalAnalyzerFile));        
        while (reader.ready()) {
            String line = reader.readLine();
            bw.write(line + "\n");
        }
        bw.close();
        reader.close();
        process = rt.exec(PERL_EXE + " " + TERM_EXTRACT_CHASEN_EXE + " "
                + tmpJapaneseMorphologicalAnalyzerFile.getAbsolutePath());
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
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
        for (Iterator i = UpperConceptManager.getUpperConceptLabelSet().iterator(); i.hasNext();) {
            String ucLabel = (String) i.next();
            Set wordSet = UpperConceptManager.getWordSet(ucLabel);
            for (Iterator j = wordSet.iterator(); j.hasNext();) {
                String word = (String) j.next();
                WordInfo info = wordInfoMap.get(word);
                if (info != null) {
                    info.addUpperConcept(ucLabel);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == analyzeMorphemeButton) {
            UpperConceptManager.makeUpperOntologyList();
            wordInfoMap.clear();
            ListModel listModel = inputDocList.getModel();
            for (int i = 0; i < listModel.getSize(); i++) {
                Document doc = (Document) listModel.getElementAt(i);
                analyzeMorphem(doc, true);
            }
            listModel = docList.getModel();
            for (int i = 0; i < listModel.getSize(); i++) {
                Document doc = (Document) listModel.getElementAt(i);
                analyzeMorphem(doc, false);
            }
            setUpperConcept();
            removeDocWordSet();
            int docNum = docList.getModel().getSize() + inputDocList.getModel().getSize();
            inputWordSelectionPanel.setWordInfoTableModel(wordInfoMap, docNum);
            inputWordSelectionPanel.setInputDocumentListModel(inputDocList.getModel());
            DODDLE.setSelectedIndex(DODDLE.INPUT_WORD_SELECTION_PANEL);
        } else if (e.getSource() == docLangBox) {
            if (docList.getSelectedValues().length == 1) {
                String lang = (String) docLangBox.getSelectedItem();
                Document doc = (Document) docList.getSelectedValue();
                doc.setLang(lang);
                updateUI();
            }
        } else if (e.getSource() == inputDocLangBox) {
            if (inputDocList.getSelectedValues().length == 1) {
                String lang = (String) inputDocLangBox.getSelectedItem();
                Document doc = (Document) inputDocList.getSelectedValue();
                doc.setLang(lang);
                updateUI();
            }
        }
    }

    private void removeDocWordSet() {
        Set docWordSet = new HashSet();
        for (Iterator i = wordInfoMap.values().iterator(); i.hasNext();) {
            WordInfo info = (WordInfo) i.next();
            if (!info.isInputWord()) {
                docWordSet.add(info.getWord());
            }
        }
        for (Iterator i = docWordSet.iterator(); i.hasNext();) {
            String dw = (String) i.next();
            wordInfoMap.remove(dw);
        }
    }

    private static boolean isWindowsOS() {
        return UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }

    public static String getTextString(Document doc) {
        File file = doc.getFile();
        try {
            FileInputStream fis = new FileInputStream(file);
            if (!isWindowsOS() && file.getAbsolutePath().toLowerCase().matches(".*.pdf")) {
                // CMAPの設定をしないと，日本語の処理はうまくできない．
                // うまくできている場合もある．JISAutoDetectがいけないのか？
                // 登録されていないエンコーディングは，処理できない．
                PDFParser pdfParser = new PDFParser(fis);
                pdfParser.parse();
                PDDocument pddoc = pdfParser.getPDDocument();
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pddoc);
                text = text.replaceAll("\n", "");
                text = text.replaceAll("\\.|．|。", "．\n");
                pddoc.close();
                return text;
            } else if (file.getAbsolutePath().toLowerCase().matches(".*.txt")) {
                return getTextString(new InputStreamReader(fis, "UTF-8"));
            } else if (isWindowsOS()) {
                Runtime rt = Runtime.getRuntime();
                Process process = rt.exec(XDOC2TXT_EXE + " " + file.getAbsolutePath());
                return getTextString(new InputStreamReader(process.getInputStream(), "UTF-8"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * @param reader
     * @return
     * @throws IOException
     */
    private static String getTextString(Reader reader) throws IOException {
        BufferedReader bufReader = new BufferedReader(reader);
        StringWriter writer = new StringWriter();        
        while (bufReader.ready()) {
            String line = bufReader.readLine();
            writer.write(line);
        }
        writer.close();
        reader.close();
        String text = writer.toString();
        text = text.replaceAll("\n", "");
        text = text.replaceAll("\\.|．|。", "．\n");
        return text;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == inputDocList && inputDocList.getSelectedValues().length == 1) {
            Document doc = (Document) inputDocList.getSelectedValue();
            inputDocArea.setText(getTextString(doc));
            inputDocLangBox.setSelectedItem(doc.getLang());
        } else if (e.getSource() == docList && docList.getSelectedValues().length == 1) {
            Document doc = (Document) docList.getSelectedValue();
            inputDocArea.setText(getTextString(doc));
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
        JFileChooser chooser = new JFileChooser(DODDLE.PROJECT_DIR);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) { return null; }
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
            String text = getTextString(doc);
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
            String text = getTextString(doc);
            StringBuilder buf = new StringBuilder();
            if (text != null) {
                String[] lines = text.split("\n");
                for (int j = 0; j < lines.length; j++) {
                    String line = lines[j];
                    if (line.matches(".*" + word + ".*")) {
                        line = line.replaceAll(word, "<b><font color=red>" + word + "</font></b>");
                        buf.append("<b><font color=navy>" + (j + 1) + "行目: </font></b>" + line + "<br>");
                    }
                }
            }
            if (0 < buf.toString().length()) {
                writer.write("<h3>" + doc.getFile().getAbsolutePath() + "</h3>");
                writer.write(buf.toString());
            }
        }
        writer.write("</body></html>");
        return writer.toString();
    }

    private void addDocuments(JList list, Set fileSet) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        for (Iterator i = fileSet.iterator(); i.hasNext();) {
            File file = (File) i.next();
            model.addElement(new Document(file));
        }
    }

    class AddDocAction extends AbstractAction {

        public AddDocAction(String title) {
            super(title);
        }

        public void actionPerformed(ActionEvent e) {
            Set fileSet = getFiles();
            if (fileSet == null) { return; }
            addDocuments(docList, fileSet);            
        }
    }

    class AddInputDocAction extends AbstractAction {
        public AddInputDocAction(String title) {
            super(title);
        }
        public void actionPerformed(ActionEvent e) {
            Set fileSet = getFiles();
            if (fileSet == null) { return; }
            addDocuments(inputDocList, fileSet);
            project.getConceptDefinitionPanel().setInputDocList();
        }
    }

    class RemoveDocAction extends AbstractAction {
        public RemoveDocAction(String title) {
            super(title);
        }
        public void actionPerformed(ActionEvent e) {
            Object[] removeElements = docList.getSelectedValues();
            DefaultListModel model = (DefaultListModel) docList.getModel();
            for (int i = 0; i < removeElements.length; i++) {
                model.removeElement(removeElements[i]);
            }
            inputDocArea.setText("");        
        }
    }

    class RemoveInputDocAction extends AbstractAction {
        public RemoveInputDocAction(String title) {
            super(title);
        }
        public void actionPerformed(ActionEvent e) {
            Object[] removeElements = inputDocList.getSelectedValues();
            DefaultListModel model = (DefaultListModel) inputDocList.getModel();
            for (int i = 0; i < removeElements.length; i++) {
                model.removeElement(removeElements[i]);
            }
            inputDocArea.setText("");
            project.getConceptDefinitionPanel().setInputDocList();
        }
    }

    public Set<Document> getDocSet() {
        Set<Document> docSet = new HashSet<Document>();
        ListModel listModel = inputDocList.getModel();
        for (int i = 0; i < listModel.getSize(); i++) {
            Document doc = (Document) listModel.getElementAt(i);
            docSet.add(doc);
        }
        return docSet;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        DODDLE.DODDLE_DIC = "C:/usr/eclipse_workspace/DODDLE_DIC/";
        frame.getContentPane().add(new DocumentSelectionPanel(null, null), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
