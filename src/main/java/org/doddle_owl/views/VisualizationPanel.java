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

package org.doddle_owl.views;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.doddle_owl.DODDLEProject;
import org.doddle_owl.DODDLE_OWL;
import org.doddle_owl.utils.ConceptTreeMaker;
import org.doddle_owl.utils.JenaModelMaker;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Takeshi Morita
 */
public class VisualizationPanel extends JPanel implements ActionListener {

    private JButton toMR3Button;
    private JButton toDoddleButton;
    private DODDLEProject currentProject;

    public VisualizationPanel(DODDLEProject project) {
        currentProject = project;
        toMR3Button = new JButton("<html><body>DODDLE-OWL to MR<sup>3</sup></body></html>");
        toMR3Button.addActionListener(this);
        toMR3Button.setFont(new Font("Dialog", Font.BOLD, 20));
        toDoddleButton = new JButton("<html><body>MR<sup>3</sup> to DODDLE-OWL</body></html>");
        toDoddleButton.setFont(new Font("Dialog", Font.BOLD, 20));
        toDoddleButton.addActionListener(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1));
        mainPanel.add(toMR3Button);
        mainPanel.add(toDoddleButton);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.NORTH);
    }

    public void toMR3() {
        DODDLE_OWL.STATUS_BAR.setText("Loading DODDLE-OWL to MR^3");
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();
        ConceptDefinitionPanel conceptDefinitionPanel = currentProject.getConceptDefinitionPanel();
        Model ontology = JenaModelMaker.makeClassModel(constructClassPanel.getIsaTreeModelRoot(),
                ModelFactory.createDefaultModel(), ConceptTreePanel.CLASS_ISA_TREE);
        JenaModelMaker.makePropertyModel(constructPropertyPanel.getIsaTreeModelRoot(), ontology,
                ConceptTreePanel.PROPERTY_ISA_TREE);
        conceptDefinitionPanel.addConceptDefinition(ontology);
        DODDLE_OWL.getDODDLEPlugin().replaceRDFSModel(ontology);
        DODDLE_OWL.STATUS_BAR.setText("Loading DODDLE-OWL to MR^3 Done");
    }

    public void toDODDLE() {
        DODDLE_OWL.STATUS_BAR.setText("Loading MR^3 to DODDLE-OWL");
        InputConceptSelectionPanel inputConceptSelectionPanel = currentProject.getInputConceptSelectionPanel();
        ConstructClassPanel constructClassPanel = currentProject.getConstructClassPanel();
        ConstructPropertyPanel constructPropertyPanel = currentProject.getConstructPropertyPanel();

        constructClassPanel.getIsaTree().setSelectionRow(0); // 照合結果分析結果を選択した状態で復元すると例外が発生するため
        constructPropertyPanel.getIsaTree().setSelectionRow(0);// 照合結果分析結果を選択した状態で復元すると例外が発生するため

        constructClassPanel.init();
        constructPropertyPanel.init();
        currentProject.resetURIConceptMap();
        ConceptTreeMaker.getInstance().setInputConceptSet(inputConceptSelectionPanel.getInputConceptSet());

        Model model = DODDLE_OWL.getDODDLEPlugin().getModel();
        currentProject.initUserIDCount();
        TreeNode rootNode = ConceptTreeMaker.getInstance()
                .getConceptTreeRoot(currentProject, model,
                        ResourceFactory.createResource(ConceptTreeMaker.DODDLE_CLASS_ROOT_URI),
                        ConceptTreePanel.CLASS_ISA_TREE);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        treeModel = constructClassPanel.setConceptTreeModel(treeModel);
        constructClassPanel.setVisibleIsaTree(true);
        currentProject.setUserIDCount(currentProject.getUserIDCount() + 1);
        ConceptTreeMaker.getInstance().conceptDriftManagement(treeModel);
        constructClassPanel.setConceptDriftManagementResult();
        treeModel.reload();

        currentProject.setUserIDCount(currentProject.getUserIDCount());
        rootNode = ConceptTreeMaker.getInstance().getPropertyTreeRoot(currentProject, model,
                ResourceFactory.createResource(ConceptTreeMaker.DODDLE_PROPERTY_ROOT_URI),
                ConceptTreePanel.PROPERTY_ISA_TREE);
        treeModel = new DefaultTreeModel(rootNode);
        treeModel = constructPropertyPanel.setConceptTreeModel(treeModel);
        constructPropertyPanel.setVisibleIsaTree(true);
        ConceptTreeMaker.getInstance().conceptDriftManagement(treeModel);
        constructPropertyPanel.setConceptDriftManagementResult();
        treeModel.reload();
        currentProject.setUserIDCount(currentProject.getUserIDCount() + 1);
        DODDLE_OWL.STATUS_BAR.setText("Loading MR^3 to DODDLE-OWL Done");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == toMR3Button) {
            toMR3();
        } else if (e.getSource() == toDoddleButton) {
            toDODDLE();
        }
    }
}
