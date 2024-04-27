import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        getContentPane().setBackground(new Color(119, 136, 153)); // Light Slate Gray

        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.white); // White background for tabs

        // Staff tab
        JPanel staffPanel = new JPanel();
        staffPanel.setLayout(new BorderLayout());
        staffPanel.setBackground(new Color(128, 128, 128)); // Gray
        staffPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(176, 226, 255)); // Light Blue
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setForeground(Color.white); // White text
        filterField = new JTextField(20);
        JButton filterButton = new JButton("Filter");
        filterButton.setBackground(Color.white); // White button background
        filterButton.setForeground(new Color(192, 192, 192)); // Silver
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(filterButton);

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTableWithFilter();
            }
        });

        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(Color.white); // White background
        populateTable(); // Method to populate table initially
        JScrollPane scrollPane = new JScrollPane(staffTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        staffPanel.add(filterPanel, BorderLayout.NORTH);
        staffPanel.add(tablePanel, BorderLayout.CENTER);

        tabbedPane.addTab("Staff", staffPanel);
        add(tabbedPane);
    }

    private void populateTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            String query = "SELECT s.first_name, s.last_name, a.address, a.district, " +
                "c.city AS city_name, a.postal_code, a.phone, s.store_id, s.active " +
                "FROM staff s " +
                "JOIN address a ON s.address_id = a.address_id " +
                "JOIN city c ON a.city_id = c.city_id";
            ResultSet rs = statement.executeQuery(query);
            DefaultTableModel tableModel = new DefaultTableModel();
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
            staffTable = new JTable(tableModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTableWithFilter() {
        String filterText = filterField.getText();
        if (filterText.isEmpty()) {
            filterText = "%"; 
        } else {
            filterText = "%" + filterText + "%"; 
        }

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement ps = connection.prepareStatement("SELECT s.first_name, s.last_name, a.address, a.district, " +
                    "c.city AS city_name, a.postal_code, a.phone, s.store_id, s.active " +
                    "FROM staff s " +
                    "JOIN address a ON s.address_id = a.address_id " +
                    "JOIN city c ON a.city_id = c.city_id " +
                    "WHERE s.first_name LIKE ? OR s.last_name LIKE ? OR a.address LIKE ?")) {
            ps.setString(1, filterText);
            ps.setString(2, filterText);
            ps.setString(3, filterText);

            ResultSet rs = ps.executeQuery();
            DefaultTableModel tableModel = (DefaultTableModel) staffTable.getModel();
            tableModel.setRowCount(0); // clear existing data

            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StaffGUI staffGUI = new StaffGUI();
            staffGUI.setVisible(true);
        });
    }
}
