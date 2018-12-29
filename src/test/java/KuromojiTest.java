import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.List;

public class KuromojiTest {
    public static void main(String[] args) {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize("領域オントロジー構築支援環境");
        for (Token token : tokens) {
            System.out.println(token.getSurface() + "\t" + token.getAllFeatures());
        }
    }
}
