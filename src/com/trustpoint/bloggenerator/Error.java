package com.trustpoint.bloggenerator;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Display an error message
 *
 * @author zli
 *
 */
public class Error extends JFrame
{
    private static final long serialVersionUID = -6524201347289495994L;

    public void initErrorFrame(String errorMessage)
    {
        JFrame errorFrame = new JFrame(Value.ERROR);
        errorFrame.setSize(600, 200);
        errorFrame.setLocationRelativeTo(null);
        errorFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridLayout gridLayout = new GridLayout(3, 1);
        errorFrame.setLayout(gridLayout);

        JLabel lable = new JLabel(errorMessage);
        lable.setHorizontalAlignment(JLabel.CENTER);
        lable.setVerticalAlignment(JLabel.BOTTOM);
        lable.setSize(300, 300);

        errorFrame.add(lable);
        errorFrame.setVisible(true);
    }
}
