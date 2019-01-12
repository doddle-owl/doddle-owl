package org.doddle_owl;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class StanfordParserTest {
    public static void main(String[] args) {
        Document doc = new Document("add your text here! It can contain multiple sentences.");
        for (Sentence s : doc.sentences()) {
            System.out.println(s);
            for (String w : s.words()) {
                System.out.println(w);
            }
            for (String l : s.lemmas()) {
                System.out.println(l);
            }
            for (String p : s.posTags()) {
                System.out.println(p);
            }
        }

        
    }
}
