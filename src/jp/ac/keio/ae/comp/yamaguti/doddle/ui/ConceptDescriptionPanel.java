package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/*
 * @(#)  2005/07/17
 */

/**
 * @author takeshi morita
 */
public class ConceptDescriptionPanel extends JPanel implements ActionListener {

    private JLabel prefixLabel;
    private JLabel prefixValueLabel;
    private JLabel idLabel;
    private JLabel idValueLabel;
    private JLabel typicalWordLabel;
    private JLabel typicalWordValueLabel;
    private JButton setTypcialWordButton;

    private JList jpWordList;
    private DefaultListModel jpWordListModel;
    private JTextField jpWordField;
    private JButton addJPWordButton;
    private JButton deleteJPWordButton;
    private JButton editJPWordButton;

    private JList enWordList;
    private DefaultListModel enWordListModel;
    private JTextField enWordField;
    private JButton addENWordButton;
    private JButton deleteENWordButton;
    private JButton editENWordButton;

    private JTextArea jpExplanationArea;
    private JButton editJPExplanationButton;
    private JTextArea enExplanationArea;
    private JButton editENExplanationButton;

    private JLabel trimmedNodeCntLabel;
    private JLabel trimmedNodeCntValueLabel;

    private JTree conceptTree;

    private EDRConceptDefinitionPanel conceptDefinitionPanel;

    private void init(JTree tree, DefaultTreeCellRenderer renderer) {
        conceptTree = tree;
        prefixLabel = new JLabel("接頭辞: ");
        prefixValueLabel = new JLabel("");
        idLabel = new JLabel("ID: ");
        idValueLabel = new JLabel("");
        typicalWordLabel = new JLabel("代表見出し: ");
        typicalWordValueLabel = new JLabel("");
        setTypcialWordButton = new JButton("代表見出しの設定");
        setTypcialWordButton.addActionListener(this);
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(3, 3, 5, 5));
        northPanel.add(prefixLabel);
        northPanel.add(prefixValueLabel);
        northPanel.add(new JLabel(""));
        northPanel.add(idLabel);
        northPanel.add(idValueLabel);
        northPanel.add(new JLabel(""));
        northPanel.add(typicalWordLabel);
        northPanel.add(typicalWordValueLabel);
        northPanel.add(setTypcialWordButton);

        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new GridLayout(1, 2, 5, 5));
        wordPanel.add(getJPWordPanel());
        wordPanel.add(getENWordPanel());

        JPanel explanationPanel = new JPanel();
        explanationPanel.setLayout(new GridLayout(1, 2, 5, 5));
        explanationPanel.add(getJPExplanationPanel());
        explanationPanel.add(getENExplanationPanel());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));
        centerPanel.add(wordPanel);
        centerPanel.add(explanationPanel);

        trimmedNodeCntLabel = new JLabel("削除された中間概念数： ");
        trimmedNodeCntValueLabel = new JLabel("");
        JPanel trimmedNodeCntPanel = new JPanel();
        trimmedNodeCntPanel.add(trimmedNodeCntLabel);
        trimmedNodeCntPanel.add(trimmedNodeCntValueLabel);

        setBorder(BorderFactory.createTitledBorder("概念について"));
        setLayout(new BorderLayout());
        add(getWestPanel(northPanel), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(getWestPanel(trimmedNodeCntPanel), BorderLayout.SOUTH);
        setTreeConfig(renderer);
    }

    public ConceptDescriptionPanel(JTree tree, DefaultTreeCellRenderer renderer) {
        init(tree, renderer);
    }

    public ConceptDescriptionPanel(JTree tree, DefaultTreeCellRenderer renderer, EDRConceptDefinitionPanel cdp) {
        conceptDefinitionPanel = cdp;
        init(tree, renderer);
    }

    private void setTreeConfig(DefaultTreeCellRenderer renderer) {
        conceptTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if (path == null) { return; }
                ConceptTreeNode conceptTreeNode = (ConceptTreeNode) path.getLastPathComponent();
                if (conceptTreeNode != null) {
                    jpWordField.setText("");
                    enWordField.setText("");
                    prefixValueLabel.setText(conceptTreeNode.getPrefix());
                    idValueLabel.setText(conceptTreeNode.getId());
                    typicalWordValueLabel.setText(conceptTreeNode.getInputWord());
                    setWordList(jpWordListModel, conceptTreeNode.getJpWords());
                    setWordList(enWordListModel, conceptTreeNode.getEnWords());
                    jpExplanationArea.setText(conceptTreeNode.getJpExplanation());
                    enExplanationArea.setText(conceptTreeNode.getEnExplanation());
                    trimmedNodeCntValueLabel.setText(Integer.toString(conceptTreeNode.getTrimmedCount()));
                    if (conceptDefinitionPanel != null && conceptTreeNode.getConcept() instanceof VerbConcept) {
                        conceptDefinitionPanel.init();
                        VerbConcept vc = (VerbConcept) conceptTreeNode.getConcept();
                        conceptDefinitionPanel.setDomainList(vc.getDomainSet());
                        conceptDefinitionPanel.setRangeList(vc.getRangeSet());
                    }
                }
            }
        });

        renderer.setFont(new Font("Dialog", Font.PLAIN, 14));
        conceptTree.setCellRenderer(renderer);
        conceptTree.setEditable(true);
        conceptTree.putClientProperty("JTree.lineStyle", "Angled");
        conceptTree.setVisible(false);
    }

    private JComponent getWestPanel(JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, BorderLayout.WEST);
        return panel;
    }

    private JComponent getJPWordPanel() {
        jpWordListModel = new DefaultListModel();
        jpWordList = new JList(jpWordListModel);
        JScrollPane jpWordListScroll = new JScrollPane(jpWordList);
        jpWordField = new JTextField(15);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridLayout(1, 4, 5, 5));
        addJPWordButton = new JButton("追加");
        addJPWordButton.addActionListener(this);
        deleteJPWordButton = new JButton("削除");
        deleteJPWordButton.addActionListener(this);
        editJPWordButton = new JButton("編集");
        editJPWordButton.addActionListener(this);
        editPanel.add(jpWordField);
        editPanel.add(addJPWordButton);
        editPanel.add(deleteJPWordButton);
        editPanel.add(editJPWordButton);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("日本語見出し"));
        panel.setLayout(new BorderLayout());
        panel.add(jpWordListScroll, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent getENWordPanel() {
        enWordListModel = new DefaultListModel();
        enWordList = new JList(enWordListModel);
        JScrollPane enWordListScroll = new JScrollPane(enWordList);
        enWordField = new JTextField(15);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridLayout(1, 4, 5, 5));
        addENWordButton = new JButton("追加");
        addENWordButton.addActionListener(this);
        deleteENWordButton = new JButton("削除");
        deleteENWordButton.addActionListener(this);
        editENWordButton = new JButton("編集");
        editENWordButton.addActionListener(this);
        editPanel.add(enWordField);
        editPanel.add(addENWordButton);
        editPanel.add(deleteENWordButton);
        editPanel.add(editENWordButton);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("英語見出し"));
        panel.setLayout(new BorderLayout());
        panel.add(enWordListScroll, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent getJPExplanationPanel() {
        jpExplanationArea = new JTextArea();
        jpExplanationArea.setLineWrap(true);
        JScrollPane jpExplanationAreaScroll = new JScrollPane(jpExplanationArea);

        editJPExplanationButton = new JButton("編集");
        editJPExplanationButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(editJPExplanationButton, BorderLayout.EAST);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("日本語説明"));
        panel.setLayout(new BorderLayout());
        panel.add(jpExplanationAreaScroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JComponent getENExplanationPanel() {
        enExplanationArea = new JTextArea();
        enExplanationArea.setLineWrap(true);
        JScrollPane enExplanationAreaScroll = new JScrollPane(enExplanationArea);

        editENExplanationButton = new JButton("編集");
        editENExplanationButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(editENExplanationButton, BorderLayout.EAST);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("英語説明"));
        panel.setLayout(new BorderLayout());
        panel.add(enExplanationAreaScroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setWordList(DefaultListModel listModel, String[] words) {
        listModel.clear();
        for (int i = 0; i < words.length; i++) {
            if (0 < words[i].length()) {
                listModel.addElement(words[i]);
            }
        }
    }

    private boolean deleteWord(String lang, JList wordList, Concept concept) {
        Object[] deleteList = wordList.getSelectedValues();
        DefaultListModel model = (DefaultListModel) wordList.getModel();
        boolean isInputWord = false;
        for (int i = 0; i < deleteList.length; i++) {
            if (deleteList[i].equals(concept.getInputWord())) {
                isInputWord = true;
            }
            model.removeElement(deleteList[i]);
        }
        String wordListStr = "";
        for (int i = 0; i < model.getSize(); i++) {
            if (i == model.getSize() - 1) {
                wordListStr += model.getElementAt(i);
            } else {
                wordListStr += model.getElementAt(i) + "\t";
            }
        }
        if (lang.equals("ja")) {
            concept.setJpWord(wordListStr);
        } else if (lang.equals("en")) {
            concept.setEnWord(wordListStr);
        }
        if (isInputWord) {
            concept.setInputWord();
            typicalWordValueLabel.setText(concept.getInputWord());
            reloadConceptTreeNode(concept);
        }
        return isInputWord;
    }

    private void deleteConceptTreeNode() {
        if (conceptTree.getSelectionCount() == 1) {
            DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) conceptTree.getLastSelectedPathComponent();
            if (node.getParent() != null) {
                model.removeNodeFromParent((DefaultMutableTreeNode) conceptTree.getLastSelectedPathComponent());
            }
        }
    }

    private boolean isSameConcept(Concept concept, ConceptTreeNode node, Set sameConceptSet) {
        return concept.getId().equals(node.getConcept().getId());
    }

    private void searchSameConceptTreeNode(Concept concept, ConceptTreeNode node, Set sameConceptSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (isSameConcept(concept, childNode, sameConceptSet)) {
                sameConceptSet.add(childNode);
            }
            searchSameConceptTreeNode(concept, childNode, sameConceptSet);
        }
    }

    private void reloadConceptTreeNode(Concept concept) {
        DefaultTreeModel treeModel = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
        Set sameConceptSet = new HashSet();
        searchSameConceptTreeNode(concept, rootNode, sameConceptSet);
        for (Iterator i = sameConceptSet.iterator(); i.hasNext();) {
            ConceptTreeNode node = (ConceptTreeNode) i.next();
            treeModel.reload(node);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!(conceptTree.getSelectionCount() == 1)) { return; }
        // DefaultTreeModel treeModel = (DefaultTreeModel)
        // conceptTree.getModel();
        TreePath path = conceptTree.getSelectionPath();
        ConceptTreeNode conceptTreeNode = (ConceptTreeNode) path.getLastPathComponent();
        Concept concept = conceptTreeNode.getConcept();

        if (e.getSource() == addJPWordButton) {
            concept.setJpWord(concept.getJpWord() + "\t" + jpWordField.getText());
            setWordList(jpWordListModel, concept.getJpWords());
        } else if (e.getSource() == deleteJPWordButton) {
            deleteWord("ja", jpWordList, concept);
        } else if (e.getSource() == editJPWordButton) {
            if (jpWordList.getSelectedIndices().length == 1) {
                boolean isInputWord = deleteWord("ja", jpWordList, concept);
                concept.setJpWord(concept.getJpWord() + "\t" + jpWordField.getText());
                if (isInputWord) {
                    concept.setInputWord(concept.getJpWord());
                    typicalWordValueLabel.setText(concept.getJpWord());
                    reloadConceptTreeNode(concept);
                }
                setWordList(jpWordListModel, concept.getJpWords());
            }
        } else if (e.getSource() == addENWordButton) {
            concept.setEnWord(concept.getEnWord() + "\t" + enWordField.getText());
            setWordList(enWordListModel, concept.getEnWords());
        } else if (e.getSource() == deleteENWordButton) {
            deleteWord("en", enWordList, concept);
        } else if (e.getSource() == editENWordButton) {
            if (enWordList.getSelectedIndices().length == 1) {
                boolean isInputWord = deleteWord("en", enWordList, concept);
                concept.setEnWord(concept.getEnWord() + "\t" + enWordField.getText());
                if (isInputWord) {
                    concept.setInputWord(concept.getEnWord());
                    typicalWordValueLabel.setText(concept.getEnWord());
                    reloadConceptTreeNode(concept);
                }
                setWordList(enWordListModel, concept.getEnWords());
            }
        } else if (e.getSource() == editJPExplanationButton) {
            concept.setJpExplanation(jpExplanationArea.getText());
        } else if (e.getSource() == editENExplanationButton) {
            concept.setEnExplanation(enExplanationArea.getText());
        } else if (e.getSource() == setTypcialWordButton) {
            String word = (String) jpWordList.getSelectedValue();
            if (word == null) {
                word = (String) enWordList.getSelectedValue();
            }
            if (word != null) {
                concept.setInputWord(word);
                typicalWordValueLabel.setText(word);
                reloadConceptTreeNode(concept);
            }
        }
        conceptTreeNode.setConcept(concept);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new ConceptDescriptionPanel(new JTree(), new DefaultTreeCellRenderer()));
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}
