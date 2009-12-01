/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2009 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.actions.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import com.hp.hpl.jena.db.*;

/**
 * @author takeshi morita
 */
public class DBManagerDialog extends JDialog implements ActionListener, ListSelectionListener {

    private int projectID;
    private int lastProjectID;
    private String author;
    private String projectName;
    private String projectComment;
    private DODDLEProject currentProject;

    private JButton connectButton;

    private Connection con;
    private IDBConnection icon;
    private Statement stmt;

    private JLabel projectNameLabel;
    private JTextField projectNameField;
    private JLabel authorLabel;
    private JTextField authorField;
    private JTextArea commentArea;
    private JButton editCommentButton;

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
    private JButton removeProjectButton;

    private ProjectItemSelectionPanel projectItemSelectionPanel;

    private SaveOntologyAction saveOntologyAction;
    private LoadOntologyAction loadOntologyAction;

    private static String GENERAL_ONTOLOGY_INFO;
    private static String DOCUMENT_INFO;
    private static String TERM_INFO_TABLE;
    private static String INPUT_TERM_SET;
    private static String TERM_EVAL_CONCEPT_SET;
    private static String TERM_CORRESPOND_CONCEPT_SET;
    private static String INPUT_TERM_CONSTRUCT_TREE_OPTION;
    private static String INPUT_CONCEPT_SET;
    private static String CONSTRUCT_TREE_OPTION;
    private static String UNDEFINED_TERM_SET;
    private static String TAXONOMY;
    private static String CLASS_TRIMMED_RESULT_ANALYSIS;
    private static String PROPERTY_TRIMMED_RESULT_ANALYSIS;
    private static String CONCEPT_DEFINITION_PARAMETERS;
    private static String CONCEPT_DEFINITION;
    private static String WRONG_CONCEPT_PAIRS;
    private static String APRIORI_RESULT;
    private static String WORDSPACE_RESULT;

    private ImageIcon databaseAddIcon = Utils.getImageIcon("database_add.png");
    private ImageIcon databaseSaveIcon = Utils.getImageIcon("database_save.png");
    private ImageIcon databaseDeleteIcon = Utils.getImageIcon("database_delete.png");
    private ImageIcon databaseOpenIcon = Utils.getImageIcon("database_go.png");
    private ImageIcon databaseEditIcon = Utils.getImageIcon("database_edit.png");
    private ImageIcon databaseConnectIcon = Utils.getImageIcon("database_connect.png");

    public void setProjectItems() {
        GENERAL_ONTOLOGY_INFO = Translator.getTerm("GeneralOntologyInfoCheckBox");
        DOCUMENT_INFO = Translator.getTerm("DocumentInfoCheckBox");
        TERM_INFO_TABLE = Translator.getTerm("TermInfoTableCheckBox");
        INPUT_TERM_SET = Translator.getTerm("InputTermSetCheckBox");
        TERM_EVAL_CONCEPT_SET = Translator.getTerm("TermEvalConceptSetCheckBox");
        TERM_CORRESPOND_CONCEPT_SET = Translator.getTerm("TermCorrespondConceptSetCheckBox");
        INPUT_TERM_CONSTRUCT_TREE_OPTION = Translator.getTerm("InputTermConstructTreeOptionCheckBox");
        INPUT_CONCEPT_SET = Translator.getTerm("InputConceptSetCheckBox");
        CONSTRUCT_TREE_OPTION = Translator.getTerm("ConstructTreeOptionCheckBox");
        UNDEFINED_TERM_SET = Translator.getTerm("UndefinedTermSetCheckBox");
        TAXONOMY = Translator.getTerm("TaxonomyCheckBox");
        CLASS_TRIMMED_RESULT_ANALYSIS = Translator.getTerm("ClassTrimmedResultAnalysisCheckBox");
        PROPERTY_TRIMMED_RESULT_ANALYSIS = Translator.getTerm("PropertyTrimmedResultAnalysisCheckBox");
        CONCEPT_DEFINITION_PARAMETERS = Translator.getTerm("ConceptDefinitionParametersCheckBox");
        CONCEPT_DEFINITION = Translator.getTerm("ConceptDefinitionCheckBox");
        WRONG_CONCEPT_PAIRS = Translator.getTerm("WrongConceptPairsCheckBox");
        APRIORI_RESULT = Translator.getTerm("AprioriResultCheckBox");
        WORDSPACE_RESULT = Translator.getTerm("WordSpaceResultCheckBox");
    }

    public DBManagerDialog() {
        super(DODDLE.rootFrame, Translator.getTerm("DBManagerDialog"));
        setIconImage(Utils.getImageIcon("database.png").getImage());
        setProjectItems();
        projectNameLabel = new JLabel(Translator.getTerm("ProjectNameLabel"));
        projectNameField = new JTextField(20);
        authorLabel = new JLabel(Translator.getTerm("AuthorLabel"));
        authorField = new JTextField(20);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(projectNameLabel);
        panel.add(projectNameField);
        panel.add(authorLabel);
        panel.add(authorField);
        commentArea = new JTextArea(4, 20);
        JScrollPane commentAreaScroll = new JScrollPane(commentArea);
        commentAreaScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("ProjectCommentLabel")));
        editCommentButton = new JButton(Translator.getTerm("UpdateButton"), databaseEditIcon);
        editCommentButton.addActionListener(this);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(editCommentButton, BorderLayout.EAST);
        JPanel projectInfoPanel = new JPanel();
        projectInfoPanel.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("ProjectInfoBorder")));
        projectInfoPanel.setLayout(new BorderLayout());
        projectInfoPanel.add(panel, BorderLayout.NORTH);
        projectInfoPanel.add(commentAreaScroll, BorderLayout.CENTER);
        projectInfoPanel.add(eastPanel, BorderLayout.SOUTH);

        serverHostLabel = new JLabel(Translator.getTerm("ServerHostLabel"));
        serverHostTextField = new JTextField(20);
        serverHostTextField.setText("");
        userNameLabel = new JLabel(Translator.getTerm("UserNameLabel"));
        userNameTextField = new JTextField(20);
        userNameTextField.setText("");
        passWordLabel = new JLabel(Translator.getTerm("PasswordLabel"));
        passWordField = new JPasswordField(20);
        passWordField.setText("");

        connectButton = new JButton(Translator.getTerm("ConnectionButton"), databaseConnectIcon);
        connectButton.addActionListener(this);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        JPanel dbAccountPanel = new JPanel();
        dbAccountPanel.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("DBAccountBorder")));
        dbAccountPanel.setLayout(new GridLayout(4, 2));
        dbAccountPanel.add(serverHostLabel);
        dbAccountPanel.add(serverHostTextField);
        dbAccountPanel.add(userNameLabel);
        dbAccountPanel.add(userNameTextField);
        dbAccountPanel.add(passWordLabel);
        dbAccountPanel.add(passWordField);
        dbAccountPanel.add(new JLabel(""));
        dbAccountPanel.add(connectButton);
        JPanel dbAccountNorthPanel = new JPanel();
        dbAccountNorthPanel.setLayout(new BorderLayout());
        dbAccountNorthPanel.add(dbAccountPanel, BorderLayout.NORTH);

        northPanel.add(projectInfoPanel, BorderLayout.CENTER);
        northPanel.add(dbAccountNorthPanel, BorderLayout.WEST);

        projectInfoTable = new JTable();
        projectInfoTable.getSelectionModel().addListSelectionListener(this);
        projectInfoTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setProjectInfoTableModel();
        JScrollPane projectInfoTableScroll = new JScrollPane(projectInfoTable);

        newProjectButton = new JButton(Translator.getTerm("NewProjectAction"), databaseAddIcon);
        newProjectButton.addActionListener(this);
        openProjectButton = new JButton(Translator.getTerm("OpenProjectAction"), databaseOpenIcon);
        openProjectButton.addActionListener(this);
        updateProjectButton = new JButton(Translator.getTerm("UpdateProjectAction"), databaseSaveIcon);
        updateProjectButton.addActionListener(this);
        removeProjectButton = new JButton(Translator.getTerm("RemoveProjectAction"), databaseDeleteIcon);
        removeProjectButton.addActionListener(this);

        saveOntologyAction = new SaveOntologyAction("", SaveOntologyAction.OWL_ONTOLOGY);
        loadOntologyAction = new LoadOntologyAction("", LoadOntologyAction.OWL_ONTOLOGY);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(newProjectButton);
        buttonPanel.add(openProjectButton);
        buttonPanel.add(updateProjectButton);
        buttonPanel.add(removeProjectButton);

        getContentPane().add(northPanel, BorderLayout.NORTH);
        getContentPane().add(projectInfoTableScroll, BorderLayout.CENTER);
        getContentPane().add(Utils.createWestPanel(buttonPanel), BorderLayout.SOUTH);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(DODDLE.rootFrame);
    }

    public void setProjectInfoTableModel() {
        Object[] titles = new Object[] { "id", Translator.getTerm("ProjectNameLabel"),
                Translator.getTerm("AuthorLabel"), Translator.getTerm("CreationDateLabel"),
                Translator.getTerm("ModificationDateLabel")};

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
            if (columnName.equals("id")) { return Integer.class; }
            return String.class;
        }
    }

    public void connectDB() {
        try {
            setProjectInfoTableModel();
            Class.forName("org.gjt.mm.mysql.Driver");
            String host = serverHostTextField.getText();
            String dbName = "doddle";
            String url = "jdbc:mysql://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
            String userName = userNameTextField.getText();
            String passWord = String.valueOf(passWordField.getPassword());
            // System.out.println(url+":"+userName+":"+passWord);
            con = DriverManager.getConnection(url, userName, passWord);
            icon = new DBConnection(url, userName, passWord, "MySQL");
            stmt = con.createStatement();
            String sql = "SELECT * from project_info order by Project_ID";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Project_ID");
                if (lastProjectID < id) {
                    lastProjectID = id;
                }
                String projectName = rs.getString("Project_Name");
                String author = rs.getString("Author");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String creationDate = dateFormat.format(rs.getTimestamp("Creation_Date"));
                String modificationDate = dateFormat.format(rs.getTimestamp("Modification_Date"));
                projectInfoTableModel.addRow(new Object[] { id, projectName, author, creationDate, modificationDate});
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
        ReferenceOntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        InputConceptSelectionPanel inputConceptSelectionPanel = currentProject.getInputConceptSelectionPanel();
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
            DBManagerDialog.deleteTableContents(projectID, stmt, "project_info");

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
                    + "Total_Property_Count,Average_Sibling_Classes,Average_Sibling_Properties,Base_URI,Comment) ");
            sqlbuf.append("VALUES(");

            sqlbuf.append(projectID);
            sqlbuf.append(",'");
            sqlbuf.append(projectName);
            sqlbuf.append("','");
            sqlbuf.append(author);
            sqlbuf.append("','");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sqlbuf.append(dateFormat.format(creationDate));
            sqlbuf.append("','");
            sqlbuf.append(dateFormat.format(Calendar.getInstance().getTime()));
            sqlbuf.append("','");
            sqlbuf.append(ontSelectionPanel.getEnableDicList());
            sqlbuf.append("',");
            if (inputConceptSelectionPanel.getInputTermModelSet() != null) {
                sqlbuf.append(inputConceptSelectionPanel.getInputTermCnt());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            sqlbuf.append(inputConceptSelectionPanel.getPerfectlyMatchedTermCnt());
            sqlbuf.append(",");
            sqlbuf.append(inputConceptSelectionPanel.getSystemAddedPerfectlyMatchedTermCnt());
            sqlbuf.append(",");
            sqlbuf.append(inputConceptSelectionPanel.getPartiallyMatchedTermCnt());
            sqlbuf.append(",");
            sqlbuf.append(inputConceptSelectionPanel.getMatchedTermCnt());
            sqlbuf.append(",");
            sqlbuf.append(inputConceptSelectionPanel.getUndefinedTermCnt());
            sqlbuf.append(",");
            if (inputConceptSelectionPanel.getInputConceptSet() != null) {
                sqlbuf.append(inputConceptSelectionPanel.getInputConceptSet().size());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            if (inputConceptSelectionPanel.getInputNounConceptSet() != null) {
                sqlbuf.append(inputConceptSelectionPanel.getInputNounConceptSet().size());
            } else {
                sqlbuf.append(0);
            }
            sqlbuf.append(",");
            if (inputConceptSelectionPanel.getInputVerbConceptSet() != null) {
                sqlbuf.append(inputConceptSelectionPanel.getInputVerbConceptSet().size());
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
            sqlbuf.append(constructClassPanel.getAddedAbstractCompoundConceptCnt());
            sqlbuf.append(",");
            sqlbuf.append(constructClassPanel.getAverageAbstracCompoundConceptGroupSiblingConceptCnt());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getAddedAbstractCompoundConceptCnt());
            sqlbuf.append(",");
            sqlbuf.append(constructPropertyPanel.getAverageAbstracCompoundConceptGroupSiblingConceptCnt());
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
            sqlbuf.append("','");
            sqlbuf.append(projectComment);
            sqlbuf.append("')");
            stmt.executeUpdate(sqlbuf.toString());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void removeProject() {
        int response = JOptionPane.showConfirmDialog(this, "[id: " + projectID + " " + projectName + "] "
                + Translator.getTerm("RemoveProjectMessage"), Translator.getTerm("RemoveProjectDialog"),
                JOptionPane.OK_CANCEL_OPTION);
        if (response == JOptionPane.CANCEL_OPTION) { return; }
        DBManagerDialog.deleteTableContents(projectID, stmt, "general_ontology_info");
        DBManagerDialog.deleteTableContents(projectID, stmt, "doc_info");
        DBManagerDialog.deleteTableContents(projectID, stmt, "term_info");
        DBManagerDialog.deleteTableContents(projectID, stmt, "term_info_pos_list");
        DBManagerDialog.deleteTableContents(projectID, stmt, "term_info_doc_list");
        DBManagerDialog.deleteTableContents(projectID, stmt, "removed_term_info");
        DBManagerDialog.deleteTableContents(projectID, stmt, "removed_term_info_pos_list");
        DBManagerDialog.deleteTableContents(projectID, stmt, "removed_term_info_doc_list");
        DBManagerDialog.deleteTableContents(projectID, stmt, "input_term_set");
        DBManagerDialog.deleteTableContents(projectID, stmt, "term_eval_concept_set");
        DBManagerDialog.deleteTableContents(projectID, stmt, "eval_concept_set");
        DBManagerDialog.deleteTableContents(projectID, stmt, "input_term_concept_map");
        DBManagerDialog.deleteTableContents(projectID, stmt, "input_term_construct_tree_option");
        DBManagerDialog.deleteTableContents(projectID, stmt, "input_concept_set");
        DBManagerDialog.deleteTableContents(projectID, stmt, "construct_tree_option");
        DBManagerDialog.deleteTableContents(projectID, stmt, "undefined_term_set");
        saveOntologyAction.removeOWLOntology(projectID, icon);
        DBManagerDialog.deleteTableContents(projectID, stmt, "class_trimmed_result_analysis");
        DBManagerDialog.deleteTableContents(projectID, stmt, "trimmed_class_list");
        DBManagerDialog.deleteTableContents(projectID, stmt, "property_trimmed_result_analysis");
        DBManagerDialog.deleteTableContents(projectID, stmt, "trimmed_property_list");
        DBManagerDialog.deleteTableContents(projectID, stmt, "concept_definition_parameter");
        DBManagerDialog.deleteTableContents(projectID, stmt, "concept_definition");
        DBManagerDialog.deleteTableContents(projectID, stmt, "wrong_pair");
        DBManagerDialog.deleteTableContents(projectID, stmt, "wordspace_result");
        DBManagerDialog.deleteTableContents(projectID, stmt, "apriori_result");
        DBManagerDialog.deleteTableContents(projectID, stmt, "project_info");
        connectDB();
    }

    class UpdateProjectWorker extends SwingWorker implements java.beans.PropertyChangeListener {

        private int currentTaskCnt;

        public UpdateProjectWorker() {
            currentTaskCnt = 0;
            addPropertyChangeListener(this);
        }

        @Override
        protected Object doInBackground() throws Exception {
            DODDLE.STATUS_BAR.setLastMessage(projectName);
            try {
                DODDLEProject project = DODDLE.getCurrentProject();
                project.setTitle(projectName);
                ReferenceOntologySelectionPanel ontSelectionPanel = project.getOntologySelectionPanel();
                InputDocumentSelectionPanel docSelectionPanel = project.getDocumentSelectionPanel();
                InputTermSelectionPanel inputTermSelectionPanel = project.getInputTermSelectionPanel();
                InputConceptSelectionPanel inputConceptSelectionPanel = project.getInputConceptSelectionPanel();
                ConceptDefinitionPanel conceptDefinitionPanel = project.getConceptDefinitionPanel();

                saveProjectInfo(projectID, project);
                setProgress(currentTaskCnt++);

                if (projectItemSelectionPanel.isSaveEnable(GENERAL_ONTOLOGY_INFO)) {
                    ontSelectionPanel.saveGeneralOntologyInfoToDB(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(DOCUMENT_INFO)) {
                    docSelectionPanel.saveDocumentInfo(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(TERM_INFO_TABLE)) {
                    inputTermSelectionPanel.saveTermInfoTable(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(INPUT_TERM_SET)) {
                    inputConceptSelectionPanel.saveInputTermSetToDB(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(TERM_EVAL_CONCEPT_SET)) {
                    inputConceptSelectionPanel.saveTermEvalConceptSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(TERM_CORRESPOND_CONCEPT_SET)) {
                    inputConceptSelectionPanel.saveTermCorrespondConceptSetMapToDB(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(INPUT_TERM_CONSTRUCT_TREE_OPTION)) {
                    inputConceptSelectionPanel.saveInputTermConstructTreeOptionSetToDB(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(INPUT_CONCEPT_SET)) {
                    inputConceptSelectionPanel.saveInputConceptSetToDB(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(CONSTRUCT_TREE_OPTION)) {
                    inputConceptSelectionPanel.saveConstructTreeOptionToDB(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(UNDEFINED_TERM_SET)) {
                    inputConceptSelectionPanel.saveUndefinedTermSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(TAXONOMY)) {
                    saveOntologyAction.saveOWLOntology(projectID, icon, project);
                }
                setProgress(currentTaskCnt++);

                if (projectItemSelectionPanel.isSaveEnable(CLASS_TRIMMED_RESULT_ANALYSIS)) {
                    project.getConstructClassPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(
                            projectID, stmt, "class_trimmed_result_analysis", "trimmed_class_list");
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(PROPERTY_TRIMMED_RESULT_ANALYSIS)) {
                    project.getConstructPropertyPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(
                            projectID, stmt, "property_trimmed_result_analysis", "trimmed_property_list");
                }
                setProgress(currentTaskCnt++);

                if (projectItemSelectionPanel.isSaveEnable(CONCEPT_DEFINITION_PARAMETERS)) {
                    conceptDefinitionPanel.saveConeptDefinitionParameters(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(CONCEPT_DEFINITION)) {
                    conceptDefinitionPanel.saveConceptDefinition(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(WRONG_CONCEPT_PAIRS)) {
                    conceptDefinitionPanel.saveWrongPairSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(WORDSPACE_RESULT)) {
                    conceptDefinitionPanel.saveWordSpaceResult(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isSaveEnable(APRIORI_RESULT)) {
                    conceptDefinitionPanel.saveAprioriResult(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DODDLE.STATUS_BAR.unLock();
                DODDLE.STATUS_BAR.hideProgressBar();
                connectDB();
            }

            return "done";
        }

        @Override
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE.STATUS_BAR.setValue(currentTaskCnt);
            }
        }

    }

    public void updateProject() {
        projectItemSelectionPanel = new ProjectItemSelectionPanel("[id: " + projectID + " " + projectName + "] "
                + Translator.getTerm("UpdateProjectAction"));
        if (!projectItemSelectionPanel.isOK()) { return; }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("UpdateProjectAction"));
                DODDLE.STATUS_BAR.startTime();
                DODDLE.STATUS_BAR.initNormal(19);
                DODDLE.STATUS_BAR.lock();
                UpdateProjectWorker worker = new UpdateProjectWorker();
                DODDLE.STATUS_BAR.setSwingWorker(worker);
                worker.execute();
            }
        });
    }

    public void newProject() {
        connectDB(); // 他の人がプロジェクトを追加している場合にlastProjectIDがかぶる場合があるため
        NewProjectDialog newProjectPanel = new NewProjectDialog();
        if (newProjectPanel.isNewProject()) {
            author = newProjectPanel.getAuthor();
            projectName = newProjectPanel.getProjectName();
            projectComment = newProjectPanel.getComment();
            projectID = lastProjectID + 1;
            updateProject();
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

    class OpenProjectWorker extends SwingWorker implements java.beans.PropertyChangeListener {

        private int currentTaskCnt;

        public OpenProjectWorker(int taskCnt) {
            currentTaskCnt = taskCnt;
            addPropertyChangeListener(this);
        }

        @Override
        protected Object doInBackground() throws Exception {
            while (!currentProject.isInitialized()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenProjectAction") + ": " + projectName);
            setProgress(currentTaskCnt++);

            try {
                currentProject.setVisible(false);
                currentProject.setTitle(projectName);
                ReferenceOntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
                InputConceptSelectionPanel inputConceptSelectionPanel = currentProject.getInputConceptSelectionPanel();
                InputDocumentSelectionPanel docSelectionPanel = currentProject.getDocumentSelectionPanel();
                InputTermSelectionPanel inputTermSelectionPanel = currentProject.getInputTermSelectionPanel();
                ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();

                loadBaseURI(projectID);
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(DOCUMENT_INFO)) {
                    docSelectionPanel.loadDocuments(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(GENERAL_ONTOLOGY_INFO)) {
                    ontSelectionPanel.loadGeneralOntologyInfo(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                // ontSelectionPanel.loadOWLMetaDataSet(new File(openDir,
                // ProjectFileNames.OWL_META_DATA_SET_DIR));

                if (projectItemSelectionPanel.isOpenEnable(TERM_INFO_TABLE)) {
                    inputTermSelectionPanel.loadInputTermInfoTable(projectID, stmt, docSelectionPanel.getDocNum());
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(INPUT_TERM_SET)) {
                    inputConceptSelectionPanel.loadInputTermSet(projectID, stmt, currentTaskCnt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(TERM_EVAL_CONCEPT_SET)) {
                    DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenInputConceptSelectionResultAction"));
                    inputConceptSelectionPanel.loadTermEvalConceptSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(TERM_CORRESPOND_CONCEPT_SET)) {
                    DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenInputTermConceptMapAction"));
                    inputConceptSelectionPanel.loadTermCorrespondConceptSetMap(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(CONSTRUCT_TREE_OPTION)) {
                    inputConceptSelectionPanel.loadConstructTreeOption(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(INPUT_TERM_CONSTRUCT_TREE_OPTION)) {
                    inputConceptSelectionPanel.loadInputTermConstructTreeOptionSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(INPUT_CONCEPT_SET)) {
                    DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenInputConceptSetAction"));
                    inputConceptSelectionPanel.loadInputConceptSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(UNDEFINED_TERM_SET)) {
                    inputConceptSelectionPanel.loadUndefinedTermSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(TAXONOMY)) {
                    DODDLE.STATUS_BAR.printMessage(Translator.getTerm("OpenOWLOntologyAction"));
                    loadOntologyAction.loadOWLOntology(projectID, icon, currentProject);
                }
                setProgress(currentTaskCnt++);

                ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
                ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
                if (projectItemSelectionPanel.isOpenEnable(CLASS_TRIMMED_RESULT_ANALYSIS)) {
                    constructClassPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(projectID, stmt,
                            "class_trimmed_result_analysis", "trimmed_class_list");
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(PROPERTY_TRIMMED_RESULT_ANALYSIS)) {
                    constructPropertyPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(projectID, stmt,
                            "property_trimmed_result_analysis", "trimmed_property_list");
                }
                setProgress(currentTaskCnt++);

                conceptDefinitionPanel.setInputDocList();
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(CONCEPT_DEFINITION_PARAMETERS)) {
                    conceptDefinitionPanel.loadConceptDefinitionParameters(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(WORDSPACE_RESULT)) {
                    conceptDefinitionPanel.loadWordSpaceResult(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(APRIORI_RESULT)) {
                    conceptDefinitionPanel.loadAprioriResult(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(CONCEPT_DEFINITION)) {
                    conceptDefinitionPanel.loadConceptDefinition(projectID, stmt);
                }
                setProgress(currentTaskCnt++);
                if (projectItemSelectionPanel.isOpenEnable(WRONG_CONCEPT_PAIRS)) {
                    conceptDefinitionPanel.loadWrongPairSet(projectID, stmt);
                }
                setProgress(currentTaskCnt++);

                inputConceptSelectionPanel.selectTopList();
                constructClassPanel.expandIsaTree();
                constructClassPanel.expandHasaTree();
                constructPropertyPanel.expandIsaTree();
            } finally {
                try {
                    currentProject.setXGALayout();
                    currentProject.setVisible(true);
                    currentProject.setMaximum(true);
                } catch (java.beans.PropertyVetoException pve) {
                    pve.printStackTrace();
                }
                setProgress(currentTaskCnt++);
                DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("OpenProjectDoneMessage"));
                DODDLE.STATUS_BAR.unLock();
                DODDLE.STATUS_BAR.hideProgressBar();
            }

            return "done";
        }

        @Override
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getNewValue() instanceof Integer) {
                DODDLE.STATUS_BAR.setValue(currentTaskCnt);
            }
        }

    }

    public void openProject() {
        projectItemSelectionPanel = new ProjectItemSelectionPanel("[id: " + projectID + " " + projectName + "] "
                + Translator.getTerm("OpenProjectAction"));
        if (!projectItemSelectionPanel.isOK()) { return; }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                OpenProjectWorker worker = null;
                if (DODDLE.isExistingCurrentProject()) {
                    currentProject = DODDLE.getCurrentProject();
                    DODDLE.STATUS_BAR.setLastMessage(Translator.getTerm("OpenProjectAction"));
                    DODDLE.STATUS_BAR.startTime();
                    DODDLE.STATUS_BAR.initNormal(22);
                    DODDLE.STATUS_BAR.lock();
                    worker = new OpenProjectWorker(0);
                } else {
                    currentProject = new DODDLEProject("new", 33);
                    worker = new OpenProjectWorker(11);
                }
                DODDLE.STATUS_BAR.setSwingWorker(worker);
                worker.execute();
            }
        });
    }

    public void editProjectInfo() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String modificationDate = dateFormat.format(Calendar.getInstance().getTime());
            String sql = "UPDATE project_info SET Project_Name='" + projectNameField.getText() + "',Author='"
                    + authorField.getText() + "',Modification_Date='" + modificationDate + "',Comment='"
                    + commentArea.getText() + "' WHERE Project_ID=" + projectID;
            stmt.executeUpdate(sql);
            connectDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        int selectedRow = projectInfoTable.getSelectedRow();
        if (selectedRow != -1) {
            projectID = (Integer) projectInfoTable.getValueAt(selectedRow, getColumnNamePosition("id"));
            projectName = (String) projectInfoTable.getValueAt(selectedRow, getColumnNamePosition(Translator
                    .getTerm("ProjectNameLabel")));
            author = (String) projectInfoTable.getValueAt(selectedRow, getColumnNamePosition(Translator
                    .getTerm("AuthorLabel")));
            projectComment = commentArea.getText();
        }
        if (e.getSource() == openProjectButton) {
            openProject();
        } else if (e.getSource() == updateProjectButton) {
            updateProject();
        } else if (e.getSource() == newProjectButton) {
            newProject();
        } else if (e.getSource() == removeProjectButton) {
            removeProject();
        } else if (e.getSource() == connectButton) {
            connectDB();
        } else if (e.getSource() == editCommentButton) {
            editProjectInfo();
        }
    }

    private int getColumnNamePosition(String columnName) {
        for (int i = 0; i < projectInfoTable.getColumnCount(); i++) {
            if (projectInfoTable.getColumnName(i).equals(columnName)) { return i; }
        }
        return 0;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedRow = projectInfoTable.getSelectedRow();
        if (selectedRow != -1) {
            // projectInfoTableModelとすると，列をソートした状態でプロジェクトを開いたり，更新したり，削除することがうまくできない
            projectID = (Integer) projectInfoTable.getValueAt(selectedRow, getColumnNamePosition("id"));
        }
        try {
            String sql = "SELECT * from project_info WHERE Project_ID=" + projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                projectNameField.setText(rs.getString("Project_Name"));
                authorField.setText(rs.getString("Author"));
                commentArea.setText(rs.getString("Comment"));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public class NewProjectDialog extends JDialog implements ActionListener {
        private JLabel projectNameLabel;
        private JTextField projectNameField;
        private JLabel authorLabel;
        private JTextField authorField;
        private JTextArea commentArea;

        private JButton okButton;
        private JButton cancelButton;

        private boolean isNewProject;

        public NewProjectDialog() {
            super(DODDLE.rootFrame, "[id: " + (lastProjectID + 1) + "] " + Translator.getTerm("NewProjectDialog"), true);
            projectNameLabel = new JLabel(Translator.getTerm("ProjectNameLabel"));
            projectNameField = new JTextField(20);
            authorLabel = new JLabel(Translator.getTerm("AuthorLabel"));
            authorField = new JTextField(20);
            commentArea = new JTextArea();
            JScrollPane commentAreaScroll = new JScrollPane(commentArea);
            commentAreaScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("ProjectCommentLabel")));

            JPanel northPanel = new JPanel();
            northPanel.setLayout(new GridLayout(2, 2));
            northPanel.add(projectNameLabel);
            northPanel.add(projectNameField);
            northPanel.add(authorLabel);
            northPanel.add(authorField);

            okButton = new JButton(Translator.getTerm("OKButton"));
            okButton.addActionListener(this);
            cancelButton = new JButton(Translator.getTerm("CancelButton"));
            cancelButton.addActionListener(this);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            JPanel southPanel = new JPanel();
            southPanel.setLayout(new BorderLayout());
            southPanel.add(buttonPanel, BorderLayout.EAST);

            getContentPane().add(northPanel, BorderLayout.NORTH);
            getContentPane().add(commentAreaScroll, BorderLayout.CENTER);
            getContentPane().add(southPanel, BorderLayout.SOUTH);
            setSize(400, 250);
            setLocationRelativeTo(DODDLE.rootFrame);
            setVisible(true);
        }

        public String getAuthor() {
            return authorField.getText();
        }

        public String getProjectName() {
            return projectNameField.getText();
        }

        public String getComment() {
            return commentArea.getText();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
                isNewProject = true;
                setVisible(false);
            } else if (e.getSource() == cancelButton) {
                isNewProject = false;
                setVisible(false);
            }
        }

        public boolean isNewProject() {
            return isNewProject;
        }
    }

    class ProjectItemSelectionPanel extends JDialog implements ActionListener {

        private boolean isOK;
        private String[] projectItemNames;

        private Map<String, Boolean> projectItemEnableMap;
        private JCheckBox[] projectItemButtonList;
        private JButton selectAllButton;
        private JButton releaseAllButton;
        private JButton okButton;
        private JButton cancelButton;

        public ProjectItemSelectionPanel(String title) {
            super(DODDLE.rootFrame, title, true);
            projectItemNames = new String[] { GENERAL_ONTOLOGY_INFO, DOCUMENT_INFO, TERM_INFO_TABLE, INPUT_TERM_SET,
                    TERM_EVAL_CONCEPT_SET, TERM_CORRESPOND_CONCEPT_SET, INPUT_TERM_CONSTRUCT_TREE_OPTION,
                    INPUT_CONCEPT_SET, CONSTRUCT_TREE_OPTION, UNDEFINED_TERM_SET, TAXONOMY,
                    CLASS_TRIMMED_RESULT_ANALYSIS, PROPERTY_TRIMMED_RESULT_ANALYSIS, CONCEPT_DEFINITION_PARAMETERS,
                    WORDSPACE_RESULT, APRIORI_RESULT, CONCEPT_DEFINITION, WRONG_CONCEPT_PAIRS};
            projectItemEnableMap = new HashMap<String, Boolean>();
            JPanel projectItemListPanel = new JPanel();
            projectItemListPanel.setLayout(new GridLayout(projectItemNames.length, 1));
            projectItemButtonList = new JCheckBox[projectItemNames.length];
            for (int i = 0; i < projectItemButtonList.length; i++) {
                projectItemButtonList[i] = new JCheckBox(projectItemNames[i], true);
                projectItemButtonList[i].addActionListener(this);
                projectItemEnableMap.put(projectItemNames[i], true);
                projectItemListPanel.add(projectItemButtonList[i]);
            }
            selectAllButton = new JButton(Translator.getTerm("SelectAllButton"));
            selectAllButton.addActionListener(this);
            releaseAllButton = new JButton(Translator.getTerm("ReleaseAllButton"));
            releaseAllButton.addActionListener(this);
            okButton = new JButton(Translator.getTerm("OKButton"));
            okButton.addActionListener(this);
            cancelButton = new JButton(Translator.getTerm("CancelButton"));
            cancelButton.addActionListener(this);
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(selectAllButton);
            buttonPanel.add(releaseAllButton);
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            getContentPane().add(new JScrollPane(projectItemListPanel), BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            setSize(400, 500);
            setLocationRelativeTo(DODDLE.rootFrame);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            for (int i = 0; i < projectItemButtonList.length; i++) {
                if (e.getSource() == projectItemButtonList[i]) {
                    projectItemEnableMap.put(projectItemButtonList[i].getText(), projectItemButtonList[i].isSelected());
                }
            }

            if (e.getSource() == selectAllButton) {
                for (int i = 0; i < projectItemButtonList.length; i++) {
                    projectItemButtonList[i].setSelected(true);
                    projectItemEnableMap.put(projectItemButtonList[i].getText(), true);
                }
            } else if (e.getSource() == releaseAllButton) {
                for (int i = 0; i < projectItemButtonList.length; i++) {
                    projectItemButtonList[i].setSelected(false);
                    projectItemEnableMap.put(projectItemButtonList[i].getText(), false);
                }
            } else if (e.getSource() == okButton) {
                isOK = true;
                setVisible(false);
            } else if (e.getSource() == cancelButton) {
                isOK = false;
                setVisible(false);
            }
        }

        public boolean isOpenEnable(String projectItem) {
            // System.out.println(projectItemEnableMap.get(projectItem) + ":" +
            // projectItem);
            return projectItemEnableMap.get(projectItem);
        }

        public boolean isSaveEnable(String projectItem) {
            // System.out.println(projectItemEnableMap.get(projectItem) + ":" +
            // projectItem);
            return projectItemEnableMap.get(projectItem);
        }

        public boolean isOK() {
            return isOK;
        }

    }

}
