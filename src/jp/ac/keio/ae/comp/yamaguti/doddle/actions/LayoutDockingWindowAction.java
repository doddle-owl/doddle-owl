/*
 * @(#)  2007/02/17
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.actions;

import java.awt.event.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

/**
 * @author takeshi morita
 */
public class LayoutDockingWindowAction extends AbstractAction{

    private String layoutType;
    public static String XGA_LAYOUT = "XGA_LAYOUT";
    public static String UXGA_LAYOUT = "UXGA_LAYOUT";
        
    public LayoutDockingWindowAction(String type, String title) {
        super(title);
        layoutType = type;
    }
    
    public void actionPerformed(ActionEvent e) {
        DODDLEProject currentProject = DODDLE.getCurrentProject();
        if (currentProject == null) {
            return;
        }
        if (layoutType.equals(XGA_LAYOUT)) {
            currentProject.setXGALayoutForAll();
        } else if (layoutType.equals(UXGA_LAYOUT)) {
            currentProject.setUXGALayoutForAll();
        }
    }

}
