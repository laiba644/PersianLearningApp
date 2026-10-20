
package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.ComponentDAO;
import edu.persianlearningapp.dao.DataAccessException;
import edu.persianlearningapp.domain.ChapterComponent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLComponentDAO implements ComponentDAO {

    @Override
    public List<ChapterComponent> findComponentsByChapterId(int chapterId) {
        String sql =
                "SELECT component_id, chapter_id, component_type, display_order "
              + "FROM chapter_components "
              + "WHERE chapter_id = ? "
              + "ORDER BY display_order ASC";

        List<ChapterComponent> components =
                new ArrayList<ChapterComponent>();

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, chapterId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    components.add(new ChapterComponent(
                            result.getInt("component_id"),
                            result.getInt("chapter_id"),
                            result.getString("component_type"),
                            result.getInt("display_order")
                    ));
                }
            }

            return components;

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Unable to load chapter structure.", e);
        }
    }

    @Override
    public void replaceStructure(
            int chapterId,
            List<ChapterComponent> components) {

        if (chapterId <= 0) {
            throw new IllegalArgumentException(
                    "Chapter id must be positive.");
        }

        if (components == null) {
            throw new IllegalArgumentException(
                    "Components list is required.");
        }

        String selectSql =
                "SELECT component_id, chapter_id, component_type, display_order "
              + "FROM chapter_components WHERE chapter_id = ?";

        String shiftSql =
                "UPDATE chapter_components "
              + "SET display_order = -component_id WHERE chapter_id = ?";

        String deleteSql =
                "DELETE FROM chapter_components "
              + "WHERE chapter_id = ? AND component_type = ?";

        String insertSql =
                "INSERT INTO chapter_components "
              + "(chapter_id, component_type, display_order) "
              + "VALUES (?, ?, ?)";

        String updateOrderSql =
                "UPDATE chapter_components "
              + "SET display_order = ? "
              + "WHERE chapter_id = ? AND component_type = ?";

        try (Connection connection = DatabaseConnection.open()) {

            boolean originalAutoCommit = connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                Map<String, ChapterComponent> existing =
                        new HashMap<String, ChapterComponent>();

                try (PreparedStatement statement =
                             connection.prepareStatement(selectSql)) {

                    statement.setInt(1, chapterId);

                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            ChapterComponent component =
                                    new ChapterComponent(
                                            result.getInt("component_id"),
                                            result.getInt("chapter_id"),
                                            result.getString("component_type"),
                                            result.getInt("display_order")
                                    );

                            existing.put(
                                    component.getComponentType(), component);
                        }
                    }
                }

                Set<String> requestedTypes = new HashSet<String>();

                for (ChapterComponent component : components) {
                    if (component == null
                            || component.getComponentType() == null
                            || component.getDisplayOrder() <= 0) {
                        throw new IllegalArgumentException(
                                "Invalid chapter component.");
                    }

                    if (!requestedTypes.add(component.getComponentType())) {
                        throw new IllegalArgumentException(
                                "Duplicate component type: "
                                + component.getComponentType());
                    }
                }

                // Move existing orders out of the way before assigning new ones.
                try (PreparedStatement statement =
                             connection.prepareStatement(shiftSql)) {
                    statement.setInt(1, chapterId);
                    statement.executeUpdate();
                }

                // Delete only removed types. Their dependent content is
                // deleted by the database's ON DELETE CASCADE constraint.
                try (PreparedStatement statement =
                             connection.prepareStatement(deleteSql)) {
                    for (String existingType : existing.keySet()) {
                        if (!requestedTypes.contains(existingType)) {
                            statement.setInt(1, chapterId);
                            statement.setString(2, existingType);
                            statement.addBatch();
                        }
                    }
                    statement.executeBatch();
                }

                // Insert only new types. Existing component IDs are preserved.
                try (PreparedStatement statement =
                             connection.prepareStatement(insertSql)) {
                    int temporaryOrder = Integer.MIN_VALUE;

                    for (ChapterComponent component : components) {
                        if (!existing.containsKey(
                                component.getComponentType())) {

                            statement.setInt(1, chapterId);
                            statement.setString(
                                    2, component.getComponentType());
                            statement.setInt(3, temporaryOrder++);
                            statement.addBatch();
                        }
                    }

                    statement.executeBatch();
                }

                // Apply the final display order.
                try (PreparedStatement statement =
                             connection.prepareStatement(updateOrderSql)) {
                    for (ChapterComponent component : components) {
                        statement.setInt(
                                1, component.getDisplayOrder());
                        statement.setInt(2, chapterId);
                        statement.setString(
                                3, component.getComponentType());
                        statement.addBatch();
                    }

                    statement.executeBatch();
                }

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;

            } finally {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {
                    // The connection is about to close.
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Unable to save chapter structure.", e);
        }
    }
}
