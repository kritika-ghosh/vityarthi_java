import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class firstproject {

    private static Connection connection;
    private static String loggedInUsername = null;

    public static void main(String[] args) {

        // 🔌 Database connection
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/k1",
                "root",
                "your_password_here"
            );
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(500, 700);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.GRAY);

        JTextField entuser = new JTextField("  enter username");
        entuser.setBounds(100, 300, 300, 30);

        JTextField entpsw = new JTextField("  enter password");
        entpsw.setBounds(100, 360, 300, 30);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 420, 100, 30);

        JButton signInButton = new JButton("Sign In");
        signInButton.setBounds(300, 420, 100, 30);

        JLabel label = new JLabel("WELCOME!!");
        label.setFont(new Font("Times New Roman", Font.BOLD, 20));
        label.setBounds(180, 200, 200, 40);

        // 🧠 REGISTER LOGIC (same as before)
        registerButton.addActionListener(e -> {
            String username = entuser.getText().trim();
            String password = entpsw.getText().trim();

            int valid = 0;

            if (password.length() >= 8) valid++;
            if (!password.equals(password.toLowerCase())) valid++;
            if (password.matches(".*[@#$&_\\-].*")) valid++;
            if (password.matches(".*\\d.*")) valid++;

            if (valid == 4) {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";

                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(null, "User registered successfully!");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error registering user.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Password does not meet criteria.");
            }
        });

        // 🔐 LOGIN LOGIC
        signInButton.addActionListener(e -> {
            String username = entuser.getText().trim();
            String password = entpsw.getText().trim();

            String query = "SELECT * FROM users WHERE username=? AND password=?";

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    loggedInUsername = username;

                    JOptionPane.showMessageDialog(null, "Login successful!");

                    loginFrame.dispose();
                    openDashboard();

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Login error.");
            }
        });

        panel.add(label);
        panel.add(entuser);
        panel.add(entpsw);
        panel.add(registerButton);
        panel.add(signInButton);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    // 🧱 Basic dashboard (placeholder)
    private static void openDashboard() {
        JFrame dash = new JFrame("Dashboard");
        dash.setSize(600, 400);
        dash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel welcome = new JLabel("Welcome, " + loggedInUsername);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        dash.add(welcome);
        dash.setVisible(true);
    }
}