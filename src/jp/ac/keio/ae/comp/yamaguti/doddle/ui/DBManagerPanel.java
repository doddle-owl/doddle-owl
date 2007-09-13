/*
 * @(#)  2007/09/13
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class DBManagerPanel extends JDialog implements ActionListener {

    private JButton openDBButton;
    private JButton saveDBButton;

    public DBManagerPanel() {
        openDBButton = new JButton("OpenDB");
        openDBButton.addActionListener(this);
        saveDBButton = new JButton("SaveDB");
        saveDBButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openDBButton);
        buttonPanel.add(saveDBButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setSize(new Dimension(800, 600));
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

    public void saveDB(int projectID) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            String host = "zest.comp.ae.keio.ac.jp";
            String dbName = "doddle";
            String url = "jdbc:mysql://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=UTF-8";
            String userName = "t_morita";
            String passWord = "t_morita.pass";
            Connection con = DriverManager.getConnection(url, userName, passWord);
            Statement stmt = con.createStatement();

            DODDLEProject project = DODDLE.getCurrentProject();
            OntologySelectionPanel ontSelectionPanel = project.getOntologySelectionPanel();
            DisambiguationPanel disambiguationPanel = project.getDisambiguationPanel();

            ontSelectionPanel.saveGeneralOntologyInfoToDB(projectID, stmt);
            disambiguationPanel.saveInputWordSetToDB(projectID, stmt);
            disambiguationPanel.saveWordCorrespondConceptSetMapToDB(projectID, stmt);
            disambiguationPanel.saveInputWordConstructTreeOptionSetToDB(projectID, stmt);
            disambiguationPanel.saveInputConceptSetToDB(projectID, stmt);
            disambiguationPanel.saveConstructTreeOptionToDB(projectID, stmt);

            String sql = "SELECT * from general_ontology_info";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Project_ID");
                String edr = rs.getString("EDR_General");
                String edrt = rs.getString("EDR_Technical");
                String wn = rs.getString("WordNet");
                System.out.println(id + " " + edr + " " + edrt + " " + wn);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDB(int projectID) {
        System.out.println("open project");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openDBButton) {
            openDB(1);
        } else if (e.getSource() == saveDBButton) {
            saveDB(1);
        }
    }

}
