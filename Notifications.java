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
            
            // Create table model
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
            
            // Add rows to table model
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
            
            // Set the table model
            clientsTable.setModel(tableModel);
            
            // Add buttons panel to each row
            for (int i = 0; i < clientsTable.getRowCount(); i++) {
                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                buttonsPanel.setBackground(new Color(230, 230, 230)); // Grey-blue background color
                
                JButton addButton = new JButton("Add");
                JButton createButton = new JButton("Create");
                JButton updateButton = new JButton("Update");
                JButton refreshButton = new JButton("Refresh");
                JButton formerClientsButton = new JButton("Former Clients");
                JButton activeClientsButton = new JButton("Active Clients");
                
                // Style buttons
                addButton.setBackground(new Color(192, 192, 192)); // Grey-blue button color
                createButton.setBackground(new Color(192, 192, 192)); // Grey-blue button color
                updateButton.setBackground(new Color(192, 192, 192)); // Grey-blue button color
                refreshButton.setBackground(new Color(192, 192, 192)); // Grey-blue button color
                formerClientsButton.setBackground(new Color(192, 192, 192)); // Grey-blue button color
                activeClientsButton.setBackground(new Color(192, 192, 192)); // Grey-blue button color
                
                // Add buttons to the panel
                buttonsPanel.add(addButton);
                buttonsPanel.add(createButton);
                buttonsPanel.add(updateButton);
                buttonsPanel.add(refreshButton);
                buttonsPanel.add(formerClientsButton);
                buttonsPanel.add(activeClientsButton);
                
                // Add buttons panel to the table
                clientsTable.add(buttonsPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
