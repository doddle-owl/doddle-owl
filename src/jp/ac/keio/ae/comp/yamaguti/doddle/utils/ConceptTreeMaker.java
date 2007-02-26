package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.util.*;

import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/*
 * @(#)  2004/08/20
 */

/**
 * @author takeshi morita
 */
public class ConceptTreeMaker {

    private List<List<ConceptTreeNode>> mraResultList;
    private SinNodeListSorter sinNodeListSorter;
    private List<ConceptTreeNode> traResultList;
    private Set<Concept> multipleInheritanceConceptSet;
    private Set<Concept> inputConceptSet;
    private static Set<Concept> trimmedConceptSet = new HashSet<Concept>();

    private static ConceptTreeMaker maker = new ConceptTreeMaker();
    public static String EDR_CLASS_ROOT_ID = "ID3aa966";
    public static String EDRT_CLASS_ROOT_ID = "ID2f3526";
    public static String DODDLE_CLASS_ROOT_URI = DODDLE.BASE_URI + "CLASS_ROOT";
    public static String DODDLE_PROPERTY_ROOT_URI = DODDLE.BASE_URI + "PROP_ROOT";

    private ConceptTreeMaker() {
        inputConceptSet = new HashSet<Concept>();
        mraResultList = new ArrayList<List<ConceptTreeNode>>();
        sinNodeListSorter = new SinNodeListSorter();
        traResultList = new ArrayList<ConceptTreeNode>();
        multipleInheritanceConceptSet = new HashSet<Concept>();
    }

    public static boolean isDODDLEClassRootURI(String uri) {
        return uri.equals(DODDLE_CLASS_ROOT_URI);
    }

    public static boolean isDODDLEPropertyRootURI(String uri) {
        return uri.equals(DODDLE_PROPERTY_ROOT_URI);
    }

    public void setInputConceptSet(Set<Concept> cSet) {
        inputConceptSet = cSet;
    }

    public void init() {
        inputConceptSet = new HashSet<Concept>();
        mraResultList.clear();
        traResultList.clear();
        beforeTrimmingConceptNum = 0;
        multipleInheritanceConceptSet.clear();
    }

    public Set<List<Concept>> getPathList(Set<Concept> conceptSet) {
        inputConceptSet = conceptSet;
        Set<List<Concept>> pathSet = new HashSet<List<Concept>>();
        for (Concept c : conceptSet) {
            Set<List<Concept>> tmpPathSet = OWLOntologyManager.getPathToRootSet(c.getURI());
            int pathSize = 0;
            for (List<Concept> pathToRoot: tmpPathSet) {
                if (pathSize < pathToRoot.size()) {
                    pathSize = pathToRoot.size();
                }
            }
            if (pathSize <= 1&& DODDLE.GENERAL_ONTOLOGY_NAMESPACE_SET.contains(c.getNameSpace())) {
                if (c.getNameSpace().equals(DODDLE.EDR_URI)) {
                    pathSet.addAll(EDRTree.getEDRTree().getPathToRootSet(c.getLocalName()));
                } else if (c.getNameSpace().equals(DODDLE.EDRT_URI)) {
                    pathSet.addAll(EDRTree.getEDRTTree().getPathToRootSet(c.getLocalName()));
                } else if (c.getNameSpace().equals(DODDLE.WN_URI)) {
                    pathSet.addAll(WordNetDic.getPathToRootSet(new Long(c.getLocalName())));
                }
            } else {
                pathSet.addAll(tmpPathSet);
            }
        }
        return pathSet;
    }

    public static ConceptTreeMaker getInstance() {
        return maker;
    }

    /**
     * 
     * 概念名(String)のリストを渡して,TreeModelを返す
     * 
     * @param conceptList
     *            概念(String)のリスト
     * @return TreeModel
     */
    public TreeModel getDefaultConceptTreeModel(Set pathSet, DODDLEProject project, String type) {
        Concept rootConcept = null;
        if (type.equals(ConceptTreeMaker.DODDLE_CLASS_ROOT_URI)) {
            rootConcept = new VerbConcept(ConceptTreeMaker.DODDLE_CLASS_ROOT_URI, "");
            rootConcept.addLabel(new DODDLELiteral("ja", "名詞的概念"));
            rootConcept.addLabel(new DODDLELiteral("en", "Root Class"));
        } else if (type.equals(ConceptTreeMaker.DODDLE_PROPERTY_ROOT_URI)) {
            rootConcept = new VerbConcept(ConceptTreeMaker.DODDLE_PROPERTY_ROOT_URI, "");
            rootConcept.addLabel(new DODDLELiteral("ja","動詞的概念"));
            rootConcept.addLabel(new DODDLELiteral("en","Root Property"));
        }
        ConceptTreeNode rootNode = new ConceptTreeNode(rootConcept, project);
        rootNode.setIsUserConcept(true);
        for (Iterator i = pathSet.iterator(); i.hasNext();) {
            List nodeList = (List) i.next();
            // nodeList -> [x, x, x, ..., 入力概念]
            addTreeNode(rootNode, nodeList, project);
        }
        return new DefaultTreeModel(rootNode);
    }

    public int getTrimmedConceptNum() {
        return trimmedConceptSet.size();
    }

    private int beforeTrimmingConceptNum;

    public int getBeforeTrimmingConceptNum() {
        return beforeTrimmingConceptNum;
    }

    /**
     * 
     * 概念名(String)のリストを渡して,TreeModelを返す 概念変動を行っている。
     * 
     * @param conceptList
     *            概念(String)のリスト
     * @return TreeModel
     */
    public TreeModel getTrimmedTreeModel(Set pathSet, DODDLEProject project, String type) {
        TreeModel treeModel = getDefaultConceptTreeModel(pathSet, project, type);
        beforeTrimmingConceptNum = Utils.getAllConcept(treeModel).size();
        ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
        trimmedConceptSet.clear();
        if (project.getDisambiguationPanel().getPerfectMatchedOptionPanel().isTrimming()) {
            trimTree(rootNode);
            removeSameNode(rootNode);
        }
        return treeModel;
    }

    /**
     * 不必要なノードかどうか
     */
    private boolean isUnnecessaryNode(ConceptTreeNode node) {
        return (node.getChildCount() == 1 && !isInputConcept(node.getConcept()));
    }

    /**
     * 不要な中間概念を剪定
     */
    private void trimTree(DefaultMutableTreeNode treeNode) {
        for (Enumeration i = treeNode.children(); i.hasMoreElements();) {
            ConceptTreeNode childNode = (ConceptTreeNode) i.nextElement();
            if (isUnnecessaryNode(childNode)) {
                // 子供が一つしかない場合にしか，トリミングは行われないため，getChildAt(0)でよい
                ConceptTreeNode grandChildNode = (ConceptTreeNode) childNode.getChildAt(0);
                trimmedConceptSet.add(childNode.getConcept()); // 削除された概念を保存
                // 剪定される概念が保持している剪定概念を保存
                grandChildNode.addAllTrimmedConcept(childNode.getTrimmedConceptList().get(0));
                grandChildNode.addTrimmedConcept(childNode.getConcept()); // 削除された概念を保存
                treeNode.add(grandChildNode);
                treeNode.remove(childNode);
                trimTree(treeNode);
            } else {
                trimTree(childNode);
            }
        }
    }

    /**
     * Trimmingしたことにより，同じレベルで同一の概念が含まれる場合があるため， それらを除去する．
     * 
     * @param treeNode
     */
    private void removeSameNode(ConceptTreeNode treeNode) {
        Map<Concept, List<ConceptTreeNode>> sameNodeMap = new HashMap<Concept, List<ConceptTreeNode>>();
        for (Enumeration i = treeNode.children(); i.hasMoreElements();) {
            ConceptTreeNode childNode = (ConceptTreeNode) i.nextElement();
            List<ConceptTreeNode> sameNodeList = sameNodeMap.get(childNode.getConcept());
            if (sameNodeList == null) {
                sameNodeList = new ArrayList<ConceptTreeNode>();
            }
            sameNodeList.add(childNode);
            sameNodeMap.put(childNode.getConcept(), sameNodeList);
            if (0 < childNode.getChildCount()) {
                removeSameNode(childNode);
            }
        }
        for (Iterator i = sameNodeMap.values().iterator(); i.hasNext();) {
            List<ConceptTreeNode> sameNodeList = (List<ConceptTreeNode>) i.next();
            if (1 < sameNodeList.size()) {
                ConceptTreeNode baseNode = sameNodeList.get(0);
                for (int j = 1; j < sameNodeList.size(); j++) {
                    ConceptTreeNode removeNode = sameNodeList.get(j);
                    baseNode.addTrimmedConceptList(removeNode.getTrimmedConceptList().get(0));
                }
                // System.out.println("samenodesize: " + sameNodeList.size());
                for (int j = 1; j < sameNodeList.size(); j++) {
                    // System.out.println("j: " + j);
                    treeNode.remove(sameNodeList.get(j));
                }
            }
        }
    }

    /**
     * 概念木のノードと概念Xを含む木をあらわすリストを渡して, リストを概念木の中の適切な場所に追加する
     * 
     * nodeList -> a > b > c > X
     */
    private boolean addTreeNode(ConceptTreeNode treeNode, List nodeList, DODDLEProject project) {
        if (nodeList.isEmpty()) { return false; }
        if (treeNode.isLeaf()) { return insertTreeNodeList(treeNode, nodeList, project); }
        for (Enumeration i = treeNode.children(); i.hasMoreElements();) {
            ConceptTreeNode node = (ConceptTreeNode) i.nextElement();
            Concept firstNode = (Concept) nodeList.get(0);
            if (node.getConcept().getURI().equals(firstNode.getURI())) { return addTreeNode(node, nodeList.subList(1,
                    nodeList.size()), project); }
        }
        return insertTreeNodeList(treeNode, nodeList, project);
    }

    /**
     * 概念Xを含む木をあらわすリストを概念木に挿入する
     */
    private boolean insertTreeNodeList(DefaultMutableTreeNode treeNode, List nodeList, DODDLEProject project) {
        if (nodeList.isEmpty()) { return false; }
        Concept firstNode = (Concept) nodeList.get(0);
        if (firstNode != null) {
            boolean isInputConcept = isInputConcept(firstNode);
            VerbConcept c = new VerbConcept(firstNode);
            project.getDisambiguationPanel().removeRefOntConceptLabel(c, isInputConcept);
            ConceptTreeNode childNode = new ConceptTreeNode(c, project);
            childNode.setIsInputConcept(isInputConcept);
            treeNode.add(childNode);
            insertTreeNodeList(childNode, nodeList.subList(1, nodeList.size()), project);
        }
        return true;
    }

    /**
     * 概念変動を行うために必要な情報を分析し，セットする
     */
    public void conceptDriftManagement(TreeModel model) {
        mraResultList.clear();
        traResultList.clear();
        ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
        matchedResultAnalysis(rootNode);
        trimmedResultAnalysis(rootNode, 3);
        multipleInheritanceConceptSet.clear();
        getMultipleInheritanceConceptSet(rootNode);
    }

    public void resetTRA() {
        traResultList.clear();
    }

    public void resetMRA() {
        mraResultList.clear();
    }
    
    /**
     * 
     * TRA(Trimmed Result Analysis)を行う．
     * 
     */
    public void trimmedResultAnalysis(ConceptTreeNode node, int trimmedCnt) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            List<Integer> trimmedCount = childNode.getTrimmedCountList();
            for (int cnt : trimmedCount) {
                if (trimmedCnt <= cnt) {
                    traResultList.add(childNode);
                    break;
                }
            }
            trimmedResultAnalysis(childNode, trimmedCnt);
        }
    }

    private boolean isSameSinNode(ConceptTreeNode sinNode, ConceptTreeNode lastSinNode) {
        return lastSinNode != null && lastSinNode.toString().equals(sinNode.toString());
    }

    /**
     * 
     * MRA(Matched Result Analysis)を行う．
     * 
     */
    public void matchedResultAnalysis(ConceptTreeNode rootNode) {
        List<ConceptTreeNode> sinNodeList = new ArrayList<ConceptTreeNode>();
        getSINNodeSet(rootNode, sinNodeList);
        Collections.sort(sinNodeList, sinNodeListSorter);
        ConceptTreeNode lastSinNode = null;
        for (ConceptTreeNode sinNode : sinNodeList) {
            List<ConceptTreeNode> stm = new ArrayList<ConceptTreeNode>();
            stm.add(sinNode);
            getSubTreesManuallyMoved(sinNode, stm);
            if (isSameSinNode(sinNode, lastSinNode)) {
                mraResultList.get(mraResultList.size() - 1).addAll(stm);
            } else {
                mraResultList.add(stm);
            }
            lastSinNode = sinNode;
        }
    }

    /**
     * 
     * SINノードをソート
     * 
     * @author takeshi morita
     * 
     */
    class SinNodeListSorter implements Comparator {
        public int compare(Object o1, Object o2) {
            ConceptTreeNode n1 = (ConceptTreeNode) o1;
            ConceptTreeNode n2 = (ConceptTreeNode) o2;
            return n1.toString().compareTo(n2.toString());
        }
    }

    /**
     * 
     * STM(Sub Trees manually Moved)を得る． MRA(Matched Result Analysis)で用いる．
     * 
     * @param node
     * @param stm
     */
    private void getSubTreesManuallyMoved(ConceptTreeNode node, List<ConceptTreeNode> stm) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.isInputConcept()) {
                stm.add(childNode);
                getSubTreesManuallyMoved(childNode, stm);
            }
        }
    }

    /**
     * 
     * SIN(a Salient Internal Nodes)の集合を得る．
     * 
     * @param node
     * @param sinNodeSet
     */
    private void getSINNodeSet(ConceptTreeNode node, List<ConceptTreeNode> sinNodeSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.isSINNode()) {
                sinNodeSet.add(childNode);
            }
            getSINNodeSet(childNode, sinNodeSet);
        }
    }

    public Set<Concept> getMulipleInheritanceConceptSet() {
        return multipleInheritanceConceptSet;
    }
    
    public Set<Concept> getMulipleInheritanceConceptSet(ConceptTreeNode node) {
        multipleInheritanceConceptSet.clear();
        getMultipleInheritanceConceptSet(node);
        return multipleInheritanceConceptSet;
    }

    /**
     * 
     * 多重継承概念の集合を得る．
     * 
     * @param node
     * @param sinNodeSet
     */
    private void getMultipleInheritanceConceptSet(ConceptTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.isMultipleInheritance()) {
                multipleInheritanceConceptSet.add(childNode.getConcept());
            }
            getMultipleInheritanceConceptSet(childNode);
        }
    }

    public List<List<ConceptTreeNode>> getMRAresult() {
        return mraResultList;
    }

    public List<ConceptTreeNode> getTRAresult() {
        return traResultList;
    }

    public boolean isInputConcept(Concept c) {
        if (inputConceptSet == null) { return false; }
        for (Concept ic : inputConceptSet) {
            if (ic == null) { return false; }
            if (ic.getURI().equals(c.getURI())) { return true; }
        }
        return false;
    }

    private static Map supSubSetMap;

    public TreeNode getConceptTreeRoot(DODDLEProject currentProject, Model model, Resource rootResource) {
        supSubSetMap = new HashMap();
        for (ResIterator i = model.listSubjectsWithProperty(RDF.type); i.hasNext();) {
            Resource resource = i.nextResource();
            for (StmtIterator j = resource.listProperties(RDFS.subClassOf); j.hasNext();) {
                Statement stmt = j.nextStatement();
                Resource supResource = (Resource) stmt.getObject();
                Set subResourceSet = (Set) supSubSetMap.get(supResource);
                if (subResourceSet == null) {
                    subResourceSet = new HashSet();
                }
                subResourceSet.add(resource);
                if (subResourceSet.contains(supResource)) {
                    subResourceSet.remove(supResource);
                }
                supSubSetMap.put(supResource, subResourceSet);
            }
        }
        Concept rootEDRConcept = new VerbConcept(DODDLE_CLASS_ROOT_URI, "");
        rootEDRConcept.addLabel(new DODDLELiteral("ja", "名詞的概念"));
        rootEDRConcept.addLabel(new DODDLELiteral("en", "Root Class"));
        ConceptTreeNode rootNode = new ConceptTreeNode(rootEDRConcept, currentProject);
        rootNode.setIsUserConcept(true);
        if (supSubSetMap.get(rootResource) != null) {
            makeTree(currentProject, rootResource, rootNode);
        } else { // 前のバージョンとの互換性を保持するため
            rootResource = ResourceFactory.createResource(DODDLE.EDR_URI + EDR_CLASS_ROOT_ID);
            if (supSubSetMap.get(rootResource) != null) {
                makeTree(currentProject, rootResource, rootNode);
            }
            rootResource = ResourceFactory.createResource(DODDLE.OLD_EDR_URI + EDR_CLASS_ROOT_ID);
            if (supSubSetMap.get(rootResource) != null) {
                makeTree(currentProject, rootResource, rootNode);
            }
        }
        return rootNode;
    }

    public TreeNode getPropertyTreeRoot(DODDLEProject currentProject, Model model, Resource rootResource) {
        supSubSetMap = new HashMap();
        for (ResIterator i = model.listSubjectsWithProperty(RDF.type); i.hasNext();) {
            Resource resource = i.nextResource();
            for (StmtIterator j = resource.listProperties(RDFS.subPropertyOf); j.hasNext();) {
                Statement stmt = j.nextStatement();
                Resource supResource = (Resource) stmt.getObject();
                Set subResourceSet = (Set) supSubSetMap.get(supResource);
                if (subResourceSet == null) {
                    subResourceSet = new HashSet();
                }
                subResourceSet.add(resource);
                supSubSetMap.put(supResource, subResourceSet);
            }
        }
        // System.out.println(supSubSetMap);
        VerbConcept propRoot = new VerbConcept(DODDLE_PROPERTY_ROOT_URI, "");
        propRoot.addLabel(new DODDLELiteral("ja", "動詞的概念"));
        propRoot.addLabel(new DODDLELiteral("en", "Root Property"));
        ConceptTreeNode rootNode = new ConceptTreeNode(propRoot, currentProject);
        rootNode.setIsUserConcept(true);
        if (supSubSetMap.get(rootResource) != null) {
            makeTree(currentProject, rootResource, rootNode);
        } else { // 前のバージョンとの互換性を保持するため
            rootResource = ResourceFactory.createResource(DODDLE.OLD_EDR_URI + "ID" + DODDLE_PROPERTY_ROOT_URI);
            if (supSubSetMap.get(rootResource) != null) {
                makeTree(currentProject, rootResource, rootNode);
            }
        }
        return rootNode;
    }

    private void makeTree(DODDLEProject currentProject, Resource resource, DefaultMutableTreeNode node) {
        Set subRDFSSet = (Set) supSubSetMap.get(resource);
        for (Iterator i = subRDFSSet.iterator(); i.hasNext();) {
            Resource subRDFS = (Resource) i.next();
            VerbConcept concept = new VerbConcept(subRDFS.getURI(), "");
            Statement prefLabelStmt = subRDFS.getProperty(JenaModelMaker.SKOS_PREFLABEL);
            if (prefLabelStmt != null) { // prefLabelが設定されていない場合
                Literal prefLabel = (Literal)prefLabelStmt.getObject();
                concept.setInputLabel(new DODDLELiteral(prefLabel.getLanguage(), prefLabel.getString()));
            }
            for (StmtIterator stmtIter = subRDFS.listProperties(RDFS.label); stmtIter.hasNext();) {
                Statement stmt = stmtIter.nextStatement();
                Literal lit = (Literal) stmt.getObject();
                concept.addLabel(new DODDLELiteral(lit.getLanguage(), lit.getString()));
            }
            for (StmtIterator stmtIter = subRDFS.listProperties(RDFS.comment); stmtIter.hasNext();) {
                Statement stmt = stmtIter.nextStatement();
                Literal lit = (Literal) stmt.getObject();
                concept.addDescription(new DODDLELiteral(lit.getLanguage(), lit.getString()));
            }

            Set domainSet = new HashSet();
            for (StmtIterator stmtIter = subRDFS.listProperties(RDFS.domain); stmtIter.hasNext();) {
                Statement stmt = stmtIter.nextStatement();
                Resource domain = (Resource) stmt.getObject();
                domainSet.add(domain.getURI());
            }
            concept.addAllDomain(domainSet);

            Set rangeSet = new HashSet();
            for (StmtIterator stmtIter = subRDFS.listProperties(RDFS.range); stmtIter.hasNext();) {
                Statement stmt = stmtIter.nextStatement();
                Resource range = (Resource) stmt.getObject();
                rangeSet.add(range.getURI());
            }
            concept.addAllRange(rangeSet);

            boolean isInputConcept = concept != null && isInputConcept(concept);
            ConceptTreeNode subNode = new ConceptTreeNode(concept, currentProject);
            subNode.setIsInputConcept(isInputConcept);
            if (subRDFS.getLocalName().indexOf("UID") != -1) {
                currentProject.setUserIDCount(Integer.parseInt(subRDFS.getLocalName().split("UID")[1]));
                subNode.setIsUserConcept(true);
            }
            if (supSubSetMap.get(subRDFS) != null) {
                makeTree(currentProject, subRDFS, subNode);
            }
            node.add(subNode);
        }
    }
}