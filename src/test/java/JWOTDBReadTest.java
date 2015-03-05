import java.io.File;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

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
