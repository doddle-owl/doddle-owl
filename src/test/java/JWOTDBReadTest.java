import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDFS;

import java.io.File;

public class JWOTDBReadTest {
	public static void main(String[] args) {
		File dir = new File("/Users/t_morita/Downloads/jwo");
		for (File f : dir.listFiles()) {
			// System.out.println(f.getName());
		}
		Dataset dataset = TDBFactory.createDataset("/Users/t_morita/Downloads/jwo");
		Model model = dataset.getDefaultModel();
		for (NodeIterator i = model.listObjectsOfProperty(RDFS.label); i.hasNext();) {
			Literal literal = i.nextNode().asLiteral();
			System.out.println(literal.toString());
		}

		dataset.close();
	}
}
