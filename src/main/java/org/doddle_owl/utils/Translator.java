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

package org.doddle_owl.utils;

import org.doddle_owl.models.DODDLEConstants;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/*
 *
 * @author Takeshi Morita
 *
 */
public class Translator {

    protected static ResourceBundle resourceBundle;
    private static Map<String, String> uriTermMap;
    private static Map<String, String> uriDescriptionMap;

    public static String getString(String sKey) {
        try {
            return resourceBundle.getString(sKey);
        } catch (Exception e) {
            e.printStackTrace();
            return "FAILED";
        }
    }

    private static Set<Locale> systemLocaleSet;

    static {
        systemLocaleSet = new HashSet<>();
        systemLocaleSet.add(Locale.JAPAN);
        systemLocaleSet.add(Locale.ENGLISH);
        systemLocaleSet.add(Locale.CHINA);
    }

    /**
     * デフォルトのロカールの言語ファイルがシステムに内蔵されている場合は， その言語を返し，内蔵されていない場合には，英語の言語を返す.
     */
    public static String getSystemLanguage() {
        if (systemLocaleSet.contains(Locale.getDefault())) {
            return Locale.getDefault().getLanguage();
        }
        return Locale.ENGLISH.getLanguage();
    }

    private static boolean isSystemLanguage(String lang) {
        for (Locale locale : systemLocaleSet) {
            if (locale.getLanguage().equals(lang)) {
                return true;
            }
        }
        return false;
    }

    /**
     * loadDODDLEComponentOntologyと置換
     *
     * @param lang
     */
    public static void loadResourceBundle(String lang) {
        try {
            InputStream ins = null;
            File resFile = new File("./resources/DODDLE_" + lang + ".properties");
            if (resFile.exists()) {
                ins = new FileInputStream(resFile);
            }
            if (ins == null) {
                if (isSystemLanguage(lang)) {
                    ins = Utils.class.getClassLoader().getResourceAsStream("DODDLE_" + lang + ".properties");
                } else {
                    ins = Utils.class.getClassLoader().getResourceAsStream("DODDLE_" + getSystemLanguage() + ".properties");
                }
            }
            resourceBundle = new PropertyResourceBundle(ins);
            ins.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void loadDODDLEComponentOntology(String lang) {
        uriTermMap = new HashMap<>();
        uriDescriptionMap = new HashMap<>();
        try {
            File componentOntFile = new File("./resources/DODDLEComponent.owl");
            Model ontModel = ModelFactory.createDefaultModel();
            if (componentOntFile.exists()) {
                ontModel.read(new FileInputStream(componentOntFile), DODDLEConstants.BASE_URI);
            } else {
                InputStream ins = Utils.class.getClassLoader().getResourceAsStream("DODDLEComponent.owl");
                ontModel.read(ins, DODDLEConstants.BASE_URI);
            }

            for (ResIterator resItor = ontModel.listSubjectsWithProperty(RDF.type, OWL.Class); resItor
                    .hasNext(); ) {
                Resource res = resItor.nextResource();
                for (StmtIterator stmtItor = res.listProperties(RDFS.label); stmtItor.hasNext(); ) {
                    Statement stmt = stmtItor.nextStatement();
                    Literal label = (Literal) stmt.getObject();
                    if (label.getLanguage().equals(lang)) {
                        uriTermMap.put(res.getURI(), label.getString());
                    }
                }
                for (StmtIterator stmtItor = res.listProperties(RDFS.comment); stmtItor.hasNext(); ) {
                    Statement stmt = stmtItor.nextStatement();
                    Literal description = (Literal) stmt.getObject();
                    if (description != null && description.getLanguage().equals(lang)) {
                        uriDescriptionMap.put(res.getURI(), description.getString());
                    }
                }
            }
            ontModel.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String getTerm(String key) {
        return uriTermMap.get(DODDLEConstants.DODDLE_URI + key); // BASE_URIは変化することがあるため，DODDLE_URIを用いる
    }

    public static String getDescription(String key) {
        return uriDescriptionMap.get(DODDLEConstants.DODDLE_URI + key); // BASE_URIは変化することがあるため，DODDLE_URIを用いる
    }

    public static void main(String[] args) {
        // Translator.loadResourceBundle("en");
        // System.out.println(Translator.getString("Lang"));
        Translator.loadDODDLEComponentOntology("ja");
        System.out.println(Translator.getTerm("PropertyTreeConstructionPanel"));
        System.out.println(Translator.getTerm("RangeLabel"));
        System.out.println(Translator.getTerm("RemoveCorrectConceptPairButton"));
    }
}
