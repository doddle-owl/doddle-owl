package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class ConceptDescriptionUI extends JPanel implements ListSelectionListener, ActionListener {

    private Set inputConceptSet;

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
        showOnlyInputConceptsButton = new JCheckBox("ì¸óÕäTîOÇÃÇ›ï\é¶");
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
        fromRelationJListScroll.setBorder(BorderFactory.createTitledBorder("Å©ä÷åWéq"));
        verbIDJList = new JList();
        verbIDJList.addListSelectionListener(this);
        verbIDJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane verbIDJListScroll = new JScrollPane(verbIDJList);
        verbIDJListScroll.setBorder(BorderFactory.createTitledBorder("ìÆéåìIäTîOÉäÉXÉg"));
        subVerbIDJList = new JList();
        subVerbIDJList.addListSelectionListener(this);
        subVerbIDJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane subVerbIDJListScroll = new JScrollPane(subVerbIDJList);
        subVerbIDJListScroll.setBorder(BorderFactory.createTitledBorder("ìÆéåìIäTîOÇÃâ∫à äTîOÉäÉXÉg"));
        JPanel verbPanel = new JPanel();
        verbPanel.setLayout(new GridLayout(2, 1));
        verbPanel.add(verbIDJListScroll);
        verbPanel.add(subVerbIDJListScroll);
        toRelationJList = new JList();
        toRelationJList.addListSelectionListener(this);
        JScrollPane toRelationJListScroll = new JScrollPane(toRelationJList);
        toRelationJListScroll.setBorder(BorderFactory.createTitledBorder("ä÷åWéqÅ®"));
        nounIDJList = new JList();
        nounIDJList.addListSelectionListener(this);
        JScrollPane nounIDJListScroll = new JScrollPane(nounIDJList);
        nounIDJListScroll.setBorder(BorderFactory.createTitledBorder("ñºéåìIäTîOÉäÉXÉg"));
        subNounIDJList = new JList();
        subNounIDJList.addListSelectionListener(this);
        JScrollPane subNounIDJListScroll = new JScrollPane(subNounIDJList);
        subNounIDJListScroll.setBorder(BorderFactory.createTitledBorder("ñºéåìIäTîOÇÃâ∫à äTîOÉäÉXÉg"));
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

    public void setInputConceptSet(Set set) {
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
        Set subVerbIDSet = new TreeSet(conceptDescription.getSubIDSet(verbID));
        if (showOnlyInputConceptsButton.isSelected()) {
            Set inputSubVerbIDSet = new TreeSet();
            for (Iterator i = subVerbIDSet.iterator(); i.hasNext();) {
                String subVerbID = (String) i.next();
                if (inputConceptSet.contains(subVerbID)) {
                    inputSubVerbIDSet.add(subVerbID);
                }
            }
            subVerbIDJList.setListData(inputSubVerbIDSet.toArray());
        } else {
            subVerbIDJList.setListData(subVerbIDSet.toArray());
        }
    }

    private void setSubNounIDList() {
        String nounID = (String) nounIDJList.getSelectedValue();
        Set subNounIDSet = new TreeSet(conceptDescription.getSubIDSet(nounID));
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
