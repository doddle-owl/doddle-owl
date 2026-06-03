/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: https://doddle-owl.github.io/
 *
 * Copyright (C) 2004-2026 Takeshi Morita. All rights reserved.
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

package io.github.doddle_owl.views.common;

import io.github.doddle_owl.utils.Translator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Takeshi Morita
 */
public class UnmatchedTermListPanel extends JPanel implements ActionListener {

    public static boolean isDragUnmatchedList = false;

    private final JButton searchButton;
    private final JTextField searchField;

    private final JTextField addWordField;
    private final JButton addButton;
    private final JButton removeButton;

    private ListModel unmatchedTermListModel;
    private final JList unmatchedTermJList;
    private final TitledBorder unmatchedTermJListTitle;

    public UnmatchedTermListPanel() {
        searchButton = new JButton(Translator.getTerm("SearchButton"));
        searchButton.addActionListener(this);
        searchField = new JTextField(30);
        searchField.addActionListener(this);
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        unmatchedTermListModel = new DefaultListModel();
        unmatchedTermJList = new JList(unmatchedTermListModel);
        JScrollPane unmatchedTermJListScroll = new JScrollPane(unmatchedTermJList);
        unmatchedTermJListTitle = BorderFactory.createTitledBorder(Translator.getTerm("UnmatchedTermList"));
        unmatchedTermJList.setDragEnabled(true);
        new DropTarget(unmatchedTermJList, new UnmatchedTermJListDropTargetAdapter());

        addWordField = new JTextField(30);
        addWordField.addActionListener(this);
        addButton = new JButton(Translator.getTerm("AddButton"));
        addButton.addActionListener(this);
        removeButton = new JButton(Translator.getTerm("RemoveButton"));
        removeButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        JPanel addRemovePanel = new JPanel();
        addRemovePanel.setLayout(new BorderLayout());
        addRemovePanel.add(addWordField, BorderLayout.CENTER);
        addRemovePanel.add(buttonPanel, BorderLayout.EAST);
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(addRemovePanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(unmatchedTermJListScroll, BorderLayout.CENTER);
        setBorder(unmatchedTermJListTitle);
    }

    class UnmatchedTermJListDropTargetAdapter extends DropTargetAdapter {

        public void dragEnter(DropTargetDragEvent dtde) {
            isDragUnmatchedList = true;
        }

        public void drop(DropTargetDropEvent dtde) {
        }
    }

    public List getSelectedValuesList() {
        return unmatchedTermJList.getSelectedValuesList();
    }

    public DefaultListModel getModel() {
        return (DefaultListModel) unmatchedTermListModel;
    }

    public DefaultListModel getViewModel() {
        if (unmatchedTermJList.getModel() == unmatchedTermListModel) {
            return null;
        }
        return (DefaultListModel) unmatchedTermJList.getModel();
    }

    public String getSearchRegex() {
        return searchField.getText();
    }

    public void clearSelection() {
        unmatchedTermJList.clearSelection();
    }

    private void setTitle() {
        unmatchedTermJListTitle.setTitle(Translator.getTerm("UnmatchedTermList"));
    }

    public void setTitleWithSize() {
        unmatchedTermJListTitle.setTitle(Translator.getTerm("UnmatchedTermList") + " （"
                + unmatchedTermJList.getModel().getSize() + "/" + getModel().getSize() + "）");
        repaint();
    }

    public void setUnmatchedTermListModel(ListModel model) {
        unmatchedTermListModel = model;
        unmatchedTermJList.setModel(model);
        setTitle();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchField || e.getSource() == searchButton) {
            String searchRegex = searchField.getText();
            if (searchRegex.length() == 0) {
                unmatchedTermJList.setModel(unmatchedTermListModel);
            } else {
                DefaultListModel searchListModel = new DefaultListModel();
                for (int i = 0; i < unmatchedTermListModel.getSize(); i++) {
                    String word = (String) unmatchedTermListModel.getElementAt(i);
                    if (word.matches(searchRegex)) {
                        searchListModel.addElement(word);
                    }
                }
                unmatchedTermJList.setModel(searchListModel);
            }
            setTitleWithSize();
        } else if (e.getSource() == addButton || e.getSource() == addWordField) {
            String addWord = addWordField.getText();
            if (0 < addWord.length()) {
                for (int i = 0; i < unmatchedTermListModel.getSize(); i++) {
                    String word = (String) unmatchedTermListModel.getElementAt(i);
                    if (word.equals(addWord)) {
                        return;
                    }
                }
                ((DefaultListModel) unmatchedTermListModel).addElement(addWord);
                if (getViewModel() != null && addWord.matches(getSearchRegex())) {
                    getViewModel().addElement(addWord);
                }
                setTitleWithSize();
                addWordField.setText("");
            }
        } else if (e.getSource() == removeButton) {
            DefaultListModel model = (DefaultListModel) unmatchedTermJList.getModel();
            List removeWordList = unmatchedTermJList.getSelectedValuesList();
            for (Object word : removeWordList) {
                model.removeElement(word);
                ((DefaultListModel) unmatchedTermListModel).removeElement(word);
            }
            setTitleWithSize();
        }
    }
}
