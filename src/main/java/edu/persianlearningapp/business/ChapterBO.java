package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.LearningEvent;
import edu.persianlearningapp.observer.LearningEventSource;

import java.util.List;

public class ChapterBO {

    private static final int MAX_TITLE_LENGTH = 255;

    private final IDALFacade dal;
    private final LearningEventSource events;

    public ChapterBO(IDALFacade dal, LearningEventSource events) {
        this.dal = dal;
        this.events = events;
    }

    public int createChapter(String title) {
        String cleanTitle = validateAndCleanTitle(title);

        Chapter chapter = new Chapter(0, cleanTitle);
        int chapterId = dal.insert(chapter);

        events.publish(new LearningEvent("CHAPTER_CREATED", chapterId));
        return chapterId;
    }

    public List<Chapter> listChapters() {
        return dal.findAll();
    }

    public Chapter getChapter(int chapterId) {
        requirePositiveId(chapterId);

        Chapter chapter = dal.findById(chapterId);
        if (chapter == null) {
            throw new BusinessException("Chapter not found: " + chapterId);
        }

        return chapter;
    }

    public void renameChapter(int chapterId, String title) {
        requirePositiveId(chapterId);
        String cleanTitle = validateAndCleanTitle(title);

        boolean updated = dal.updateTitle(chapterId, cleanTitle);
        if (!updated) {
            throw new BusinessException("Chapter not found: " + chapterId);
        }

        events.publish(new LearningEvent("CHAPTER_UPDATED", chapterId));
    }

    public void deleteChapter(int chapterId, boolean confirmed) {
        requirePositiveId(chapterId);

        if (!confirmed) {
            return;
        }

        boolean deleted = dal.deleteById(chapterId);
        if (!deleted) {
            throw new BusinessException("Chapter not found: " + chapterId);
        }

        events.publish(new LearningEvent("CHAPTER_DELETED", chapterId));
    }

    private String validateAndCleanTitle(String title) {
        if (title == null) {
            throw new BusinessException("Title is required.");
        }

        String cleanTitle = title.trim();
        if (cleanTitle.isEmpty()) {
            throw new BusinessException("Title is required.");
        }

        int characterCount = cleanTitle.codePointCount(0, cleanTitle.length());
        
        if (characterCount > MAX_TITLE_LENGTH) {
            throw new BusinessException("Title cannot exceed 255 characters.");
        }
        String scriptPattern =
                "[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF"
              + "\\uFB50-\\uFDFF\\uFE70-\\uFEFF\\s،۔؟؛:()]+";

        if (!cleanTitle.matches(scriptPattern)) {
            throw new BusinessException("صرف اردو یا فارسی حروف لکھیں");
        }
        return cleanTitle;
    }

    private void requirePositiveId(int chapterId) {
        if (chapterId <= 0) {
            throw new BusinessException("Chapter id must be positive.");
        }
    }
}