package edu.persianlearningapp.business;

import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.domain.GlossaryEntry;
import edu.persianlearningapp.domain.LearningMode;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JUnit 5 unit tests for GlossaryBO (UC-04).
 *
 * Rules followed:
 *   - No Mockito.  All stubs are hand-written anonymous subclasses of StubDALFacade.
 *   - Tests are independent of each other and of the database.
 *   - Each test creates its own stub and its own GlossaryBO instance.
 *   - MemoryStore (at the bottom of this file) is a tiny in-memory list used
 *     by stubs that need to remember what was saved.
 *
 * SRS coverage: FR-GL-01..06, BR-03, BR-04, BR-06, BR-07,
 *               NFR-US-01, NFR-US-02, NFR-MT-01, NFR-MT-03
 *
 * Owner: Rabia (UC-04)
 */
public class GlossaryBOTest {

    // ------------------------------------------------------------------
    // Convenience: build a chapter stub that knows about chapter id=1.
    // ------------------------------------------------------------------

    /** Returns a stub where findById(1) returns a real Chapter and anything else returns null. */
    private StubDALFacade stubWithChapter1() {
        return new StubDALFacade() {
            @Override
            public Chapter findById(int id) {
                return (id == 1) ? new Chapter(1, "Test Chapter") : null;
            }
        };
    }

    // ==================================================================
    // addEntry — happy path
    // ==================================================================

    /**
     * A valid Persian word and Urdu meaning must be accepted and the
     * returned id must be the one the DAO generated (FR-GL-02, BR-03).
     */
    @Test
    public void addEntry_valid_returnsGeneratedId() {
        // Stub: chapter 1 exists; insert returns id 42.
        StubDALFacade stub = new StubDALFacade() {
            @Override
            public Chapter findById(int id) {
                return new Chapter(1, "Ch");
            }
            @Override
            public int insert(GlossaryEntry entry) {
                return 42;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);

        // (book in Persian) / (book in Urdu)
        int id = bo.addEntry(1,
            "کتاب",
            "کتاب");

        assertEquals(42, id, "addEntry must return the id given by the DAO.");
    }

    // ==================================================================
    // addEntry — validation failures (BR-03, BR-07, NFR-US-06)
    // ==================================================================

    /** Blank Persian word must throw BusinessException (BR-03). */
    @Test
    public void addEntry_blankPersian_throwsBusinessException() {
        GlossaryBO bo = new GlossaryBO(stubWithChapter1());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            // Urdu meaning is valid; Persian is blank.
            bo.addEntry(1, "   ",
                "اردو معنی")
        );

        assertEquals(GlossaryBO.MSG_PERSIAN_BLANK, ex.getMessage(),
            "Error message must exactly match the Persian-blank constant.");
    }

    /** Blank Urdu meaning must throw BusinessException (BR-03). */
    @Test
    public void addEntry_blankUrdu_throwsBusinessException() {
        GlossaryBO bo = new GlossaryBO(stubWithChapter1());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            // (tree in Persian) is valid; Urdu is blank.
            bo.addEntry(1, "درخت", "")
        );

        assertEquals(GlossaryBO.MSG_URDU_BLANK, ex.getMessage(),
            "Error message must exactly match the Urdu-blank constant.");
    }

    /**
     * When both fields are blank, Persian is validated first so the
     * Persian-blank message must be thrown (BR-03, BR-07).
     */
    @Test
    public void addEntry_bothBlank_throwsPersianBlankFirst() {
        GlossaryBO bo = new GlossaryBO(stubWithChapter1());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            bo.addEntry(1, "", "")
        );

        assertEquals(GlossaryBO.MSG_PERSIAN_BLANK, ex.getMessage(),
            "Persian word is validated before Urdu meaning.");
    }

    /**
     * Whitespace-only values (tab, newline) count as blank after trimming (BR-03).
     */
    @Test
    public void addEntry_whitespaceOnly_treatedAsBlank() {
        GlossaryBO bo = new GlossaryBO(stubWithChapter1());

        assertThrows(BusinessException.class, () ->
            bo.addEntry(1, "\t\n", "\t\n"),
            "Tab and newline only must be treated as blank."
        );
    }

    // ==================================================================
    // addEntry — length boundary tests (BR-07)
    // ==================================================================

    /** Persian word exactly at the 255-character limit must succeed. */
    @Test
    public void addEntry_persianAtMaxLength_succeeds() {
        // Build a 255-character string using the letter 'a'.
        String word255 = repeat("a", GlossaryBO.MAX_PERSIAN_LENGTH);

        StubDALFacade stub = new StubDALFacade() {
            @Override public Chapter findById(int id) { return new Chapter(1, "Ch"); }
            @Override public int insert(GlossaryEntry e) { return 1; }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        // Must not throw — exactly at the limit is allowed.
        assertDoesNotThrow(() ->
            bo.addEntry(1, word255, "meaning")
        );
    }

    /** Persian word one character over the limit must be rejected. */
    @Test
    public void addEntry_persianOneOverMaxLength_throws() {
        String word256 = repeat("a", GlossaryBO.MAX_PERSIAN_LENGTH + 1);

        GlossaryBO bo = new GlossaryBO(stubWithChapter1());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            bo.addEntry(1, word256, "meaning")
        );

        assertEquals(GlossaryBO.MSG_PERSIAN_TOO_LONG, ex.getMessage());
    }

    /** Urdu meaning exactly at the 1000-character limit must succeed. */
    @Test
    public void addEntry_urduAtMaxLength_succeeds() {
        String meaning1000 = repeat("a", GlossaryBO.MAX_URDU_LENGTH);

        StubDALFacade stub = new StubDALFacade() {
            @Override public Chapter findById(int id) { return new Chapter(1, "Ch"); }
            @Override public int insert(GlossaryEntry e) { return 1; }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        assertDoesNotThrow(() ->
            bo.addEntry(1, "word", meaning1000)
        );
    }

    /** Urdu meaning one character over the limit must be rejected. */
    @Test
    public void addEntry_urduOneOverMaxLength_throws() {
        String meaning1001 = repeat("a", GlossaryBO.MAX_URDU_LENGTH + 1);

        GlossaryBO bo = new GlossaryBO(stubWithChapter1());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            bo.addEntry(1, "word", meaning1001)
        );

        assertEquals(GlossaryBO.MSG_URDU_TOO_LONG, ex.getMessage());
    }

    // ==================================================================
    // addEntry — chapter existence (FR-GL-06)
    // ==================================================================

    /** Adding to a chapter that does not exist must throw BusinessException. */
    @Test
    public void addEntry_chapterNotFound_throws() {
        // Default StubDALFacade.findById returns null for every id.
        GlossaryBO bo = new GlossaryBO(new StubDALFacade());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            bo.addEntry(99, "word", "meaning")
        );

        assertEquals(GlossaryBO.MSG_CHAPTER_NOT_FOUND, ex.getMessage());
    }

    // ==================================================================
    // editEntry — happy path (FR-GL-04)
    // ==================================================================

    /** A valid edit must call update and save the new values (FR-GL-04, BR-06). */
    @Test
    public void editEntry_valid_updatesEntry() {
        // The store holds the entry we are going to edit.
        MemoryStore store = new MemoryStore();
        store.add(new GlossaryEntry(10, 1, "old-word", "old-meaning"));

        StubDALFacade stub = new StubDALFacade() {
            @Override public Chapter findById(int id) { return new Chapter(1, "Ch"); }

            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int chapterId) {
                return store.allForChapter(chapterId);
            }

            @Override
            public boolean update(GlossaryEntry entry) {
                store.replace(entry);   // save new values in memory
                return true;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        // (water in Persian) / (water in Urdu)
        bo.editEntry(1, 10,
            "آب",             // آب  (Persian: water)
            "پانی"); // پانی (Urdu: water)

        // The store must now hold the new values.
        GlossaryEntry saved = store.findById(10);
        assertNotNull(saved, "Entry must still exist after edit.");
        // "آب" == "آب"
        assertEquals("آب",             saved.getPersianWord());
        // "پانی" == "پانی"
        assertEquals("پانی", saved.getUrduMeaning());
    }

    /**
     * When validation fails on an edit, update must NOT be called and the
     * old data must remain untouched (BR-06, NFR-RL-02).
     */
    @Test
    public void editEntry_invalidData_doesNotCallUpdate() {
        // Track whether update was called.
        boolean[] updateCalled = {false};

        MemoryStore store = new MemoryStore();
        store.add(new GlossaryEntry(10, 1, "original-word", "original-meaning"));

        StubDALFacade stub = new StubDALFacade() {
            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int id) {
                return store.allForChapter(id);
            }
            @Override
            public boolean update(GlossaryEntry entry) {
                updateCalled[0] = true;  // should never reach here
                return true;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);

        // Blank Persian word — validation must throw before update is called.
        assertThrows(BusinessException.class, () ->
            bo.editEntry(1, 10, "", "valid meaning")
        );

        assertFalse(updateCalled[0], "update() must NOT be called when validation fails.");
    }

    /**
     * Editing an entry that belongs to a different chapter must throw (FR-GL-06).
     */
    @Test
    public void editEntry_entryBelongsToOtherChapter_throws() {
        // Entry 10 lives in chapter 99, not chapter 1.
        StubDALFacade stub = new StubDALFacade() {
            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int chapterId) {
                // Chapter 1 is empty; chapter 99 has the entry.
                if (chapterId == 99) {
                    List<GlossaryEntry> list = new ArrayList<GlossaryEntry>();
                    list.add(new GlossaryEntry(10, 99, "word", "meaning"));
                    return list;
                }
                return Collections.<GlossaryEntry>emptyList();
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);

        BusinessException ex = assertThrows(BusinessException.class, () ->
            // Try to edit entry 10 via chapter 1 — must be rejected.
            bo.editEntry(1, 10, "new-word", "new-meaning")
        );

        assertEquals(GlossaryBO.MSG_ENTRY_NOT_FOUND, ex.getMessage());
    }

    // ==================================================================
    // deleteEntry (FR-GL-05, BR-04)
    // ==================================================================

    /** Confirmed delete must call dal.delete (FR-GL-05, BR-04). */
    @Test
    public void deleteEntry_confirmed_callsDelete() {
        boolean[] deleteCalled = {false};

        StubDALFacade stub = new StubDALFacade() {
            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int id) {
                List<GlossaryEntry> list = new ArrayList<GlossaryEntry>();
                list.add(new GlossaryEntry(5, 1, "word", "meaning"));
                return list;
            }
            @Override
            public boolean delete(int chapterId, int entryId) {
                deleteCalled[0] = true;
                return true;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        bo.deleteEntry(1, 5, true); // confirmed = true

        assertTrue(deleteCalled[0], "delete() must be called when confirmed=true.");
    }

    /**
     * When the user does not confirm, delete must NOT be called and the
     * entry must remain unchanged (BR-04, BR-06).
     */
    @Test
    public void deleteEntry_notConfirmed_doesNotCallDelete() {
        boolean[] deleteCalled = {false};

        StubDALFacade stub = new StubDALFacade() {
            @Override
            public boolean delete(int chapterId, int entryId) {
                deleteCalled[0] = true;
                return true;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        bo.deleteEntry(1, 5, false); // confirmed = false

        assertFalse(deleteCalled[0], "delete() must NOT be called when confirmed=false.");
    }

    /** Deleting an entry that does not exist in the chapter must throw (BR-07). */
    @Test
    public void deleteEntry_unknownEntry_throws() {
        // The chapter's glossary is empty — entry 99 is not there.
        GlossaryBO bo = new GlossaryBO(new StubDALFacade());

        BusinessException ex = assertThrows(BusinessException.class, () ->
            bo.deleteEntry(1, 99, true)
        );

        assertEquals(GlossaryBO.MSG_ENTRY_NOT_FOUND, ex.getMessage());
    }

    // ==================================================================
    // listForAdmin (FR-GL-03)
    // ==================================================================

    /**
     * listForAdmin must return only the entries belonging to the requested
     * chapter, not entries from other chapters (FR-GL-03, FR-GL-06).
     */
    @Test
    public void listForAdmin_returnsOnlyRequestedChaptersEntries() {
        StubDALFacade stub = new StubDALFacade() {
            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int chapterId) {
                List<GlossaryEntry> list = new ArrayList<GlossaryEntry>();
                if (chapterId == 1) {
                    list.add(new GlossaryEntry(1, 1, "word-A", "meaning-A"));
                    list.add(new GlossaryEntry(2, 1, "word-B", "meaning-B"));
                } else if (chapterId == 2) {
                    list.add(new GlossaryEntry(3, 2, "word-C", "meaning-C"));
                }
                return list;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        List<GlossaryEntry> result = bo.listForAdmin(1);

        assertEquals(2, result.size(), "listForAdmin(1) must return exactly 2 entries.");
        assertEquals(1, result.get(0).getChapterId());
        assertEquals(1, result.get(1).getChapterId());
    }

    // ==================================================================
    // listForLearner (FR-CS-01, STUDY/EXAM assumption — see GlossaryBO)
    // ==================================================================

    /** STUDY mode must return the chapter's glossary entries. */
    @Test
    public void listForLearner_studyMode_returnsEntries() {
        StubDALFacade stub = new StubDALFacade() {
            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int id) {
                List<GlossaryEntry> list = new ArrayList<GlossaryEntry>();
                list.add(new GlossaryEntry(1, 1, "word", "meaning"));
                return list;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        List<GlossaryEntry> result = bo.listForLearner(1, LearningMode.STUDY);

        assertFalse(result.isEmpty(), "STUDY mode must return glossary entries.");
    }

    /** EXAM mode must return an empty list (glossary is hidden during exam). */
    @Test
    public void listForLearner_examMode_returnsEmptyList() {
        // Even if the DAO has entries, EXAM mode must hide them.
        StubDALFacade stub = new StubDALFacade() {
            @Override
            public List<GlossaryEntry> findGlossaryByChapterId(int id) {
                List<GlossaryEntry> list = new ArrayList<GlossaryEntry>();
                list.add(new GlossaryEntry(1, 1, "word", "meaning"));
                return list;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        List<GlossaryEntry> result = bo.listForLearner(1, LearningMode.EXAM);

        assertTrue(result.isEmpty(), "EXAM mode must return an empty list.");
    }

    // ==================================================================
    // Text preservation (FR-CS-07, NFR-US-02)
    // ==================================================================

    /**
     * Persian and Urdu text must survive the round-trip through the BO
     * exactly as entered — no characters must be lost or changed.
     * FR-CS-07, NFR-US-02, NFR-US-03.
     */
    @Test
    public void addEntry_persianAndUrduTextPreservedExactly() {
        // "آزمایش" (test/experiment in Persian)
        // "اِمتحان" (examination in Urdu)
        final String persian = "آزمایش"; // آزمایش
        final String urdu    = "اِمتحان"; // اِمتحان

        // The stub captures exactly what was passed to insert().
        GlossaryEntry[] captured = {null};

        StubDALFacade stub = new StubDALFacade() {
            @Override public Chapter findById(int id) { return new Chapter(1, "Ch"); }
            @Override
            public int insert(GlossaryEntry entry) {
                captured[0] = entry;
                return 1;
            }
        };

        GlossaryBO bo = new GlossaryBO(stub);
        bo.addEntry(1, persian, urdu);

        assertNotNull(captured[0], "insert() must have been called.");
        assertEquals(persian, captured[0].getPersianWord(),
            "Persian word must be preserved byte-for-byte.");
        assertEquals(urdu, captured[0].getUrduMeaning(),
            "Urdu meaning must be preserved byte-for-byte.");
    }

    // ==================================================================
    // MemoryStore — tiny in-memory helper used by stubs that need to
    // remember saved data across multiple calls within one test.
    // This class lives inside the test file; it is NOT used in production.
    // ==================================================================

    /**
     * A simple list-backed store for GlossaryEntry objects.
     * Stubs extend StubDALFacade and use a MemoryStore instance to
     * simulate a real (but in-memory) database table.
     */
    private static class MemoryStore {

        private final List<GlossaryEntry> entries = new ArrayList<GlossaryEntry>();

        /** Add an entry to the store (simulates an existing database row). */
        void add(GlossaryEntry entry) {
            entries.add(entry);
        }

        /** Return all entries whose chapterId matches (simulates SELECT WHERE chapter_id=?). */
        List<GlossaryEntry> allForChapter(int chapterId) {
            List<GlossaryEntry> result = new ArrayList<GlossaryEntry>();
            for (GlossaryEntry e : entries) {
                if (e.getChapterId() == chapterId) {
                    result.add(e);
                }
            }
            return result;
        }

        /** Find an entry by id; returns null if not found. */
        GlossaryEntry findById(int id) {
            for (GlossaryEntry e : entries) {
                if (e.getId() == id) {
                    return e;
                }
            }
            return null;
        }

        /**
         * Replace the stored entry that has the same id with the new values
         * (simulates UPDATE ... WHERE entry_id=?).
         */
        void replace(GlossaryEntry updated) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getId() == updated.getId()) {
                    entries.set(i, updated);
                    return;
                }
            }
        }

        /** Remove an entry by id (simulates DELETE WHERE entry_id=?). */
        void remove(int id) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getId() == id) {
                    entries.remove(i);
                    return;
                }
            }
        }
    }

    // ==================================================================
    // Utility — Java 8 compatible repeat (String.repeat is Java 11+).
    // ==================================================================

    /** Return a string made of n copies of s. */
    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
