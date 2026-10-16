package edu.persianlearningapp.dao;

import edu.persianlearningapp.domain.ComponentContent;

/** UC-03 persistence contract, separate from UC-02 structure persistence. */
public interface ComponentContentDAO {
    ComponentContent findContent(int chapterId, int componentId);
    boolean saveContent(int chapterId, int componentId, String persian, String urdu);
    /** Removes the saved text of a component (FR-CS-04); the component itself stays configured. */
    boolean deleteContent(int chapterId, int componentId);
}
