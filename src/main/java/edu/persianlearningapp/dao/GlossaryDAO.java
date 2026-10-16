package edu.persianlearningapp.dao;

import edu.persianlearningapp.domain.GlossaryEntry;
import java.util.List;

public interface GlossaryDAO {
    List<GlossaryEntry> findGlossaryByChapterId(int chapterId);
    int insert(GlossaryEntry entry);
    boolean update(GlossaryEntry entry);
    boolean delete(int chapterId, int entryId);
}
