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

import org.doddle_owl.models.Concept;
import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.models.DODDLELiteral;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Takeshi Morita
 */
public class LiteralPanel extends JPanel implements ListSelectionListener {

    protected JList langJList;
    protected JList literalJList;
    protected DefaultListModel literalJListModel;

    protected Concept selectedConcept;

    protected String literalType;

    private static final int LANG_SIZE = 60;
    public static final String LABEL = "LABEL";
    public static final String DESCRIPTION = "DESCRIPTION";

    public LiteralPanel(String langTitle, String literalTitle, String type) {
        literalType = type;
        langJList = new JList();
        langJList.addListSelectionListener(this);
        JScrollPane langJListScroll = new JScrollPane(langJList);
        langJListScroll.setPreferredSize(new Dimension(LANG_SIZE, 10));
        langJListScroll.setMinimumSize(new Dimension(LANG_SIZE, 10));
        langJListScroll.setBorder(BorderFactory.createTitledBorder(langTitle));
        literalJList = new JList();
        literalJList.addListSelectionListener(this);
        JScrollPane literalJListScroll = new JScrollPane(literalJList);
        literalJListScroll.setBorder(BorderFactory.createTitledBorder(literalTitle));

        setLayout(new BorderLayout());
        JPanel langAndLabelPanel = new JPanel();
        langAndLabelPanel.setLayout(new BorderLayout());
        langAndLabelPanel.add(langJListScroll, BorderLayout.WEST);
        langAndLabelPanel.add(literalJListScroll, BorderLayout.CENTER);
        add(langAndLabelPanel, BorderLayout.CENTER);
    }

    public void clearData() {
        langJList.setListData(new Object[0]);
        literalJList.setListData(new Object[0]);
    }

    private void setLangList(Set<String> langSet) {
        langJList.setListData(langSet.toArray());
        if (langSet.size() == 0) { return; }
        langJList.setSelectedValue(DODDLEConstants.LANG, true);
        if (langJList.getSelectedValue() == null) {
            langJList.setSelectedIndex(0);
        }
    }

    public void setSelectedConcept(Concept c) {
        selectedConcept = c;
    }

    public void setLabelLangList() {
        setLangList(selectedConcept.getLangLabelListMap().keySet());
    }

    public void setDescriptionLangList() {
        setLangList(selectedConcept.getLangDescriptionListMap().keySet());
    }

    public void setDescriptionList() {
        DefaultListModel listModel = new DefaultListModel();
        Object[] langList = langJList.getSelectedValues();
        Map<String, List<DODDLELiteral>> langDescriptionListMap = selectedConcept.getLangDescriptionListMap();
        for (int i = 0; i < langList.length; i++) {
            if (langDescriptionListMap.get(langList[i]) != null) {
                for (DODDLELiteral description : langDescriptionListMap.get(langList[i])) {
                    listModel.addElement(description);
                }
            }
        }
        literalJList.setModel(listModel);
    }

    public void setLabelList() {
        DefaultListModel listModel = new DefaultListModel();
        Object[] langList = langJList.getSelectedValues();
        Map<String, List<DODDLELiteral>> langLabelListMap = selectedConcept.getLangLabelListMap();
        for (int i = 0; i < langList.length; i++) {
            if (langLabelListMap.get(langList[i]) != null) {
                for (DODDLELiteral label : langLabelListMap.get(langList[i])) {
                    if (0 < label.getString().length()) {
                        listModel.addElement(label);
                    }
                }
            }
        }
        literalJList.setModel(listModel);
    }

    public void setField() {
    }

    public void valueChanged(ListSelectionEvent e) {
        if (literalType == LABEL) {
            if (e.getSource() == langJList) {
                setLabelList();
            } else if (e.getSource() == literalJList) {
                setField();
            }
        } else if (literalType == DESCRIPTION) {
            if (e.getSource() == langJList) {
                setDescriptionList();
            }
        }
    }
}
