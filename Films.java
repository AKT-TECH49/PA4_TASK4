import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Films {

    // Database connection constants
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";

    
    // Method to retrieve film data and return a DefaultTableModel
    public static DefaultTableModel getFilmTableModel() {
        DefaultTableModel tableModel = new DefaultTableModel();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement()) {
            // Prepare SQL query to retrieve film data
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



    // Method to add a new film to the database
    public static void addFilm(String title, String description, int releaseYear, String language,
        int rentalDuration, double rentalRate, int length,
        double replacementCost, String rating, String specialFeatures) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO film (title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features) VALUES (?, ?, ?, (SELECT language_id FROM language WHERE name = ?), ?, ?, ?, ?, ?, ?)")) {
                // Set parameters for the prepared statement
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


    // Method to search for films based on specified criteria
    public static DefaultTableModel searchFilms(String titleKeyword, String category, String language, int releaseYear) {
        DefaultTableModel tableModel = new DefaultTableModel();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT f.*, l.name AS language_name ")
                        .append("FROM film f ")
                        .append("JOIN language l ON f.language_id = l.language_id ")
                        .append("JOIN film_category fc ON f.film_id = fc.film_id ")
                        .append("JOIN category c ON fc.category_id = c.category_id ")
                        .append("WHERE 1 = 1 "); // Placeholder for dynamic conditions
            
            List<Object> parameters = new ArrayList<>(); // List to hold query parameters
            
            // Add search criteria to the SQL query dynamically
            if (!titleKeyword.isEmpty()) {
                queryBuilder.append("AND f.title LIKE ? ");
                parameters.add("%" + titleKeyword + "%");
            }

            if (!category.isEmpty()) {
                queryBuilder.append("AND c.name = ? ");
                parameters.add(category);
            }

            if (!language.isEmpty()) {
                queryBuilder.append("AND l.name = ? ");
                parameters.add(language);
            }

            if (releaseYear != 0) {
                queryBuilder.append("AND f.release_year = ? ");
                parameters.add(releaseYear);
            }
            
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            
            // Set parameters for the prepared statement
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add columns to the table model
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            // Add rows to the table model
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

}