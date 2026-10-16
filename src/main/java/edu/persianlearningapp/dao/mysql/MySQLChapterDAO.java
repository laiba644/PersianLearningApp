package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.ChapterDAO;
import edu.persianlearningapp.domain.Chapter;
import java.util.List;

/** UC-01 JDBC skeleton. Laiba writes SQL and database handling. */
public class MySQLChapterDAO implements ChapterDAO {
    public int insert(Chapter chapter) { throw new UnsupportedOperationException("TODO UC-01 DAO"); }
    public List<Chapter> findAll() { throw new UnsupportedOperationException("TODO UC-01 DAO"); }
    public Chapter findById(int chapterId) { throw new UnsupportedOperationException("TODO UC-01 DAO"); }
    public boolean updateTitle(int chapterId, String title) { throw new UnsupportedOperationException("TODO UC-01 DAO"); }
    public boolean deleteById(int chapterId) { throw new UnsupportedOperationException("TODO UC-01 DAO"); }
}
