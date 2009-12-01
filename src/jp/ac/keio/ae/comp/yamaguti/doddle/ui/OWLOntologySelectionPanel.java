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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.apache.log4j.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.*;

/**
 * @author takeshi morita
 */
public class OWLOntologySelectionPanel extends JPanel implements ActionListener, ListSelectionListener {

    private JList ontologyList;
    private DefaultListModel listModel;
    private OWLMetaDataPanel owlMetaDataPanel;
    private JButton addOWLFileButton;
    private JButton addOWLURIButton;
    private JButton deleteButton;
    private NameSpaceTable nsTable;

    public OWLOntologySelectionPanel(NameSpaceTable nst) {
        nsTable = nst;
        listModel = new DefaultListModel();
        ontologyList = new JList(listModel);
        ontologyList.addListSelectionListener(this);
        ontologyList.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("OntologyList")));
        addOWLFileButton = new JButton(Translator.getTerm("AddOntologyFromFileButton"));
        addOWLFileButton.addActionListener(this);
        addOWLURIButton = new JButton(Translator.getTerm("AddOntologyFromURIButton"));
        addOWLURIButton.addActionListener(this);
        deleteButton = new JButton(Translator.getTerm("RemoveButton"));
        deleteButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.add(addOWLFileButton);
        buttonPanel.add(addOWLURIButton);
        buttonPanel.add(deleteButton);

        JPanel ontologyListPanel = new JPanel();
        ontologyListPanel.setPreferredSize(new Dimension(100, 250));
        ontologyListPanel.setMinimumSize(new Dimension(100, 250));
        ontologyListPanel.setLayout(new BorderLayout());
        ontologyListPanel.add(new JScrollPane(ontologyList), BorderLayout.CENTER);
        ontologyListPanel.add(Utils.createWestPanel(buttonPanel), BorderLayout.SOUTH);

        owlMetaDataPanel = new OWLMetaDataPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ontologyListPanel, owlMetaDataPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(DODDLEConstants.DIVIDER_SIZE);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private String getType(String str) {
        String ext = FileUtils.getFilenameExt(str);
        String type = "RDF/XML";
        if (ext.equals("n3")) {
            type = "N3";
        }
        return type;
    }

    public void addOWLOntology(ReferenceOWLOntology refOnt) {
        OWLOntologyManager.addRefOntology(refOnt.getURI(), refOnt);
        listModel.addElement(refOnt.getURI());
        owlMetaDataPanel.setMetaData(refOnt);
    }

    private void addOWLOntology(Model ontModel, String uri) {
        try {
            ReferenceOWLOntology refOnt = new ReferenceOWLOntology(ontModel, uri, nsTable);
            addOWLOntology(refOnt);
            DODDLE.STATUS_BAR.setText("Add Reference OWL Ontology");
        } catch (Exception e) {
            JOptionPane
                    .showMessageDialog(this, "Can not set the selected ontology", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void addOWLFile() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String uri = file.getAbsolutePath();
            if (!listModel.contains(uri)) {
                try {
                    Model ontModel = null;
                    if (uri.endsWith("mm")) {
                        ontModel = FreeMindModelMaker.getOWLModel(file);
                    } else {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ontModel = Utils.getOntModel(fileInputStream, "---", getType(uri), DODDLEConstants.BASE_URI);
                    }
                    addOWLOntology(ontModel, uri);
                } catch (FileNotFoundException fne) {
                    fne.printStackTrace();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Can not set the selected ontology", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    private void addOWLURI() {
        String url = JOptionPane.showInputDialog(this, Translator.getTerm("InputURIMessage"),
                "http://www.yamaguchi.comp.ae.keio.ac.jp/doddle/sample.owl");
        if (url != null) {
            if (!listModel.contains(url)) {
                try {
                    InputStream inputStream = new URL(url).openStream();
                    Model ontModel = Utils.getOntModel(inputStream, "---", getType(url), DODDLEConstants.BASE_URI);
                    addOWLOntology(ontModel, url);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error", "Can not set the selected ontology",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    private void delete() {
        Object[] values = ontologyList.getSelectedValues();
        for (int i = 0; i < values.length; i++) {
            OWLOntologyManager.removeRefOntology((String) values[i]);
            listModel.removeElement(values[i]);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addOWLFileButton) {
            addOWLFile();
        } else if (e.getSource() == addOWLURIButton) {
            addOWLURI();
        } else if (e.getSource() == deleteButton) {
            delete();
        }
    }

    public void saveOWLMetaDataSet(File saveDir) {
        BufferedWriter writer = null;
        try {
            for (int i = 0; i < listModel.getSize(); i++) {
                int num = i + 1;
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveDir + "/"
                        + ProjectFileNames.OWL_META_DATA_FILE + num + ".txt"), "UTF-8"));
                String uri = (String) listModel.getElementAt(i);
                ReferenceOWLOntology refOnt = OWLOntologyManager.getRefOntology(uri);
                Properties properties = new Properties();
                properties.setProperty("isAvailable", String.valueOf(refOnt.isAvailable()));
                properties.setProperty("Location", refOnt.getURI());
                OWLOntologyExtractionTemplate template = refOnt.getOWLOntologyExtractionTemplate();
                properties.setProperty("SearchConceptTemplate", template.getSearchConceptTemplate().getAbsolutePath());
                properties.setProperty("SearchSubConceptTemplate", template.getSearchSubConceptTemplate()
                        .getAbsolutePath());
                properties
                        .setProperty("SearchClassSetTemplate", template.getSearchClassSetTemplate().getAbsolutePath());
                properties.setProperty("SearchPropertySetTemplate", template.getSearchPropertySetTemplate()
                        .getAbsolutePath());
                properties.setProperty("SearchRegionSetTemplate", template.getSearchRegionSetTemplate()
                        .getAbsolutePath());
                properties.store(writer, "Save OWL Meta Data" + num);
                writer.close();
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void loadOWLMetaDataSet(File loadDir) {
        if (!loadDir.exists()) { return; }
        File[] owlMetaDataFiles = loadDir.listFiles();
        BufferedReader reader = null;
        try {
            for (int i = 0; i < owlMetaDataFiles.length; i++) {
                if (!owlMetaDataFiles[i].isFile()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(owlMetaDataFiles[i]);
                reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                Properties properties = new Properties();
                properties.load(reader);
                boolean isAvailable = new Boolean(properties.getProperty("isAvailable"));
                String uri = properties.getProperty("Location");
                File file = new File(uri);
                if (file.exists()) {
                    Model ontModel = null;
                    if (uri.endsWith("mm")) {
                        ontModel = FreeMindModelMaker.getOWLModel(file);
                    } else {
                        FileInputStream inputStream = new FileInputStream(file);
                        ontModel = ModelFactory.createDefaultModel();
                        ontModel.read(inputStream, DODDLEConstants.BASE_URI, getType(uri));
                    }
                    addOWLOntology(ontModel, uri);
                } else {
                    InputStream inputStream = new URL(uri).openStream();
                    Model ontModel = Utils.getOntModel(inputStream, "---", getType(uri), DODDLEConstants.BASE_URI);
                    ontModel.read(inputStream, DODDLEConstants.BASE_URI, getType(uri));
                    addOWLOntology(ontModel, uri);
                }
                ReferenceOWLOntology refOnt = OWLOntologyManager.getRefOntology(uri);
                refOnt.setAvailable(isAvailable);
                OWLOntologyExtractionTemplate template = refOnt.getOWLOntologyExtractionTemplate();
                template.setSearchConceptTemplate(new File(properties.getProperty("SearchConceptTemplate")));
                template.setSearchSubConceptTemplate(new File(properties.getProperty("SearchSubConceptTemplate")));
                template.setSearchClassSetTemplate(new File(properties.getProperty("SearchClassSetTemplate")));
                template.setSearchPropertySetTemplate(new File(properties.getProperty("SearchPropertySetTemplate")));
                template.setSearchRegionSetTemplate(new File(properties.getProperty("SearchRegionSetTemplate")));
            }
        } catch (MalformedURLException mue) {
            DODDLE.getLogger().log(Level.INFO, "MalformedURLException");
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        if (0 < ontologyList.getModel().getSize()) {
            ontologyList.setSelectedIndex(0);
        }
    }

    public static void main(String[] args) {
        Translator.loadDODDLEComponentOntology(DODDLEConstants.LANG);
        JFrame frame = new JFrame();
        NameSpaceTable nsTable = new NameSpaceTable();
        frame.getContentPane().add(new OWLOntologySelectionPanel(nsTable));
        frame.setSize(new Dimension(500, 400));
        frame.setVisible(true);
    }

    public void valueChanged(ListSelectionEvent e) {
        String uri = (String) ontologyList.getSelectedValue();
        ReferenceOWLOntology refOnt = OWLOntologyManager.getRefOntology(uri);
        if (refOnt != null) {
            owlMetaDataPanel.setMetaData(refOnt);
        }
    }
}
