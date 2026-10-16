package edu.persianlearningapp.dao;

/** Unified DAO contract used by business objects, matching the course DAL Facade notes. */
public interface IDALFacade extends ChapterDAO, ComponentDAO, ComponentContentDAO, GlossaryDAO {
}
