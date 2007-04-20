/*
 * @(#)  2007/03/17
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
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
        langJList.setSelectedValue(DODDLE.LANG, true);
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

    public void valueChanged(ListSelectionEvent e) {
        if (literalType == LABEL) {
            setLabelList();
        } else if (literalType == DESCRIPTION) {
            setDescriptionList();
        }
    }

}
