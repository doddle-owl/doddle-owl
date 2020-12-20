package org.doddle_owl.actions;

import org.doddle_owl.models.common.DODDLEConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ShowManualAction extends AbstractAction {

    public ShowManualAction(String title) {
        super(title);
    }

    public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported()) {
            try {
                switch (DODDLEConstants.LANG) {
                    case "ja":
                        Desktop.getDesktop().browse(new URI("http://docs.doddle-owl.org/ja/latest/"));
                        break;
                    default:
                        Desktop.getDesktop().browse(new URI("http://docs.doddle-owl.org/en/latest/"));

                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }
}
