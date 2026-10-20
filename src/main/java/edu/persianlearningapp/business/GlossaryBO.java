package edu.persianlearningapp.business;

import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.GlossaryEntry;
import edu.persianlearningapp.domain.LearningMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UC-04 Business Object: Manage Chapter Glossary.
 *
 * Responsibilities:
 *   - Validate inputs before accessing the database.
 *   - Apply business rules defined in the SRS.
 *   - Use IDALFacade to save and retrieve data.
 *   - Keep database operations outside this class.
 *
 * Urdu messages are stored as constants in this class.
 * This makes the messages easy to find, understand, and update.
 * Each message has a comment explaining its purpose.
 *
 * Owner: Rabia (UC-04)
 */

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

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    /**
     * Add a new glossary entry to a chapter.
     *
     * SRS: FR-GL-02, BR-03, BR-07, NFR-RL-02
     *
     * @param chapterId   the chapter this entry belongs to
     * @param persianWord the Persian word (must not be blank, max 255 chars)
     * @param urduMeaning the Urdu meaning (must not be blank, max 1000 chars)
     * @return the database-generated entry_id of the new entry
     * @throws BusinessException if any validation fails or the chapter is missing
     */
    public int addEntry(int chapterId, String persianWord, String urduMeaning) {
        // Trim once; every check below uses the trimmed values.
        String word    = trim(persianWord);
        String meaning = trim(urduMeaning);

        // Validate fields (BR-03, BR-07, NFR-US-06).
        validatePersianWord(word);
        validateUrduMeaning(meaning);

        // The chapter must exist before we can add an entry (FR-GL-06).
        requireChapterExists(chapterId);

        // Build the entry with id=0; the database will assign the real id.
        GlossaryEntry entry = new GlossaryEntry(0, chapterId, word, meaning);

        // Delegate to DAO; it returns the generated entry_id (NFR-RL-01).
        return dal.insert(entry);
    }

    /**
     * Edit an existing glossary entry.
     *
     * The entry must belong to the given chapter. If any validation fails,
     * nothing is written to the database and the old data is preserved (BR-06).
     *
     * SRS: FR-GL-04, BR-03, BR-06, BR-07, NFR-RL-02
     *
     * @param chapterId   the chapter that owns the entry
     * @param entryId     the id of the entry to edit
     * @param persianWord the new Persian word
     * @param urduMeaning the new Urdu meaning
     * @throws BusinessException if validation fails or the entry is not found
     */
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

        // All checks passed. Build the updated entry and save it.
        GlossaryEntry updated = new GlossaryEntry(entryId, chapterId, word, meaning);
        boolean saved = dal.update(updated);

        // If update returns false the entry was not found / not updated.
        if (!saved) {
            throw new BusinessException(MSG_ENTRY_NOT_FOUND);
        }
    }

    /**
     * Delete a glossary entry after the user has confirmed the operation.
     *
     * If confirmed is false the method returns immediately and nothing is deleted.
     * This mirrors the confirmation-dialog result passed straight from the UI (BR-04).
     *
     * SRS: FR-GL-05, BR-04, BR-06
     *
     * @param chapterId the chapter that owns the entry
     * @param entryId   the id of the entry to delete
     * @param confirmed true if the user pressed Yes in the confirmation dialog
     * @throws BusinessException if the entry does not exist in this chapter
     */
    public void deleteEntry(int chapterId, int entryId, boolean confirmed) {
        // User did not confirm — do nothing (BR-04).
        if (!confirmed) {
            return;
        }

        // Verify the entry exists in this chapter before trying to delete.
        requireEntryInChapter(chapterId, entryId);

        // Delete it. If the DAO says nothing was deleted, the entry is gone.
        boolean deleted = dal.delete(chapterId, entryId);
        if (!deleted) {
            throw new BusinessException(MSG_ENTRY_NOT_FOUND);
        }
    }

    /**
     * Return all glossary entries for a chapter (admin view).
     *
     * SRS: FR-GL-03
     *
     * @param chapterId the chapter whose entries to return
     * @return list of entries; empty if the chapter has no entries
     */
    public List<GlossaryEntry> listForAdmin(int chapterId) {
        return dal.findGlossaryByChapterId(chapterId);
    }

    /**
     * Return glossary entries appropriate for the given learning mode.
     *
     * ASSUMPTION (flagged per task instructions):
     * FR-10 / the SRS (Iteration 1) does not fully specify learner screens.
     * The team has agreed on the following interpretation until confirmed:
     *   - STUDY mode: the learner can see the full glossary to look up words.
     *   - EXAM  mode: the glossary is hidden so the learner cannot look up answers.
     * If the instructor changes this rule, update only the two lines below.
     *
     * SRS reference: FR-CS-01 (Glossary component), NFR-US-01
     *
     * @param chapterId the chapter whose glossary to return
     * @param mode      STUDY or EXAM
     * @return the chapter's glossary in STUDY mode; an empty list in EXAM mode
     */
    public List<GlossaryEntry> listForLearner(int chapterId, LearningMode mode) {
        if (mode == LearningMode.STUDY) {
            return dal.findGlossaryByChapterId(chapterId);
        }
        // EXAM mode: return empty list (glossary is hidden during an exam).
        return new ArrayList<GlossaryEntry>();
    }

    // ----------------------------------------------------------------
    // Private helpers — all validation lives here so each rule is
    // written exactly once and easy to find for the viva.
    // ----------------------------------------------------------------

    /**
     * Return the string trimmed of surrounding whitespace, or an empty
     * string if the input is null.
     */
    private static String trim(String value) {
        return (value == null) ? "" : value.trim();
    }

    /**
     * Throw BusinessException if the Persian word is blank or too long.
     * BR-03 requires both a Persian word and an Urdu meaning.
     */
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

    /**
     * Throw BusinessException if the Urdu meaning is blank or too long.
     * BR-03 requires both a Persian word and an Urdu meaning.
     */
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

    /**
     * Throw BusinessException if the chapter with the given id does not exist.
     * Returning null from findById means the chapter is not in the database.
     */
    private void requireChapterExists(int chapterId) {
        Chapter chapter = dal.findById(chapterId);
        if (chapter == null) {
            throw new BusinessException(MSG_CHAPTER_NOT_FOUND);
        }
    }

    /**
     * Throw BusinessException if no entry with the given entryId belongs to
     * the given chapter. This enforces FR-GL-06 (entry belongs to one chapter)
     * and prevents editing or deleting another chapter's entries.
     */
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
    /**
     * Return true if the text contains any English (Latin) letter.
     */
   
}
