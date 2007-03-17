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

    private static NameSpaceTable nsTable;
    private static SwoogleWebServiceData swoogleWebServiceData = new SwoogleWebServiceData();

    public static String SWOOGLE_QUERY_RESULTS_DIR = "C:/DODDLE-OWL/swoogle_query_results/";
    public static String OWL_ONTOLOGIES_DIR = "C:/DODDLE-OWL/owl_ontologies/";

    private static final String ONTOLOGY_URL = "ontology_url";
    private static final String ONTOLOGY_RANK = "ontoRank";
    private static final String PROPERTY = "property";
    private static final String CLASS = "class";
    private static final String ENCODING = "encoding";
    private static final String RDF_TYPE = "rdf_type";

    public static final String RESOURCE_DIR = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";

    public static SwoogleWebServiceData getSwoogleWebServiceData() {
        return swoogleWebServiceData;
    }

    /**
     * 保存されているSwoogleクエリー結果からswoogleWebServiceDataのuriSwoogleOWLMetaDataMapにOWLメタデータを格納する
     */
    public static void initSwoogleOWLMetaData() {
        QueryExecution qexec = null;
        try {
            File swoogleQueryResultsDir = new File(SWOOGLE_QUERY_RESULTS_DIR);
            File[] files = swoogleQueryResultsDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File queryResultFile = files[i];
                if (queryResultFile.getName().indexOf("queryType%3Dsearch_swd_ontology") != -1
                        || queryResultFile.getName().indexOf("queryType%3Ddigest_swd") != -1) {
                    Model model = getModel(new FileInputStream(queryResultFile));
                    String sparqlQueryString = SPARQLQueryUtil.getQueryString(getSearchOntologyQuery());
                    Query query = QueryFactory.create(sparqlQueryString);
                    qexec = QueryExecutionFactory.create(query, model);
                    ResultSet results = qexec.execSelect();
                    while (results.hasNext()) {
                        QuerySolution qs = results.nextSolution();
                        saveOntology(qs);
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
            files[i].delete();
        }
    }

    private static InputStream getSearchOntologyQuery() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "swoogle_queries/SearchOntology.rq");
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

    private static Model getModel(InputStream inputStream) {
        Model model = ModelFactory.createDefaultModel();
        try {
            model.read(inputStream, DODDLE.BASE_URI, "RDF/XML");
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

    private static void saveOntology(File file, InputStream inputStream, String encoding) {
        saveFile(file, inputStream, encoding);
    }

    private static void saveQueryResult(File file, InputStream inputStream) {
        saveFile(file, inputStream, "UTF-8");
        try {
            DODDLE.getLogger().log(Level.DEBUG, "sleep 2 sec");
            Thread.sleep(2000); // 1秒間間隔をあけてアクセスする
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private static Model getSwoogleQueryResultModel(String restQuery) {
        Model model = null;
        try {
            String encodedRestQuery = URLEncoder.encode(restQuery, "UTF-8");
            if (!SWOOGLE_QUERY_RESULTS_DIR.endsWith(File.separator)) {
                SWOOGLE_QUERY_RESULTS_DIR += File.separator;
            }
            File queryCachFile = new File(SWOOGLE_QUERY_RESULTS_DIR + encodedRestQuery);
            if (queryCachFile.exists()) {
                DODDLE.getLogger().log(Level.DEBUG, "Using Cashed Data");
                model = getModel(new FileInputStream(queryCachFile));
            } else {
                URL url = new URL(restQuery);
                saveQueryResult(queryCachFile, url.openStream());
                model = getModel(url.openStream());
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
            File ontFile = new File(OWL_ONTOLOGIES_DIR + owlMetaData.getEncodedURL());
            if (!ontFile.exists()) {
                DODDLE.getLogger().log(Level.DEBUG, "Save Ontology: " + ontologyURL);
                try {
                    saveOntology(ontFile, ontURL.openStream(), owlMetaData.getFileEncoding());
                } catch (Exception e) {
                    DODDLE.getLogger().log(Level.DEBUG, "ignore exception !!");
                }
            }
            Model ontModel = getModel(new FileInputStream(ontFile));
            if (swoogleWebServiceData.getRefOntology(ontologyURL.getURI()) == null) {
                DODDLE.getLogger().log(Level.DEBUG, "Regist Ontology: " + ontologyURL);
                ReferenceOWLOntology refOnto = new ReferenceOWLOntology(ontModel, ontologyURL.getURI(), nsTable);
                refOnto.getOntologyRank().setSwoogleOntoRank(owlMetaData.getOntoRank());
                swoogleWebServiceData.putRefOntology(ontologyURL.getURI(), refOnto);
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
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
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=search_swd_ontology&searchString=def:"
                    + inputWord + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search Ontology: " + inputWord);
            Model model = getSwoogleQueryResultModel(restQuery);
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

    private static void searchListPropertiesOfaRegionClass(String restQuery, String regionURI, Property regionType) {
        Resource region = ResourceFactory.createResource(regionURI);
        QueryExecution qexec = null;
        try {
            Model model = getSwoogleQueryResultModel(restQuery);
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
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=rel_swd_instance_domain_c2p&searchString="
                    + URLEncoder.encode(domainURI, "UTF-8") + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search List Properties Of a Domain Class: " + domainURI);
            searchListPropertiesOfaRegionClass(restQuery, domainURI, RDFS.domain);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * 入力したクラスを値域とするプロパティを獲得
     */
    public static void searchListPropertiesOfaRangeClass(String rangeURI) {
        try {
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=rel_swd_instance_range_c2p&searchString="
                    + URLEncoder.encode(rangeURI, "UTF-8") + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search List Properties Of a Range Class: " + rangeURI);
            searchListPropertiesOfaRegionClass(restQuery, rangeURI, RDFS.range);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    private static void searchListRegionClassOfaProperty(String restQuery, String propertyURI, Property regionType) {
        Resource property = ResourceFactory.createResource(propertyURI);
        QueryExecution qexec = null;
        try {
            Model model = getSwoogleQueryResultModel(restQuery);
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
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=rel_swd_instance_domain_p2c&searchString="
                    + URLEncoder.encode(propertyURI, "UTF-8") + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search List Domain Class Of a Property: " + propertyURI);
            searchListRegionClassOfaProperty(restQuery, propertyURI, RDFS.domain);
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * プロパティのURIを入力として，そのプロパティの値域を獲得
     */
    public static void searchListRangeClassOfaProperty(String propertyURI) {
        try {
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=rel_swd_instance_range_p2c&searchString="
                    + URLEncoder.encode(propertyURI, "UTF-8") + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search List Range Class Of a Property: " + propertyURI);
            searchListRegionClassOfaProperty(restQuery, propertyURI, RDFS.range);
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
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=rel_swt_swd&searchString="
                    + URLEncoder.encode(swtURI, "UTF-8") + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search List Documents Using Term: " + swtURI);
            Model model = getSwoogleQueryResultModel(restQuery);
            String sparqlQueryString = SPARQLQueryUtil.getQueryString(getListDocumentsUsingTermQuery());
            Query query = QueryFactory.create(sparqlQueryString);
            qexec = QueryExecutionFactory.create(query, model);
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource ontologyURL = (Resource) qs.get(ONTOLOGY_URL);
                SwoogleOWLMetaData owlMetaData = swoogleWebServiceData.getSwoogleOWLMetaData(ontologyURL.getURI());
                if (owlMetaData == null) {
                    searchDigestSemanticWebDocument(ontologyURL.getURI());
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
            String restQuery = "http://logos.cs.umbc.edu:8080/swoogle31/q?queryType=digest_swd&searchString="
                    + URLEncoder.encode(swdURI, "UTF-8") + "&key=demo";
            DODDLE.getLogger().log(Level.DEBUG, "Search Digest Semantic Web Document: " + swdURI);
            Model model = getSwoogleQueryResultModel(restQuery);
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
        int[] classCnt = new int[4];
        int[] propertyCnt = new int[4];
        
        for (String inputWord : inputWordSet) {
            searchOntology(inputWord);
        }
        setSWTSet(inputWordSet);
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

        // クラスについては，はじめに獲得したオントロジーのみを扱う
        // for (Resource classResource : swoogleWebServiceData.getClassSet()) {
        // searchListDocumentsUsingTerm(classResource.getURI());
        // }

        // 定義域と値域を満たすプロパティを定義しているオントロジーを獲得する
        for (Resource propertyResource : swoogleWebServiceData.getPropertySet()) {
            boolean isDefinedProperty = false;
            for (String uri: swoogleWebServiceData.getRefOntologyURISet()) {
                ReferenceOWLOntology refOnto = swoogleWebServiceData.getRefOntology(uri);
                if (refOnto.getConcept(propertyResource.getURI()) != null) {
                     DODDLE.getLogger().log(Level.DEBUG, "defined property: "+propertyResource);
                    isDefinedProperty = true;
                    break;
                }
            }
            if (!isDefinedProperty) {
                searchListDocumentsUsingTerm(propertyResource.getURI());
            }
        }
        classCnt[3] = swoogleWebServiceData.getClassSet().size();
        propertyCnt[3] = swoogleWebServiceData.getPropertySet().size();

        for (int i = 0; i < classCnt.length; i++) {
            DODDLE.getLogger().log(Level.DEBUG, "1: class size: "+classCnt[i]+" propertySize: "+propertyCnt[i]);    
        }
        swoogleWebServiceData.calcOntologyRank(inputWordSet);
    }

    public static void main(String[] args) {
        // SwoogleWebServiceWrapper.deleteOWLOntologies(); // 注意！！
        DODDLE.getLogger().setLevel(Level.DEBUG);
        SwoogleWebServiceWrapper.initSwoogleOWLMetaData();
        SwoogleWebServiceWrapper.setNameSpaceTable(new NameSpaceTable());
        Set<String> inputWordSet = new HashSet<String>();
        inputWordSet.add("business");
        inputWordSet.add("organization");
        // inputWordSet.add("counteroffer");// 定義が少なすぎる
        // inputWordSet.add("business");
        // inputWordSet.add("person");// 定義が多すぎる
        SwoogleWebServiceWrapper.acquireRelevantOWLOntologies(inputWordSet);

        // SwoogleWebServiceWrapper.searchOntology("business");
        // SwoogleWebServiceWrapper.setSWTSet(inputWordSet);
        // System.out.println(SwoogleWebServiceWrapper.getSwoogleWebServiceData().getClassSet());
        // System.out.println(SwoogleWebServiceWrapper.getSwoogleWebServiceData().getPropertySet());

        // System.out.println("domain");
        // SwoogleWebServiceWrapper
        // .searchListPropertiesOfaDomainClass("http://makna.ag-nbi.de/test-ontologies/imdb-ontology#Writer");
        // SwoogleWebServiceWrapper
        // .searchListDomainClassOfaProperty("http://makna.ag-nbi.de/test-ontologies/imdb-ontology#writes");
        // System.out.println("range");
        // SwoogleWebServiceWrapper
        // .searchListPropertiesOfaRangeClass("http://makna.ag-nbi.de/test-ontologies/imdb-ontology#Writer");
        // SwoogleWebServiceWrapper
        // .searchListRangeClassOfaProperty("http://makna.ag-nbi.de/test-ontologies/imdb-ontology#writes");
        // SwoogleWebServiceWrapper
        // .searchListDocumentsUsingTerm("http://makna.ag-nbi.de/test-ontologies/imdb-ontology#Movie");
    }
}
