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

package io.github.doddle_owl.views.reference_ontology_selection;

import io.github.doddle_owl.DODDLE_OWL;
import io.github.doddle_owl.models.reference_ontology_selection.WebOntologyExtractionTemplate;
import io.github.doddle_owl.models.reference_ontology_selection.ReferenceWebOntology;
import io.github.doddle_owl.utils.Translator;
import io.github.doddle_owl.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Takeshi Morita
 */
class OWLMetaDataPanel extends JPanel implements ActionListener {

    private final JCheckBox isAvailableCheckBox;

    private final JLabel locationLabel;

    private final OWLMetaDataTablePanel owlMetaDataTablePanel;

    private final JTextField isaRelationExtractionTemplateField;
    private final JButton setIsaRelationExtractionTemplateButton;
    private final JTextField annotationExtractionTemplateField;
    private final JButton setAnnotationExtractionTemplateButton;
    private final JTextField classExtractionTemplateField;
    private final JButton setClassExtractionTemplateButton;
    private final JTextField propertyExtractionTemplateField;
    private final JButton setPropertyExtractionTemplateButton;
    private final JTextField domainRangeExtractionTemplateField;
    private final JButton setDomainRangeExtractionTemplateButton;

    private ReferenceWebOntology refOnt;
    private static final int TEXT_FIELD_WIDTH = 30;

    public OWLMetaDataPanel() {

        JPanel templatePanel = new JPanel();
        templatePanel.setLayout(new GridLayout(8, 1));

        isAvailableCheckBox = new JCheckBox(Translator.getTerm("AvailableCheckBox"));
        isAvailableCheckBox.addActionListener(this);
        templatePanel.add(isAvailableCheckBox);

        JLabel locationTitleLabel = new JLabel(Translator.getTerm("LocationLabel") + ": ");
        locationLabel = new JLabel();
        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BorderLayout());
        locationPanel.add(locationTitleLabel, BorderLayout.WEST);
        locationPanel.add(locationLabel, BorderLayout.CENTER);
        templatePanel.add(locationPanel);
        String browse = Translator.getTerm("ReferenceButton");

        JLabel searchClassSetTemplateLabel = new JLabel(Translator.getTerm("ClassExtractionTemplateLabel") + ": ");
        classExtractionTemplateField = new JTextField(TEXT_FIELD_WIDTH);
        classExtractionTemplateField.setEditable(false);
        setClassExtractionTemplateButton = new JButton(browse);
        setClassExtractionTemplateButton.addActionListener(this);
        JPanel panel = getTemplatePanel(searchClassSetTemplateLabel, classExtractionTemplateField,
                setClassExtractionTemplateButton);
        templatePanel.add(Utils.createEastPanel(panel));

        JLabel searchPropertySetTemplateLabel = new JLabel(Translator.getTerm("PropertyExtractionTemplateLabel") + ": ");
        propertyExtractionTemplateField = new JTextField(TEXT_FIELD_WIDTH);
        propertyExtractionTemplateField.setEditable(false);
        setPropertyExtractionTemplateButton = new JButton(browse);
        setPropertyExtractionTemplateButton.addActionListener(this);
        panel = getTemplatePanel(searchPropertySetTemplateLabel, propertyExtractionTemplateField,
                setPropertyExtractionTemplateButton);
        templatePanel.add(Utils.createEastPanel(panel));

        JLabel searchConceptTemplateLabel = new JLabel(Translator.getTerm("AnnotationExtractionTemplateLabel") + ": ");
        annotationExtractionTemplateField = new JTextField(TEXT_FIELD_WIDTH);
        annotationExtractionTemplateField.setEditable(false);
        setAnnotationExtractionTemplateButton = new JButton(browse);
        setAnnotationExtractionTemplateButton.addActionListener(this);
        panel = getTemplatePanel(searchConceptTemplateLabel, annotationExtractionTemplateField, setAnnotationExtractionTemplateButton);
        templatePanel.add(Utils.createEastPanel(panel));

        JLabel searchSubConceptTemplateLabel = new JLabel(Translator.getTerm("IsaRelationExtractionTemplateLabel") + ": ");
        isaRelationExtractionTemplateField = new JTextField(TEXT_FIELD_WIDTH);
        isaRelationExtractionTemplateField.setEditable(false);
        setIsaRelationExtractionTemplateButton = new JButton(browse);
        setIsaRelationExtractionTemplateButton.addActionListener(this);
        panel = getTemplatePanel(searchSubConceptTemplateLabel, isaRelationExtractionTemplateField,
                setIsaRelationExtractionTemplateButton);
        templatePanel.add(Utils.createEastPanel(panel));

        JLabel searchRegionSetTemplateLabel = new JLabel(Translator.getTerm("DomainRangeExtractionTemplateLabel") + ": ");
        domainRangeExtractionTemplateField = new JTextField(TEXT_FIELD_WIDTH);
        domainRangeExtractionTemplateField.setEditable(false);
        setDomainRangeExtractionTemplateButton = new JButton(browse);
        setDomainRangeExtractionTemplateButton.addActionListener(this);
        panel = getTemplatePanel(searchRegionSetTemplateLabel, domainRangeExtractionTemplateField,
                setDomainRangeExtractionTemplateButton);
        templatePanel.add(Utils.createEastPanel(panel));

        owlMetaDataTablePanel = new OWLMetaDataTablePanel();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(Utils.createNorthPanel(templatePanel), BorderLayout.CENTER);
        mainPanel.add(owlMetaDataTablePanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.WEST);
    }

    public void setMetaData(ReferenceWebOntology ont) {
        refOnt = ont;
        WebOntologyExtractionTemplate owlExtractionTemplate = refOnt.getWebOntologyExtractionTemplate();
        owlMetaDataTablePanel.setModel(refOnt.getOWLMetaDataTableModel());
        isAvailableCheckBox.setSelected(refOnt.isAvailable());
        locationLabel.setText(refOnt.getURI());
        annotationExtractionTemplateField.setText(owlExtractionTemplate.getAnnotationExtractionTemplateLabel());
        isaRelationExtractionTemplateField.setText(owlExtractionTemplate.getIsaRelationExtractionTemplateLabel());
        classExtractionTemplateField.setText(owlExtractionTemplate.getClassExtractionTemplateLabel());
        propertyExtractionTemplateField.setText(owlExtractionTemplate.getPropertyExtractionTemplateLabel());
        domainRangeExtractionTemplateField.setText(owlExtractionTemplate.getDomainRangeExtractionTemplateLabel());
    }

    private JPanel getTemplatePanel(JLabel label, JTextField field, JButton button) {
        JPanel templatePanel = new JPanel();
        templatePanel.add(label);
        templatePanel.add(field);
        templatePanel.add(button);
        return templatePanel;
    }

    private String getTemplateFileName(String currentDir) {
        JFileChooser chooser = new JFileChooser(currentDir);
        int retval = chooser.showOpenDialog(DODDLE_OWL.rootPane);
        if (retval != JFileChooser.APPROVE_OPTION) { return ""; }
        return chooser.getSelectedFile().getAbsolutePath();
    }

    public void actionPerformed(ActionEvent e) {
        if (refOnt == null) { return; }
        String templateFileName;
        WebOntologyExtractionTemplate owlExtractionTemplate = refOnt.getWebOntologyExtractionTemplate();
        if (e.getSource() == setClassExtractionTemplateButton) {
            templateFileName = getTemplateFileName(classExtractionTemplateField.getText());
            if (0 < templateFileName.length()) {
                classExtractionTemplateField.setText(templateFileName);
                owlExtractionTemplate.setClassExtractionTemplate(new File(templateFileName));
                refOnt.reload();
            }
        } else if (e.getSource() == setPropertyExtractionTemplateButton) {
            templateFileName = getTemplateFileName(propertyExtractionTemplateField.getText());
            if (0 < templateFileName.length()) {
                propertyExtractionTemplateField.setText(templateFileName);
                owlExtractionTemplate.setPropertyExtractionTemplate(new File(templateFileName));
                refOnt.reload();
            }
        } else if (e.getSource() == setDomainRangeExtractionTemplateButton) {
            templateFileName = getTemplateFileName(domainRangeExtractionTemplateField.getText());
            if (0 < templateFileName.length()) {
                domainRangeExtractionTemplateField.setText(templateFileName);
                owlExtractionTemplate.setDomainRangeExtractionTemplate(new File(templateFileName));
                refOnt.reload();
            }
        } else if (e.getSource() == setAnnotationExtractionTemplateButton) {
            templateFileName = getTemplateFileName(annotationExtractionTemplateField.getText());
            if (0 < templateFileName.length()) {
                annotationExtractionTemplateField.setText(templateFileName);
                owlExtractionTemplate.setAnnotationExtractionTemplate(new File(templateFileName));
                refOnt.reload();
            }
        } else if (e.getSource() == setIsaRelationExtractionTemplateButton) {
            templateFileName = getTemplateFileName(isaRelationExtractionTemplateField.getText());
            if (0 < templateFileName.length()) {
                isaRelationExtractionTemplateField.setText(templateFileName);
                owlExtractionTemplate.setIsaRelationExtractionTemplate(new File(templateFileName));
                refOnt.reload();
            }
        } else if (e.getSource() == isAvailableCheckBox) {
            refOnt.setAvailable(isAvailableCheckBox.isSelected());
        }
    }
}
