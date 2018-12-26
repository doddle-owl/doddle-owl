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

package org.doddle_owl;

import org.apache.jena.rdf.model.Model;
import org.doddle_owl.models.DODDLEConstants;
import org.doddle_owl.utils.Translator;
import org.doddle_owl.views.SplashWindow;
import org.mrcube.plugin.MR3Plugin;

import java.util.Set;

/**
 * @author Takeshi Morita
 */
public class DODDLEPlugin extends MR3Plugin {

    public void replaceRDFSModel(Model model) {
        mergeRDFSModel(model);
    }

    /**
     * DODDLE-OWLをMR3のプラグインとして起動
     */
    public void exec() {
        DODDLE_OWL.doddlePlugin = this;
        new Thread(() -> {
            SplashWindow splashWindow = new SplashWindow(null);
            try {
                DODDLE_OWL.initOptions(new String[]{});
                Translator.loadDODDLEComponentOntology(DODDLEConstants.LANG);
                new DODDLE_OWL();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                splashWindow.setVisible(false);
            }
        }).start();
    }

    public void selectClasses(Set nodes) {
        this.selectClassNodes(nodes);
    }

    public void selectProperties(Set nodes) {
        this.selectPropertyNodes(nodes);
    }

    public void groupMR3Nodes(Set group) {
        this.groupClassNodes(group);
    }

    public void unGroupMR3Nodes(Set group) {
        this.unGroupClassNodes(group);
    }

    public Model getModel() {
        return getRDFSModel();
    }
}