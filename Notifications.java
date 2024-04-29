import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Notifications {

    private StaffGUI parentFrame;
    private JPanel panel;
    private JTable clientsTable;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";

    public Notifications(StaffGUI parentFrame) {
        this.parentFrame = parentFrame;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        clientsTable = new JTable();

        panel.add(new JScrollPane(clientsTable), BorderLayout.CENTER);

        populateClientsTable();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void populateClientsTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT c.customer_id, c.store_id, c.first_name, c.last_name, c.email, " +
                    "c.address_id, c.active, c.create_date, c.last_update " +
                    "FROM customer c";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Customer ID");
            tableModel.addColumn("Store ID");
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");
            tableModel.addColumn("Email");
            tableModel.addColumn("Address ID");
            tableModel.addColumn("Active");
            tableModel.addColumn("Create Date");
            tableModel.addColumn("Last Update");
            while (resultSet.next()) {
                Object[] rowData = new Object[9];
                rowData[0] = resultSet.getInt("customer_id");
                rowData[1] = resultSet.getInt("store_id");
                rowData[2] = resultSet.getString("first_name");
                rowData[3] = resultSet.getString("last_name");
                rowData[4] = resultSet.getString("email");
                rowData[5] = resultSet.getInt("address_id");
                rowData[6] = resultSet.getInt("active");
                rowData[7] = resultSet.getTimestamp("create_date");
                rowData[8] = resultSet.getTimestamp("last_update");
                tableModel.addRow(rowData);
            }
            clientsTable.setModel(tableModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
