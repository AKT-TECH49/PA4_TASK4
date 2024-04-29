import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private JTable filmTable;


    public StaffGUI() {
        setTitle("Database Management");
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

        // Film tab
        JPanel filmPanel = new JPanel();
        filmPanel.setLayout(new BorderLayout());
        filmPanel.setBackground(new Color(staffPanel.getBackground().getRed(), staffPanel.getBackground().getGreen(), staffPanel.getBackground().getBlue()));
        filmPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding

        tabbedPane.addTab("Films", filmPanel);


        //tab for report :
         // Add Report tab
         JPanel reportPanel = new JPanel();
         tabbedPane.addTab("Report", reportPanel); // Add Report tab
         reportPanel.setBackground(new Color(staffPanel.getBackground().getRed(), staffPanel.getBackground().getGreen(), staffPanel.getBackground().getBlue()));
         setupReportTab(reportPanel);

         JPanel notificationsPanel = new Notifications(this).getPanel();
         tabbedPane.addTab("Notifications", notificationsPanel);

        // Add tabbed pane to the frame
        add(tabbedPane);
        // Add action listener to the Films tab
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 1) { // If Films tab is selected
                    showAddFilmPopup(); // Show the Add Film popup
                } else if (tabbedPane.getSelectedIndex() == 2) { // If Films tab is selected
                    refreshFilmTable(); // Refresh film table
                }
            }
        });

        // Setup film table initially
        setupFilmTable(filmPanel);
        setupFilmSearchPanel(filmPanel);
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

    private void showAddFilmPopup() {
        if (filmTable.getModel().getRowCount() == 0) { // Check if film table is empty
            AddFilmPopup popup = new AddFilmPopup(this); // Pass the frame as owner
            popup.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please clear the film table before adding a new film.",
                    "Film Table Not Empty", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setupFilmTable(JPanel filmPanel) {
        // Initialize the film table with an empty model
        filmTable = new JTable(new DefaultTableModel());
    
        // Create buttons
        JButton clearButton = new JButton("Clear Table");
        JButton addButton = new JButton("Add Film");
    
        // Add action listeners to the buttons
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilmTable();
            }
        });
    
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddFilmPopup();
            }
        });
    
        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
    
        // Add the buttons to the film panel
        filmPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Create a panel for the film table
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(filmTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
    
        // Add the film table panel to the film panel
        filmPanel.add(tablePanel, BorderLayout.CENTER);
    }
    
    private void clearFilmTable() {
        DefaultTableModel model = (DefaultTableModel) filmTable.getModel();
        model.setRowCount(0); // Clear the table
    }

    public void refreshFilmTable() {
        DefaultTableModel filmTableModel = Films.getFilmTableModel(); // Retrieve updated film data
        if (filmTable != null && filmTableModel != null) {
            filmTable.setModel(filmTableModel); // Set the updated model to the film table
        }
    }

    private void setupFilmSearchPanel(JPanel filmPanel) {
        // Create search components
        JLabel titleLabel = new JLabel("Title Keywords:");
        JTextField titleKeywordField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        JLabel languageLabel = new JLabel("Language:");
        JTextField languageField = new JTextField();
        JLabel releaseYearLabel = new JLabel("Release Year:");
        JTextField releaseYearField = new JTextField();
        JButton searchButton = new JButton("Search");
    
        // Add action listener to the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve search criteria from input fields
                String titleKeyword = titleKeywordField.getText();
                String category = categoryField.getText();
                String language = languageField.getText();
                int releaseYear = 0;
                try {
                    releaseYear = Integer.parseInt(releaseYearField.getText());
                } catch (NumberFormatException ex) {
                    // Handle invalid input for release year
                }
    
                // Perform search and update film table with search results
                DefaultTableModel searchResultTableModel = Films.searchFilms(titleKeyword, category, language, releaseYear);
                filmTable.setModel(searchResultTableModel);
            }
        });
    
        // Create panel for search components
        JPanel searchPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        searchPanel.add(titleLabel);
        searchPanel.add(titleKeywordField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryField);
        searchPanel.add(languageLabel);
        searchPanel.add(languageField);
        searchPanel.add(releaseYearLabel);
        searchPanel.add(releaseYearField);
        searchPanel.add(new JLabel()); // Placeholder for alignment
        searchPanel.add(searchButton);
    
        // Add the search panel to the film panel
        filmPanel.add(searchPanel, BorderLayout.SOUTH);
    }

    private void setupReportTab(JPanel reportPanel) {
        DefaultTableModel reportTableModel = ReportGenerator.generateReport();
        JTable reportTable = new JTable(reportTableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        reportPanel.setLayout(new BorderLayout());
        reportPanel.add(scrollPane, BorderLayout.CENTER);
    }
    

 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StaffGUI staffGUI = new StaffGUI();
            staffGUI.setVisible(true);
        });
    }
}
