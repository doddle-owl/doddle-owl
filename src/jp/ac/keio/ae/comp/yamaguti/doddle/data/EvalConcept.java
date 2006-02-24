package jp.ac.keio.ae.comp.yamaguti.doddle.data;

/*
 * @(#)  2005/05/20
 *
 *
 * Copyright (C) 2003-2005 The MMM Project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

/**
 * @author takeshi morita
 */
public class EvalConcept implements Comparable {

    private Concept concept;
    private int evalValue;

    public EvalConcept(Concept c, int v) {
        concept = c;
        evalValue = v;
    }

    public int getEvalValue() {
        return evalValue;
    }

    public Concept getConcept() {
        return concept;
    }

    public int compareTo(Object o) {
        int ev = ((EvalConcept) o).getEvalValue();
        EvalConcept c = (EvalConcept) o;
        if (evalValue < ev) {
            return 1;
        } else if (evalValue > ev) {
            return -1;
        } else {
            return concept.getIdentity().compareTo(c.getConcept().getIdentity());
        }
    }

    public String toString() {
        if (concept == null) { return "ŠY“–‚È‚µ"; }
        return "[" + evalValue + "]" + "[" + concept.getIdentity() + "]" + "[" + concept.getWord() + "]";
    }
}
