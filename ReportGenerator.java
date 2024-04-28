import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ReportGenerator {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";

    public static DefaultTableModel generateReport() {
        DefaultTableModel tableModel = new DefaultTableModel();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            // Prepare SQL query to get the report data
            String query = "SELECT s.store_id,c.name AS genre, COUNT(*) AS movie_count " +
                           "FROM inventory i " +
                           "JOIN store s ON i.store_id = s.store_id " +
                           "JOIN film_category fc ON i.film_id = fc.film_id " +
                           "JOIN category c ON fc.category_id = c.category_id " +
                           "GROUP BY s.store_id, c.category_id";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            // Get metadata
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

    public static void main(String[] args) {
        DefaultTableModel reportTableModel = generateReport();
        // Display or use the report table model as needed
    }
}
