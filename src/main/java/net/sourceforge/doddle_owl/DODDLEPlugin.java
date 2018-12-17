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

package net.sourceforge.doddle_owl;

import net.sourceforge.doddle_owl.data.DODDLEConstants;
import net.sourceforge.doddle_owl.ui.SplashWindow;
import net.sourceforge.doddle_owl.utils.Translator;
import net.sourceforge.mr3.plugin.MR3Plugin;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.Set;

/**
 * @author Takeshi Morita
 *
 */
public class DODDLEPlugin extends MR3Plugin {

    public void replaceRDFSModel(Model model) {
        // TODO MR3プラグインのJena APIを更新する
        //mergeRDFSModel(model);
    }

    /**
     * DODDLE-OWLをMR3のプラグインとして起動
     */
    public void exec() {
        DODDLE_OWL.doddlePlugin = this;
        new Thread() {
            public void run() {
                SplashWindow splashWindow = new SplashWindow(null);
                try {
                    DODDLE_OWL.initOptions(new String[] {});
                    Translator.loadDODDLEComponentOntology(DODDLEConstants.LANG);
                    new DODDLE_OWL();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    splashWindow.setVisible(false);
                }
            }
        }.start();
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
        // MR3プラグインのJena APIを更新する
        //return getRDFSModel();
        return ModelFactory.createDefaultModel();
    }
}