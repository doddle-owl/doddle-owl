package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/*
 * @(#)  2005/07/17
 */

/**
 * @author takeshi morita
 */
public class ConceptInformationPanel extends JPanel implements ActionListener, ListSelectionListener {

    private Concept selectedConcept;
    
    private JLabel uriLabel;
    private JLabel uriValueLabel;
    private JLabel typicalWordLabel;
    private JLabel typicalWordValueLabel;
    
    private JList labelLangJList;
    private JList labelJList;
    private DefaultListModel labelJListModel;
    private JTextField langField;
    private JTextField labelField;
    private JButton addLabelButton;
    private JButton deleteLabelButton;
    private JButton editLabelButton;
    private JButton setTypcialLabelButton;
    
    private JList descriptionLangJList;
    private JList descriptionJList;
    private DefaultListModel descriptionJListModel;
    private JButton addDescriptionButton;
    private JButton deleteDescriptionButton;
    private JButton editDescriptionButton;
    
    private JLabel trimmedNodeCntLabel;
    private JLabel trimmedNodeCntValueLabel;

    private JTree conceptTree;
    private ConceptDriftManagementPanel conceptDriftManagementPanel;

    private EDRConceptDefinitionPanel edrConceptDefinitionPanel;
    private static final int LANG_SIZE = 60;
    
    private void init(JTree tree, DefaultTreeCellRenderer renderer) {
        conceptTree = tree;
        uriLabel = new JLabel("URI: ");
        uriValueLabel = new JLabel("");
        JPanel uriPanel = new JPanel();
        uriPanel.setLayout(new GridLayout(1, 2));
        uriPanel.add(uriLabel);
        uriPanel.add(uriValueLabel);
        typicalWordLabel = new JLabel(Translator.getString("ConceptTreePanel.DisplayWord") + ": ");        
        typicalWordValueLabel = new JLabel("");
        
        JPanel typicalWordPanel = new JPanel();
        typicalWordPanel.setLayout(new GridLayout(1, 2));
        typicalWordPanel.add(typicalWordLabel);
        typicalWordPanel.add(typicalWordValueLabel);
        
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(2,1, 5, 5));
        northPanel.add(uriPanel);
        northPanel.add(typicalWordPanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2));
        centerPanel.add(getLabelPanel());
        centerPanel.add(getDescriptionPanel());

        trimmedNodeCntLabel = new JLabel(Translator.getString("ConceptTreePanel.TrimmedConceptCount") + "： ");
        trimmedNodeCntValueLabel = new JLabel("");
        JPanel trimmedNodeCntPanel = new JPanel();
        trimmedNodeCntPanel.add(trimmedNodeCntLabel);
        trimmedNodeCntPanel.add(trimmedNodeCntValueLabel);

        setLayout(new BorderLayout());
        add(getWestPanel(northPanel), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(getWestPanel(trimmedNodeCntPanel), BorderLayout.SOUTH);
        setTreeConfig(renderer);
    }

    public void clearPanel() {
        labelLangJList.setListData(new Object[0]);
        descriptionLangJList.setListData(new Object[0]);
        labelJListModel = new DefaultListModel();
        descriptionJListModel = new DefaultListModel();
        labelJList.setModel(labelJListModel);
        descriptionJList.setModel(descriptionJListModel);

        uriValueLabel.setText("");
        typicalWordValueLabel.setText("");
        trimmedNodeCntValueLabel.setText("");
    }

    public ConceptInformationPanel(JTree tree, DefaultTreeCellRenderer renderer, ConceptDriftManagementPanel cdmp) {
        init(tree, renderer);
        conceptDriftManagementPanel = cdmp;
    }

    public ConceptInformationPanel(JTree tree, DefaultTreeCellRenderer renderer, EDRConceptDefinitionPanel ecdp,
            ConceptDriftManagementPanel cdmp) {
        edrConceptDefinitionPanel = ecdp;
        init(tree, renderer);
        conceptDriftManagementPanel = cdmp;
    }

    private void setLabelLangList() {
        Set<String> langSet = selectedConcept.getLangLabelListMap().keySet();
        labelLangJList.setListData(langSet.toArray());
        if (langSet.size() == 0) {
            return;
        }
        labelLangJList.setSelectedValue(DODDLE.LANG, true);
        if (labelLangJList.getSelectedValue() == null) {
            labelLangJList.setSelectedIndex(0);
        }
    }
    
    private void setLabelList() {
        DefaultListModel listModel = new DefaultListModel();
        Map<String, List<DODDLELiteral>> langLabelListMap = selectedConcept.getLangLabelListMap();        
        Object[] langList = labelLangJList.getSelectedValues();
        for (int i = 0; i < langList.length; i++) {
            for (DODDLELiteral label: langLabelListMap.get(langList[i])) {
              listModel.addElement(label);  
            }
        }
        labelJList.setModel(listModel);
    }
    
    private void setDescriptionLangList() {
        Set<String> langSet = selectedConcept.getLangDescriptionListMap().keySet();
        descriptionLangJList.setListData(langSet.toArray());
        if (langSet.size() == 0) {
            return;
        }
        descriptionLangJList.setSelectedValue(DODDLE.LANG, true);
        if (descriptionLangJList.getSelectedValue() == null) {
            descriptionLangJList.setSelectedIndex(0);
        }
    }
    
    private void setDescriptionList() {
        DefaultListModel listModel = new DefaultListModel();
        Map<String, List<DODDLELiteral>> langDescriptionListMap = selectedConcept.getLangDescriptionListMap();        
        Object[] langList = descriptionLangJList.getSelectedValues();
        for (int i = 0; i < langList.length; i++) {
            for (DODDLELiteral label: langDescriptionListMap.get(langList[i])) {
              listModel.addElement(label);  
            }
        }
        descriptionJList.setModel(listModel);
    }
    
    
    private void setTreeConfig(DefaultTreeCellRenderer renderer) {
        conceptTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if (path == null) { return; }
                ConceptTreeNode conceptTreeNode = (ConceptTreeNode) path.getLastPathComponent();
                if (conceptTreeNode != null) {
                    selectedConcept = conceptTreeNode.getConcept(); 
                    setLabelLangList();
                    setLabelList();
                    setDescriptionLangList();
                    setDescriptionList();
                    uriValueLabel.setText(conceptTreeNode.getURI());
                    typicalWordValueLabel.setText(conceptTreeNode.getInputWord());
                    StringBuilder trimmedCntStr = new StringBuilder();
                    for (int trimmedCnt : conceptTreeNode.getTrimmedCountList()) {
                        trimmedCntStr.append(trimmedCnt + ", ");
                    }
                    trimmedNodeCntValueLabel.setText(trimmedCntStr.toString());
                    if (edrConceptDefinitionPanel != null && conceptTreeNode.getConcept() instanceof VerbConcept) {
                        edrConceptDefinitionPanel.init();
                        VerbConcept vc = (VerbConcept) conceptTreeNode.getConcept();
                        edrConceptDefinitionPanel.setDomainList(vc.getDomainSet());
                        edrConceptDefinitionPanel.setRangeList(vc.getRangeSet());
                    }
                    conceptDriftManagementPanel.traAction(conceptTreeNode);
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

    private JComponent getLabelPanel() {
        labelLangJList = new JList();
        labelLangJList.addListSelectionListener(this);
        JScrollPane labelLangJListScroll = new JScrollPane(labelLangJList);
        labelLangJListScroll.setPreferredSize(new Dimension(LANG_SIZE, 10));
        labelLangJListScroll.setMinimumSize(new Dimension(LANG_SIZE, 10));
        labelLangJListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getString("Lang")));
        labelJListModel = new DefaultListModel();
        labelJList = new JList(labelJListModel);
        labelJList.addListSelectionListener(this);
        JScrollPane labelJListScroll = new JScrollPane(labelJList);
        labelJListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getString("Text")));
        langField = new JTextField(5);
        langField.setBorder(BorderFactory.createTitledBorder(Translator.getString("Lang")));
        labelField = new JTextField(15);
        labelField.setBorder(BorderFactory.createTitledBorder(Translator.getString("Text")));

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.add(langField, BorderLayout.WEST);
        fieldPanel.add(labelField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        addLabelButton = new JButton(Translator.getString("ConceptTreePanel.Add"));
        addLabelButton.addActionListener(this);
        deleteLabelButton = new JButton(Translator.getString("ConceptTreePanel.Remove"));
        deleteLabelButton.addActionListener(this);
        editLabelButton = new JButton(Translator.getString("ConceptTreePanel.Edit"));
        editLabelButton.addActionListener(this);
        setTypcialLabelButton = new JButton(Translator.getString("ConceptTreePanel.SetDisplayWord"));
        setTypcialLabelButton.addActionListener(this);
        buttonPanel.add(setTypcialLabelButton);
        buttonPanel.add(addLabelButton);
        buttonPanel.add(editLabelButton);
        buttonPanel.add(deleteLabelButton);
        
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BorderLayout());
        editPanel.add(fieldPanel, BorderLayout.NORTH);
        editPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel langAndLabelPanel = new JPanel();
        langAndLabelPanel.setLayout(new BorderLayout());
        langAndLabelPanel.add(labelLangJListScroll, BorderLayout.WEST);
        langAndLabelPanel.add(labelJListScroll, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(Translator.getString("Label")));
        panel.setLayout(new BorderLayout());
        panel.add(langAndLabelPanel, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JComponent getDescriptionPanel() {
        descriptionLangJList = new JList();        
        descriptionLangJList.addListSelectionListener(this);
        JScrollPane descriptionLangJListScroll = new JScrollPane(descriptionLangJList);
        descriptionLangJListScroll.setPreferredSize(new Dimension(LANG_SIZE, 10));
        descriptionLangJListScroll.setMinimumSize(new Dimension(LANG_SIZE, 10));
        descriptionLangJListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getString("Lang")));
        descriptionJListModel = new DefaultListModel();
        descriptionJList = new JList(descriptionJListModel);        
        JScrollPane descriptionJListScroll = new JScrollPane(descriptionJList);
        descriptionJListScroll.setBorder(BorderFactory.createTitledBorder(Translator.getString("Text")));

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridLayout(1, 3));
        addDescriptionButton = new JButton(Translator.getString("ConceptTreePanel.Add"));
        addDescriptionButton.addActionListener(this);
        deleteDescriptionButton = new JButton(Translator.getString("ConceptTreePanel.Remove"));
        deleteDescriptionButton.addActionListener(this);
        editDescriptionButton = new JButton(Translator.getString("ConceptTreePanel.Edit"));
        editDescriptionButton.addActionListener(this);
        editPanel.add(addDescriptionButton);
        editPanel.add(editDescriptionButton);
        editPanel.add(deleteDescriptionButton);

        JPanel langAndDescriptionPanel = new JPanel();
        langAndDescriptionPanel.setLayout(new BorderLayout());
        langAndDescriptionPanel.add(descriptionLangJListScroll, BorderLayout.WEST);
        langAndDescriptionPanel.add(descriptionJListScroll, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(Translator.getString("Description")));
        panel.setLayout(new BorderLayout());
        panel.add(langAndDescriptionPanel, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.SOUTH);

        return panel;
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
        return concept.getURI().equals(node.getConcept().getURI());
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

    private void clearLabelField() {
        langField.setText("");
        labelField.setText("");
    }
    
    public void actionPerformed(ActionEvent e) {
        if (!(conceptTree.getSelectionCount() == 1)) { return; }
        // DefaultTreeModel treeModel = (DefaultTreeModel)
        // conceptTree.getModel();
        TreePath path = conceptTree.getSelectionPath();
        ConceptTreeNode conceptTreeNode = (ConceptTreeNode) path.getLastPathComponent();
        Concept concept = conceptTreeNode.getConcept();
        selectedConcept = concept;

        if (e.getSource() == addLabelButton) {
            concept.addLabel(new DODDLELiteral(langField.getText(), labelField.getText()));
            setLabelLangList();
            clearLabelField();
        } else if (e.getSource() == deleteLabelButton) {
            Object[] labelList = labelJList.getSelectedValues();
            for (int i = 0; i < labelList.length; i++) {
                concept.removeLabel((DODDLELiteral)labelList[i]);
            }
            setLabelLangList();
            clearLabelField();
        } else if (e.getSource() == editLabelButton) {
            if (labelJList.getSelectedIndices().length == 1 && 0 < labelField.getText().length()) {
                DODDLELiteral label = (DODDLELiteral)labelJList.getSelectedValue();
                if (label.getLang().equals(langField.getText())) {
                    label.setString(labelField.getText());
                } else {
                    selectedConcept.removeLabel(label);
                    label.setLang(langField.getText());
                    label.setString(labelField.getText());
                    selectedConcept.addLabel(label);
                }
                setLabelLangList();
                clearLabelField();
                reloadConceptTreeNode(concept);
            }
        } else if (e.getSource() == addDescriptionButton) {
            EditDescriptionDialog editDescriptionDialog = new EditDescriptionDialog(DODDLE.rootFrame);
            editDescriptionDialog.setVisible(true);
            selectedConcept.addDescription(editDescriptionDialog.getDescription());
            setDescriptionLangList();
        } else if (e.getSource() == deleteDescriptionButton) {
            Object[] descritionList = descriptionJList.getSelectedValues();
            for (int i = 0; i < descritionList.length; i++) {
                selectedConcept.removeDescription((DODDLELiteral)descritionList[i]);
            }
            setDescriptionLangList();
        } else if (e.getSource() == editDescriptionButton) {
            if (descriptionJList.getSelectedValues().length == 1) {
                EditDescriptionDialog editDescriptionDialog = new EditDescriptionDialog(DODDLE.rootFrame);
                DODDLELiteral description =(DODDLELiteral)descriptionJList.getSelectedValue(); 
                editDescriptionDialog.setDescription(description);
                editDescriptionDialog.setVisible(true);
                DODDLELiteral editDescription = editDescriptionDialog.getDescription();
                if (0 < editDescription.getString().length()) {
                    if (editDescription.getLang().equals(description.getLang())) {
                        description.setString(editDescription.getString());
                    } else {
                        selectedConcept.removeDescription(description);
                        description.setLang(editDescription.getLang());
                        description.setString(editDescription.getString());
                        selectedConcept.addDescription(description);
                    }
                    setDescriptionLangList();
                }
            }
        } else if (e.getSource() == setTypcialLabelButton) {
            DODDLELiteral label = (DODDLELiteral) labelJList.getSelectedValue();
            setTypicalWord(concept, label);
        }
        conceptTreeNode.setConcept(concept);
    }

    /**
     * @param concept
     * @param label
     */
    private void setTypicalWord(Concept concept, DODDLELiteral label) {
        concept.setInputLabel(label);
        typicalWordValueLabel.setText(label.getString());
        reloadConceptTreeNode(concept);
    }

    private void setLabelField() {
        if (labelJList.getSelectedValue() != null) {            
            DODDLELiteral label = (DODDLELiteral)labelJList.getSelectedValue();
            langField.setText(label.getLang());
            labelField.setText(label.getString());
        }
    }
    
    public void valueChanged(ListSelectionEvent e) {        
        if (e.getSource() == labelLangJList) {
            setLabelList();
        } else if (e.getSource() == descriptionLangJList) {
            setDescriptionList();
        } else if (e.getSource() == labelJList){
            setLabelField();
        }
    }
}
