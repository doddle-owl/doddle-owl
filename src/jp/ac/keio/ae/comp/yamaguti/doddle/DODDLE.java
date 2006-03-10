package jp.ac.keio.ae.comp.yamaguti.doddle;

import gnu.getopt.*;

import java.awt.*;
import java.awt.Container;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.actions.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;
import net.sourceforge.mlf.metouia.*;

import org.apache.log4j.*;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author takeshi morita
 */
public class DODDLE extends JFrame {

    public static boolean IS_USING_DB;
    public static boolean IS_USING_SKINLF;
    public static DODDLEPlugin doddlePlugin;
    public static JDesktopPane desktop;

    public static JMenu projectMenu;

    private OptionDialog optionDialog;

    public static StatusBarPanel STATUS_BAR;
    public static int DIVIDER_SIZE = 10;

    public static final String VERSION = "2006-03-11";

    public static final int DOCUMENT_SELECTION_PANEL = 1;
    public static final int INPUT_WORD_SELECTION_PANEL = 2;
    public static final int INPUT_MODULE = 3;
    public static final int TAXONOMIC_PANEL = 4;
    public static String BASE_URI = "http://www.yamaguti.comp.ae.keio.ac.jp/doddle-j#";
    public static String BASE_PREFIX = "keio";
    public static String EDR_URI = "http://www2.nict.go.jp/kk/e416/EDR#";
    public static String OLD_EDR_URI = "http://www2.nict.go.jp/kk/e416/EDR/";
    public static String EDRT_URI = "http://www2.nict.go.jp/kk/e416/EDRT#";
    public static String WN_URI = "http://wordnet.princeton.edu/wn/2.0#";

    public static String DODDLE_DIC = "C:/DODDLE_DIC/";
    public static String DODDLE_EDRT_DIC = "C:/DODDLE_EDRT_DIC/";
    public static String SEN_HOME = "C:/sen-1.2.1/";
    public static String PROJECT_DIR = "./project/";
    private static final String RESOURCES = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";
    public static String JWNL_PROPERTIES_FILE = RESOURCES + "file_properties.xml";
    public static String WORDNET_PATH = "C:/program files/wordnet/2.0/dict";

    public static JRootPane rootPane;

    private NewProjectAction newProjectAction;
    private OpenProjectAction openProjectAction;
    private SaveProjectAction saveProjectAction;
    private SaveProjectAsAction saveProjectAsAction;

    public DODDLE() {
        EDRDic.init();
        rootPane = getRootPane();
        desktop = new JDesktopPane();
        optionDialog = new OptionDialog(this);
        STATUS_BAR = new StatusBarPanel();

        Container contentPane = getContentPane();
        makeActions();
        makeMenuBar();
        contentPane.add(getToolBar(), BorderLayout.NORTH);
        contentPane.add(desktop, BorderLayout.CENTER);
        contentPane.add(STATUS_BAR, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocation(50, 50);
        setTitle("DODDLE - バージョン: " + VERSION);
        setVisible(true);
    }

    public DODDLEPlugin getDODDLEPlugin() {
        return doddlePlugin;
    }

    public OptionDialog getOptionDialog() {
        return optionDialog;
    }

    public void exit() {
        int messageType = JOptionPane.showConfirmDialog(rootPane, "DODDLEを終了しますか？");
        if (messageType == JOptionPane.YES_OPTION) {
            EDRDic.closeDB();
            if (doddlePlugin == null) {
                System.exit(0);
            } else {
                dispose();
            }
        }
    }

    public static DODDLEProject newProject() {
        DODDLEProject project = new DODDLEProject("新規プロジェクト", projectMenu);
        try {
            desktop.add(project);
            project.toFront();
            desktop.setSelectedFrame(project);
            project.setVisible(true);
            project.setMaximum(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project;
    }

    private void makeActions() {
        newProjectAction = new NewProjectAction(Translator.getString("Component.File.NewProject.Text"));
        openProjectAction = new OpenProjectAction("プロジェクトを開く", this);
        saveProjectAction = new SaveProjectAction(Translator.getString("Component.File.SaveProject.Text"), this);
        saveProjectAsAction = new SaveProjectAsAction(Translator.getString("Component.File.SaveAsProject.Text"), this);
    }

    private void makeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(Translator.getString("Component.File.Text"));
        fileMenu.add(newProjectAction);
        fileMenu.addSeparator();
        fileMenu.add(openProjectAction);
        JMenu loadMenu = new JMenu("復元");
        loadMenu.add(new LoadWordInfoTableAction("入力単語テーブルを復元"));
        loadMenu.add(new LoadWordEvalConceptSetAction("多義性解消結果を復元"));
        loadMenu.add(new LoadWordConceptMapAction("単語とIDの対応を復元"));
        loadMenu.add(new LoadOntologyAction("オントロジーを復元", this));
        loadMenu.add(new LoadIDTypicalWordAction("IDと代表見出しの対応を復元", this));
        fileMenu.add(loadMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveProjectAction);
        fileMenu.add(saveProjectAsAction);
        JMenu saveMenu = new JMenu("保存");
        // saveMenu.add(new SaveMatchedWordList("辞書に載っていた入力語彙を保存"));
        saveMenu.add(new SaveWordInfoTableAction("入力単語テーブルを保存"));
        saveMenu.add(new SaveWordEvalConceptSetAction("多義性解消結果を保存"));
        saveMenu.add(new SaveWordIDMapAction("単語とIDの対応を保存"));
        saveMenu.add(new SaveCompleteMatchWordAction("完全照合単語リストを保存"));
        saveMenu.add(new SaveCompleteMatchWordWithComplexWordAction("完全照合単語リストと対応する複合語を保存"));
        saveMenu.add(new SaveOntologyAction("オントロジーを保存", this));
        saveMenu.add(new SaveIDTypicalWordAction("IDと代表見出しの対応を保存", this));
        fileMenu.add(saveMenu);
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction("終了", this));
        menuBar.add(fileMenu);

        JMenu toolMenu = new JMenu("ツール");
        toolMenu.add(new OpenWordListAction("単語リストを開く"));
        toolMenu.addSeparator();
        toolMenu.add(new ShowAllWordsAction("すべての単語を表示"));
        toolMenu.addSeparator();
        toolMenu.add(new AutomaticDisAmbiguationAction("多義性解消"));
        toolMenu.addSeparator();
        toolMenu.add(new ConstructNounTreeAction("概念階層構築（名詞）"));
        toolMenu.add(new ConstructNounAndVerbTreeAction("概念階層構築（名詞および動詞）"));
        toolMenu.addSeparator();
        if (doddlePlugin != null) {
            toolMenu.add(new VisualizeAction("視覚化", doddlePlugin));
            toolMenu.addSeparator();
        }
        toolMenu.add(new OptionAction("オプション", this));
        menuBar.add(toolMenu);

        projectMenu = new JMenu("プロジェクト");
        menuBar.add(projectMenu);

        JMenu helpMenu = new JMenu("ヘルプ");
        JMenuItem versionItem = new JMenuItem("バージョン");
        versionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SplashWindow();
            }
        });
        helpMenu.add(versionItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.add(newProjectAction).setToolTipText(newProjectAction.getTitle());
        toolBar.add(openProjectAction).setToolTipText(openProjectAction.getTitle());
        toolBar.add(saveProjectAction).setToolTipText(saveProjectAction.getTitle());
        toolBar.add(saveProjectAsAction).setToolTipText(saveProjectAsAction.getTitle());
        return toolBar;
    }

    public static DODDLEProject getCurrentProject() {
        DODDLEProject currentProject = (DODDLEProject) desktop.getSelectedFrame();
        if (currentProject == null) {
            currentProject = newProject();
        }
        return currentProject;
    }

    public void saveProject(File saveDir, DODDLEProject currentProject) {
        InputModuleUI inputModuleUI = currentProject.getInputModuleUI();
        DocumentSelectionPanel docSelectionPanel = currentProject.getDocumentSelectionPanel();
        InputWordSelectionPanel inputWordSelectionPanel = currentProject.getInputWordSelectionPanel();
        currentProject.setTitle(saveDir.getAbsolutePath());
        currentProject.setProjectName(saveDir.getAbsolutePath());
        saveDir.mkdir();
        optionDialog.saveOption(new File(saveDir, "option.txt"));
        docSelectionPanel.saveDocuments(saveDir);
        inputWordSelectionPanel.saveWordInfoTable(new File(saveDir, "WordInfoTable.txt"));
        inputModuleUI.saveInputWordSet(new File(saveDir, "InputWordSet.txt"));
        inputModuleUI.saveWordEvalConceptSet(new File(saveDir, "wordEvalConceptSet.txt"));
        inputModuleUI.saveWordConceptMap(new File(saveDir, "InputWord_ID.txt"));
        inputModuleUI.saveConstructTreeOptionSet(new File(saveDir, "InputWord_ConstructTreeOption.txt"));
        inputModuleUI.saveInputConceptSet(new File(saveDir, "InputIDSet.txt"));
        inputModuleUI.saveUndefinedWordSet(new File(saveDir, "UndefinedWordSet.txt"));
        saveOntology(currentProject, new File(saveDir, "Ontology.owl"));
        saveIDTypicalWord(currentProject, new File(saveDir, "ID_TypicalWord.txt"));
        saveProjectInfo(currentProject, new File(saveDir, "projectInfo.txt"));
        STATUS_BAR
                .setText(java.util.Calendar.getInstance().getTime() + ": " + currentProject.getTitle() + "プロジェクトを保存．");
    }

    public void loadIDTypicalWord(DODDLEProject currentProject, File file) {
        ConstructConceptTreePanel constructConceptTreePanel = currentProject.getConstructConceptTreePanel();
        ConstructPropertyTreePanel constructConceptDefinitionPanel = currentProject.getConstructPropertyTreePanel();

        if (!file.exists()) { return; }
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "JISAutoDetect"));
            String line = "";
            Map<String, String> idTypicalWordMap = new HashMap<String, String>();
            while ((line = reader.readLine()) != null) {
                String[] idInputWord = line.replaceAll("\n", "").split("\t");
                if (idInputWord.length == 2) {
                    idTypicalWordMap.put(idInputWord[0], idInputWord[1]);
                }
            }
            constructConceptTreePanel.loadIDTypicalWord(idTypicalWordMap);
            if (OptionDialog.isNounAndVerbConceptHierarchyConstructionMode()) {
                constructConceptDefinitionPanel.loadIDTypicalWord(idTypicalWordMap);
            }
            reader.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void saveProjectInfo(DODDLEProject currentProject, File file) {
        OntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        InputModuleUI inputModuleUI = currentProject.getInputModuleUI();
        ConstructConceptTreePanel constructConceptTreePanel = currentProject.getConstructConceptTreePanel();
        ConstructPropertyTreePanel constructPropertyTreePanel = currentProject.getConstructPropertyTreePanel();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            StringBuffer buf = new StringBuffer();
            buf.append(BASE_URI + "\n");
            buf.append("利用可能な汎用辞書: " + ontSelectionPanel.getEnableDicList() + "\n");
            if (inputModuleUI.getInputWordModelSet() != null) {
                buf.append("入力単語数: " + inputModuleUI.getInputWordModelSet().size() + "\n");
            }
            buf.append("完全照合単語数: " + inputModuleUI.getPerfectMatchedWordNum() + "\n");
            buf.append("部分照合単語数: " + inputModuleUI.getPartialMatchedWordNum() + "\n");
            buf.append("照合単語数: " + inputModuleUI.getMatchedWordNum() + "\n");
            buf.append("未照合単語数: " + inputModuleUI.getUndefinedWordNum() + "\n");

            if (inputModuleUI.getInputConceptSet() != null) {
                buf.append("入力概念数: " + inputModuleUI.getInputConceptSet().size() + "\n");
            }
            if (inputModuleUI.getInputNounConceptSet() != null) {
                buf.append("入力名詞的概念数: " + inputModuleUI.getInputNounConceptSet().size() + "\n");
            }
            if (inputModuleUI.getInputVerbConceptSet() != null) {
                buf.append("入力動詞的概念数: " + inputModuleUI.getInputVerbConceptSet().size() + "\n");
            }

            buf.append("クラス階層構築における追加SIN数: " + constructConceptTreePanel.getAddedSINNum() + "\n");
            buf.append("剪定前クラス数: " + constructConceptTreePanel.getBeforeTrimmingConceptNum() + "\n");
            buf.append("剪定クラス数: " + constructConceptTreePanel.getTrimmedConceptNum() + "\n");
            int afterTrimmingConceptNum = constructConceptTreePanel.getAfterTrimmingConceptNum();
            buf.append("剪定後クラス数: " + afterTrimmingConceptNum + "\n");

            buf.append("プロパティ階層構築における追加SIN数: " + constructPropertyTreePanel.getAddedSINNum() + "\n");
            buf.append("剪定前プロパティ数: " + constructPropertyTreePanel.getBeforeTrimmingConceptNum() + "\n");
            buf.append("剪定プロパティ数: " + constructPropertyTreePanel.getTrimmedConceptNum() + "\n");
            int afterTrimmingPropertyNum = constructPropertyTreePanel.getAfterTrimmingConceptNum();
            buf.append("剪定後プロパティ数: " + afterTrimmingPropertyNum + "\n");

            buf.append("追加抽象中間クラス数: " + constructConceptTreePanel.getAddedAbstractComplexConceptCnt() + "\n");
            buf.append("抽象中間クラスの平均兄弟クラスグループ化数: "
                    + constructConceptTreePanel.getAverageAbstracComplexConceptGroupSiblingConceptCnt() + "\n");

            buf.append("追加抽象中間プロパティ数: " + constructPropertyTreePanel.getAddedAbstractComplexConceptCnt() + "\n");
            buf.append("抽象中間プロパティの平均兄弟プロパティグループ化数: "
                    + constructPropertyTreePanel.getAverageAbstracComplexConceptGroupSiblingConceptCnt() + "\n");

            int lastClassNum = constructConceptTreePanel.getAllConceptCnt();
            int lastPropertyNum = constructPropertyTreePanel.getAllConceptCnt();

            buf.append("複合語クラス数: " + (lastClassNum - afterTrimmingConceptNum) + "\n");
            buf.append("複合語プロパティ数: " + (lastPropertyNum - afterTrimmingPropertyNum) + "\n");

            buf.append("最終クラス数: " + lastClassNum + "\n");
            buf.append("最終プロパティ数: " + lastPropertyNum + "\n");

            buf.append("平均兄弟クラス数: " + constructConceptTreePanel.getChildCntAverage() + "\n");
            buf.append("平均兄弟プロパティ数: " + constructPropertyTreePanel.getChildCntAverage() + "\n");

            writer.write(buf.toString());
            writer.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void saveIDTypicalWord(DODDLEProject currentProject, File file) {
        ConstructConceptTreePanel constructConceptTreePanel = currentProject.getConstructConceptTreePanel();
        ConstructPropertyTreePanel constructConceptDefinitionPanel = currentProject.getConstructPropertyTreePanel();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            Map idTypicalWordMap = constructConceptTreePanel.getIDTypicalWordMap();
            idTypicalWordMap.putAll(constructConceptDefinitionPanel.getIDTypicalWordMap());
            StringBuffer buf = new StringBuffer();
            for (Iterator i = idTypicalWordMap.keySet().iterator(); i.hasNext();) {
                String id = (String) i.next();
                String typicalWord = (String) idTypicalWordMap.get(id);
                buf.append(id + "\t" + typicalWord + "\n");
            }
            writer.write(buf.toString());
            writer.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void setSelectedIndex(int index) {
        DODDLEProject currentProject = (DODDLEProject) desktop.getSelectedFrame();
        currentProject.setSelectedIndex(index);
    }

    public static Model getOntology(DODDLEProject currentProject) {
        ConstructConceptTreePanel constructConceptTreePanel = currentProject.getConstructConceptTreePanel();
        ConstructPropertyTreePanel constructPropertyTreePanel = currentProject.getConstructPropertyTreePanel();
        TextConceptDefinitionPanel conceptDefinitionWithTextPanel = currentProject.getTextConceptDefinitionPanel();

        Model ontology = JenaModelMaker.makeClassModel(constructConceptTreePanel.getTreeModelRoot(), ModelFactory
                .createDefaultModel());
        JenaModelMaker.makePropertyModel(constructPropertyTreePanel.getTreeModelRoot(), ontology);
        conceptDefinitionWithTextPanel.addConceptDefinition(ontology);
        return ontology;
    }

    public void saveOntology(DODDLEProject project, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF8"));
            Model ontModel = getOntology(project);
            RDFWriter rdfWriter = ontModel.getWriter("RDF/XML-ABBREV");
            rdfWriter.setProperty("xmlbase", BASE_URI);
            rdfWriter.setProperty("showXmlDeclaration", Boolean.TRUE);
            rdfWriter.write(ontModel, writer, BASE_URI);
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadBaseURI(File file) {
        if (!file.exists()) { return; }
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            BASE_URI = reader.readLine();
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadOntology(DODDLEProject currentProject, File file) {
        InputModuleUI inputModuleUI = currentProject.getInputModuleUI();
        ConstructConceptTreePanel constructConceptTreePanel = currentProject.getConstructConceptTreePanel();
        ConstructPropertyTreePanel constructPropertyTreePanel = currentProject.getConstructPropertyTreePanel();

        if (!file.exists()) { return; }
        constructConceptTreePanel.init();
        constructPropertyTreePanel.init();
        currentProject.resetIDConceptMap();
        ConceptTreeMaker.getInstance().setInputConceptSet(inputModuleUI.getInputConceptSet());
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            Model model = ModelFactory.createDefaultModel();
            model.read(reader, BASE_URI, "RDF/XML");
            currentProject.initUserIDCount();
            TreeNode rootNode = ConceptTreeMaker.getInstance().getConceptTreeRoot(currentProject, model,
                    ResourceFactory.createResource(EDR_URI + "ID" + ConceptTreeMaker.DODDLE_CLASS_ROOT_ID));
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            constructConceptTreePanel.setTreeModel(treeModel);
            constructConceptTreePanel.setVisibleConceptTree(true);
            constructConceptTreePanel.checkMultipleInheritance(treeModel);
            currentProject.setUserIDCount(currentProject.getUserIDCount() + 1);
            treeModel.reload();

            currentProject.setUserIDCount(currentProject.getUserIDCount());
            rootNode = ConceptTreeMaker.getInstance().getPropertyTreeRoot(currentProject, model,
                    ResourceFactory.createResource(EDR_URI + "ID" + ConceptTreeMaker.DODDLE_PROPERTY_ROOT_ID));
            treeModel = new DefaultTreeModel(rootNode);
            constructPropertyTreePanel.setTreeModel(treeModel);
            constructPropertyTreePanel.setVisibleConceptTree(true);
            constructPropertyTreePanel.checkMultipleInheritance(treeModel);
            treeModel.reload();
            currentProject.setUserIDCount(currentProject.getUserIDCount() + 1);

            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void setPath() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("./config.txt"));
            SEN_HOME = properties.getProperty("SEN_HOME");
            DODDLE_DIC = properties.getProperty("DODDLE_DIC");
            DODDLE_EDRT_DIC = properties.getProperty("DODDLE_EDRT_DIC");
            DocumentSelectionPanel.PERL_EXE = properties.getProperty("PERL_EXE");
            DocumentSelectionPanel.CHASEN_EXE = properties.getProperty("CHASEN_EXE");
            DocumentSelectionPanel.SS_TAGGER_HOME = properties.getProperty("SSTAGGER_HOME");
            BASE_URI = properties.getProperty("BASE_URI");
            BASE_PREFIX = properties.getProperty("BASE_PREFIX");
            PROJECT_DIR = properties.getProperty("PROJECT_DIR");
            UpperConceptManager.UPPER_CONCEPT_LIST = properties.getProperty("UPPER_CONCEPT_LIST");
            WORDNET_PATH = properties.getProperty("WORDNET_PATH");
        } catch (Exception e) {
            e.printStackTrace();
        }
        EDRDic.ID_DEFINITION_MAP = DODDLE_DIC + "idDefinitionMapforEDR.txt";
        EDRDic.WORD_IDSET_MAP = DODDLE_DIC + "wordIDSetMapforEDR.txt";
        EDRTree.ID_SUBIDSET_MAP = DODDLE_DIC + "idSubIDSetMapforEDR.txt";
        ConceptDefinition.CONCEPT_DEFINITION = DODDLE_DIC + "conceptDefinitionforEDR.txt";

        EDRDic.EDRT_ID_DEFINITION_MAP = DODDLE_EDRT_DIC + "idDefinitionMapforEDR.txt";
        EDRDic.EDRT_WORD_IDSET_MAP = DODDLE_EDRT_DIC + "wordIDSetMapforEDR.txt";
        EDRTree.EDRT_ID_SUBIDSET_MAP = DODDLE_EDRT_DIC + "idSubIDSetMapforEDR.txt";
    }

    public static void setProgressValue() {
        InputModule.INIT_PROGRESS_VALUE = 887253;
        // InputModule.INIT_PROGRESS_VALUE = 283517;
    }

    public static Logger getLogger() {
        return Logger.getLogger(DODDLE.class);
    }

    private static void setDefaultLoggerFormat() {
        for (Enumeration enumeration = Logger.getRootLogger().getAllAppenders(); enumeration.hasMoreElements();) {
            Appender appender = (Appender) enumeration.nextElement();
            if (appender.getName().equals("stdout")) {
                appender.setLayout(new PatternLayout("[%5p][%c{1}][%d{yyyy-MMM-dd HH:mm:ss}]: %m\n"));
            }
        }
    }

    public static void initOptions(String[] args) {
        DODDLE.IS_USING_DB = false;
        setDefaultLoggerFormat();

        LongOpt[] longopts = new LongOpt[4];
        longopts[0] = new LongOpt("DB", LongOpt.NO_ARGUMENT, null, 'd');
        longopts[1] = new LongOpt("DEBUG", LongOpt.NO_ARGUMENT, null, 'g');
        longopts[2] = new LongOpt("SKIN", LongOpt.NO_ARGUMENT, null, 's');
        Getopt g = new Getopt("DODDLE", args, "", longopts);
        g.setOpterr(false);

        setPath();
        setProgressValue();
        int c;
        while ((c = g.getopt()) != -1) {
            switch (c) {
            case 'g':
                getLogger().setLevel(Level.DEBUG);
                break;
            case 'd':
                DODDLE.IS_USING_DB = true;
                break;
            case 's':
                DODDLE.IS_USING_SKINLF = true;
                break;
            default:
                break;
            }
        }
        if (DODDLE.IS_USING_DB) {
            getLogger().log(Level.INFO, "Read EDR DIC using Berkeley DB");
        } else {
            getLogger().log(Level.INFO, "Read EDR DIC on Memory");
        }
    }

    public static void main(String[] args) {
        Translator.loadResourceBundle("ja");
        SplashWindow splashWindow = new SplashWindow();
        DODDLE.initOptions(args);
        try {
            ToolTipManager.sharedInstance().setEnabled(true);
            if (DODDLE.IS_USING_SKINLF) {
                UIManager.setLookAndFeel(new MetouiaLookAndFeel());
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            new DODDLE();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            splashWindow.setVisible(false);
        }
    }
}