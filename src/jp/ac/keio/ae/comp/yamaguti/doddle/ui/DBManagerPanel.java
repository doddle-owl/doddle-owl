/*
 * @(#)  2007/09/13
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.actions.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

import com.hp.hpl.jena.db.*;

/**
 * @author takeshi morita
 */
public class DBManagerPanel extends JDialog implements ActionListener {

    private int lastProjectID;
    
    private String host;
    private String projectName;
    private String userName;
    private String passWord;

    private JButton connectButton;

    private Connection con;
    private IDBConnection icon;
    private Statement stmt;

    private JLabel serverHostLabel;
    private JTextField serverHostTextField;
    private JLabel userNameLabel;
    private JTextField userNameTextField;
    private JLabel passWordLabel;
    private JPasswordField passWordField;

    private JTable projectInfoTable;
    private TableRowSorter<TableModel> rowSorter;
    private ProjectInfoTableModel projectInfoTableModel;

    private JButton openProjectButton;
    private JButton updateProjectButton;
    private JButton newProjectButton;
    private JButton renameProjectButton;
    private JButton removeProjectButton;

    private SaveOntologyAction saveOntologyAction;
    private LoadOntologyAction loadOntologyAction;

    public DBManagerPanel() {
        super(DODDLE.rootFrame, "DB Manager");
        serverHostLabel = new JLabel("サーバホスト");
        serverHostTextField = new JTextField(20);
        userNameLabel = new JLabel("ユーザ名");
        userNameTextField = new JTextField(20);
        passWordLabel = new JLabel("パスワード");
        passWordField = new JPasswordField(20);

        connectButton = new JButton("接続");
        connectButton.addActionListener(this);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        JPanel dbAccountPanel = new JPanel();
        dbAccountPanel.setBorder(BorderFactory.createTitledBorder("DBアカウント"));
        dbAccountPanel.setLayout(new GridLayout(4, 2));
        dbAccountPanel.add(serverHostLabel);
        dbAccountPanel.add(serverHostTextField);
        dbAccountPanel.add(userNameLabel);
        dbAccountPanel.add(userNameTextField);
        dbAccountPanel.add(passWordLabel);
        dbAccountPanel.add(passWordField);
        dbAccountPanel.add(new JLabel(""));
        dbAccountPanel.add(connectButton);

        northPanel.add(dbAccountPanel, BorderLayout.WEST);

        projectInfoTable = new JTable();
        setProjectInfoTableModel();
        JScrollPane projectInfoTableScroll = new JScrollPane(projectInfoTable);

        openProjectButton = new JButton("Open");
        openProjectButton.addActionListener(this);
        updateProjectButton = new JButton("Update");
        updateProjectButton.addActionListener(this);
        newProjectButton = new JButton("New");
        newProjectButton.addActionListener(this);
        renameProjectButton = new JButton("Rename");
        renameProjectButton.addActionListener(this);
        removeProjectButton = new JButton("Remove");
        removeProjectButton.addActionListener(this);

        saveOntologyAction = new SaveOntologyAction("", SaveOntologyAction.OWL_ONTOLOGY);
        loadOntologyAction = new LoadOntologyAction("", LoadOntologyAction.OWL_ONTOLOGY);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openProjectButton);
        buttonPanel.add(updateProjectButton);
        buttonPanel.add(newProjectButton);
        buttonPanel.add(renameProjectButton);
        buttonPanel.add(removeProjectButton);

        getContentPane().add(northPanel, BorderLayout.NORTH);
        getContentPane().add(projectInfoTableScroll, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(DODDLE.rootFrame);
    }

    public void setProjectInfoTableModel() {
        // String WORD = Translator.getTerm("WordLabel");
        Object[] titles = new Object[] { "id", "プロジェクト名", "作成者", "作成日", "更新日"};

        projectInfoTableModel = new ProjectInfoTableModel(null, titles);
        projectInfoTableModel.getColumnClass(0);
        rowSorter = new TableRowSorter<TableModel>(projectInfoTableModel);
        rowSorter.setMaxSortKeys(5);

        projectInfoTable.setRowSorter(rowSorter);
        projectInfoTable.setModel(projectInfoTableModel);
        projectInfoTable.getTableHeader().setToolTipText("sorted by column");        
    }

    class ProjectInfoTableModel extends DefaultTableModel {

        ProjectInfoTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        public Class< ? > getColumnClass(int columnIndex) {
            String columnName = getColumnName(columnIndex);
            if (columnName.equals("id")) {
                return Integer.class;
            } 
            return String.class;
        }
    }

    public void connectDB() {
        try {
            setProjectInfoTableModel();
            Class.forName("org.gjt.mm.mysql.Driver");
            host = serverHostTextField.getText();
            String dbName = "doddle";
            String url = "jdbc:mysql://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
            userName = userNameTextField.getText();
            passWord = String.valueOf(passWordField.getPassword());
            con = DriverManager.getConnection(url, userName, passWord);
            icon = new DBConnection(url, userName, passWord, "MySQL");
            stmt = con.createStatement();

            String sql = "SELECT * from project_info";
            ResultSet rs = stmt.executeQuery(sql);            
            while (rs.next()) {
                lastProjectID = rs.getInt("Project_ID");
                String projectName = rs.getString("Project_Name");
                String author = rs.getString("Author");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String creationDate = dateFormat.format(rs.getTimestamp("Creation_Date"));
                String modificationDate = dateFormat.format(rs.getTimestamp("Modification_Date"));
                projectInfoTableModel.addRow(new Object[]{lastProjectID, projectName, author, creationDate, modificationDate});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "DB Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
            if (icon != null) {
                icon.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTableContents(int projectID, Statement stmt, String tableName) {
        try {
            String sql = "DELETE FROM " + tableName + " WHERE Project_ID=" + projectID;
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static int getMySQLBoolean(boolean t) {
        if (t) { return 1; }
        return 0;
    }

    public static boolean getMySQLBoolean(int t) {
        return t == 1;
    }

    public void saveProjectInfo(int projectID, DODDLEProject currentProject) {
        OntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        DisambiguationPanel disambiguationPanel = currentProject.getDisambiguationPanel();
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

        try {
            String sql = "SELECT * from project_info where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            Date creationDate = null;
            while (rs.next()) {
                creationDate = rs.getTimestamp("Creation_Date");
                projectName = rs.getString("Project_Name");
            }
            if (creationDate == null) {
                creationDate = Calendar.getInstance().getTime();
            }
            DBManagerPanel.deleteTableContents(projectID, stmt, "project_info");

            StringBuffer sqlbuf = new StringBuffer();
            sqlbuf.append("INSERT INTO project_info (Project_ID,Project_Name,Author,Creation_Date,"
                    + "Modification_Date,Available_General_Ontologies,Input_Term_Count,Perfectly_Matched_Term_Count,"
                    + "System_Added_Perfectly_Matched_Term_Count,Partially_Matched_Term_Count,"
                    + "Matched_Term_Count,Undefined_Term_Count,Input_Concept_Count,Input_Noun_Concept_Count,"
                    + "Input_Verb_Concept_Count,Class_SIN_Count,Before_Trimming_Class_Count,"
                    + "Trimmed_Class_Count,After_Trimming_Class_Count,Property_SIN_Count,"
                    + "Before_Trimming_Property_Count,Trimmed_Property_Count,After_Trimming_Property_Count,"
                    + "Abstract_Internal_Class_Count,Average_Abstract_Sibling_Concept_Count_In_Classes,"
                    + "Abstract_Internal_Property_Count_Message,Average_Abstract_Sibling_Concept_Count_In_Properties,"
                    + "Class_From_Compound_Word_Count,Property_From_Compound_Word_Count,Total_Class_Count,"
                    + "Total_Property_Count,Average_Sibling_Classes,Average_Sibling_Properties,Base_URI) ");
            sqlbuf.append("VALUES(");

            sqlbuf.append(projectID);
            sqlbuf.append(",'");
            sqlbuf.append(projectName);
            sqlbuf.append("','");
            sqlbuf.append(userName);
            sqlbuf.append("','");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sqlbuf.append(dateFormat.format(creationDate));
            sqlbuf.append("','");
            sqlbuf.append(dateFormat.format(Calendar.getInstance().getTime()));
            sqlbuf.append("','");
            sqlbuf.append(ontSelectionPanel.getEnableDicList());
            sqlbuf.append("',");
            if (disambiguationPanel.getInputWordModelSet() != null) {
                sqlbuf.append(disambiguationPanel.getInputWordCnt());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            sqlbuf.append(disambiguationPanel.getPerfectlyMatchedWordCnt());
            sqlbuf.append(",");
            sqlbuf.append(disambiguationPanel.getSystemAddedPerfectlyMatchedWordCnt());
            sqlbuf.append(",");
            sqlbuf.append(disambiguationPanel.getPartiallyMatchedWordCnt());
            sqlbuf.append(",");
            sqlbuf.append(disambiguationPanel.getMatchedWordCnt());
            sqlbuf.append(",");
            sqlbuf.append(disambiguationPanel.getUndefinedWordCnt());
            sqlbuf.append(",");
            if (disambiguationPanel.getInputConceptSet() != null) {
                sqlbuf.append(disambiguationPanel.getInputConceptSet().size());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            if (disambiguationPanel.getInputNounConceptSet() != null) {
                sqlbuf.append(disambiguationPanel.getInputNounConceptSet().size());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            if (disambiguationPanel.getInputVerbConceptSet() != null) {
                sqlbuf.append(disambiguationPanel.getInputVerbConceptSet().size());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getAddedSINNum());
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getBeforeTrimmingConceptNum());
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getTrimmedConceptNum());
            sqlbuf.append(",");
            int afterTrimmingConceptNum = constructClassPanel.getAfterTrimmingConceptNum();
            sqlbuf.append(afterTrimmingConceptNum);
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getAddedSINNum());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getBeforeTrimmingConceptNum());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getTrimmedConceptNum());
            sqlbuf.append(",");
            int afterTrimmingPropertyNum = constructPropertyPanel.getAfterTrimmingConceptNum();
            sqlbuf.append(afterTrimmingPropertyNum);
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getAddedAbstractComplexConceptCnt());
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getAverageAbstracComplexConceptGroupSiblingConceptCnt());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getAddedAbstractComplexConceptCnt());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getAverageAbstracComplexConceptGroupSiblingConceptCnt());
            sqlbuf.append(",");

            int lastClassNum = constructClassPanel.getAllConceptCnt();
            int lastPropertyNum = constructPropertyPanel.getAllConceptCnt();

            sqlbuf.append((lastClassNum - afterTrimmingConceptNum));
            sqlbuf.append(",");
            sqlbuf.append((lastPropertyNum - afterTrimmingPropertyNum));
            sqlbuf.append(",");
            sqlbuf.append(lastClassNum);
            sqlbuf.append(",");
            sqlbuf.append(lastPropertyNum);
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getChildCntAverage());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getChildCntAverage());
            sqlbuf.append(",'");
            sqlbuf.append(DODDLEConstants.BASE_URI);
            sqlbuf.append("')");
            stmt.executeUpdate(sqlbuf.toString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void removeProject(int projectID, int selectedRow) {
        DBManagerPanel.deleteTableContents(projectID, stmt, "general_ontology_info");
        DBManagerPanel.deleteTableContents(projectID, stmt, "doc_info");
        DBManagerPanel.deleteTableContents(projectID, stmt, "term_info");
        DBManagerPanel.deleteTableContents(projectID, stmt, "term_info_pos_list");
        DBManagerPanel.deleteTableContents(projectID, stmt, "term_info_doc_list");
        DBManagerPanel.deleteTableContents(projectID, stmt, "removed_term_info");
        DBManagerPanel.deleteTableContents(projectID, stmt, "removed_term_info_pos_list");
        DBManagerPanel.deleteTableContents(projectID, stmt, "removed_term_info_doc_list");
        DBManagerPanel.deleteTableContents(projectID, stmt, "input_term_set");
        DBManagerPanel.deleteTableContents(projectID, stmt, "term_eval_concept_set");
        DBManagerPanel.deleteTableContents(projectID, stmt, "eval_concept_set");
        DBManagerPanel.deleteTableContents(projectID, stmt, "input_term_concept_map");
        DBManagerPanel.deleteTableContents(projectID, stmt, "input_term_construct_tree_option");
        DBManagerPanel.deleteTableContents(projectID, stmt, "input_concept_set");
        DBManagerPanel.deleteTableContents(projectID, stmt, "construct_tree_option");
        DBManagerPanel.deleteTableContents(projectID, stmt, "undefined_term_set");
        saveOntologyAction.saveOWLOntology(projectID, icon, DODDLE.getCurrentProject());
        DBManagerPanel.deleteTableContents(projectID, stmt, "class_trimmed_result_analysis");
        DBManagerPanel.deleteTableContents(projectID, stmt, "trimmed_class_list");
        DBManagerPanel.deleteTableContents(projectID, stmt, "property_trimmed_result_analysis");
        DBManagerPanel.deleteTableContents(projectID, stmt, "trimmed_property_list");
        DBManagerPanel.deleteTableContents(projectID, stmt, "concept_definition_parameter");
        DBManagerPanel.deleteTableContents(projectID, stmt, "concept_definition");
        DBManagerPanel.deleteTableContents(projectID, stmt, "wrong_pair");
        DBManagerPanel.deleteTableContents(projectID, stmt, "wordspace_result");
        DBManagerPanel.deleteTableContents(projectID, stmt, "apriori_result");
        DBManagerPanel.deleteTableContents(projectID, stmt, "project_info");
        projectInfoTableModel.removeRow(selectedRow);
        System.out.println("remove project: " + projectID);
    }

    public void updateProject(int projectID) {
        try {
            DODDLEProject project = DODDLE.getCurrentProject();
            OntologySelectionPanel ontSelectionPanel = project.getOntologySelectionPanel();
            DocumentSelectionPanel docSelectionPanel = project.getDocumentSelectionPanel();
            InputWordSelectionPanel inputWordSelectionPanel = project.getInputWordSelectionPanel();
            DisambiguationPanel disambiguationPanel = project.getDisambiguationPanel();
            ConceptDefinitionPanel conceptDefinitionPanel = project.getConceptDefinitionPanel();

            ontSelectionPanel.saveGeneralOntologyInfoToDB(projectID, stmt);
            docSelectionPanel.saveDocumentInfo(projectID, stmt);
            inputWordSelectionPanel.saveWordInfoTable(projectID, stmt);
            disambiguationPanel.saveInputWordSetToDB(projectID, stmt);
            disambiguationPanel.saveWordEvalConceptSet(projectID, stmt);
            disambiguationPanel.saveWordCorrespondConceptSetMapToDB(projectID, stmt);
            disambiguationPanel.saveInputWordConstructTreeOptionSetToDB(projectID, stmt);
            disambiguationPanel.saveInputConceptSetToDB(projectID, stmt);
            disambiguationPanel.saveConstructTreeOptionToDB(projectID, stmt);
            disambiguationPanel.saveUndefinedWordSet(projectID, stmt);
            saveOntologyAction.saveOWLOntology(projectID, icon, project);

            project.getConstructClassPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(projectID,
                    stmt, "class_trimmed_result_analysis", "trimmed_class_list");
            project.getConstructPropertyPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(projectID,
                    stmt, "property_trimmed_result_analysis", "trimmed_property_list");

            conceptDefinitionPanel.saveConeptDefinitionParameters(projectID, stmt);
            conceptDefinitionPanel.saveConceptDefinition(projectID, stmt);
            conceptDefinitionPanel.saveWrongPairSet(projectID, stmt);
            conceptDefinitionPanel.saveWordSpaceResult(projectID, stmt);
            conceptDefinitionPanel.saveAprioriResult(projectID, stmt);

            saveProjectInfo(projectID, project);
            System.out.println("save db done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newProject() {
        projectName = JOptionPane.showInputDialog(this, "Input Project Name", "New Project", JOptionPane.OK_CANCEL_OPTION);
        if (projectName != null) {
            updateProject(lastProjectID+1);
            connectDB();
        }
    }

    public void renameProject(int projectID, int selectedRow) {
        projectName = JOptionPane.showInputDialog(this, "Input Project Name", "Rename Project", JOptionPane.OK_CANCEL_OPTION);
        if (projectName == null) {
            return;
        }
        try {
            String sql = "UPDATE project_info SET Project_Name='" + projectName + "' WHERE Project_ID="+projectID;
            System.out.println(sql);
            stmt.executeUpdate(sql);
            projectInfoTableModel.setValueAt(projectName, selectedRow, 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBaseURI(int projectID) {
        try {
            String sql = "SELECT * from project_info where Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String baseURI = rs.getString("Base_URI");
                DODDLEConstants.BASE_URI = baseURI;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void openProject(int projectID) {
        System.out.println(projectID);
        // DODDLEProject currentProject = new DODDLEProject(projectName, 32);
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        OntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        DisambiguationPanel disambiguationPanel = currentProject.getDisambiguationPanel();
        DocumentSelectionPanel docSelectionPanel = currentProject.getDocumentSelectionPanel();
        InputWordSelectionPanel inputWordSelectionPanel = currentProject.getInputWordSelectionPanel();
        ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();

        loadBaseURI(projectID);
        docSelectionPanel.loadDocuments(projectID, stmt);
        ontSelectionPanel.loadGeneralOntologyInfo(projectID, stmt);
        // ontSelectionPanel.loadOWLMetaDataSet(new File(openDir,
        // ProjectFileNames.OWL_META_DATA_SET_DIR));

        inputWordSelectionPanel.loadWordInfoTable(projectID, stmt, docSelectionPanel.getDocNum());
        disambiguationPanel.loadInputWordSet(projectID, stmt);
        disambiguationPanel.loadWordEvalConceptSet(projectID, stmt);
        disambiguationPanel.loadWordCorrespondConceptSetMap(projectID, stmt);
        disambiguationPanel.loadConstructTreeOption(projectID, stmt);
        disambiguationPanel.loadInputWordConstructTreeOptionSet(projectID, stmt);
        disambiguationPanel.loadInputConceptSet(projectID, stmt);
        disambiguationPanel.loadUndefinedWordSet(projectID, stmt);
        loadOntologyAction.loadOWLOntology(projectID, icon, currentProject);

        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
        constructClassPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(projectID, stmt,
                "class_trimmed_result_analysis", "trimmed_class_list");
        constructPropertyPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(projectID, stmt,
                "property_trimmed_result_analysis", "trimmed_property_list");

        conceptDefinitionPanel.setInputDocList();
        conceptDefinitionPanel.loadConceptDefinitionParameters(projectID, stmt);
        conceptDefinitionPanel.loadWordSpaceResult(projectID, stmt);
        conceptDefinitionPanel.loadAprioriResult(projectID, stmt);
        conceptDefinitionPanel.loadConceptDefinition(projectID, stmt);
        conceptDefinitionPanel.loadWrongPairSet(projectID, stmt);

        disambiguationPanel.selectTopList();
        constructClassPanel.expandIsaTree();
        constructClassPanel.expandHasaTree();
        constructPropertyPanel.expandIsaTree();
        System.out.println("load db done");
    }

    public void actionPerformed(ActionEvent e) {
        int selectedRow = projectInfoTable.getSelectedRow();
        int id = -1;
        if (selectedRow != -1) {
            id = (Integer)projectInfoTable.getValueAt(selectedRow, 0);
        }
        if (e.getSource() == openProjectButton) {
            openProject(id);
        } else if (e.getSource() == updateProjectButton) {
            updateProject(id);
        } else if (e.getSource() == newProjectButton) {
            newProject();
        } else if (e.getSource() == renameProjectButton) {
            renameProject(id, selectedRow);
        } else if (e.getSource() == removeProjectButton) {
            removeProject(id, selectedRow);            
        } else if (e.getSource() == connectButton) {
            connectDB();
        }
    }

}
