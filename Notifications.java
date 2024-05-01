import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Notifications {
    // Database connection constants
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";


    // Parent frame reference
    private MainGUI parentFrame;

    // UI components
    private JPanel panel;
    private JTable clientsTable;
    private JScrollPane scrollPane;
    private JPanel buttonsPanel;


    // Constructor
    public Notifications(MainGUI parentFrame) {
        this.parentFrame = parentFrame;
        panel = new JPanel(new BorderLayout());

        clientsTable = new JTable();
        scrollPane = new JScrollPane(clientsTable);
        scrollPane.setPreferredSize(new Dimension(600, 150)); 

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        initializeButtons(); // Initialize buttons

        panel.add(scrollPane, BorderLayout.CENTER); 
        panel.add(buttonsPanel, BorderLayout.SOUTH); 

        populateClientsTable(); // Populate clients table
    }



    // Method to retrieve the panel
    public JPanel getPanel() {
        return panel;
    }



    // Method to populate the clients table
    private void populateClientsTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            connection.setAutoCommit(false); // transaction
            
            // Get customer table data
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
            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to initialize buttons
    private void initializeButtons() {
        buttonsPanel.setBackground(new Color(230, 230, 230)); 
    
        // Create buttons
        JButton createButton = new JButton("Create");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");
        JButton formerClientsButton = new JButton("Former Clients");
        JButton activeClientsButton = new JButton("Active Clients");
    
        // Add action listeners to the buttons
        createButton.addActionListener(e -> {
            JPanel addPanel = new JPanel(new GridLayout(9, 2));

        JTextField cust_id = new JTextField(10);
        JTextField sto_id = new JTextField(10);
        JTextField fName = new JTextField(10);
        JTextField lName = new JTextField(10);
        JTextField Custemail = new JTextField(10);
        JTextField id_addre = new JTextField(10);
        JTextField custActive = new JTextField(10);
        JTextField dateCreated = new JTextField(10);
        JTextField lastUp = new JTextField(10);

        addPanel.add(new JLabel("Customer ID (must be an integer)"));
        addPanel.add(cust_id);
        addPanel.add(new JLabel("Store ID"));
        addPanel.add(sto_id);
        addPanel.add(new JLabel("First Name"));
        addPanel.add(fName);
        addPanel.add(new JLabel("Last Name"));
        addPanel.add(lName);
        addPanel.add(new JLabel("Customer Email"));
        addPanel.add(Custemail);
        addPanel.add(new JLabel("Address ID"));
        addPanel.add(id_addre);
        addPanel.add(new JLabel("Active (0 or 1)"));
        addPanel.add(custActive);

        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        dateCreated.setText(currentTimeStamp.toString());
        lastUp.setText(currentTimeStamp.toString());

        int val = JOptionPane.showConfirmDialog(panel, addPanel, "Add New Customer", JOptionPane.OK_CANCEL_OPTION);
        
        if (val == JOptionPane.OK_OPTION) {
            try {
                int customerID = Integer.parseInt(cust_id.getText().trim());
                byte storeID = Byte.parseByte(sto_id.getText().trim());
                String firstName = fName.getText().trim();
                String lastName = lName.getText().trim();
                String email = Custemail.getText().trim();
                int addressID = Integer.parseInt(id_addre.getText().trim());
                byte active = Byte.parseByte(custActive.getText().trim());
                String createDate = dateCreated.getText().trim();
                String lastUpdate = lastUp.getText().trim();

                // Create a SQL query to insert the new customer
                String query = "INSERT INTO customer (customer_id, store_id, first_name, last_name, email, address_id, active, create_date, last_update) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setInt(1, customerID);
                    preparedStatement.setByte(2, storeID);
                    preparedStatement.setString(3, firstName);
                    preparedStatement.setString(4, lastName);
                    preparedStatement.setString(5, email);
                    preparedStatement.setInt(6, addressID);
                    preparedStatement.setByte(7, active);
                    preparedStatement.setString(8, createDate);
                    preparedStatement.setString(9, lastUpdate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(panel, "Customer added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        populateClientsTable(); // Refresh the table to reflect the new customer
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to add customer", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please enter valid data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });



        updateButton.addActionListener(e -> {
            // Get the selected row index
            int selectedRow = clientsTable.getSelectedRow();
            
            // Check if a row is selected
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a client to update.", "Update Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get the selected client's data from the table
            int customerID = (int) clientsTable.getValueAt(selectedRow, 0);
            int storeID = (int) clientsTable.getValueAt(selectedRow, 1);
            String firstName = (String) clientsTable.getValueAt(selectedRow, 2);
            String lastName = (String) clientsTable.getValueAt(selectedRow, 3);
            String email = (String) clientsTable.getValueAt(selectedRow, 4);
            int addressID = (int) clientsTable.getValueAt(selectedRow, 5);
            int active = (int) clientsTable.getValueAt(selectedRow, 6);

            // Create a panel for updating client information
            JPanel updatePanel = new JPanel(new GridLayout(8, 2));

            JTextField fName = new JTextField(firstName, 10);
            JTextField lName = new JTextField(lastName, 10);
            JTextField Custemail = new JTextField(email, 10);
            JTextField custActive = new JTextField(String.valueOf(active), 10);

            updatePanel.add(new JLabel("First Name"));
            updatePanel.add(fName);
            updatePanel.add(new JLabel("Last Name"));
            updatePanel.add(lName);
            updatePanel.add(new JLabel("Customer Email"));
            updatePanel.add(Custemail);
            updatePanel.add(new JLabel("Active (0 or 1)"));
            updatePanel.add(custActive);

            int option = JOptionPane.showConfirmDialog(panel, updatePanel, "Update Client", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                    String updateQuery = "UPDATE customer SET first_name = ?, last_name = ?, email = ?, active = ? WHERE customer_id = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

                    updateStatement.setString(1, fName.getText());
                    updateStatement.setString(2, lName.getText());
                    updateStatement.setString(3, Custemail.getText());
                    updateStatement.setInt(4, Integer.parseInt(custActive.getText()));
                    updateStatement.setInt(5, customerID);

                    int rowsAffected = updateStatement.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(panel, "Client updated successfully.", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                        populateClientsTable(); // Refresh the table to reflect the changes
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to update client.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        

        
        deleteButton.addActionListener(e-> {
        // Get the selected row index
        int selectedRow = clientsTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Please select a client to delete.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the selected client's ID
        int customerID = (int) clientsTable.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this client?", "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                String deleteQuery = "DELETE FROM customer WHERE customer_id = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, customerID);

                int rowsAffected = deleteStatement.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(panel, "Client deleted successfully.", "Delete Success", JOptionPane.INFORMATION_MESSAGE);
                    populateClientsTable(); // Refresh the table to reflect the changes
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to delete client.", "Delete Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });


        
        refreshButton.addActionListener(e -> {
            populateClientsTable();
        });
        


        formerClientsButton.addActionListener(e -> {

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                String query = "SELECT c.customer_id, c.store_id, c.first_name, c.last_name, c.email, " +
                    "c.address_id, c.active, c.create_date, c.last_update " +
                    "FROM customer c WHERE c.active = '0'";
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
                
                clientsTable.setModel(tableModel);
            } catch (SQLException p) {
                    p.printStackTrace();
            }
        });
        


        activeClientsButton.addActionListener(e -> {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                String query = "SELECT c.customer_id, c.store_id, c.first_name, c.last_name, c.email, " +
                    "c.address_id, c.active, c.create_date, c.last_update " +
                    "FROM customer c WHERE c.active = '1'";
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
        
                clientsTable.setModel(tableModel);
            } catch (SQLException p) {
                    p.printStackTrace();
            }
        });



        buttonsPanel.add(createButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(formerClientsButton);
        buttonsPanel.add(activeClientsButton);
    }
    
}