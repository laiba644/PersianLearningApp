package edu.persianlearningapp.dao;

import edu.persianlearningapp.domain.Chapter;
import java.util.List;

public interface ChapterDAO {
    int insert(Chapter chapter);
    List<Chapter> findAll();
    Chapter findById(int chapterId);
    boolean updateTitle(int chapterId, String title);
    boolean deleteById(int chapterId);
    boolean existsByTitleExceptId(String title, int excludedChapterId);
}
