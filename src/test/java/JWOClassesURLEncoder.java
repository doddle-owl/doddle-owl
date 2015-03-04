import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class JWOClassesURLEncoder {
	public static void main(String[] args) {
		Model model = FileManager.get().loadModel("/Users/t_morita/Downloads/jwo_classes_org.owl");
		Model refinedModel = ModelFactory.createDefaultModel();
		String jwoNS = "http://www.wikipediaontology.org/class/";
		for (Resource res : model.listSubjectsWithProperty(RDFS.subClassOf).toSet()) {
			for (Statement stmt : res.listProperties().toSet()) {
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
		}
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(
					"/Users/t_morita/Downloads/jwo_classes.owl"), "UTF-8");
			refinedModel.write(writer, "RDF/XML-ABBREV");
			System.out.println(refinedModel.size());
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
