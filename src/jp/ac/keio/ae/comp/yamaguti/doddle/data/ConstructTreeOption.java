package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;

/**
 * @author takeshi morita
 */
public class ConstructTreeOption {
    private Concept c;
    private String option;

    public ConstructTreeOption(Concept c) {
        this.c = c;
        if (OptionDialog.isComplexWordSetSameConcept()) {
            option = "SAME";
        } else {
            option = "SUB";
        }
    }

    public ConstructTreeOption(Concept c, String opt) {
        this.c = c;
        option = opt;
    }

    public void setConcept(Concept c) {
        this.c = c;
    }

    public Concept getConcept() {
        return c;
    }

    public void setOption(String opt) {
        option = opt;
    }

    public String getOption() {
        return option;
    }
}
