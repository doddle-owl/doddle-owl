/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: https://doddle-owl.github.io/
 *
 * Copyright (C) 2004-2024 Takeshi Morita. All rights reserved.
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

package io.github.doddle_owl.models.reference_ontology_selection;

import io.github.doddle_owl.DODDLE_OWL;

import java.io.File;
import java.io.InputStream;

/**
 * @author Takeshi Morita
 */
public class WebOntologyExtractionTemplate {

    private File OWLMetaDataExtractionTemplate;
    private File classExtractionTemplate;
    private File propertyExtractionTemplate;
    private File annotationExtractionTemplate;
    private File domainRangeExtractionTemplate;
    private File isaRelationExtractionTemplate;

    public WebOntologyExtractionTemplate() {
        setOWLMetaDataExtractionTemplate(new File("ontology_templates/OWLMetaDataExtraction.rq"));
        setClassExtractionTemplate(new File("ontology_templates/ClassExtraction.rq"));
        setPropertyExtractionTemplate(new File("ontology_templates/PropertyExtraction.rq"));
        setAnnotationExtractionTemplate(new File("ontology_templates/AnnotationExtraction.rq"));
        setDomainRangeExtractionTemplate(new File("ontology_templates/DomainRangeExtraction.rq"));
        setIsaRelationExtractionTemplate(new File("ontology_templates/IsaRelationExtraction.rq"));
    }

    public String getOWLMetaDataExtractionTemplateLabel() {
        if (OWLMetaDataExtractionTemplate.exists()) {
            return OWLMetaDataExtractionTemplate.getAbsolutePath();
        }
        return "Default Search OWL Meta Data Template";
    }

    public File getOWLMetaDataExtractionTemplate() {
        return OWLMetaDataExtractionTemplate;
    }

    public InputStream getDefaultSearchOWLMetaDataTemplate() {
        return DODDLE_OWL.class.getClassLoader().getResourceAsStream("ontology_templates/OWLMetaDataExtraction.rq");
    }

    private void setOWLMetaDataExtractionTemplate(File OWLMetaDataExtractionTemplate) {
        this.OWLMetaDataExtractionTemplate = OWLMetaDataExtractionTemplate;
    }

    public String getIsaRelationExtractionTemplateLabel() {
        if (isaRelationExtractionTemplate.exists()) {
            return isaRelationExtractionTemplate.getAbsolutePath();
        }
        return "Default Is-a Relation Extraction Template";
    }

    public File getIsaRelationExtractionTemplate() {
        return isaRelationExtractionTemplate;
    }

    public InputStream getDefaultIsaRelationExtractionTemplate() {
        return DODDLE_OWL.class.getClassLoader().getResourceAsStream("ontology_templates/IsaRelationExtraction.rq");
    }

    public void setIsaRelationExtractionTemplate(File isaRelationExtractionTemplate) {
        this.isaRelationExtractionTemplate = isaRelationExtractionTemplate;
    }

    public String getClassExtractionTemplateLabel() {
        if (classExtractionTemplate.exists()) {
            return classExtractionTemplate.getAbsolutePath();
        }
        return "Default Class Extraction Template";
    }

    public File getClassExtractionTemplate() {
        return classExtractionTemplate;
    }

    public InputStream getDefaultClassExtractionTemplate() {
        return DODDLE_OWL.class.getClassLoader().getResourceAsStream("ontology_templates/ClassExtraction.rq");
    }

    public void setClassExtractionTemplate(File searchClassSetTemplate) {
        this.classExtractionTemplate = searchClassSetTemplate;
    }

    public String getAnnotationExtractionTemplateLabel() {
        if (annotationExtractionTemplate.exists()) {
            return annotationExtractionTemplate.getAbsolutePath();
        }
        return "Default Annotation Extraction Template";
    }

    public File getAnnotationExtractionTemplate() {
        return annotationExtractionTemplate;
    }

    public InputStream getDefaultAnnotationExtractionTemplate() {
        return DODDLE_OWL.class.getClassLoader().getResourceAsStream("ontology_templates/AnnotationExtraction.rq");
    }

    public void setAnnotationExtractionTemplate(File annotationExtractionTemplate) {
        this.annotationExtractionTemplate = annotationExtractionTemplate;
    }

    public String getPropertyExtractionTemplateLabel() {
        if (propertyExtractionTemplate.exists()) {
            return propertyExtractionTemplate.getAbsolutePath();
        }
        return "Default Property Extraction Template";
    }

    public File getPropertyExtractionTemplate() {
        return propertyExtractionTemplate;
    }

    public InputStream getDefaultPropertyExtractionTemplate() {
        return DODDLE_OWL.class.getClassLoader().getResourceAsStream("ontology_templates/PropertyExtraction.rq");
    }

    public void setPropertyExtractionTemplate(File propertyExtractionTemplate) {
        this.propertyExtractionTemplate = propertyExtractionTemplate;
    }

    public String getDomainRangeExtractionTemplateLabel() {
        if (domainRangeExtractionTemplate.exists()) {
            return domainRangeExtractionTemplate.getAbsolutePath();
        }
        return "Default Domain and Range Extraction Template";
    }

    public File getDomainRangeExtractionTemplate() {
        return domainRangeExtractionTemplate;
    }

    public InputStream getDefaultDomainRangeExtractionTemplate() {
        return DODDLE_OWL.class.getClassLoader().getResourceAsStream("ontology_templates/DomainRangeExtraction.rq");
    }

    public void setDomainRangeExtractionTemplate(File domainRangeExtractionTemplate) {
        this.domainRangeExtractionTemplate = domainRangeExtractionTemplate;
    }

}
