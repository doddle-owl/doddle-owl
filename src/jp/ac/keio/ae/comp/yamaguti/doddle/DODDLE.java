package jp.ac.keio.ae.comp.yamaguti.doddle;

import gnu.getopt.*;

import java.awt.*;
import java.awt.Container;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

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
    public static String LANG = "ja"; // DB構築時に必要
    public static DODDLEPlugin doddlePlugin;
    public static JDesktopPane desktop;

    public static JMenu projectMenu;

    private OptionDialog optionDialog;

    public static StatusBarPanel STATUS_BAR;
    public static int DIVIDER_SIZE = 10;

    public static final String VERSION = "2006-10-02";

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
    public static String SEN_HOME = "C:/DODDLE_DIC/sen-1.2.1/";
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
        setTitle(Translator.getString("Title") + " - " + Translator.getString("Component.Help.Version") + ": "
                + VERSION);
        setIconImage(Utils.getImageIcon("doddle_splash.png").getImage());
        setVisible(true);
    }

    public static DODDLEPlugin getDODDLEPlugin() {
        return doddlePlugin;
    }

    public OptionDialog getOptionDialog() {
        return optionDialog;
    }

    public void exit() {
        int messageType = JOptionPane.showConfirmDialog(rootPane, Translator.getString("Component.File.Exit.DODDLE"),
                Translator.getString("Component.File.Exit"), JOptionPane.YES_NO_CANCEL_OPTION);
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
        DODDLEProject project = new DODDLEProject(Translator.getString("Component.File.NewProject.Text"), projectMenu);
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
        openProjectAction = new OpenProjectAction(Translator.getString("Component.File.OpenProject.Text"), this);
        saveProjectAction = new SaveProjectAction(Translator.getString("Component.File.SaveProject.Text"), this);
        saveProjectAsAction = new SaveProjectAsAction(Translator.getString("Component.File.SaveAsProject.Text"), this);
    }

    private void makeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(Translator.getString("Component.File.Text"));
        fileMenu.add(newProjectAction);
        fileMenu.addSeparator();
        fileMenu.add(openProjectAction);
        JMenu loadMenu = new JMenu(Translator.getString("Component.File.Open"));
        loadMenu.add(new LoadWordInfoTableAction(Translator.getString("Component.File.Open.InputWordTable")));
        loadMenu
                .add(new LoadWordEvalConceptSetAction(Translator.getString("Component.File.Open.DisambiguationResult")));
        loadMenu.add(new LoadWordConceptMapAction(Translator.getString("Component.File.Open.InputWordIDMap")));
        loadMenu.add(new LoadOntologyAction(Translator.getString("Component.File.Open.Ontology"), this));
        loadMenu.add(new LoadIDTypicalWordAction(Translator.getString("Component.File.Open.IDTypicalWordMap"), this));
        fileMenu.add(loadMenu);
        fileMenu.addSeparator();
        fileMenu.add(saveProjectAction);
        fileMenu.add(saveProjectAsAction);
        JMenu saveMenu = new JMenu(Translator.getString("Component.File.Save"));
        // saveMenu.add(new SaveMatchedWordList("辞書に載っていた入力語彙を保存"));
        saveMenu.add(new SaveWordInfoTableAction(Translator.getString("Component.File.Save.InputWordTable")));
        saveMenu
                .add(new SaveWordEvalConceptSetAction(Translator.getString("Component.File.Save.DisambiguationResult")));
        saveMenu.add(new SaveWordIDMapAction(Translator.getString("Component.File.Save.InputWordIDMap")));
        saveMenu.add(new SaveCompleteMatchWordAction(Translator.getString("Component.File.Save.CompleteMatchWord")));
        saveMenu.add(new SaveCompleteMatchWordWithComplexWordAction(Translator
                .getString("Component.File.Save.CompleteMatchWordComplexWordMap")));
        saveMenu.add(new SaveOntologyAction(Translator.getString("Component.File.Save.Ontology"), this));
        saveMenu.add(new SaveIDTypicalWordAction(Translator.getString("Component.File.Save.IDTypicalWordMap"), this));
        fileMenu.add(saveMenu);
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(Translator.getString("Component.File.Exit"), this));
        menuBar.add(fileMenu);

        JMenu toolMenu = new JMenu(Translator.getString("Component.Tool.Text"));
        toolMenu.add(new OpenWordListAction(Translator.getString("Component.Tool.OpenInputWordList")));
        toolMenu.addSeparator();
        toolMenu.add(new ShowAllWordsAction(Translator.getString("Component.Tool.OpenAllWordList")));
        toolMenu.addSeparator();
        toolMenu.add(new AutomaticDisAmbiguationAction(Translator.getString("Component.Tool.Disambiguation")));
        toolMenu.addSeparator();
        toolMenu.add(new ConstructNounTreeAction());
        toolMenu.add(new ConstructNounAndVerbTreeAction());
        toolMenu.addSeparator();
        toolMenu.add(new OptionAction(Translator.getString("Component.Tool.Option"), this));
        menuBar.add(toolMenu);

        projectMenu = new JMenu(Translator.getString("Component.Project.Text"));
        menuBar.add(projectMenu);
        JMenu helpMenu = new JMenu(Translator.getString("Component.Help.Text"));
        JMenuItem versionItem = new JMenuItem(Translator.getString("Component.Help.Version"));
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
        OntologySelectionPanel ontSelectionPanel = currentProject.getOntologySelectionPanel();
        DisambiguationPanel disambiguationPanel = currentProject.getDisambiguationPanel();
        DocumentSelectionPanel docSelectionPanel = currentProject.getDocumentSelectionPanel();
        InputWordSelectionPanel inputWordSelectionPanel = currentProject.getInputWordSelectionPanel();
        currentProject.setTitle(saveDir.getAbsolutePath());
        currentProject.setProjectName(saveDir.getAbsolutePath());
        saveDir.mkdir();
        optionDialog.saveOption(new File(saveDir, "option.txt"), disambiguationPanel);
        ontSelectionPanel.saveOntologyInfo(new File(saveDir, "OntologyInfo.txt"));
        docSelectionPanel.saveDocuments(saveDir);
        inputWordSelectionPanel.saveWordInfoTable(new File(saveDir, "WordInfoTable.txt"));
        disambiguationPanel.saveInputWordSet(new File(saveDir, "InputWordSet.txt"));
        disambiguationPanel.saveWordEvalConceptSet(new File(saveDir, "wordEvalConceptSet.txt"));
        disambiguationPanel.saveWordCorrespondConceptSetMap(new File(saveDir, "InputWord_ID.txt"));
        disambiguationPanel.saveConstructTreeOptionSet(new File(saveDir, "InputWord_ConstructTreeOption.txt"));
        disambiguationPanel.saveInputConceptSet(new File(saveDir, "InputIDSet.txt"));
        disambiguationPanel.saveUndefinedWordSet(new File(saveDir, "UndefinedWordSet.txt"));
        saveOntology(currentProject, new File(saveDir, "Ontology.owl"));
        saveIDTypicalWord(currentProject, new File(saveDir, "ID_TypicalWord.txt"));

        saveTrimmedResultAnalysis(currentProject.getConstructClassPanel().getConceptDriftManagementPanel(), new File(
                saveDir, "ClassTrimmedResultAnalysis.txt"));
        saveTrimmedResultAnalysis(currentProject.getConstructPropertyPanel().getConceptDriftManagementPanel(),
                new File(saveDir, "PropertyTrimmedResultAnalysis.txt"));
        saveProjectInfo(currentProject, new File(saveDir, "projectInfo.txt"));
        STATUS_BAR.setText(Translator.getString("StatusBar.Message.SaveProjectDone") + " ----- "
                + java.util.Calendar.getInstance().getTime() + ": " + currentProject.getTitle());
    }

    public void loadIDTypicalWord(DODDLEProject currentProject, File file) {
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

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
            constructClassPanel.loadIDTypicalWord(idTypicalWordMap);
            if (OptionDialog.isNounAndVerbConceptHierarchyConstructionMode()) {
                constructPropertyPanel.loadIDTypicalWord(idTypicalWordMap);
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
        DisambiguationPanel disambiguationPanel = currentProject.getDisambiguationPanel();
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            StringBuffer buf = new StringBuffer();
            buf.append(BASE_URI + "\n");
            buf.append("利用可能な汎用辞書: " + ontSelectionPanel.getEnableDicList() + "\n");
            if (disambiguationPanel.getInputWordModelSet() != null) {
                buf.append("入力単語数: " + disambiguationPanel.getInputWordModelSet().size() + "\n");
            }
            buf.append("完全照合単語数: " + disambiguationPanel.getPerfectMatchedWordCnt() + "\n");
            buf.append("部分照合単語数: " + disambiguationPanel.getPartialMatchedWordCnt() + "\n");
            buf.append("照合単語数: " + disambiguationPanel.getMatchedWordCnt() + "\n");
            buf.append("未照合単語数: " + disambiguationPanel.getUndefinedWordCnt() + "\n");

            if (disambiguationPanel.getInputConceptSet() != null) {
                buf.append("入力概念数: " + disambiguationPanel.getInputConceptSet().size() + "\n");
            }
            if (disambiguationPanel.getInputNounConceptSet() != null) {
                buf.append("入力名詞的概念数: " + disambiguationPanel.getInputNounConceptSet().size() + "\n");
            }
            if (disambiguationPanel.getInputVerbConceptSet() != null) {
                buf.append("入力動詞的概念数: " + disambiguationPanel.getInputVerbConceptSet().size() + "\n");
            }

            buf.append("クラス階層構築における追加SIN数: " + constructClassPanel.getAddedSINNum() + "\n");
            buf.append("剪定前クラス数: " + constructClassPanel.getBeforeTrimmingConceptNum() + "\n");
            buf.append("剪定クラス数: " + constructClassPanel.getTrimmedConceptNum() + "\n");
            int afterTrimmingConceptNum = constructClassPanel.getAfterTrimmingConceptNum();
            buf.append("剪定後クラス数: " + afterTrimmingConceptNum + "\n");

            buf.append("プロパティ階層構築における追加SIN数: " + constructPropertyPanel.getAddedSINNum() + "\n");
            buf.append("剪定前プロパティ数: " + constructPropertyPanel.getBeforeTrimmingConceptNum() + "\n");
            buf.append("剪定プロパティ数: " + constructPropertyPanel.getTrimmedConceptNum() + "\n");
            int afterTrimmingPropertyNum = constructPropertyPanel.getAfterTrimmingConceptNum();
            buf.append("剪定後プロパティ数: " + afterTrimmingPropertyNum + "\n");

            buf.append("追加抽象中間クラス数: " + constructClassPanel.getAddedAbstractComplexConceptCnt() + "\n");
            buf.append("抽象中間クラスの平均兄弟クラスグループ化数: "
                    + constructClassPanel.getAverageAbstracComplexConceptGroupSiblingConceptCnt() + "\n");

            buf.append("追加抽象中間プロパティ数: " + constructPropertyPanel.getAddedAbstractComplexConceptCnt() + "\n");
            buf.append("抽象中間プロパティの平均兄弟プロパティグループ化数: "
                    + constructPropertyPanel.getAverageAbstracComplexConceptGroupSiblingConceptCnt() + "\n");

            int lastClassNum = constructClassPanel.getAllConceptCnt();
            int lastPropertyNum = constructPropertyPanel.getAllConceptCnt();

            buf.append("複合語クラス数: " + (lastClassNum - afterTrimmingConceptNum) + "\n");
            buf.append("複合語プロパティ数: " + (lastPropertyNum - afterTrimmingPropertyNum) + "\n");

            buf.append("最終クラス数: " + lastClassNum + "\n");
            buf.append("最終プロパティ数: " + lastPropertyNum + "\n");

            buf.append("平均兄弟クラス数: " + constructClassPanel.getChildCntAverage() + "\n");
            buf.append("平均兄弟プロパティ数: " + constructPropertyPanel.getChildCntAverage() + "\n");

            writer.write(buf.toString());
            writer.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void saveIDTypicalWord(DODDLEProject currentProject, File file) {
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            Map idTypicalWordMap = constructClassPanel.getIDTypicalWordMap();
            idTypicalWordMap.putAll(constructPropertyPanel.getIDTypicalWordMap());
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

    public void saveTrimmedResultAnalysis(ConceptDriftManagementPanel conceptDriftManagementPanel, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            StringBuilder builder = new StringBuilder();

            List<ConceptTreeNode> conceptTreeNodeList = conceptDriftManagementPanel.getTRAResult();
            for (ConceptTreeNode traNode : conceptTreeNodeList) {
                builder.append(traNode.getConcept().getIdentity());
                builder.append(",");
                ConceptTreeNode parentNode = (ConceptTreeNode) traNode.getParent();
                builder.append(parentNode.getConcept().getIdentity());
                builder.append(",");
                List<List<Concept>> trimmedConceptList = traNode.getTrimmedConceptList();
                for (List<Concept> list : trimmedConceptList) {
                    builder.append("|");
                    for (Concept tc : list) {
                        builder.append(tc.getIdentity());
                        builder.append(",");
                    }
                }
                builder.append("\n");
            }
            writer.write(builder.toString());
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
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
        ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();

        Model ontology = JenaModelMaker.makeClassModel(constructClassPanel.getTreeModelRoot(), ModelFactory
                .createDefaultModel());
        JenaModelMaker.makePropertyModel(constructPropertyPanel.getTreeModelRoot(), ontology);
        conceptDefinitionPanel.addConceptDefinition(ontology);
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
        DisambiguationPanel disambiguationPanel = currentProject.getDisambiguationPanel();
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

        if (!file.exists()) { return; }
        constructClassPanel.init();
        constructPropertyPanel.init();
        currentProject.resetIDConceptMap();
        ConceptTreeMaker.getInstance().setInputConceptSet(disambiguationPanel.getInputConceptSet());
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            Model model = ModelFactory.createDefaultModel();
            model.read(reader, BASE_URI, "RDF/XML");
            currentProject.initUserIDCount();
            TreeNode rootNode = ConceptTreeMaker.getInstance().getConceptTreeRoot(currentProject, model,
                    ResourceFactory.createResource(BASE_URI + ConceptTreeMaker.DODDLE_CLASS_ROOT_ID));
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
            constructClassPanel.setConceptTreeModel(treeModel);
            constructClassPanel.setVisibleConceptTree(true);
            constructClassPanel.checkMultipleInheritance(treeModel);
            currentProject.setUserIDCount(currentProject.getUserIDCount() + 1);
            ConceptTreeMaker.getInstance().conceptDriftManagement(treeModel);
            constructClassPanel.setConceptDriftManagementResult();
            treeModel.reload();

            currentProject.setUserIDCount(currentProject.getUserIDCount());
            rootNode = ConceptTreeMaker.getInstance().getPropertyTreeRoot(currentProject, model,
                    ResourceFactory.createResource(BASE_URI + ConceptTreeMaker.DODDLE_PROPERTY_ROOT_ID));
            treeModel = new DefaultTreeModel(rootNode);
            constructPropertyPanel.setConceptTreeModel(treeModel);
            constructPropertyPanel.setVisibleConceptTree(true);
            constructPropertyPanel.checkMultipleInheritance(treeModel);
            ConceptTreeMaker.getInstance().conceptDriftManagement(treeModel);
            constructPropertyPanel.setConceptDriftManagementResult();
            treeModel.reload();
            currentProject.setUserIDCount(currentProject.getUserIDCount() + 1);

            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void setPath() {
        String configPath = getExecPath();
        File configFile = new File(configPath + "config.txt");
        if (configFile.exists()) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(configPath + "config.txt"));
                SEN_HOME = properties.getProperty("SEN_HOME");
                DODDLE_DIC = properties.getProperty("DODDLE_DIC");
                DODDLE_EDRT_DIC = properties.getProperty("DODDLE_EDRT_DIC");
                DocumentSelectionPanel.PERL_EXE = properties.getProperty("PERL_EXE");
                DocumentSelectionPanel.CHASEN_EXE = properties.getProperty("CHASEN_EXE");
                DocumentSelectionPanel.SS_TAGGER_HOME = properties.getProperty("SSTAGGER_HOME");
                DocumentSelectionPanel.XDOC2TXT_EXE = properties.getProperty("XDOC2TXT_EXE");
                BASE_URI = properties.getProperty("BASE_URI");
                BASE_PREFIX = properties.getProperty("BASE_PREFIX");
                PROJECT_DIR = properties.getProperty("PROJECT_DIR");
                UpperConceptManager.UPPER_CONCEPT_LIST = properties.getProperty("UPPER_CONCEPT_LIST");
                WORDNET_PATH = properties.getProperty("WORDNET_PATH");
                if (properties.getProperty("USING_DB").equals("true")) {
                    IS_USING_DB = true;
                }
                DODDLE.LANG = properties.getProperty("LANG");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        longopts[3] = new LongOpt("LANG", LongOpt.REQUIRED_ARGUMENT, null, 'l');
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
            case 'l':
                DODDLE.LANG = g.getOptarg();
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
        if (DODDLE.LANG == null) {
            DODDLE.LANG = "ja";
        }
        Translator.loadResourceBundle(DODDLE.LANG);
    }

    public static String getExecPath() {
        if (doddlePlugin == null) { return "." + File.separator; }
        String jarPath = DODDLE.class.getClassLoader().getResource("").getFile();
        File file = new File(jarPath);
        String configPath = file.getAbsolutePath() + File.separator;
        return configPath;
    }

    public static void main(String[] args) {
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