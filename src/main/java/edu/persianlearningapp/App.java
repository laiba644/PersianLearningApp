package edu.persianlearningapp;

import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.business.StructureBO;
import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.dao.mysql.MySQLDAOFactory;
import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.presentation.MainFrame;

import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class App {

    public static void main(String[] args) {
        IDALFacade dal = new MySQLDAOFactory().createDALFacade();
        LearningEventSource events = new LearningEventSource();

        ChapterBO chapterBO = new ChapterBO(dal, events);
        StructureBO structureBO = new StructureBO(dal);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FontUIResource font =
                        new FontUIResource("Arial", Font.PLAIN, 26);

                UIManager.put("OptionPane.messageFont", font);
                UIManager.put("OptionPane.buttonFont", font);
                UIManager.put("TextField.font", font);
                UIManager.put("OptionPane.okButtonText", "ٹھیک ہے");

                MainFrame frame =
                        new MainFrame(chapterBO, structureBO, events);
                frame.setVisible(true);
            }
        });
    }
}