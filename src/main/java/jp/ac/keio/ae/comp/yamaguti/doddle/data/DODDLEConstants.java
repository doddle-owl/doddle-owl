/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved. 
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

/**
 * @author takeshi morita
 */
public class DODDLEConstants {

    public static int DIVIDER_SIZE = 10;
    public static final String VERSION = "Beta7";
    public static final String LAST_UPDATE = "2009-06-15";

    public static boolean DEBUG = false;
    public static boolean IS_INTEGRATING_SWOOGLE = false;
    public static String LANG = "en"; // DB構築時に必要

    public static final int ONTOLOGY_SELECTION_PANEL = 0;
    public static final int INPUT_DOCUMENT_SELECTION_PANEL = 1;
    public static final int INPUT_WORD_SELECTION_PANEL = 2;
    public static final int DISAMBIGUATION_PANEL = 3;
    public static final int TAXONOMIC_PANEL = 4;
    public static String BASE_URI = "http://www.yamaguti.comp.ae.keio.ac.jp/doddle#";
    public static final String DODDLE_URI = "http://www.yamaguti.comp.ae.keio.ac.jp/doddle#";
    public static String BASE_PREFIX = "keio";
    public static String EDR_URI = "http://www2.nict.go.jp/kk/e416/EDR#";
    public static String OLD_EDR_URI = "http://www2.nict.go.jp/kk/e416/EDR/";
    public static String EDRT_URI = "http://www2.nict.go.jp/kk/e416/EDRT#";
    public static String WN_URI = "http://wordnet.princeton.edu/wn/";
    public static String JPN_WN_URI = "http://nlpwww.nict.go.jp/wn-ja/";

    public static String EDR_HOME = "C:/DODDLE-OWL/EDR_DIC/";
    public static String EDRT_HOME = "C:/DODDLE-OWL/EDRT_DIC/";
    public static String JPNWN_HOME = "C:/DODDLE-OWL/JPNWN_DIC/";
    public static String GOSEN_CONFIGURATION_FILE = "C:/DODDLE-OWL/GoSenDictionary/dictionary.xml";
    public static String PROJECT_HOME = "C:/DODDLE-OWL/DODDLEProject/";
    public static String WORDNET_HOME = "C:/program files/wordnet/2.0/dict/";
}