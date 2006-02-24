package jp.ac.keio.ae.comp.yamaguti.doddle.data;

/**
 * @author takeshi morita
 */
public class WordSpaceData {

    private int gramNumber;
    private int gramCount;
    private int frontScope;
    private int behindScope;
    private double underValue;

    public WordSpaceData() {
    }

    public WordSpaceData(int gn, int gc, int fs, int bs, double uv) {
        gramNumber = gn;
        gramCount = gc;
        frontScope = fs;
        behindScope = bs;
        underValue = uv;
    }

    public void setGramNumber(int n) {
        gramNumber = n;
    }

    public int getGramNumber() {
        return gramNumber;
    }

    public void setGramCount(int n) {
        gramCount = n;
    }

    public int getGramCount() {
        return gramCount;
    }

    public void setFrontScope(int n) {
        frontScope = n;
    }

    public int getFrontScope() {
        return frontScope;
    }

    public void setBehindScope(int n) {
        behindScope = n;
    }

    public int getBehindScope() {
        return behindScope;
    }

    public void setUnderValue(double n) {
        underValue = n;
    }

    public double getUnderValue() {
        return underValue;
    }
}