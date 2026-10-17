package edu.persianlearningapp;

import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.dao.mysql.MySQLDAOFactory;
import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.presentation.MainFrame;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        IDALFacade dal = new MySQLDAOFactory().createDALFacade();
        LearningEventSource events = new LearningEventSource();
        ChapterBO chapterBO = new ChapterBO(dal, events);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame(chapterBO, events);
                frame.setVisible(true);
            }
        });
    }
}