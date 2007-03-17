/*
 * @(#)  2006/12/14
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import javax.swing.table.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author takeshi morita
 */
public class ReferenceOWLOntology  implements Comparable{
    private boolean isAvailable;
    private String uri;
    private Model ontModel;
    private Map<String, Set<String>> wordURIsMap;
    private Map<String, Concept> uriConceptMap;
    private Set<String> classSet;
    private Set<String> propertySet;
    private Set<Resource> conceptResourceSet;
    private Map<String, Set<String>> domainMap;
    private Map<String, Set<String>> rangeMap;

    private OWLMetaDataTableModel owlMetaDataTableModel;

    private OntologyRank ontoRank;

    private NameSpaceTable nsTable;
    private OWLOntologyExtractionTemplate owlExtractionTemplate;

    public ReferenceOWLOntology(Model model, String uri, NameSpaceTable nst) {
        isAvailable = true;
        this.uri = uri;
        ontoRank = new OntologyRank();
        owlExtractionTemplate = new OWLOntologyExtractionTemplate();
        nsTable = nst;
        ontModel = model;
        wordURIsMap = new HashMap<String, Set<String>>();
        uriConceptMap = new HashMap<String, Concept>();
        classSet = new HashSet<String>();
        propertySet = new HashSet<String>();
        conceptResourceSet = new HashSet<Resource>();
        domainMap = new HashMap<String, Set<String>>();
        rangeMap = new HashMap<String, Set<String>>();
        Object[] columnNames = new Object[] { "Property", "Value"};
        owlMetaDataTableModel = new OWLMetaDataTableModel(nsTable, columnNames, 0);

        setOWLMetaData(owlExtractionTemplate.getSearchOWLMetaDataTemplate());
        setClassSet(owlExtractionTemplate.getSearchClassSetTemplate());
        setPropertySet(owlExtractionTemplate.getSearchPropertySetTemplate());
        makeWordURIsMap();
    }

    public OntologyRank getOntologyRank() {
        return ontoRank;
    }

    public void reload() {
        wordURIsMap.clear();
        uriConceptMap.clear();
        classSet.clear();
        propertySet.clear();
        conceptResourceSet.clear();
        domainMap.clear();
        rangeMap.clear();
        setOWLMetaData(owlExtractionTemplate.getSearchOWLMetaDataTemplate());
        setClassSet(owlExtractionTemplate.getSearchClassSetTemplate());
        setPropertySet(owlExtractionTemplate.getSearchPropertySetTemplate());
        makeWordURIsMap();
    }

    public TableModel getOWLMetaDataTableModel() {
        return owlMetaDataTableModel;
    }

    public void setAvailable(boolean t) {
        isAvailable = t;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    private static final String SUB_CONCEPT_QUERY_STRING = "subConcept";
    private static final String CLASS_QUERY_STRING = "class";
    private static final String PROPERTY_QUERY_STRING = "property";
    private static final String VALUE_QUERY_STRING = "value";
    private static final String LABEL_QUERY_STRING = "label";
    private static final String DESCRIPTION_QUERY_STRING = "description";
    private static final String DOMAIN_QUERY_STRING = "domain";
    private static final String RANGE_QUERY_STRING = "range";

    public String getURI() {
        return uri;
    }

    public OWLOntologyExtractionTemplate getOWLOntologyExtractionTemplate() {
        return owlExtractionTemplate;
    }

    private QueryExecution getQueryExcecution(InputStream inputStream) {
        String queryString = SPARQLQueryUtil.getQueryString(inputStream);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ontModel);
        return qexec;
    }

    private void setOWLMetaData(File searchOWLMetaDataTemplate) {
        QueryExecution qexec = null;
        if (searchOWLMetaDataTemplate.exists()) {
            try {
                qexec = getQueryExcecution(new FileInputStream(searchOWLMetaDataTemplate));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            qexec = getQueryExcecution(owlExtractionTemplate.getDefaultSearchOWLMetaDataTemplate());
        }
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource propertyRes = (Resource) qs.get(PROPERTY_QUERY_STRING);
                nsTable.addNameSpaceTable(Utils.getNameSpace(propertyRes));
                RDFNode value = qs.get(VALUE_QUERY_STRING);
                if (value instanceof Resource) {
                    Resource resourceValue = (Resource) value;
                    owlMetaDataTableModel.addRow(new Object[] { propertyRes, resourceValue});
                } else if (value instanceof Literal) {
                    Literal literalValue = (Literal) value;
                    owlMetaDataTableModel.addRow(new Object[] { propertyRes, literalValue});
                }
            }
            owlMetaDataTableModel.refreshTableModel();
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    private void setPropertySet(File searchPropertiesTemplate) {
        QueryExecution qexec = null;
        if (searchPropertiesTemplate.exists()) {
            try {
                qexec = getQueryExcecution(new FileInputStream(searchPropertiesTemplate));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            qexec = getQueryExcecution(owlExtractionTemplate.getDefaultSearchPropertySetTemplate());
        }
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource propertyRes = (Resource) qs.get(PROPERTY_QUERY_STRING);
                propertySet.add(propertyRes.getURI());
                conceptResourceSet.add(propertyRes);
                nsTable.addNameSpaceTable(Utils.getNameSpace(propertyRes));
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    private void setClassSet(File searchClassesTemplate) {
        QueryExecution qexec = null;
        if (searchClassesTemplate.exists()) {
            try {
                qexec = getQueryExcecution(new FileInputStream(searchClassesTemplate));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            qexec = getQueryExcecution(owlExtractionTemplate.getDefaultSearchClassSetTemplate());
        }
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource classRes = (Resource) qs.get(CLASS_QUERY_STRING);
                classSet.add(classRes.getURI());
                conceptResourceSet.add(classRes);
                nsTable.addNameSpaceTable(Utils.getNameSpace(classRes));
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    public void makeWordURIsMap() {
        for (Resource conceptResource : conceptResourceSet) {
            if (conceptResource.isAnon()) {
                continue;
            }
            // ローカル名をラベルとして扱う
            String localName = Utils.getLocalName(conceptResource);
            if (wordURIsMap.get(localName) != null) {
                Set<String> uris = wordURIsMap.get(localName);
                uris.add(conceptResource.getURI());
            } else {
                Set<String> uris = new HashSet<String>();
                uris.add(conceptResource.getURI());
                // 英単語はすべて小文字に変換してから登録する
                wordURIsMap.put(localName.toLowerCase(), uris);
            }
            setWordURIsMap(conceptResource.getURI(), owlExtractionTemplate.getSearchConceptTemplate());
        }
    }

    private static final int LABEL_DESCRIPTION_LENGHT = 10; // 10文字以下の説明はラベルとしても利用する

    private void setWordURIsMap(String uri, File searchConceptTemplate) {
        String queryString = "";
        if (searchConceptTemplate.exists()) {
            try {
                queryString = SPARQLQueryUtil.getQueryString(new FileInputStream(searchConceptTemplate));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            queryString = SPARQLQueryUtil.getQueryString(owlExtractionTemplate.getDefaultSearchConceptTemplate());
        }
        queryString = queryString.replaceAll("\\?concept", "<" + uri + ">"); // ?conceptを<概念URI>に置換
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ontModel);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Literal label = (Literal) qs.get(LABEL_QUERY_STRING);
                if (label != null) {
                    setWordURIsMap(uri, label);
                }
                Literal description = (Literal) qs.get(DESCRIPTION_QUERY_STRING);
                if (description != null && description.getString().length() < LABEL_DESCRIPTION_LENGHT) {
                    setWordURIsMap(uri, description);
                }
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    private void setWordURIsMap(String uri, Literal label) {
        if (wordURIsMap.get(label.getString()) != null) {
            Set<String> uris = wordURIsMap.get(label.getString());
            uris.add(uri);
        } else {
            Set<String> uris = new HashSet<String>();
            uris.add(uri);
            // 英語はすべて小文字に変換してから登録する
            wordURIsMap.put(label.getString().toLowerCase(), uris);
        }
    }

    public Set<String> getClassSet() {
        return classSet;
    }
    
    public Set<String> getPropertySet() {
        return propertySet;
    }

    private void setRegionSet(File searchRegionSetTemplate) {
        QueryExecution qexec = null;
        if (searchRegionSetTemplate.exists()) {
            try {
                qexec = getQueryExcecution(new FileInputStream(searchRegionSetTemplate));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            qexec = getQueryExcecution(owlExtractionTemplate.getDefaultSearchRegionSetTemplate());
        }
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource propertyRes = (Resource) qs.get(PROPERTY_QUERY_STRING);
                Resource domainRes = (Resource) qs.get(DOMAIN_QUERY_STRING);
                Resource rangeRes = (Resource) qs.get(RANGE_QUERY_STRING);

                if (propertyRes != null && domainRes != null) {
                    setRegionMap(propertyRes, domainRes, domainMap);
                }
                if (propertyRes != null && rangeRes != null) {
                    setRegionMap(propertyRes, rangeRes, rangeMap);
                }
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
    }

    private void setRegionMap(Resource propertyRes, Resource regionRes, Map<String, Set<String>> regionMap) {
        if (regionMap.get(propertyRes.getURI()) != null) {
            Set<String> regionSet = regionMap.get(propertyRes.getURI());
            regionSet.add(regionRes.getURI());
        } else {
            Set<String> regionSet = new HashSet<String>();
            regionSet.add(regionRes.getURI());
            regionMap.put(propertyRes.getURI(), regionSet);
        }
    }

    public void setRegionSet() {
        if (domainMap.size() == 0 || rangeMap.size() == 0) {
            setRegionSet(owlExtractionTemplate.getSearchRegionSetTemplate());
        }
    }

    public Set<String> getDomainSet(String uri) {
        if (domainMap.get(uri) != null) { return domainMap.get(uri); }
        return new HashSet<String>();
    }

    public Set<String> getRangeSet(String uri) {
        if (rangeMap.get(uri) != null) { return rangeMap.get(uri); }
        return new HashSet<String>();
    }

    public Set<String> getURISet(String word) {
        return wordURIsMap.get(word);
    }

    public Concept getConcept(String uri) {
        return getConcept(uri, owlExtractionTemplate.getSearchConceptTemplate());
    }

    private Concept getConcept(String uri, File searchConceptTemplate) {
        if (uriConceptMap.get(uri) != null) { return uriConceptMap.get(uri); }
        Concept concept = new Concept(uri, "");

        String queryString = "";
        if (searchConceptTemplate.exists()) {
            try {
                queryString = SPARQLQueryUtil.getQueryString(new FileInputStream(searchConceptTemplate));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            queryString = SPARQLQueryUtil.getQueryString(owlExtractionTemplate.getDefaultSearchConceptTemplate());
        }
        queryString = queryString.replaceAll("\\?concept", "<" + uri + ">"); // ?conceptを<概念URI>に置換
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ontModel);

        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Literal label = (Literal) qs.get(LABEL_QUERY_STRING);
                Literal description = (Literal) qs.get(DESCRIPTION_QUERY_STRING);

                if (label != null && 0 < label.getString().length()) {
                    concept.addLabel(new DODDLELiteral(label.getLanguage(), label.getString()));
                }
                if (description != null && 0 < description.getString().length()) {
                    concept.addDescription(new DODDLELiteral(description.getLanguage(), description.getString()));
                }
            }
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
        uriConceptMap.put(uri, concept);
        return concept;
    }

    public Set<List<Concept>> getPathToRootSet(String uri) {
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        ArrayList<Concept> pathToRoot = new ArrayList<Concept>();
        pathToRoot.add(getConcept(uri));
        Property subConceptOf = null;
        if (propertySet.contains(uri)) {
            subConceptOf = RDFS.subPropertyOf;
        } else {
            subConceptOf = RDFS.subClassOf;
        }
        pathToRootSet.addAll(setPathToRoot(ontModel.createResource(uri), pathToRoot, subConceptOf));
        return pathToRootSet;
    }

    public Set<List<Concept>> setPathToRoot(Resource conceptRes, List<Concept> pathToRoot, Property subConceptOf) {
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        if (!ontModel.listObjectsOfProperty(conceptRes, subConceptOf).hasNext()) {
            pathToRootSet.add(pathToRoot);
            return pathToRootSet;
        }
        for (NodeIterator i = ontModel.listObjectsOfProperty(conceptRes, subConceptOf); i.hasNext();) {
            RDFNode node = i.nextNode();
            if (node instanceof Resource && !node.isAnon()) {
                List<Concept> pathToRootClone = new ArrayList<Concept>(pathToRoot);
                Resource supConceptRes = (Resource) node;
                if (!(ConceptTreeMaker.isDODDLEClassRootURI(supConceptRes.getURI()) || ConceptTreeMaker
                        .isDODDLEPropertyRootURI(supConceptRes.getURI()))) {
                    pathToRootClone.add(0, getConcept(supConceptRes.getURI()));
                }
                pathToRootSet.addAll(setPathToRoot(supConceptRes, pathToRootClone, subConceptOf));
            }
        }
        return pathToRootSet;
    }

    public Set<String> getSubURISet(String uri) {
        Set<String> subURISet = new HashSet<String>();
        String queryString = "";
        if (owlExtractionTemplate.getSearchSubConceptTemplate().exists()) {
            try {
                queryString = SPARQLQueryUtil.getQueryString(new FileInputStream(owlExtractionTemplate
                        .getSearchSubConceptTemplate()));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        } else {
            queryString = SPARQLQueryUtil.getQueryString(owlExtractionTemplate.getDefaultSearchSubConceptTemplate());
        }
        queryString = queryString.replaceAll("\\?concept", "<" + uri + ">"); // ?conceptを<概念URI>に置換
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ontModel);
        try {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource subConcept = (Resource) qs.get(SUB_CONCEPT_QUERY_STRING);
                subURISet.add(subConcept.getURI());
            }
        } finally {
            qexec.close();
        }
        return subURISet;
    }

    public void getSubURISet(String uri, Set<String> nounURISet, Set<String> refineSubURISet) {
        Set<String> subURISet = getSubURISet(uri);
        if (subURISet.size() == 0) { return; }
        for (String subURI : subURISet) {
            if (nounURISet.contains(subURI)) {
                refineSubURISet.add(subURI);
            }
        }
        if (0 < refineSubURISet.size()) { return; }
        for (String subURI : subURISet) {
            getSubURISet(subURI, nounURISet, refineSubURISet);
        }
    }

    public boolean equals(Object obj) {
        ReferenceOWLOntology refOnto = (ReferenceOWLOntology) obj;
        return uri.equals(refOnto.getURI());
    }

    public int compareTo(Object obj) {
        ReferenceOWLOntology refOnto = (ReferenceOWLOntology) obj;
        return refOnto.getOntologyRank().compareTo(getOntologyRank());
    }

    public String toString() {
        return getOntologyRank().toString() + " " + uri;
    }

    public static void main(String[] args) {
        try {
            NameSpaceTable nsTable = new NameSpaceTable();
            Model ontModel = ModelFactory.createDefaultModel();
            String fileName = "test.owl";
            ontModel.read(new FileInputStream(fileName), DODDLE.BASE_URI, "RDF/XML");
            ReferenceOWLOntology info = new ReferenceOWLOntology(ontModel, fileName, nsTable);
            System.out.println(info.getURISet("Resource"));
            System.out.println(info.getURISet("animal"));
            System.out.println(info.getURISet("dog"));
            System.out.println(info.getURISet("cat"));
            System.out.println(info.getURISet("動物"));
            System.out.println(info.getURISet("犬"));
            System.out.println(info.getURISet("猫"));
            System.out.println(info.getURISet("ひっかく"));
            System.out.println(info.getURISet("bow"));
            Concept c = info.getConcept("http://mmm.semanticweb.org/mr3#animal");
            System.out.println("word: " + c.getWord());
            System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#cat"));
            System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#testdog"));
            System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#animal"));
            System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#bow"));
            System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#hikkaku"));
            System.out.println(info.getDomainSet("http://mmm.semanticweb.org/mr3#bow"));
            System.out.println(info.getRangeSet("http://mmm.semanticweb.org/mr3#bow"));
            System.out.println(info.getDomainSet("http://mmm.semanticweb.org/mr3#hikkaku"));
            System.out.println(info.getRangeSet("http://mmm.semanticweb.org/mr3#hikkaku"));
            Set<List<Concept>> pathToRootSet = info.getPathToRootSet("http://mmm.semanticweb.org/mr3#Siamese");
            System.out.println("path to root: " + pathToRootSet);
            System.out.println(pathToRootSet.size());
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        }
    }
}
