/*
 * @(#)  2005/09/08
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class SplashWindow extends JWindow {

    public SplashWindow() {
        ImageIcon logo = Utils.getImageIcon("doddle_splash.png");
        JLabel logoLabel = new JLabel(logo);
        getContentPane().add(logoLabel, BorderLayout.CENTER);
        JLabel versionInfoLabel = new JLabel("ÉoÅ[ÉWÉáÉì:" + DODDLE.VERSION);
        versionInfoLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        versionInfoLabel.setFont(versionInfoLabel.getFont().deriveFont(Font.PLAIN, 18));
        getContentPane().add(versionInfoLabel, BorderLayout.SOUTH);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }
    public static void main(String[] args) {
        new SplashWindow();
    }
}
