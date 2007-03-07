/*
 * @(#)  2006/12/16
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class OWLOntologyManager {

    private static Map<String, ReferenceOWLOntology> refOntMap = new HashMap<String, ReferenceOWLOntology>();

    public static void addRefOntology(String uri, ReferenceOWLOntology ontInfo) {
        refOntMap.put(uri, ontInfo);
    }

    public static ReferenceOWLOntology getRefOntology(String uri) {
        return refOntMap.get(uri);
    }

    public static void removeRefOntology(String uri) {
        refOntMap.remove(uri);
    }

    public static Collection<ReferenceOWLOntology> getRefOntologySet() {
        return refOntMap.values();
    }

    public static Set<List<Concept>> getPathToRootSet(String uri) {
        Set<List<Concept>> pathToRootSet = new HashSet<List<Concept>>();
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                Set<List<Concept>> set = refOnt.getPathToRootSet(uri);
                if (set != null) {
                    pathToRootSet.addAll(set);
                }
            }
        }
        return pathToRootSet;
    }

    public static Set<Concept> getVerbConceptSet(Set<Concept> inputConceptSet) {
        Set<Concept> verbConceptSet = new HashSet<Concept>();
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                for (String uri : refOnt.getPropertySet()) {
                    Concept c = refOnt.getConcept(uri);
                    if (inputConceptSet.contains(c)) {
                        verbConceptSet.add(c);
                    }
                }
            }
        }
        return verbConceptSet;
    }

    public static Concept getConcept(String uri) {
        Concept concept = null;
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                concept = refOnt.getConcept(uri);
            }
        }
        return concept;
    }

    
    public static void setRegionSet() {
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                refOnt.setRegionSet();
            }
        }
    }
    
    public static Set<String> getDomainSet(Concept c, List<List<Concept>> trimmedConceptList) {
        Set<String> domainSet = new HashSet<String>();
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                domainSet.addAll(refOnt.getDomainSet(c.getURI()));
                for (List<Concept> list : trimmedConceptList) {
                    for (Concept trimmedConcept : list) {
                        domainSet.addAll(refOnt.getDomainSet(trimmedConcept.getURI()));
                    }
                }
            }
        }
        return domainSet;
    }

    public static Set<String> getRangeSet(Concept c, List<List<Concept>> trimmedConceptList) {
        Set<String> rangeSet = new HashSet<String>();
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                rangeSet.addAll(refOnt.getRangeSet(c.getURI()));
                for (List<Concept> list : trimmedConceptList) {
                    for (Concept trimmedConcept : list) {
                        rangeSet.addAll(refOnt.getRangeSet(trimmedConcept.getURI()));
                    }
                }
            }
        }
        return rangeSet;
    }

    public static Set<String> getSubURISet(String uri, Set<String> nounURISet) {
        Set<String> subURISet = new HashSet<String>();
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                Set<String> uriSet = new HashSet<String>();
                refOnt.getSubURISet(uri, nounURISet, uriSet);
                subURISet.addAll(uriSet);
            }
        }
        return subURISet;
    }

    public static void setOWLConceptSet(String word, Set<Concept> conceptSet) {
        for (ReferenceOWLOntology refOnt : getRefOntologySet()) {
            if (refOnt.isAvailable()) {
                Set<String> uriSet = refOnt.getURISet(word);
                if (uriSet == null) {
                    continue;
                }
                for (String uri : uriSet) {
                    conceptSet.add(refOnt.getConcept(uri));
                }
            }
        }
    }
}
