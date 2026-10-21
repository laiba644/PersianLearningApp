package edu.persianlearningapp.business;
import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.ChapterComponent;
import edu.persianlearningapp.domain.ComponentContent;
import edu.persianlearningapp.domain.GlossaryEntry;
import java.util.Collections;
import java.util.List;
public class StubDALFacade implements IDALFacade {
    // UC-01
    public int insert(Chapter chapter) { return 1; }
    public List<Chapter> findAll() { return Collections.<Chapter>emptyList(); }
    public Chapter findById(int chapterId) { return null; }
    public boolean updateTitle(int chapterId, String title) { return false; }
    public boolean deleteById(int chapterId) { return false; }
    @Override
    public boolean existsByTitleExceptId(
            String title, int excludedChapterId) {
        return false;
    }
    // UC-02
    public List<ChapterComponent> findComponentsByChapterId(int chapterId) { return Collections.<ChapterComponent>emptyList(); }
    public void replaceStructure(int chapterId, List<ChapterComponent> components) { }
    // UC-03
    public ComponentContent findContent(int chapterId, int componentId) { return null; }
    public boolean saveContent(int chapterId, int componentId, String persian, String urdu) { return false; }
    public boolean deleteContent(int chapterId, int componentId) { return false; }
    // UC-04
    public List<GlossaryEntry> findGlossaryByChapterId(int chapterId) { return Collections.<GlossaryEntry>emptyList(); }
    public int insert(GlossaryEntry entry) { return 1; }
    public boolean update(GlossaryEntry entry) { return false; }
    public boolean delete(int chapterId, int entryId) { return false; }
}
