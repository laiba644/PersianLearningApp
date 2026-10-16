package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.observer.LearningEventSource;
import java.util.List;

/** UC-01 BO skeleton. Laiba implements validation and use-case rules. */
public class ChapterBO {
    private final IDALFacade dal;
    private final LearningEventSource events;
    public ChapterBO(IDALFacade dal, LearningEventSource events) { this.dal = dal; this.events = events; }
    public int createChapter(String title) { throw new UnsupportedOperationException("TODO UC-01"); }
    public List<Chapter> listChapters() { throw new UnsupportedOperationException("TODO UC-01"); }
    public Chapter getChapter(int id) { throw new UnsupportedOperationException("TODO UC-01"); }
    public void renameChapter(int id, String title) { throw new UnsupportedOperationException("TODO UC-01"); }
    public void deleteChapter(int id, boolean confirmed) { throw new UnsupportedOperationException("TODO UC-01"); }
}
