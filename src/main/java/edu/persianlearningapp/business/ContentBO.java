package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.ChapterComponent;
import java.util.List;

/** UC-03 BO skeleton. Faseeh implements content rules and Unicode preservation. */
public class ContentBO {
    private final IDALFacade dal;
    public ContentBO(IDALFacade dal) { this.dal = dal; }
    public List<ChapterComponent> listConfiguredComponents(int chapterId) { throw new UnsupportedOperationException("TODO UC-03"); }
    public void saveContent(int chapterId, int componentId, String persian, String urdu) { throw new UnsupportedOperationException("TODO UC-03"); }
    public void removeContent(int chapterId, int componentId) { throw new UnsupportedOperationException("TODO UC-03"); }
}
