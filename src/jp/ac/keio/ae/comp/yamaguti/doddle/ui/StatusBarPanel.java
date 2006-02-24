package jp.ac.keio.ae.comp.yamaguti.doddle.ui;

/*
 * @(#)  ProgressDialog 2004/10/9
 *
 */

import java.awt.*;
import java.util.*;

import javax.swing.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;

import org.apache.log4j.*;

/**
 * @author takeshi morita
 */
public class StatusBarPanel extends Panel {

    private boolean isLock;

    private long startTime;
    private int progressTime;

    private int maxValue;
    private int currentValue;

    private JTextField statusField;
    private JProgressBar progressBar;
    private static final Color STATUS_BAR_COLOR = new Color(240, 240, 240);

    private Thread timer;

    public StatusBarPanel() {
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        progressBar.setStringPainted(false);
        progressBar.setVisible(false);
        statusField = new JTextField();
        statusField.setBackground(STATUS_BAR_COLOR);
        statusField.setEditable(false);
        setLayout(new BorderLayout());
        add(statusField, BorderLayout.CENTER);
        add(progressBar, BorderLayout.EAST);
    }

    private String lastMessage;

    public void setLastMessage(String msg) {
        lastMessage = msg;
    }

    private void startTimer() {
        timer = new Thread() {
            public void run() {
                while (progressBar.isVisible()) {
                    try {
                        sleep(1000);
                        setCurrentTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DODDLE.getLogger().log(Level.DEBUG, "çáåvéûä‘: " + new Integer(progressTime).toString());
                setValue(lastMessage);
            }
        };
        timer.start();
    }

    public void setCurrentTime() {
        progressTime = (int) (Calendar.getInstance().getTimeInMillis() - startTime) / 1000;
        statusField.setText("åoâﬂéûä‘: " + new Integer(progressTime).toString());
    }

    public void lock() {
        isLock = true;
    }

    public void unLock() {
        isLock = false;
    }

    public void startTime() {
        if (!isLock) {
            startTime = Calendar.getInstance().getTimeInMillis();
            setCurrentTime();
        }
    }

    public double getProgressTime() {
        return progressTime;
    }

    public void initNormal(int max) {
        if (!isLock) {
            maxValue = max;
            progressBar.setIndeterminate(false);
            progressBar.setMinimum(0);
            progressBar.setMaximum(maxValue);
            progressBar.setValue(0);
            currentValue = 0;
            progressBar.setVisible(true);
            startTimer();
        }
    }

    public void initIndeterminate() {
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
    }

    public void addValue(String message) {
        currentValue++;
        if (maxValue < 10) {
            setValue(message);
        } else if (currentValue % (maxValue / 10) == 0) {
            setValue(message);
        }
    }

    public void addProjectValue() {
        currentValue++;
        if (maxValue < 20) {
            setValue();
        } else if (currentValue % (maxValue / 10) == 0) {
            setValue();
        }
    }

    public void addValue() {
        if (!isLock) {
            addProjectValue();
        }
    }

    public void setValue(String message) {
        statusField.setText(message);
        progressBar.setValue(currentValue);
        progressBar.paintImmediately(progressBar.getVisibleRect());
    }

    public void setValue() {
        progressBar.setValue(currentValue);
        progressBar.paintImmediately(progressBar.getVisibleRect());
    }

    public int getValue() {
        return progressBar.getValue();
    }

    public void setMaximum(int max) {
        progressBar.setMaximum(max);
    }

    public void setMinimum(int min) {
        progressBar.setMinimum(min);
    }

    public void hideProgressBar() {
        if (!isLock) {
            progressBar.setVisible(false);
            timer = null;
        }
    }

    public void setText(String text) {
        statusField.setText(text);
    }

    public String getText() {
        return statusField.getText();
    }
}
