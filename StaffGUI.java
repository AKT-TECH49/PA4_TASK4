import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StaffGUI extends JFrame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";

    private JTable staffTable;
    private JTextField filterField;

    public StaffGUI() {
        setTitle("Staff Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(119, 136, 153)); // Sky Blue

        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.white); // White background for tabs

        // Staff tab
        JPanel staffPanel = new JPanel();
        staffPanel.setLayout(new BorderLayout());
        staffPanel.setBackground(new Color(128, 128, 128)); // Sky Blue
        staffPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(176, 226, 255)); // Sky Blue
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setForeground(Color.white); // White text
        filterField = new JTextField(20);
        JButton filterButton = new JButton("Filter");
        filterButton.setBackground(Color.white); // White button background
        filterButton.setForeground(new Color(192, 192, 192)); 
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(filterButton);

        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.white); // White background

        // Fetch data and populate table
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
                String retrieveDataSQL = "SELECT s.first_name, s.last_name, a.address, a.district, " +
                "c.city AS city_name, a.postal_code, a.phone,s.store_id,s.active " +
                "FROM staff s " +
                "JOIN address a ON s.address_id = a.address_id " +
                "JOIN city c ON a.city_id = c.city_id ";
            ResultSet resultSet = statement.executeQuery(retrieveDataSQL);
            ResultSetMetaData metaData = resultSet.getMetaData();

            DefaultTableModel tableModel = new DefaultTableModel();
            int columnCount = metaData.getColumnCount();

            // Get column names
            for (int i = 0; i < columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i + 1));
            }

            // Get data
            // Add data rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                tableModel.addRow(row);
            }

            // Create table
            staffTable = new JTable(tableModel);
            staffTable.setBackground(Color.white);
            JScrollPane scrollPane = new JScrollPane(staffTable);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        staffPanel.add(filterPanel, BorderLayout.NORTH);
        staffPanel.add(tablePanel, BorderLayout.CENTER);

        tabbedPane.addTab("Staff", staffPanel);
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StaffGUI staffGUI = new StaffGUI();
            staffGUI.setVisible(true);
        });
    }
}
