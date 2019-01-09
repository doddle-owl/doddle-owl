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

import org.doddle_owl.views.DODDLEProjectPanel;
import org.doddle_owl.actions.LoadOntologyAction;
import org.doddle_owl.actions.SaveOntologyAction;
import org.doddle_owl.models.Concept;
import org.doddle_owl.views.ConceptTreePanel;
import org.doddle_owl.views.ConstructClassPanel;
import org.doddle_owl.views.ConstructPropertyPanel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Takeshi Morita
 */
public class UndoManager {
    
    private int index;    
    private Command lastCommand;
    private List<Command> commandList;
    private LoadOntologyAction loadOntologyAction;
    private SaveOntologyAction saveOntologyAction;
    private DODDLEProjectPanel project;
    
    private static int MAX_COMMAND_CNT = 50;
    
    public UndoManager(DODDLEProjectPanel project) {
        index = -1;
        commandList = new ArrayList<>();
        loadOntologyAction = new LoadOntologyAction(Translator.getTerm("OpenOWLOntologyAction"),
                LoadOntologyAction.OWL_ONTOLOGY);
        saveOntologyAction = new SaveOntologyAction(Translator.getTerm("SaveOWLOntologyAction"),
                SaveOntologyAction.OWL_ONTOLOGY);
        this.project = project;        
    }
    
    public void initUndoManager() {
        index = -1;
        lastCommand = null;
        commandList.clear();
    }
        
    public void addCommand(Concept parentConcept, Concept targetConcept, String treeType) {
        ++index;
        commandList.add(index, new Command(parentConcept, targetConcept, treeType));
        if (commandList.size() == MAX_COMMAND_CNT+1) {
            commandList.remove(0);
            index = MAX_COMMAND_CNT-1;
        } else if (index+1 < commandList.size()){ // 新たなCommandを追加したら，redoはできなくなる
            for (int i = index+1; i < commandList.size(); i++) {
                commandList.remove(i);
            }
        }
    }

    public void loadFiles() {
        loadOntologyAction.loadOWLOntology(project, lastCommand.getOntFile());
        ConstructClassPanel classPanel = project.getConstructClassPanel();
        ConstructPropertyPanel propertyPanel = project.getConstructPropertyPanel();
        classPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(lastCommand.getClassTRAFile());
        propertyPanel.getConceptDriftManagementPanel().loadTrimmedResultAnalysis(lastCommand.getPropertyTRAFile());
        if (lastCommand.getTargetConcept() != null) {
            switch (lastCommand.getTreeType()) {
                case ConceptTreePanel.CLASS_ISA_TREE:
                    classPanel.selectIsaTreeNode(lastCommand.getTargetConcept(), lastCommand.getParentConcept());
                    break;
                case ConceptTreePanel.CLASS_HASA_TREE:
                    classPanel.selectHasaTreeNode(lastCommand.getTargetConcept(), lastCommand.getParentConcept());
                    break;
                case ConceptTreePanel.PROPERTY_ISA_TREE:
                    propertyPanel.selectIsaTreeNode(lastCommand.getTargetConcept(), lastCommand.getParentConcept());
                    break;
                case ConceptTreePanel.PROPERTY_HASA_TREE:
                    propertyPanel.selectHasaTreeNode(lastCommand.getTargetConcept(), lastCommand.getParentConcept());
                    break;
            }
        }
    }
    
    public void undo() {
        if (canUndo()) {
            if (lastCommand != null && lastCommand.equals(commandList.get(index)) && -1 <= index - 2) {
                lastCommand = commandList.get(index-1);
                index -=2;
            } else {
                lastCommand = commandList.get(index--);
            }
            loadFiles();
        }
    }
    
    public void redo() {
        if (canRedo()) {
            if (lastCommand != null && lastCommand.equals(commandList.get(index+1)) && index+2 < commandList.size()) {
                index += 2;
                lastCommand = commandList.get(index);  
            } else {
                lastCommand = commandList.get(++index);  
            }            
            loadFiles();
        }
    }
    
    public boolean canUndo() {
        return 0 <= index;
    }
    
    public boolean canRedo() {
        return index+1 < commandList.size();
    }
    
    class Command {
        private String treeType;
        private Concept parentConcept;
        private Concept targetConcept;
        private File ontFile;
        private File classTRAFile;
        private File propertyTRAFile;
        
        public String getTreeType() {
            return treeType;
        }

        public File getClassTRAFile() {
            return classTRAFile;
        }
        
        public File getPropertyTRAFile() {
            return propertyTRAFile;
        }
        
        public File getOntFile() {
            return ontFile;
        }

        public Concept getParentConcept() {
            return parentConcept;
        }

        public Concept getTargetConcept() {
            return targetConcept;
        }

        public Command(Concept p, Concept t, String type) {
            treeType = type;
            parentConcept = p;
            targetConcept = t;
            try { 
                ontFile = File.createTempFile("doddle_history_ont", null);
                ontFile.deleteOnExit();
                classTRAFile = File.createTempFile("doddle_history_class_tra", null);
                classTRAFile.deleteOnExit();
                propertyTRAFile = File.createTempFile("doddle_history_property_tra", null);
                propertyTRAFile.deleteOnExit();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            saveOntologyAction.saveOWLOntology(project, ontFile);
            project.getConstructClassPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(classTRAFile);
            project.getConstructPropertyPanel().getConceptDriftManagementPanel().saveTrimmedResultAnalysis(propertyTRAFile);
        }
    }
}
