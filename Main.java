public class Main {
  
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/u23618583_u23539764_sakila";
    static final String USERNAME = "root";
    static final String PASSWORD = "amantle29";

    public static void main(String[] args) {
        MainGUI MainGUI = new MainGUI();

        javax.swing.SwingUtilities.invokeLater(() -> {
            MainGUI.setVisible(true);
        });
    }
}