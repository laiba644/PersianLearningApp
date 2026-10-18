
package edu.persianlearningapp.presentation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.ComponentOrientation;

import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.business.StructureBO;
import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.business.ContentBO;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame(ChapterBO chapterBO, StructureBO structurebo,ContentBO contentBO, LearningEventSource events) {
        super("فارسی سیکھنے کی ایپ");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main navigation tabs
        JTabbedPane tabs = new JTabbedPane();

        // Existing chapter management screen
        ChapterManagementPanel chapterPanel =
                new ChapterManagementPanel(chapterBO, events);

        tabs.addTab("ابواب کا انتظام", chapterPanel);

        // Chapter components management screen
//        StructureBO structureBO = new StructureBO();

        StructureManagementPanel structurePanel =
                new StructureManagementPanel(structurebo, chapterBO);

        tabs.addTab("اجزاء کا انتظام", structurePanel);
        
     //UC3
        ContentManagementPanel contentPanel =
                new ContentManagementPanel(contentBO, chapterBO);

        tabs.addTab("تعلیمی مواد کا انتظام", contentPanel);

        setContentPane(tabs);

        setFontRecursively(
                getContentPane(),
                new Font("Arial", Font.PLAIN, 30)
        );

        applyComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT
        );

        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void setFontRecursively(Component component, Font font) {

        component.setFont(font);

        if (component instanceof Container) {
            Component[] children =
                    ((Container) component).getComponents();

            for (Component child : children) {
                setFontRecursively(child, font);
            }
        }
    }
}
