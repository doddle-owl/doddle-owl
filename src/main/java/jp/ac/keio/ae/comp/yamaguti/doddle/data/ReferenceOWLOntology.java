/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved.
 *
 * This file is part of DODDLE-OWL.
 *
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.TableModel;

import jp.ac.keio.ae.comp.yamaguti.doddle.ui.GeneralOntologySelectionPanel;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.NameSpaceTable;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.ConceptTreeMaker;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.SPARQLQueryUtil;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.Translator;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.Utils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author takeshi morita
 */
public class ReferenceOWLOntology implements Comparable<ReferenceOWLOntology> {
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
		Object[] columnNames = new Object[] { "Property", "Value" };
		owlMetaDataTableModel = new OWLMetaDataTableModel(nsTable, columnNames, 0);

		setOWLMetaData(owlExtractionTemplate.getSearchOWLMetaDataTemplate());
		setClassSet(owlExtractionTemplate.getSearchClassSetTemplate());
		setPropertySet(owlExtractionTemplate.getSearchPropertySetTemplate());
		if (!uri.equals(GeneralOntologySelectionPanel.JWO_HOME)) {
			makeWordURIsMap();
		}
		setRegionSet();
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
		if (!uri.equals(GeneralOntologySelectionPanel.JWO_HOME)) {
			makeWordURIsMap();
		}
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
			Map<Resource, Set<RDFNode>> propertyRDFNodeSetMap = new HashMap<Resource, Set<RDFNode>>();
			while (results.hasNext()) {
				QuerySolution qs = results.nextSolution();
				Resource propertyRes = (Resource) qs.get(PROPERTY_QUERY_STRING);
				if (propertyRes.equals(RDF.type)) {
					continue;
				}
				nsTable.addNameSpaceTable(Utils.getNameSpace(propertyRes));
				RDFNode value = qs.get(VALUE_QUERY_STRING);
				if (propertyRDFNodeSetMap.get(propertyRes) != null) {
					Set<RDFNode> rdfNodeSet = propertyRDFNodeSetMap.get(propertyRes);
					rdfNodeSet.add(value);
				} else {
					Set<RDFNode> rdfNodeSet = new HashSet<RDFNode>();
					rdfNodeSet.add(value);
					propertyRDFNodeSetMap.put(propertyRes, rdfNodeSet);
				}
			}
			for (Resource propertyRes : propertyRDFNodeSetMap.keySet()) {
				for (RDFNode value : propertyRDFNodeSetMap.get(propertyRes)) {
					owlMetaDataTableModel.addRow(new Object[] { propertyRes, value });
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
			// 英単語はすべて小文字に変換してから登録する
			localName = localName.toLowerCase();
			if (wordURIsMap.get(localName) != null) {
				Set<String> uris = wordURIsMap.get(localName);
				uris.add(conceptResource.getURI());
			} else {
				Set<String> uris = new HashSet<String>();
				uris.add(conceptResource.getURI());
				wordURIsMap.put(localName, uris);
			}
			setWordURIsMap(conceptResource.getURI(),
					owlExtractionTemplate.getSearchConceptTemplate());
		}
	}

	private static final int LABEL_DESCRIPTION_LENGHT = 10; // 10文字以下の説明はラベルとしても利用する

	private void setWordURIsMap(String uri, File searchConceptTemplate) {
		String queryString = "";
		if (searchConceptTemplate.exists()) {
			try {
				queryString = SPARQLQueryUtil.getQueryString(new FileInputStream(
						searchConceptTemplate));
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
		} else {
			queryString = SPARQLQueryUtil.getQueryString(owlExtractionTemplate
					.getDefaultSearchConceptTemplate());
		}
		queryString = queryString.replaceAll("\\?concept", "<" + uri + ">"); // ?conceptを<概念URI>に置換
		QueryExecution qexec = null;
		try {
			Query query = QueryFactory.create(queryString);
			qexec = QueryExecutionFactory.create(query, ontModel);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution qs = results.nextSolution();
				if (qs.contains(LABEL_QUERY_STRING)) {
					Literal label = (Literal) qs.get(LABEL_QUERY_STRING);
					if (label != null) {
						setWordURIsMap(label.getString(), uri);
					}
				}
				if (qs.contains(DESCRIPTION_QUERY_STRING)) {
					Literal description = (Literal) qs.get(DESCRIPTION_QUERY_STRING);
					if (description != null
							&& description.getString().length() < LABEL_DESCRIPTION_LENGHT) {
						setWordURIsMap(description.getString(), uri);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("error onto: " + getURI());
			System.out.println("query error: " + queryString);
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}
	}

	private void setWordURIsMap(String labelStr, String uri) {
		// 英語はすべて小文字に変換してから登録する
		labelStr = labelStr.toLowerCase();
		if (wordURIsMap.get(labelStr) != null) {
			Set<String> uris = wordURIsMap.get(labelStr);
			uris.add(uri);
		} else {
			Set<String> uris = new HashSet<String>();
			uris.add(uri);
			wordURIsMap.put(labelStr, uris);
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
				RDFNode domainNode = qs.get(DOMAIN_QUERY_STRING);
				RDFNode rangeNode = qs.get(RANGE_QUERY_STRING);
				Resource domainRes = null;
				Resource rangeRes = null;
				if (domainNode instanceof Resource) {
					domainRes = (Resource) domainNode;
				}
				if (rangeNode instanceof Resource) {
					rangeRes = (Resource) rangeNode;
				}
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

	private void setRegionMap(Resource propertyRes, Resource regionRes,
			Map<String, Set<String>> regionMap) {
		if (regionMap.get(propertyRes.getURI()) != null) {
			Set<String> regionSet = regionMap.get(propertyRes.getURI());
			regionSet.add(regionRes.getURI());
		} else {
			Set<String> regionSet = new HashSet<String>();
			regionSet.add(regionRes.getURI());
			regionMap.put(propertyRes.getURI(), regionSet);
		}
	}

	private void setRegionSet() {
		if (domainMap.size() == 0 || rangeMap.size() == 0) {
			setRegionSet(owlExtractionTemplate.getSearchRegionSetTemplate());
		}
	}

	private Set<String> getRegionSet(String uri, Map<String, Set<String>> regionMap) {
		Set<String> regionSet = new HashSet<String>();
		// 上位概念で定義されている定義域，値域も獲得
		for (List<Concept> cList : getPathToRootSet(uri)) {
			for (Concept c : cList) {
				Set<String> rset = regionMap.get(c.getURI());
				if (rset != null) {
					regionSet.addAll(rset);
				}
			}
		}
		return regionSet;
	}

	public Set<String> getDomainSet(String uri) {
		return getRegionSet(uri, domainMap);
	}

	public Set<String> getRangeSet(String uri) {
		return getRegionSet(uri, rangeMap);
	}

	public Set<String> getURISet(String word) {
		if (wordURIsMap.get(word.toLowerCase()) == null
				&& uri.equals(GeneralOntologySelectionPanel.JWO_HOME)) {
			for (ResIterator i = ontModel.listSubjectsWithProperty(RDFS.label, word); i.hasNext();) {
				Resource res = i.nextResource();
				System.out.println(res.getURI() + ": " + word);
				if (wordURIsMap.get(word) != null) {
					Set<String> uris = wordURIsMap.get(word);
					uris.add(res.getURI());
				} else {
					Set<String> uris = new HashSet<String>();
					uris.add(res.getURI());
					wordURIsMap.put(word, uris);
				}
			}
		}
		return wordURIsMap.get(word.toLowerCase());
	}

	public Concept getConcept(String uri) {
		return getConcept(uri, owlExtractionTemplate.getSearchConceptTemplate());
	}

	private Concept getConcept(String uri, File searchConceptTemplate) {
		if (!(classSet.contains(uri) || propertySet.contains(uri))) {
			return null;
		}
		if (uriConceptMap.get(uri) != null) {
			return uriConceptMap.get(uri);
		}
		Concept concept = new Concept(uri, "");
		String queryString = "";
		if (searchConceptTemplate.exists()) {
			try {
				queryString = SPARQLQueryUtil.getQueryString(new FileInputStream(
						searchConceptTemplate));
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
		} else {
			queryString = SPARQLQueryUtil.getQueryString(owlExtractionTemplate
					.getDefaultSearchConceptTemplate());
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
					concept.addDescription(new DODDLELiteral(description.getLanguage(), description
							.getString()));
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
		Concept c = getConcept(uri);
		if (c != null) {
			pathToRoot.add(c);
		}
		Property subConceptOf = null;
		if (propertySet.contains(uri)) {
			subConceptOf = RDFS.subPropertyOf;
		} else {
			subConceptOf = RDFS.subClassOf;
		}
		int depth = 1;
		pathToRootSet.addAll(setPathToRoot(depth, ontModel.createResource(uri), pathToRoot,
				subConceptOf));
		return pathToRootSet;
	}

	public Set<List<String>> getURIPathToRootSet(String uri) {
		Set<List<String>> pathToRootSet = new HashSet<List<String>>();
		ArrayList<String> pathToRoot = new ArrayList<String>();
		Concept c = getConcept(uri);
		if (c != null) {
			pathToRoot.add(uri);
		}
		Property subConceptOf = null;
		if (propertySet.contains(uri)) {
			subConceptOf = RDFS.subPropertyOf;
		} else {
			subConceptOf = RDFS.subClassOf;
		}
		int depth = 1;
		pathToRootSet.addAll(setURIPathToRoot(depth, ontModel.createResource(uri), pathToRoot,
				subConceptOf));
		return pathToRootSet;
	}

	public Set<List<Concept>> setPathToRoot(int depth, Resource conceptRes,
			List<Concept> pathToRoot, Property subConceptOf) {
		Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
		if (!ontModel.listObjectsOfProperty(conceptRes, subConceptOf).hasNext()) {
			pathToRootSet.add(pathToRoot);
			return pathToRootSet;
		}
		if (30 < depth) { // 深さが30以上の場合は循環定義とみなす
			// System.out.println("return: 30 < depth");
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
					Concept c = getConcept(supConceptRes.getURI());
					if (c != null) {
						pathToRootClone.add(0, c);
					}
				}
				depth++;
				pathToRootSet.addAll(setPathToRoot(depth, supConceptRes, pathToRootClone,
						subConceptOf));
			}
		}
		return pathToRootSet;
	}

	public Set<List<String>> setURIPathToRoot(int depth, Resource conceptRes,
			List<String> pathToRoot, Property subConceptOf) {
		Set<List<String>> pathToRootSet = new HashSet<List<String>>();
		if (!ontModel.listObjectsOfProperty(conceptRes, subConceptOf).hasNext()) {
			pathToRootSet.add(pathToRoot);
			return pathToRootSet;
		}
		if (30 < depth) { // 深さが30以上の場合は循環定義とみなす
			// System.out.println("return: 30 < depth");
			pathToRootSet.add(pathToRoot);
			return pathToRootSet;
		}
		for (NodeIterator i = ontModel.listObjectsOfProperty(conceptRes, subConceptOf); i.hasNext();) {
			RDFNode node = i.nextNode();
			if (node instanceof Resource && !node.isAnon()) {
				List<String> pathToRootClone = new ArrayList<String>(pathToRoot);
				Resource supConceptRes = (Resource) node;
				if (!(ConceptTreeMaker.isDODDLEClassRootURI(supConceptRes.getURI()) || ConceptTreeMaker
						.isDODDLEPropertyRootURI(supConceptRes.getURI()))) {
					Concept c = getConcept(supConceptRes.getURI());
					if (c != null) {
						pathToRootClone.add(0, supConceptRes.getURI());
					}
				}
				depth++;
				pathToRootSet.addAll(setURIPathToRoot(depth, supConceptRes, pathToRootClone,
						subConceptOf));
			}
		}
		return pathToRootSet;
	}

	public Set<String> getSubURISet(String uri) {
		Set<String> subURISet = new HashSet<String>();
		String queryString = "";
		if (owlExtractionTemplate.getSearchSubConceptTemplate().exists()) {
			try {
				queryString = SPARQLQueryUtil.getQueryString(new FileInputStream(
						owlExtractionTemplate.getSearchSubConceptTemplate()));
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
		} else {
			queryString = SPARQLQueryUtil.getQueryString(owlExtractionTemplate
					.getDefaultSearchSubConceptTemplate());
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
		if (subURISet.size() == 0) {
			return;
		}
		for (String subURI : subURISet) {
			if (nounURISet.contains(subURI)) {
				refineSubURISet.add(subURI);
			}
		}
		if (0 < refineSubURISet.size()) {
			return;
		}
		for (String subURI : subURISet) {
			getSubURISet(subURI, nounURISet, refineSubURISet);
		}
	}

	public boolean equals(Object obj) {
		ReferenceOWLOntology refOnto = (ReferenceOWLOntology) obj;
		return uri.equals(refOnto.getURI());
	}

	@Override
	public int compareTo(ReferenceOWLOntology refOnt) {
		return refOnt.getOntologyRank().compareTo(getOntologyRank());
	}

	public String toString() {
		return getOntologyRank().toString() + " " + uri;
	}

	public static void main(String[] args) {
		try {
			Translator.loadDODDLEComponentOntology(DODDLEConstants.LANG);
			NameSpaceTable nsTable = new NameSpaceTable();
			Model ontModel = ModelFactory.createDefaultModel();
			String fileName = DODDLEConstants.PROJECT_HOME + "test.owl";
			ontModel.read(new FileInputStream(fileName), DODDLEConstants.BASE_URI, "RDF/XML");
			ReferenceOWLOntology info = new ReferenceOWLOntology(ontModel, fileName, nsTable);
			System.out.println("res: " + info.getURISet("Resource")); // すべて小文字に変換しているため
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
			System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#cate"));
			System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#cat").getWord());
			System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#testdog").getWord());
			System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#animal").getWord());
			System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#bow").getWord());
			System.out.println(info.getConcept("http://mmm.semanticweb.org/mr3#hikkaku").getWord());
			System.out.println(info.getDomainSet("http://mmm.semanticweb.org/mr3#bow"));
			System.out.println(info.getRangeSet("http://mmm.semanticweb.org/mr3#bow"));
			System.out.println(info.getDomainSet("http://mmm.semanticweb.org/mr3#hikkaku"));
			System.out.println(info.getRangeSet("http://mmm.semanticweb.org/mr3#hikkaku"));
			Set<List<Concept>> pathToRootSet = info
					.getPathToRootSet("http://mmm.semanticweb.org/mr3#specialSiamese");
			System.out.println("path to root: " + pathToRootSet);
			System.out.println(pathToRootSet.size());
		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
		}
	}
}
