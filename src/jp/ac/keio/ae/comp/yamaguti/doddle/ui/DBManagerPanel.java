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

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.actions.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

import com.hp.hpl.jena.db.*;

/**
 * @author takeshi morita
 */
public class DBManagerPanel extends JDialog implements ActionListener {

    private String host;
    private String projectName;
    private String userName;
    private String passWord;

    private Connection con;
    private IDBConnection icon;
    private Statement stmt;

    private JButton openDBButton;
    private JButton saveDBButton;
    private JButton removeDBButton;

    private SaveOntologyAction saveOntologyAction;
    private LoadOntologyAction loadOntologyAction;

    public DBManagerPanel() {
        connectDB();
        openDBButton = new JButton("Open DB");
        openDBButton.addActionListener(this);
        saveDBButton = new JButton("Save DB");
        saveDBButton.addActionListener(this);
        removeDBButton = new JButton("Remove DB");
        removeDBButton.addActionListener(this);

        saveOntologyAction = new SaveOntologyAction("", SaveOntologyAction.OWL_ONTOLOGY);
        loadOntologyAction = new LoadOntologyAction("", LoadOntologyAction.OWL_ONTOLOGY);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openDBButton);
        buttonPanel.add(saveDBButton);
        buttonPanel.add(removeDBButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setSize(new Dimension(800, 600));
    }

    public void connectDB() {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            projectName = "sample project";
            host = "zest.comp.ae.keio.ac.jp";
            String dbName = "doddle";
            String url = "jdbc:mysql://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
            userName = "t_morita";
            passWord = "t_morita.pass";
            con = DriverManager.getConnection(url, userName, passWord);
            icon = new DBConnection(url, userName, passWord, "MySQL");
            stmt = con.createStatement();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            stmt.close();
            con.close();
            icon.close();
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

    public void removeDB(int projectID) {
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
        System.out.println("remove project: "+ projectID);
    }
    
    public void saveDB(int projectID) {
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

    public void openDB(int projectID) {
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
        if (e.getSource() == openDBButton) {
            openDB(1);
        } else if (e.getSource() == saveDBButton) {
            saveDB(1);
        } else if (e.getSource() == removeDBButton) {
            removeDB(1);
        }
    }

}
