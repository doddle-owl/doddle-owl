/*
 * @(#)  2006/12/15
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

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
        ontologyList.setBorder(BorderFactory.createTitledBorder(Translator
                .getString("OWLOntologySelectionPanel.OntologyList")));
        addOWLFileButton = new JButton(Translator.getString("Add") + "(File)");
        addOWLFileButton.addActionListener(this);
        addOWLURIButton = new JButton(Translator.getString("Add") + "(URI)");
        addOWLURIButton.addActionListener(this);
        deleteButton = new JButton(Translator.getString("Remove"));
        deleteButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addOWLFileButton);
        buttonPanel.add(addOWLURIButton);
        buttonPanel.add(deleteButton);

        JPanel ontologyListPanel = new JPanel();
        ontologyListPanel.setPreferredSize(new Dimension(150, 150));
        ontologyListPanel.setMinimumSize(new Dimension(150, 150));
        ontologyListPanel.setLayout(new BorderLayout());
        ontologyListPanel.add(new JScrollPane(ontologyList), BorderLayout.CENTER);
        ontologyListPanel.add(buttonPanel, BorderLayout.SOUTH);

        owlMetaDataPanel = new OWLMetaDataPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ontologyListPanel, owlMetaDataPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(DODDLE.DIVIDER_SIZE);
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private void addOWLOntology(InputStream inputStream, String uri) {
        try {
            ReferenceOWLOntology refOnt = new ReferenceOWLOntology(inputStream, uri, nsTable);
            OWLOntologyManager.addRefOntology(uri, refOnt);
            listModel.addElement(uri);
            owlMetaDataPanel.setMetaData(refOnt);
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
                    FileInputStream fileInputStream = new FileInputStream(file);
                    addOWLOntology(fileInputStream, uri);
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
        String url = JOptionPane.showInputDialog(this, Translator.getString("OWLOntologySelectionPanel.InputURI"),
                "http://mmm.semanticweb.org/doddle/sample.owl");
        if (url != null) {
            if (!listModel.contains(url)) {
                try {
                    InputStream inputStream = new URL(url).openStream();
                    addOWLOntology(inputStream, url);
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
                properties.setProperty("Location", refOnt.getURI());
                OWLOntologyExtractionTemplate template = refOnt.getOWLOntologyExtractionTemplate();
                properties.setProperty("SearchConceptTemplate", template.getSearchConceptTemplate());
                properties.setProperty("SearchSubConceptTemplate", template.getSearchSubConceptTemplate());
                properties.setProperty("SearchLabelSetTemplate", template.getSearchLabelSetTemplate());
                properties.setProperty("SearchClassSetTemplate", template.getSearchClassSetTemplate());
                properties.setProperty("SearchPropertySetTemplate", template.getSearchPropertySetTemplate());
                properties.setProperty("SearchDomainSetTemplate", template.getSearchDomainSetTemplate());
                properties.setProperty("SearchRangeSetTemplate", template.getSearchRangeSetTemplate());
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
                FileInputStream fis = new FileInputStream(owlMetaDataFiles[i]);
                reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                Properties properties = new Properties();
                properties.load(reader);
                String uri = properties.getProperty("Location");
                File file = new File(uri);
                if (file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    addOWLOntology(fileInputStream, uri);
                } else {
                    InputStream inputStream = new URL(uri).openStream();
                    addOWLOntology(inputStream, uri);
                }
                ReferenceOWLOntology refOnt = OWLOntologyManager.getRefOntology(uri);
                OWLOntologyExtractionTemplate template = refOnt.getOWLOntologyExtractionTemplate();
                template.setSearchConceptTemplate(properties.getProperty("SearchConceptTemplate"));
                template.setSearchSubConceptTemplate(properties.getProperty("SearchSubConceptTemplate"));
                template.setSearchLabelSetTemplate(properties.getProperty("SearchLabelSetTemplate"));
                template.setSearchClassSetTemplate(properties.getProperty("SearchClassSetTemplate"));
                template.setSearchPropertySetTemplate(properties.getProperty("SearchPropertySetTemplate"));
                template.setSearchDomainSetTemplate(properties.getProperty("SearchDomainSetTemplate"));
                template.setSearchRangeSetTemplate(properties.getProperty("SearchRangeSetTemplate"));
                reader.close();
            }            
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
        Translator.loadResourceBundle(DODDLE.LANG);
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
