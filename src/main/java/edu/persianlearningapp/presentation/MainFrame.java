package edu.persianlearningapp.presentation;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Persian Learning App — Design Skeleton");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new JLabel("Team skeleton: connect the approved UC screens here.", SwingConstants.CENTER));
        setSize(720, 420);
        setLocationRelativeTo(null);
    }
}
