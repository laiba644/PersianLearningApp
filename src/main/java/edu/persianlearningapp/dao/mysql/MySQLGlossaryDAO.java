package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.DataAccessException;
import edu.persianlearningapp.dao.GlossaryDAO;
import edu.persianlearningapp.domain.GlossaryEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UC-04 JDBC implementation of GlossaryDAO.
 *
 * Rules followed:
 *   - Every SQL statement uses PreparedStatement with ? parameters.
 *     No string-concatenated SQL anywhere (prevents SQL injection, NFR-RL-02).
 *   - Every Connection/Statement/ResultSet is opened inside a try-with-resources
 *     block so it is always closed, even if an exception occurs.
 *   - Every SQLException is caught and re-thrown as DataAccessException
 *     so the business layer never sees raw JDBC exceptions.
 *   - The database URL in DatabaseConnection already includes
 *     useUnicode=true&characterEncoding=UTF-8, so Persian and Urdu text
 *     (which is UTF-8 / UTF-16 in Java) survives unchanged (FR-CS-07).
 *   - delete() uses both chapter_id AND entry_id in the WHERE clause so it
 *     cannot accidentally delete an entry that belongs to a different chapter.
 *   - No cascade-deletion code is written here; the schema's ON DELETE CASCADE
 *     on the glossary_entries foreign key already handles chapter deletion.
 *
 * Table: glossary_entries (entry_id, chapter_id, persian_word, urdu_meaning)
 *
 * Owner: Rabia (UC-04)
 */
public class MySQLGlossaryDAO implements GlossaryDAO {

    // ------------------------------------------------------------------
    // SQL statements — defined as constants so they are easy to read
    // and change in one place (good for the viva).
    // ------------------------------------------------------------------

    /** SELECT all entries for one chapter, ordered by entry_id for a stable list. */
    private static final String SQL_FIND_BY_CHAPTER =
            "SELECT entry_id, chapter_id, persian_word, urdu_meaning "
            + "FROM glossary_entries "
            + "WHERE chapter_id = ? "
            + "ORDER BY entry_id";

    /** INSERT a new entry and ask MySQL to return the generated entry_id. */
    private static final String SQL_INSERT =
            "INSERT INTO glossary_entries (chapter_id, persian_word, urdu_meaning) "
            + "VALUES (?, ?, ?)";

    /** UPDATE persian_word and urdu_meaning for one entry in one chapter. */
    private static final String SQL_UPDATE =
            "UPDATE glossary_entries "
            + "SET persian_word = ?, urdu_meaning = ? "
            + "WHERE entry_id = ? AND chapter_id = ?";

    /**
     * DELETE an entry only when BOTH entry_id and chapter_id match.
     * This prevents accidentally deleting an entry from the wrong chapter.
     */
    private static final String SQL_DELETE =
            "DELETE FROM glossary_entries "
            + "WHERE entry_id = ? AND chapter_id = ?";

    // ------------------------------------------------------------------
    // GlossaryDAO implementation
    // ------------------------------------------------------------------

    /**
     * Return all glossary entries that belong to the given chapter.
     *
     * SRS: FR-GL-03, NFR-RL-01, NFR-PF-01, FR-CS-07
     *
     * @param chapterId the chapter whose entries to fetch
     * @return a list of GlossaryEntry objects; empty list if none exist
     * @throws DataAccessException if the database query fails
     */
    @Override
    public List<GlossaryEntry> findGlossaryByChapterId(int chapterId) {
        List<GlossaryEntry> results = new ArrayList<GlossaryEntry>();

        // try-with-resources closes conn and ps automatically.
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CHAPTER)) {

            ps.setInt(1, chapterId);   // bind ? to chapterId

            // ResultSet is also a resource; close it when done.
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Read each column by name — clearer than by index.
                    int    entryId     = rs.getInt("entry_id");
                    int    chapId      = rs.getInt("chapter_id");
                    String persianWord = rs.getString("persian_word");
                    String urduMeaning = rs.getString("urdu_meaning");

                    results.add(new GlossaryEntry(entryId, chapId,
                                                  persianWord, urduMeaning));
                }
            }

        } catch (SQLException e) {
            // Wrap raw JDBC exception so callers only see DataAccessException.
            throw new DataAccessException(
                    "Failed to load glossary entries for chapter " + chapterId, e);
        }

        return results;
    }

    /**
     * Insert a new glossary entry and return the database-generated entry_id.
     *
     * The entry passed in should have id=0; the real id is returned.
     *
     * SRS: FR-GL-02, NFR-RL-01, NFR-RL-02, NFR-PF-02, FR-CS-07
     *
     * @param entry a GlossaryEntry with id=0, chapterId, persianWord, urduMeaning set
     * @return the generated entry_id assigned by the database
     * @throws DataAccessException if the insert fails
     */
    @Override
    public int insert(GlossaryEntry entry) {
        // RETURN_GENERATED_KEYS tells JDBC to give us the auto-increment value.
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entry.getChapterId());
            ps.setString(2, entry.getPersianWord());   // UTF-8 safe via JDBC + DB URL
            ps.setString(3, entry.getUrduMeaning());

            ps.executeUpdate();

            // Retrieve the generated entry_id.
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            // Should never happen after a successful insert, but be safe.
            throw new DataAccessException(
                    "Insert succeeded but no generated key was returned.", null);

        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert glossary entry.", e);
        }
    }

    /**
     * Update the Persian word and Urdu meaning of an existing entry.
     *
     * The WHERE clause includes chapter_id so the update only affects an
     * entry that actually belongs to the right chapter.
     *
     * SRS: FR-GL-04, BR-06, NFR-RL-02, FR-CS-07
     *
     * @param entry the entry to update (id and chapterId identify the row)
     * @return true if one row was updated; false if no matching row was found
     * @throws DataAccessException if the database update fails
     */
    @Override
    public boolean update(GlossaryEntry entry) {
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, entry.getPersianWord());
            ps.setString(2, entry.getUrduMeaning());
            ps.setInt(3, entry.getId());
            ps.setInt(4, entry.getChapterId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;   // true = one row updated

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to update glossary entry id=" + entry.getId(), e);
        }
    }

    /**
     * Delete the entry identified by both entryId and chapterId.
     *
     * Using both columns in the WHERE clause ensures we cannot accidentally
     * delete an entry that belongs to a different chapter.
     *
     * SRS: FR-GL-05, BR-04, NFR-RL-02
     *
     * @param chapterId the chapter the entry must belong to
     * @param entryId   the entry to delete
     * @return true if one row was deleted; false if no matching row was found
     * @throws DataAccessException if the database delete fails
     */
    @Override
    public boolean delete(int chapterId, int entryId) {
        try (Connection conn = DatabaseConnection.open();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, entryId);
            ps.setInt(2, chapterId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;   // true = one row deleted

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to delete glossary entry id=" + entryId
                    + " from chapter " + chapterId, e);
        }
    }
}
