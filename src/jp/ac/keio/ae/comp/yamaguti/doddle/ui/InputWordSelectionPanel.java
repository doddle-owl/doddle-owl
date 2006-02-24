package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class InputWordSelectionPanel extends JPanel implements ActionListener {

    private JTable wordInfoTable;
    private TableSorter wordInfoTableModel;

    private JTextArea inputWordArea;

    private int docNum;
    private Map wordInfoMap;

    private TitledBorder wordInfoTableBorder;

    private JButton addInputWordButton;
    private JButton deleteTableItemButton;
    private JButton setInputWordSetButton;

    private InputModuleUI inputModuleUI;

    public static String UPPER_CONCEPT_LIST = "./upperConceptList.txt";

    public InputWordSelectionPanel(InputModuleUI ui) {
        System.setProperty("sen.home", DODDLE.DODDLE_DIC + "sen-1.2.1");
        inputModuleUI = ui;
        wordInfoTable = new JTable();
        JScrollPane wordInfoTableScroll = new JScrollPane(wordInfoTable);
        setWordInfoTableModel(null, 0);

        wordInfoTableBorder = BorderFactory.createTitledBorder("抽出単語テーブル");
        wordInfoTableScroll.setBorder(wordInfoTableBorder);

        inputWordArea = new JTextArea(10, 20);
        JScrollPane inputWordsAreaScroll = new JScrollPane(inputWordArea);
        inputWordsAreaScroll.setBorder(BorderFactory.createTitledBorder("入力単語リスト"));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(wordInfoTableScroll, BorderLayout.CENTER);
        centerPanel.add(inputWordsAreaScroll, BorderLayout.EAST);

        addInputWordButton = new JButton("入力単語に追加");
        addInputWordButton.addActionListener(this);
        deleteTableItemButton = new JButton("削除");
        deleteTableItemButton.addActionListener(this);
        JPanel tableButtonPanel = new JPanel();
        tableButtonPanel.add(addInputWordButton);
        tableButtonPanel.add(deleteTableItemButton);

        setInputWordSetButton = new JButton("入力語彙をセット");
        setInputWordSetButton.addActionListener(this);
        JPanel inputWordsButtonPanel = new JPanel();
        inputWordsButtonPanel.add(setInputWordSetButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(tableButtonPanel, BorderLayout.WEST);
        buttonPanel.add(inputWordsButtonPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadWordInfoTable() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showOpenDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadWordInfoTable(file);
        }
    }

    public void loadWordInfoTable(File loadFile) {
        if (!loadFile.exists()) { return; }
        wordInfoMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(loadFile));
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
                String[] docSet = items[5].split(":");
                for (int i = 0; i < docSet.length; i++) {
                    if (docSet[i].split("=").length != 2) {
                        continue;
                    }
                    String doc = docSet[i].split("=")[0];
                    Integer num = new Integer(docSet[i].split("=")[1]);
                    info.putDoc(new File(doc), num);
                }
                String[] inputDocSet = items[6].split(":");
                for (int i = 0; i < inputDocSet.length; i++) {
                    String inputDoc = inputDocSet[i].split("=")[0];
                    Integer num = new Integer(inputDocSet[i].split("=")[1]);
                    info.putInputDoc(new File(inputDoc), num);
                }
                if (items.length == 8) {
                    String[] upperConceptSet = items[7].split(":");
                    for (int i = 0; i < upperConceptSet.length; i++) {
                        info.addUpperConcept(upperConceptSet[i]);
                    }
                }
                wordInfoMap.put(word, info);
            }
            reader.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        setWordInfoTableModel(wordInfoMap, docNum);
    }

    public void saveWordInfoTable() {
        JFileChooser chooser = new JFileChooser(".");
        int retval = chooser.showSaveDialog(DODDLE.rootPane);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            saveWordInfoTable(file);
        }
    }

    public void saveWordInfoTable(File saveFile) {
        if (wordInfoMap == null) { return; }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
            writer.write("docNum=" + docNum + "\n");
            for (Iterator i = wordInfoMap.values().iterator(); i.hasNext();) {
                WordInfo info = (WordInfo) i.next();
                writer.write(info.toString() + "\n");
            }
            writer.close();
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public void setWordInfoTableModel(Map wiMap, int dn) {
        docNum = dn;
        Object[] titles = new Object[] { "単語", "品詞", "TF", "IDF", "TFIDF", "文書", "入力文書", "上位概念"};
        wordInfoTableModel = new TableSorter(new DefaultTableModel(null, titles));
        wordInfoTableModel.setTableHeader(wordInfoTable.getTableHeader());
        wordInfoTable.setModel(wordInfoTableModel);
        wordInfoTable.getTableHeader().setToolTipText("列でソートします．");
        wordInfoMap = wiMap;
        if (wordInfoMap == null) { return; }
        Collection wordInfoSet = wordInfoMap.values();
        for (Iterator i = wordInfoSet.iterator(); i.hasNext();) {
            WordInfo info = (WordInfo) i.next();
            DefaultTableModel model = (DefaultTableModel) wordInfoTableModel.getTableModel();
            model.addRow(info.getRowData());
        }
        wordInfoTableBorder.setTitle("抽出単語テーブル（" + wiMap.size() + "）");
    }
    private void setInputWordSet() {
        DODDLE.STATUS_BAR.setLastMessage("入力単語リストの読み込み完了");
        String[] inputWords = inputWordArea.getText().split("\n");
        Set inputWordSet = new HashSet(Arrays.asList(inputWords));
        inputModuleUI.loadInputWordSet(inputWordSet);
        DODDLE.setSelectedIndex(DODDLE.INPUT_MODULE);
    }

    private void addInputWords() {
        int[] rows = wordInfoTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            String word = (String) wordInfoTable.getValueAt(rows[i], 0);
            String inputWords = inputWordArea.getText() + word + "\n";
            inputWordArea.setText(inputWords);
        }
    }

    public Map<DefaultMutableTreeNode, String> getAbstractNodeLabelMap() {
        return inputModuleUI.getAbstractNodeLabelMap();
    }

    private void deleteTableItems() {
        int[] rows = wordInfoTable.getSelectedRows();
        Set deleteWordSet = new HashSet();
        for (int i = 0; i < rows.length; i++) {
            String deleteWord = (String) wordInfoTable.getValueAt(rows[i], 0);
            deleteWordSet.add(deleteWord);
            wordInfoMap.remove(deleteWord);
        }
        DefaultTableModel model = (DefaultTableModel) wordInfoTableModel.getTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String word = (String) model.getValueAt(i, 0);
            if (deleteWordSet.contains(word)) {
                model.removeRow(i);
                i = 0;
                continue;
            }
        }
        wordInfoTableBorder.setTitle("抽出単語テーブル（" + wordInfoMap.size() + "）");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == setInputWordSetButton) {
            setInputWordSet();
        } else if (e.getSource() == addInputWordButton) {
            addInputWords();
        } else if (e.getSource() == deleteTableItemButton) {
            deleteTableItems();
        }
    }
}
