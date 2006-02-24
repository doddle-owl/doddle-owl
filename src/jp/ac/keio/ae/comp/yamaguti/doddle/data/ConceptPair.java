package jp.ac.keio.ae.comp.yamaguti.doddle.data;
/**
 * @author shigeta
 * 
 */
public class ConceptPair implements Comparable {

    private String fromConcept;
    private String toConcept;
    private Double relationValue = new Double(0);

    private Double wsValue = new Double(0);
    private Double arValue = new Double(0);
    private Double nvValue = new Double(0);
    private Double sumValue = new Double(0);

    private boolean isCorrectPair = false;
    private boolean isNotFoundPair = false;

    public int compareTo(Object o) {
        ConceptPair pair = (ConceptPair) o;
        return pair.getRelatoinValue().compareTo(this.getRelatoinValue());
        // return this.relationValue.compareTo(pair.getRelatoinValue());
    }

    public void setRelationValue(String method) {
        // method == ws or ar or nv
        if (method.equals("ws")) {
            wsValue = relationValue;
        } else if (method.equals("ar")) {
            arValue = relationValue;
        } else if (method.equals("nv")) {
            nvValue = relationValue;
        }
    }

    public void setSumValue() {
        double sum = 0;
        sum = wsValue.doubleValue() + arValue.doubleValue() + nvValue.doubleValue();
        sumValue = new Double(sum);
    }

    public ConceptPair(String A, String B, Double value) {
        fromConcept = A;
        toConcept = B;
        relationValue = value;
    }

    public ConceptPair(String A, String B) {
        fromConcept = A;
        toConcept = B;
    }

    public ConceptPair() {
    }

    public String getFromConceptLong() { // +++
        return fromConcept;
    }

    public String getToConceptLong() { // +++
        return toConcept;
    }

    public void setValues(String fromC, String toC, Double value) {
        fromConcept = fromC;
        toConcept = toC;
        relationValue = value;
    }

    /*
     * public String toString() { return fromConcept.toString() + "--->" +
     * toConcept.toString() + "=" + relationValue.toString(); }
     */
    public String getCombinationToString() {
        return fromConcept.toString() + "->" + toConcept.toString();
    }

    public String toString() {
        return fromConcept + "-->" + toConcept + "\t\t" + relationValue;
    }

    public String toIdentifier() {
        String identifier = fromConcept + ":" + toConcept;
        return identifier;
    }

    /**
     * ˆø”‚ÌNonTaxonomicRelationPair ‚Ì’l‚ð‰ÁŽZ
     * 
     * @param aPair
     */
    public void addValues(ConceptPair aPair) {
        double wsDouble;
        double arDouble;
        double nvDouble;
        double sumDouble;

        wsDouble = wsValue.doubleValue() + aPair.getWsValue().doubleValue();
        arDouble = arValue.doubleValue() + aPair.getArValue().doubleValue();
        nvDouble = nvValue.doubleValue() + aPair.getNvValue().doubleValue();
        sumDouble = wsDouble + arDouble + nvDouble;

        wsValue = new Double(wsDouble);
        arValue = new Double(arDouble);
        nvValue = new Double(nvDouble);
        sumValue = new Double(sumDouble);
    }

    public String toOffsetAndNameString() {
        return fromConcept + "-->" + toConcept + "=" + relationValue;
    }

    public String[] getTableData() {
        String[] str = new String[2];
        str[0] = toConcept;
        str[1] = relationValue.toString();
        return str;
    }

    public String getToConceptLabel() {
        return toConcept;
    }

    public String getFromConceptLabel() {
        return fromConcept;
    }

    public String getToConceptString() {
        return toConcept.toString();

    }

    public boolean isSameCombination(ConceptPair pair) {
        return (pair.getCombinationToString().equals(this.getCombinationToString()));
    }

    public boolean isSameCombination(String str) {
        return (this.getCombinationToString().equals(str));
    }

    // getter & setter

    public Double getRelatoinValue() {
        return relationValue;
    }

    public double getRelationDoubleValue() {
        return relationValue.doubleValue();
    }

    public float getRelationFloatValue() {
        return relationValue.floatValue();
    }

    /**
     * @return
     */
    public Double getArValue() {
        return arValue;
    }

    /**
     * @return
     */
    public boolean isCorrectPair() {
        return isCorrectPair;
    }

    /**
     * @return
     */
    public Double getNvValue() {
        return nvValue;
    }

    /**
     * @return
     */
    public Double getSumValue() {
        return sumValue;
    }

    /**
     * @return
     */
    public Double getWsValue() {
        return wsValue;
    }

    /**
     * @param double1
     */
    public void setArValue(Double double1) {
        arValue = double1;
    }

    /**
     * @param b
     */
    public void setCorrectPair(boolean b) {
        isCorrectPair = b;
    }

    /**
     * @param double1
     */
    public void setNvValue(Double double1) {
        nvValue = double1;
    }

    /**
     * @param double1
     */
    public void setSumValue(Double double1) {
        sumValue = double1;
    }

    /**
     * @param double1
     */
    public void setWsValue(Double double1) {
        wsValue = double1;
    }

    /**
     * @return
     */
    public boolean isNotFoundPair() {
        return isNotFoundPair;
    }

    /**
     * @param b
     */
    public void setNotFoundPair(boolean b) {
        isNotFoundPair = b;
    }
}