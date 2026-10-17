package edu.persianlearningapp.presentation;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.observer.LearningEventSource;

import javax.swing.JFrame;
import java.awt.ComponentOrientation;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainFrame(ChapterBO chapterBO, LearningEventSource events) {
        super("فارسی سیکھنے کی ایپ");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new ChapterManagementPanel(chapterBO, events)) ;
        setFontRecursively(getContentPane(), new Font("Arial", Font.PLAIN, 30));
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    private void setFontRecursively(Component component, Font font) {
        component.setFont(font);

        if (component instanceof Container) {
            Component[] children = ((Container) component).getComponents();

            for (Component child : children) {
                setFontRecursively(child, font);
            }
        }
    }
}