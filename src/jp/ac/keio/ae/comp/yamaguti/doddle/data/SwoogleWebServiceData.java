/*
 * @(#)  2007/03/13
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;

/**
 * @author takeshi morita
 */
public class SwoogleWebServiceData {

    private Set<Resource> classSet;
    private Set<Resource> propertySet;

    private Map<String, SwoogleOWLMetaData> uriSwoogleOWLMetaDataMap;
    private static Map<String, ReferenceOWLOntology> uriRefOntologyMap;
    
    public SwoogleWebServiceData() {
        classSet = new HashSet<Resource>();
        propertySet = new HashSet<Resource>();
        uriSwoogleOWLMetaDataMap = new HashMap<String, SwoogleOWLMetaData>();
        uriRefOntologyMap  = new HashMap<String, ReferenceOWLOntology>();
    }
    
    public void putRefOntology(String uri, ReferenceOWLOntology refOntology) {
        uriRefOntologyMap.put(uri, refOntology);
    }
    
    public ReferenceOWLOntology getRefOntology(String uri) {
        return uriRefOntologyMap.get(uri);
    }
    
    public Set<String> getRefOntologyURISet() {
        return uriRefOntologyMap.keySet();
    }

    public void putSwoogleOWLMetaData(String uri, SwoogleOWLMetaData data) {
        uriSwoogleOWLMetaDataMap.put(uri, data);
    }
    
    public SwoogleOWLMetaData getSwoogleOWLMetaData(String uri) {
        return uriSwoogleOWLMetaDataMap.get(uri);
    }

    public void addClass(Resource property) {
        classSet.add(property);
    }
    
    public Set<Resource> getClassSet() {
        return classSet;
    }

    public void addProperty(Resource property) {
        propertySet.add(property);
    }
    
    public Set<Resource> getPropertySet() {
        return propertySet;
    }
    
    public String toString() {
        return uriSwoogleOWLMetaDataMap.toString();
    }
}
