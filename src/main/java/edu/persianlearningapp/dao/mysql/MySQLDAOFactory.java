package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.*;

public class MySQLDAOFactory extends AbstractDAOFactory {
    public ChapterDAO createChapterDAO() { return new MySQLChapterDAO(); }
    public ComponentDAO createComponentDAO() { return new MySQLComponentDAO(); }
    public ComponentContentDAO createComponentContentDAO() { return new MySQLComponentContentDAO(); }
    public GlossaryDAO createGlossaryDAO() { return new MySQLGlossaryDAO(); }
    public IDALFacade createDALFacade() {
        return new DALFacade(createChapterDAO(), createComponentDAO(), createComponentContentDAO(), createGlossaryDAO());
    }
}
