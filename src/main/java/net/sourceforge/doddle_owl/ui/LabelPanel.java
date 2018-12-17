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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.sourceforge.doddle_owl.data.*;
import net.sourceforge.doddle_owl.utils.*;

/**
 * @author Takeshi Morita
 */
public class LabelPanel extends LiteralPanel implements ActionListener {

    private JLabel preferentialTermLabel;
    private JLabel preferentialTermValueLabel;
    private JTextField langField;
    private JTextField labelField;
    private JButton addLabelButton;
    private JButton deleteLabelButton;
    private JButton editLabelButton;
    private JButton setTypcialLabelButton;

    private ConceptInformationPanel conceptInfoPanel;

    @Override
    public void setSelectedConcept(Concept c) {
        super.setSelectedConcept(c);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LabelPanel(String type, ConceptInformationPanel ciPanel) {
        super(Translator.getTerm("LanguageLabel"), Translator.getTerm("LabelList"), type);
        conceptInfoPanel = ciPanel;

        preferentialTermLabel = new JLabel(Translator.getTerm("PreferentialTermLabel") + ": ");
        preferentialTermValueLabel = new JLabel("");
        JPanel preferentialTermPanel = new JPanel();
        preferentialTermPanel.setLayout(new GridLayout(1, 2));
        preferentialTermPanel.add(preferentialTermLabel);
        preferentialTermPanel.add(preferentialTermValueLabel);

        langField = new JTextField(5);
        langField.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("LangTextField")));
        labelField = new JTextField(15);
        labelField.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("LabelTextField")));

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.add(langField, BorderLayout.WEST);
        fieldPanel.add(labelField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        addLabelButton = new JButton(Translator.getTerm("AddButton"));
        addLabelButton.addActionListener(this);
        deleteLabelButton = new JButton(Translator.getTerm("RemoveButton"));
        deleteLabelButton.addActionListener(this);
        editLabelButton = new JButton(Translator.getTerm("EditButton"));
        editLabelButton.addActionListener(this);
        setTypcialLabelButton = new JButton(Translator.getTerm("SetPreferentialTermButton"));
        setTypcialLabelButton.addActionListener(this);
        buttonPanel.add(setTypcialLabelButton);
        buttonPanel.add(addLabelButton);
        buttonPanel.add(editLabelButton);
        buttonPanel.add(deleteLabelButton);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        editPanel.add(fieldPanel, BorderLayout.NORTH);
        editPanel.add(buttonPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createTitledBorder(Translator.getTerm("LabelBorder")));
        add(preferentialTermPanel, BorderLayout.NORTH);
        add(editPanel, BorderLayout.SOUTH);
    }

    public void setPreferentialTerm(String word) {
        preferentialTermValueLabel.setText(word);
    }

    public void clearPreferentialTermValue() {
        preferentialTermValueLabel.setText("");
    }

    public void clearLabelField() {
        langField.setText("");
        labelField.setText("");
    }

    public void setLabelList() {
        super.setLabelList();
        if (langJList.getSelectedValuesList().isEmpty()) {
            langField.setText("");
        } else if (langJList.getSelectedValuesList().size() == 1) {
            langField.setText(langJList.getSelectedValue().toString());
        }
    }

    public void setField() {
        if (literalJList.getSelectedValuesList().isEmpty()) {
            labelField.setText("");
        } else if (literalJList.getSelectedValuesList().size() == 1) {
            DODDLELiteral label = (DODDLELiteral) literalJList.getSelectedValue();
            labelField.setText(label.getString());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (selectedConcept == null) {
            return;
        }
        if (e.getSource() == addLabelButton) {
            selectedConcept.addLabel(new DODDLELiteral(langField.getText(), labelField.getText()));
            setLabelLangList();
            clearLabelField();
        } else if (e.getSource() == deleteLabelButton) {
            java.util.List<DODDLELiteral> labelList = literalJList.getSelectedValuesList();
            for (int i = 0; i < labelList.size(); i++) {
                DODDLELiteral label = labelList.get(i);
                if (label.getString().equals(preferentialTermValueLabel.getText())) {
                    conceptInfoPanel.setPreferentialTerm(selectedConcept, new DODDLELiteral("", ""));
                }
                selectedConcept.removeLabel(label);
            }
            setLabelLangList();
            clearLabelField();
        } else if (e.getSource() == editLabelButton) {
            if (literalJList.getSelectedIndices().length == 1 && 0 < labelField.getText().length()) {
                DODDLELiteral label = (DODDLELiteral) literalJList.getSelectedValue();
                String labelText = label.getString();
                if (label.getLang().equals(langField.getText())) {
                    label.setString(labelField.getText());
                } else {
                    selectedConcept.removeLabel(label);
                    label.setLang(langField.getText());
                    label.setString(labelField.getText());
                    selectedConcept.addLabel(label);
                }
                if (labelText.equals(preferentialTermValueLabel.getText())) {
                    conceptInfoPanel.setPreferentialTerm(selectedConcept, label);
                }
                setLabelLangList();
                clearLabelField();
                conceptInfoPanel.reloadConceptTreeNode(selectedConcept);
                conceptInfoPanel.reloadHasaTreeNode(selectedConcept);
            }
        } else if (e.getSource() == setTypcialLabelButton) {
            DODDLELiteral label = (DODDLELiteral) literalJList.getSelectedValue();
            conceptInfoPanel.setPreferentialTerm(selectedConcept, label);
        }
    }
}
