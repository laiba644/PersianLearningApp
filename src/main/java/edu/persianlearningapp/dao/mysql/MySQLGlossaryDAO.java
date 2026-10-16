package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.GlossaryDAO;
import edu.persianlearningapp.domain.GlossaryEntry;
import java.util.List;

/** UC-04 JDBC skeleton. Rabia writes SQL and database handling. */
public class MySQLGlossaryDAO implements GlossaryDAO {
    public List<GlossaryEntry> findGlossaryByChapterId(int chapterId) { throw new UnsupportedOperationException("TODO UC-04 DAO"); }
    public int insert(GlossaryEntry entry) { throw new UnsupportedOperationException("TODO UC-04 DAO"); }
    public boolean update(GlossaryEntry entry) { throw new UnsupportedOperationException("TODO UC-04 DAO"); }
    public boolean delete(int chapterId, int entryId) { throw new UnsupportedOperationException("TODO UC-04 DAO"); }
}
