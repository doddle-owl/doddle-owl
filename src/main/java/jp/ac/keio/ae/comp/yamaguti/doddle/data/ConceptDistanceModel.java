/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2009 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

/**
 * @author takeshi morita
 */
public class ConceptDistanceModel implements Comparable<ConceptDistanceModel> {

    private Concept c1;
    private Concept c2;
    private Concept commonAncestor;
    private List<Integer> commonAncestorDepthList;
    private int c1ToCommonAncestorDistance;
    private int c2ToCommonAncestorDistance;

    public ConceptDistanceModel(Concept c1, Concept c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public int getC1ToCommonAncestorDistance() {
        return c1ToCommonAncestorDistance;
    }

    public int getConceptDistance() {
        return c1ToCommonAncestorDistance + c2ToCommonAncestorDistance;
    }

    public void setC1ToCommonAncestorDistance(int toCommonAncestorDistance) {
        c1ToCommonAncestorDistance = toCommonAncestorDistance;
    }

    public int getC2ToCommonAncestorDistance() {
        return c2ToCommonAncestorDistance;
    }

    public void setC2ToCommonAncestorDistance(int toCommonAncestorDistance) {
        c2ToCommonAncestorDistance = toCommonAncestorDistance;
    }

    public void setCommonAncestor(Concept c) {
        commonAncestor = c;
    }

    public void setCommonAncestorDepth(List<Integer> depthList) {
        commonAncestorDepthList = depthList;
    }

    public Concept getConcept1() {
        return c1;
    }

    public Concept getConcept2() {
        return c2;
    }

    public Concept getCommonAncestor() {
        return commonAncestor;
    }

    public List<Integer> getCommonAncestorDepthList() {
        return commonAncestorDepthList;
    }

    public int getShortestCommonAncestorDepth() {
        int depth = 1000;
        for (int d : commonAncestorDepthList) {
            if (d < depth) {
                depth = d;
            }
        }
        return depth;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("");
        // String firstLabel = commonAncestor.getURI();
        // if (commonAncestor.getLangLabelListMap().get("ja") != null) {
        // for (DODDLELiteral literal :
        // commonAncestor.getLangLabelListMap().get("ja")) {
        // firstLabel = literal.getString();
        // break;
        // }
        // } else if (commonAncestor.getLangDescriptionListMap().get("ja") !=
        // null) {
        // for (DODDLELiteral literal :
        // commonAncestor.getLangDescriptionListMap().get("ja")) {
        // firstLabel = literal.getString();
        // break;
        // }
        // }
        // builder.append(firstLabel);
        // builder.append(",");
        builder.append(c1ToCommonAncestorDistance);
        builder.append(",");
        builder.append(c2ToCommonAncestorDistance);
        builder.append(",");
        // for (int depth : commonAncestorDepthList) {
        // builder.append(depth);
        // builder.append(" ");
        // }
        builder.append(getShortestCommonAncestorDepth());
        builder.append(",");
        builder.append(commonAncestor.getURI());
        builder.append(",");
        builder.append(c1.getURI());
        builder.append(",");
        builder.append(c2.getURI());
        return builder.toString();
    }

    @Override
    public int compareTo(ConceptDistanceModel cdModel) {
        int d1 = this.getConceptDistance();
        int d2 = cdModel.getConceptDistance();
        return d1 - d2;
    }
}
