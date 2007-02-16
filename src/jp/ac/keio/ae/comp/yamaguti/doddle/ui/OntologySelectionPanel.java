/*
 * @(#)  2006/03/01
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.infonode.docking.*;
import net.infonode.docking.util.*;

/**
 * @author takeshi morita
 */
public class OntologySelectionPanel extends JPanel implements ActionListener {

    private JButton nextTabButton;
    private NameSpaceTable nsTable;

    private GeneralOntologySelectionPanel generalOntologySelectionPanel;
    private OWLOntologySelectionPanel owlOntologySelectionPanel;

    public OntologySelectionPanel() {
        generalOntologySelectionPanel = new GeneralOntologySelectionPanel();
        nsTable = new NameSpaceTable();
        owlOntologySelectionPanel = new OWLOntologySelectionPanel(nsTable);

        nextTabButton = new JButton(Translator.getString("OntologySelectionPanel.DocumentSelection"));
        nextTabButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(nextTabButton, BorderLayout.EAST);

        View[] mainViews = new View[3];
        ViewMap viewMap = new ViewMap();
        mainViews[0] = new View(Translator.getString("OntologySelectionPanel.RefGenericOntologySelection"), null,
                generalOntologySelectionPanel);
        mainViews[1] = new View(Translator.getString("OntologySelectionPanel.OWLOntologySelection"), null,
                owlOntologySelectionPanel);
        mainViews[2] = new View(Translator.getString("OntologySelectionPanel.NameSpaceTable"), null, nsTable);

        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        RootWindow rootWindow = Utils.createDODDLERootWindow(viewMap);
        TabWindow tabWindow = new TabWindow(new DockingWindow[] { mainViews[0], mainViews[1], mainViews[2]});
        rootWindow.setWindow(tabWindow);

        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        mainViews[0].restoreFocus();
    }
    public String getPrefix(String ns) {
        return nsTable.getPrefix(ns);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLE.setSelectedIndex(DODDLE.DOCUMENT_SELECTION_PANEL);
    }

    public void saveOWLMetaDataSet(File saveDir) {
        owlOntologySelectionPanel.saveOWLMetaDataSet(saveDir);
    }

    public void loadOWLMetaDataSet(File loadDir) {
        owlOntologySelectionPanel.loadOWLMetaDataSet(loadDir);
    }

    public void saveGeneralOntologyInfo(File saveFile) {
        generalOntologySelectionPanel.saveGeneralOntologyInfo(saveFile);
    }

    public void loadGeneralOntologyInfo(File loadFile) {
        generalOntologySelectionPanel.loadGeneralOntologyInfo(loadFile);
    }

    public String getEnableDicList() {
        return generalOntologySelectionPanel.getEnableDicList();
    }

    public boolean isEDREnable() {
        return generalOntologySelectionPanel.isEDREnable();
    }

    public boolean isEDRTEnable() {
        return generalOntologySelectionPanel.isEDRTEnable();
    }

    public boolean isWordNetEnable() {
        return generalOntologySelectionPanel.isWordNetEnable();
    }
}
