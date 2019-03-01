/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.org/
 * 
 * Copyright (C) 2004-2019 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package org.doddle_owl.models.common;

import org.doddle_owl.utils.Translator;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author Takeshi Morita
 */
public class TurtleFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        return f.getName().toLowerCase().endsWith(".ttl");
    }
    @Override
    public String getDescription() {
        return Translator.getTerm("TurtleFileFilter");
    }
}
