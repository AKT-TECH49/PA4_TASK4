import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Films {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";

    public static DefaultTableModel getFilmTableModel() {
        DefaultTableModel tableModel = new DefaultTableModel();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "SELECT f.*, l.name AS language_name " +
                    "FROM film f " +
                    "JOIN language l ON f.language_id = l.language_id";
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableModel;
    }

    public static void addFilm(String title, String description, int releaseYear, String language,
                               int rentalDuration, double rentalRate, int length,
                               double replacementCost, String rating, String specialFeatures) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO film (title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features) VALUES (?, ?, ?, (SELECT language_id FROM language WHERE name = ?), ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, releaseYear);
            preparedStatement.setString(4, language);
            preparedStatement.setInt(5, rentalDuration);
            preparedStatement.setDouble(6, rentalRate);
            preparedStatement.setInt(7, length);
            preparedStatement.setDouble(8, replacementCost);
            preparedStatement.setString(9, rating);
            preparedStatement.setString(10, specialFeatures);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
