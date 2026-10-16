package edu.persianlearningapp.dao;

public interface DAOFactory {
    ChapterDAO createChapterDAO();
    ComponentDAO createComponentDAO();
    ComponentContentDAO createComponentContentDAO();
    GlossaryDAO createGlossaryDAO();
    IDALFacade createDALFacade();
}
