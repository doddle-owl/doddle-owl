/*
 * @(#)  2006/02/16
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

/**
 * @author takeshi morita
 */
public class NameSpaceTable {

    private static Map<String, String> prefixNSMap;

    static {
        prefixNSMap = new HashMap<String, String>();
        prefixNSMap.put("wn", "http://wordnet.princeton.edu/wn/2.0#");
        prefixNSMap.put("edr", "http://www2.nict.go.jp/kk/e416/EDR#");
    }

    public static String getNS(String prefix) {
        return prefixNSMap.get(prefix);
    }

}
