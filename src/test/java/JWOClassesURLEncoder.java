import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

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
				Resource objectRes = null;
				Resource encodedObjectRes = null;
				if (predicate.equals(RDFS.subClassOf)) {
					objectRes = (Resource) object;
					String[] nsAndLocalName = objectRes.getURI().split("class/");
					if (nsAndLocalName.length == 2) {
						String localName = nsAndLocalName[1];
						try {
							String encodedLocalName = URLEncoder.encode(localName, "UTF-8");
							encodedObjectRes = ResourceFactory.createResource(jwoNS
									+ encodedLocalName);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				String[] nsAndLocalName = subject.getURI().split("class/");
				if (nsAndLocalName.length == 2) {
					String localName = nsAndLocalName[1];
					try {
						String encodedLocalName = URLEncoder.encode(localName, "UTF-8");
						Resource encodedRes = ResourceFactory.createResource(jwoNS
								+ encodedLocalName);
						if (encodedObjectRes != null) {
							refinedModel.add(encodedRes, predicate, encodedObjectRes);
						} else {
							refinedModel.add(encodedRes, predicate, object);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println(refinedModel.size());
		dataset.close();
	}
}
