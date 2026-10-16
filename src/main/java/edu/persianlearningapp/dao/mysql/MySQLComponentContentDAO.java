package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.ComponentContentDAO;
import edu.persianlearningapp.domain.ComponentContent;

/** UC-03 JDBC skeleton. Faseeh writes SQL and Unicode-safe persistence. */
public class MySQLComponentContentDAO implements ComponentContentDAO {
    public ComponentContent findContent(int chapterId, int componentId) { throw new UnsupportedOperationException("TODO UC-03 DAO"); }
    public boolean saveContent(int chapterId, int componentId, String persian, String urdu) { throw new UnsupportedOperationException("TODO UC-03 DAO"); }
    public boolean deleteContent(int chapterId, int componentId) { throw new UnsupportedOperationException("TODO UC-03 DAO"); }
}
