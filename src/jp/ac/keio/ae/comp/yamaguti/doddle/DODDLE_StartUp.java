package jp.ac.keio.ae.comp.yamaguti.doddle;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

/**
 * @author takeshi morita
 */
public class DODDLE_StartUp extends JDialog implements ActionListener {

    private JRadioButton onMemoryButton;
    private JRadioButton berkeleyDBButton;

    private JLabel minMemoryLabel;
    private JLabel maxMemoryLabel;
    private JTextField minMemoryField;
    private JTextField maxMemoryField;

    private JButton okButton;
    private JButton cancelButton;

    public DODDLE_StartUp() {
        onMemoryButton = new JRadioButton("オンメモリ");
        onMemoryButton.addActionListener(this);
        berkeleyDBButton = new JRadioButton("Berkeley DB");
        berkeleyDBButton.setSelected(true);
        berkeleyDBButton.addActionListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(onMemoryButton);
        group.add(berkeleyDBButton);

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new GridLayout(2, 1));
        radioButtonPanel.add(onMemoryButton);
        radioButtonPanel.add(berkeleyDBButton);

        minMemoryLabel = new JLabel("最小メモリ");
        minMemoryField = new JTextField("256m");
        maxMemoryLabel = new JLabel("最大メモリ");
        maxMemoryField = new JTextField("256m");

        JPanel memoryPanel = new JPanel();
        memoryPanel.setLayout(new GridLayout(2, 2));
        memoryPanel.add(minMemoryLabel);
        memoryPanel.add(minMemoryField);
        memoryPanel.add(maxMemoryLabel);
        memoryPanel.add(maxMemoryField);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        centerPanel.add(radioButtonPanel);
        centerPanel.add(memoryPanel);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        setTitle("起動オプション");
        setSize(200, 180);
        setLocation(200, 200);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            Runtime runtime = Runtime.getRuntime();
            try {
                if (onMemoryButton.isSelected()) {
                    System.out.println("on memory");
                    runtime.exec("java.exe -Xmx" + maxMemoryField.getText() + " -Xms" + minMemoryField.getText()
                            + " -cp DODDLE-J.jar jp.ac.keio.ae.comp.yamaguti.doddle_j.DODDLE_J");
                } else if (berkeleyDBButton.isSelected()) {
                    System.out.println("berkeleydb");
                    runtime.exec("java.exe -Xmx" + maxMemoryField.getText() + " -Xms" + minMemoryField.getText()
                            + " -cp DODDLE-J.jar  jp.ac.keio.ae.comp.yamaguti.doddle_j.DODDLE_J --DB");
                    System.out.println("done");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            System.exit(0);
        } else if (e.getSource() == cancelButton) {
            System.exit(0);
        } else if (e.getSource() == onMemoryButton) {
            minMemoryField.setText("512m");
            maxMemoryField.setText("512m");
        } else if (e.getSource() == berkeleyDBButton) {
            minMemoryField.setText("256m");
            maxMemoryField.setText("256m");
        }
    }

    public static void main(String[] args) {
        try {
            ToolTipManager.sharedInstance().setEnabled(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new DODDLE_StartUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
