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

/**
 * @author Takeshi Morita
 */
public class DODDLEConstants {

    public static int DIVIDER_SIZE = 10;
    public static final String VERSION = "1.0RC3";

    public static boolean DEBUG = false;
    public static boolean IS_INTEGRATING_SWOOGLE = true;
    public static String LANG = "en"; // DB構築時に必要

    public static final int ONTOLOGY_SELECTION_PANEL = 0;
    public static final int INPUT_DOCUMENT_SELECTION_PANEL = 1;
    public static final int INPUT_WORD_SELECTION_PANEL = 2;
    public static final int DISAMBIGUATION_PANEL = 3;
    public static final int TAXONOMIC_PANEL = 4;
    public static String BASE_URI = "http://doddle-owl.org#";
    public static final String DODDLE_URI = "http://doddle-owl.org#";
    public static String BASE_PREFIX = "doddle-owl";
    public static String EDR_URI = "http://www2.nict.go.jp/kk/e416/EDR#";
    public static String OLD_EDR_URI = "http://www2.nict.go.jp/kk/e416/EDR/";
    public static String EDRT_URI = "http://www2.nict.go.jp/kk/e416/EDRT#";
    public static String WN_URI = "http://wordnet.princeton.edu/wn/";
    public static String JPN_WN_URI = "http://nlpwww.nict.go.jp/wn-ja/";
    public static String JWO_URI = "http://www.wikipediaontology.org/class/";

    public static String EDR_HOME = "C:/DODDLE-OWL/EDR_DIC/";
    public static String EDRT_HOME = "C:/DODDLE-OWL/EDRT_DIC/";
    public static String ENWN_3_0_HOME = "enwn_dict_3.0/";
    public static String ENWN_3_1_HOME = "enwn_dict_3.1/";
    public static String ENWN_HOME = ENWN_3_1_HOME;
    public static String JPWN_HOME = "jpwn_dict_1.1/";
    public static String PROJECT_HOME = "./";
}