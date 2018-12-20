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

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

/**
 * @author Takeshi Morita
 */
public class SwoogleOWLMetaData {

    private String url;
    private String fileEncoding;
    private String rdfType;
    private String fileType;
    private double ontoRank;

    public SwoogleOWLMetaData(Resource u, Literal fe, Literal ft, Resource rt, Literal rank) {
        url = u.getURI();
        fileEncoding = fe.getString();
        fileType = ft.getString();
        rdfType = rt.getURI();
        ontoRank = rank.getDouble();
    }

    public double getOntoRank() {
        return ontoRank;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public String getFileType() {
        return fileType;
    }

    public String getRdfType() {
        return rdfType;
    }

    public String getURL() {
        return url;
    }

    public String toString() {
        return url + ", " + rdfType + "," + fileEncoding;
    }
}
