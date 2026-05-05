/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: https://doddle-owl.github.io/
 *
 * Copyright (C) 2004-2026 Takeshi Morita. All rights reserved.
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

package io.github.doddle_owl.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 2005-04-21
 * EDRアクセント記号をUTF-8のアクセント記号に変換して出力するスクリプト
 * 2005-05-27: Ï, Ë, ï, ë, î, Ã, ã, Ñ, ñ, Õ, õ, Í, í, Û,û, Á, á,Ó ,ó
 * （その他たくさん）を追加．
 * ^は正規表現で使われる記号のため\\でエスケープした
 * @<mと@<と@lは不明のため，@を＠で置換しておく
 *
 * @author Takeshi Morita
 */
class AccentSymbolConverter {

    private static final Map<String, String> accentSymbolTable;

    static {
        accentSymbolTable = new HashMap<>();
        accentSymbolTable.put("@~A", "Ā");
        accentSymbolTable.put("@~a", "ā");
        accentSymbolTable.put("@~E", "Ē");
        accentSymbolTable.put("@~e", "ē");
        accentSymbolTable.put("@~I", "Ī");
        accentSymbolTable.put("@~i", "ī");
        accentSymbolTable.put("@~O", "Ō");
        accentSymbolTable.put("@~o", "ō");
        accentSymbolTable.put("@~U", "Ū");
        accentSymbolTable.put("@~u", "ū");
        accentSymbolTable.put("@<A", "Á");
        accentSymbolTable.put("@<a", "á");
        accentSymbolTable.put("@<E", "É");
        accentSymbolTable.put("@<e", "é");
        accentSymbolTable.put("@<I", "Í");
        accentSymbolTable.put("@<i", "í");
        accentSymbolTable.put("@<O", "Ó");
        accentSymbolTable.put("@<o", "ó");
        accentSymbolTable.put("@<U", "Ú");
        accentSymbolTable.put("@<u", "ú");
        accentSymbolTable.put("@>A", "À");
        accentSymbolTable.put("@>E", "È");
        accentSymbolTable.put("@>a", "à");
        accentSymbolTable.put("@>e", "è");
        accentSymbolTable.put("@>u", "ù");
        accentSymbolTable.put("@\\^A", "Â");
        accentSymbolTable.put("@\\^E", "Ê");
        accentSymbolTable.put("@\\^I", "Î");
        accentSymbolTable.put("@\\^U", "Û");
        accentSymbolTable.put("@\\^a", "â");
        accentSymbolTable.put("@\\^e", "ê");
        accentSymbolTable.put("@\\^i", "î");
        accentSymbolTable.put("@\\^o", "ô");
        accentSymbolTable.put("@\\^u", "û");
        accentSymbolTable.put("@@A", "Ã");
        accentSymbolTable.put("@@N", "Ñ");
        accentSymbolTable.put("@@O", "Õ");
        accentSymbolTable.put("@@a", "ã");
        accentSymbolTable.put("@@n", "ñ");
        accentSymbolTable.put("@@o", "õ");
        accentSymbolTable.put("@:A", "Ä");
        accentSymbolTable.put("@:O", "Ö");
        accentSymbolTable.put("@:U", "Ü");
        accentSymbolTable.put("@:I", "Ï");
        accentSymbolTable.put("@:E", "Ë");
        accentSymbolTable.put("@:a", "ä");
        accentSymbolTable.put("@:o", "ö");
        accentSymbolTable.put("@:u", "ü");
        accentSymbolTable.put("@:i", "ï");
        accentSymbolTable.put("@:e", "ë");
        accentSymbolTable.put("@&C", "Ç");
        accentSymbolTable.put("@&c", "ç");
    }

    public static String convertAccentSymbol(String str) {
        str = str.replaceAll("＾", "^"); // EDRのバグだと思われる
        str = str.replaceAll("@@<", "@<"); // EDRのバグだと思われる
        str = str.replaceAll("@@\\^", "@^"); // EDRのバグだと思われる
        for (String key : accentSymbolTable.keySet()) {
            String value = accentSymbolTable.get(key);
            str = str.replaceAll(key, value);
        }
        str = str.replaceAll("@", "＠");
        return str;
    }

    public static void main(String[] args) {
        // String test = "test @~u";
        // test = test.replaceAll("@~u", "ù");
        // System.out.println(test);
        // System.exit(0);
        BufferedReader br = null;
        try {
            Reader reader = new InputStreamReader(new FileInputStream(args[0]), "SJIS");
            br = new BufferedReader(reader);
            Writer writer = new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_16);
            BufferedWriter bw = new BufferedWriter(writer);

            while (br.ready()) {
                String line = br.readLine();
                line = AccentSymbolConverter.convertAccentSymbol(line);
                bw.write(line);
                bw.newLine();
            }
            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
            }
        }
    }
}
