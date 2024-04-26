import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
  
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    static final String USERNAME = "root";
    static final String PASSWORD = "amantle29";

    
    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            System.out.println("Connected to the database!");
             try (Statement statement = connection.createStatement()) {
                // Retrieve data from the table
                String retrieveDataSQL = "SELECT * FROM staff"; 
                try (ResultSet resultSet = statement.executeQuery(retrieveDataSQL)) {
                    System.out.println("Retrieving data from the table:");
                    while (resultSet.next()) {
                        int id = resultSet.getInt("staff_id");
                        String name = resultSet.getString("first_name");
                        String email = resultSet.getString("email");
                    
                        System.out.println("ID: " + id + ", Name: " + name + ", Email: " + email);
                    }
                }
            }
            
        
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
    }
}
