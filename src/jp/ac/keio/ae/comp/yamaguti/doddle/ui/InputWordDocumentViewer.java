/*
 * @(#)  2006/11/29
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.Document;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.didion.jwnl.data.*;
import net.java.sen.*;

/**
 * @author takeshi morita
 */
public class InputWordDocumentViewer extends JPanel implements MouseListener, ActionListener, HyperlinkListener, KeyListener {
    private JList documentList;
    private ListModel inputDocumentListModel;
    private JEditorPane documentArea;
    private JEditorPane linkArea;

    private TitledBorder documentAreaBorder;
    
    private JTextField displayLineNumField;
    private JButton setDisplayLineNumButton;
    
    private JTextField searchField;
    private JButton addWordButton;
    private JRadioButton complexWordRadioButton;
    private JRadioButton nounRadioButton;
    private JRadioButton verbRadioButton;
    private JRadioButton otherRadioButton;

    
    private JTable wordInfoTable;
    private DefaultTableModel wordInfoTableModel;

    private static final String CORRECT_WORD_LINK_COLOR = "#325eff";
    private static final String REMOVED_WORD_LINK_COLOR = "gray";
    private static int DISPLAY_LINE_NUM = 20;
    
    private JViewport documentAreaViewport;
    
    public InputWordDocumentViewer() {
        documentList = new JList();
        documentList.addMouseListener(this);
        documentList.setCellRenderer(new DocumentListCellRenderer());
        JScrollPane documentListScroll = new JScrollPane(documentList);
        documentListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("InputDocumentList")));
        documentArea = new JEditorPane("text/html", "");
        documentArea.addMouseListener(this);
        documentArea.addHyperlinkListener(this);
        documentArea.setEditable(false);                
        JScrollPane documentAreaScroll = new JScrollPane(documentArea);
        documentAreaViewport = documentAreaScroll.getViewport();
        documentAreaBorder = BorderFactory.createTitledBorder(Translator.getTerm("InputDocumentArea"));
        documentAreaScroll.setBorder(documentAreaBorder);
        
        linkArea = new JEditorPane("text/html", "");
        linkArea.addHyperlinkListener(this);
        linkArea.setEditable(false);                
        JScrollPane linkAreaScroll = new JScrollPane(linkArea);
        linkAreaScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("PageLinesList")));
        linkAreaScroll.setMinimumSize(new Dimension(100, 50));
        linkAreaScroll.setPreferredSize(new Dimension(100, 50));

        JPanel documentPanel = new JPanel();
        documentPanel.setLayout(new BorderLayout());
        documentPanel.add(documentAreaScroll, BorderLayout.CENTER);
        documentPanel.add(linkAreaScroll, BorderLayout.WEST);
        
        
        displayLineNumField = new JTextField(5);
        setDisplayLineNumButton = new JButton(Translator.getTerm("SetLinesPerPageButton"));
        setDisplayLineNumButton.addActionListener(this);
        
        searchField = new JTextField(15);        
        searchField.addActionListener(this);
        searchField.addKeyListener(this);
        
        addWordButton = new JButton(Translator.getTerm("AddButton"));
        addWordButton.addActionListener(this);
        
        complexWordRadioButton = new JRadioButton(Translator.getTerm("GensenCheckBox"));
        complexWordRadioButton.addActionListener(this);
        nounRadioButton = new JRadioButton(Translator.getTerm("NounCheckBox"));
        nounRadioButton.addActionListener(this);
        verbRadioButton = new JRadioButton(Translator.getTerm("VerbCheckBox"));
        verbRadioButton.addActionListener(this);
        otherRadioButton = new JRadioButton(Translator.getTerm("OtherPOSCheckBox"));
        otherRadioButton.addActionListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(complexWordRadioButton);
        group.add(nounRadioButton);
        group.add(verbRadioButton);
        group.add(otherRadioButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(displayLineNumField);
        buttonPanel.add(setDisplayLineNumButton);
        buttonPanel.add(searchField);
        buttonPanel.add(addWordButton);
        buttonPanel.add(complexWordRadioButton);
        buttonPanel.add(nounRadioButton);
        buttonPanel.add(verbRadioButton);
        buttonPanel.add(otherRadioButton);
        
        wordInfoTable = new JTable();        
        JScrollPane wordInfoTableScroll = new JScrollPane(wordInfoTable);
        wordInfoTableScroll.setPreferredSize(new Dimension(40, 40));
        wordInfoTableScroll.setMinimumSize(new Dimension(40, 40));
        setWordInfoTableModel();
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        southPanel.add(wordInfoTableScroll, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, documentListScroll, documentPanel);
        splitPane.setDividerSize(DODDLEConstants.DIVIDER_SIZE);
        splitPane.setOneTouchExpandable(true);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(southPanel,BorderLayout.SOUTH);
    }
    
    public void setWordInfoTableModel() {
        String WORD = Translator.getTerm("WordLabel");
        String POS = Translator.getTerm("POSLabel");
        String TF = Translator.getTerm("TFLabel");
        String IDF = Translator.getTerm("IDFLabel");
        String TFIDF = Translator.getTerm("TFIDFLabel");
        String INPUT_DOCUMENT = Translator.getTerm("InputDocumentLabel");
        String UPPER_CONCEPT = Translator.getTerm("UpperConceptLabel");
        Object[] titles = new Object[] { WORD, POS, TF, IDF, TFIDF, INPUT_DOCUMENT, UPPER_CONCEPT};

        wordInfoTableModel = new DefaultTableModel(null, titles);
        wordInfoTableModel.getColumnClass(0);

        wordInfoTable.setModel(wordInfoTableModel);
        wordInfoTable.getTableHeader().setToolTipText("sorted by column");
    }

    private final Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    
    public void removeHighlights(JTextComponent jtc) {
        Highlighter highlight = jtc.getHighlighter();
        Highlighter.Highlight[] highlights = highlight.getHighlights();
        for(int i=0;i<highlights.length;i++) {
            if(highlights[i].getPainter() instanceof DefaultHighlighter.DefaultHighlightPainter) {
                highlight.removeHighlight(highlights[i]);
            }
        }
    }
    
    public void setHighlight(JTextComponent jtc, String pattern) {
        removeHighlights(jtc);
        try{
          Highlighter highlight = jtc.getHighlighter();
          javax.swing.text.Document doc = jtc.getDocument();
          String text = doc.getText(0, doc.getLength());
          int pos = 0;
          while((pos = text.indexOf(pattern, pos)) >= 0) {
            highlight.addHighlight(pos, pos+pattern.length(), highlightPainter);
            pos += pattern.length();
          }
        }catch(BadLocationException e) {
            e.printStackTrace();
        }
      }
    
    enum DODDLE_POS {
        COMPLEX, NOUN, VERB, OTHER;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == complexWordRadioButton) {
            selectedPOS = DODDLE_POS.COMPLEX;
            setDocumentAndLinkArea();
        } else if (e.getSource() == nounRadioButton) {
            selectedPOS = DODDLE_POS.NOUN;
            setDocumentAndLinkArea();
        } else if (e.getSource() == verbRadioButton) {
            selectedPOS = DODDLE_POS.VERB;
            setDocumentAndLinkArea();
        } else if (e.getSource() == otherRadioButton) {
            selectedPOS = DODDLE_POS.OTHER;
            setDocumentAndLinkArea();
        } else if (e.getSource() == searchField) {
            if (searchField.getText().length() == 0) {
                removeHighlights(documentArea);
            } else {
                setHighlight(documentArea, searchField.getText());
            }
        } else if (e.getSource() == addWordButton) {
            addUserDefinedWord(searchField.getText());
        } else if (e.getSource() == setDisplayLineNumButton) {
            try {
                 int num= Integer.parseInt(displayLineNumField.getText());
                 if (0 < num) {
                     DISPLAY_LINE_NUM = num;
                     setDocumentAndLinkArea();
                     documentArea.setCaretPosition(0);
                 }                 
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
    }

    /**
     * 抽出できなかった入力単語をユーザ定義単語として追加する
     */
    private void addUserDefinedWord(String word) {        
        if (word.length() == 0) {
            return;
        }
        word = word.replaceAll("<", "");
        word = word.replaceAll(">", "");
        String basicWord = "";
        if (selectedDoc.getLang().equals("ja")) {
            try {
                StringTagger tagger = StringTagger.getInstance();
                Token[] tokenList = tagger.analyze(word);
                for (int i = 0; i < tokenList.length; i++) {
                    basicWord += tokenList[i].getBasicString();
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        } else if (selectedDoc.getLang().equals("en")) {
            String[] words = word.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String basic = "";
                WordNetDic wordNetAPI = WordNetDic.getInstance();
                IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, words[i].toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma().toLowerCase();
                }
                if (basic.equals("")) {
                    indexWord = wordNetAPI.getIndexWord(POS.VERB, words[i].toLowerCase());
                    if (indexWord != null && indexWord.getLemma() != null) {
                        basic = indexWord.getLemma().toLowerCase();
                    }
                }
                if (basic.equals("")) {
                    basic = words[i];
                }
                basicWord += basic+" ";
            }
            word = basicWord;
        }
        String pos = Translator.getTerm("UserDefinedWordCheckBox");
        InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
        WordInfo info = new WordInfo(word, 1);
        info.addPos(pos);
        info.putInputDoc(selectedDoc.getFile());
        inputWordSelectionPanel.addWordInfo(info);
        setDocumentAndLinkArea();
    }

    class DocumentListCellRenderer extends JRadioButton implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JRadioButton radioButton = (JRadioButton) value;
            setText(radioButton.getText());
            setSelected(radioButton.isSelected());
            return this;
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == documentList &&  e.getModifiers() == InputEvent.BUTTON1_MASK) {
            Point p = e.getPoint();
            int index = documentList.locationToIndex(p);
            if (0 < documentList.getModel().getSize()) {
                lineNum  = 0;
                documentList.setSelectedIndex(0);
                documentAreaBorder.setTitle(Translator.getTerm("InputDocumentArea")+" ("+(lineNum+1)+"-"+ (lineNum+DISPLAY_LINE_NUM) +")");
                JRadioButton radioButton = (JRadioButton) documentList.getModel().getElementAt(index);
                radioButton.setSelected(true);
            }
            if (inputDocumentListModel != null) {
                selectedDoc = (Document) inputDocumentListModel.getElementAt(index);
                setDocumentAndLinkArea();
            }
            repaint();
        } else if (e.getSource() == documentArea && e.getModifiers() == InputEvent.BUTTON3_MASK) {
            addUserDefinedWord(documentArea.getSelectedText());
        }
    }

    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }

    public void setDocumentList(ListModel inputDocListModel) {
        inputDocumentListModel = inputDocListModel;
        DefaultListModel listModel = new DefaultListModel();
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < inputDocumentListModel.getSize(); i++) {
            Document doc = (Document) inputDocListModel.getElementAt(i);
            JRadioButton radioButton = new JRadioButton(doc.getFile().getAbsolutePath());
            listModel.addElement(radioButton);
            group.add(radioButton);
        }
        documentList.setModel(listModel);
    }

    private boolean isNoun(String pos) {
        return pos.indexOf("名詞") != -1 || pos.toLowerCase().indexOf("noun") != -1;
    }
    
    private boolean isVerb(String pos) {
        return pos.indexOf("動詞") != -1 || pos.toLowerCase().indexOf("verb") != -1;
    }
    
    private boolean isComplexword(String pos) {
        return pos.indexOf("複合語") != -1 || pos.toLowerCase().indexOf("complex word") != -1;
    }
    
    private boolean isUserDefinedWord(String pos) {
        return pos.equals(Translator.getTerm("UserDefinedWordCheckBox"));
    }
    
    private boolean isOther(String pos) {
        return !(isNoun(pos) || isVerb(pos) || isComplexword(pos)) || isUserDefinedWord(pos);
    }

    private String getAddedSpaceText(String text) {
        text = text.replaceAll(",", " , ");
        text = text.replaceAll("．", " ． ");
        text = text.replaceAll("\"", " \" ");
        text = text.replaceAll("'", " ' ");
        return text;
    }
    
    private List<String> getEnBasicWordList(String text) {
        List<String > basicWordList = new ArrayList<String>();
        String[] words = getAddedSpaceText(text).split("\\s+");
        text = "";
        for (int i = 0; i < words.length; i++) {
            String word = words[i];                        
            String basic = "";
            WordNetDic wordNetAPI = WordNetDic.getInstance();
            
            IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, word.toLowerCase());
            if (indexWord != null && indexWord.getLemma() != null) {
                basic = indexWord.getLemma().toLowerCase();
                basicWordList.add(basic);
            }
            
            if (basic.equals("")) {
                indexWord = wordNetAPI.getIndexWord(POS.VERB, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma().toLowerCase();
                    basicWordList.add(basic);
                }
            }
            if (basic.equals("")) {
                basicWordList.add(word.toLowerCase());
            }
        }
        return basicWordList;
    }
    
    private List<String> getJaBasicWordList(String text) {
        List<String > basicWordList = new ArrayList<String>();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] tokenList = tagger.analyze(text);
            for (int i = 0; i < tokenList.length; i++) {
               basicWordList.add(tokenList[i].getBasicString());
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return basicWordList;
    }
    
    private List<String> getJaSurfaceWordList(String text) {
        List<String > surfaceList = new ArrayList<String>();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] tokenList = tagger.analyze(text);
            for (int i = 0; i < tokenList.length; i++) {
               surfaceList.add(tokenList[i].getSurface());
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return surfaceList;
    }
    
    
    private String highlightEnText(String text) {
        StringBuilder builder = new StringBuilder();
        String[] words = getAddedSpaceText(text).split("\\s+");
        text = "";
        for (int i = 0; i < words.length; i++) {
            String word = words[i];                        
            String pos = "";
            String basic = "";
            WordNetDic wordNetAPI = WordNetDic.getInstance();
            if (selectedPOS == DODDLE_POS.NOUN) {
                IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma();
                    pos = "noun";
                }
            } else if (selectedPOS == DODDLE_POS.VERB) {
                IndexWord indexWord = wordNetAPI.getIndexWord(POS.VERB, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma();
                    pos = "verb";
                }
            } 
            if (basic.equals("")) {
                basic = word;
            }
            InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
            DocumentSelectionPanel docSelectionPanel = DODDLE.getCurrentProject().getDocumentSelectionPanel();
            if (!docSelectionPanel.isOneWordChecked() && basic.length() == 1) {
                builder.append(word);
                builder.append(" ");
                continue;
            }
            if (docSelectionPanel.isStopWord(basic)) {
                builder.append(word);
                builder.append(" ");
                continue;
            }

            WordInfo wordInfo = inputWordSelectionPanel.getWordInfo(basic);
            WordInfo removedWordInfo = inputWordSelectionPanel.getRemovedWordInfo(basic);
            String type = "";
            if (wordInfo != null) {
                type = "inputword";
            } else if (removedWordInfo != null){
                type = "removedword";
            }
            
            if (type.equals("")) {
                builder.append(word);
            } else  if (selectedPOS == DODDLE_POS.NOUN && isNoun(pos)) {
                builder.append(getHighlightWord(basic, word, type));
            } else if (selectedPOS == DODDLE_POS.VERB && isVerb(pos)) {
                builder.append(getHighlightWord(basic, word, type));                
            } else {
                builder.append(word);
            }
            builder.append(" ");
        }
        return builder.toString();
    }
    
    
    private String highlightJaText(String text) {
        StringBuilder builder = new StringBuilder();
        try {
            StringTagger tagger = StringTagger.getInstance();
            Token[] tokenList = tagger.analyze(text);
            for (int i = 0; i < tokenList.length; i++) {
                Token token = tokenList[i];
                String basic = token.getBasicString();
                String pos = token.getPos();
                String word = token.getSurface();
                InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
                DocumentSelectionPanel docSelectionPanel = DODDLE.getCurrentProject().getDocumentSelectionPanel();
                if (!docSelectionPanel.isOneWordChecked() && basic.length() == 1) {
                    builder.append(word);
                    continue;
                }
                if (docSelectionPanel.isStopWord(basic)) {
                    builder.append(word);
                    continue;
                }
                
                WordInfo wordInfo = inputWordSelectionPanel.getWordInfo(basic);
                WordInfo removedWordInfo = inputWordSelectionPanel.getRemovedWordInfo(basic);
                String type = "";
                if (wordInfo != null) {
                    type = "inputword";
                } else if (removedWordInfo != null){
                    type = "removedword";
                }
                if (type.equals("")) {
                    builder.append(word);
                } else  if (selectedPOS == DODDLE_POS.NOUN && isNoun(pos)) {
                    builder.append(getHighlightWord(basic, word, type));
                } else if (selectedPOS == DODDLE_POS.VERB && isVerb(pos)) {
                    builder.append(getHighlightWord(basic, word, type));                
                } else if (selectedPOS == DODDLE_POS.OTHER && isOther(pos)) {
                    builder.append(getHighlightWord(basic, word, type));
                } else {
                    builder.append(word);
                }
            }
        } catch(IOException ioe) {
           ioe.printStackTrace();
        }
        return builder.toString();
    }
    
    private String getHighlightWord(String basic, String word, String type) {
        String color = CORRECT_WORD_LINK_COLOR;
        if (type.equals("inputword")) {
            color = CORRECT_WORD_LINK_COLOR;    
        } else {
            color = REMOVED_WORD_LINK_COLOR;  
        }
        return "<font color=\""+color+"\"><a href=\""+type+":"+basic+"\">" + word  + "</a></font>";
    }
    
    
    private Set<List<String>> getWordInfoEnComplexWordSet(Collection<WordInfo> wordInfoSet) {
        WordNetDic wordNetAPI = WordNetDic.getInstance();
        Set<List<String>> wordInfoComplexWordSet = new HashSet<List<String>>();
        for (WordInfo info: wordInfoSet) {
            String word = info.getWord();
            Set<String> posSet = info.getPosSet();
            for (String pos: posSet) {
                if (isComplexword(pos) || isUserDefinedWord(pos)) {
                    List<String> complexWordList = new ArrayList<String>();
                    String[] words = word.split("\\s+");
                    for (int i = 0; i < words.length; i++) {
                        String basic = "";
                        IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, words[i]);                        
                        if (indexWord != null && indexWord.getLemma() != null) {
                            basic = indexWord.getLemma();
                        }
                        if (basic.equals("")) {
                            indexWord = wordNetAPI.getIndexWord(POS.VERB, words[i]);
                            if (indexWord != null && indexWord.getLemma() != null) {
                                basic = indexWord.getLemma();
                            }  
                        }
                        if (basic.equals("")) {
                            complexWordList.add(words[i]);
                        } else {
                            complexWordList.add(basic);
                        }
                    }
                    wordInfoComplexWordSet.add(complexWordList);
                    break;
                }
            }
        }
        return wordInfoComplexWordSet;
    }
    
    private Set<List<String>> getWordInfoJaComplexWordSet(Collection<WordInfo> wordInfoSet) {
        Set<List<String>> wordInfoComplexWordSet = new HashSet<List<String>>();
        for (WordInfo info: wordInfoSet) {
            String word = info.getWord();
            Set<String> posSet = info.getPosSet();
            for (String pos: posSet) {
                if (isComplexword(pos) || isUserDefinedWord(pos)) {
                    List<String> complexWordList = new ArrayList<String>();
                    try {
                        StringTagger tagger = StringTagger.getInstance();
                        Token[] tokenList = tagger.analyze(word);
                        for (int i = 0; i < tokenList.length; i++) {
                            complexWordList.add(tokenList[i].getBasicString());
                        }
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                    }
                    wordInfoComplexWordSet.add(complexWordList);
                    break;
                }
            }
        }
        return wordInfoComplexWordSet;
    }
    
    private String highlightEnComplexWord(String text) {
        StringBuilder builder = new StringBuilder();
        List<String> basicStringList = getEnBasicWordList(text);
        
        InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
        Collection<WordInfo> wordInfoSet = inputWordSelectionPanel.getWordInfoSet();
        Collection<WordInfo> removedWordInfoSet = inputWordSelectionPanel.getRemovedWordInfoSet();

        Set<List<String>> wordInfoComplexWordSet = getWordInfoEnComplexWordSet(wordInfoSet);    
        Set<List<String>> removedWordInfoComplexWordSet = getWordInfoEnComplexWordSet(removedWordInfoSet);    
        
        String[] texts = getAddedSpaceText(text).split("\\s+");
        for (int i = 0; i < basicStringList.size(); i++) {
            int complexWordSize = getWordSize(basicStringList, wordInfoComplexWordSet, i);
            int removedComplexWordSize = getWordSize(basicStringList, removedWordInfoComplexWordSet, i);
            String word = "";
            String basicWord = "";
            if (0 < complexWordSize) {
                for (int j = i; j < i+complexWordSize; j++) {
                    word += texts[j];
                    basicWord += basicStringList.get(j);
                    if (j != i+complexWordSize-1) {
                        word += " ";
                        basicWord += " ";
                    }
                }
                builder.append("<font color=\""+CORRECT_WORD_LINK_COLOR+"\"><a href=\"inputword:"+basicWord+"\">" + word  + "</a></font> ");
                i+=complexWordSize-1;
            }  else if (0 < removedComplexWordSize){
                for (int j = i; j < i+removedComplexWordSize; j++) {
                    word += texts[j];
                    basicWord += basicStringList.get(j);
                    if (j != i+removedComplexWordSize-1) {
                        word += " ";
                        basicWord += " ";
                    }
                }
                basicWord = basicWord.replace("\\s+$", "");
                builder.append("<font color=\""+REMOVED_WORD_LINK_COLOR+"\"><a href=\"removedword:"+basicWord+"\">" + word  + "</a></font> ");
                i+=removedComplexWordSize-1;
            }  else {
                word = texts[i]+" ";
                builder.append(word);
            }
        }
        return builder.toString();
    }
    
    private String highlightJaComplexWord(String text) {
        StringBuilder builder = new StringBuilder();
        List<String> basicStringList = getJaBasicWordList(text);
        
        InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
        Collection<WordInfo> wordInfoSet = inputWordSelectionPanel.getWordInfoSet();
        Collection<WordInfo> removedWordInfoSet = inputWordSelectionPanel.getRemovedWordInfoSet();

        Set<List<String>> wordInfoComplexWordSet = getWordInfoJaComplexWordSet(wordInfoSet);    
        Set<List<String>> removedWordInfoComplexWordSet = getWordInfoJaComplexWordSet(removedWordInfoSet);    
        
        List<String> surfaceList = getJaSurfaceWordList(text);
        for (int i = 0; i < basicStringList.size(); i++) {
            int complexWordSize = getWordSize(basicStringList, wordInfoComplexWordSet, i);
            int removedComplexWordSize = getWordSize(basicStringList, removedWordInfoComplexWordSet, i);
            String word = "";
            String basicWord = "";
            if (0 < complexWordSize) {
                for (int j = i; j < i+complexWordSize; j++) {
                    word += surfaceList.get(j);
                    basicWord += basicStringList.get(j);
                }
                builder.append("<font color=\""+CORRECT_WORD_LINK_COLOR+"\"><a href=\"inputword:"+basicWord+"\">" + word  + "</a></font>");
                i+=complexWordSize-1;
            }  else if (0 < removedComplexWordSize){
                for (int j = i; j < i+removedComplexWordSize; j++) {
                    word += surfaceList.get(j);
                    basicWord += basicStringList.get(j);
                }
                builder.append("<font color=\""+REMOVED_WORD_LINK_COLOR+"\"><a href=\"removedword:"+basicWord+"\">" + word  + "</a></font>");
                i+=removedComplexWordSize-1;
            }  else {
                word = surfaceList.get(i);
                builder.append(word);
            }
        }
        return builder.toString();
    }

    /**
     * @param basicStringList
     * @param wordInfoComplexWordSet
     * @param i
     * @return
     */
    private int getWordSize(List<String> basicStringList, Set<List<String>> wordInfoComplexWordSet, int i) {
        int complexWordSize = 0; 
        for (List<String> complexWordList: wordInfoComplexWordSet) {
            boolean isComplexWord = true;
            for (int j = 0; j < complexWordList.size(); j++) {
                if (!complexWordList.get(j).equals(basicStringList.get(i+j))) {
                    isComplexWord = false;
                    break;
                }
            }
            if (isComplexWord) {
                if (complexWordSize < complexWordList.size()) {
                    complexWordSize = complexWordList.size();
                }
            }
        }
        return complexWordSize;
    }

    private int lineNum = 0;
    private Document selectedDoc = null;
    private DODDLE_POS selectedPOS = null;
    
    public String getHighlightedString() {
        if (selectedDoc == null) {
            return "";
        }
        String text = "";
        String[] texts = selectedDoc.getTexts();
        int num = 0; 
        for (int i = lineNum; i < selectedDoc.getSize(); i++,num++) {
            if (num == DISPLAY_LINE_NUM) {
                break;
            }
            text += texts[i]+"<br>";
        }
        
        if (selectedPOS == DODDLE_POS.COMPLEX) {
            if (selectedDoc.getLang().equals("en")) {
                text = highlightEnComplexWord(text);
            } else if (selectedDoc.getLang().equals("ja")){
                text = highlightJaComplexWord(text);
            }
        }else {
            if (selectedDoc.getLang().equals("en")) {
                text = highlightEnText(text);            
            } else if (selectedDoc.getLang().equals("ja")) {
                text = highlightJaText(text);
            }
        }
        return text;
    }
    
    public void setDocumentAndLinkArea() {        
        setDocumentArea();
        setLinkArea();
    }
    
    public void setDocumentArea() {
        if (inputDocumentListModel == null) { return; }
        StringBuilder docBuilder = new StringBuilder();        
        docBuilder.append("<html><body>");        
        String text = getHighlightedString();
        docBuilder.append(text);
        docBuilder.append("<br><br>");
        docBuilder.append("</body></html>");
        setDocumentArea(docBuilder.toString());
    }
    
    private void setDocumentArea(String text) {
        int p = documentArea.getCaretPosition();
        documentArea.setText(text);
        documentArea.setCaretPosition(p);
    }
    
    public void setLinkArea() {
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append("<html><body>");
        if (selectedDoc != null) {
            for (int j = 0; j < selectedDoc.getSize(); j+=DISPLAY_LINE_NUM) {
                if (j == lineNum) {
                    linkBuilder.append("<font size=5 color=red><a href=\""+j+"\">"+(j+1)+"-"+(j+DISPLAY_LINE_NUM)+"</a></font><br>");
                } else {
                    linkBuilder.append("<font color="+CORRECT_WORD_LINK_COLOR+"><a href=\""+j+"\">"+(j+1)+"-"+(j+DISPLAY_LINE_NUM)+"</a></font><br>");
                }
            }
        }
        linkBuilder.append("</body></html>");
        linkArea.setText(linkBuilder.toString());
    }
    
    private boolean isRegisteredWord(String word, String type) {
        InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
        if (type.equals("inputword")) {
            return inputWordSelectionPanel.getWordInfo(word) != null;
        } 
        return inputWordSelectionPanel.getRemovedWordInfo(word) != null;
    }
    
    
    private String getSelectedLinkText(String type, String word) {
        if (!isRegisteredWord(word, type)) {                    
            if (selectedDoc.getLang().equals("ja")) {
                try {
                    StringTagger tagger = StringTagger.getInstance();
                    int num = 0;
                    String text = "";
                    for (int i = lineNum; i < selectedDoc.getSize(); i++,num++) {
                        if (num == DISPLAY_LINE_NUM) {
                            break;
                        }
                        text += selectedDoc.getTexts()[i]+"<br>";
                    }
                    Token[] tokenList = tagger.analyze(text);
                    for (int i = 0; i < tokenList.length; i++) {
                        Token token = tokenList[i];
                        if (token.getSurface().equals(word)) {
                            String basic = token.getBasicString();
                            if (isRegisteredWord(basic, type)) {
                                word = basic;
                                break;
                            }
                        }                                    
                    }                                
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            } else if(selectedDoc.getLang().equals("en")) {
                String[] words = word.split("\\s+");
                String basicWord = "";
                for (int i = 0; i < words.length;  i++) {
                    WordNetDic wordNetAPI = WordNetDic.getInstance();
                    String basic = "";
                    IndexWord indexWord = wordNetAPI.getIndexWord(POS.NOUN, words[i].toLowerCase());
                    if (indexWord != null && indexWord.getLemma() != null) {
                        basic = indexWord.getLemma();
                    }
                    if (basic.equals("")) {
                        indexWord = wordNetAPI.getIndexWord(POS.VERB, words[i].toLowerCase());
                        if (indexWord != null && indexWord.getLemma() != null) {
                            basic = indexWord.getLemma();
                        }
                    }
                    if (basic.equals("")) {
                        basic = words[i];
                    }
                    basicWord += basic+" ";
                }
                word = basicWord;
            }
        }
        return word;
    }
    
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {            
            String[] descriptions = e.getDescription().split(":");
            StringBuilder builder = new StringBuilder();
            builder.append("<html><body>");
            if (descriptions.length == 1) {                
                lineNum = Integer.parseInt(e.getDescription());
                builder.append(getHighlightedString());                            
            } else {
                String type = descriptions[0];
                String word = descriptions[1];
                word = getSelectedLinkText(type, word);
                InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
                if (type.equals("inputword")) {                    
                    inputWordSelectionPanel.removeWord(word);
                } else {
                    inputWordSelectionPanel.addWord(word);
                }
                builder.append(getHighlightedString());  
            }
            builder.append("</body></html>");
            setDocumentArea(builder.toString());
            documentAreaBorder.setTitle(Translator.getTerm("InputDocumentArea")+" ("+(lineNum+1)+"-"+ (lineNum+DISPLAY_LINE_NUM) +")");
            setLinkArea();
            if (descriptions.length == 1) {
                documentArea.setCaretPosition(0);
            }
            repaint();
        } else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            String[] descriptions = e.getDescription().split(":");
            if (descriptions.length == 2) {
                String type = descriptions[0];
                String word = descriptions[1];
                word = getSelectedLinkText(type, word);
                InputWordSelectionPanel inputWordSelectionPanel = DODDLE.getCurrentProject().getInputWordSelectionPanel();
                WordInfo info = null;
                if (type.equals("inputword")) {
                    info = inputWordSelectionPanel.getWordInfo(word);
                } else {
                    info = inputWordSelectionPanel.getRemovedWordInfo(word);
                }
                if (0 < wordInfoTableModel.getRowCount()) {
                    wordInfoTableModel.removeRow(0);
                }
                if (info != null) {
                    wordInfoTableModel.addRow(info.getRowData());
                }
            }
        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            if (0 < wordInfoTableModel.getRowCount()) {
                wordInfoTableModel.removeRow(0);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (searchField.getText().length() == 0) {
            removeHighlights(documentArea);
        } else {
            setHighlight(documentArea, searchField.getText());
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
