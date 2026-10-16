package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.GlossaryEntry;
import edu.persianlearningapp.domain.LearningMode;
import java.util.List;

/** UC-04 BO skeleton. Rabia implements glossary validation and study/exam rules. */
public class GlossaryBO {
    private final IDALFacade dal;
    public GlossaryBO(IDALFacade dal) { this.dal = dal; }
    public List<GlossaryEntry> listForAdmin(int chapterId) { throw new UnsupportedOperationException("TODO UC-04"); }
    public List<GlossaryEntry> listForLearner(int chapterId, LearningMode mode) { throw new UnsupportedOperationException("TODO UC-04"); }
    public int addEntry(int chapterId, String persianWord, String urduMeaning) { throw new UnsupportedOperationException("TODO UC-04"); }
    public void editEntry(int chapterId, int entryId, String persianWord, String urduMeaning) { throw new UnsupportedOperationException("TODO UC-04"); }
    public void deleteEntry(int chapterId, int entryId, boolean confirmed) { throw new UnsupportedOperationException("TODO UC-04"); }
}
