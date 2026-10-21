package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.ChapterDAO;
import edu.persianlearningapp.dao.DataAccessException;
import edu.persianlearningapp.domain.Chapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySQLChapterDAO implements ChapterDAO {

    private static final String INSERT_CHAPTER =
            "INSERT INTO chapters (title) VALUES (?)";

    private static final String FIND_ALL_CHAPTERS =
            "SELECT chapter_id, title, created_at, updated_at "
            + "FROM chapters ORDER BY chapter_id";

    private static final String FIND_CHAPTER_BY_ID =
            "SELECT chapter_id, title, created_at, updated_at "
            + "FROM chapters WHERE chapter_id = ?";

    private static final String UPDATE_CHAPTER_TITLE =
            "UPDATE chapters SET title = ? WHERE chapter_id = ?";

    private static final String FIND_CHAPTER_EXISTS =
            "SELECT 1 FROM chapters WHERE chapter_id = ?";

    private static final String DELETE_CHAPTER =
            "DELETE FROM chapters WHERE chapter_id = ?";

    @Override
    public int insert(Chapter chapter) {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_CHAPTER, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, chapter.getTitle());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }

                throw new DataAccessException(
                        "The database did not return the new chapter id.",
                        new SQLException("No generated key was returned."));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not create the chapter.", e);
        }
    }

    @Override
    public List<Chapter> findAll() {
        List<Chapter> chapters = new ArrayList<Chapter>();

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_ALL_CHAPTERS);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                chapters.add(readChapter(results));
            }
            return chapters;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load chapters.", e);
        }
    }

    @Override
    public Chapter findById(int chapterId) {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_CHAPTER_BY_ID)) {

            statement.setInt(1, chapterId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return readChapter(results);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the chapter.", e);
        }
    }

    @Override
    public boolean updateTitle(int chapterId, String title) {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE_CHAPTER_TITLE)) {

            statement.setString(1, title);
            statement.setInt(2, chapterId);

            int updatedRows = statement.executeUpdate();

            // Also return true if the title was unchanged but the chapter exists.
            return updatedRows > 0 || chapterExists(chapterId);
        } catch (SQLException e) {
            throw new DataAccessException("Could not update the chapter.", e);
        }
    }

    @Override
    public boolean deleteById(int chapterId) {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement =
                     connection.prepareStatement(DELETE_CHAPTER)) {

            statement.setInt(1, chapterId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete the chapter.", e);
        }
    }

    private boolean chapterExists(int chapterId) {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_CHAPTER_EXISTS)) {

            statement.setInt(1, chapterId);

            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Could not check whether the chapter exists.", e);
        }
    }

    @Override
    public boolean existsByTitleExceptId(
            String title, int excludedChapterId) {

        String sql = "SELECT 1 FROM chapters "
                + "WHERE title = ? AND chapter_id <> ? LIMIT 1";

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setInt(2, excludedChapterId);

            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Could not check the chapter title.", e);
        }
    }
    private Chapter readChapter(ResultSet results) throws SQLException {
        Chapter chapter = new Chapter(
                results.getInt("chapter_id"),
                results.getString("title"));

        chapter.setCreatedAt(results.getTimestamp("created_at"));
        chapter.setUpdatedAt(results.getTimestamp("updated_at"));

        return chapter;
    }
}