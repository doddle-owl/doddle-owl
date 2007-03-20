/*
 * @(#)  2007/03/13
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.net.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;

import org.apache.log4j.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author takeshi morita
 */
public class SwoogleWebServiceWrapper {

    private static final int MAX_CNT = 1;
    
    private static List<String> owlOntologyList;
    private static List<String> swoogleQueryList;

    private static NameSpaceTable nsTable;
    private static SwoogleWebServiceData swoogleWebServiceData = new SwoogleWebServiceData();

    private static final String SWOOGLE_WEB_SERVICE_URI = "http://logos.cs.umbc.edu:8080/swoogle31/q?";
    private static final String SWOOGLE_WEB_SERVICE_KEY = "&key=demo";
    public static String SWOOGLE_QUERY_RESULTS_DIR = "C:/DODDLE-OWL/swoogle_query_results/";
    private static String SWOOGLE_QUERY_RESULT_LIST_FILE = "swoogle_query_files.txt";
    public static String OWL_ONTOLOGIES_DIR = "C:/DODDLE-OWL/owl_ontologies/";
    private static String OWL_ONTOLOGIY_RESULT_LIST_FILE = "owl_files.txt";

    private static final String ONTOLOGY_URL = "ontology_url";
    private static final String ONTOLOGY_RANK = "ontoRank";
    private static final String SWT_URI = "swt_uri";
    private static final String TERM_RANK = "termRank";
    private static final String PROPERTY = "property";
    private static final String CLASS = "class";
    private static final String ENCODING = "encoding";
    private static final String RDF_TYPE = "rdf_type";
    private static final String REFERENCE_TYPE = "reference_type";

    public static final String RESOURCE_DIR = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";

    public static SwoogleWebServiceData getSwoogleWebServiceData() {
        return swoogleWebServiceData;
    }

    private static void initOWLOntologyList() {
        owlOntologyList = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(OWL_ONTOLOGIES_DIR
                    + OWL_ONTOLOGIY_RESULT_LIST_FILE)));
            while (reader.ready()) {                
                String line = reader.readLine();
                owlOntologyList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initSwoogleQueryList() {
        swoogleQueryList = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(SWOOGLE_QUERY_RESULTS_DIR
                    + SWOOGLE_QUERY_RESULT_LIST_FILE)));
            while (reader.ready()) {
                String line = reader.readLine();
                swoogleQueryList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存されているSwoogleクエリー結果からswoogleWebServiceDataのuriSwoogleOWLMetaDataMapにOWLメタデータを格納する
     */
    private static void initSwoogleOWLMetaData() {
        QueryExecution qexec = null;
        try {
            for (int i = 0; i < swoogleQueryList.size(); i++) {
                String queryTypeAndSearchString = swoogleQueryList.get(i);
                int index = swoogleQueryList.lastIndexOf(queryTypeAndSearchString);
                if (queryTypeAndSearchString.indexOf("queryType=search_swd_ontology") != -1 || queryTypeAndSearchString.indexOf("queryType=digest_swd") != -1) {
                    File file = new File(SWOOGLE_QUERY_RESULTS_DIR+"query_"+(index+1));
                    Model model = getModel(new FileInputStream(file), DODDLE.BASE_URI);
                    String sparqlQueryString = SPARQLQueryUtil.getQueryString(getSearchOntologyQuery());
                    Query query = QueryFactory.create(sparqlQueryString);
                    qexec = QueryExecutionFactory.create(query, model);
                    ResultSet results = qexec.execSelect();
                    while (results.hasNext()) {
                        QuerySolution qs = results.nextSolution();
                        loadOWLMetaData(qs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    public static void setNameSpaceTable(NameSpaceTable nstbl) {
        nsTable = nstbl;
    }

    public static void deleteOWLOntologies() {
        File ontologyDir = new File(OWL_ONTOLOGIES_DIR);
        File[] files = ontologyDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // files[i].delete();
        }
    }

    public static void deleteSwoogleQueryResults() {
        File ontologyDir = new File(SWOOGLE_QUERY_RESULTS_DIR);
        File[] files = ontologyDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // files[i].delete();
        }
    }

    private static InputStream getSearchOntologyQuery() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "swoogle_queries/SearchOntology.rq");
    }

    private static InputStream getSearchTermQuery() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "swoogle_queries/SearchTerm.rq");
    }

    private static InputStream getListDocumentsUsingTermQuery() {
        return DODDLE.class.getClassLoader().getResourceAsStream(
                RESOURCE_DIR + "swoogle_queries/listDocumentsUsingTerm.rq");
    }

    private static InputStream getListPropertiesOfaRegionClassQuery() {
        return DODDLE.class.getClassLoader().getResourceAsStream(
                RESOURCE_DIR + "swoogle_queries/listPropertiesOfaRegionClass.rq");
    }

    private static InputStream getListRegionClassesOfaPropertyQuery() {
        return DODDLE.class.getClassLoader().getResourceAsStream(
                RESOURCE_DIR + "swoogle_queries/listRegionClassesOfaProperty.rq");
    }

    private static Model getModel(InputStream inputStream, String baseURI) {
        Model model = ModelFactory.createDefaultModel();
        try {
            model.read(inputStream, baseURI, "RDF/XML");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    private static void saveFile(File file, InputStream inputStream, String encoding) {
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
            reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
            String line = "";
            while ((line = reader.readLine()) != null) { // reader.ready()を使うと書き込み途中で終了する場合がある.
                writer.write(line);
                writer.write("\n");
            }
        } catch (FileNotFoundException fne) {
            System.out.println("fileName error: " + file);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static void appendURI(String uri, File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            writer.append(uri);
            writer.append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void appendOntology(String url) {
        appendURI(url, new File(OWL_ONTOLOGIES_DIR + OWL_ONTOLOGIY_RESULT_LIST_FILE));
        owlOntologyList.add(url);
    }

    private static void appendQuery(String url) {
        appendURI(url, new File(SWOOGLE_QUERY_RESULTS_DIR + SWOOGLE_QUERY_RESULT_LIST_FILE));
        swoogleQueryList.add(url);
    }

    private static void saveOntology(String url, File file, InputStream inputStream, String encoding) {
        saveFile(file, inputStream, encoding);
        appendOntology(url);
    }

    private static void saveQueryResult(String queryTypeAndSearchString,  File file, InputStream inputStream) {        
        saveFile(file, inputStream, "UTF-8");
        try {
            DODDLE.getLogger().log(Level.DEBUG, "sleep 2 sec");
            Thread.sleep(2000); // 1秒間間隔をあけてアクセスする
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        appendQuery(queryTypeAndSearchString);
    }

    private static Model getSwoogleQueryResultModel(String queryTypeAndSearchString) {
        String restQuery = SWOOGLE_WEB_SERVICE_URI+queryTypeAndSearchString+SWOOGLE_WEB_SERVICE_KEY;
        Model model = null;
        try {   
            if (!SWOOGLE_QUERY_RESULTS_DIR.endsWith(File.separator)) {
                SWOOGLE_QUERY_RESULTS_DIR += File.separator;
            }
            int index = swoogleQueryList.lastIndexOf(queryTypeAndSearchString);
            index += 1;
            File queryCachFile = new File(SWOOGLE_QUERY_RESULTS_DIR+"query_"+index);
            if (queryCachFile.exists()) {                
                DODDLE.getLogger().log(Level.DEBUG, "Using Cashed Data");
                model = getModel(new FileInputStream(queryCachFile), DODDLE.BASE_URI);
            } else {
                queryCachFile = new File(SWOOGLE_QUERY_RESULTS_DIR+"query_"+(swoogleQueryList.size()+1));
                URL url = new URL(restQuery);            
                saveQueryResult(queryTypeAndSearchString, queryCachFile, url.openStream());
                model = getModel(url.openStream(), DODDLE.BASE_URI);
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return model;
    }

    private static void loadOWLMetaData(QuerySolution qs) {
        Resource ontologyURL = (Resource) qs.get(ONTOLOGY_URL);
        Literal ontoRankLiteral = (Literal) qs.get(ONTOLOGY_RANK);
        Literal encoding = (Literal) qs.get(ENCODING);
        Resource rdfType = (Resource) qs.get(RDF_TYPE);
        SwoogleOWLMetaData owlMetaData = new SwoogleOWLMetaData(ontologyURL.getURI(), encoding.getString(), rdfType
                .getURI(), ontoRankLiteral.getDouble());
        swoogleWebServiceData.putSwoogleOWLMetaData(ontologyURL.getURI(), owlMetaData);
    }

    private static void saveOntology(QuerySolution qs) {
        try {
            Resource ontologyURL = (Resource) qs.get(ONTOLOGY_URL);
            Literal ontoRankLiteral = (Literal) qs.get(ONTOLOGY_RANK);
            Literal encoding = (Literal) qs.get(ENCODING);
            Resource rdfType = (Resource) qs.get(RDF_TYPE);
            SwoogleOWLMetaData owlMetaData = new SwoogleOWLMetaData(ontologyURL.getURI(), encoding.getString(), rdfType
                    .getURI(), ontoRankLiteral.getDouble());
            swoogleWebServiceData.putSwoogleOWLMetaData(ontologyURL.getURI(), owlMetaData);
            URL ontURL = new URL(ontologyURL.getURI());
            if (!OWL_ONTOLOGIES_DIR.endsWith(File.separator)) {
                OWL_ONTOLOGIES_DIR += File.separator;
            }
            int index = owlOntologyList.lastIndexOf(owlMetaData.getURL());
            index += 1;
            File ontFile = new File(OWL_ONTOLOGIES_DIR + "onto_"+index);
            if (!ontFile.exists()) {
                DODDLE.getLogger().log(Level.DEBUG, "Save Ontology: " + ontologyURL);
                try {
                    ontFile = new File(OWL_ONTOLOGIES_DIR + "onto_"+(owlOntologyList.size()+1));
                    saveOntology(ontologyURL.getURI(), ontFile, ontURL.openStream(), owlMetaData.getFileEncoding());
                } catch (Exception e) {
                    DODDLE.getLogger().log(Level.DEBUG, "ignore exception !!");
                }
            } else {
                DODDLE.getLogger().log(Level.DEBUG, "Using Cashed Data");
            }
            Model ontModel = getModel(new FileInputStream(ontFile), Utils.getNameSpace(ontologyURL));
            if (swoogleWebServiceData.getRefOntology(ontologyURL.getURI()) == null) {
                DODDLE.getLogger().log(Level.DEBUG, "Regist Ontology: " + ontologyURL);
                ReferenceOWLOntology refOnto = new ReferenceOWLOntology(ontModel, ontologyURL.getURI(), nsTable);
                refOnto.getOntologyRank().setSwoogleOntoRank(owlMetaData.getOntoRank());
                swoogleWebServiceData.putRefOntology(ontologyURL.getURI(), refOnto);
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (FileNotFoundException fne) {
            System.out.println("FileNotFoundException");
        }
    }

    /**
     * 獲得したオントロジーの中から入力単語に関連するクラス及びプロパティを抽出する
     */
    public static void setSWTSet(Set<String> inputWordSet) {
        for (String uri : swoogleWebServiceData.getRefOntologyURISet()) {
            ReferenceOWLOntology refOnt = swoogleWebServiceData.getRefOntology(uri);
            for (String inputWord : inputWordSet) {
                // 英単語はすべて小文字に変換してから検索する
                Set<String> conceptURISet = refOnt.getURISet(inputWord.toLowerCase());
                if (conceptURISet == null) {
                    continue;
                }
                for (String conceptURI : conceptURISet) {
                    Resource conceptResource = ResourceFactory.createResource(conceptURI);
                    if (refOnt.getPropertySet().contains(conceptURI)) {
                        swoogleWebServiceData.addProperty(conceptResource);
                    } else {
                        swoogleWebServiceData.addClass(conceptResource);
                    }
                }
            }
        }
    }

    /**
     * 入力単語に関連するオントロジーを獲得
     * 
     * @param inputWord
     */
    public static void searchOntology(String inputWord) {
        QueryExecution qexec = null;
        try {
            String queryTypeAndSearchString = "queryType=search_swd_ontology&searchString=def:" + inputWord;
            DODDLE.getLogger().log(Level.DEBUG, "Search Ontology: " + inputWord);
            DODDLE.STATUS_BAR.setText("Search Ontology: " + inputWord);
            Model model = getSwoogleQueryResultModel(queryTypeAndSearchString);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getSearchOntologyQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                saveOntology(qs);
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    /**
     * 入力単語に関連するSWTを獲得
     * 
     * @param maxCnt
     * @param inputWord
     * @param type
     */
    public static void searchTerms(int maxCnt, String inputWord, String type) {
        QueryExecution qexec = null;
        try {
            String searchString = "(localname:" + inputWord + ") (type:owl." + type + " OR type.rdfs." + type + ")";
            String queryTypeAndSearchString = "queryType=search_swt&searchString=" + URLEncoder.encode(searchString, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search Terms: " + inputWord);
            DODDLE.STATUS_BAR.setText("Search Terms: " + inputWord);
            Model model = getSwoogleQueryResultModel(queryTypeAndSearchString);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getSearchTermQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            int cnt = 0;
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                if (cnt == maxCnt) {
                    break;
                }
                cnt++;
                Resource swtURI = (Resource) qs.get(SWT_URI);
                Literal termRank = (Literal) qs.get(TERM_RANK);
                if (type.equals(CLASS)) {
                    swoogleWebServiceData.addClass(swtURI);
                } else if (type.equals("PROPERTY")) {
                    swoogleWebServiceData.addProperty(swtURI);
                }
                swoogleWebServiceData.putTermRank(swtURI.getURI(), termRank.getDouble());
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    private static void searchListPropertiesOfaRegionClass(String queryTypeAndSearchString, String regionURI, Property regionType) {
        Resource region = ResourceFactory.createResource(regionURI);
        QueryExecution qexec = null;
        try {
            Model model = getSwoogleQueryResultModel(queryTypeAndSearchString);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getListPropertiesOfaRegionClassQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource property = (Resource) qs.get(PROPERTY);
                swoogleWebServiceData.addProperty(property);
                if (regionType == RDFS.domain) {
                    swoogleWebServiceData.addPropertyDomain(property, region);
                } else if (regionType == RDFS.range) {
                    swoogleWebServiceData.addPropertyRange(property, region);
                }
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    /**
     * 入力したクラスを定義域とするプロパティを獲得
     */
    public static void searchListPropertiesOfaDomainClass(String domainURI) {
        try {
            String queryTypeAndSearchString = "queryType=rel_swd_instance_domain_c2p&searchString="+ URLEncoder.encode(domainURI, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search List Properties Of a Domain Class: " + domainURI);
            DODDLE.STATUS_BAR.setText("Search List Properties Of a Domain Class: " + domainURI);
            searchListPropertiesOfaRegionClass(queryTypeAndSearchString, domainURI, RDFS.domain);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * 入力したクラスを値域とするプロパティを獲得
     */
    public static void searchListPropertiesOfaRangeClass(String rangeURI) {
        try {
            String queryTypeAndSearchString = "queryType=rel_swd_instance_range_c2p&searchString="+ URLEncoder.encode(rangeURI, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search List Properties Of a Range Class: " + rangeURI);
            DODDLE.STATUS_BAR.setText("Search List Properties Of a Range Class: " + rangeURI);
            searchListPropertiesOfaRegionClass(queryTypeAndSearchString, rangeURI, RDFS.range);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    private static void searchListRegionClassOfaProperty(String queryTypeAndSearchString, String propertyURI, Property regionType) {
        Resource property = ResourceFactory.createResource(propertyURI);
        QueryExecution qexec = null;
        try {
            Model model = getSwoogleQueryResultModel(queryTypeAndSearchString);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getListRegionClassesOfaPropertyQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource classResource = (Resource) qs.get(CLASS);
                if (swoogleWebServiceData.getClassSet().contains(classResource)) { // 入力単語に関連する定義域と値域のみを格納
                    if (regionType == RDFS.domain) {
                        swoogleWebServiceData.addPropertyDomain(property, classResource);
                    } else if (regionType == RDFS.range) {
                        swoogleWebServiceData.addPropertyRange(property, classResource);
                    }
                }
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    /**
     * プロパティのURIを入力として，そのプロパティの定義域を獲得
     */
    public static void searchListDomainClassOfaProperty(String propertyURI) {
        try {
            String queryTypeAndSearchString = "queryType=rel_swd_instance_domain_p2c&searchString="+ URLEncoder.encode(propertyURI, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search List Domain Class Of a Property: " + propertyURI);
            DODDLE.STATUS_BAR.setText("Search List Domain Class Of a Property: " + propertyURI);
            searchListRegionClassOfaProperty(queryTypeAndSearchString, propertyURI, RDFS.domain);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * プロパティのURIを入力として，そのプロパティの値域を獲得
     */
    public static void searchListRangeClassOfaProperty(String propertyURI) {
        try {
            String queryTypeAndSearchString = "queryType=rel_swd_instance_range_p2c&searchString="+ URLEncoder.encode(propertyURI, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search List Range Class Of a Property: " + propertyURI);
            DODDLE.STATUS_BAR.setText("Search List Range Class Of a Property: " + propertyURI);
            searchListRegionClassOfaProperty(queryTypeAndSearchString, propertyURI, RDFS.range);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * SWTが定義されているオントロジーを獲得
     */
    public static void searchListDocumentsUsingTerm(String swtURI) {
        QueryExecution qexec = null;
        try {
            String queryTypeAndSearchString = "queryType=rel_swt_swd&searchString="+ URLEncoder.encode(swtURI, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search List Documents Using Term: " + swtURI);
            DODDLE.STATUS_BAR.setText("Search List Documents Using Term: " + swtURI);
            Model model = getSwoogleQueryResultModel(queryTypeAndSearchString);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getListDocumentsUsingTermQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource ontologyURL = (Resource) qs.get(ONTOLOGY_URL);
                Resource referenceType = (Resource) qs.get(REFERENCE_TYPE);
                if (ontologyURL != null
                        && referenceType.getURI().equals(
                                "http://daml.umbc.edu/ontologies/webofbelief/1.4/wob.owl#hasClassDefinitionIn")
                        || referenceType.getURI().equals(
                                "http://daml.umbc.edu/ontologies/webofbelief/1.4/wob.owl#hasPropertyDefinitionIn")) {
                    SwoogleOWLMetaData owlMetaData = swoogleWebServiceData.getSwoogleOWLMetaData(ontologyURL.getURI());
                    if (owlMetaData == null) {
                        searchDigestSemanticWebDocument(ontologyURL.getURI());
                    }
                }
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    /**
     * SWDのメタデータを獲得
     */
    public static void searchDigestSemanticWebDocument(String swdURI) {
        QueryExecution qexec = null;
        try {
            String queryTypeAndSearchString = "queryType=digest_swd&searchString=" + URLEncoder.encode(swdURI, "UTF-8");
            DODDLE.getLogger().log(Level.DEBUG, "Search Digest Semantic Web Document: " + swdURI);
            Model model = getSwoogleQueryResultModel(queryTypeAndSearchString);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getSearchOntologyQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                saveOntology(qs);
            }
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    /**
     * 入力単語に関連するオントロジーを獲得する
     * 
     * @param inputWordSet
     */
    public static void acquireRelevantOWLOntologies(Set<String> inputWordSet) {
        DODDLE.STATUS_BAR.setText("Acquire OWL Ontologies Start");
        DODDLE.STATUS_BAR.startTime();
        DODDLE.STATUS_BAR.initNormal(5);
        DODDLE.STATUS_BAR.lock();

        int[] classCnt = new int[4];
        int[] propertyCnt = new int[4];

        for (String inputWord : inputWordSet) {
            searchTerms(MAX_CNT, inputWord, CLASS);
            searchTerms(MAX_CNT, inputWord, PROPERTY);
        }
        DODDLE.STATUS_BAR.addProjectValue();
        classCnt[0] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[0] = swoogleWebServiceData.getPropertySet().size();

        // get related properties
        for (Resource classResource : swoogleWebServiceData.getClassSet()) {
            searchListPropertiesOfaDomainClass(classResource.getURI());
            searchListPropertiesOfaRangeClass(classResource.getURI());
        }
        DODDLE.STATUS_BAR.addProjectValue();
        classCnt[1] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[1] = swoogleWebServiceData.getPropertySet().size();

        // get related domain and range
        for (Resource propertyResource : swoogleWebServiceData.getPropertySet()) {
            searchListDomainClassOfaProperty(propertyResource.getURI());
            searchListRangeClassOfaProperty(propertyResource.getURI());
        }
        DODDLE.STATUS_BAR.addProjectValue();
        swoogleWebServiceData.removeUnnecessaryPropertySet();

        classCnt[2] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[2] = swoogleWebServiceData.getPropertySet().size();

        Set<String> relatedWordSet = new HashSet<String>();
        relatedWordSet.addAll(inputWordSet);
        for (Resource property : swoogleWebServiceData.getPropertySet()) {
            relatedWordSet.add(Utils.getLocalName(property));
        }

        // 関連するオントロジーを獲得
        for (String word : relatedWordSet) {
            System.out.println("r: " + word);
            searchOntology(word);
        }
        setSWTSet(relatedWordSet);

        Set<Resource> conceptSet = swoogleWebServiceData.getConceptSet();
        // searchOntologyで獲得できなかったオントロジーを獲得
        for (Resource conceptResource : conceptSet) {
            boolean isDefinedConcept = false;
            for (String uri : swoogleWebServiceData.getRefOntologyURISet()) {
                ReferenceOWLOntology refOnto = swoogleWebServiceData.getRefOntology(uri);
                if (refOnto.getConcept(conceptResource.getURI()) != null) {
                    DODDLE.getLogger().log(Level.DEBUG, "defined concept: " + conceptResource);
                    isDefinedConcept = true;
                    break;
                }
            }
            if (!isDefinedConcept) {
                System.out.println("not defined: " + conceptResource);
                searchListDocumentsUsingTerm(conceptResource.getURI());
            }
        }

        DODDLE.STATUS_BAR.addProjectValue();
        classCnt[3] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[3] = swoogleWebServiceData.getPropertySet().size();

        for (int i = 0; i < classCnt.length; i++) {
            DODDLE.getLogger().log(Level.DEBUG, "1: class size: " + classCnt[i] + " propertySize: " + propertyCnt[i]);
        }
        DODDLE.STATUS_BAR.setText("calc Ontology Rank");
        swoogleWebServiceData.calcOntologyRank(inputWordSet);
        DODDLE.STATUS_BAR.addProjectValue();
        DODDLE.STATUS_BAR.unLock();
        DODDLE.STATUS_BAR.hideProgressBar();
        DODDLE.STATUS_BAR.setText("Acquire Ontologies done");
    }
    
    /** 
     * テスト用メソッド
     * 
     */
    public static void acquireRelevantOWLOntologiesTest(Set<String> inputWordSet) {
        int[] classCnt = new int[4];
        int[] propertyCnt = new int[4];

        for (String inputWord : inputWordSet) {
            searchTerms(MAX_CNT, inputWord, CLASS);
            searchTerms(MAX_CNT, inputWord, PROPERTY);
        }

        classCnt[0] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[0] = swoogleWebServiceData.getPropertySet().size();

        // get related properties
        for (Resource classResource : swoogleWebServiceData.getClassSet()) {
            searchListPropertiesOfaDomainClass(classResource.getURI());
            searchListPropertiesOfaRangeClass(classResource.getURI());
        }

        classCnt[1] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[1] = swoogleWebServiceData.getPropertySet().size();

        // get related domain and range
        for (Resource propertyResource : swoogleWebServiceData.getPropertySet()) {
            searchListDomainClassOfaProperty(propertyResource.getURI());
            searchListRangeClassOfaProperty(propertyResource.getURI());
        }
        swoogleWebServiceData.removeUnnecessaryPropertySet();

        classCnt[2] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[2] = swoogleWebServiceData.getPropertySet().size();

        Set<String> relatedWordSet = new HashSet<String>();
        relatedWordSet.addAll(inputWordSet);
        for (Resource property : swoogleWebServiceData.getPropertySet()) {
            relatedWordSet.add(Utils.getLocalName(property));
        }

        // 関連するオントロジーを獲得
        for (String word : relatedWordSet) {
            System.out.println("r: " + word);
            searchOntology(word);
        }
        setSWTSet(relatedWordSet);

        Set<Resource> conceptSet = swoogleWebServiceData.getConceptSet();
        // searchOntologyで獲得できなかったオントロジーを獲得
        for (Resource conceptResource : conceptSet) {
            boolean isDefinedConcept = false;
            for (String uri : swoogleWebServiceData.getRefOntologyURISet()) {
                ReferenceOWLOntology refOnto = swoogleWebServiceData.getRefOntology(uri);
                if (refOnto.getConcept(conceptResource.getURI()) != null) {
                    DODDLE.getLogger().log(Level.DEBUG, "defined concept: " + conceptResource);
                    isDefinedConcept = true;
                    break;
                }
            }
            if (!isDefinedConcept) {
                System.out.println("not defined: " + conceptResource);
                searchListDocumentsUsingTerm(conceptResource.getURI());
            }
        }

        classCnt[3] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[3] = swoogleWebServiceData.getPropertySet().size();

        for (int i = 0; i < classCnt.length; i++) {
            DODDLE.getLogger().log(Level.DEBUG, "1: class size: " + classCnt[i] + " propertySize: " + propertyCnt[i]);
        }
        swoogleWebServiceData.calcOntologyRank(inputWordSet);
    }
    
    public static void initSwoogleWebServiceWrapper() {
        initOWLOntologyList();
        initSwoogleQueryList();    
        initSwoogleOWLMetaData();
    }

    public static void main(String[] args) {
        Translator.loadResourceBundle("ja");
        DODDLE.STATUS_BAR = new StatusBarPanel();
        DODDLE.getLogger().setLevel(Level.DEBUG);
        initSwoogleWebServiceWrapper();
        SwoogleWebServiceWrapper.setNameSpaceTable(new NameSpaceTable());
        Set<String> inputWordSet = new HashSet<String>();
//      inputWordSet.add("business");
        //inputWordSet.add("organization");
        //inputWordSet.add("counteroffer");// 定義が少なすぎる
        // inputWordSet.add("person");// 定義が多すぎる
        inputWordSet.add("contract");
        SwoogleWebServiceWrapper.acquireRelevantOWLOntologies(inputWordSet);
    }
}
