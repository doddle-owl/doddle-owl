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
    private Map idSubIDSetMap;
    private TreeModel edrTreeModel;
    public static String ID_SUBIDSET_MAP = DODDLE.DODDLE_DIC + "idSubIDSetMapforEDR.txt";
    public static String EDRT_ID_SUBIDSET_MAP = DODDLE.DODDLE_EDRT_DIC + "idSubIDSetMapforEDR.txt";

    private static EDRTree edrTree;
    private static EDRTree edrtTree;

    private boolean isSpecial;

    public static EDRTree getEDRTree() {
        if (edrTree == null) {
            edrTree = new EDRTree(ID_SUBIDSET_MAP, ConceptTreeMaker.EDR_CLASS_ROOT_ID, false);
        }
        return edrTree;
    }

    public static EDRTree getEDRTTree() {
        if (edrtTree == null) {
            edrtTree = new EDRTree(EDRT_ID_SUBIDSET_MAP, ConceptTreeMaker.EDRT_CLASS_ROOT_ID, true);
        }
        return edrtTree;
    }

    private EDRTree(String idSubIDSetMapPath, String edrClassRootID, boolean t) {
        isSpecial = t;
        idNodeSetMap = new HashMap<String, Set<TreeNode>>();
        idSubIDSetMap = new HashMap();
        try {
            InputStream inputStream = new FileInputStream(idSubIDSetMapPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "SJIS"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] lines = line.replaceAll("\n", "").split("\t");
                idSubIDSetMap.put(lines[0], lines);
            }
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(edrClassRootID);
            Set<TreeNode> nodeSet = new HashSet<TreeNode>();
            nodeSet.add(rootNode);
            idNodeSetMap.put(edrClassRootID, nodeSet);
            makeEDRTree(edrClassRootID, rootNode);
            edrTreeModel = new DefaultTreeModel(rootNode);
            idSubIDSetMap.clear();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<List<Concept>> getPathToRootSet(String id) {
        Concept c = null;
        if (isSpecial) {
            EDRDic.getEDRTConcept(id);
        } else {
            EDRDic.getEDRConcept(id);
        }
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
                if (isSpecial) {
                    path.add(EDRDic.getEDRTConcept(nid));
                } else {
                    path.add(EDRDic.getEDRConcept(nid));
                }
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
        String[] subConceptSet = (String[]) idSubIDSetMap.get(id);
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
            if (idSubIDSetMap.get(subID) != null) {
                makeEDRTree(subID, subNode);
            }
            node.add(subNode);
        }
    }

    public static void main(String[] args) {
        DODDLE.IS_USING_DB = true;
        EDRDic.init();
        Set<List<Concept>> pathSet = EDRTree.getEDRTTree().getPathToRootSet("3f543e");
        // Set<List<Concept>> pathSet =
        // EDRTree.getEDRTree().getPathToRootSet("3c1170");
        for (List<Concept> path : pathSet) {
            for (Concept c : path) {
                System.out.print(c + "\t");
            }
            System.out.println();
        }
    }
}
