package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.ComponentDAO;
import edu.persianlearningapp.domain.ChapterComponent;
import java.util.List;

/** UC-02 JDBC skeleton. Zohaib writes SQL and transactional ordering. */
public class MySQLComponentDAO implements ComponentDAO {
    public List<ChapterComponent> findComponentsByChapterId(int chapterId) { throw new UnsupportedOperationException("TODO UC-02 DAO"); }
    public void replaceStructure(int chapterId, List<ChapterComponent> components) { throw new UnsupportedOperationException("TODO UC-02 DAO"); }
}
