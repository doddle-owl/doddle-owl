/*
 * @(#)  2007/02/09
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class OWLOntologyExtractionTemplate {

    private File searchLabelSetTemplate;
    private File searchClassSetTemplate;
    private File searchPropertySetTemplate;
    private File searchConceptTemplate;
    private File searchDomainSetTemplate;
    private File searchRangeSetTemplate;
    private File searchSubConceptTemplate;

    public static final String RESOURCE_DIR = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";

    public OWLOntologyExtractionTemplate() {
        setSearchClassSetTemplate(new File("ontology_templates/SearchClassSet.rq"));
        setSearchPropertySetTemplate(new File("ontology_templates/SearchPropertySet.rq"));
        setSearchConceptTemplate(new File("ontology_templates/SearchConcept.rq"));
        setSearchLabelSetTemplate(new File("ontology_templates/SearchLabelSet.rq"));
        setSearchDomainSetTemplate(new File("ontology_templates/SearchDomainSet.rq"));
        setSearchRangeSetTemplate(new File("ontology_templates/SearchRangeSet.rq"));
        setSearchSubConceptTemplate(new File("ontology_templates/SearchSubConcept.rq"));
    }

    public String getSearchSubConceptTemplateLabel() {
        if (searchSubConceptTemplate.exists()) { return searchSubConceptTemplate.getAbsolutePath(); }
        return "Default Search Sub Concept Template";
    }

    public File getSearchSubConceptTemplate() {
        return searchSubConceptTemplate;
    }

    public InputStream getDefaultSearchSubConceptTemplate() {
        return DODDLE.class.getClassLoader().getResourceAsStream(
                RESOURCE_DIR + "ontology_templates/SearchSubConcept.rq");
    }

    public void setSearchSubConceptTemplate(File searchSubConceptTemplate) {
        this.searchSubConceptTemplate = searchSubConceptTemplate;
    }

    public String getSearchClassSetTemplateLabel() {
        if (searchClassSetTemplate.exists()) { return searchClassSetTemplate.getAbsolutePath(); }
        return "Default Search Class Set Template";
    }

    public File getSearchClassSetTemplate() {
        return searchClassSetTemplate;
    }

    public InputStream getDefaultSearchClassSetTemplate() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "ontology_templates/SearchClassSet.rq");
    }

    public void setSearchClassSetTemplate(File searchClassSetTemplate) {
        this.searchClassSetTemplate = searchClassSetTemplate;
    }

    public String getSearchConceptTemplateLabel() {
        if (searchConceptTemplate.exists()) {
            return searchConceptTemplate.getAbsolutePath();
        } 
        return "Default Search Concept Template";
    }
    
    public File getSearchConceptTemplate() {
        return searchConceptTemplate;
    }

    public InputStream getDefaultSearchConceptTemplate() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "ontology_templates/SearchConcept.rq");
    }

    public void setSearchConceptTemplate(File searchConceptTemplate) {
        this.searchConceptTemplate = searchConceptTemplate;
    }

    public String getSearchDomainSetTemplateLabel() {
        if (searchDomainSetTemplate.exists()) {
            return searchDomainSetTemplate.getAbsolutePath();
        } 
        return "Default Search Domain Set Template";
    }
    
    public File getSearchDomainSetTemplate() {
        return searchDomainSetTemplate;
    }

    public InputStream getDefaultSearchDomainSetTemplate() {
        return DODDLE.class.getClassLoader()
                .getResourceAsStream(RESOURCE_DIR + "ontology_templates/SearchDomainSet.rq");
    }

    public void setSearchDomainSetTemplate(File searchDomainSetTemplate) {
        this.searchDomainSetTemplate = searchDomainSetTemplate;
    }

    public String getSearchLabelSetTemplateLabel() {
        if (searchLabelSetTemplate.exists()) {
            return searchLabelSetTemplate.getAbsolutePath();
        } 
        return "Default Search Label Set Template";
    }
    
    public File getSearchLabelSetTemplate() {
        return searchLabelSetTemplate;
    }

    public InputStream getDefaultSearchLabelSetTemplate() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "ontology_templates/SearchLabelSet.rq");
    }

    public void setSearchLabelSetTemplate(File searchLabelSetTemplate) {
        this.searchLabelSetTemplate = searchLabelSetTemplate;
    }

    public String getSearchPropertySetTemplateLabel() {
        if (searchPropertySetTemplate.exists()) {
            return searchPropertySetTemplate.getAbsolutePath();
        } 
        return "Default Search Property Set Template";
    }
    
    public File getSearchPropertySetTemplate() {
        return searchPropertySetTemplate;
    }

    public InputStream getDefaultSearchPropertySetTemplate() {
        return DODDLE.class.getClassLoader().getResourceAsStream(
                RESOURCE_DIR + "ontology_templates/SearchPropertySet.rq");
    }

    public void setSearchPropertySetTemplate(File searchPropertySetTemplate) {
        this.searchPropertySetTemplate = searchPropertySetTemplate;
    }

    public String getSearchRangeSetTemplateLabel() {
        if (searchRangeSetTemplate.exists()) {
            return searchRangeSetTemplate.getAbsolutePath();
        } 
        return "Default Search Range Set Template";
    }
    
    public File getSearchRangeSetTemplate() {
        return searchRangeSetTemplate;
    }

    public InputStream getDefaultSearchRangeSetTemplate() {
        return DODDLE.class.getClassLoader().getResourceAsStream(RESOURCE_DIR + "ontology_templates/SearchRangeSet.rq");
    }

    public void setSearchRangeSetTemplate(File searchRangeSetTemplate) {
        this.searchRangeSetTemplate = searchRangeSetTemplate;
    }

}
