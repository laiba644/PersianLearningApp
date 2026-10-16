package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.*;
import edu.persianlearningapp.domain.*;
import java.util.List;

public class DALFacade implements IDALFacade {
    private final ChapterDAO chapters;
    private final ComponentDAO components;
    private final ComponentContentDAO content;
    private final GlossaryDAO glossary;
    public DALFacade(ChapterDAO chapters, ComponentDAO components, ComponentContentDAO content, GlossaryDAO glossary) {
        this.chapters = chapters; this.components = components; this.content = content; this.glossary = glossary;
    }
    public int insert(Chapter value) { return chapters.insert(value); }
    public List<Chapter> findAll() { return chapters.findAll(); }
    public Chapter findById(int id) { return chapters.findById(id); }
    public boolean updateTitle(int id, String title) { return chapters.updateTitle(id, title); }
    public boolean deleteById(int id) { return chapters.deleteById(id); }
    public List<ChapterComponent> findComponentsByChapterId(int id) { return components.findComponentsByChapterId(id); }
    public void replaceStructure(int id, List<ChapterComponent> values) { components.replaceStructure(id, values); }
    public ComponentContent findContent(int chapterId, int componentId) { return content.findContent(chapterId, componentId); }
    public boolean saveContent(int chapterId, int componentId, String persian, String urdu) { return content.saveContent(chapterId, componentId, persian, urdu); }
    public boolean deleteContent(int chapterId, int componentId) { return content.deleteContent(chapterId, componentId); }
    public List<GlossaryEntry> findGlossaryByChapterId(int id) { return glossary.findGlossaryByChapterId(id); }
    public int insert(GlossaryEntry value) { return glossary.insert(value); }
    public boolean update(GlossaryEntry value) { return glossary.update(value); }
    public boolean delete(int chapterId, int entryId) { return glossary.delete(chapterId, entryId); }
}
