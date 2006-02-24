package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

/*
 * @(#)  2005/07/17
 *
 */

/**
 * @author takeshi morita
 */
public class UndefinedWordListPanel extends JPanel implements ActionListener {

    public static boolean isDragUndefinedList = false;

    private JButton searchButton;
    private JTextField searchField;

    private JTextField addWordField;
    private JButton addButton;
    private JButton removeButton;

    private ListModel undefinedWordListModel;
    private JList undefinedWordJList;
    private TitledBorder undefinedWordJListTitle;

    public UndefinedWordListPanel() {
        searchButton = new JButton("検索");
        searchButton.addActionListener(this);
        searchField = new JTextField(30);
        searchField.addActionListener(this);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        undefinedWordListModel = new DefaultListModel();
        undefinedWordJList = new JList(undefinedWordListModel);
        JScrollPane unefinedWordJListScroll = new JScrollPane(undefinedWordJList);
        undefinedWordJListTitle = BorderFactory.createTitledBorder("辞書に載ってない単語リスト");
        unefinedWordJListScroll.setBorder(undefinedWordJListTitle);
        undefinedWordJList.setDragEnabled(true);
        new DropTarget(undefinedWordJList, new UndefinedWordJListDropTargetAdapter());

        addWordField = new JTextField(30);
        addWordField.addActionListener(this);
        addButton = new JButton("追加");
        addButton.addActionListener(this);
        removeButton = new JButton("削除");
        removeButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(addWordField, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(unefinedWordJListScroll, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    class UndefinedWordJListDropTargetAdapter extends DropTargetAdapter {

        public void dragEnter(DropTargetDragEvent dtde) {
            isDragUndefinedList = true;
        }

        public void drop(DropTargetDropEvent dtde) {
        }
    }

    public Object[] getSelectedValues() {
        return undefinedWordJList.getSelectedValues();
    }

    public DefaultListModel getModel() {
        return (DefaultListModel) undefinedWordListModel;
    }

    public DefaultListModel getViewModel() {
        if (undefinedWordJList.getModel() == undefinedWordListModel) { return null; }
        return (DefaultListModel) undefinedWordJList.getModel();
    }

    public String getSearchRegex() {
        return searchField.getText();
    }

    public void clearSelection() {
        undefinedWordJList.clearSelection();
    }

    public void setTitle() {
        undefinedWordJListTitle.setTitle("辞書に載っていない単語リスト");
    }

    public void setTitleWithSize() {
        undefinedWordJListTitle.setTitle("辞書に載っていない単語リスト（" + undefinedWordJList.getModel().getSize() + "/"
                + getModel().getSize() + "）");
        repaint();
    }

    public void setUndefinedWordListModel(ListModel model) {
        undefinedWordListModel = model;
        undefinedWordJList.setModel(model);
        setTitle();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchField || e.getSource() == searchButton) {
            String searchRegex = searchField.getText();
            if (searchRegex.length() == 0) {
                undefinedWordJList.setModel(undefinedWordListModel);
            } else {
                DefaultListModel searchListModel = new DefaultListModel();
                for (int i = 0; i < undefinedWordListModel.getSize(); i++) {
                    String word = (String) undefinedWordListModel.getElementAt(i);
                    if (word.matches(searchRegex)) {
                        searchListModel.addElement(word);
                    }
                }
                undefinedWordJList.setModel(searchListModel);
            }
            setTitleWithSize();
        } else if (e.getSource() == addButton || e.getSource() == addWordField) {
            String addWord = addWordField.getText();
            for (int i = 0; i < undefinedWordListModel.getSize(); i++) {
                String word = (String) undefinedWordListModel.getElementAt(i);
                if (word.equals(addWord)) { return; }
            }
            ((DefaultListModel) undefinedWordListModel).addElement(addWord);
            if (getViewModel() != null && addWord.matches(getSearchRegex())) {
                getViewModel().addElement(addWord);
            }
            setTitleWithSize();
        } else if (e.getSource() == removeButton) {
            DefaultListModel model = (DefaultListModel) undefinedWordJList.getModel();
            Object[] removeWordList = undefinedWordJList.getSelectedValues();
            for (int i = 0; i < removeWordList.length; i++) {
                model.removeElement(removeWordList[i]);
                ((DefaultListModel) undefinedWordListModel).removeElement(removeWordList[i]);
            }
            setTitleWithSize();
        }
    }
}
