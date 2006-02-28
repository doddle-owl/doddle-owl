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
    private EDRViews views;

    /**
     * Open the database and views.
     */
    public DBManager(boolean isReadOnly, String dicPath) throws DatabaseException, FileNotFoundException {
        db = new EDRDatabase(dicPath, isReadOnly);
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
                    return getMaxEvalValue(EDRTree.getEDRTree().getSiblingIDsSet(id), id);
                }

                /**
                 * @param id
                 * @return
                 */
                private int cntRelevantSupConcepts(String id) {
                    return getMaxEvalValue(EDRTree.getEDRTree().getPathToRootSet(id), id);
                }

                /**
                 * @param id
                 * @return
                 */
                private int cntRelevantSubConcepts(String id) {
                    return getMaxEvalValue(EDRTree.getEDRTree().getSubIDsSet(id), id);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isIncludeInputWords(Concept c) {
        String[] jpWords = c.getJaWords();
        for (int j = 0; j < jpWords.length; j++) {
            if (wordSet.contains(jpWords[j])) { return true; }
        }
        String[] enWords = c.getEnWords();
        for (int j = 0; j < enWords.length; j++) {
            if (wordSet.contains(enWords[j])) { return true; }
        }
        if (wordSet.contains(c.getJaExplanation())) { return true; }
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

    public Set<String> getEDRIDSet(String subIW) {
        String idListStr = (String) views.getWordIDSetMap().get(subIW);
        if (idListStr == null) { return null; }
        String[] idListArray = idListStr.replaceAll("\n", "").split("\\s");
        return new HashSet<String>(Arrays.asList(idListArray));
    }

    public Map getWordIDSetMap() {
        return views.getWordIDSetMap();
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

    public void test(boolean isSpecial) throws Exception {
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        runner.run(new EDRDatabaseTester(isSpecial));
    }

    public void makeDB(String prefix, boolean isSpecial) throws Exception {
        TransactionRunner runner = new TransactionRunner(db.getEnvironment());
        runner.run(new EDRDatabaseMaker(prefix, isSpecial));
    }

    private class EDRDatabaseTester implements TransactionWorker {

        boolean isSpecial;

        EDRDatabaseTester(boolean t) {
            isSpecial = t;
        }

        void testWordIDSetDBAccess() {
            Map wordIDSetMap = views.getWordIDSetMap();
            String idStr = (String) wordIDSetMap.get("概念");
            System.out.println(idStr);
            idStr = (String) wordIDSetMap.get("起爆");
            System.out.println(idStr);
            idStr = (String) wordIDSetMap.get("起動");
            System.out.println(idStr);
        }

        void testIDConceptDBAccess() {
            Map<String, Concept> idConceptMap = views.getIDConceptMap();
            Concept c = idConceptMap.get("3d02a7");
            System.out.println(c.getJaWord());
            System.out.println(c.getEnWord());
            c = idConceptMap.get("444d17");
            System.out.println(c.getJaWord());
            System.out.println(c.getEnWord());
            c = idConceptMap.get("0ebb6e");
            System.out.println(c.getJaWord());
            System.out.println(c.getEnWord());
        }

        void testEDRTIDConceptDBAccess() {
            Map<String, Concept> idConceptMap = views.getIDConceptMap();
            Concept c = idConceptMap.get("3cbda3");
            System.out.println(c.getJaWord());
            System.out.println(c.getEnWord());
            c = idConceptMap.get("3c84ef");
            System.out.println(c.getJaWord());
            System.out.println(c.getEnWord());
        }

        void testEDRTWordIDSetDBAccess() {
            Map wordIDSetMap = views.getWordIDSetMap();
            String idStr = (String) wordIDSetMap.get("デジタル通信技術");
            System.out.println(idStr);
            idStr = (String) wordIDSetMap.get("デジタル論理回路検査");
            System.out.println(idStr);
        }

        public void doWork() throws Exception {
            if (isSpecial) {
                System.out.println("EDRT idDefinitionMap Test");
                testEDRTIDConceptDBAccess();
                System.out.println("EDRT wordIDSetMap Test");
                testEDRTWordIDSetDBAccess();
            } else {
                System.out.println("EDR idDefinitionMap Test");
                testIDConceptDBAccess();
                System.out.println("EDR wordIDSetMap Test");
                testWordIDSetDBAccess();
            }
        }
    }

    private class EDRDatabaseMaker implements TransactionWorker {

        private String prefix;
        private boolean isSpecial;
        private String ID_DEFINITION_MAP = "C:/DODDLE_DIC/idDefinitionMapforEDR.txt";
        private String WORD_IDSet_MAP = "C:/DODDLE_DIC/wordIDSetMapforEDR.txt";
        private String EDRT_ID_DEFINITION_MAP = "C:/DODDLE_EDRT_DIC/idDefinitionMapforEDR.txt";

        private String EDRT_WORD_IDSet_MAP = "C:/DODDLE_EDRT_DIC/wordIDSetMapforEDR.txt";

        EDRDatabaseMaker(String prefix, boolean isSpecial) {
            this.prefix = prefix;
            this.isSpecial = isSpecial;
        }

        public void makeWordIDSetDB(String path) {
            System.out.println("Make WordIDSetDB Start");
            try {
                InputStream inputStream = new FileInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

                String line = reader.readLine().replaceAll("\n", "");
                String[] allWordList = line.split("\t");
                line = reader.readLine().replaceAll("\n", "");
                String[] wordIDSetList = line.split("\\|");

                System.out.println("all word size: " + allWordList.length);
                System.out.println("word IDSet size: " + wordIDSetList.length);
                Map wordIDSetMap = views.getWordIDSetMap();
                for (int i = 0; i < allWordList.length; i++) {
                    if (allWordList[i].replaceAll("\\s*", "").length() == 0) {
                        System.out.println("空白文字: " + allWordList[i]);
                        continue;
                    }
                    wordIDSetMap.put(allWordList[i], wordIDSetList[i]);
                    if (i % 10000 == 0) {
                        System.out.println(i + "/" + allWordList.length);
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.out.println("Make WordIDSetDB Done");
        }

        void makeIDConceptDB(String path) {
            System.out.println("Make idConceptDB Start");
            try {
                InputStream inputStream = new FileInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF16"));

                String line = reader.readLine().replaceAll("\n", "");
                String[] allIDList = line.split("\\|");
                System.out.println("id size: " + allIDList.length);
                line = reader.readLine().replaceAll("\n", "");
                String[] definitionList = line.split("\"");
                System.out.println("definition list size: " + definitionList.length);

                Map idConceptMap = views.getIDConceptMap();
                for (int i = 0; i < allIDList.length; i++) {
                    String id = allIDList[i];
                    // System.out.println("id: "+id+"def: "+definitionList[i]);
                    Concept edrConcept = new Concept(id, definitionList[i].split("\\^"));
                    edrConcept.setPrefix(prefix);
                    idConceptMap.put(id, edrConcept); // データベースに追加
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Make idConceptDB Done");
        }

        public void doWork() throws Exception {
            if (isSpecial) {
                makeIDConceptDB(EDRT_ID_DEFINITION_MAP);
                makeWordIDSetDB(EDRT_WORD_IDSet_MAP);
            } else {
                makeIDConceptDB(ID_DEFINITION_MAP);
                makeWordIDSetDB(WORD_IDSet_MAP);
            }
        }
    }

    public void close() throws DatabaseException {
        db.close();
    }

    public static void main(String[] args) {
        DBManager edrDBManager = null;
        DBManager edrtDBManager = null;
        try {
            DODDLE.setPath();
            edrDBManager = new DBManager(false, DODDLE.DODDLE_DIC);
            edrtDBManager = new DBManager(false, DODDLE.DODDLE_EDRT_DIC);
            if (args.length == 1 && args[0].equals("-makeDB")) {
                edrDBManager.makeDB("edr", false);
                edrtDBManager.makeDB("edrt", true);
            } else if (args.length == 1 && args[0].equals("-test")) {
                edrDBManager.test(false);
                edrtDBManager.test(true);
            }
        } catch (Exception e) {
            // If an exception reaches this point, the last transaction did not
            // complete. If the exception is RunRecoveryException, follow
            // the Berkeley DB recovery procedures before running again.
            e.printStackTrace();
        } finally {
            if (edrDBManager != null || edrtDBManager != null) {
                try {
                    // Always attempt to close the database cleanly.
                    edrDBManager.close();
                    edrtDBManager.close();
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
        private StoredSortedMap wordIDSetMap;

        /**
         * Create the data bindings and collection views.
         */
        public EDRViews(EDRDatabase db) {
            ClassCatalog catalog = db.getClassCatalog();
            EntryBinding idConceptDataBinding = new SerialBinding(catalog, Concept.class);
            EntryBinding stringBinding = TupleBinding.getPrimitiveBinding(String.class);

            idConceptMap = new StoredSortedMap(db.getIDConceptDatabase(), stringBinding, idConceptDataBinding, true);
            wordIDSetMap = new StoredSortedMap(db.getWordIDsDatabase(), stringBinding, stringBinding, true);
        }

        public final StoredSortedMap getIDConceptMap() {
            return idConceptMap;
        }

        public final StoredSortedMap getWordIDSetMap() {
            return wordIDSetMap;
        }
    }
}
