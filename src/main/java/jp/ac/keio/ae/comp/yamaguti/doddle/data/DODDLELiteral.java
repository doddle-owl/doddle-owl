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

import java.io.*;

/**
 * @author takeshi morita
 */
public class DODDLELiteral implements Serializable {

    private String lang;
    private String string;

    public DODDLELiteral(String l, String str) {
        lang = l;
        string = str;
    }

    public boolean equals(Object obj) {
        DODDLELiteral literal = (DODDLELiteral) obj;
        return literal.getString().equals(string) && literal.getLang().equals(lang);
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getString() {
        return string;
    }

    public void setString(String text) {
        this.string = text;
    }

    public String getFormattedText(String text) {
        String[] words = text.split(" ");
        StringBuilder newText = new StringBuilder();
        newText.append("<html><body>");
        int size = 0;
        for (String word : words) {
            newText.append(word);
            newText.append(" ");
            size += word.length();
            if (30 < size) {
                newText.append("<br>");
                size = 0;
            }
        }
        newText.append("</body></html>");
        return newText.toString();
    }

    @Override
    public String toString() {
        return getFormattedText(string);
    }
}
