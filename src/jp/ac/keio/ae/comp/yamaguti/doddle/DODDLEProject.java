package jp.ac.keio.ae.comp.yamaguti.doddle;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class DODDLEProject extends JInternalFrame implements ActionListener {

    private JTabbedPane tabbedPane;
    private DocumentSelectionPanel docSelectionPanel;
    private InputWordSelectionPanel inputWordSelectinPanel;
    private InputModuleUI inputModuleUI;
    private ConstructConceptTreePanel constructConceptTreePanel;
    private ConstructPropertyTreePanel constructPropertyTreePanel;
    private TextConceptDefinitionPanel textConceptDefinitionPanel;

    private int userIDCount;
    private Map<String, Concept> idConceptMap;

    private JMenu projectMenu;
    private JCheckBoxMenuItem projectMenuItem;

    public DODDLEProject(String title, JMenu pm) {
        super(title, true, true, true, true);
        projectMenu = pm;
        projectMenuItem = new JCheckBoxMenuItem(title);
        projectMenuItem.addActionListener(this);
        projectMenu.add(projectMenuItem);

        userIDCount = 0;
        idConceptMap = new HashMap<String, Concept>();
        constructConceptTreePanel = new ConstructConceptTreePanel(this);
        constructPropertyTreePanel = new ConstructPropertyTreePanel(this);
        inputModuleUI = new InputModuleUI(constructConceptTreePanel, constructPropertyTreePanel, this);
        inputWordSelectinPanel = new InputWordSelectionPanel(inputModuleUI);
        docSelectionPanel = new DocumentSelectionPanel(inputWordSelectinPanel);
        textConceptDefinitionPanel = new TextConceptDefinitionPanel(this);
        inputModuleUI.setDocumentSelectionPanel(docSelectionPanel);
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("文書選択", Utils.getImageIcon("open_doc.gif"), docSelectionPanel);
        tabbedPane.addTab("入力単語選択", Utils.getImageIcon("input_words.png"), inputWordSelectinPanel);
        tabbedPane.addTab("多義性解消", Utils.getImageIcon("disambiguation.png"), inputModuleUI);
        tabbedPane.addTab("クラス階層構築", Utils.getImageIcon("class_tree.png"), constructConceptTreePanel);
        tabbedPane.addTab("プロパティ階層構築", Utils.getImageIcon("property_tree.png"), constructPropertyTreePanel);
        tabbedPane.addTab("概念定義", Utils.getImageIcon("non-taxonomic.png"), textConceptDefinitionPanel);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                int messageType = JOptionPane.showConfirmDialog(tabbedPane, getTitle() + "\nプロジェクトを終了しますか？");
                if (messageType == JOptionPane.YES_OPTION) {
                    projectMenu.remove(projectMenuItem);
                    dispose();
                }
            }
        });
        setSize(600, 500);
    }

    public void setProjectName(String name) {
        projectMenuItem.setText(name);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == projectMenuItem) {
            for (int i = 0; i < projectMenu.getItemCount(); i++) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) projectMenu.getItem(i);
                item.setSelected(false);
            }
            projectMenuItem.setSelected(true);            
            toFront();
            try { 
                setSelected(true);
            } catch(PropertyVetoException pve) {
                pve.printStackTrace();
            }
        }
    }

    public void resetIDConceptMap() {
        idConceptMap.clear();
    }

    public Set getAllConcept() {
        return idConceptMap.keySet();
    }

    public void putConcept(String id, Concept c) {
        idConceptMap.put(c.getId(), c);
    }

    public Concept getConcept(String id) {
        return idConceptMap.get(id);
    }

    public void initUserIDCount() {
        userIDCount = 0;
    }

    public int getUserIDCount() {
        return userIDCount;
    }

    public String getUserIDStr() {
        return "UID" + Integer.toString(userIDCount++);
    }

    public void setUserIDCount(int id) {
        if (userIDCount < id) {
            userIDCount = id;
        }
    }

    public DocumentSelectionPanel getDocumentSelectionPanel() {
        return docSelectionPanel;
    }

    public InputWordSelectionPanel getInputWordSelectionPanel() {
        return inputWordSelectinPanel;
    }

    public InputModuleUI getInputModuleUI() {
        return inputModuleUI;
    }
    
    public InputModule getInputModule() {
        return inputModuleUI.getInputModule();
    }

    public ConstructPropertyTreePanel getConstructPropertyTreePanel() {
        return constructPropertyTreePanel;
    }

    public ConstructConceptTreePanel getConstructConceptTreePanel() {
        return constructConceptTreePanel;
    }

    public TextConceptDefinitionPanel getTextConceptDefinitionPanel() {
        return textConceptDefinitionPanel;
    }

    public void setSelectedIndex(int i) {
        tabbedPane.setSelectedIndex(i);
    }

    public boolean isPerfectMatchedAmbiguityCntCheckBox() {
        return inputModuleUI.isPerfectMatchedAmbiguityCntCheckBox();
    }

    public boolean isPartialMatchedAmbiguityCntCheckBox() {
        return inputModuleUI.isPartialMatchedAmbiguityCntCheckBox();
    }

    public boolean isPartialMatchedComplexWordCheckBox() {
        return inputModuleUI.isPartialMatchedComplexWordCheckBox();
    }

    public boolean isPartialMatchedMatchedWordBox() {
        return inputModuleUI.isPartialMatchedMatchedWordBox();
    }
}
