/*
 * @(#)  2006/12/15
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import com.hp.hpl.jena.util.*;

/**
 * @author takeshi morita
 */
public class OWLOntologySelectionPanel extends JPanel implements ActionListener {

    private JList ontologyList;
    private DefaultListModel listModel;
    private JButton addOWLFileButton;
    private JButton addOWLURIButton;
    private JButton deleteButton;
    private NameSpaceTable nsTable;

    public OWLOntologySelectionPanel(NameSpaceTable nst) {
        nsTable = nst;
        listModel = new DefaultListModel();
        ontologyList = new JList(listModel);
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

        setLayout(new BorderLayout());
        add(new JScrollPane(ontologyList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String getType(String str) {
        String ext = FileUtils.getFilenameExt(str);
        String type = "RDF/XML";
        if (ext.equals("n3")) {
            type = "N3";
        }
        return type;
    }

    private void addOWLOntology(InputStream inputStream, String uri) {
        try {
            ReferenceOWLOntology refOnt = new ReferenceOWLOntology(inputStream, getType(uri), nsTable);
            OWLOntologyManager.addRefOntology(uri, refOnt);
            listModel.addElement(uri);
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

    public void saveOWLOntologySet(File saveFile) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8"));
            for (int i = 0; i < listModel.getSize(); i++) {
                String uri = (String) listModel.getElementAt(i);
                writer.write(uri);
                writer.write("\n");
            }
            writer.close();
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

    public void loadOWLOntologySet(File loadFile) {
        if (!loadFile.exists()) { return; }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(loadFile);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (reader.ready()) {
                String uri = reader.readLine();
                File file = new File(uri);
                if (file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    addOWLOntology(fileInputStream, uri);
                } else {
                    InputStream inputStream = new URL(uri).openStream();
                    addOWLOntology(inputStream, uri);
                }
            }
            reader.close();
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
    }

    public static void main(String[] args) {
        Translator.loadResourceBundle(DODDLE.LANG);
        JFrame frame = new JFrame();
        NameSpaceTable nsTable = new NameSpaceTable();
        frame.getContentPane().add(new OWLOntologySelectionPanel(nsTable));
        frame.setSize(new Dimension(500, 400));
        frame.setVisible(true);
    }
}
