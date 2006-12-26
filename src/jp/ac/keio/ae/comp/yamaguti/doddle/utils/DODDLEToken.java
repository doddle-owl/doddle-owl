/*
 * @(#)  2006/12/26
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

/**
 * @author takeshi morita
 */
public class DODDLEToken {
    
    private String pos;
    private String basicString;
    private String string;

    public DODDLEToken(String p, String bs, String s) {
        pos = p;
        basicString = bs;
        string = s;
    }
    
    public String getPos() {
        return pos;
    }
    
    public String getBasicString() {
        return basicString;
    }
    
    public String getString() {
        return string;
    }
}
