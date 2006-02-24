/*
 * @(#)  2005/09/15
 *
 *
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class ConceptTreeCellRenderer extends DefaultTreeCellRenderer {

    private ImageIcon SIN_ICON;
    private ImageIcon MULTIPLE_SIN_ICON;
    private ImageIcon BEST_MATCH_ICON;
    private ImageIcon MULTIPLE_BEST_MATCH_ICON;

    public final ImageIcon PROP_SIN_ICON = Utils.getImageIcon("property_sin_icon.png");
    public final ImageIcon PROP_BEST_MATCH_ICON = Utils.getImageIcon("property_best_match_icon.png");
    public final ImageIcon PROP_MULTIPLE_SIN_ICON = Utils.getImageIcon("property_multiple_sin_icon.png");
    public final ImageIcon PROP_MULTIPLE_BEST_MATCH_ICON = Utils.getImageIcon("property_multiple_best_match_icon.png");

    public final ImageIcon CLASS_SIN_ICON = Utils.getImageIcon("class_sin_icon.png");
    public final ImageIcon CLASS_BEST_MATCH_ICON = Utils.getImageIcon("class_best_match_icon.png");
    public final ImageIcon CLASS_MULTIPLE_SIN_ICON = Utils.getImageIcon("class_multiple_sin_icon.png");
    public final ImageIcon CLASS_MULTIPLE_BEST_MATCH_ICON = Utils.getImageIcon("class_multiple_best_match_icon.png");

    public static final String NOUN_CONCEPT_TREE = "Noun Concept Tree";
    public static final String VERB_CONCEPT_TREE = "Verb Concept Tree";

    public ConceptTreeCellRenderer(String type) {
        if (type.equals(NOUN_CONCEPT_TREE)) {
            SIN_ICON = CLASS_SIN_ICON;
            MULTIPLE_SIN_ICON = CLASS_MULTIPLE_SIN_ICON;
            BEST_MATCH_ICON = CLASS_BEST_MATCH_ICON;
            MULTIPLE_BEST_MATCH_ICON = CLASS_MULTIPLE_BEST_MATCH_ICON;
        } else if (type.equals(VERB_CONCEPT_TREE)) {
            SIN_ICON = PROP_SIN_ICON;
            MULTIPLE_SIN_ICON = PROP_MULTIPLE_SIN_ICON;
            BEST_MATCH_ICON = PROP_BEST_MATCH_ICON;
            MULTIPLE_BEST_MATCH_ICON = PROP_MULTIPLE_BEST_MATCH_ICON;
        }
        setOpaque(true);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        setText(value.toString());

        if (selected) {
            setBackground(new Color(0, 0, 128));
            setForeground(Color.white);
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }

        if (value.getClass().equals(ConceptTreeNode.class)) {
            ConceptTreeNode node = (ConceptTreeNode) value;
            if (node.isInputConcept() || node.isUserConcept()) {
                if (node.isMultipleInheritance()) {
                    setIcon(MULTIPLE_BEST_MATCH_ICON);
                } else {
                    setIcon(BEST_MATCH_ICON);
                }
            } else {
                if (node.isMultipleInheritance()) {
                    setIcon(MULTIPLE_SIN_ICON);
                } else {
                    setIcon(SIN_ICON);
                }
            }
        }
        return component;
    }
}
