/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2009 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.Concept;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.ConceptDefinition;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author takeshi morita
 */
public class ConceptDescriptionUI extends JPanel implements ListSelectionListener, ActionListener {

    private Set<Concept> inputConceptSet;

    private JLabel idLabel;
    private JTextField idField;
    private JButton evalButton;
    private JCheckBox showOnlyInputConceptsButton;

    private JList fromRelationJList;
    private JList verbIDJList;
    private JList subVerbIDJList;
    private JList toRelationJList;
    private JList nounIDJList;
    private JList subNounIDJList;

    private ConceptDefinition conceptDescription;

    public ConceptDescriptionUI() {
        conceptDescription = ConceptDefinition.getInstance();

        idLabel = new JLabel("");
        idField = new JTextField();
        evalButton = new JButton("eval");
        evalButton.addActionListener(this);
        showOnlyInputConceptsButton = new JCheckBox("入力概念のみ表示");
        showOnlyInputConceptsButton.setSelected(true);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(evalButton);
        buttonPanel.add(showOnlyInputConceptsButton);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(idLabel, BorderLayout.WEST);
        inputPanel.add(idField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        fromRelationJList = new JList();
        fromRelationJList.addListSelectionListener(this);
        JScrollPane fromRelationJListScroll = new JScrollPane(fromRelationJList);
        fromRelationJListScroll.setBorder(BorderFactory.createTitledBorder("←関係子"));
        verbIDJList = new JList();
        verbIDJList.addListSelectionListener(this);
        verbIDJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane verbIDJListScroll = new JScrollPane(verbIDJList);
        verbIDJListScroll.setBorder(BorderFactory.createTitledBorder("動詞的概念リスト"));
        subVerbIDJList = new JList();
        subVerbIDJList.addListSelectionListener(this);
        subVerbIDJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane subVerbIDJListScroll = new JScrollPane(subVerbIDJList);
        subVerbIDJListScroll.setBorder(BorderFactory.createTitledBorder("動詞的概念の下位概念リスト"));
        JPanel verbPanel = new JPanel();
        verbPanel.setLayout(new GridLayout(2, 1));
        verbPanel.add(verbIDJListScroll);
        verbPanel.add(subVerbIDJListScroll);
        toRelationJList = new JList();
        toRelationJList.addListSelectionListener(this);
        JScrollPane toRelationJListScroll = new JScrollPane(toRelationJList);
        toRelationJListScroll.setBorder(BorderFactory.createTitledBorder("関係子→"));
        nounIDJList = new JList();
        nounIDJList.addListSelectionListener(this);
        JScrollPane nounIDJListScroll = new JScrollPane(nounIDJList);
        nounIDJListScroll.setBorder(BorderFactory.createTitledBorder("名詞的概念リスト"));
        subNounIDJList = new JList();
        subNounIDJList.addListSelectionListener(this);
        JScrollPane subNounIDJListScroll = new JScrollPane(subNounIDJList);
        subNounIDJListScroll.setBorder(BorderFactory.createTitledBorder("名詞的概念の下位概念リスト"));
        JPanel nounPanel = new JPanel();
        nounPanel.setLayout(new GridLayout(2, 1));
        nounPanel.add(nounIDJListScroll);
        nounPanel.add(subNounIDJListScroll);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(1, 4));
        selectionPanel.add(fromRelationJListScroll);
        selectionPanel.add(verbPanel);
        selectionPanel.add(toRelationJListScroll);
        selectionPanel.add(nounPanel);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(selectionPanel, BorderLayout.CENTER);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == fromRelationJList) {
            setVerbIDList();
        } else if (e.getSource() == verbIDJList) {
            setSubVerbIDList();
            setToRelationList();            
        } else if (e.getSource() == toRelationJList) {
            setNounIDList();
        } else if (e.getSource() == nounIDJList) {
            setSubNounIDList();
        }
    }

    public void setConcept(String id) {
        idLabel.setText(id);
        setFromRelationList();
    }

    public void setInputConceptSet(Set<Concept> set) {
        inputConceptSet = set;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == evalButton) {
            setConcept(idField.getText());
        }
    }

    private void setVerbIDList() {
        Set verbIDSet = new TreeSet();
        Object[] relationList = fromRelationJList.getSelectedValues();
        for (int i = 0; i < relationList.length; i++) {
            verbIDSet.addAll(conceptDescription.getVerbIDSet(idLabel.getText(), (String) relationList[i]));
        }
        if (showOnlyInputConceptsButton.isSelected()) {
            Set inputVerbIDSet = new TreeSet();
            for (Iterator i = verbIDSet.iterator(); i.hasNext();) {
                String verbID = (String) i.next();
                if (inputConceptSet.contains(verbID)) {
                    inputVerbIDSet.add(verbID);
                }
            }
            verbIDJList.setListData(inputVerbIDSet.toArray());
        } else {
            verbIDJList.setListData(verbIDSet.toArray());
        }
    }

    private void setSubVerbIDList() {
        String verbID = (String) verbIDJList.getSelectedValue();
        Set<String> subVerbURISet = new TreeSet<String>(conceptDescription.getSubURISet(verbID));
        if (showOnlyInputConceptsButton.isSelected()) {
            Set<String> inputSubVerbIDSet = new TreeSet<String>();
            for (String subVerbID : subVerbURISet) {
                if (inputConceptSet.contains(subVerbID)) {
                    inputSubVerbIDSet.add(subVerbID);
                }
            }
            subVerbIDJList.setListData(inputSubVerbIDSet.toArray());
        } else {
            subVerbIDJList.setListData(subVerbURISet.toArray());
        }
    }

    private void setSubNounIDList() {
        String nounID = (String) nounIDJList.getSelectedValue();
        Set subNounIDSet = new TreeSet(conceptDescription.getSubURISet(nounID));
        if (showOnlyInputConceptsButton.isSelected()) {
            Set inputSubNounIDSet = new TreeSet();
            for (Iterator i = subNounIDSet.iterator(); i.hasNext();) {
                String subNounID = (String) i.next();
                if (inputConceptSet.contains(subNounID)) {
                    inputSubNounIDSet.add(subNounID);
                }
            }
            subNounIDJList.setListData(inputSubNounIDSet.toArray());
        } else {
            subNounIDJList.setListData(subNounIDSet.toArray());
        }
    }

    private void setFromRelationList() {
        // Set fromRelationSet =
        // conceptDescription.getFromRelationSet(idLabel.getText());
        // fromRelationJList.setListData(fromRelationSet.toArray());
        fromRelationJList.setListData(ConceptDefinition.relationList);
    }

    private void setToRelationList() {
        // String verbID = (String) verbIDJList.getSelectedValue();
        // toRelationJList.setListData(conceptDescription.getToRelationSet(verbID).toArray());
        toRelationJList.setListData(ConceptDefinition.relationList);
    }

    private void setNounIDList() {
        Set nounIDSet = new TreeSet();
        String verbID = (String) verbIDJList.getSelectedValue();
        Object[] toRelationList = toRelationJList.getSelectedValues();
        for (int i = 0; i < toRelationList.length; i++) {
            String relation = (String) toRelationList[i];
            if (conceptDescription.getIDSet(verbID, relation) != null) {
                nounIDSet.addAll(conceptDescription.getIDSet(verbID, relation));
            }
        }
        if (showOnlyInputConceptsButton.isSelected()) {
            Set inputNounIDSet = new TreeSet();
            for (Iterator i = nounIDSet.iterator(); i.hasNext();) {
                String nounID = (String) i.next();
                if (inputConceptSet.contains(nounID)) {
                    inputNounIDSet.add(nounID);
                }
            }
            subNounIDJList.setListData(inputNounIDSet.toArray());
        } else {
            nounIDJList.setListData(nounIDSet.toArray());
        }
    }

    public static void main(String[] args) {
        // EDRTree.init();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(50, 50, 800, 600);
        ConceptDescriptionUI panel = new ConceptDescriptionUI();
        Container contentPane = frame.getContentPane();
        contentPane.add(panel);
        frame.setVisible(true);
    }
}
