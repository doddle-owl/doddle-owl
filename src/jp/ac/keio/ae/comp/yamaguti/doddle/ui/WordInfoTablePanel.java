/*
 * @(#)  2006/11/29
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.Map.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.table.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class WordInfoTablePanel extends JPanel implements ActionListener, KeyListener {

    private int docNum;
    private Map<String, WordInfo> wordInfoMap;

    private JTextField searchWordField;
    private JTextField searchPOSField;
    private JTable wordInfoTable;
    private TableRowSorter<TableModel> rowSorter;
    private WordInfoTableModel wordInfoTableModel;

    public WordInfoTablePanel() {
        searchWordField = new JTextField(20);
        searchWordField.addActionListener(this);
        searchWordField.addKeyListener(this);
        searchWordField.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("WordFilterTextField")));
        searchPOSField = new JTextField(20);
        searchPOSField.addActionListener(this);
        searchPOSField.addKeyListener(this);
        searchPOSField.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("POSFilterTextField")));
        JPanel searchPanel = new JPanel();
        searchPanel.add(searchWordField);
        searchPanel.add(searchPOSField);

        wordInfoTable = new JTable();
        JScrollPane wordInfoTableScroll = new JScrollPane(wordInfoTable);
        setWordInfoTableModel(null, 0);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(wordInfoTableScroll, BorderLayout.CENTER);
    }

    public TableModel getTableModel() {
        return wordInfoTable.getModel();
    }

    public JTable getTable() {
        return wordInfoTable;
    }

    public int getTableSize() {
        return wordInfoMap.size();
    }

    public WordInfo getWordInfo(String word) {
        return wordInfoMap.get(word);
    }

    public void addWordInfoMapKey(String addWord, WordInfo info) {
        wordInfoMap.put(addWord, info);
    }

    public void removeWordInfoMapKey(String deleteWord) {
        wordInfoMap.remove(deleteWord);
    }

    public JTable getWordInfoTable() {
        return wordInfoTable;
    }

    public Collection<WordInfo> getWordInfoSet() {
        return wordInfoMap.values();
    }

    public void setWordInfoTableModel(Map<String, WordInfo> wiMap, int dn) {
        docNum = dn;
        String WORD = Translator.getTerm("WordLabel");
        String POS = Translator.getTerm("POSLabel");
        String TF = Translator.getTerm("TFLabel");
        String IDF = Translator.getTerm("IDFLabel");
        String TFIDF = Translator.getTerm("TFIDFLabel");
        String INPUT_DOCUMENT = Translator.getTerm("InputDocumentLabel");
        String UPPER_CONCEPT = Translator.getTerm("UpperConceptLabel");
        Object[] titles = new Object[] { WORD, POS, TF, IDF, TFIDF, INPUT_DOCUMENT, UPPER_CONCEPT};

        wordInfoTableModel = new WordInfoTableModel(null, titles);
        wordInfoTableModel.getColumnClass(0);
        rowSorter = new TableRowSorter<TableModel>(wordInfoTableModel);
        rowSorter.setMaxSortKeys(5);

        wordInfoTable.setRowSorter(rowSorter);
        wordInfoTable.setModel(wordInfoTableModel);
        wordInfoTable.getTableHeader().setToolTipText("sorted by column");
        wordInfoMap = wiMap;
        if (wordInfoMap == null) { return; }
        Collection<WordInfo> wordInfoSet = wordInfoMap.values();
        for (WordInfo info : wordInfoSet) {
            Vector rowData = info.getRowData();
            wordInfoTableModel.addRow(rowData);
        }
    }

    public void loadWordInfoTable(File loadFile) {
        wordInfoMap = new HashMap<String, WordInfo>();
        if (!loadFile.exists()) { return; }
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(loadFile);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line = reader.readLine();
            docNum = new Integer(line.split("=")[1]).intValue();
            while ((line = reader.readLine()) != null) {
                String[] items = line.split("\t");
                String word = items[0];
                WordInfo info = new WordInfo(word, docNum);
                String[] posSet = items[1].split(":");
                for (int i = 0; i < posSet.length; i++) {
                    info.addPos(posSet[i]);
                }
                try {
                    // 以下，docsを処理する場合には，5, 6, 7を一つずつインクリメントする必要あり
                    if (5 < items.length) {
                        String[] inputDocSet = items[5].split(":");
                        for (int i = 0; i < inputDocSet.length; i++) {
                            String inputDoc = inputDocSet[i].split("=")[0];
                            Integer num = new Integer(inputDocSet[i].split("=")[1]);
                            info.putInputDoc(new File(inputDoc), num);
                        }
                    }
                    if (items.length == 7) {
                        String[] upperConceptSet = items[6].split(":");
                        for (int i = 0; i < upperConceptSet.length; i++) {
                            info.addUpperConcept(upperConceptSet[i]);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException aiobe) {
                    // 無視
                }
                wordInfoMap.put(word, info);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
        setWordInfoTableModel(wordInfoMap, docNum);
    }
    
    public void loadWordInfoTable(int projectID, Statement stmt, String wordTable, int docNum) {
        String posTable = wordTable+"_pos_list";
        String docTable = wordTable+"_doc_list";
        wordInfoMap = new HashMap<String, WordInfo>();
        Map<Integer, WordInfo> posListIDWordInfoMap = new HashMap<Integer, WordInfo>();
        Map<Integer, WordInfo> docListIDWordInfoMap = new HashMap<Integer, WordInfo>();
        try {
            String sql = "SELECT * from "+ wordTable +" where Project_ID="+projectID;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String  term = rs.getString("Term");
                int  posListID = rs.getInt("POS_List_ID");
                int tf = rs.getInt("TF");
                double idf = rs.getDouble("IDF");
                double tfidf = rs.getDouble("TF_IDF");
                int  docListID = rs.getInt("DOC_List_ID");
                this.docNum = docNum;
                WordInfo info = new WordInfo(term, docNum);
                
                posListIDWordInfoMap.put(posListID, info);
                docListIDWordInfoMap.put(docListID, info);
                                
                wordInfoMap.put(term, info);
            }
            
            for(Entry<Integer, WordInfo> entry: posListIDWordInfoMap.entrySet()) {
                int posListID = entry.getKey();
                WordInfo info = entry.getValue();
                sql = "SELECT * from "+ posTable +" where Project_ID="+projectID + " and POS_List_ID="+posListID;
                ResultSet rs1 = stmt.executeQuery(sql);
                while (rs1.next()) {
                    String  pos = rs1.getString("POS");
                    info.addPos(pos);
                }
            }
            
            for(Entry<Integer, WordInfo> entry: posListIDWordInfoMap.entrySet()) {
                int docListID = entry.getKey();
                WordInfo info = entry.getValue();
                sql = "SELECT * from "+ docTable +" where Project_ID="+projectID + " and Doc_List_ID="+docListID;
                ResultSet rs2 = stmt.executeQuery(sql);
                while (rs2.next()) {
                    String  doc = URLDecoder.decode(rs2.getString("Doc"), "UTF8");
                    int docTF = rs2.getInt("TF");
                    info.putInputDoc(new File(doc), docTF);
                }
            }
        } catch (SQLException e) {
           e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } 
        setWordInfoTableModel(wordInfoMap, docNum);
    }

    public void saveWordInfoTable(File saveFile) {
        if (wordInfoMap == null) { return; }
        BufferedWriter writer = null;
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.write("docNum=" + docNum + "\n");
            for (WordInfo info : wordInfoMap.values()) {
                writer.write(info.toString() + "\n");
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }
    }
    
    public void saveWordInfoTable(int projectID, Statement stmt, String wordTable) {
        String posTable = wordTable+"_pos_list";
        String docTable = wordTable+"_doc_list";
        DBManagerPanel.deleteTableContents(projectID, stmt, wordTable);
        DBManagerPanel.deleteTableContents(projectID, stmt, posTable);
        DBManagerPanel.deleteTableContents(projectID, stmt, docTable);
        if (wordInfoMap == null) { return; }        
        try {
            int posAndDocListID = 1;
            for (WordInfo info: wordInfoMap.values()) {
                String term = info.getWord();
                Set<String> posSet = info.getPosSet();
                int tf = info.getTF();
                double idf = info.getIDF();
                double tfidf = info.getTFIDF();
                Set<File> docSet = info.getInputDocumentSet();
                                
                String sql = "INSERT INTO "+ wordTable +" (Project_ID,Term,POS_List_ID,TF,IDF,TF_IDF,Doc_List_ID) " +
                "VALUES(" + projectID + ",'"+ term + "',"+ posAndDocListID +","+ tf + "," + idf + "," + tfidf + "," + posAndDocListID + ")";
                stmt.executeUpdate(sql);
                for (String pos: posSet) {
                    sql = "INSERT INTO "+ posTable +" (Project_ID,POS_List_ID,POS) VALUES(" + projectID + ","+ posAndDocListID +",'"+ pos + "')";
                    stmt.executeUpdate(sql);
                }
                for (File doc: docSet) {
                    int inputDocTF = info.getInputDocumentTF(doc);
                    String docPath = URLEncoder.encode(doc.getAbsolutePath(), "UTF8"); 
                    sql = "INSERT INTO "+ docTable +" (Project_ID,Doc_List_ID,Doc,TF) VALUES(" + projectID + ","+ posAndDocListID +",'"+ 
                    docPath  +"'," +  inputDocTF + ")";
                    stmt.executeUpdate(sql);
                }
                posAndDocListID++;
            }
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }        
    }

    class WordInfoTableModel extends DefaultTableModel {

        WordInfoTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        public Class< ? > getColumnClass(int columnIndex) {
            String columnName = getColumnName(columnIndex);
            if (columnName.equals("TF")) {
                return Integer.class;
            } else if (columnName.equals("IDF") || columnName.equals("TF-IDF")) {
                return Double.class;
            } else {
                return String.class;
            }
        }
    }

    public void loadWordInfoTable() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadWordInfoTable(file);
        }
    }

    public void saveWordInfoTable() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showSaveDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            saveWordInfoTable(file);
        }
    }

    public void actionPerformed(ActionEvent e) {
        searchWordOrPOS(e);
    }

    /**
     * @param e
     */
    private void searchWordOrPOS(EventObject e) {
        if (e.getSource() == searchWordField || e.getSource() == searchPOSField) {
            try {
                if (searchWordField.getText().length() == 0 && searchPOSField.getText().length() == 0) {
                    rowSorter.setRowFilter(RowFilter.regexFilter(".*", new int[] { 0}));
                }
                if (searchWordField.getText().length() != 0) {
                    rowSorter.setRowFilter(RowFilter.regexFilter(searchWordField.getText(), new int[] { 0}));
                }
                if (searchPOSField.getText().length() != 0) {
                    rowSorter.setRowFilter(RowFilter.regexFilter(searchPOSField.getText(), new int[] { 1}));
                }
            } catch (PatternSyntaxException pse) {
                pse.printStackTrace();
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        searchWordOrPOS(e);
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
