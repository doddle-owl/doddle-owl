/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 *
 * Copyright (C) 2004-2018 Yamaguchi Laboratory, Keio University. All rights reserved.
 *
 * This file is part of DODDLE-OWL.
 *
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.doddle_owl.models;

import org.doddle_owl.DODDLEProject;
import org.doddle_owl.views.OptionDialog;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mioki
 * @author Takeshi Morita
 *
 */
public class ConceptTreeNode extends DefaultMutableTreeNode implements Comparable {

    private String uri;
    private boolean isInputConcept;
    private boolean isUserConcept;
    private boolean isMultipleInheritance;

    private List<List<Concept>> trimmedConceptList;

    private DODDLEProject project;

    public ConceptTreeNode(Concept c, DODDLEProject p) {
        super();
        project = p;
        uri = c.getURI();
        if (project.getConcept(uri) == null) {
            project.putConcept(uri, c);
        }
        initTrimmedConceptList();
    }

    public void initTrimmedConceptList() {
        trimmedConceptList = new ArrayList<List<Concept>>();
        trimmedConceptList.add(new ArrayList<Concept>());
    }

    /**
     * コピー用のコンストラクタ このコンストラクタ内では，初期化処理はしないこと
     */
    public ConceptTreeNode(ConceptTreeNode ctn, DODDLEProject p) {
        super();
        project = p;
        Concept c = ctn.getConcept();
        uri = c.getURI();
        if (project.getConcept(uri) == null) {
            project.putConcept(uri, c);
        }
        isInputConcept = ctn.isInputConcept();
        isUserConcept = ctn.isUserConcept();
        isMultipleInheritance = ctn.isMultipleInheritance();
        trimmedConceptList = ctn.getTrimmedConceptList();
    }

    public void addTrimmedConcept(Concept c) {
        trimmedConceptList.get(0).add(c);
    }

    public void addAllTrimmedConcept(List<Concept> list) {
        trimmedConceptList.get(0).addAll(list);
    }

    public void addTrimmedConceptList(List<Concept> list) {
        trimmedConceptList.add(list);
    }

    public void setTrimmedConceptList(List<List<Concept>> list) {
        trimmedConceptList = list;
    }

    public List<List<Concept>> getTrimmedConceptList() {
        return trimmedConceptList;
    }

    public Concept getConcept() {
        return project.getConcept(uri);
    }

    public void setConcept(Concept c) {
        project.putConcept(c.getURI(), c);
    }

    public void removeRelation() {
        this.removeAllChildren();
        this.removeFromParent();
    }

    public Map<String, List<DODDLELiteral>> getLangLabelLiteralListMap() {
        return getConcept().getLangLabelListMap();
    }

    public Map<String, List<DODDLELiteral>> getLangDescriptionLiteralListMap() {
        return getConcept().getLangDescriptionListMap();
    }

    public List<Integer> getTrimmedCountList() {
        List<Integer> trimmedCountList = new ArrayList<Integer>();
        for (List<Concept> tcList : trimmedConceptList) {
            trimmedCountList.add(tcList.size());
        }
        return trimmedCountList;
    }

    public String getURI() {
        return getConcept().getURI();
    }

    public String getInputWord() {
        if (getConcept().getInputLabel() != null) { return getConcept().getInputLabel().getString(); }
        return "";
    }

    public void setIsInputConcept(boolean t) {
        isInputConcept = t;
    }

    public boolean isInputConcept() {
        return isInputConcept;
    }

    public boolean isSINNode() {
        return !(isInputConcept || isUserConcept);
    }

    public void setIsMultipleInheritance(boolean t) {
        isMultipleInheritance = t;
    }

    public boolean isMultipleInheritance() {
        return isMultipleInheritance;
    }

    public boolean isUserConcept() {
        return isUserConcept;
    }

    public void setIsUserConcept(boolean t) {
        isUserConcept = t;
    }

    public String toString() {
        if (OptionDialog.isShowQName()) { return getConcept().toString(); }
        if (getConcept() == null) { return ""; }
        return getConcept().getWord();
    }

    public int compareTo(Object o) {
        ConceptTreeNode node = (ConceptTreeNode) o;
        return getConcept().getWord().compareTo(node.getConcept().getWord());
    }
}