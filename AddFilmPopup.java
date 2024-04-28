import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddFilmPopup extends JDialog {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "amantle29";

    private StaffGUI owner;

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField releaseYearField;
    private JComboBox<String> languageComboBox;
    private JTextField rentalDurationField;
    private JTextField rentalRateField;
    private JTextField lengthField;
    private JTextField replacementCostField;
    private JComboBox<String> ratingComboBox;
    private JTextField specialFeaturesField;

    public AddFilmPopup(StaffGUI owner) {
        super(owner, "Add New Film", true);
        this.owner = owner;
        setSize(400, 400);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(11, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to the panel
        panel.add(new JLabel("Title:"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        panel.add(descriptionScrollPane);

        panel.add(new JLabel("Release Year:"));
        releaseYearField = new JTextField();
        panel.add(releaseYearField);

        panel.add(new JLabel("Language:"));
        languageComboBox = new JComboBox<>();
        populateLanguageComboBox();
        panel.add(languageComboBox);

        panel.add(new JLabel("Rental Duration (days):"));
        rentalDurationField = new JTextField();
        panel.add(rentalDurationField);

        panel.add(new JLabel("Rental Rate:"));
        rentalRateField = new JTextField();
        panel.add(rentalRateField);

        panel.add(new JLabel("Length (minutes):"));
        lengthField = new JTextField();
        panel.add(lengthField);

        panel.add(new JLabel("Replacement Cost:"));
        replacementCostField = new JTextField();
        panel.add(replacementCostField);

        panel.add(new JLabel("Rating:"));
        ratingComboBox = new JComboBox<>(new String[]{"PG", "G", "NC-17", "PG-13", "R"});
        panel.add(ratingComboBox);

        panel.add(new JLabel("Special Features:"));
        specialFeaturesField = new JTextField();
        panel.add(specialFeaturesField);

        // Add button to save film
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFilm();
            }
        });
        panel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the popup
                owner.refreshFilmTable(); // Refresh film table
            }
        });
        panel.add(cancelButton);

        add(panel);
    }

    private void populateLanguageComboBox() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT name FROM language");
            while (resultSet.next()) {
                languageComboBox.addItem(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveFilm() {
        try {
            // Retrieve data from fields
            String title = titleField.getText();
            String description = descriptionArea.getText();
            int releaseYear = Integer.parseInt(releaseYearField.getText());
            String language = (String) languageComboBox.getSelectedItem(); // Assuming language names are unique
            int rentalDuration = Integer.parseInt(rentalDurationField.getText());
            double rentalRate = Double.parseDouble(rentalRateField.getText());
            int length = Integer.parseInt(lengthField.getText());
            double replacementCost = Double.parseDouble(replacementCostField.getText());
            String rating = (String) ratingComboBox.getSelectedItem();
            String specialFeatures = specialFeaturesField.getText();

            // Insert data into the database
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
            }
            // Close the popup and refresh film table
            dispose();

            owner.refreshFilmTable();


        } catch (NumberFormatException | SQLException ex) {

            JOptionPane.showMessageDialog(this, "Error saving film. Please check your input and try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StaffGUI staffGUI = new StaffGUI(); // Create an instance of StaffGUI
            AddFilmPopup popup = new AddFilmPopup(staffGUI); // Pass the instance of StaffGUI as owner
            popup.setVisible(true);
        });
    }
}
