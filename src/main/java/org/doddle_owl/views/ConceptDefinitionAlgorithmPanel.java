/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 *
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.doddle_owl.views;

import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.models.ConceptPair;
import org.doddle_owl.models.Document;
import org.doddle_owl.models.WordSpaceData;
import org.doddle_owl.utils.Apriori;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.utils.WordSpace;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Yoshihiro Shigeta
 * @author Takeshi Morita
 */
public class ConceptDefinitionAlgorithmPanel extends JPanel implements ChangeListener,
        ActionListener {

    private Set<WordSpace> wordSpaceSet;
    private Set<Apriori> aprioriSet;
    private TreeMap<Document, Map<String, List<ConceptPair>>> docWSResultMap;
    private TreeMap<Document, Map<String, List<ConceptPair>>> docAprioriResultMap;

    private JLabel minSupport;
    private JTextField minSupportField;
    private JSlider minConfidenceSlider;
    private JLabel confidenceValue;

    private JLabel wordSpaceValue;
    private JSlider wordSpaceValueSlider;
    private JLabel gramNumber;
    private JLabel gramCount;
    private JLabel frontscope;
    private JLabel behindscope;
    private JTextField gramNumberField;
    private JTextField gramCountField;
    private JTextField frontScopeField;
    private JTextField behindScopeField;

    private JButton exeWordSpaceButton;
    private JButton exeAprioriButton;

    private JList inputConceptJList;

    private JComponent wordSpaceParamPanel;
    private JComponent aprioriParamPanel;

    private DODDLEProject doddleProject;

    public ConceptDefinitionAlgorithmPanel(JList list, DODDLEProject project) {
        inputConceptJList = list;
        doddleProject = project;

        wordSpaceSet = new HashSet<WordSpace>();
        aprioriSet = new HashSet<Apriori>();
        docWSResultMap = new TreeMap<Document, Map<String, List<ConceptPair>>>();
        docAprioriResultMap = new TreeMap<Document, Map<String, List<ConceptPair>>>();

        wordSpaceValueSlider = new JSlider();
        wordSpaceValueSlider.addChangeListener(this);
        minConfidenceSlider = new JSlider();
        minConfidenceSlider.addChangeListener(this);

        gramNumber = new JLabel("N-Gram    ");
        gramCount = new JLabel("Gram Count    ");
        frontscope = new JLabel("Front Scope    ");
        behindscope = new JLabel("Behind Scope    ");

        minSupport = new JLabel("Minimum Support     ");

        gramNumberField = new JTextField();
        gramNumberField.setHorizontalAlignment(JTextField.RIGHT);
        gramNumberField.setText("4");
        gramCountField = new JTextField();
        gramCountField.setHorizontalAlignment(JTextField.RIGHT);
        gramCountField.setText("7");
        frontScopeField = new JTextField();
        frontScopeField.setHorizontalAlignment(JTextField.RIGHT);
        frontScopeField.setText("60");
        behindScopeField = new JTextField();
        behindScopeField.setHorizontalAlignment(JTextField.RIGHT);
        behindScopeField.setText("10");

        minSupportField = new JTextField();
        minSupportField.setHorizontalAlignment(JTextField.RIGHT);
        minSupportField.setText("0");

        exeWordSpaceButton = new JButton(Translator.getTerm("ExecuteWordSpaceButton"));
        exeWordSpaceButton.addActionListener(this);
        exeAprioriButton = new JButton(Translator.getTerm("ExecuteAprioriButton"));
        exeAprioriButton.addActionListener(this);

        wordSpaceParamPanel = getNorthWestComponent(getWordSpacePanel());
        aprioriParamPanel = getNorthWestComponent(getAprioriPanel());
        /*
         * View[] mainViews = new View[2]; ViewMap viewMap = new ViewMap();
         *
         * mainViews[0] = new
         * View(Translator.getString("ConceptDefinitionPanel.WordSpaceParameters"
         * ), null, getWestComponent(wordSpaceParamPanel)); mainViews[1] = new
         * View
         * (Translator.getString("ConceptDefinitionPanel.AprioriParameters"),
         * null, getWestComponent(aprioriParamPanel));
         *
         * for (int i = 0; i < mainViews.length; i++) { viewMap.addView(i,
         * mainViews[i]); } RootWindow rootWindow =
         * Utils.createDODDLERootWindow(viewMap); //SplitWindow sw1 = new
         * SplitWindow(false, 0.3f, mainViews[0], mainViews[1]);
         * rootWindow.setWindow(new TabWindow(new DockingWindow[]{mainViews[0],
         * mainViews[1]}));
         *
         * setLayout(new BorderLayout()); add(rootWindow, BorderLayout.CENTER);
         * /* JTabbedPane parameterTab = new JTabbedPane();
         * parameterTab.add(getWestComponent(wordSpaceParamPanel), Translator
         * .getString("ConceptDefinitionPanel.WordSpaceParameters"));
         * parameterTab.add(getWestComponent(aprioriParamPanel), Translator
         * .getString("ConceptDefinitionPanel.AprioriParameters"));
         *
         * setLayout(new BorderLayout()); add(parameterTab,
         * BorderLayout.CENTER);
         */
    }

    public JComponent getWordSpaceParamPanel() {
        return wordSpaceParamPanel;
    }

    public JComponent getAprioriParamPanel() {
        return aprioriParamPanel;
    }

    public Map<Document, Map<String, List<ConceptPair>>> getDocWordSpaceResult() {
        return docWSResultMap;
    }

    public Map<Document, Map<String, List<ConceptPair>>> getDocAprioriResult() {
        return docAprioriResultMap;
    }

    public int getGramNumber() {
        int gramNum = 0;
        if (gramNumberField.getText() != null) {
            gramNum = Integer.valueOf(gramNumberField.getText());
        }
        return gramNum;
    }

    public int getGramCount() {
        int gramCount = 0;
        if (gramCountField.getText() != null) {
            gramCount = Integer.valueOf(gramCountField.getText());
        }
        return gramCount;
    }

    public int getFrontScope() {
        int frontScope = 0;
        if (frontScopeField.getText() != null) {
            frontScope = Integer.valueOf(frontScopeField.getText());
        }
        return frontScope;
    }

    public int getBehindScope() {
        int behindScope = 0;
        if (behindScopeField.getText() != null) {
            behindScope = Integer.valueOf(behindScopeField.getText());
        }
        return behindScope;
    }

    public double getMinSupport() {
        double minSupport = 0;
        if (minSupportField.getText() != null) {
            minSupport = Double.valueOf(minSupportField.getText());
        }
        return minSupport;
    }

    private JComponent getEastComponent(JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(comp, BorderLayout.EAST);
        return p;
    }

    private JComponent getNorthWestComponent(JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(comp, BorderLayout.WEST);
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.add(p, BorderLayout.NORTH);
        return p2;
    }

    private JPanel getAprioriPanel() {
        confidenceValue = new JLabel("0.50");
        confidenceValue.setFont(new Font("Dialog", Font.PLAIN, 14));
        JPanel barPanel = new JPanel();
        barPanel.setPreferredSize(new Dimension(150, 20));
        barPanel.setLayout(new BorderLayout());
        barPanel.add(confidenceValue, BorderLayout.WEST);
        barPanel.add(minConfidenceSlider, BorderLayout.CENTER);

        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new GridLayout(2, 2, 0, 0));
        paramPanel.add(minSupport);
        paramPanel.add(minSupportField);
        paramPanel.add(new JLabel("Minimum Confidence"));
        paramPanel.add(barPanel);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(paramPanel, BorderLayout.NORTH);
        panel.add(getEastComponent(exeAprioriButton), BorderLayout.SOUTH);

        return panel;
    }

    public double getMinConfidence() {
        return Double.parseDouble(confidenceValue.getText());
    }

    public double getWordSpaceUnderValue() {
        return Double.parseDouble(wordSpaceValue.getText());
    }

    private JPanel getWordSpacePanel() {
        // Integer inte = new Integer(wordSpaceValueSlider.getValue());
        wordSpaceValue = new JLabel("0.50");
        wordSpaceValue.setFont(new Font("Dialog", Font.PLAIN, 14));

        JPanel barPanel = new JPanel();
        barPanel.setPreferredSize(new Dimension(150, 20));
        barPanel.setLayout(new BorderLayout());
        barPanel.add(wordSpaceValue, BorderLayout.WEST);
        barPanel.add(wordSpaceValueSlider, BorderLayout.CENTER);

        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new GridLayout(3, 4, 5, 5));
        paramPanel.add(gramNumber);
        paramPanel.add(gramNumberField);
        paramPanel.add(gramCount);
        paramPanel.add(gramCountField);
        paramPanel.add(frontscope);
        paramPanel.add(frontScopeField);
        paramPanel.add(behindscope);
        paramPanel.add(behindScopeField);
        paramPanel.add(new JLabel("WordSpace Value"));
        paramPanel.add(barPanel);
        paramPanel.add(new Label()); // dammy
        paramPanel.add(exeWordSpaceButton);

        return paramPanel;
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == minConfidenceSlider) {
            Integer inte = minConfidenceSlider.getValue();
            Double value = inte.doubleValue() / 100;
            if (value.toString().length() == 4) {
                confidenceValue.setText(value.toString());
            } else {
                confidenceValue.setText(value.toString() + "0");
            }
        } else if (e.getSource() == wordSpaceValueSlider) {
            Integer inte = wordSpaceValueSlider.getValue();
            Double value = inte.doubleValue() / 100;
            if (value.toString().length() == 4) {
                wordSpaceValue.setText(value.toString());
            } else {
                wordSpaceValue.setText(value.toString() + "0");
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exeWordSpaceButton) {
            SwingWorker worker = new SwingWorker<String, String>() {
                public String doInBackground() {
                    exeWordSpace();
                    return "done";
                }
            };
            DODDLE_OWL.STATUS_BAR.setSwingWorker(worker);
            worker.execute();
        } else if (e.getSource() == exeAprioriButton) {
            SwingWorker worker = new SwingWorker<String, String>() {
                public String doInBackground() {
                    exeApriori();
                    return "done";
                }
            };
            DODDLE_OWL.STATUS_BAR.setSwingWorker(worker);
            worker.execute();
        }
    }

    public WordSpaceData getWordSpaceData() {
        int gramNumber = getGramNumber();
        int gramCount = getGramCount();
        int frontScope = getFrontScope();
        int behindScope = getBehindScope();
        double underValue = getWordSpaceUnderValue();
        return new WordSpaceData(gramNumber, gramCount, frontScope, behindScope, underValue);
    }

    public void setInputConcept() {
        wordSpaceSet.clear();
        aprioriSet.clear();
        ConceptDefinitionPanel conceptDefinitionPanel = doddleProject.getConceptDefinitionPanel();
        conceptDefinitionPanel.setInputConceptJList();
        if (0 < conceptDefinitionPanel.getInputTermList().size()) {
            Set<Document> docSet = doddleProject.getDocumentSelectionPanel().getDocSet();
            for (Document doc : docSet) {
                wordSpaceSet.add(new WordSpace(conceptDefinitionPanel, doc));
                aprioriSet.add(new Apriori(conceptDefinitionPanel, doc));
            }
        }
    }

    private List<String> getTargetInputWordList(Document doc) {
        List<String> inputWordList = doddleProject.getConceptDefinitionPanel().getInputTermList();
        if (inputWordList == null) {
            return null;
        }
        List<String> targetInputWordList = new ArrayList<String>();
        for (String iw : inputWordList) {
            String text = doc.getText();
            // '_'が入力単語に含まれている場合には，スペースに変換した場合もチェックする
            if (text.indexOf(iw.replaceAll("_", " ")) != -1 || text.indexOf(iw) != -1) {
                targetInputWordList.add(iw);
            } else {
                // System.out.println("文書中に存在しない入力単語: " + iw);
            }
        }
        DODDLE_OWL.STATUS_BAR.printMessage(doc.getFile().getAbsolutePath() + ": "
                + targetInputWordList.size() + "/" + inputWordList.size());
        return targetInputWordList;
    }

    public static int APRIORI = 0;
    public static int WORDSPACE = 1;

    public void saveResult(File dir, int algorithm) {
        TreeMap<Document, Map<String, List<ConceptPair>>> docResultMap = null;
        if (algorithm == APRIORI) {
            docResultMap = docAprioriResultMap;
        } else if (algorithm == WORDSPACE) {
            docResultMap = docWSResultMap;
        }
        BufferedWriter writer = null;
        try {
            for (Entry<Document, Map<String, List<ConceptPair>>> entry : docResultMap.entrySet()) {
                Document doc = entry.getKey();
                Map<String, List<ConceptPair>> map = entry.getValue();
                if (map == null) {
                    continue;
                }
                FileOutputStream fos = new FileOutputStream(dir.getPath() + File.separator
                        + doc.getFile().getName());
                writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                for (List<ConceptPair> pairList : map.values()) {
                    Collections.sort(pairList);
                    for (ConceptPair pair : pairList) {
                        writer.write(pair.getFromConceptLabel());
                        writer.write("\t");
                        writer.write(pair.getToConceptLabel());
                        writer.write("\t");
                        writer.write(pair.getRelatoinValue().toString());
                        writer.newLine();
                    }
                }
                writer.close(); // ここでファイルを閉じないと，結果が書き込まれない場合がある
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close(); // 途中で例外が発生した場合に対処
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }

    public void saveResult(int projectID, Statement stmt, int algorithm) {
        TreeMap<Document, Map<String, List<ConceptPair>>> docResultMap = null;
        String algorithmTable = "";
        if (algorithm == APRIORI) {
            docResultMap = docAprioriResultMap;
            algorithmTable = "apriori_result";
        } else if (algorithm == WORDSPACE) {
            docResultMap = docWSResultMap;
            algorithmTable = "wordspace_result";
        }
        int docID = 1;
        try {
            for (Entry<Document, Map<String, List<ConceptPair>>> entry : docResultMap.entrySet()) {
                Document doc = entry.getKey();
                Map<String, List<ConceptPair>> map = entry.getValue();
                if (map == null) {
                    continue;
                }
                for (List<ConceptPair> pairList : map.values()) {
                    Collections.sort(pairList);
                    for (ConceptPair pair : pairList) {
                        String fromConceptLabel = URLEncoder.encode(pair.getFromConceptLabel(),
                                "UTF8");
                        String toConceptLabel = URLEncoder.encode(pair.getToConceptLabel(), "UTF8");
                        String sql = "INSERT INTO " + algorithmTable
                                + " (Project_ID,Doc_ID,Term1,Term2,Value) " + "VALUES(" + projectID
                                + "," + docID + ",'" + fromConceptLabel + "','" + toConceptLabel
                                + "'," + pair.getRelatoinValue() + ")";
                        stmt.executeUpdate(sql);
                    }
                }
                docID++;
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void loadResult(File dir, int algorithm) {
        TreeMap<Document, Map<String, List<ConceptPair>>> docResultMap = null;
        if (algorithm == APRIORI) {
            docResultMap = docAprioriResultMap;
        } else if (algorithm == WORDSPACE) {
            docResultMap = docWSResultMap;
        }
        BufferedReader reader = null;
        try {
            Set<Document> docSet = doddleProject.getDocumentSelectionPanel().getDocSet();
            for (Document doc : docSet) {
                File file = new File(dir.getPath() + File.separator + doc.getFile().getName());
                if (!file.exists()) {
                    continue;
                }
                FileInputStream fis = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                Map<String, List<ConceptPair>> wordCPListMap = new HashMap<String, List<ConceptPair>>();
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] lines = line.split("\t");
                    if (lines.length != 3) { // ファイルが正常に保存できなかった場合の対応
                        // System.out.println(file.getAbsolutePath() + ": " +
                        // line);
                        continue;
                    }
                    String toC = lines[0];
                    String fromC = lines[1];
                    Double relVal = Double.parseDouble(lines[2]);
                    ConceptPair cp = new ConceptPair(toC, fromC, relVal);
                    if (wordCPListMap.get(toC) != null) {
                        List<ConceptPair> cpList = wordCPListMap.get(toC);
                        cpList.add(cp);
                    } else {
                        List<ConceptPair> cpList = new ArrayList<>();
                        cpList.add(cp);
                        wordCPListMap.put(toC, cpList);
                    }
                }
                docResultMap.put(doc, wordCPListMap);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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

    public void loadResult(int projectID, Statement stmt, int algorithm) {
        TreeMap<Document, Map<String, List<ConceptPair>>> docResultMap = null;
        String resultTable = "";
        if (algorithm == APRIORI) {
            docResultMap = docAprioriResultMap;
            resultTable = "apriori_result";
        } else if (algorithm == WORDSPACE) {
            docResultMap = docWSResultMap;
            resultTable = "wordspace_result";
        }

        try {
            Map<String, Integer> docIDMap = new HashMap<String, Integer>();
            String sql = "SELECT * from doc_info where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int docID = rs.getInt("Doc_ID");
                String path = URLDecoder.decode(rs.getString("Doc_Path"), "UTF8");
                docIDMap.put(path, docID);
            }

            Set<Document> docSet = doddleProject.getDocumentSelectionPanel().getDocSet();
            for (Document doc : docSet) {
                int docID = docIDMap.get(doc.getFile().getAbsolutePath());
                sql = "SELECT * from " + resultTable + " where Project_ID=" + projectID
                        + " and Doc_ID=" + docID;
                rs = stmt.executeQuery(sql);
                Map<String, List<ConceptPair>> wordCPListMap = new HashMap<String, List<ConceptPair>>();
                while (rs.next()) {
                    String term1 = URLDecoder.decode(rs.getString("Term1"), "UTF8");
                    String term2 = URLDecoder.decode(rs.getString("Term2"), "UTF8");
                    double value = rs.getDouble("Value");
                    ConceptPair cp = new ConceptPair(term1, term2, value);
                    if (wordCPListMap.get(term1) != null) {
                        List<ConceptPair> cpList = wordCPListMap.get(term1);
                        cpList.add(cp);
                    } else {
                        List<ConceptPair> cpList = new ArrayList<ConceptPair>();
                        cpList.add(cp);
                        wordCPListMap.put(term1, cpList);
                    }
                }
                docResultMap.put(doc, wordCPListMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void saveConceptDefinitionParameters(File file) {
        BufferedWriter writer = null;
        try {
            Properties properties = new Properties();
            properties.setProperty("N-Gram", gramNumberField.getText());
            properties.setProperty("Gram_Count", gramCountField.getText());
            properties.setProperty("Front_Scope", frontScopeField.getText());
            properties.setProperty("Behind_Scope", behindScopeField.getText());
            properties.setProperty("WordSpace_Value",
                    String.valueOf(wordSpaceValueSlider.getValue()));
            properties.setProperty("Minimum_Support", minSupportField.getText());
            properties.setProperty("Minimum_Confidence",
                    String.valueOf(minConfidenceSlider.getValue()));
            FileOutputStream fos = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            properties.store(writer, "Concept Definition Parameters");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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

    public void saveConceptDefinitionParameters(int projectID, Statement stmt) {
        try {
            int minimumConfidence = minConfidenceSlider.getValue();
            double minimumSupport = Double.parseDouble(minSupportField.getText());
            int frontScope = Integer.parseInt(frontScopeField.getText());
            int behindScope = Integer.parseInt(behindScopeField.getText());
            int nGram = Integer.parseInt(gramNumberField.getText());
            int gramCount = Integer.parseInt(gramCountField.getText());
            int wordSpaceValue = wordSpaceValueSlider.getValue();

            String sql = "INSERT INTO concept_definition_parameter (Project_ID,Minimum_Confidence,Minimum_Support,Front_Scope,Behind_Scope,N_Gram,Gram_Count,Word_Space_Value) "
                    + "VALUES("
                    + projectID
                    + ","
                    + minimumConfidence
                    + ","
                    + minimumSupport
                    + ","
                    + frontScope
                    + ","
                    + behindScope
                    + ","
                    + nGram
                    + ","
                    + gramCount
                    + ","
                    + wordSpaceValue + ")";
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void loadConceptDefinitionParameters(File file) {
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            Properties properties = new Properties();
            properties.load(reader);
            gramNumberField.setText(properties.getProperty("N-Gram"));
            gramCountField.setText(properties.getProperty("Gram_Count"));
            frontScopeField.setText(properties.getProperty("Front_Scope"));
            behindScopeField.setText(properties.getProperty("Behind_Scope"));
            wordSpaceValueSlider.setValue(Integer.parseInt(properties
                    .getProperty("WordSpace_Value")));
            minSupportField.setText(properties.getProperty("Minimum_Support"));
            minConfidenceSlider.setValue(Integer.parseInt(properties
                    .getProperty("Minimum_Confidence")));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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

    public void loadConceptDefinitionParameters(int projectID, Statement stmt) {
        try {
            String sql = "SELECT * from  concept_definition_parameter where Project_ID="
                    + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                gramNumberField.setText(Integer.toString(rs.getInt("N_Gram")));
                gramCountField.setText(Integer.toString(rs.getInt("Gram_Count")));
                frontScopeField.setText(Integer.toString(rs.getInt("Front_Scope")));
                behindScopeField.setText(Integer.toString(rs.getInt("Behind_Scope")));
                wordSpaceValueSlider.setValue((int) rs.getDouble("Word_Space_Value"));
                minSupportField.setText(Double.toString(rs.getDouble("Minimum_Support")));
                minConfidenceSlider.setValue((int) rs.getDouble("Minimum_Confidence"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exeWordSpace() {
        try {
            DODDLE_OWL.STATUS_BAR.setLastMessage(Translator.getTerm("ExecuteWordSpaceButton"));
            DODDLE_OWL.STATUS_BAR.startTime();
            DODDLE_OWL.STATUS_BAR.initNormal(4 * wordSpaceSet.size());
            DODDLE_OWL.STATUS_BAR.lock();
            docWSResultMap.clear();
            WordSpaceData wsData = getWordSpaceData();
            for (WordSpace ws : wordSpaceSet) {
                if (ws != null) {
                    ws.setWSData(wsData);
                    List<String> targetInputWordList = getTargetInputWordList(ws.getDocument());
                    docWSResultMap.put(ws.getDocument(),
                            ws.calcWordSpaceResult(targetInputWordList));
                }
            }
            if (0 < inputConceptJList.getModel().getSize()) {
                inputConceptJList.setSelectedIndex(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DODDLE_OWL.STATUS_BAR.printMessage("WordSpace Done");
            DODDLE_OWL.STATUS_BAR.unLock();
            DODDLE_OWL.STATUS_BAR.hideProgressBar();
        }
    }

    public void exeApriori() {
        try {
            DODDLE_OWL.STATUS_BAR.setLastMessage(Translator.getTerm("ExecuteAprioriButton"));
            DODDLE_OWL.STATUS_BAR.startTime();
            DODDLE_OWL.STATUS_BAR.initNormal(2 * aprioriSet.size());
            DODDLE_OWL.STATUS_BAR.lock();
            docAprioriResultMap.clear();
            double minSupport = getMinSupport();
            double minConfidence = getMinConfidence();
            for (Apriori apriori : aprioriSet) {
                if (apriori != null) {
                    apriori.setParameters(minSupport, minConfidence);
                    List<String> targetInputWordList = getTargetInputWordList(apriori.getDocument());
                    docAprioriResultMap.put(apriori.getDocument(),
                            apriori.calcAprioriResult(targetInputWordList));
                }
            }
            if (0 < inputConceptJList.getModel().getSize()) {
                inputConceptJList.setSelectedIndex(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DODDLE_OWL.STATUS_BAR.printMessage("Apriori Done");
            DODDLE_OWL.STATUS_BAR.unLock();
            DODDLE_OWL.STATUS_BAR.hideProgressBar();
        }
    }
}