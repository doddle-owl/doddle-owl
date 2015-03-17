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

package net.sourceforge.doddle_owl.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import net.sourceforge.doddle_owl.utils.*;

/**
 * @author Takeshi Morita
 */
public class OWLMetaDataTablePanel extends JPanel {

    private JTable owlMetaDataTable;

    public OWLMetaDataTablePanel() {
        owlMetaDataTable = new JTable();
        owlMetaDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane owlMetaDataTableScroll = new JScrollPane(owlMetaDataTable);
        owlMetaDataTableScroll.setBorder(BorderFactory.createTitledBorder(Translator.getTerm("OWLMetaDataTable")));

        setLayout(new BorderLayout());
        add(owlMetaDataTableScroll, BorderLayout.CENTER);
    }

    public void setModel(TableModel model) {
        owlMetaDataTable.setModel(model);
    }
}
