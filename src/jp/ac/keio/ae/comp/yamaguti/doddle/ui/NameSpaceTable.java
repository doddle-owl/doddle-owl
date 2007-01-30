/*
 * @(#)  2006/12/15
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.Translator;

import org.semanticweb.mmm.mr3.data.*;
import org.semanticweb.mmm.mr3.jgraph.*;
import org.semanticweb.mmm.mr3.util.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author takeshi morita
 */
public class NameSpaceTable extends JPanel implements ActionListener, TableModelListener {

    private JTable nsTable;
    private NSTableModel nsTableModel;

    transient private JButton addNSButton;
    transient private JButton removeNSButton;
    transient private JButton cancelButton;
    transient private JTextField prefixField;
    transient private JTextField nsField;

    private Map<String, String> prefixNSMap;
    private Map<String, String> knownNSPrefixMap;
    private Map<String, PrefixNSInfo> nsInfoMap;
    private static final String WARNING = Translator.getString("Warning");

    public NameSpaceTable() {
        prefixNSMap = new HashMap<String, String>();
        nsInfoMap = new HashMap<String, PrefixNSInfo>();
        initTable();
        setLayout(new BorderLayout());
        setTableLayout();
        setInputLayout();
        initKnownPrefixNSMap();
        setDefaultNSPrefix();
    }

    private void initKnownPrefixNSMap() {
        knownNSPrefixMap = new HashMap<String, String>();
        knownNSPrefixMap.put("http://purl.org/dc/elements/1.1/", "dc");
        knownNSPrefixMap.put("http://purl.org/rss/1.0/", "rss");
        knownNSPrefixMap.put("http://xmlns.com/foaf/0.1/", "foaf");
        knownNSPrefixMap.put("http://www.w3.org/2002/07/owl#", "owl");
        knownNSPrefixMap.put("http://web.resource.org/cc/", "cc");
        knownNSPrefixMap.put("http://www2.nict.go.jp/kk/e416/EDR#", "edr");
        knownNSPrefixMap.put("http://www2.nict.go.jp/kk/e416/EDRT#", "edrt");
        knownNSPrefixMap.put("http://wordnet.princeton.edu/wn/2.0#", "wn20");
    }

    // baseURIがrdf, rdfs, mr3の場合があるため
    private void addDefaultNS(String prefix, String addNS) {
        if (!isValidPrefix(prefix)) {
            prefix = getMR3Prefix(addNS);
        }
        if (isValidNS(addNS)) {
            addNameSpaceTable(new Boolean(true), prefix, addNS);
        }
    }

    public void setDefaultNSPrefix() {
        addDefaultNS("base", DODDLE.BASE_URI);
        addDefaultNS("rdf", RDF.getURI());
        addDefaultNS("rdfs", RDFS.getURI());
        addDefaultNS("owl", OWL.NS);
        addDefaultNS("edr", "http://www2.nict.go.jp/kk/e416/EDR#");
        addDefaultNS("edrt", "http://www2.nict.go.jp/kk/e416/EDRT#");
        addDefaultNS("wn20", "http://wordnet.princeton.edu/wn/2.0#");
        setNSInfoMap();
    }

    private String getKnownPrefix(Model model, String ns) {
        String prefix = model.getNsURIPrefix(ns);
        // System.out.println(ns);
        // System.out.println(prefix);
        if (prefix != null && (!prefix.equals(""))) { return prefix; }
        return getKnownPrefix(ns);
    }

    public String getPrefix(String ns) {
        PrefixNSInfo info = nsInfoMap.get(ns);
        if (info.isAvailable()) { return info.getPrefix(); }
        return ns;
    }

    public String getNS(String prefix) {
        return prefixNSMap.get(prefix);
    }

    private static final String PREFIX = "p";

    private String getKnownPrefix(String ns) {
        String knownPrefix = knownNSPrefixMap.get(ns);
        if (knownPrefix == null) {
            knownPrefix = PREFIX;
        }
        return knownPrefix;
    }

    private String getMR3Prefix(String knownPrefix) {
        for (int i = 0; true; i++) {
            String cnt = Integer.toString(i);
            if (isValidPrefix(knownPrefix + cnt)) {
                knownPrefix = knownPrefix + cnt;
                break;
            }
        }
        return knownPrefix;
    }

    public void setCurrentNSPrefix(Model model) {
        Set<String> nsSet = new HashSet<String>();
        for (StmtIterator i = model.listStatements(); i.hasNext();) {
            Statement stmt = i.nextStatement();
            String ns = Utils.getNameSpace(stmt.getSubject());
            if (ns != null) {
                nsSet.add(ns);
            }
            RDFNode object = stmt.getObject();
            if (object instanceof Resource) {
                Resource res = (Resource) object;
                ns = Utils.getNameSpace(res);
                if (ns != null) {
                    nsSet.add(ns);
                }
            }
        }
        for (String ns : nsSet) {
            if (isValidNS(ns)) {
                String knownPrefix = getKnownPrefix(model, ns);
                if (isValidPrefix(knownPrefix) && (!knownPrefix.equals(PREFIX))) {
                    addNameSpaceTable(new Boolean(true), knownPrefix, ns);
                } else {
                    addNameSpaceTable(new Boolean(true), getMR3Prefix(getKnownPrefix(ns)), ns);
                }
            }
        }
        setNSInfoMap();
    }
    public NSTableModel getNSTableModel() {
        return nsTableModel;
    }

    public void loadState(List list) {
        Map map = (Map) list.get(0);
        NSTableModel model = (NSTableModel) list.get(1);
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isAvailable = (Boolean) model.getValueAt(i, 0);
            String prefix = (String) model.getValueAt(i, 1);
            String ns = (String) model.getValueAt(i, 2);
            if (isValidPrefix(prefix) && isValidNS(ns)) {
                addNameSpaceTable(isAvailable, prefix, ns);
            }
        }
        // ここでprefixNSMapを設定しないと，上の内容を元に戻すことができない．(non validとなる）
        prefixNSMap.putAll(map);
        setNSInfoMap();
    }

    public void resetNSTable() {
        prefixNSMap = new HashMap<String, String>();
        // 一気にすべて削除する方法がわからない．
        while (nsTableModel.getRowCount() != 0) {
            nsTableModel.removeRow(nsTableModel.getRowCount() - 1);
        }
        nsInfoMap.clear();
    }

    private void initTable() {
        Object[] columnNames = new Object[] { Translator.getString("Available"), Translator.getString("Prefix"), "URI"};
        nsTableModel = new NSTableModel(columnNames, 0);
        nsTableModel.addTableModelListener(this);
        nsTable = new JTable(nsTableModel);
        nsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumnModel tcModel = nsTable.getColumnModel();
        tcModel.getColumn(0).setPreferredWidth(50);
        tcModel.getColumn(1).setPreferredWidth(100);
        tcModel.getColumn(2).setPreferredWidth(450);
    }

    private void setTableLayout() {
        JScrollPane nsTableScroll = new JScrollPane(nsTable);
        nsTableScroll.setPreferredSize(new Dimension(700, 115));
        nsTableScroll.setMinimumSize(new Dimension(700, 115));
        add(nsTableScroll, BorderLayout.CENTER);
    }

    private void setInputLayout() {
        prefixField = new JTextField(10);
        JComponent prefixFieldP = Utilities.createTitledPanel(prefixField, Translator.getString("Prefix"));

        nsField = new JTextField(30);
        JComponent nsFieldP = Utilities.createTitledPanel(nsField, Translator.getString("NameSpace"));

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(prefixFieldP, BorderLayout.WEST);
        southPanel.add(nsFieldP, BorderLayout.CENTER);
        southPanel.add(getButtonPanel(), BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JComponent getButtonPanel() {
        addNSButton = new JButton(Translator.getString("Add") + "(A)");
        addNSButton.setMnemonic('a');
        addNSButton.addActionListener(this);
        removeNSButton = new JButton(Translator.getString("Remove") + "(R)");
        removeNSButton.setMnemonic('r');
        removeNSButton.addActionListener(this);
        cancelButton = new JButton(Translator.getString("Cancel"));
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 5, 5));
        buttonPanel.add(addNSButton);
        buttonPanel.add(removeNSButton);
        buttonPanel.add(cancelButton);
        return Utilities.createSouthPanel(buttonPanel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addNSButton) {
            addNameSpaceTable(new Boolean(true), prefixField.getText(), nsField.getText());
            setNSInfoMap();
        } else if (e.getSource() == removeNSButton) {
            removeNameSpaceTable();
        } else if (e.getSource() == cancelButton) {
            setVisible(false);
        }
    }

    private boolean isValidPrefix(String prefix) {
        Set keySet = prefixNSMap.keySet();
        return (!keySet.contains(prefix) && !prefix.equals(""));
    }

    private boolean isValidNS(String ns) {
        Collection values = prefixNSMap.values();
        return (ns != null && !ns.equals("") && !ns.equals("http://") && !values.contains(ns));
    }

    /**
     * prefix が空でなくかつ，すでに登録されていない場合true
     */
    private boolean isValidPrefixWithWarning(String prefix) {
        if (isValidPrefix(prefix)) { return true; }
        JOptionPane.showMessageDialog(null, Translator.getString("Warning.Message5"), WARNING,
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * nsが空でもnullでもなく，すでに登録されてない場合 true
     */
    private boolean isValidNSWithWarning(String ns) {
        if (isValidNS(ns)) { return true; }
        JOptionPane.showMessageDialog(null, Translator.getString("Warning.Message6"), WARNING,
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public void addNameSpaceTable(String ns) {
        if (!isValidNS(ns)) { return; }
        String prefix = "ns1";
        for (int i = 1; !isValidPrefix(prefix); i++) {
            prefix = "ns" + i;
        }
        addNameSpaceTable(true, prefix, ns);
        setNSInfoMap();
    }

    public void addNameSpaceTable(Boolean isAvailable, String prefix, String ns) {
        if (isValidPrefixWithWarning(prefix) && isValidNSWithWarning(ns)) {
            prefixNSMap.put(prefix, ns);
            Object[] list = new Object[] { isAvailable, prefix, ns};
            nsTableModel.insertRow(nsTableModel.getRowCount(), list);
            prefixField.setText("");
            nsField.setText("");
        }
    }

    private void removeNameSpaceTable() {
        int[] removeList = nsTable.getSelectedRows();
        int length = removeList.length;
        // どうやったら，複数のrowを消すせるのかがよくわからない．
        // modelから消した時点でrow番号が変わってしまうのが原因
        if (length == 0) { return; }
        int row = removeList[0];
        String rmPrefix = (String) nsTableModel.getValueAt(row, 1);
        String rmNS = (String) nsTableModel.getValueAt(row, 2);
        if (rmNS.equals(DODDLE.BASE_URI)) {
            JOptionPane.showMessageDialog(null, Translator.getString("Warning.Message7"), WARNING,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (rmNS.equals(MR3Resource.DefaultURI.getNameSpace())) {
            JOptionPane.showMessageDialog(null, Translator.getString("Warning.Message8"), WARNING,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // if (!gmanager.getAllNameSpaceSet().contains(rmNS)) {
        prefixNSMap.remove(rmPrefix);
        nsTableModel.removeRow(row);
        setNSInfoMap();
        // } else {
        // JOptionPane.showMessageDialog(null,
        // Translator.getString("Warning.Message9"), WARNING,
        // JOptionPane.ERROR_MESSAGE);
        // }
    }

    public void setNSInfoMap() {
        nsInfoMap.clear();
        for (int i = 0; i < nsTableModel.getRowCount(); i++) {
            String prefix = (String) nsTableModel.getValueAt(i, 1);
            String ns = (String) nsTableModel.getValueAt(i, 2);
            nsInfoMap.put(ns, new PrefixNSInfo(prefix, ns, isPrefixAvailable(i, 0)));
        }
    }

    private boolean isCheckBoxChanged(int type, int column) {
        return (type == TableModelEvent.UPDATE && column == 0);
    }

    /**
     * テーブルのチェックボックスがチェックされたかどうか
     */
    private boolean isPrefixAvailable(int row, int column) {
        Boolean isPrefixAvailable = (Boolean) nsTableModel.getValueAt(row, column);
        return isPrefixAvailable.booleanValue();
    }

    public void tableChanged(TableModelEvent e) {
        // int row = e.getFirstRow();
        int column = e.getColumn();
        int type = e.getType();

        if (isCheckBoxChanged(type, column)) {
            setNSInfoMap();
        }
    }

    public class NSTableModel extends DefaultTableModel {

        public NSTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 2) return false;
            return true;
        }

        public Class getColumnClass(int column) {
            Vector v = (Vector) dataVector.elementAt(0);
            return v.elementAt(column).getClass();
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (aValue instanceof String) {
                String prefix = (String) aValue;
                // 多分prefixのチェックはいらない．
                String oldPrefix = (String) nsTableModel.getValueAt(rowIndex, columnIndex);
                prefixNSMap.remove(oldPrefix);
                String ns = (String) nsTableModel.getValueAt(rowIndex, 2);
                prefixNSMap.put(prefix, ns);
            }
            super.setValueAt(aValue, rowIndex, columnIndex);
            setNSInfoMap();
        }
    }
}