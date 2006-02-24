package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.io.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import net.java.sen.*;

/**
 * @author takeshi morita
 */
public class Utils {
    public static final String RESOURCE_DIR = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";

    public static ImageIcon getImageIcon(String icon) {
        return new ImageIcon(DODDLE.class.getClassLoader().getResource(RESOURCE_DIR + icon));
    }

    public static void addJaComplexWord(List tokenList, Set<String> complexWordSet) {
        try {
            for (String complexWord : complexWordSet) {
                StringTagger tagger = StringTagger.getInstance();
                Token[] complexWordToken = tagger.analyze(complexWord);
                List complexWordList = new ArrayList();
                for (int i = 0; i < complexWordToken.length; i++) {
                    complexWordList.add(complexWordToken[i].getBasicString());
                }
                // System.out.println(complexWord);
                // System.out.println(complexWordList);
                Utils.addComplexWord(complexWord, complexWordList, tokenList, complexWordSet);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void addEnComplexWord(List tokenList, Set<String> complexWordSet) {
        for (String complexWord : complexWordSet) {
            List complexWordList = Arrays.asList(complexWord.split("\\s+"));
            Utils.addComplexWord(complexWord, complexWordList, tokenList, complexWordSet);
        }
    }

    private static void addComplexWord(String complexWord, List complexWordList, List tokenList, Set complexWordSet) {
        for (int i = 0; i < tokenList.size(); i++) {
            List complexWordSizeList = new ArrayList();
            for (int j = 0; complexWordSizeList.size() != complexWordList.size(); j++) {
                if ((i + j) == tokenList.size()) {
                    break;
                }
                String nw = (String) tokenList.get(i + j);
                if (complexWordSet.contains(nw)) {
                    continue;
                }
                complexWordSizeList.add(nw);
            }
            if (complexWordList.size() == complexWordSizeList.size()) {
                boolean isComplexWordList = true;
                for (int j = 0; j < complexWordList.size(); j++) {
                    if (!complexWordList.get(j).equals(complexWordSizeList.get(j))) {
                        isComplexWordList = false;
                        break;
                    }
                }
                if (isComplexWordList) {
                    tokenList.add(i, complexWord);
                    i++;
                }
            }
        }
    }

}
