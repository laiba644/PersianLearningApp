package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.ChapterComponent;
import java.util.List;

/** UC-02 BO skeleton. Zohaib implements validation and ordering rules. */
public class StructureBO {
    private final IDALFacade dal;
    public StructureBO(IDALFacade dal) { this.dal = dal; }
    public List<ChapterComponent> getStructure(int chapterId) { throw new UnsupportedOperationException("TODO UC-02"); }
    public void saveStructure(int chapterId, List<ChapterComponent> components) { throw new UnsupportedOperationException("TODO UC-02"); }
}
