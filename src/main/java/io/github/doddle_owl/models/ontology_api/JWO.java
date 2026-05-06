package io.github.doddle_owl.models.ontology_api;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import io.github.doddle_owl.models.common.DODDLEConstants;
import io.github.doddle_owl.models.reference_ontology_selection.ReferenceWebOntology;
import io.github.doddle_owl.utils.WebOntologyManager;
import io.github.doddle_owl.views.reference_ontology_selection.NameSpaceTable;
import org.apache.jena.tdb2.TDB2Factory;

import java.io.File;

public class JWO {

    private static Dataset dataset;
    public static boolean isAvailable = false;

    public static boolean initJWODic(NameSpaceTable nameSpaceTable) {
        File jwoDir = new File(DODDLEConstants.JWO_HOME);
        if (jwoDir.exists()) {
            if (WebOntologyManager.getRefOntology(jwoDir.getAbsolutePath()) == null) {
                dataset = TDB2Factory.connectDataset(jwoDir.getAbsolutePath());
                Model ontModel = dataset.getDefaultModel();
                ReferenceWebOntology refOnt = new ReferenceWebOntology(ontModel, jwoDir.getAbsolutePath(), nameSpaceTable);
                WebOntologyManager.addRefOntology(refOnt.getURI(), refOnt);
            }
            isAvailable = true;
        } else {
            isAvailable = false;
        }
        return isAvailable;
    }

    public static void closeDataSet() {
        if (dataset != null) {
            dataset.close();
        }
    }
}
