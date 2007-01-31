/*
 * @(#)  2006/12/14
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

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
public class ReferenceOWLOntology {
    private Model ontModel;
    private Map<String, Set<String>> wordURIsMap;
    private Map<String, Concept> uriConceptMap;
    private Set<String> propertySet;
    private Map<String, Set<String>> domainMap;
    private Map<String, Set<String>> rangeMap;

    private NameSpaceTable nsTable;

    public ReferenceOWLOntology(InputStream is, String type, NameSpaceTable nst) {
        nsTable = nst;
        ontModel = ModelFactory.createDefaultModel();
        ontModel.read(is, DODDLE.BASE_URI, type);
        wordURIsMap = new HashMap<String, Set<String>>();
        uriConceptMap = new HashMap<String, Concept>();
        propertySet = new HashSet<String>();
        domainMap = new HashMap<String, Set<String>>();
        rangeMap = new HashMap<String, Set<String>>();
        makeWordURIsMap();
    }

    private static Resource[] conceptTypeList = { OWL.Class, RDFS.Class, OWL.ObjectProperty, OWL.DatatypeProperty,
            RDF.Property};

    private Set<Resource> getConceptResourceSet() {
        Set<Resource> conceptResourceSet = new HashSet<Resource>();
        for (int i = 0; i < conceptTypeList.length; i++) {
            for (ResIterator j = ontModel.listSubjectsWithProperty(RDF.type, conceptTypeList[i]); j.hasNext();) {
                Resource resource = j.nextResource();
                if (!resource.isAnon()) {
                    conceptResourceSet.add(resource);
                    nsTable.addNameSpaceTable(Utils.getNameSpace(resource));
                    if (conceptTypeList[i] == RDF.Property || conceptTypeList[i] == OWL.ObjectProperty
                            || conceptTypeList[i] == OWL.DatatypeProperty) {
                        propertySet.add(resource.getURI());
                    }
                }
            }
        }
        return conceptResourceSet;
    }

    public void makeWordURIsMap() {
        Set<Resource> conceptResourceSet = getConceptResourceSet();
        for (Resource conceptResource : conceptResourceSet) {
            String localName = conceptResource.getLocalName();
            if (wordURIsMap.get(localName) != null) {
                Set<String> uris = wordURIsMap.get(localName);
                uris.add(conceptResource.getURI());
            } else {
                Set<String> uris = new HashSet<String>();
                uris.add(conceptResource.getURI());
                wordURIsMap.put(localName, uris);
            }
            for (NodeIterator i = ontModel.listObjectsOfProperty(conceptResource, RDFS.label); i.hasNext();) {
                RDFNode node = i.nextNode();
                if (node instanceof Literal) {
                    Literal lit = (Literal) node;
                    if (wordURIsMap.get(lit.getString()) != null) {
                        Set<String> uris = wordURIsMap.get(lit.getString());
                        uris.add(conceptResource.getURI());
                    } else {
                        Set<String> uris = new HashSet<String>();
                        uris.add(conceptResource.getURI());
                        wordURIsMap.put(lit.getString(), uris);
                    }
                }
            }
        }
    }

    public Set<String> getPropertySet() {
        return propertySet;
    }

    public Set<String> getDomainSet(String uri) {
        if (domainMap.get(uri) != null) { return domainMap.get(uri); }
        Set<String> domainSet = new HashSet<String>();
        for (NodeIterator i = ontModel.listObjectsOfProperty(ontModel.createResource(uri), RDFS.domain); i.hasNext();) {
            RDFNode node = i.nextNode();
            if (node instanceof Resource) {
                Resource res = (Resource) node;
                if (!res.isAnon()) {
                    domainSet.add(res.getURI());
                }
            }
        }
        domainMap.put(uri, domainSet);
        return domainSet;
    }

    public Set<String> getRangeSet(String uri) {
        if (rangeMap.get(uri) != null) { return rangeMap.get(uri); }
        Set<String> rangeSet = new HashSet<String>();
        for (NodeIterator i = ontModel.listObjectsOfProperty(ontModel.createResource(uri), RDFS.range); i.hasNext();) {
            RDFNode node = i.nextNode();
            if (node instanceof Resource) {
                Resource res = (Resource) node;
                if (!res.isAnon()) {
                    rangeSet.add(res.getURI());
                }
            }
        }
        rangeMap.put(uri, rangeSet);
        return rangeSet;
    }

    public Set<String> getURISet(String word) {
        return wordURIsMap.get(word);
    }

    public Concept getConcept(String uri) {
        if (uriConceptMap.get(uri) != null) { return uriConceptMap.get(uri); }
        Concept concept = new Concept(uri, "");
        for (NodeIterator i = ontModel.listObjectsOfProperty(ontModel.createResource(uri), RDFS.label); i.hasNext();) {
            RDFNode node = i.nextNode();
            if (node instanceof Literal) {
                Literal lit = (Literal) node;
                if (lit.getLanguage().equals("ja")) {
                    concept.addJaWord(lit.getString());
                } else {
                    concept.addEnWord(lit.getString());
                }
            }
        }
        for (NodeIterator i = ontModel.listObjectsOfProperty(ontModel.createResource(uri), RDFS.comment); i.hasNext();) {
            RDFNode node = i.nextNode();
            if (node instanceof Literal) {
                Literal lit = (Literal) node;
                if (lit.getLanguage().equals("ja")) {
                    concept.setJaExplanation(lit.getString());
                } else {
                    concept.setEnExplanation(lit.getString());
                }
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
                pathToRootClone.add(0, getConcept(supConceptRes.getURI()));
                pathToRootSet.addAll(setPathToRoot(supConceptRes, pathToRootClone, subConceptOf));
            }
        }
        return pathToRootSet;
    }

    public Set<String> getSubURISet(String uri) {
        Set<String> subURISet = new HashSet<String>();
        String queryString = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?subConcept WHERE { ?subConcept rdfs:subClassOf <" + uri + ">}";
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ontModel);
        ResultSet results = qexec.execSelect();
        try {
            while (results.hasNext()) {
                QuerySolution qs = results.nextSolution();
                Resource subConcept = (Resource) qs.get("subConcept");
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

    public static void main(String[] args) {
        try {
            NameSpaceTable nsTable = new NameSpaceTable();
            ReferenceOWLOntology info = new ReferenceOWLOntology(new FileInputStream("test.owl"), "RDF/XML", nsTable);
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
            System.out.println("en word: " + c.getEnWord());
            System.out.println("ja word: " + c.getJaWord());
            System.out.println("ja exp: " + c.getJaExplanation());
            System.out.println("en exp: " + c.getEnExplanation());
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
