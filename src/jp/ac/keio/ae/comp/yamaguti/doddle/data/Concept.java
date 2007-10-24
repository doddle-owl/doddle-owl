package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.io.*;
import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.ui.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

import com.hp.hpl.jena.rdf.model.*;

/** 
 *  このクラスを編集した場合，DBを作成しなおす必要あり
 */

/**
 * @author takeshi morita
 */
public class Concept implements Serializable {

    private String uriStr;
    transient private Resource uri; 
    private DODDLELiteral inputLabel;
    private Map<String, List<DODDLELiteral>> langLabelListMap;
    private Map<String, List<DODDLELiteral>> langDescriptionListMap;

    public Concept() {
        langLabelListMap = new HashMap<String, List<DODDLELiteral>>();
        langDescriptionListMap = new HashMap<String, List<DODDLELiteral>>();
    }

    public Concept(Concept c) {
        uri = c.getResource();
        uriStr = c.getURI();
        langLabelListMap = c.getLangLabelListMap();
        langDescriptionListMap = c.getLangDescriptionListMap();
        if (c.getInputLabel() != null && !c.getInputLabel().getString().equals("")) {
            inputLabel = c.getInputLabel();
        }
    }

    public Concept(String uri, String word) {
        setURI(uri);
        langLabelListMap = new HashMap<String, List<DODDLELiteral>>();
        if (0 < word.length()) {
            DODDLELiteral literal = new DODDLELiteral(DODDLEConstants.LANG, word);
            List<DODDLELiteral> literalList =new ArrayList<DODDLELiteral>();
            literalList.add(literal);
            langLabelListMap.put(DODDLEConstants.LANG, literalList);
        }
        langDescriptionListMap = new HashMap<String, List<DODDLELiteral>>();
    }   

    public Concept(String uri, String[] items) {
        setURI(uri);

        langLabelListMap = new HashMap<String, List<DODDLELiteral>>();
        langDescriptionListMap = new HashMap<String, List<DODDLELiteral>>();
        
        String jaWord = removeNullWords(items[0]);
        String[] jaWords = jaWord.split("\t");
        List<DODDLELiteral> jaLabelLiteralList = new ArrayList<DODDLELiteral>();
        for (int i = 0; i < jaWords.length; i++) {            
            if (0 < jaWords[i].length()) {
                DODDLELiteral literal = new DODDLELiteral("ja", jaWords[i]);
                jaLabelLiteralList.add(literal);
            }
        }
        if (0 < jaLabelLiteralList.size()) {
            langLabelListMap.put("ja", jaLabelLiteralList);
        }
        
        String enWord = removeNullWords(items[1]);
        String[] enWords = enWord.split("\t");
        List<DODDLELiteral> enLabelLiteralList = new ArrayList<DODDLELiteral>();
        for (int i = 0; i < enWords.length; i++) {
            if (0 < enWords[i].length()) {
                DODDLELiteral literal = new DODDLELiteral("en", enWords[i]);
                enLabelLiteralList.add(literal);
            }
        }
        if (0 < enLabelLiteralList.size()) {
            langLabelListMap.put("en", enLabelLiteralList);
        }
        
        String jaDescription = removeNullWords(items[2]);
        jaDescription = jaDescription.replaceAll("\t", "");
        if (0 < jaDescription.length()) {
            List<DODDLELiteral> jaDescriptionLiteralList = new ArrayList<DODDLELiteral>();
            DODDLELiteral jaDescriptionLiteral = new DODDLELiteral("ja", jaDescription);
            jaDescriptionLiteralList.add(jaDescriptionLiteral);
            langDescriptionListMap.put("ja", jaDescriptionLiteralList);
        }
        
        String enDescription = removeNullWords(items[3]);
        enDescription = enDescription.replaceAll("\t", "");
        if (0 < enDescription.length()) {
            List<DODDLELiteral> enDescriptionLiteralList = new ArrayList<DODDLELiteral>();
            DODDLELiteral enDescriptionLiteral = new DODDLELiteral("en", enDescription);
            enDescriptionLiteralList.add(enDescriptionLiteral);
            langDescriptionListMap.put("en", enDescriptionLiteralList);
        }
        
        setInputWord();
    }

    public void setURI(String uri) {
        uriStr = uri;
        this.uri = ResourceFactory.createResource(uri);    
    }

    private String removeNullWords(String str) {
        return str.replaceAll("\\*\\*\\*", "");
    }

    public void setInputWord() {
        inputLabel = null;
        List<DODDLELiteral> literalList = langLabelListMap.get(DODDLEConstants.LANG);
        if (literalList != null && 0 < literalList.size()) {
            inputLabel = literalList.get(0);
            return;
        }
        for (String lang: langLabelListMap.keySet()) {
            literalList = langLabelListMap.get(lang);
            if (literalList != null && 0 < literalList.size()) {
                inputLabel = literalList.get(0);
                return;
            }
        }        
    }

    public void setInputLabel(DODDLELiteral label) {
        inputLabel = label;
    }

    public DODDLELiteral getInputLabel() {
        return inputLabel;
    }


    public void addLabel(DODDLELiteral labelLiteral) {
        if (labelLiteral.getString().length() == 0) {
            return;
        }
        String string =labelLiteral.getString();
        string = string.replaceAll("\t", "");
        labelLiteral.setString(string);
        String lang = labelLiteral.getLang();
        if (labelLiteral.getLang().length()==0) {
            lang = "default";
        }
        List<DODDLELiteral> labelLiteralList = langLabelListMap.get(lang);
        if (labelLiteralList != null) {
            if (!labelLiteralList.contains(labelLiteral)) {
                labelLiteralList.add(labelLiteral);
            } 
        } else {
            labelLiteralList = new ArrayList<DODDLELiteral>();
            labelLiteralList.add(labelLiteral);
            langLabelListMap.put(lang, labelLiteralList);
        }
    }
    
    public void removeLabel(DODDLELiteral label) {
        if (label.getLang().length() == 0) {
            List<DODDLELiteral> labelLiteralList = langLabelListMap.get("default");
            labelLiteralList.remove(label);            
            if (labelLiteralList.size()==0) {
                langLabelListMap.remove("default");
            }
        }else {
            List<DODDLELiteral> labelLiteralList = langLabelListMap.get(label.getLang());
            labelLiteralList.remove(label);
            if (labelLiteralList.size()==0) {
                langLabelListMap.remove(label.getLang());
            }
        }
    }
    
    public void addDescription(DODDLELiteral descriptionLiteral) {
        if (descriptionLiteral.getString().length() == 0) {
            return;
        }
        String lang = descriptionLiteral.getLang();
        if (descriptionLiteral.getLang().length()==0) {
            lang = "default";
        }
        List<DODDLELiteral> descriptionLiteralList = langDescriptionListMap.get(lang);
        if (descriptionLiteralList != null) {
            if (!descriptionLiteralList.contains(descriptionLiteral)) {
                descriptionLiteralList.add(descriptionLiteral);    
            }
        } else {
            descriptionLiteralList = new ArrayList<DODDLELiteral>();
            descriptionLiteralList.add(descriptionLiteral);
            langDescriptionListMap.put(descriptionLiteral.getLang(), descriptionLiteralList);
        }
    }
    
    public void removeDescription(DODDLELiteral description) {
        if (description.getLang().length()==0){
            List<DODDLELiteral> descriptionLiteralList = langDescriptionListMap.get("default");
            descriptionLiteralList.remove(description);
            if (descriptionLiteralList.size()==0) {
                langDescriptionListMap.remove("default");
            }
        } else {
            List<DODDLELiteral> descriptionLiteralList = langDescriptionListMap.get(description.getLang());
            descriptionLiteralList.remove(description);
            if (descriptionLiteralList.size()==0) {
                langDescriptionListMap.remove(description.getLang());
            }
        }
    }
    
    public Resource getResource() {
        return uri;
    }
    
    public String getURI() {
        return uri.getURI();
    }

    public Map<String, List<DODDLELiteral>> getLangLabelListMap() {
        return langLabelListMap;
    }
    
    public Map<String, List<DODDLELiteral>> getLangDescriptionListMap() {
        return langDescriptionListMap;
    }
    
    public String getWord() {
        if (inputLabel != null && 0 < inputLabel.getString().length()) {
            return inputLabel.getString();
        }
        List<DODDLELiteral> literalList = langLabelListMap.get(DODDLEConstants.LANG);
        if (literalList != null && 0 < literalList.size()) {
            return literalList.get(0).getString();
        }
        literalList = langDescriptionListMap.get(DODDLEConstants.LANG);
        if (literalList != null && 0 < literalList.size()) {
            return literalList.get(0).getString();
        }
        for (String lang: langLabelListMap.keySet()) {
            literalList = langLabelListMap.get(lang);
            if (literalList != null && 0 < literalList.size()) {
                return literalList.get(0).getString();
            }
        }
        for (String lang: langDescriptionListMap.keySet()) {
            literalList = langDescriptionListMap.get(lang);
            if (literalList != null && 0 < literalList.size()) {
                return literalList.get(0).getString();
            }
        }
        return uri.getURI();
    }

    public String getNameSpace() {
        return Utils.getNameSpace(uri);
    }
    
    public String getLocalName() {    
        return Utils.getLocalName(uri);
    }
    
    public String getQName() {
        if (DODDLE.getCurrentProject() == null) {
            return getLocalName();
        }
        ReferenceOntologySelectionPanel ontSelectionPanel = DODDLE.getCurrentProject().getOntologySelectionPanel();
        String prefix = ontSelectionPanel.getPrefix(getNameSpace());
        return prefix+":"+getLocalName(); 
    }

    public String toString() {
        return getWord() + " [" + getQName() + "]";
    }

    public boolean equals(Object c) {
        Concept concept = (Concept)c;
        return getURI().equals(concept.getURI());
    }
}
