package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

/*
 * Created on 2004/02/06
 *  
 */

import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * @author takeshi morita
 * 
 */
public class JenaModelMaker {

    private static void addDefaultConceptInfo(Model ontology, Resource child, ConceptTreeNode node) {
        String[] enWords = node.getEnWords();
        for (int i = 0; i < enWords.length; i++) {
            if (!enWords[i].equals("")) {
                ontology.add(child, RDFS.label, ontology.createLiteral(enWords[i], "en"));
            }
        }
        String[] jpWords = node.getJpWords();
        for (int i = 0; i < jpWords.length; i++) {
            if (!jpWords[i].equals("")) {
                ontology.add(child, RDFS.label, ontology.createLiteral(jpWords[i], "ja"));
            }
        }
        if (!node.getEnExplanation().equals("")) {
            ontology.add(child, RDFS.comment, ontology.createLiteral(node.getEnExplanation(), "en"));
        }
        if (!node.getJpExplanation().equals("")) {
            ontology.add(child, RDFS.comment, ontology.createLiteral(node.getJpExplanation(), "ja"));
        }
    }

    private static Resource createResource(ConceptTreeNode node) {
        if (node.isUserConcept()) { return ResourceFactory.createResource(DODDLE.BASE_URI + node.getIdStr()); }
        if (node.getPrefix().equals("edr")) {
            return ResourceFactory.createResource(DODDLE.EDR_URI + node.getIdStr());
        } else if (node.getPrefix().equals("edrt")) {
            return ResourceFactory.createResource(DODDLE.EDRT_URI + node.getIdStr());
        } else if (node.getPrefix().equals("wn")) {
            return ResourceFactory.createResource(DODDLE.WN_URI + node.getIdStr());
        } else {
            return ResourceFactory.createResource(DODDLE.EDR_URI + node.getIdStr());
        }
    }

    private static Resource getResource(ConceptTreeNode node, Model ontology) {
        ConceptTreeNode parentNode = (ConceptTreeNode) node.getParent();
        if (parentNode.isUserConcept()) { return ontology.getResource(DODDLE.BASE_URI + parentNode.getIdStr()); }
        if (parentNode.getPrefix().equals("edr")) {
            return ontology.getResource(DODDLE.EDR_URI + parentNode.getIdStr());
        } else if (parentNode.getPrefix().equals("edrt")) {
            return ontology.getResource(DODDLE.EDRT_URI + parentNode.getIdStr());
        } else if (parentNode.getPrefix().equals("wn")) {
            return ontology.getResource(DODDLE.WN_URI + parentNode.getIdStr());
        } else {
            return ontology.getResource(DODDLE.EDR_URI + parentNode.getIdStr());
        }
    }

    public static Model makeClassModel(ConceptTreeNode node, Model ontology) {
        if (node == null) { return ontology; }
        if (node.isLeaf()) {
            Resource child = createResource(node);
            ontology.add(child, RDF.type, OWL.Class);
            addDefaultConceptInfo(ontology, child, node);
            if (node.getParent() != null) {
                Resource parent = getResource(node, ontology);
                ontology.add(child, RDFS.subClassOf, parent);
            }
        } else {
            if (node.isRoot()) {
                ontology.add(createResource(node), RDF.type, OWL.Class);
            } else {
                Resource child = createResource(node);
                ontology.add(child, RDF.type, OWL.Class);
                addDefaultConceptInfo(ontology, child, node);
                Resource parent = getResource(node, ontology);
                ontology.add(child, RDFS.subClassOf, parent);
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            ontology = makeClassModel((ConceptTreeNode) node.getChildAt(i), ontology);
        }
        return ontology;
    }

    private static void addRegion(Resource resource, Property region, Model ontology, Set<String> regionSet,
            VerbConcept vc) {
        for (String id : regionSet) {
            Resource regionValue = null;
            if (id.indexOf("UID") != -1) {
                regionValue = ontology.getResource(DODDLE.BASE_URI + id);
            } else {
                if (vc.getPrefix().equals("edr")) {
                    regionValue = ontology.getResource(DODDLE.EDR_URI + "ID" + id);
                } else if (vc.getPrefix().equals("edrt")) {
                    regionValue = ontology.getResource(DODDLE.EDR_URI + "ID" + id);
                } else if (vc.getPrefix().equals("wn")) {
                    regionValue = ontology.getResource(DODDLE.WN_URI + "ID" + id);
                } else {
                    regionValue = ontology.getResource(DODDLE.EDR_URI + "ID" + id);
                }
            }
            ontology.add(resource, region, regionValue);
        }
    }

    public static Model makePropertyModel(ConceptTreeNode node, Model ontology) {
        if (node == null) { return ontology; }
        if (node.isLeaf() && !node.isRoot()) {
            Resource child = createResource(node);
            ontology.add(child, RDF.type, OWL.ObjectProperty);
            addDefaultConceptInfo(ontology, child, node);
            VerbConcept vc = (VerbConcept) node.getConcept();
            addRegion(child, RDFS.domain, ontology, vc.getDomainSet(), vc);
            addRegion(child, RDFS.range, ontology, vc.getRangeSet(), vc);
            Resource parent = getResource(node, ontology);
            ontology.add(child, RDFS.subPropertyOf, parent);
        } else {
            if (node.isRoot()) {
                ontology.add(createResource(node), RDF.type, OWL.ObjectProperty);
            } else {
                Resource child = createResource(node);
                ontology.add(child, RDF.type, OWL.ObjectProperty);
                addDefaultConceptInfo(ontology, child, node);
                VerbConcept vc = (VerbConcept) node.getConcept();
                addRegion(child, RDFS.domain, ontology, vc.getDomainSet(), vc);
                addRegion(child, RDFS.range, ontology, vc.getRangeSet(), vc);
                Resource parent = getResource(node, ontology);
                ontology.add(child, RDFS.subPropertyOf, parent);
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            ontology = makePropertyModel((ConceptTreeNode) node.getChildAt(i), ontology);
        }
        return ontology;
    }
}