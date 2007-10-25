/*
 * @(#)  2006/08/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.Container;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.EDR2DoddleDicConverter.*;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author takeshi morita
 */
public class EDR2DoddleDicConverterUI extends JDialog implements ActionListener {

    private JRadioButton edrRadioButton;
    private JRadioButton edrtRadioButton;
    private JCheckBox txtBox;
    private JCheckBox dbBox;
    private JCheckBox owlBox;
    private JTextField edrPathField;
    private JButton refEDRPathButton;
    private JTextField doddleDicPathField;
    private JButton refDoddleDicPathButton;
    private JButton convertButton;
    private JButton exitButton;
    private static JLabel progressLabel = new JLabel();
    private static JProgressBar progressBar = new JProgressBar();

    public EDR2DoddleDicConverterUI() {
        edrRadioButton = new JRadioButton("EDR");
        edrRadioButton.setSelected(true);
        edrtRadioButton = new JRadioButton("EDRT");
        ButtonGroup group = new ButtonGroup();
        group.add(edrRadioButton);
        group.add(edrtRadioButton);
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new GridLayout(1, 2));
        radioButtonPanel.setBorder(BorderFactory.createTitledBorder("Dictionary Type"));
        radioButtonPanel.add(edrRadioButton);
        radioButtonPanel.add(edrtRadioButton);

        txtBox = new JCheckBox("Text", true);
        dbBox = new JCheckBox("Berkely DB", false);
        owlBox = new JCheckBox("OWL", false);
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new GridLayout(1, 3));
        checkBoxPanel.setBorder(BorderFactory.createTitledBorder("Conversion Type"));
        checkBoxPanel.add(txtBox);
        checkBoxPanel.add(dbBox);
        checkBoxPanel.add(owlBox);

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(1, 2));
        optionPanel.add(radioButtonPanel);
        optionPanel.add(checkBoxPanel);

        edrPathField = new JTextField(40);
        edrPathField.setEditable(false);
        refEDRPathButton = new JButton("Browse");
        refEDRPathButton.addActionListener(this);
        JPanel edrPathPanel = new JPanel();
        edrPathPanel.setLayout(new BorderLayout());
        edrPathPanel.add(edrPathField, BorderLayout.CENTER);
        edrPathPanel.add(refEDRPathButton, BorderLayout.EAST);
        edrPathPanel.setBorder(BorderFactory.createTitledBorder("Input: EDR Path"));

        doddleDicPathField = new JTextField(40);
        doddleDicPathField.setEditable(false);
        refDoddleDicPathButton = new JButton("Browse");
        refDoddleDicPathButton.addActionListener(this);
        JPanel doddleDicPanel = new JPanel();
        doddleDicPanel.setLayout(new BorderLayout());
        doddleDicPanel.add(doddleDicPathField, BorderLayout.CENTER);
        doddleDicPanel.add(refDoddleDicPathButton, BorderLayout.EAST);
        doddleDicPanel.setBorder(BorderFactory.createTitledBorder("Output: DODDLE EDR Path"));

        convertButton = new JButton("Convert");
        convertButton.addActionListener(this);
        JPanel progressBarPanel = new JPanel();
        progressBarPanel.setLayout(new BorderLayout());
        progressBarPanel.setBorder(BorderFactory.createTitledBorder("Progress"));
        progressBarPanel.add(progressBar, BorderLayout.CENTER);
        progressBarPanel.add(convertButton, BorderLayout.EAST);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(exitButton, BorderLayout.EAST);

        progressLabel.setBorder(BorderFactory.createTitledBorder("Message"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1));
        mainPanel.add(optionPanel);
        mainPanel.add(edrPathPanel);
        mainPanel.add(doddleDicPanel);
        mainPanel.add(progressBarPanel);
        mainPanel.add(progressLabel);

        Container contentPane = getContentPane();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();
        setTitle("EDR2DODDLE_Dic");
        setLocationRelativeTo(null);
    }

    public static void addProgressValue() {
        progressBar.setValue(progressBar.getValue() + 1);
    }

    public static void setProgressText(String text) {
        progressLabel.setText(text);
    }

    private void setDicPath(JTextField textField) {
        File currentDirectory = new File(textField.getText());
        JFileChooser jfc = new JFileChooser(currentDirectory);
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setDialogTitle("Select Directory");
        int fd = jfc.showOpenDialog(this);
        if (fd == JFileChooser.APPROVE_OPTION) {
            textField.setText(jfc.getSelectedFile().toString() + File.separator);
            System.out.println(textField.getText());
        }
    }

    private DictionaryType getDicType() {
        if (edrRadioButton.isSelected()) {
            return DictionaryType.EDR;
        } else if (edrtRadioButton.isSelected()) { return DictionaryType.EDRT; }
        return DictionaryType.EDR;
    }

    private void convertEDR2DoddleDic() {
        new Thread() {

            private void makeData() {
                progressLabel.setText("Make Data File");
                EDR2DoddleDicConverter.makeEDRData();
                EDR2DoddleDicConverter.writeEDRData();
            }

            private void makeDataFPList() {
                progressLabel.setText("Make Data File Pointer List");
                EDR2DoddleDicConverter.makeIDFilePointerMap();
                EDR2DoddleDicConverter.writeDataFilePointerList();
                EDR2DoddleDicConverter.clearDataFilePointerList();
                addProgressValue();
            }

            private void makeIndex() {
                progressLabel.setText("Make Index");
                EDR2DoddleDicConverter.makeEDRIndex();
                EDR2DoddleDicConverter.writeEDRIndex();
                addProgressValue();
            }

            private void makeIndexFPList() {
                progressLabel.setText("Make Index File Pointer List");
                EDR2DoddleDicConverter.writeIndexFilePointerList();
                addProgressValue();
            }

            private void clearMap() {
                progressLabel.setText("Clear Map");
                EDR2DoddleDicConverter.clearIDDefinitionMap();
                EDR2DoddleDicConverter.clearWordIDSetMap();
                EDR2DoddleDicConverter.clearIDFilePointerMap();
                EDR2DoddleDicConverter.clearWordFilePointerSetMap();
            }

            private void makeIDSubIDSetMap() {
                progressLabel.setText("Make ID SubIDSet Map");
                EDR2DoddleDicConverter.makeIDSubIDSetMap();
                EDR2DoddleDicConverter.writeIDSubIDSetMap();
                EDR2DoddleDicConverter.clearIDSubIDSetMap();
                addProgressValue();
            }

            private void makeConceptDefinitionMap(DictionaryType dicType) {
                if (dicType == DictionaryType.EDR) {
                    progressLabel.setText("make conceptDefinitionMap");
                    EDR2DoddleDicConverter.makeConceptDefinitionMap();
                    addProgressValue();
                }
            }

            private void convertEDR2DODDLETextDic(DictionaryType dicType) {
                makeData();
                makeDataFPList();
                makeIndex();
                makeIndexFPList();
                clearMap();
                makeIDSubIDSetMap();
                makeConceptDefinitionMap(dicType);
            }

            private void convertEDR2DODDLEDBDic(DictionaryType dicType) {
                DBManager edrDBManager = null;
                DBManager edrtDBManager = null;
                try {
                    if (dicType == DictionaryType.EDR) {
                        edrDBManager = new DBManager(false, doddleDicPathField.getText());
                        edrDBManager.makeDB("edr", doddleDicPathField.getText(), false);
                    } else {
                        edrtDBManager = new DBManager(false, doddleDicPathField.getText());
                        edrtDBManager.makeDB("edrt", doddleDicPathField.getText(), true);
                    }
                } catch (Exception e) {
                    // If an exception reaches this point, the last transaction
                    // did not
                    // complete. If the exception is RunRecoveryException,
                    // follow
                    // the Berkeley DB recovery procedures before running again.
                    e.printStackTrace();
                } finally {
                    if (edrDBManager != null) {
                        try {
                            // Always attempt to close the database cleanly.
                            edrDBManager.close();
                            System.out.println("Close DB");
                        } catch (Exception e) {
                            System.err.println("Exception during database close:");
                            e.printStackTrace();
                        }
                    }
                    if (edrtDBManager != null) {
                        try {
                            // Always attempt to close the database cleanly.
                            edrtDBManager.close();
                            System.out.println("Close DB");
                        } catch (Exception e) {
                            System.err.println("Exception during database close:");
                            e.printStackTrace();
                        }
                    }
                }
            }

            public void convertEDR2OWL(DictionaryType dicType) {
                Model ontModel = ModelFactory.createDefaultModel();
                String ns = "";
                if (dicType == DictionaryType.EDR) {
                    ns = DODDLEConstants.EDR_URI;
                } else if (dicType == DictionaryType.EDRT) {
                    ns = DODDLEConstants.EDRT_URI;
                }
                progressLabel.setText("Writing OWL Concept");
                EDR2DoddleDicConverter.makeEDRData();
                EDR2DoddleDicConverter.writeOWLConcept(ontModel, ns);
                addProgressValue();
                progressLabel.setText("Clear Map");
                EDR2DoddleDicConverter.clearIDDefinitionMap();
                addProgressValue();
                progressLabel.setText("Writing ID subID Set");
                EDR2DoddleDicConverter.writeIDSubIDOWL(ontModel, ns);
                addProgressValue();

                if (dicType == DictionaryType.EDR) {
                    Model ontDescriptionModel = ModelFactory.createDefaultModel();
                    progressLabel.setText("Write Region");
                    EDR2DoddleDicConverter.writeRegionOWL(ontDescriptionModel, ns);
                    EDR2DoddleDicConverter.saveOntology(ontDescriptionModel, dicType + "_Description.owl");
                }
                addProgressValue();
                EDR2DoddleDicConverter.saveOntology(ontModel, dicType + ".owl");
            }

            private void setProgressValue(DictionaryType dicType) {
                progressBar.setValue(0);
                progressLabel.setText("");
                int value = 0;
                if (dbBox.isSelected()) {
                    value += 3;
                }
                if (dicType == DictionaryType.EDR) {
                    if (txtBox.isSelected()) {
                        value += 9;
                    }
                } else if (dicType == DictionaryType.EDRT) {
                    if (txtBox.isSelected()) {
                        value += 6;
                    }
                }
                progressBar.setMaximum(value);
            }

            public void run() {
                DictionaryType dicType = getDicType();
                EDR2DoddleDicConverter.setEDRDicPath(edrPathField.getText(), dicType);
                EDR2DoddleDicConverter.setDODDLEDicPath(doddleDicPathField.getText());
                setProgressValue(dicType);
                try {
                    if (owlBox.isSelected()) {
                        progressBar.setMaximum(4);
                        convertEDR2OWL(dicType);
                    } else {
                        if (txtBox.isSelected()) {
                            convertEDR2DODDLETextDic(dicType);
                        }
                        if (dbBox.isSelected()) {
                            convertEDR2DODDLEDBDic(dicType);
                        }
                    }
                    progressLabel.setText("Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setValue(0);
                    progressLabel.setText("Error");
                }
            }
        }.start();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == refEDRPathButton) {
            setDicPath(edrPathField);
        } else if (e.getSource() == refDoddleDicPathButton) {
            setDicPath(doddleDicPathField);
        } else if (e.getSource() == convertButton) {
            convertEDR2DoddleDic();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            EDR2DoddleDicConverterUI converter = new EDR2DoddleDicConverterUI();
            converter.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
