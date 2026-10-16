package edu.persianlearningapp.dao;

import edu.persianlearningapp.domain.ChapterComponent;
import java.util.List;

/** UC-02 persistence contract: selected component types and their order. */
public interface ComponentDAO {
    List<ChapterComponent> findComponentsByChapterId(int chapterId);
    void replaceStructure(int chapterId, List<ChapterComponent> components);
}
