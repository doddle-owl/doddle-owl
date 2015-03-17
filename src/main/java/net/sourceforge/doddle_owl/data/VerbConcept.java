/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2015 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

package net.sourceforge.doddle_owl.data;

import java.util.*;

/**
 * @author Takeshi Morita
 */
public class VerbConcept extends Concept {

    private Set<String> domainSet;
    private Set<String> rangeSet;

    public VerbConcept(Concept c) {
        super(c);
        domainSet = new TreeSet<String>();
        rangeSet = new TreeSet<String>();
    }

    public VerbConcept(String uri, String concept) {
        super(uri, concept);
        domainSet = new TreeSet<String>();
        rangeSet = new TreeSet<String>();
    }

    public void addAllDomain(Set<String> set) {
        domainSet.addAll(set);
    }

    public void addAllRange(Set<String> set) {
        rangeSet.addAll(set);
    }

    public void addDomain(String id) {
        domainSet.add(id);
    }

    public Set<String> getDomainSet() {
        return domainSet;
    }

    public Set<String> getRangeSet() {
        return rangeSet;
    }

    public void addRange(String id) {
        rangeSet.add(id);
    }

    public void deleteDomain(String id) {
        // System.out.println("delete domain");
        domainSet.remove(id);
    }

    public void deleteRange(String id) {
        // System.out.println("delete range");
        rangeSet.remove(id);
    }
}
