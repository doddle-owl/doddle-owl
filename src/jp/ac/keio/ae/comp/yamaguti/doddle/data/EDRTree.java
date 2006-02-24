package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import org.apache.log4j.*;

/*
 /**
 * @author takeshi morita
 */
public class EDRTree {

    private Map<String, Set<TreeNode>> idNodeSetMap;
    private Map conceptSubConceptSetMap;
    private TreeModel edrTreeModel;
    public static String CONCEPT_SUBCONCEPTSET_MAP = DODDLE.DODDLE_DIC + "conceptSubConceptSetMapforEDR.txt";

    private static EDRTree edrTree;

    public static EDRTree getInstance() {
        if (edrTree == null) {
            edrTree = new EDRTree();
        }
        return edrTree;
    }

    private EDRTree() {
        idNodeSetMap = new HashMap<String, Set<TreeNode>>();
        conceptSubConceptSetMap = new HashMap();
        try {
            // System.out.println(CONCEPT_SUBCONCEPTSET_MAP);
            InputStream inputStream = new FileInputStream(CONCEPT_SUBCONCEPTSET_MAP);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "SJIS"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] lines = line.replaceAll("\n", "").split("\t");
                conceptSubConceptSetMap.put(lines[0], lines);
            }
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(ConceptTreeMaker.EDR_CLASS_ROOT_ID);
            Set<TreeNode> nodeSet = new HashSet<TreeNode>();
            nodeSet.add(rootNode);
            idNodeSetMap.put(ConceptTreeMaker.EDR_CLASS_ROOT_ID, nodeSet);
            makeEDRTree(ConceptTreeMaker.EDR_CLASS_ROOT_ID, rootNode);
            edrTreeModel = new DefaultTreeModel(rootNode);
            conceptSubConceptSetMap.clear();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<List<Concept>> getPathToRootSet(String id) {
        Concept c = EDRDic.getEDRConcept(id);
        Set<TreeNode> nodeSet = idNodeSetMap.get(id);
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        if (nodeSet == null) {
            // è„à ÅEâ∫à ä÷åWÇ™íËã`Ç≥ÇÍÇƒÇ¢Ç»Ç¢ID
            pathToRootSet.add(Arrays.asList(new Concept[] { c}));
            DODDLE.getLogger().log(Level.DEBUG, "äKëwä÷åWÇ™íËã`Ç≥ÇÍÇƒÇ¢Ç»Ç¢äTîO: " + c);
            return pathToRootSet;
        }

        for (TreeNode node : nodeSet) {
            TreeNode[] pathToRoot = ((DefaultTreeModel) edrTreeModel).getPathToRoot(node);
            List<Concept> path = new ArrayList<Concept>();
            for (int j = 1; j < pathToRoot.length; j++) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) pathToRoot[j];
                String nid = (String) n.getUserObject();
                path.add(EDRDic.getEDRConcept(nid));
            }
            pathToRootSet.add(path);
        }
        return pathToRootSet;
    }

    private void getSubConcept(TreeNode node, Set subConceptSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode childNode = node.getChildAt(i);
            subConceptSet.add(childNode.toString());
            getSubConcept(childNode, subConceptSet);
        }
    }

    public void getSubIDSet(TreeNode node, Set nounIDSet, Set subIDSet) {
        for (int j = 0; j < node.getChildCount(); j++) {
            TreeNode childNode = node.getChildAt(j);
            if (nounIDSet.contains(childNode.toString())) {
                subIDSet.add(childNode);
                return;
            }
            getSubIDSet(childNode, nounIDSet, subIDSet);
        }
    }

    public String getSubID(String id, Set nounIDSet) {
        Set subIDSet = new HashSet();
        Set nodeSet = idNodeSetMap.get(id);
        for (Iterator i = nodeSet.iterator(); i.hasNext();) {
            TreeNode node = (TreeNode) i.next();
            getSubIDSet(node, nounIDSet, subIDSet);
        }
        if (subIDSet.size() == 0) { return null; }
        int depth = 100;
        String matchedID = null;
        for (Iterator i = subIDSet.iterator(); i.hasNext();) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) i.next();
            if (node.getDepth() < depth) {
                depth = node.getDepth();
                matchedID = node.toString();
            }
        }
        return matchedID;
    }

    public Set getSubIDsSet(String id) {
        Set nodeSet = idNodeSetMap.get(id);
        Set subConceptsSet = new HashSet();
        if (nodeSet == null) { return subConceptsSet; }
        for (Iterator i = nodeSet.iterator(); i.hasNext();) {
            Set subConceptSet = new HashSet();
            TreeNode node = (TreeNode) i.next();
            getSubConcept(node, subConceptSet);
            subConceptsSet.add(subConceptSet);
        }
        return subConceptsSet;
    }

    public Set getSiblingIDsSet(String id) {
        Set nodeSet = idNodeSetMap.get(id);
        Set siblingConceptsSet = new HashSet();
        if (nodeSet == null) { return siblingConceptsSet; }
        for (Iterator i = nodeSet.iterator(); i.hasNext();) {
            Set siblingConceptSet = new HashSet();
            TreeNode node = (TreeNode) i.next();
            // System.out.println("NODE: " + node);
            TreeNode parentNode = node.getParent();
            // System.out.println("PARENT_NODE: " + parentNode);
            for (int j = 0; j < parentNode.getChildCount(); j++) {
                TreeNode siblingNode = parentNode.getChildAt(j);
                if (siblingNode != node) {
                    siblingConceptSet.add(siblingNode.toString());
                }
            }
            siblingConceptsSet.add(siblingConceptSet);
        }
        return siblingConceptsSet;
    }

    private void makeEDRTree(String id, DefaultMutableTreeNode node) {
        String[] subConceptSet = (String[]) conceptSubConceptSetMap.get(id);
        for (int i = 1; i < subConceptSet.length; i++) {
            String subID = subConceptSet[i];
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subID);
            if (idNodeSetMap.get(subID) == null) {
                Set nodeSet = new HashSet();
                nodeSet.add(subNode);
                idNodeSetMap.put(subID, nodeSet);
            } else {
                Set nodeSet = idNodeSetMap.get(subID);
                nodeSet.add(subNode);
            }
            if (conceptSubConceptSetMap.get(subID) != null) {
                makeEDRTree(subID, subNode);
            }
            node.add(subNode);
        }
    }

    public static void main(String[] args) {
        // edrTree.getSubIDsSet("3aa966");
        // edrTree.getSiblingIDsSet("30f751");
        // Set pathSet = edrTree.getPathToRootSet("3bed80");
        // Set pathSet = edrTree.getPathToRootSet("1faaeb");
        System.out.println(CONCEPT_SUBCONCEPTSET_MAP);
        Set pathSet = edrTree.getPathToRootSet("3c1170");
        for (Iterator i = pathSet.iterator(); i.hasNext();) {
            List path = (List) i.next();
            for (Iterator j = path.iterator(); j.hasNext();) {
                System.out.print(j.next() + "\t");
            }
            System.out.println();
        }
    }
}
