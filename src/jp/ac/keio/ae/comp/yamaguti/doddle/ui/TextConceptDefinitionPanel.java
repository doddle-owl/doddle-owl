package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author shigeta
 * 
 * 2004-12-06 modified by takeshi morita
 * 
 */
public class TextConceptDefinitionPanel extends JPanel implements ListSelectionListener {

    private Map<String, Concept> wordConceptMap;
    private Map<String, Concept> complexWordConceptMap;
    private Map<String, Set<Concept>> wordConceptSetMap;

    public String corpusString;
    public List<String> inputWordList;

    private JList inputConceptJList;
    private ConceptDefinitionResultPanel resultPanel;
    private ConceptDefinitionAlgorithmPanel algorithmPanel;
    private ConceptDefinitionResultPanel.ConceptDefinitionPanel conceptDefinitionPanel;

    private DODDLEProject doddleProject;
    private DocumentSelectionPanel docSelectionPanel;
    private InputModuleUI inputModuleUI;

    public void setInputConceptJList() {
        inputWordList = getInputWordList();
        inputConceptJList.removeAll();
        DefaultListModel listModel = new DefaultListModel();
        for (String iw : inputWordList) {
            listModel.addElement(iw);
        }
        inputConceptJList.setModel(listModel);
    }

    public Concept getConcept(String word) {
        Concept c = null;
        if (complexWordConceptMap.get(word) != null) {
            c = complexWordConceptMap.get(word);
            // System.out.println("cid: " + id);
        } else if (wordConceptMap.get(word) != null) {
            c = wordConceptMap.get(word);
            // System.out.println("id: " + id);
        } else {
            Set<Concept> wordConceptSet = wordConceptSetMap.get(word);
            if (wordConceptSet != null) {
                c = (Concept) wordConceptSet.toArray()[0];
            }
        }
        if (c == null) { return null; }
        Concept concept = doddleProject.getConcept(c.getId());
        if (concept != null) { return concept; }
        if (c.getPrefix().equals("edr")) {
            return EDRDic.getEDRConcept(c.getId());
        } else if (c.getPrefix().equals("wn")) { return WordNetDic.getWNConcept(c.getId()); }
        return EDRDic.getEDRConcept(c.getId());
    }

    private Resource getResource(Concept c, Model ontology) {
        String id = c.getId();
        if (id.indexOf("UID") != -1) { return ontology.getResource(DODDLE.BASE_URI + id); }
        return ontology.getResource(DODDLE.EDR_URI + "ID" + id);
    }

    public Model addConceptDefinition(Model ontology) {
        for (int i = 0; i < resultPanel.getRelationCount(); i++) {
            Object[] relation = resultPanel.getRelation(i);
            String domainWord = (String) relation[1];
            String rangeWord = (String) relation[3];
            Concept property = (Concept) relation[2];
            Concept domainConcept = getConcept(domainWord);
            Concept rangeConcept = getConcept(rangeWord);

            // System.out.println("r: "+property+"d: "+domainConcept + "r:
            // "+rangeConcept);

            ontology.add(getResource(property, ontology), RDFS.domain, getResource(domainConcept, ontology));
            ontology.add(getResource(property, ontology), RDFS.range, getResource(rangeConcept, ontology));
        }
        return ontology;
    }

    public TextConceptDefinitionPanel(DODDLEProject project) {
        doddleProject = project;
        docSelectionPanel = project.getDocumentSelectionPanel();
        inputModuleUI = project.getInputModuleUI();

        inputConceptJList = new JList(new DefaultListModel());
        inputConceptJList.addListSelectionListener(this);

        algorithmPanel = new ConceptDefinitionAlgorithmPanel(inputConceptJList, doddleProject);
        resultPanel = new ConceptDefinitionResultPanel(inputConceptJList, algorithmPanel, doddleProject);
        conceptDefinitionPanel = resultPanel.getDefinePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, algorithmPanel, resultPanel);
        splitPane.setDividerSize(DODDLE.DIVIDER_SIZE);
        splitPane.setOneTouchExpandable(true);
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        setTableAction();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (inputConceptJList.getSelectedValue() != null) {
            String selectedInputConcept = inputConceptJList.getSelectedValue().toString();
            resultPanel.calcWSandARValue(selectedInputConcept);
        }
    }

    public void loadAcceptedandWrongPairSet(Set acceptedSet, Set wrongSet) {
        resultPanel.loadAcceptedandWrongPairSet(acceptedSet, wrongSet);
    }

    private void setTableAction() {
        resultPanel.getWordSpaceSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    // System.out.println(lsm.getMinSelectionIndex());
                    String c1 = inputConceptJList.getSelectedValue().toString();
                    String c2 = resultPanel.getWSTableRowConceptName(selectedRow);
                    conceptDefinitionPanel.setCText(c1, c2);
                }
            }
        });

        resultPanel.getAprioriSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    String c1 = inputConceptJList.getSelectedValue().toString();
                    String c2 = resultPanel.getARTableRowConceptName(selectedRow);
                    conceptDefinitionPanel.setCText(c1, c2);
                    // System.out.println("-----" + selectedRow);
                }
            }
        });

        resultPanel.getWASelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    // String c1 = comboBox.getSelectedItem().toString();
                    String c1 = inputConceptJList.getSelectedValue().toString();
                    String c2 = resultPanel.getWATableRowConceptName(selectedRow);
                    conceptDefinitionPanel.setCText(c1, c2);
                    // System.out.println("-----" + selectedRow);
                }
            }
        });

    }

    public int getWSResultTableSelectedRow() {
        return resultPanel.getWSTableSelectedRow();
    }

    public int getAprioriResultTableSelectedRow() {
        return resultPanel.getARTableSelectedRow();
    }

    public int getWAResultTableSelectedRow() {
        return resultPanel.getWATableSelectedRow();
    }

    public DefaultTableModel getWSResultTableModel() {
        return resultPanel.getWResultTableModel();
    }

    public DefaultTableModel getAprioriResultTableModel() {
        return resultPanel.getAResultTableModel();
    }

    public DefaultTableModel getWAResultTableModel() {
        return resultPanel.getWAResultTableModel();
    }

    public void saveResult() {

        resultPanel.getAcceptedandWrongPairText();

    }

    public void saveList(List list) {
        try {
            JFileChooser chooser = new JFileChooser(".");
            int returnVal = chooser.showSaveDialog(null);
            int index = -1;
            double max = -1;
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                PrintWriter f = new PrintWriter(new FileWriter(chooser.getSelectedFile()));
                while (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        ConceptPair pair = (ConceptPair) list.get(i);
                        // f.println(pair.toString());
                        if (pair.getRelationDoubleValue() > max) {
                            index = i;
                            max = pair.getRelationDoubleValue();
                        }
                    }
                    f.println(((ConceptPair) list.get(index)).toString());
                    System.out.println(((ConceptPair) list.get(index)).toString());

                    list.remove(index);
                    index = -1;
                    max = -1;
                }
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public ConceptPair getPair(String str, List list) {
        for (int i = 0; i < list.size(); i++) {
            if (((ConceptPair) list.get(i)).getCombinationToString().equals(str)) { return (ConceptPair) list.get(i); }
        }
        return null;
    }

    public boolean contains(List list, ConceptPair pair) {
        for (int i = 0; i < list.size(); i++) {
            if (pair.isSameCombination((ConceptPair) list.get(i))) { return true; }
        }
        return false;
    }

    public List makeValidList(List list) {
        List returnList = new ArrayList();
        List resultA = (ArrayList) list.get(0);
        boolean flag = false;
        for (int j = 0; j < resultA.size(); j++) {
            ConceptPair pair = (ConceptPair) resultA.get(j);
            for (int i = 1; i < list.size(); i++) {
                List resultB = (List) list.get(i);
                flag = contains(resultB, pair);
            }
            if (flag) {
                returnList.add(pair.getCombinationToString());
            }
        }
        return returnList;
    }

    public ConceptPair getSameCombination(ConceptPair pair, List list) {
        for (int i = 0; i < list.size(); i++) {
            ConceptPair item = (ConceptPair) list.get(i);
            if (item.isSameCombination(pair)) { return item; }
        }
        return null;
    }

    public Set<Document> getDocSet() {
        return docSelectionPanel.getDocSet();
    }

    private boolean isVerbConcept(Concept c) {
        if (c.getId().indexOf("UID") != -1) {
            if (doddleProject.getConstructPropertyTreePanel().isConceptContains(c)) { return true; }
            return false;
        }
        Set<List<Concept>> pathSet = null;
        if (c.getPrefix().equals("edr")) {
            pathSet = EDRTree.getInstance().getPathToRootSet(c.getId());
        } else if (c.getPrefix().equals("wn")) {
            pathSet = WordNetDic.getPathToRootSet(new Long(c.getId()));
        }
        for (List<Concept> path : pathSet) {
            if (path.size() == 1) { return false; }
            Concept upperConcept = path.get(1); // éñè€äTîOÇÃâ∫à Ç…à⁄ìÆÇ∆çsà◊Ç™Ç†ÇÈÇΩÇﬂÅCÇPÇ∆Ç∑ÇÈ
            // à⁄ìÆÇ‹ÇΩÇÕçsà◊ÇÃâ∫à äTîOÇÃèÍçáÇÕÅCìÆéåÇ∆å©Ç»Ç∑ÅD
            if (upperConcept.getId().equals("30f83e") || upperConcept.getId().equals("30f801")) { return true; }
        }
        return false;
    }

    public List<String> getInputWordList() {
        List<String> inputWordList = new ArrayList<String>();
        wordConceptMap = inputModuleUI.getWordConceptMap();
        wordConceptSetMap = inputModuleUI.getWordConceptSetMap();
        complexWordConceptMap = doddleProject.getConstructConceptTreePanel().getComplexWordConceptMap();
        Set<InputWordModel> inputWordModelSet = inputModuleUI.getInputWordModelSet();
        for (InputWordModel iwModel : inputWordModelSet) {
            String word = iwModel.getWord();
            Concept c = getConcept(word);
            // ï°çáåÍÇÃèÍçáÅCäTîOäKëwÇ™ç\ízå„Ç≈Ç»ÇØÇÍÇŒÅCConceptÇÕnullÇ∆Ç»ÇÈ
            if (c != null && !isVerbConcept(c)) {
                inputWordList.add(word);
            }
        }
        return inputWordList;
    }

    public Set<String> getComplexWordSet() {
        return complexWordConceptMap.keySet();
    }
}