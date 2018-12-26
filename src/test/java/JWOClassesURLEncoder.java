import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class JWOClassesURLEncoder {
	public static void main(String[] args) {
		Model model = FileManager.get().loadModel("/Users/t_morita/Downloads/jwo_classes_org.owl");
		Dataset dataset = TDBFactory.createDataset("/Users/t_morita/Downloads/jwo");
		Model refinedModel = dataset.getDefaultModel();
		String jwoNS = "http://www.wikipediaontology.org/class/";

		for (Statement stmt : model.listStatements().toSet()) {
			Resource subject = stmt.getSubject();
			Property predicate = stmt.getPredicate();
			RDFNode object = stmt.getObject();
			if (predicate.equals(RDF.type) || predicate.equals(RDFS.label)
					|| predicate.equals(RDFS.comment) || predicate.equals(RDFS.subClassOf)) {
				Resource objectRes;
				Resource encodedObjectRes = null;
				if (predicate.equals(RDFS.subClassOf)) {
					objectRes = (Resource) object;
					String[] nsAndLocalName = objectRes.getURI().split("class/");
					if (nsAndLocalName.length == 2) {
						String localName = nsAndLocalName[1];
						String encodedLocalName = URLEncoder.encode(localName, StandardCharsets.UTF_8);
						encodedObjectRes = ResourceFactory.createResource(jwoNS
								+ encodedLocalName);
					}
				}
				String[] nsAndLocalName = subject.getURI().split("class/");
				if (nsAndLocalName.length == 2) {
					String localName = nsAndLocalName[1];
					String encodedLocalName = URLEncoder.encode(localName, StandardCharsets.UTF_8);
					Resource encodedRes = ResourceFactory.createResource(jwoNS
							+ encodedLocalName);
					if (encodedObjectRes != null) {
						refinedModel.add(encodedRes, predicate, encodedObjectRes);
					} else {
						refinedModel.add(encodedRes, predicate, object);
					}
				}
			}
		}
		System.out.println(refinedModel.size());
		dataset.close();
	}
}
