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

    private SwoogleWebServiceWrapperPanel swoogleWebServiceWrapperPanel;
    private GeneralOntologySelectionPanel generalOntologySelectionPanel;
    private OWLOntologySelectionPanel owlOntologySelectionPanel;

    private View[] mainViews;
    private RootWindow rootWindow;
    
    public OntologySelectionPanel() {        
        generalOntologySelectionPanel = new GeneralOntologySelectionPanel();
        nsTable = new NameSpaceTable();        
        owlOntologySelectionPanel = new OWLOntologySelectionPanel(nsTable);
        swoogleWebServiceWrapperPanel = new SwoogleWebServiceWrapperPanel(nsTable, owlOntologySelectionPanel); 

        nextTabButton = new JButton("多義性解消");
        nextTabButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(nextTabButton, BorderLayout.EAST);
        
        mainViews = new View[4];
        ViewMap viewMap = new ViewMap();
        mainViews[0] = new View(Translator.getString("OntologySelectionPanel.RefGenericOntologySelection"), null,
                generalOntologySelectionPanel);
        mainViews[1] = new View(Translator.getString("OntologySelectionPanel.OWLOntologySelection"), null,
                owlOntologySelectionPanel);
        mainViews[2] = new View(Translator.getString("OntologySelectionPanel.NameSpaceTable"), null, nsTable);
        mainViews[3] = new View("Swoogle Web Service Wrapper", null, swoogleWebServiceWrapperPanel);

        for (int i = 0; i < mainViews.length; i++) {
            viewMap.addView(i, mainViews[i]);
        }
        rootWindow = Utils.createDODDLERootWindow(viewMap);        
        setXGALayout();
        setLayout(new BorderLayout());
        add(rootWindow, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);   
    }
    
    public void setInputWordArea(String inputWordText) {
        swoogleWebServiceWrapperPanel.setInputWordArea(inputWordText);
    }    
    
    public void setXGALayout() {
        SplitWindow sw1 = new SplitWindow(false, 0.3f, mainViews[0], mainViews[2]);
        TabWindow tabWindow = new TabWindow(new DockingWindow[] { sw1, mainViews[3], mainViews[1]});
        rootWindow.setWindow(tabWindow);
        mainViews[0].restoreFocus();
    }    
    
    public void setUXGALayout() {
        SplitWindow sw1 = new SplitWindow(false, 0.25f, mainViews[0], mainViews[1]);
        SplitWindow sw2 = new SplitWindow(false, 0.75f, sw1, mainViews[2]);
        TabWindow tabWindow = new TabWindow(new DockingWindow[] {mainViews[3], sw2});
        rootWindow.setWindow(tabWindow);
    }
    
    public String getPrefix(String ns) {
        return nsTable.getPrefix(ns);
    }

    public void actionPerformed(ActionEvent e) {
        DODDLE.setSelectedIndex(DODDLE.DISAMBIGUATION_PANEL);
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
