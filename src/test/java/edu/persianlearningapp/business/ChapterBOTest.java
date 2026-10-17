package edu.persianlearningapp.business;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.observer.Observable;
import edu.persianlearningapp.observer.Observer;

public class ChapterBOTest {

    private RecordingDAL dal;
    private LearningEventSource events;
    private ChapterBO chapterBO;
    private int eventCount;

    @BeforeEach
    public void setUp() {
        dal = new RecordingDAL();
        events = new LearningEventSource();
        chapterBO = new ChapterBO(dal, events);
        eventCount = 0;

        events.addObserver(new Observer() {
            @Override
            public void update(Observable subject) {
                eventCount++;
            }
        });
    }

    @Test
    public void createChapterTrimsTitleSavesChapterAndPublishesEvent() {
        int id = chapterBO.createChapter("  درسِ فارسی  ");

        assertEquals(1, id);
        assertEquals("درسِ فارسی", dal.insertedChapter.getTitle());
        assertEquals(1, dal.insertCalls);
        assertEquals(1, eventCount);
    }

    @Test
    public void createChapterPreservesPersianAndUrduTitle() {
        String title = "درس فارسی اور اردو سبق";

        chapterBO.createChapter(title);

        assertEquals(title, dal.insertedChapter.getTitle());
    }

    @Test
    public void createChapterRejectsNullTitle() {
        assertThrows(BusinessException.class, () -> chapterBO.createChapter(null));

        assertEquals(0, dal.insertCalls);
        assertEquals(0, eventCount);
    }

    @Test
    public void createChapterRejectsEmptyTitle() {
        assertThrows(BusinessException.class, () -> chapterBO.createChapter(""));

        assertEquals(0, dal.insertCalls);
        assertEquals(0, eventCount);
    }

    @Test
    public void createChapterRejectsWhitespaceOnlyTitle() {
        assertThrows(BusinessException.class, () -> chapterBO.createChapter("   "));

        assertEquals(0, dal.insertCalls);
        assertEquals(0, eventCount);
    }

    @Test
    public void createChapterRejectsTitleLongerThan255Characters() {
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            longTitle.append('A');
        }

        assertThrows(BusinessException.class,
                () -> chapterBO.createChapter(longTitle.toString()));

        assertEquals(0, dal.insertCalls);
        assertEquals(0, eventCount);
    }

    @Test
    public void listChaptersReturnsSavedChapters() {
        dal.addChapter(1, "Chapter One");
        dal.addChapter(2, "Chapter Two");

        List<Chapter> chapters = chapterBO.listChapters();

        assertEquals(2, chapters.size());
        assertEquals("Chapter One", chapters.get(0).getTitle());
        assertEquals("Chapter Two", chapters.get(1).getTitle());
    }

    @Test
    public void getChapterReturnsChapterWhenItExists() {
        dal.addChapter(5, "Existing Chapter");

        Chapter result = chapterBO.getChapter(5);

        assertEquals(5, result.getId());
        assertEquals("Existing Chapter", result.getTitle());
    }

    @Test
    public void getChapterThrowsErrorWhenChapterDoesNotExist() {
        assertThrows(BusinessException.class, () -> chapterBO.getChapter(99));
    }

    @Test
    public void renameChapterRejectsBlankTitleAndKeepsOldTitle() {
        Chapter existing = dal.addChapter(3, "Original Title");

        assertThrows(BusinessException.class,
                () -> chapterBO.renameChapter(3, "   "));

        assertEquals("Original Title", existing.getTitle());
        assertEquals(0, dal.updateCalls);
        assertEquals(0, eventCount);
    }

    @Test
    public void renameChapterUpdatesTitleAndPublishesEvent() {
        dal.addChapter(3, "Original Title");

        chapterBO.renameChapter(3, "نیا عنوان");

        assertEquals("نیا عنوان", dal.findById(3).getTitle());
        assertEquals(1, dal.updateCalls);
        assertEquals(1, eventCount);
    }

    @Test
    public void renameChapterThrowsErrorWhenChapterDoesNotExist() {
        assertThrows(BusinessException.class,
                () -> chapterBO.renameChapter(99, "New Title"));

        assertEquals(0, eventCount);
    }

    @Test
    public void deleteChapterDoesNothingWhenNotConfirmed() {
        dal.addChapter(4, "Keep This Chapter");

        chapterBO.deleteChapter(4, false);

        assertNotNull(dal.findById(4));
        assertEquals(0, dal.deleteCalls);
        assertEquals(0, eventCount);
    }

    @Test
    public void deleteChapterDeletesChapterAndPublishesEventWhenConfirmed() {
        dal.addChapter(4, "Delete This Chapter");

        chapterBO.deleteChapter(4, true);

        assertNull(dal.findById(4));
        assertEquals(1, dal.deleteCalls);
        assertEquals(1, eventCount);
    }

    @Test
    public void deleteChapterThrowsErrorWhenChapterDoesNotExist() {
        assertThrows(BusinessException.class,
                () -> chapterBO.deleteChapter(99, true));

        assertEquals(0, eventCount);
    }

    /**
     * Small in-memory DAL for ChapterBO unit tests.
     * It records chapter operations and does not use MySQL.
     */
    private static class RecordingDAL extends StubDALFacade {
        private final List<Chapter> chapters = new ArrayList<Chapter>();
        private Chapter insertedChapter;
        private int insertCalls;
        private int updateCalls;
        private int deleteCalls;

        @Override
        public int insert(Chapter chapter) {
            insertCalls++;
            insertedChapter = chapter;
            return 1;
        }

        @Override
        public List<Chapter> findAll() {
            return new ArrayList<Chapter>(chapters);
        }

        @Override
        public Chapter findById(int chapterId) {
            for (Chapter chapter : chapters) {
                if (chapter.getId() == chapterId) {
                    return chapter;
                }
            }
            return null;
        }

        @Override
        public boolean updateTitle(int chapterId, String title) {
            updateCalls++;
            Chapter chapter = findById(chapterId);
            if (chapter == null) {
                return false;
            }
            chapter.setTitle(title);
            return true;
        }

        @Override
        public boolean deleteById(int chapterId) {
            deleteCalls++;
            Chapter chapter = findById(chapterId);
            return chapter != null && chapters.remove(chapter);
        }

        private Chapter addChapter(int id, String title) {
            Chapter chapter = new Chapter(id, title);
            chapters.add(chapter);
            return chapter;
        }
    }
}