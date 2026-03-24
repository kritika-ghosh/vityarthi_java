import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class firstproject {

    private static Connection connection;

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

        JFrame frame = new JFrame("My App");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        // 🧠 REGISTER LOGIC
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
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error registering user.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Password does not meet criteria.");
            }
        });

        panel.add(label);
        panel.add(entuser);
        panel.add(entpsw);
        panel.add(registerButton);
        panel.add(signInButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}