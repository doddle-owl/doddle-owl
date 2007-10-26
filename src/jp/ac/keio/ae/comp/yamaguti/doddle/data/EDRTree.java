package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/*
 /**
 * @author takeshi morita
 */
public class EDRTree {

    private boolean isSpecial;
    private static EDRTree edrTree;
    private static EDRTree edrtTree;
    private TreeModel edrTreeModel;

    private Map<String, String[]> idSubIDSetMap;
    private Map<String, Set<TreeNode>> idNodeSetMap;
    public static String ID_SUBIDSET_MAP = DODDLEConstants.EDR_HOME + "idSubIDSetMapforEDR.txt";
    public static String EDRT_ID_SUBIDSET_MAP = DODDLEConstants.EDRT_HOME + "idSubIDSetMapforEDR.txt";

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
        idSubIDSetMap = new HashMap<String, String[]>();
        BufferedReader reader = null;
        try {
            InputStream inputStream = new FileInputStream(idSubIDSetMapPath);
            reader = new BufferedReader(new InputStreamReader(inputStream, "SJIS"));
            while (reader.ready()) {
                String line = reader.readLine();
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
        } catch (Exception e) {
            e.printStackTrace();
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

    public Set<List<String>> getURIPathToRootSet(String id) {
        Set<TreeNode> nodeSet = idNodeSetMap.get(id);
        Set<List<String>> pathToRootSet = new HashSet<List<String>>();
        if (nodeSet == null) { // 上位・下位関係が定義されていない（できない）概念
            pathToRootSet.add(Arrays.asList(new String[] { getURI(id)}));
            return pathToRootSet;
        }
        for (TreeNode node : nodeSet) {
            TreeNode[] pathToRoot = ((DefaultTreeModel) edrTreeModel).getPathToRoot(node);
            List<String> path = new ArrayList<String>();
            for (int i = 0; i < pathToRoot.length; i++) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) pathToRoot[i];
                String nid = (String) n.getUserObject();
                path.add(getURI(nid));
            }
            pathToRootSet.add(path);
        }
        return pathToRootSet;
    }

    public Set<List<Concept>> getConceptPathToRootSet(String id) {
        Concept c = getConcept(id);
        Set<TreeNode> nodeSet = idNodeSetMap.get(id);
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        if (nodeSet == null) { // 上位・下位関係が定義されていない（できない）概念
            pathToRootSet.add(Arrays.asList(new Concept[] { c}));
            return pathToRootSet;
        }
        for (TreeNode node : nodeSet) {
            TreeNode[] pathToRoot = ((DefaultTreeModel) edrTreeModel).getPathToRoot(node);
            List<Concept> path = new ArrayList<Concept>();
            for (int i = 0; i < pathToRoot.length; i++) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) pathToRoot[i];
                String nid = (String) n.getUserObject();
                path.add(getConcept(nid));
            }
            pathToRootSet.add(path);
        }
        return pathToRootSet;
    }

    private void getSubURI(TreeNode node, Set<String> subIDSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode childNode = node.getChildAt(i);
            // subConceptSet.add(childNode.toString());
            String id = childNode.toString();
            subIDSet.add(getURI(id));
            getSubURI(childNode, subIDSet);
        }
    }

    private void getSubIDSet(TreeNode node, Set<String> subIDSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            subIDSet.add(childNode.toString());
        }
    }

    public void getSubIDSet(String id, Set<String> nounIDSet, Set<String> refineSubIDSet) {
        Set<String> subIDSet = new HashSet<String>();
        Set<TreeNode> nodeSet = idNodeSetMap.get(id);
        for (TreeNode node : nodeSet) {
            getSubIDSet(node, subIDSet);
        }
        if (subIDSet.size() == 0) { return; }
        for (String subID : subIDSet) {
            if (nounIDSet.contains(subID)) {
                refineSubIDSet.add(subID);
            }
        }
        if (0 < refineSubIDSet.size()) { return; }
        for (String subID : subIDSet) {
            getSubIDSet(subID, nounIDSet, refineSubIDSet);
        }
    }

    public Set<Set<String>> getSubURISet(String id) {
        Set<TreeNode> nodeSet = idNodeSetMap.get(id);
        Set<Set<String>> subURIsSet = new HashSet<Set<String>>();
        if (nodeSet == null) { return subURIsSet; }
        for (TreeNode node : nodeSet) {
            Set<String> subURISet = new HashSet<String>();
            getSubURI(node, subURISet);
            subURIsSet.add(subURISet);
        }
        return subURIsSet;
    }

    private String getURI(String id) {
        if (isSpecial) { return DODDLEConstants.EDRT_URI + id; }
        return DODDLEConstants.EDR_URI + id;
    }

    private Concept getConcept(String id) {
        if (isSpecial) { return EDRDic.getEDRTConcept(id); }
        return EDRDic.getEDRConcept(id);
    }

    public Set<Set<String>> getSiblingURISet(String id) {
        Set<TreeNode> nodeSet = idNodeSetMap.get(id);
        Set<Set<String>> siblingIDsSet = new HashSet<Set<String>>();
        if (nodeSet == null) { return siblingIDsSet; }
        for (TreeNode node : nodeSet) {
            Set<String> siblingIDSet = new HashSet<String>();
            // System.out.println("NODE: " + node);
            TreeNode parentNode = node.getParent();
            // System.out.println("PARENT_NODE: " + parentNode);
            if (parentNode != null) {
                for (int i = 0; i < parentNode.getChildCount(); i++) {
                    TreeNode siblingNode = parentNode.getChildAt(i);
                    if (siblingNode != node) {
                        String sid = siblingNode.toString();
                        siblingIDSet.add(getURI(sid));
                    }
                }
            }
            siblingIDsSet.add(siblingIDSet);
        }
        return siblingIDsSet;
    }

    private void makeEDRTree(String id, DefaultMutableTreeNode node) {
        String[] subIDSet = idSubIDSetMap.get(id);
        for (int i = 1; i < subIDSet.length; i++) {
            String subID = subIDSet[i];
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subID);
            if (idNodeSetMap.get(subID) == null) {
                Set<TreeNode> nodeSet = new HashSet<TreeNode>();
                nodeSet.add(subNode);
                idNodeSetMap.put(subID, nodeSet);
            } else {
                Set<TreeNode> nodeSet = idNodeSetMap.get(subID);
                nodeSet.add(subNode);
            }
            if (idSubIDSetMap.get(subID) != null) {
                makeEDRTree(subID, subNode);
            }
            node.add(subNode);
        }
    }

    public static void main(String[] args) {
        DODDLEConstants.IS_USING_DB = false;
        EDRDic.initEDRDic();
        getEDRTree();
        Set<List<String>> pathSet = EDRTree.getEDRTTree().getURIPathToRootSet("ID3f543e");
        // Set<List<String>> pathSet =
        // EDRTree.getEDRTree().getPathToRootSet("ID3c1170");
        for (List<String> path : pathSet) {
            for (String c : path) {
                System.out.print(c + "\t");
            }
            System.out.println();
        }
    }
}
