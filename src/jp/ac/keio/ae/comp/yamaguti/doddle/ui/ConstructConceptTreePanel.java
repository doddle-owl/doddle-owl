/*
 * @(#)  2006/04/06
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import org.apache.log4j.*;

/**
 * @author takeshi morita
 */
public abstract class ConstructConceptTreePanel extends JPanel implements ComplexConceptTreeInterface {

    protected UndefinedWordListPanel undefinedWordListPanel;
    protected ConceptTreePanel conceptTreePanel;
    protected ConceptInformationPanel conceptInfoPanel;

    protected ConceptDriftManagementPanel conceptDriftManagementPanel;
    protected ConceptTreeMaker treeMaker = ConceptTreeMaker.getInstance();

    protected int trimmedConceptNum;
    protected int beforeTrimmingConceptNum;
    protected int addedSINNum;
    protected double addedAbstractComplexConceptCnt;
    protected double averageAbstracComplexConceptGroupSiblingConceptCnt;

    protected DODDLEProject project;

    public void clearPanel() {
        conceptInfoPanel.clearPanel();
    }
    
    public void loadTrimmedResultAnalysis(DODDLEProject project, File file) {
        if (!file.exists()) { return; }
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "JISAutoDetect"));
            String line = "";
            Map<String, List<List<Concept>>> idTrimmedConceptListMap = new HashMap<String, List<List<Concept>>>();
            while ((line = reader.readLine()) != null) {
                String[] lines = line.split("\\|");
                String[] concepts = lines[0].split(",");
                List<List<Concept>> trimmedConceptList = new ArrayList<List<Concept>>();
                for (int i = 1; i < lines.length; i++) {
                    String[] conceptStrs = lines[i].split(",");
                    List<Concept> list = new ArrayList<Concept>();
                    for (int j = 0; j < conceptStrs.length; j++) {
                        list.add(DODDLEDic.getConcept(conceptStrs[j]));
                    }
                    trimmedConceptList.add(list);
                }
                idTrimmedConceptListMap.put(concepts[0] + concepts[1], trimmedConceptList);
            }
            TreeModel treeModel = conceptTreePanel.getConceptTree().getModel();
            ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
            loadTrimmedResultAnalysis(rootNode, idTrimmedConceptListMap);
            conceptDriftManagementPanel.setTRADefaultValue();
            reader.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void loadTrimmedResultAnalysis(ConceptTreeNode node,
            Map<String, List<List<Concept>>> idTrimmedConceptListMap) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            String id = childNode.getConcept().getURI() + node.getConcept().getURI();
            List<List<Concept>> trimmedConceptList = idTrimmedConceptListMap.get(id);
            if (trimmedConceptList != null && 0 < trimmedConceptList.size()) {
                childNode.setTrimmedConceptList(trimmedConceptList);
                conceptDriftManagementPanel.addTRANode(childNode);
            }
            loadTrimmedResultAnalysis(childNode, idTrimmedConceptListMap);
        }
    }

    public ConceptDriftManagementPanel getConceptDriftManagementPanel() {
        return conceptDriftManagementPanel;
    }

    public void setUndefinedWordListModel(ListModel model) {
        undefinedWordListPanel.setUndefinedWordListModel(model);
        repaint();
    }

    public Map getIDTypicalWordMap() {
        return conceptTreePanel.getConceptTypicalWordMap();
    }

    public void loadIDTypicalWord(Map idTypicalWordMap) {
        conceptTreePanel.loadConceptTypicalWord(idTypicalWordMap);
    }

    public TreeModel getConceptTreeModel() {
        return conceptTreePanel.getConceptTree().getModel();
    }

    public Set<Concept> getConceptSet() {
        TreeModel treeModel = getConceptTreeModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
        Set<Concept> conceptSet = new HashSet<Concept>();
        getConceptSet(rootNode, conceptSet);
        return conceptSet;
    }

    private void getConceptSet(ConceptTreeNode node, Set<Concept> conceptSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            conceptSet.add(childNode.getConcept());
            getConceptSet(childNode, conceptSet);
        }
    }

    public JTree getConceptTree() {
        return conceptTreePanel.getConceptTree();
    }

    public TreeModel getDefaultConceptTreeModel(Set pathSet, String type) {
        return treeMaker.getDefaultConceptTreeModel(pathSet, project, type);
    }

    public void addJPWord(String identity, String word) {
        conceptTreePanel.addJPWord(identity, word);
    }
    public void addSubConcept(String identity, String word) {

        conceptTreePanel.addSubConcept(identity, word);
    }

    public double getAddedAbstractComplexConceptCnt() {
        return addedAbstractComplexConceptCnt;
    }

    public double getAverageAbstracComplexConceptGroupSiblingConceptCnt() {
        return averageAbstracComplexConceptGroupSiblingConceptCnt;
    }

    public void init() {
        addedAbstractComplexConceptCnt = 0;
        averageAbstracComplexConceptGroupSiblingConceptCnt = 0;
        ConceptTreeMaker.getInstance().init();
        conceptTreePanel.getConceptTree().setModel(new DefaultTreeModel(null));
    }

    public int getBeforeTrimmingConceptNum() {
        return beforeTrimmingConceptNum;
    }

    public int getAddedSINNum() {
        return addedSINNum;
    }

    public int getTrimmedConceptNum() {
        return trimmedConceptNum;
    }

    public int getAfterTrimmingConceptNum() {
        return beforeTrimmingConceptNum - trimmedConceptNum;
    }

    public int getAllConceptCnt() {
        // System.out.println(Utils.getAllConcept(conceptTreePanel.getConceptTree().getModel()));
        return Utils.getAllConcept(conceptTreePanel.getConceptTree().getModel()).size();
    }

    public double getChildCntAverage() {
        return Utils.getChildCntAverage(conceptTreePanel.getConceptTree().getModel());
    }

    public Set getAllConceptURI() {
        return conceptTreePanel.getAllConceptURI();
    }

    public ConceptTreeNode getTreeModelRoot() {
        JTree conceptTree = conceptTreePanel.getConceptTree();
        if (conceptTree.getModel().getRoot() instanceof ConceptTreeNode) { return (ConceptTreeNode) conceptTree
                .getModel().getRoot(); }
        return null;
    }

    public void expandTree() {
        JTree conceptTree = conceptTreePanel.getConceptTree();
        for (int i = 0; i < conceptTree.getRowCount(); i++) {
            conceptTree.expandPath(conceptTree.getPathForRow(i));
        }
    }

    public Map<String, Concept> getComplexWordConceptMap() {
        return conceptTreePanel.getComplexWordConceptMap();
    }

    public Set getSupConceptSet(String id) {
        return conceptTreePanel.getSupConceptSet(id);
    }

    public void setVisibleConceptTree(boolean isVisible) {
        conceptTreePanel.getConceptTree().setVisible(isVisible);
    }
    public void setConceptDriftManagementResult() {
        conceptDriftManagementPanel.setConceptDriftManagementResult();
    }

    public void setConceptTreeModel(TreeModel model) {
        conceptTreePanel.getConceptTree().setModel(model);
    }

    public void checkMultipleInheritance(TreeModel model) {
        conceptTreePanel.checkAllMultipleInheritanceNode(model);
    }

    public void searchSameConceptTreeNode(Concept concept, ConceptTreeNode node, Set sameConceptSet) {
        conceptTreePanel.searchSameConceptTreeNode(concept, node, sameConceptSet);
    }
    
    public void deleteLinkToUpperConcept(ConceptTreeNode targetDeleteNode) {
        conceptTreePanel.deleteLinkToUpperConcept(targetDeleteNode);
    }
}
