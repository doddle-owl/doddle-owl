/*
 * @(#)  2007/03/16
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

/**
 * @author takeshi morita
 */
public class OntologyRank {

    private double swoogleOntoRank; // Swoogle's OntoRank
    private double inputWordRatio;    // concept count / input word count
    private int relationCount;             // domain-property-range relation count
    
    public OntologyRank() {
    }

    public double getInputWordRatio() {
        return inputWordRatio;
    }

    public void setInputWordRatio(double inputWordRatio) {
        this.inputWordRatio = inputWordRatio;
    }

    public int getRelationCount() {
        return relationCount;
    }

    public void setRelationCount(int relationCount) {
        this.relationCount = relationCount;
    }

    public double getSwoogleOntoRank() {
        return swoogleOntoRank;
    }

    public void setSwoogleOntoRank(double swoogleOntoRank) {
        this.swoogleOntoRank = swoogleOntoRank;
    }

    public int compareTo(Object obj) {
        OntologyRank ontoRank = (OntologyRank) obj;
        if (ontoRank.getInputWordRatio() < inputWordRatio) {
            return 1;
        } else if (ontoRank.getInputWordRatio() > inputWordRatio) {
            return -1;
        } else {
            if (ontoRank.getRelationCount() < relationCount) {
                return 1;
            } else if (ontoRank.getRelationCount() > relationCount) {
                return -1;
            } else {
                if (ontoRank.getSwoogleOntoRank() < swoogleOntoRank) {
                    return 1;
                } else if (ontoRank.getSwoogleOntoRank() > swoogleOntoRank) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
    
    public String toString() {
        return "["+String.format("%.3f", swoogleOntoRank)+"]["+String.format("%.3f", inputWordRatio)+"]["+relationCount+"]";
    }
}
