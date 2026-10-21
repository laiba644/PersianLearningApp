package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.GlossaryEntry;
import edu.persianlearningapp.domain.LearningMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GlossaryBO {

    // ----------------------------------------------------------------
    // Dependency injected via constructor (no hard-wired DAOs here).
    // ----------------------------------------------------------------
    private final IDALFacade dal;
    private static boolean hasLatin(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeScript.of(s.charAt(i)) == Character.UnicodeScript.LATIN) {
                return true;
            }
        }
        return false;
    }

   
    private void requireNotDuplicate(int chapterId, String word, int ignoreEntryId) {
        for (GlossaryEntry e : dal.findGlossaryByChapterId(chapterId)) {
            if (e.getId() != ignoreEntryId && e.getPersianWord().trim().equals(word)) {
                throw new BusinessException(MSG_DUPLICATE_WORD);
            }
        }
    }
    public GlossaryBO(IDALFacade dal) {
        this.dal = dal;
    }

    // ----------------------------------------------------------------
    // Database column limits (taken from schema.sql, kept in one place).
    // ----------------------------------------------------------------
    /** Maximum length allowed for a Persian word in the database. */
    static final int MAX_PERSIAN_LENGTH = 255;

    /** Maximum length allowed for an Urdu meaning in the database. */
    static final int MAX_URDU_LENGTH = 1000;

    // ----------------------------------------------------------------
    // Urdu error-message constants (NFR-US-01, NFR-US-06).

    // ----------------------------------------------------------------

    // (The chapter does not exist.)
    static final String MSG_CHAPTER_NOT_FOUND =
        "باب موجود نہیں ہے۔";

    static final String MSG_PERSIAN_BLANK =
        "فارسی لفظ خالی "
        + "نہیں ہو سکتا۔";

    static final String MSG_URDU_BLANK =
        "اردو معنی خالی "
        + "نہیں ہو سکتے۔";

    static final String MSG_PERSIAN_TOO_LONG =
        "فارسی لفظ 255 حروف "
        + "سے زیادہ نہیں "
        + "ہو سکتا۔";

    static final String MSG_URDU_TOO_LONG =
        "اردو معنی 1000 حروف "
        + "سے زیادہ نہیں "
        + "ہو سکتے۔";
    static final String MSG_PERSIAN_LATIN =
    	    "فارسی لفظ میں انگریزی حروف نہیں ہو سکتے۔";

    	static final String MSG_URDU_LATIN =
    	    "اردو معنی میں انگریزی حروف نہیں ہو سکتے۔";
    // (This entry does not exist in this chapter.)
    static final String MSG_ENTRY_NOT_FOUND =
        "یہ اندراج اس باب "
        + "میں موجود نہیں۔";
    // (This word already exists in this chapter.)
    static final String MSG_DUPLICATE_WORD =
        "یہ لفظ اس باب "
        + "میں پہلے سے موجود ہے۔";
    public int addEntry(int chapterId, String persianWord, String urduMeaning) {
        // Trim once; every check below uses the trimmed values.
        String word    = trim(persianWord);
        String meaning = trim(urduMeaning);

        // Validate fields (BR-03, BR-07, NFR-US-06).
        validatePersianWord(word);
        validateUrduMeaning(meaning);

        // The chapter must exist before we can add an entry (FR-GL-06).
        requireChapterExists(chapterId);
        requireNotDuplicate(chapterId, word, 0);
        // Build the entry with id=0; the database will assign the real id.
        GlossaryEntry entry = new GlossaryEntry(0, chapterId, word, meaning);

        // Delegate to DAO; it returns the generated entry_id (NFR-RL-01).
        return dal.insert(entry);
    }
    public void editEntry(int chapterId, int entryId,
                          String persianWord, String urduMeaning) {
        // Trim once.
        String word    = trim(persianWord);
        String meaning = trim(urduMeaning);

        // Validate ALL fields first — nothing is saved until every check passes.
        validatePersianWord(word);
        validateUrduMeaning(meaning);

        // Confirm the entry belongs to this chapter.
        requireEntryInChapter(chapterId, entryId);
        requireNotDuplicate(chapterId, word, entryId);
        // All checks passed. Build the updated entry and save it.
        GlossaryEntry updated = new GlossaryEntry(entryId, chapterId, word, meaning);
        boolean saved = dal.update(updated);

        // If update returns false the entry was not found / not updated.
        if (!saved) {
            throw new BusinessException(MSG_ENTRY_NOT_FOUND);
        }
    }

    public void deleteEntry(int chapterId, int entryId, boolean confirmed) {
        // User did not confirm — do nothing (BR-04).
        if (!confirmed) {
            return;
        }

        // Verify the entry exists in this chapter before trying to delete.
        requireEntryInChapter(chapterId, entryId);
     // Isay delete kar do. Agar DAO yeh kahe ke kuch bhi delete nahi hua, toh samjho entry pehle hi khatam ho chuki hai.
        boolean deleted = dal.delete(chapterId, entryId);
        if (!deleted) {
            throw new BusinessException(MSG_ENTRY_NOT_FOUND);
        }
    }
   
    public List<Chapter> listChapters() {
        return dal.findAll();
    }

    public List<GlossaryEntry> listForAdmin(int chapterId) {
        return dal.findGlossaryByChapterId(chapterId);
    }

    public List<GlossaryEntry> listForLearner(int chapterId, LearningMode mode) {
        if (mode == LearningMode.STUDY) {
            return dal.findGlossaryByChapterId(chapterId);
        }
        // EXAM mode: return empty list (glossary is hidden during an exam).
        return new ArrayList<GlossaryEntry>();
    }

    private static String trim(String value) {
        return (value == null) ? "" : value.trim();
    }
    private static void validatePersianWord(String word) {
        if (word.isEmpty()) {
            throw new BusinessException(MSG_PERSIAN_BLANK);
        }
        if (hasLatin(word)) {
            throw new BusinessException(MSG_PERSIAN_LATIN);
        }
        if (word.length() > MAX_PERSIAN_LENGTH) {
            throw new BusinessException(MSG_PERSIAN_TOO_LONG);
        }
    }

    private static void validateUrduMeaning(String meaning) {
        if (meaning.isEmpty()) {
            throw new BusinessException(MSG_URDU_BLANK);
        }
        if (meaning.length() > MAX_URDU_LENGTH) {
            throw new BusinessException(MSG_URDU_TOO_LONG);
        }
        if (hasLatin(meaning)) {
            throw new BusinessException(MSG_URDU_LATIN);
        }
    }
    private void requireChapterExists(int chapterId) {
        Chapter chapter = dal.findById(chapterId);
        if (chapter == null) {
            throw new BusinessException(MSG_CHAPTER_NOT_FOUND);
        }
    }
    private void requireEntryInChapter(int chapterId, int entryId) {
        List<GlossaryEntry> entries = dal.findGlossaryByChapterId(chapterId);
        for (GlossaryEntry e : entries) {
            if (e.getId() == entryId) {
                return; // Found — all good.
            }
        }
        // Not found in this chapter.
        throw new BusinessException(MSG_ENTRY_NOT_FOUND);
    }
   
}
