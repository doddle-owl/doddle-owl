package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import org.apache.log4j.*;

/*
 * @(#)  2005/07/17
 *
 */

/**
 * @author takeshi morita
 */
public class ConceptTreePanel extends JPanel {
    private DODDLEProject project;

    private JTextField searchConceptField;
    private TitledBorder searchConceptFieldBorder;
    private JButton searchButton;
    private JButton searchPreviousButton;
    private JButton searchNextButton;

    private JTree conceptTree;
    private ConceptTreeNode targetConceptTreeNode;

    private AddConceptAction addConceptAction;
    private CopyConceptAction copyConceptAction;
    private CloneConceptAction cloneConceptAction;
    private CutConceptAction cutConceptAction;
    private PasteConceptAction pasteConceptAction;
    private DeleteLinkToUpperConceptAction deleteLinkToUpperConceptAction;
    private DeleteConceptAction deleteConceptAction;
    private AddUndefinedWordListAction addUndefinedWordListAction;
    private MoveUndefinedWordListAction moveUndefinedWordListAction;

    private ImageIcon addConceptIcon = Utils.getImageIcon("add_concept.png");
    private ImageIcon cloneConceptIcon = Utils.getImageIcon("clone_concept.png");
    private ImageIcon expandTreeIcon = Utils.getImageIcon("expand_tree.png");
    private ImageIcon addUndefWordIcon = Utils.getImageIcon("add_undef_word.png");
    private ImageIcon undefIcon = Utils.getImageIcon("undef.png");

    private ImageIcon copyIcon = Utils.getImageIcon("copy.gif");
    private ImageIcon cutIcon = Utils.getImageIcon("cut.gif");
    private ImageIcon pasteIcon = Utils.getImageIcon("paste.gif");
    private ImageIcon deleteIcon = Utils.getImageIcon("delete.gif");

    private UndefinedWordListPanel undefinedWordListPanel;

    private Map<String, Concept> idConceptMap;
    private Map conceptSameConceptMap;
    private Map<String, Concept> complexWordConceptMap; // ï°çáåÍÇ∆ëŒâûÇ∑ÇÈäTîOÇÃÉ}ÉbÉsÉìÉO

    public ConceptTreePanel(String title, UndefinedWordListPanel panel, DODDLEProject p) {
        project = p;
        undefinedWordListPanel = panel;
        idConceptMap = new HashMap<String, Concept>();
        conceptSameConceptMap = new HashMap();
        complexWordConceptMap = new HashMap<String, Concept>();
        Action searchAction = new SearchAction();
        searchConceptField = new JTextField(15);
        searchConceptField.addActionListener(searchAction);
        searchConceptFieldBorder = BorderFactory.createTitledBorder("äTîOåüçı (0/0)");
        searchConceptField.setBorder(searchConceptFieldBorder);
        searchButton = new JButton("åüçı");
        searchButton.addActionListener(searchAction);
        searchPreviousButton = new JButton("ëO");
        searchPreviousButton.addActionListener(searchAction);
        searchNextButton = new JButton("éü");
        searchNextButton.addActionListener(searchAction);
        JPanel searchDirectionPanel = new JPanel();
        searchDirectionPanel.setLayout(new GridLayout(1, 2));
        searchDirectionPanel.add(searchPreviousButton);
        searchDirectionPanel.add(searchNextButton);
        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.setLayout(new GridLayout(2, 1));
        searchButtonPanel.add(searchButton);
        searchButtonPanel.add(searchDirectionPanel);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(searchConceptField, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);

        addConceptAction = new AddConceptAction("äTîOÇÃí«â¡");
        copyConceptAction = new CopyConceptAction("äTîOÇÃÉRÉsÅ[");
        cloneConceptAction = new CloneConceptAction("äTîOÇÃï°êª");
        cutConceptAction = new CutConceptAction("äTîOÇÃêÿÇËéÊÇË");
        pasteConceptAction = new PasteConceptAction("äTîOÇÃì\ÇËïtÇØ");
        deleteLinkToUpperConceptAction = new DeleteLinkToUpperConceptAction("è„à äTîOÇ÷ÇÃÉäÉìÉNÇçÌèú");
        deleteConceptAction = new DeleteConceptAction("äTîOÇÃçÌèú");
        addUndefinedWordListAction = new AddUndefinedWordListAction("ñ¢íËã`ÇÃíPåÍÇí«â¡");
        moveUndefinedWordListAction = new MoveUndefinedWordListAction("ñ¢íËã`");

        conceptTree = new JTree();
        conceptTree.addMouseListener(new ConceptTreeMouseAdapter());
        conceptTree.setEditable(false);
        conceptTree.setDragEnabled(true);
        new DropTarget(conceptTree, new ConceptTreeDropTargetAdapter());
        conceptTree.setScrollsOnExpand(true);

        JScrollPane conceptTreeScroll = new JScrollPane(conceptTree);
        conceptTreeScroll.setPreferredSize(new Dimension(250, 100));
        conceptTreeScroll.setBorder(BorderFactory.createTitledBorder(title));

        JToolBar toolBar = new JToolBar();
        toolBar.add(addConceptAction).setToolTipText(addConceptAction.getTitle());
        toolBar.add(cloneConceptAction).setToolTipText(cloneConceptAction.getTitle());
        toolBar.add(copyConceptAction).setToolTipText(copyConceptAction.getTitle());
        toolBar.add(cutConceptAction).setToolTipText(cutConceptAction.getTitle());
        toolBar.add(pasteConceptAction).setToolTipText(pasteConceptAction.getTitle());
        toolBar.add(deleteLinkToUpperConceptAction).setToolTipText(deleteLinkToUpperConceptAction.getTitle());
        toolBar.add(deleteConceptAction).setToolTipText(deleteConceptAction.getTitle());
        toolBar.add(addUndefinedWordListAction).setToolTipText(addUndefinedWordListAction.getTitle());
        toolBar.add(moveUndefinedWordListAction).setToolTipText(moveUndefinedWordListAction.getTitle());
        toolBar.add(new ExpandAllPathAction("äTîOäKëwÇÃìWäJ")).setToolTipText("äTîOäKëwÇÃìWäJ");

        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());
        treePanel.add(toolBar, BorderLayout.NORTH);
        treePanel.add(conceptTreeScroll, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(treePanel, BorderLayout.CENTER);
    }

    class ConceptTreeMouseAdapter extends MouseAdapter {

        private JPopupMenu popupMenu;

        ConceptTreeMouseAdapter() {
            popupMenu = new JPopupMenu();
            popupMenu.add(addConceptAction);
            popupMenu.add(cloneConceptAction);
            popupMenu.add(copyConceptAction);
            popupMenu.add(cutConceptAction);
            popupMenu.add(pasteConceptAction);
            popupMenu.add(deleteLinkToUpperConceptAction);
            popupMenu.add(deleteConceptAction);
            popupMenu.add(addUndefinedWordListAction);
            popupMenu.add(moveUndefinedWordListAction);
            popupMenu.add(new ExpandSelectedPathAction("äTîOäKëwÇÃìWäJ"));
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (!(conceptTree.getSelectionCount() == 1)) { return; }
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public JTree getConceptTree() {
        return conceptTree;
    }

    public boolean isConceptContains(Concept c) {
        ConceptTreeNode rootNode = (ConceptTreeNode) conceptTree.getModel().getRoot();
        return isConceptContains(c, rootNode);
    }

    private boolean isConceptContains(Concept c, ConceptTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (c == childNode.getConcept()) { return true; }
            return isConceptContains(c, childNode);
        }
        return false;
    }

    class SearchAction extends AbstractAction {

        private int index;
        private List searchNodeList;
        private String searchKeyWord;

        public SearchAction() {
            index = 0;
            searchKeyWord = "";
            searchNodeList = new ArrayList();
        }

        private boolean isSearchConcept(Concept c) {
            if (c.getJaWord().indexOf(searchKeyWord) != -1) { return true; }
            if (c.getEnWord().indexOf(searchKeyWord) != -1) { return true; }
            return false;
        }

        private void searchConcept(ConceptTreeNode node) {
            for (int i = 0; i < node.getChildCount(); i++) {
                ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
                if (isSearchConcept(childNode.getConcept())) {
                    searchNodeList.add(childNode);
                }
                searchConcept(childNode);
            }
        }

        private void setSearchFieldTitle() {
            if (searchNodeList.size() == 0) {
                searchConceptFieldBorder.setTitle("äTîOåüçı (0/0)");
            } else {
                searchConceptFieldBorder.setTitle("äTîOåüçı (" + (index + 1) + "/" + searchNodeList.size() + ")");
            }
            searchConceptField.repaint();
        }

        private void selectSearchNode() {
            setSearchFieldTitle();
            ConceptTreeNode node = (ConceptTreeNode) searchNodeList.get(index);
            DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
            TreeNode[] nodes = model.getPathToRoot(node);
            TreePath path = new TreePath(nodes);
            conceptTree.scrollPathToVisible(path);
            conceptTree.setSelectionPath(path);
        }

        private void searchPrevious() {
            if (searchNodeList.size() == 0) { return; }
            if (0 <= index - 1 && index - 1 < searchNodeList.size()) {
                index--;
            } else {
                index = searchNodeList.size() - 1;
            }
            selectSearchNode();
        }

        private void searchNext() {
            if (searchNodeList.size() == 0) { return; }
            if (0 <= index + 1 && index + 1 < searchNodeList.size()) {
                index++;
            } else {
                index = 0;
            }
            selectSearchNode();
        }

        private void search() {
            index = 0;
            searchNodeList.clear();
            searchKeyWord = searchConceptField.getText();
            DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
            if (!(model.getRoot() instanceof ConceptTreeNode)) { return; }
            ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
            searchConcept(rootNode);
            setSearchFieldTitle();
            if (searchNodeList.size() == 0) { return; }
            selectSearchNode();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == searchConceptField && searchKeyWord.equals(searchConceptField.getText())
                    && 0 < searchNodeList.size()) {
                // ÉLÅ[ÉèÅ[ÉhÇ™ïœâªÇµÇƒÇ®ÇÁÇ∏ÅCåüçıåãâ Ç™ÇPà»è„Ç≈ÅCåüçıÉtÉBÅ[ÉãÉhÇ≈ÉGÉìÉ^Å[É{É^ÉìÇâüÇµÇΩèÍçá
                searchNext();
            } else if (e.getSource() == searchButton || e.getSource() == searchConceptField) {
                search();
            } else if (e.getSource() == searchPreviousButton) {
                searchPrevious();
            } else if (e.getSource() == searchNextButton) {
                searchNext();
            }
        }
    }

    class ConceptTreeDropTargetAdapter extends DropTargetAdapter {

        private TreePath[] dragPaths;

        public void dragEnter(DropTargetDragEvent dtde) {
            dragPaths = conceptTree.getSelectionPaths();
        }

        private ConceptTreeNode getSelectedNode(Point p) {
            TreePath path = conceptTree.getPathForLocation(p.x, p.y);
            if (path == null || path.getLastPathComponent() == null) { return null; }
            return (ConceptTreeNode) path.getLastPathComponent();
        }

        public void dragOver(DropTargetDragEvent dtde) {
            Point point = dtde.getLocation();
            ConceptTreeNode node = getSelectedNode(point);
            if (node != null) {
                int row = conceptTree.getRowForLocation(point.x, point.y);
                conceptTree.setSelectionRow(row);
            }
        }

        public void drop(DropTargetDropEvent dtde) {
            Point dropPoint = dtde.getLocation();
            ConceptTreeNode dropNode = getSelectedNode(dropPoint);

            if (dropNode == null || dragPaths == null) {
                dragPaths = null;
                return;
            } else if (UndefinedWordListPanel.isDragUndefinedList) {
                Object[] selectedValues = undefinedWordListPanel.getSelectedValues();
                DefaultListModel listModel = undefinedWordListPanel.getModel();
                DefaultListModel viewListModel = undefinedWordListPanel.getViewModel();
                for (int i = 0; i < selectedValues.length; i++) {
                    ConceptTreeNode parent = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
                    insertNewConceptTreeNode(selectedValues[i].toString(), parent.getConcept());
                    listModel.removeElement(selectedValues[i]);
                    if (viewListModel != null) {
                        viewListModel.removeElement(selectedValues[i]);
                    }
                }
                undefinedWordListPanel.setTitleWithSize();
                UndefinedWordListPanel.isDragUndefinedList = false;
            } else {
                DefaultTreeModel treeModel = (DefaultTreeModel) conceptTree.getModel();
                for (int i = 0; i < dragPaths.length; i++) {
                    DefaultMutableTreeNode movedNode = (DefaultMutableTreeNode) dragPaths[i].getLastPathComponent();
                    if (movedNode == dropNode) {
                        continue;
                    }
                    if (movedNode.getParent() != null) {
                        treeModel.removeNodeFromParent(movedNode);
                    }
                    treeModel.insertNodeInto(movedNode, dropNode, 0);
                    checkMultipleInheritanceNode(((ConceptTreeNode) movedNode).getConcept());
                }
            }
            dragPaths = null;
        }
    }

    private boolean isSameConcept(Concept concept, ConceptTreeNode node, Set sameConceptSet) {
        return concept.getId().equals(node.getConcept().getId());
    }

    public void searchSameConceptTreeNode(Concept concept, ConceptTreeNode node, Set sameConceptSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (isSameConcept(concept, childNode, sameConceptSet)) {
                sameConceptSet.add(childNode);
            }
            searchSameConceptTreeNode(concept, childNode, sameConceptSet);
        }
    }

    private boolean hasMultipleParent(Set sameConceptTreeNodeSet) {
        ConceptTreeNode parentTreeNode = null;
        for (Iterator i = sameConceptTreeNodeSet.iterator(); i.hasNext();) {
            ConceptTreeNode node = (ConceptTreeNode) i.next();
            if (parentTreeNode == null) {
                parentTreeNode = (ConceptTreeNode) node.getParent();
            } else {
                if (parentTreeNode.getId().equals(((ConceptTreeNode) node.getParent()).getId())) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    private void makeIDParentIDSetMap(Map idParentIDSetMap, ConceptTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (idParentIDSetMap.get(childNode.getId()) != null) {
                Set parentIDSet = (Set) idParentIDSetMap.get(childNode.getId());
                parentIDSet.add(node.getId());
                idParentIDSetMap.put(childNode.getId(), parentIDSet);
            } else {
                Set parentIDSet = new HashSet();
                parentIDSet.add(node.getId());
                idParentIDSetMap.put(childNode.getId(), parentIDSet);
            }
            makeIDParentIDSetMap(idParentIDSetMap, childNode);
        }
    }

    private void checkAllMultipleInheritanceNode(Map idParentSetMap, ConceptTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            Set parentIDSet = (Set) idParentSetMap.get(childNode.getId());
            if (1 < parentIDSet.size()) {
                childNode.setIsMultipleInheritance(true);
            }
            checkAllMultipleInheritanceNode(idParentSetMap, childNode);
        }
    }

    public void checkAllMultipleInheritanceNode(TreeModel treeModel) {
        Map idParentIDSetMap = new HashMap();
        ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
        makeIDParentIDSetMap(idParentIDSetMap, rootNode);
        checkAllMultipleInheritanceNode(idParentIDSetMap, rootNode);
    }

    public void checkMultipleInheritanceNode(Concept c) {
        ConceptTreeNode rootNode = (ConceptTreeNode) conceptTree.getModel().getRoot();
        Set sameConceptTreeNodeSet = new HashSet();
        searchSameConceptTreeNode(c, rootNode, sameConceptTreeNodeSet);
        if (hasMultipleParent(sameConceptTreeNodeSet)) {
            for (Iterator j = sameConceptTreeNodeSet.iterator(); j.hasNext();) {
                ConceptTreeNode node = (ConceptTreeNode) j.next();
                node.setIsMultipleInheritance(true);
            }
        } else {
            for (Iterator j = sameConceptTreeNodeSet.iterator(); j.hasNext();) {
                ConceptTreeNode node = (ConceptTreeNode) j.next();
                node.setIsMultipleInheritance(false);
            }
        }
    }

    public Set getAllConcept() {
        Set conceptSet = new HashSet();
        TreeModel treeModel = conceptTree.getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
        conceptSet.add(rootNode.getConcept());
        getAllConcept(rootNode, conceptSet);
        return conceptSet;
    }

    public void getAllConcept(TreeNode node, Set conceptSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            conceptSet.add(childNode.getConcept());
            getAllConcept(childNode, conceptSet);
        }
    }

    public Set getAllConceptID() {
        Set conceptSet = new HashSet();
        TreeModel treeModel = conceptTree.getModel();
        if (treeModel.getRoot() instanceof ConceptTreeNode) {
            ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
            conceptSet.add(rootNode.getConcept().getId());
            getAllConceptID(rootNode, conceptSet);
        }
        return conceptSet;
    }

    public void getAllConceptID(TreeNode node, Set conceptSet) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            conceptSet.add(childNode.getConcept().getId());
            getAllConceptID(childNode, conceptSet);
        }
    }

    /**
     * 
     */
    private void deleteConcept() {
        if (conceptTree.getSelectionCount() == 1) {
            DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
            ConceptTreeNode deleteNode = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
            ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
            Set sameConceptSet = new HashSet();
            searchSameConceptTreeNode(deleteNode.getConcept(), rootNode, sameConceptSet);
            for (Iterator i = sameConceptSet.iterator(); i.hasNext();) {
                deleteNode = (ConceptTreeNode) i.next();
                if (deleteNode.getParent() != null) {
                    model.removeNodeFromParent(deleteNode);
                }
            }
        }
    }

    private Concept searchedConcept; // óÃàÊÉIÉìÉgÉçÉWÅ[ÇÃÉmÅ[ÉhÇ…ëŒâûÇ√ÇØÇÁÇÍÇΩConcept
    private Set<Concept> supConceptSet; // è„à äTîOÇÃÉZÉbÉgÇï€ë∂

    public Set getSupConceptSet(String id) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        supConceptSet = new HashSet<Concept>();
        if (model.getRoot() instanceof ConceptTreeNode) {
            ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
            searchSupConcept(id, rootNode);
        }
        return supConceptSet;
    }

    /**
     * à¯êîÇ≈ó^Ç¶ÇΩidÇÃè„à äTîOÇåüçıÇ∑ÇÈ. ÅiEDRëSëÃÇ…íËã`Ç≥ÇÍÇƒÇ¢ÇÈConceptÇ≈ÇÕÇ»Ç¢)
     */
    private void searchSupConcept(String id, ConceptTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.getConcept() != null && childNode.getConcept().getId().equals(id)) {
                supConceptSet.add(node.getConcept());
            }
            searchSupConcept(id, childNode);
        }
    }

    /**
     * ÉmÅ[ÉhÇ…ëŒâûÇ√ÇØÇÁÇÍÇΩäTîOÇåüçıÇ∑ÇÈÅDÅiEDRëSëÃÇ…íËã`Ç≥ÇÍÇƒÇ¢ÇÈConceptÇ≈ÇÕÇ»Ç¢)
     */
    private void searchConcept(String identity, ConceptTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.getConcept().getIdentity().equals(identity)) {
                searchedConcept = childNode.getConcept();
            }
            searchConcept(identity, childNode);
        }
    }

    public void addJPWord(String identity, String word) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
        searchedConcept = null;
        searchConcept(identity, rootNode);
        if (searchedConcept != null) {
            searchedConcept.setJaWord(searchedConcept.getJaWord() + "\t" + word);
            searchedConcept.setInputWord(word);
        }
    }

    public void addSubConcept(String identity, String word) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
        searchedConcept = null;
        searchConcept(identity, rootNode);
        if (searchedConcept != null) {
            insertNewConceptTreeNode(word, searchedConcept);
        }
    }

    private Concept insertComplexWordConceptTreeNode(String identity, String newWord,
            ConceptTreeNode conceptTreeRootNode) {
        searchedConcept = null;
        if (idConceptMap.get(identity) == null) {
            searchConcept(identity, conceptTreeRootNode);
            idConceptMap.put(identity, searchedConcept);
        } else {
            searchedConcept = idConceptMap.get(identity);
        }
        if (searchedConcept != null) {
            if (complexWordConceptMap.get(newWord) != null) {
                Concept c = complexWordConceptMap.get(newWord);
                insertConceptTreeNode(c, searchedConcept, false, true);
                return c;
            }
            Concept c = insertNewConceptTreeNode(newWord, searchedConcept);
            complexWordConceptMap.put(newWord, c);
            return c;
        }
        return null;
    }

    private String getChildWordWithoutTopWord(DefaultMutableTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (childNode.getUserObject() instanceof String) {
                String complexWord = (String) childNode.getUserObject();
                InputWordModel iwModel = project.getInputModuleUI().getInputModule().makeInputWordModel(complexWord);
                return iwModel.getWordWithoutTopWord();
            }
        }
        return null;
    }

    public void addComplexWordConcept(String identity, TreeNode node, ConceptTreeNode conceptTreeRootNode) {
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            String newWord = "";
            if (childNode.getUserObject() instanceof Concept) {
                Concept c = (Concept) childNode.getUserObject();
                searchedConcept = null;
                searchConcept(identity, conceptTreeRootNode);
                String w = project.getInputWordSelectionPanel().getAbstractNodeLabelMap().get(childNode);
                if (w == null) {
                    newWord = identity + "#" + c.getWord() + getChildWordWithoutTopWord(childNode);
                    // System.out.println("null => " + newWord);
                } else {
                    newWord = identity + "#" + w;
                }
                abstractNodeNum++;
                DODDLE.getLogger().log(Level.DEBUG,
                        "[" + abstractNodeNum + "] íäè€íÜä‘ÉmÅ[Éh(åZíÌêî)ÅF" + newWord + "(" + childNode.getChildCount() + ")");
            } else {
                newWord = childNode.toString();
            }
            Concept childConcept = insertComplexWordConceptTreeNode(identity, newWord, conceptTreeRootNode);
            if (childConcept != null) {
                addComplexWordConcept(childConcept.getIdentity(), childNode, conceptTreeRootNode);
            }
        }
    }

    public Map<String, Concept> getComplexWordConceptMap() {
        return complexWordConceptMap;
    }

    private int abstractNodeNum;

    public void addComplexWordConcept(Map matchedWordIDMap, TreeNode node, ConceptTreeNode conceptTreeRootNode) {
        idConceptMap.clear();
        conceptSameConceptMap.clear();
        complexWordConceptMap.clear();
        abstractNodeNum = 0;
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode childNode = node.getChildAt(i);
            String identity = childNode.toString();
            addComplexWordConcept(identity, childNode, conceptTreeRootNode);
        }
        DODDLE.getLogger().log(Level.DEBUG, "í«â¡ÇµÇΩíäè€ÉmÅ[Éhêî: " + abstractNodeNum);
        idConceptMap.clear();
        conceptSameConceptMap.clear();
    }

    private void copyConceptTreeNode(ConceptTreeNode targetNode) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode insertNode = new ConceptTreeNode(targetNode.getConcept(), project);
        insertNode.setIsUserConcept(targetNode.isUserConcept());
        insertNode.setIsInputConcept(targetNode.isInputConcept());
        model.insertNodeInto(insertNode, targetNode, 0);
    }

    private Concept insertNewConceptTreeNode(String word, Concept parentConcept) {
        if (word.indexOf("#") != -1) {
            word = word.split("#")[1];
        }
        Concept newConcept = new VerbConcept(project.getUserIDStr(), word);
        newConcept.setPrefix("keio");
        newConcept.setInputWord(word);
        insertConceptTreeNode(newConcept, parentConcept, false, true);
        return newConcept;
    }

    private void insertConceptTreeNode(Concept insertConcept, Concept parentConcept, boolean isInputConcept,
            boolean isUserConcept) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
        Set sameConceptSet = new HashSet();
        if (parentConcept.getId().equals(rootNode.getConcept().getId())) {
            sameConceptSet.add(rootNode);
        } else {
            if (conceptSameConceptMap.get(parentConcept) != null) {
                sameConceptSet = (Set) conceptSameConceptMap.get(parentConcept);
            } else {
                searchSameConceptTreeNode(parentConcept, rootNode, sameConceptSet);
                conceptSameConceptMap.put(parentConcept, sameConceptSet);
            }
        }
        for (Iterator i = sameConceptSet.iterator(); i.hasNext();) {
            ConceptTreeNode parentNode = (ConceptTreeNode) i.next();
            ConceptTreeNode insertNode = new ConceptTreeNode(insertConcept, project);
            insertNode.setIsInputConcept(isInputConcept);
            insertNode.setIsUserConcept(isUserConcept);
            model.insertNodeInto(insertNode, parentNode, parentNode.getChildCount());
            if (parentNode == rootNode) {
                conceptTree.expandPath(conceptTree.getSelectionPath());
            }
        }
    }

    class CopyConceptAction extends AbstractAction {

        private String title;

        public String getTitle() {
            return title;
        }

        CopyConceptAction(String title) {
            super(title, copyIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            if (conceptTree.getSelectionCount() == 1) {
                targetConceptTreeNode = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
                DODDLE.STATUS_BAR.setValue("ÉRÉsÅ[: " + targetConceptTreeNode.getConcept());
            }
        }
    }

    class CloneConceptAction extends AbstractAction {
        private String title;

        public String getTitle() {
            return title;
        }

        CloneConceptAction(String title) {
            super(title, cloneConceptIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            if (conceptTree.getSelectionCount() == 1) {
                targetConceptTreeNode = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
                pasteConcept((ConceptTreeNode) targetConceptTreeNode.getParent());
                DODDLE.STATUS_BAR.setValue("ï°êª: " + targetConceptTreeNode);
            }
        }

    }

    class CutConceptAction extends AbstractAction {

        private String title;

        public String getTitle() {
            return title;
        }

        CutConceptAction(String title) {
            super(title, cutIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            if (conceptTree.getSelectionCount() == 1) {
                targetConceptTreeNode = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
                deleteLinkToUpperConcept();
                DODDLE.STATUS_BAR.setValue("êÿÇËéÊÇË: " + targetConceptTreeNode.getConcept());
            }
        }

    }

    private Concept pasteConcept(ConceptTreeNode parentNode) {
        Concept targetConcept = targetConceptTreeNode.getConcept();
        if (parentNode != null) {
            insertConceptTreeNode(targetConcept, parentNode.getConcept(), targetConceptTreeNode.isInputConcept(),
                    targetConceptTreeNode.isUserConcept());
            checkMultipleInheritanceNode(targetConcept);
        }
        return targetConcept;
    }

    class PasteConceptAction extends AbstractAction {

        private String title;

        public String getTitle() {
            return title;
        }

        PasteConceptAction(String title) {
            super(title, pasteIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            if (conceptTree.getSelectionCount() == 1) {
                ConceptTreeNode parentNode = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
                Concept pasteConcept = pasteConcept(parentNode);
                DODDLE.STATUS_BAR.setValue("ì\ÇËïtÇØ: " + pasteConcept);
            }
        }

    }

    class AddConceptAction extends AbstractAction {

        private String title;

        AddConceptAction(String title) {
            super(title, addConceptIcon);
            this.title = title;
            setToolTipText(title);
        }

        public String getTitle() {
            return title;
        }

        public void actionPerformed(ActionEvent e) {
            ConceptTreeNode parent = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
            if (parent != null) {
                insertNewConceptTreeNode("êVÉmÅ[Éh" + project.getUserIDCount(), parent.getConcept());
            }
        }
    }

    private void deleteLinkToUpperConcept() {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode targetDeleteNode = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
        if (targetDeleteNode.getParent() != null) {
            ConceptTreeNode targetDeleteNodeParent = (ConceptTreeNode) targetDeleteNode.getParent();
            ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
            Set sameConceptSet = new HashSet();
            searchSameConceptTreeNode(targetDeleteNode.getConcept(), rootNode, sameConceptSet);
            for (Iterator i = sameConceptSet.iterator(); i.hasNext();) {
                ConceptTreeNode deleteNode = (ConceptTreeNode) i.next();
                ConceptTreeNode deleteNodeParent = (ConceptTreeNode) deleteNode.getParent();
                if (deleteNodeParent != null && targetDeleteNodeParent.getId().equals(deleteNodeParent.getId())) {
                    model.removeNodeFromParent(deleteNode);
                }
            }
            checkMultipleInheritanceNode(targetDeleteNode.getConcept());
        }
    }

    class DeleteLinkToUpperConceptAction extends AbstractAction {

        private String title;

        public String getTitle() {
            return title;
        }

        DeleteLinkToUpperConceptAction(String title) {
            super(title, deleteIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            deleteLinkToUpperConcept();
        }
    }

    class DeleteConceptAction extends AbstractAction {

        private String title;

        DeleteConceptAction(String title) {
            super(title, deleteIcon);
            this.title = title;
            setToolTipText(title);
        }

        public String getTitle() {
            return title;
        }

        public void actionPerformed(ActionEvent e) {
            deleteConcept();
        }
    }

    class AddUndefinedWordListAction extends AbstractAction {

        private String title;

        public String getTitle() {
            return title;
        }

        AddUndefinedWordListAction(String title) {
            super(title, addUndefWordIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            Object[] selectedValues = undefinedWordListPanel.getSelectedValues();
            DefaultListModel listModel = undefinedWordListPanel.getModel();
            DefaultListModel viewListModel = undefinedWordListPanel.getViewModel();
            for (int i = 0; i < selectedValues.length; i++) {
                ConceptTreeNode parent = (ConceptTreeNode) conceptTree.getLastSelectedPathComponent();
                if (parent != null) {
                    insertNewConceptTreeNode(selectedValues[i].toString(), parent.getConcept());
                    listModel.removeElement(selectedValues[i]);
                    if (viewListModel != null) {
                        viewListModel.removeElement(selectedValues[i]);
                    }
                }
            }
            undefinedWordListPanel.setTitleWithSize();
        }
    }

    class MoveUndefinedWordListAction extends AbstractAction {

        private String title;

        public String getTitle() {
            return title;
        }

        MoveUndefinedWordListAction(String title) {
            super(title, undefIcon);
            this.title = title;
            setToolTipText(title);
        }

        public void actionPerformed(ActionEvent e) {
            TreePath path = conceptTree.getSelectionPath();
            if (path.getLastPathComponent() != null) {
                ConceptTreeNode conceptTreeNode = (ConceptTreeNode) path.getLastPathComponent();
                // Ç‡Ç§è≠ÇµÅCé¿ëïÇÇ¬ÇﬂÇÈïKóvÇ†ÇË
                DefaultListModel listModel = undefinedWordListPanel.getModel();
                DefaultListModel viewListModel = undefinedWordListPanel.getViewModel();
                listModel.addElement(conceptTreeNode.toString());
                if (viewListModel != null
                        && conceptTreeNode.toString().matches(undefinedWordListPanel.getSearchRegex())) {
                    viewListModel.addElement(conceptTreeNode.toString());
                }
                deleteConcept();
                undefinedWordListPanel.setTitleWithSize();
            }
        }
    }

    public Map getConceptTypicalWordMap() {
        Map conceptTypicalWordMap = new HashMap();
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        if (model.getRoot() instanceof ConceptTreeNode) {
            ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
            saveConceptTypicalWord(rootNode, conceptTypicalWordMap);
        }
        return conceptTypicalWordMap;
    }

    private void saveConceptTypicalWord(ConceptTreeNode node, Map conceptTypicalWordMap) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (childNode.getInputWord() != null) {
                conceptTypicalWordMap.put(childNode.getIdentity(), childNode.getInputWord());
            }
            saveConceptTypicalWord(childNode, conceptTypicalWordMap);
        }
    }

    public void loadConceptTypicalWord(Map idTypicalWordMap) {
        DefaultTreeModel model = (DefaultTreeModel) conceptTree.getModel();
        ConceptTreeNode rootNode = (ConceptTreeNode) model.getRoot();
        loadConceptTypicalWord(rootNode, idTypicalWordMap);
    }

    private void loadConceptTypicalWord(ConceptTreeNode node, Map conceptTypicalWordMap) {
        for (int i = 0; i < node.getChildCount(); i++) {
            ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
            if (conceptTypicalWordMap.get(childNode.getIdentity()) != null) {
                String inputWord = (String) conceptTypicalWordMap.get(childNode.getIdentity());
                Concept concept = childNode.getConcept();
                concept.setInputWord(inputWord);
                childNode.setConcept(concept);
                ((DefaultTreeModel) conceptTree.getModel()).reload(childNode);
            }
            loadConceptTypicalWord(childNode, conceptTypicalWordMap);
        }
    }

    class ExpandSelectedPathAction extends AbstractAction {

        public ExpandSelectedPathAction(String title) {
            super(title, expandTreeIcon);
        }

        public void actionPerformed(ActionEvent e) {
            conceptTree.expandPath(conceptTree.getSelectionPath());
        }
    }

    class ExpandAllPathAction extends AbstractAction {

        public ExpandAllPathAction(String title) {
            super(title, expandTreeIcon);
        }

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < conceptTree.getRowCount(); i++) {
                conceptTree.expandPath(conceptTree.getPathForRow(i));
            }
        }
    }
}
