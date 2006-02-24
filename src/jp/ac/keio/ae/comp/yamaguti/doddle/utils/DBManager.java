package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.Dictionary;
import net.java.sen.*;

import com.sleepycat.bind.*;
import com.sleepycat.bind.serial.*;
import com.sleepycat.bind.tuple.*;
import com.sleepycat.collections.*;
import com.sleepycat.je.*;

/**
 * @author takeshi morita
 */
public class DBManager {

    private EDRDatabase db;
    private static EDRViews views;

    /**
     * Open the database and views.
     */
    public DBManager(boolean isReadOnly) throws DatabaseException, FileNotFoundException {
        db = new EDRDatabase(DODDLE.DODDLE_DIC, isReadOnly);
        views = new EDRViews(db);
    }

    private Concept concept;
    private String conceptID;

    private Set<String> wordSet;
    private DODDLEProject project;
    private Map<String, Set<EvalConcept>> wordEvalConceptSetMap;

    public Map<String, Set<EvalConcept>> getWordEvalConceptSetMap() {
        return wordEvalConceptSetMap;
    }

    public void setWordEvalConceptSetMap(Map<String, Set<EvalConcept>> map) {
        wordEvalConceptSetMap = map;
    }

    /**
     * 
     * 多義性のある概念リストと入力語彙を入力として，評価値つき概念リストを返すメソッド
     * 
     */
    public void setWordEvalConceptSetMap(Set inputWordModelSet) {
        wordSet = new HashSet<String>();
        for (Iterator i = inputWordModelSet.iterator(); i.hasNext();) {
            InputWordModel iwModel = (InputWordModel) i.next();
            wordSet.add(iwModel.getMatchedWord());
        }
        wordEvalConceptSetMap = new HashMap<String, Set<EvalConcept>>();
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        try {
            if (runner == null) { return; }
            runner.run(new TransactionWorker() {

                public void doWork() throws Exception {
                    DODDLE.STATUS_BAR.startTime();
                    DODDLE.STATUS_BAR.initNormal(wordSet.size());
                    for (Iterator i = wordSet.iterator(); i.hasNext();) {
                        String inputWord = (String) i.next();
                        Set<Concept> conceptSet = wordConceptSetMap.get(inputWord);
                        Set<EvalConcept> evalConceptSet = new TreeSet<EvalConcept>();
                        for (Concept c : conceptSet) {
                            int evalValue = 0;
                            if (OptionDialog.isCheckSupConcepts()) {
                                evalValue += cntRelevantSupConcepts(c.getId());
                            }
                            if (OptionDialog.isCheckSubConcepts()) {
                                evalValue += cntRelevantSubConcepts(c.getId());
                            }
                            if (OptionDialog.isCheckSiblingConcepts()) {
                                evalValue += cntRelevantSiblingConcepts(c.getId());
                            }
                            evalConceptSet.add(new EvalConcept((Concept) views.getIDConceptMap().get(c.getId()),
                                    evalValue));
                        }
                        wordEvalConceptSetMap.put(inputWord, evalConceptSet);
                        DODDLE.STATUS_BAR.addValue();
                    }
                    DODDLE.STATUS_BAR.hideProgressBar();
                }

                private int getMaxEvalValue(Set idsSet, String id) {
                    int maxEvalValue = 0;
                    for (Iterator i = idsSet.iterator(); i.hasNext();) {
                        Collection idSet = (Collection) i.next();
                        int evalValue = 0;
                        for (Iterator j = idSet.iterator(); j.hasNext();) {
                            String sid = (String) j.next();
                            if (sid.equals(id)) {
                                continue;
                            }
                            Concept c = (Concept) views.getIDConceptMap().get(sid);
                            if (isIncludeInputWords(c)) {
                                evalValue++;
                            }
                        }
                        if (maxEvalValue < evalValue) {
                            maxEvalValue = evalValue;
                        }
                    }
                    return maxEvalValue;
                }

                /**
                 * 
                 * @param id
                 * @return 対象概念における入力語彙を含む兄弟概念数を返す
                 */
                private int cntRelevantSiblingConcepts(String id) {
                    return getMaxEvalValue(EDRTree.getInstance().getSiblingIDsSet(id), id);
                }

                /**
                 * @param id
                 * @return
                 */
                private int cntRelevantSupConcepts(String id) {
                    return getMaxEvalValue(EDRTree.getInstance().getPathToRootSet(id), id);
                }

                /**
                 * @param id
                 * @return
                 */
                private int cntRelevantSubConcepts(String id) {
                    return getMaxEvalValue(EDRTree.getInstance().getSubIDsSet(id), id);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isIncludeInputWords(Concept c) {
        String[] jpWords = c.getJpWords();
        for (int j = 0; j < jpWords.length; j++) {
            if (wordSet.contains(jpWords[j])) { return true; }
        }
        String[] enWords = c.getEnWords();
        for (int j = 0; j < enWords.length; j++) {
            if (wordSet.contains(enWords[j])) { return true; }
        }
        if (wordSet.contains(c.getJpExplanation())) { return true; }
        if (wordSet.contains(c.getEnExplanation())) { return true; }
        return false;
    }

    private Set<InputWordModel> inputWordModelSet;
    private Set<String> undefinedWordSet;
    private Map<String, Set<Concept>> wordConceptSetMap;

    public Set<InputWordModel> getInputWordModelSet() {
        return inputWordModelSet;
    }

    public Map<String, Set<Concept>> getWordConceptSetMap() {
        return wordConceptSetMap;
    }

    public Set<String> getUndefinedWordSet() {
        return undefinedWordSet;
    }

    public void initDataWithDB(Set<String> iwSet, DODDLEProject p) {
        project = p;
        wordSet = iwSet;
        inputWordModelSet = new TreeSet<InputWordModel>();
        undefinedWordSet = new TreeSet<String>();
        wordConceptSetMap = new HashMap<String, Set<Concept>>();

        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        try {
            runner.run(new TransactionWorker() {
                public void doWork() throws Exception {
                    DODDLE.STATUS_BAR.startTime();
                    DODDLE.STATUS_BAR.initNormal(wordSet.size());

                    Set<String> matchedWordSet = new HashSet<String>();
                    for (String word : wordSet) {
                        InputWordModel iwModel = setInputWord(word, project);
                        if (iwModel != null) {
                            matchedWordSet.add(iwModel.getMatchedWord());
                        }
                        DODDLE.STATUS_BAR.addValue();
                    }
                    // 部分照合した複合語中で，完全照合単語リストに含まれない照合した単語を完全照合単語として追加
                    matchedWordSet.removeAll(wordSet);
                    for (String matchedWord : matchedWordSet) {
                        InputWordModel iwModel = project.getInputModule().makeInputWordModel(matchedWord,
                                wordConceptSetMap);
                        if (iwModel != null) {
                            inputWordModelSet.add(iwModel);
                        }
                    }
                    DODDLE.STATUS_BAR.hideProgressBar();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getEDRIDSet(String subIW) {
        String idListStr = (String) views.getWordIDsMap().get(subIW);
        if (idListStr == null) { return null; }
        String[] idListArray = idListStr.replaceAll("\n", "").split("\t");
        return new HashSet<String>(Arrays.asList(idListArray));
    }

    public Map getWordIDsMap() {
        return views.getWordIDsMap();
    }

    private InputWordModel setInputWord(String iw, DODDLEProject p) {
        InputWordModel iwModel = p.getInputModuleUI().getInputModule().makeInputWordModel(iw, wordConceptSetMap);
        if (iwModel != null) {
            inputWordModelSet.add(iwModel);
        } else {
            undefinedWordSet.add(iw);
        }
        return iwModel;
    }

    private Map<String, Concept> wordConceptMap;
    private Map<InputWordModel, ConstructTreeOption> compoundConstructTreeOptionMap;
    private Set<Concept> conceptSet;

    public void setConceptSet(Map<String, Concept> wcm, Map<InputWordModel, ConstructTreeOption> cctom,
            Map<String, Set<Concept>> wcSetMap, Set<InputWordModel> iwModelSet) {
        wordConceptMap = wcm;
        compoundConstructTreeOptionMap = cctom;
        wordConceptSetMap = wcSetMap;
        inputWordModelSet = iwModelSet;
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        try {
            runner.run(new TransactionWorker() {
                public void doWork() throws Exception {
                    conceptSet = new HashSet<Concept>();
                    if (inputWordModelSet == null) { return; }
                    for (InputWordModel iwModel : inputWordModelSet) {
                        Concept c = wordConceptMap.get(iwModel.getWord());
                        if (c == null) {
                            if (iwModel.isPartialMatchWord()) {
                                c = wordConceptMap.get(iwModel.getMatchedWord());
                                wordConceptMap.put(iwModel.getWord(), c);
                            }
                            if (c == null) {
                                Set<Concept> conceptSet = wordConceptSetMap.get(iwModel.getMatchedWord());
                                if (conceptSet != null && 0 < conceptSet.size()) {
                                    c = (Concept) conceptSet.toArray()[0];
                                    wordConceptMap.put(iwModel.getWord(), c);
                                } 
                            }
                        }
                        if (c.equals(InputModuleUI.nullConcept)) {
                            compoundConstructTreeOptionMap.remove(iwModel);
                            continue;
                        }
                        if (iwModel.isPartialMatchWord()) {
                            ConstructTreeOption ctOption = new ConstructTreeOption(c);
                            compoundConstructTreeOptionMap.put(iwModel, ctOption);
                        }
                        conceptSet.add(c);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Concept> getConceptSet() {
        return conceptSet;
    }

    public void setEDRConcept(String id) {
        conceptID = id;
        concept = null;
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        try {
            runner.run(new TransactionWorker() {
                public void doWork() throws Exception {
                    concept = (Concept) views.getIDConceptMap().get(conceptID);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Concept getEDRConcept() {
        return concept;
    }

    public void test() throws Exception {
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        runner.run(new EDRDatabaseTester());
    }

    public void makeDB() throws Exception {
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        runner.run(new EDRDatabaseMaker());
    }

    private class EDRDatabaseTester implements TransactionWorker {

        void testWordIDsDBAccess() {
            Map wordIDsMap = views.getWordIDsMap();
            String idStr = (String) wordIDsMap.get("概念");
            System.out.println(idStr);
            idStr = (String) wordIDsMap.get("起爆");
            System.out.println(idStr);
            idStr = (String) wordIDsMap.get("起動");
            System.out.println(idStr);
        }

        void testIDConceptDBAccess() {
            Map<String, Concept> idConceptMap = views.getIDConceptMap();
            Concept c = idConceptMap.get("3d02a7");
            System.out.println(c.getJpWord());
            System.out.println(c.getEnWord());
            c = idConceptMap.get("444d17");
            System.out.println(c.getJpWord());
            System.out.println(c.getEnWord());
            c = idConceptMap.get("0ebb6e");
            System.out.println(c.getJpWord());
            System.out.println(c.getEnWord());
        }

        public void doWork() throws Exception {
            testIDConceptDBAccess();
            testWordIDsDBAccess();
        }
    }

    public static String ID_DEFINITION_MAP = "C:/DODDLE_DIC/idDefinitionMapforEDR.txt";
    public static String WORD_IDs_MAP = "C:/DODDLE_DIC/wordIDsMapforEDR.txt";

    private class EDRDatabaseMaker implements TransactionWorker {

        public void makeWordIDsDB() {
            System.out.println("Make WordIDsDB Start");
            try {
                InputStream inputStream = new FileInputStream(WORD_IDs_MAP);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

                String line = reader.readLine().replaceAll("\n", "");
                String[] allWordList = line.split("\t");
                line = reader.readLine().replaceAll("\n", "");
                String[] wordIDsList = line.split("\\|");

                Map wordIDsMap = views.getWordIDsMap();
                for (int i = 0; i < allWordList.length; i++) {
                    wordIDsMap.put(allWordList[i], wordIDsList[i]);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.out.println("Make WordIDsDB Done");
        }

        void makeIDConceptDB() {
            System.out.println("Make idConceptDB Start");
            try {
                InputStream inputStream = new FileInputStream(ID_DEFINITION_MAP);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF16"));

                String line = reader.readLine().replaceAll("\n", "");
                String[] allIDList = line.split("\\|");
                // System.out.println("id size: " + allIDList.length);
                line = reader.readLine().replaceAll("\n", "");
                String[] definitionList = line.split("\"");

                Map idConceptMap = views.getIDConceptMap();
                for (int i = 0; i < allIDList.length; i++) {
                    String id = allIDList[i];
                    Concept edrConcept = new Concept(id, definitionList[i].split("\\^"));
                    edrConcept.setPrefix("edr");
                    idConceptMap.put(id, edrConcept); // データベースに追加
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Make idConceptDB Done");
        }

        public void doWork() throws Exception {
            makeIDConceptDB();
            makeWordIDsDB();
        }
    }

    public void close() throws DatabaseException {
        db.close();
    }

    public static void main(String[] args) {
        DBManager dbManager = null;
        try {
            DODDLE.setPath("normal");
            dbManager = new DBManager(false);
            if (args.length == 1 && args[0].equals("-makeDB")) {
                dbManager.makeDB();
            } else if (args.length == 1 && args[0].equals("-test")) {
                dbManager.test();
            }
        } catch (Exception e) {
            // If an exception reaches this point, the last transaction did not
            // complete. If the exception is RunRecoveryException, follow
            // the Berkeley DB recovery procedures before running again.
            e.printStackTrace();
        } finally {
            if (dbManager != null) {
                try {
                    // Always attempt to close the database cleanly.
                    dbManager.close();
                    System.out.println("Close DB");
                } catch (Exception e) {
                    System.err.println("Exception during database close:");
                    e.printStackTrace();
                }
            }
        }
    }

    public class EDRDatabase {

        private static final String CLASS_CATALOG = "java_class_catalog";
        private static final String ID_WORD_MAP = "id_word_map.db";
        private static final String WORD_IDs_MAP = "word_ids_map.db";

        private Environment env;
        private Database idConceptDb;
        private Database wordIDsDb;
        private StoredClassCatalog javaCatalog;

        /**
         * Open all storage containers, indices, and catalogs.
         */
        public EDRDatabase(String homeDirectory, boolean isReadOnly) throws DatabaseException, FileNotFoundException {
            System.out.println("Opening environment in: " + homeDirectory);
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setTransactional(true);
            envConfig.setAllowCreate(true);
            env = new Environment(new File(homeDirectory), envConfig);

            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            dbConfig.setReadOnly(isReadOnly);

            Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);
            javaCatalog = new StoredClassCatalog(catalogDb);

            idConceptDb = env.openDatabase(null, ID_WORD_MAP, dbConfig);
            wordIDsDb = env.openDatabase(null, WORD_IDs_MAP, dbConfig);
        }

        /**
         * Return the storage environment for the database.
         */
        public final Environment getEnvironment() {
            return env;
        }

        /**
         * Return the class catalog.
         */
        public final StoredClassCatalog getClassCatalog() {
            return javaCatalog;
        }

        public final Database getIDConceptDatabase() {
            return idConceptDb;
        }

        public final Database getWordIDsDatabase() {
            return wordIDsDb;
        }

        public void close() throws DatabaseException {
            idConceptDb.close();
            wordIDsDb.close();
            javaCatalog.close();
            env.close();
        }
    }

    public class EDRViews {

        private StoredSortedMap idConceptMap;
        private StoredSortedMap wordIDsMap;

        /**
         * Create the data bindings and collection views.
         */
        public EDRViews(EDRDatabase db) {
            ClassCatalog catalog = db.getClassCatalog();
            EntryBinding idConceptDataBinding = new SerialBinding(catalog, Concept.class);
            EntryBinding stringBinding = TupleBinding.getPrimitiveBinding(String.class);

            idConceptMap = new StoredSortedMap(db.getIDConceptDatabase(), stringBinding, idConceptDataBinding, true);
            wordIDsMap = new StoredSortedMap(db.getWordIDsDatabase(), stringBinding, stringBinding, true);
        }

        public final StoredSortedMap getIDConceptMap() {
            return idConceptMap;
        }

        public final StoredSortedMap getWordIDsMap() {
            return wordIDsMap;
        }
    }
}
