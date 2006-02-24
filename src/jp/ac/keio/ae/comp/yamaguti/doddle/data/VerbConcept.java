package jp.ac.keio.ae.comp.yamaguti.doddle.data;
import java.util.*;

/*
 * @(#)  2005/07/18
 *
 */

/**
 * @author takeshi morita
 */
public class VerbConcept extends Concept {

    private Set domainSet;
    private Set rangeSet;
    private Set trimmedConceptSet;

    public VerbConcept(Concept c) {
        super(c);
        domainSet = new TreeSet();
        rangeSet = new TreeSet();
        trimmedConceptSet = new HashSet();
    }

    public VerbConcept(String id, String concept) {
        super(id, concept);
        domainSet = new TreeSet();
        rangeSet = new TreeSet();
        trimmedConceptSet = new HashSet();
    }

    public void addTrimmedConcept(Concept c) {
        trimmedConceptSet.add(c);
    }

    public void addAllTrimmedConcept(Set set) {
        trimmedConceptSet.addAll(set);
    }

    public Set getTrimmedConceptSet() {
        return trimmedConceptSet;
    }

    public void addAllDomain(Set set) {
        domainSet.addAll(set);
    }

    public void addAllRange(Set set) {
        rangeSet.addAll(set);
    }

    public void addDomain(String id) {
        domainSet.add(id);
    }

    public Set getDomainSet() {
        return domainSet;
    }

    public Set getRangeSet() {
        return rangeSet;
    }

    public void addRange(String id) {
        rangeSet.add(id);
    }

    public void deleteDomain(String id) {
        // System.out.println("delete domain");
        domainSet.remove(id);
    }

    public void deleteRange(String id) {
        // System.out.println("delete range");
        rangeSet.remove(id);
    }
}
