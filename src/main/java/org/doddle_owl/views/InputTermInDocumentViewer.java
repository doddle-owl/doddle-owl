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

import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.models.Document;
import org.doddle_owl.models.TermInfo;
import org.doddle_owl.models.WordNetDic;
import org.doddle_owl.utils.Translator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * @author Takeshi Morita
 */
public class InputTermInDocumentViewer extends JPanel implements MouseListener, ActionListener,
        HyperlinkListener, KeyListener {
    private JList documentList;
    private ListModel inputDocumentListModel;
    private JEditorPane documentArea;
    private JEditorPane linkArea;

    private TitledBorder documentAreaBorder;

    private JTextField displayLineNumField;
    private JButton setDisplayLineNumButton;

    private JTextField searchField;
    private JButton addTermButton;
    private JRadioButton compoundWordRadioButton;
    private JRadioButton nounRadioButton;
    private JRadioButton verbRadioButton;
    private JRadioButton otherRadioButton;

    private JTable wordInfoTable;
    private DefaultTableModel wordInfoTableModel;

    private static final String CORRECT_WORD_LINK_COLOR = "#325eff";
    private static final String REMOVED_WORD_LINK_COLOR = "gray";
    private static int DISPLAY_LINE_NUM = 20;

    public InputTermInDocumentViewer() {
        documentList = new JList();
        documentList.addMouseListener(this);
        documentList.setCellRenderer(new DocumentListCellRenderer());
        JScrollPane documentListScroll = new JScrollPane(documentList);
        documentListScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getTerm("InputDocumentList")));
        documentArea = new JEditorPane("text/html", "");
        documentArea.addMouseListener(this);
        documentArea.addHyperlinkListener(this);
        documentArea.setEditable(false);
        JScrollPane documentAreaScroll = new JScrollPane(documentArea);
        documentAreaBorder = BorderFactory.createTitledBorder(Translator
                .getTerm("InputDocumentArea"));
        documentAreaScroll.setBorder(documentAreaBorder);

        linkArea = new JEditorPane("text/html", "");
        linkArea.addHyperlinkListener(this);
        linkArea.setEditable(false);
        JScrollPane linkAreaScroll = new JScrollPane(linkArea);
        linkAreaScroll.setBorder(BorderFactory.createTitledBorder(Translator
                .getTerm("PageLinesList")));
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

        addTermButton = new JButton(Translator.getTerm("AddButton"));
        addTermButton.addActionListener(this);

        compoundWordRadioButton = new JRadioButton(Translator.getTerm("GensenCheckBox"));
        compoundWordRadioButton.addActionListener(this);
        nounRadioButton = new JRadioButton(Translator.getTerm("NounCheckBox"));
        nounRadioButton.addActionListener(this);
        verbRadioButton = new JRadioButton(Translator.getTerm("VerbCheckBox"));
        verbRadioButton.addActionListener(this);
        otherRadioButton = new JRadioButton(Translator.getTerm("OtherPOSCheckBox"));
        otherRadioButton.addActionListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(compoundWordRadioButton);
        group.add(nounRadioButton);
        group.add(verbRadioButton);
        group.add(otherRadioButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(displayLineNumField);
        buttonPanel.add(setDisplayLineNumButton);
        buttonPanel.add(searchField);
        buttonPanel.add(addTermButton);
        buttonPanel.add(compoundWordRadioButton);
        buttonPanel.add(nounRadioButton);
        buttonPanel.add(verbRadioButton);
        buttonPanel.add(otherRadioButton);

        wordInfoTable = new JTable();
        JScrollPane wordInfoTableScroll = new JScrollPane(wordInfoTable);
        wordInfoTableScroll.setPreferredSize(new Dimension(60, 60));
        wordInfoTableScroll.setMinimumSize(new Dimension(60, 60));
        setWordInfoTableModel();

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        southPanel.add(wordInfoTableScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, documentListScroll,
                documentPanel);
        splitPane.setDividerSize(DODDLEConstants.DIVIDER_SIZE);
        splitPane.setOneTouchExpandable(true);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    public void setWordInfoTableModel() {
        String WORD = Translator.getTerm("TermLabel");
        String POS = Translator.getTerm("POSLabel");
        String TF = Translator.getTerm("TFLabel");
        String IDF = Translator.getTerm("IDFLabel");
        String TFIDF = Translator.getTerm("TFIDFLabel");
        // String INPUT_DOCUMENT = Translator.getTerm("InputDocumentLabel");
        String UPPER_CONCEPT = Translator.getTerm("UpperConceptLabel");
        // Object[] titles = new Object[] { WORD, POS, TF, IDF, TFIDF,
        // INPUT_DOCUMENT, UPPER_CONCEPT};
        Object[] titles = new Object[]{WORD, POS, TF, IDF, TFIDF, UPPER_CONCEPT};

        wordInfoTableModel = new DefaultTableModel(null, titles);
        wordInfoTableModel.getColumnClass(0);

        wordInfoTable.setModel(wordInfoTableModel);
        wordInfoTable.getTableHeader().setToolTipText("sorted by column");
    }

    private final Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
            Color.YELLOW);

    public void removeHighlights(JTextComponent jtc) {
        Highlighter highlight = jtc.getHighlighter();
        Highlighter.Highlight[] highlights = highlight.getHighlights();
        for (Highlighter.Highlight highlight1 : highlights) {
            if (highlight1.getPainter() instanceof DefaultHighlighter.DefaultHighlightPainter) {
                highlight.removeHighlight(highlight1);
            }
        }
    }

    public void setHighlight(JTextComponent jtc, String pattern) {
        removeHighlights(jtc);
        try {
            Highlighter highlight = jtc.getHighlighter();
            javax.swing.text.Document doc = jtc.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                highlight.addHighlight(pos, pos + pattern.length(), highlightPainter);
                pos += pattern.length();
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    enum DODDLE_POS {
        COMPOUND_WORD, NOUN, VERB, OTHER
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == compoundWordRadioButton) {
            selectedPOS = DODDLE_POS.COMPOUND_WORD;
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
        } else if (e.getSource() == addTermButton) {
            addUserDefinedWord(searchField.getText());
        } else if (e.getSource() == setDisplayLineNumButton) {
            try {
                int num = Integer.parseInt(displayLineNumField.getText());
                if (0 < num) {
                    DISPLAY_LINE_NUM = num;
                    documentArea.setCaretPosition(0);
                    setDocumentAndLinkArea();
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
    }

    /**
     * 抽出できなかった入力単語をユーザ定義単語として追加する
     */
    private void addUserDefinedWord(String word) {
        if (word == null || word.length() == 0) {
            return;
        }
        word = word.replaceAll("<", "");
        word = word.replaceAll(">", "");
        String basicWord = "";
        if (selectedDoc.getLang().equals("ja")) {
            try {
                StringTagger tagger = SenFactory.getStringTagger(null);
                List<Token> tokenList = new ArrayList<>();
                tagger.analyze(word, tokenList);
                for (Token token : tokenList) {
                    String bw = token.getMorpheme().getBasicForm();
                    if (bw.equals("*")) {
                        bw = token.getSurface();
                    }
                    basicWord += bw;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else if (selectedDoc.getLang().equals("en")) {
            String[] words = word.split("\\s+");
            for (String word1 : words) {
                String basic = "";
                IndexWord indexWord = WordNetDic.getIndexWord(POS.NOUN, word1.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma().toLowerCase();
                }
                if (basic.equals("")) {
                    indexWord = WordNetDic.getIndexWord(POS.VERB, word1.toLowerCase());
                    if (indexWord != null && indexWord.getLemma() != null) {
                        basic = indexWord.getLemma().toLowerCase();
                    }
                }
                if (basic.equals("")) {
                    basic = word1;
                }
                basicWord += basic + " ";
            }
            word = basicWord;
        }
        String pos = Translator.getTerm("UserDefinedInputTermCheckBox");
        InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                .getInputTermSelectionPanel();
        TermInfo info = new TermInfo(word, 1);
        info.addPos(pos);
        info.putInputDoc(selectedDoc.getFile());
        inputTermSelectionPanel.addInputTermInfo(info);
        setDocumentAndLinkArea();
    }

    class DocumentListCellRenderer extends JRadioButton implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JRadioButton radioButton = (JRadioButton) value;
            setText(radioButton.getText());
            setSelected(radioButton.isSelected());
            return this;
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == documentList && e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            Point p = e.getPoint();
            int index = documentList.locationToIndex(p);
            if (0 < documentList.getModel().getSize()) {
                lineNum = 0;
                documentList.setSelectedIndex(0);
                documentAreaBorder.setTitle(Translator.getTerm("InputDocumentArea") + " ("
                        + (lineNum + 1) + "-" + (lineNum + DISPLAY_LINE_NUM) + ")");
                JRadioButton radioButton = (JRadioButton) documentList.getModel().getElementAt(
                        index);
                radioButton.setSelected(true);
            }
            if (inputDocumentListModel != null) {
                selectedDoc = (Document) inputDocumentListModel.getElementAt(index);
                setDocumentAndLinkArea();
            }
            repaint();
        } else if (e.getSource() == documentArea && e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK) {
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
        return pos.contains("名詞") || pos.toLowerCase().contains("noun");
    }

    private boolean isVerb(String pos) {
        return pos.contains("動詞") || pos.toLowerCase().contains("verb");
    }

    private boolean isCompoundWord(String pos) {
        return pos.contains(InputDocumentSelectionPanel.COMPOUND_WORD_JA)
                || pos.toLowerCase().contains(InputDocumentSelectionPanel.COMPOUND_WORD_EN);
    }

    private boolean isUserDefinedWord(String pos) {
        return pos.equals(Translator.getTerm("UserDefinedInputTermCheckBox"));
    }

    private boolean isOther(String pos) {
        return !(isNoun(pos) || isVerb(pos) || isCompoundWord(pos)) || isUserDefinedWord(pos);
    }

    private String getAddedSpaceText(String text) {
        text = text.replaceAll(",", " , ");
        text = text.replaceAll("．", " ． ");
        text = text.replaceAll("\"", " \" ");
        text = text.replaceAll("'", " ' ");
        return text;
    }

    private List<String> getEnBasicWordList(String text) {
        List<String> basicWordList = new ArrayList<>();
        String[] words = getAddedSpaceText(text).split("\\s+");
        for (String word : words) {
            String basic = "";

            IndexWord indexWord = WordNetDic.getIndexWord(POS.NOUN, word.toLowerCase());
            if (indexWord != null && indexWord.getLemma() != null) {
                basic = indexWord.getLemma().toLowerCase();
                basicWordList.add(basic);
            }

            if (basic.equals("")) {
                indexWord = WordNetDic.getIndexWord(POS.VERB, word.toLowerCase());
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
        List<String> basicWordList = new ArrayList<>();
        try {
            StringTagger tagger = SenFactory.getStringTagger(null);
            List<Token> tokenList = new ArrayList<>();
            tagger.analyze(text, tokenList);
            for (Token token : tokenList) {
                String basicForm = token.getMorpheme().getBasicForm();
                if (basicForm.equals("*")) {
                    basicForm = token.getSurface();
                }
                basicWordList.add(basicForm);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return basicWordList;
    }

    private List<String> getJaSurfaceWordList(String text) {
        List<String> surfaceList = new ArrayList<>();
        try {
            StringTagger tagger = SenFactory.getStringTagger(null);
            List<Token> tokenList = new ArrayList<>();
            tagger.analyze(text, tokenList);
            for (Token token : tokenList) {
                surfaceList.add(token.getSurface());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return surfaceList;
    }

    private String highlightEnText(String text) {
        StringBuilder builder = new StringBuilder();
        String[] words = getAddedSpaceText(text).split("\\s+");
        for (String word : words) {
            String pos = "";
            String basic = "";
            if (selectedPOS == DODDLE_POS.NOUN) {
                IndexWord indexWord = WordNetDic.getIndexWord(POS.NOUN, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma();
                    pos = "noun";
                }
            } else if (selectedPOS == DODDLE_POS.VERB) {
                IndexWord indexWord = WordNetDic.getIndexWord(POS.VERB, word.toLowerCase());
                if (indexWord != null && indexWord.getLemma() != null) {
                    basic = indexWord.getLemma();
                    pos = "verb";
                }
            }
            if (basic.equals("")) {
                basic = word;
            }
            InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                    .getInputTermSelectionPanel();
            InputDocumentSelectionPanel docSelectionPanel = DODDLE_OWL.getCurrentProject()
                    .getDocumentSelectionPanel();
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

            TermInfo wordInfo = inputTermSelectionPanel.getInputTermInfo(basic);
            TermInfo removedWordInfo = inputTermSelectionPanel.getRemovedTermInfo(basic);
            String type = "";
            if (wordInfo != null) {
                type = "inputTerm";
            } else if (removedWordInfo != null) {
                type = "removedTerm";
            }

            if (type.equals("")) {
                builder.append(word);
            } else if (selectedPOS == DODDLE_POS.NOUN && isNoun(pos)) {
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
            StringTagger tagger = SenFactory.getStringTagger(null);
            List<Token> tokenList = new ArrayList<>();
            tagger.analyze(text, tokenList);
            for (Token token : tokenList) {
                String basic = token.getMorpheme().getBasicForm();
                if (basic.equals("*")) {
                    basic = token.getSurface();
                }
                String pos = token.getMorpheme().getPartOfSpeech();
                String word = token.getSurface();
                InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                        .getInputTermSelectionPanel();
                InputDocumentSelectionPanel docSelectionPanel = DODDLE_OWL.getCurrentProject()
                        .getDocumentSelectionPanel();
                if (!docSelectionPanel.isOneWordChecked() && basic.length() == 1) {
                    builder.append(word);
                    continue;
                }
                if (docSelectionPanel.isStopWord(basic)) {
                    builder.append(word);
                    continue;
                }

                TermInfo termInfo = inputTermSelectionPanel.getInputTermInfo(basic);
                TermInfo removedTermInfo = inputTermSelectionPanel.getRemovedTermInfo(basic);
                String type = "";
                if (termInfo != null) {
                    type = "inputTerm";
                } else if (removedTermInfo != null) {
                    type = "removedTerm";
                }
                if (type.equals("")) {
                    builder.append(word);
                } else if (selectedPOS == DODDLE_POS.NOUN && isNoun(pos)) {
                    builder.append(getHighlightWord(basic, word, type));
                } else if (selectedPOS == DODDLE_POS.VERB && isVerb(pos)) {
                    builder.append(getHighlightWord(basic, word, type));
                } else if (selectedPOS == DODDLE_POS.OTHER && isOther(pos)) {
                    builder.append(getHighlightWord(basic, word, type));
                } else {
                    builder.append(word);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return builder.toString();
    }

    private String getHighlightWord(String basic, String word, String type) {
        String color;
        if (type.equals("inputTerm")) {
            color = CORRECT_WORD_LINK_COLOR;
        } else {
            color = REMOVED_WORD_LINK_COLOR;
        }
        return "<font color=\"" + color + "\"><a href=\"" + type + ":" + basic + "\">" + word
                + "</a></font>";
    }

    private Set<List<String>> getTermInfoEnCompoundWordSet(Collection<TermInfo> termInfoSet) {
        Set<List<String>> termInfoCompoundWordSet = new HashSet<>();
        for (TermInfo info : termInfoSet) {
            String word = info.getTerm();
            Set<String> posSet = info.getPosSet();
            for (String pos : posSet) {
                if (isCompoundWord(pos) || isUserDefinedWord(pos)) {
                    List<String> compoundWordList = new ArrayList<>();
                    String[] words = word.split("\\s+");
                    for (String word1 : words) {
                        String basic = "";
                        IndexWord indexWord = WordNetDic.getIndexWord(POS.NOUN, word1);
                        if (indexWord != null && indexWord.getLemma() != null) {
                            basic = indexWord.getLemma();
                        }
                        if (basic.equals("")) {
                            indexWord = WordNetDic.getIndexWord(POS.VERB, word1);
                            if (indexWord != null && indexWord.getLemma() != null) {
                                basic = indexWord.getLemma();
                            }
                        }
                        if (basic.equals("")) {
                            compoundWordList.add(word1);
                        } else {
                            compoundWordList.add(basic);
                        }
                    }
                    termInfoCompoundWordSet.add(compoundWordList);
                    break;
                }
            }
        }
        return termInfoCompoundWordSet;
    }

    private Set<List<String>> getTermInfoJaCompoundWordSet(Collection<TermInfo> termInfoSet) {
        Set<List<String>> termInfoCompoundWordSet = new HashSet<>();
        for (TermInfo info : termInfoSet) {
            String word = info.getTerm();
            Set<String> posSet = info.getPosSet();
            for (String pos : posSet) {
                if (isCompoundWord(pos) || isUserDefinedWord(pos)) {
                    List<String> compoundWordList = new ArrayList<>();
                    try {
                        StringTagger tagger = SenFactory.getStringTagger(null);
                        List<Token> tokenList = new ArrayList<>();
                        tagger.analyze(word, tokenList);
                        for (Token token : tokenList) {
                            String bf = token.getMorpheme().getBasicForm();
                            if (bf.equals("*")) {
                                bf = token.getSurface();
                            }
                            compoundWordList.add(bf);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    termInfoCompoundWordSet.add(compoundWordList);
                    break;
                }
            }
        }
        return termInfoCompoundWordSet;
    }

    private String highlightEnCompoundWord(String text) {
        StringBuilder builder = new StringBuilder();
        List<String> basicStringList = getEnBasicWordList(text);

        InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                .getInputTermSelectionPanel();
        Collection<TermInfo> wordInfoSet = inputTermSelectionPanel.getTermInfoSet();
        Collection<TermInfo> removedWordInfoSet = inputTermSelectionPanel.getRemovedTermInfoSet();

        Set<List<String>> termInfoCompoundWordSet = getTermInfoEnCompoundWordSet(wordInfoSet);
        Set<List<String>> removedTermInfoCompoundWordSet = getTermInfoEnCompoundWordSet(removedWordInfoSet);

        String[] texts = getAddedSpaceText(text).split("\\s+");
        for (int i = 0; i < basicStringList.size(); i++) {
            int compoundWordSize = getTermSize(basicStringList, termInfoCompoundWordSet, i);
            int removedCompoundWordSize = getTermSize(basicStringList,
                    removedTermInfoCompoundWordSet, i);
            String word = "";
            String basicWord = "";
            if (0 < compoundWordSize) {
                for (int j = i; j < i + compoundWordSize; j++) {
                    word += texts[j];
                    basicWord += basicStringList.get(j);
                    if (j != i + compoundWordSize - 1) {
                        word += " ";
                        basicWord += " ";
                    }
                }
                builder.append("<font color=\"" + CORRECT_WORD_LINK_COLOR
                        + "\"><a href=\"inputTerm:" + basicWord + "\">" + word + "</a></font> ");
                i += compoundWordSize - 1;
            } else if (0 < removedCompoundWordSize) {
                for (int j = i; j < i + removedCompoundWordSize; j++) {
                    word += texts[j];
                    basicWord += basicStringList.get(j);
                    if (j != i + removedCompoundWordSize - 1) {
                        word += " ";
                        basicWord += " ";
                    }
                }
                basicWord = basicWord.replace("\\s+$", "");
                builder.append("<font color=\"" + REMOVED_WORD_LINK_COLOR
                        + "\"><a href=\"removedTerm:" + basicWord + "\">" + word + "</a></font> ");
                i += removedCompoundWordSize - 1;
            } else {
                word = texts[i] + " ";
                builder.append(word);
            }
        }
        return builder.toString();
    }

    private String highlightJaCompoundWord(String text) {
        StringBuilder builder = new StringBuilder();
        List<String> basicStringList = getJaBasicWordList(text);

        InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                .getInputTermSelectionPanel();
        Collection<TermInfo> wordInfoSet = inputTermSelectionPanel.getTermInfoSet();
        Collection<TermInfo> removedWordInfoSet = inputTermSelectionPanel.getRemovedTermInfoSet();

        Set<List<String>> termInfoCompoundWordSet = getTermInfoJaCompoundWordSet(wordInfoSet);
        Set<List<String>> removedTermInfoCompoundWordSet = getTermInfoJaCompoundWordSet(removedWordInfoSet);

        List<String> surfaceList = getJaSurfaceWordList(text);
        for (int i = 0; i < basicStringList.size(); i++) {
            int compoundWordSize = getTermSize(basicStringList, termInfoCompoundWordSet, i);
            int removedCompoundWordSize = getTermSize(basicStringList,
                    removedTermInfoCompoundWordSet, i);
            String word = "";
            String basicWord = "";
            if (0 < compoundWordSize) {
                for (int j = i; j < i + compoundWordSize; j++) {
                    word += surfaceList.get(j);
                    basicWord += basicStringList.get(j);
                }
                if (inputTermSelectionPanel.getInputTermInfo(basicWord) != null) {
                    builder.append("<font color=\"" + CORRECT_WORD_LINK_COLOR
                            + "\"><a href=\"inputTerm:" + basicWord + "\">" + word + "</a></font>");
                    i += compoundWordSize - 1;
                } else {
                    word = surfaceList.get(i);
                    builder.append(word);
                }
            } else if (0 < removedCompoundWordSize) {
                for (int j = i; j < i + removedCompoundWordSize; j++) {
                    word += surfaceList.get(j);
                    basicWord += basicStringList.get(j);
                }
                if (inputTermSelectionPanel.getRemovedTermInfo(basicWord) != null) {
                    builder.append("<font color=\"" + REMOVED_WORD_LINK_COLOR
                            + "\"><a href=\"removedTerm:" + basicWord + "\">" + word
                            + "</a></font>");
                    i += removedCompoundWordSize - 1;
                } else {
                    word = surfaceList.get(i);
                    builder.append(word);
                }
            } else {
                word = surfaceList.get(i);
                builder.append(word);
            }
        }
        return builder.toString();
    }

    /**
     * @param basicStringList
     * @param termInfoCompoundWordSet
     * @param i
     * @return
     */
    private int getTermSize(List<String> basicStringList,
                            Set<List<String>> termInfoCompoundWordSet, int i) {
        int compoundWordSize = 0;
        for (List<String> compoundWordList : termInfoCompoundWordSet) {
            boolean isCompoundWord = true;
            for (int j = 0; j < compoundWordList.size(); j++) {
                if (!compoundWordList.get(j).equals(basicStringList.get(i + j))) {
                    isCompoundWord = false;
                    break;
                }
            }
            if (isCompoundWord) {
                if (compoundWordSize < compoundWordList.size()) {
                    compoundWordSize = compoundWordList.size();
                }
            }
        }
        return compoundWordSize;
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
        for (int i = lineNum; i < selectedDoc.getSize(); i++, num++) {
            if (num == DISPLAY_LINE_NUM) {
                break;
            }
            text += texts[i] + "<br>";
        }

        if (selectedPOS == DODDLE_POS.COMPOUND_WORD) {
            if (selectedDoc.getLang().equals("en")) {
                text = highlightEnCompoundWord(text);
            } else if (selectedDoc.getLang().equals("ja")) {
                text = highlightJaCompoundWord(text);
            }
        } else {
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
        if (inputDocumentListModel == null) {
            return;
        }
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
        try {
            documentArea.setCaretPosition(p);
        } catch (Exception e) {
            // 無視
        }
    }

    public void setLinkArea() {
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append("<html><body>");
        if (selectedDoc != null) {
            for (int j = 0; j < selectedDoc.getSize(); j += DISPLAY_LINE_NUM) {
                if (j == lineNum) {
                    linkBuilder.append("<font size=5 color=red><a href=\"" + j + "\">" + (j + 1)
                            + "-" + (j + DISPLAY_LINE_NUM) + "</a></font><br>");
                } else {
                    linkBuilder.append("<font color=" + CORRECT_WORD_LINK_COLOR + "><a href=\"" + j
                            + "\">" + (j + 1) + "-" + (j + DISPLAY_LINE_NUM) + "</a></font><br>");
                }
            }
        }
        linkBuilder.append("</body></html>");
        linkArea.setText(linkBuilder.toString());
    }

    private boolean isRegisteredWord(String word, String type) {
        InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                .getInputTermSelectionPanel();
        if (type.equals("inputTerm")) {
            return inputTermSelectionPanel.getInputTermInfo(word) != null;
        }
        return inputTermSelectionPanel.getRemovedTermInfo(word) != null;
    }

    private String getSelectedLinkText(String type, String word) {
        if (!isRegisteredWord(word, type)) {
            if (selectedDoc.getLang().equals("ja")) {
                try {
                    StringTagger tagger = SenFactory.getStringTagger(null);
                    int num = 0;
                    String text = "";
                    for (int i = lineNum; i < selectedDoc.getSize(); i++, num++) {
                        if (num == DISPLAY_LINE_NUM) {
                            break;
                        }
                        text += selectedDoc.getTexts()[i] + "<br>";
                    }
                    List<Token> tokenList = new ArrayList<>();
                    tagger.analyze(text, tokenList);
                    for (Token token : tokenList) {
                        if (token.getSurface().equals(word)) {
                            String basic = token.getMorpheme().getBasicForm();
                            if (basic.equals("*")) {
                                basic = token.getSurface();
                            }
                            if (isRegisteredWord(basic, type)) {
                                word = basic;
                                break;
                            }
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else if (selectedDoc.getLang().equals("en")) {
                String[] words = word.split("\\s+");
                String basicWord = "";
                for (String word1 : words) {
                    String basic = "";
                    IndexWord indexWord = WordNetDic.getIndexWord(POS.NOUN, word1.toLowerCase());
                    if (indexWord != null && indexWord.getLemma() != null) {
                        basic = indexWord.getLemma();
                    }
                    if (basic.equals("")) {
                        indexWord = WordNetDic.getIndexWord(POS.VERB, word1.toLowerCase());
                        if (indexWord != null && indexWord.getLemma() != null) {
                            basic = indexWord.getLemma();
                        }
                    }
                    if (basic.equals("")) {
                        basic = word1;
                    }
                    basicWord += basic + " ";
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
                InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                        .getInputTermSelectionPanel();
                if (type.equals("inputTerm")) {
                    inputTermSelectionPanel.removeTerm(word);
                } else {
                    inputTermSelectionPanel.addTerm(word);
                }
                builder.append(getHighlightedString());
            }
            builder.append("</body></html>");
            setDocumentArea(builder.toString());
            documentAreaBorder.setTitle(Translator.getTerm("InputDocumentArea") + " ("
                    + (lineNum + 1) + "-" + (lineNum + DISPLAY_LINE_NUM) + ")");
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
                InputTermSelectionPanel inputTermSelectionPanel = DODDLE_OWL.getCurrentProject()
                        .getInputTermSelectionPanel();
                TermInfo info;
                if (type.equals("inputTerm")) {
                    info = inputTermSelectionPanel.getInputTermInfo(word);
                } else {
                    info = inputTermSelectionPanel.getRemovedTermInfo(word);
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
