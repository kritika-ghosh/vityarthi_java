import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;


public class firstproject {
    private static String loggedInUsername = null; // Store the logged-in username
    private static Connection connection;
    private static JFrame dboardFrame;
    private static Map<String, JPanel> dayPanels = new HashMap<>(); // Store panels for each day
    private static DefaultTableModel todayTableModel;
    private static DefaultTableModel futureTableModel;

    public static void main(String[] args) {
        // MySQL connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/k1", "root", "keya_2006");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load and resize the image
        ImageIcon img = new ImageIcon("D:\\Desktop\\firstproject_java\\firstproject\\usericon.png");
        Image scaledImage = img.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Create a label for the icon
        JLabel label = new JLabel(scaledIcon);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(150, 50, 200, 200);

        // Create a text field for user id
        JTextField entuser = new JTextField("  enter username");
        entuser.setBackground(Color.GRAY);
        entuser.setBounds(100, 300, 300, 30);

        // Create a text field for user password
        JTextField entpsw = new JTextField("  enter password");
        entpsw.setBackground(Color.GRAY);
        entpsw.setBounds(100, 360, 300, 30);

        // Create a button for submission
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 420, 100, 30);

        // Create a button for sign in
        JButton signInButton = new JButton("Sign In");
        signInButton.setBounds(300, 420, 100, 30);

        // Create a welcome label
        JLabel label3 = new JLabel("WELCOME!!");
        label3.setFont(new Font("times new roman", Font.BOLD, 20));
        label3.setBounds(425, 15, 150, 40);

        // Create calendar panel
        JPanel calendarPanel = new JPanel(new GridLayout(1, 7));
        calendarPanel.setBounds(250, 70, 500, 375);
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
        for (String day : days) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            Date date = calendar.getTime();
            JLabel dayLabel = new JLabel(day + " - " + dateFormat.format(date), SwingConstants.CENTER);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dayPanel.add(dayLabel, BorderLayout.NORTH);
            dayPanel.setBackground(Color.GRAY);
            JPanel eventsArea = new JPanel();
            eventsArea.setLayout(new BoxLayout(eventsArea, BoxLayout.Y_AXIS)); // Allow multiple tasks
            eventsArea.setBackground(Color.GRAY);
            eventsArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dayPanel.add(eventsArea, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
            dayPanels.put(dateFormat.format(date), eventsArea); // Store the panel for this date
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Create and set up the login frame
        JFrame loginFrame = new JFrame("My App");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(500, 700);

        // Create new frame for dashboard
        dboardFrame = new JFrame();
        dboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dboardFrame.setSize(1000, 500);

        registerButton.addActionListener(e -> {
            String username = entuser.getText().trim();
            String password = entpsw.getText().trim();

            // Password validation
            int valid = 0;

            // Check password length
            if (password.length() >= 8) valid++;

            // Check for at least one uppercase letter
            if (!password.equals(password.toLowerCase())) valid++;

            // Check for special characters
            if (password.matches(".*[@#$&_\\-].*")) valid++;

            // Check for at least one digit
            if (password.matches(".*\\d.*")) valid++;

            if (valid == 4) {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "User registered successfully!");
                    loginFrame.dispose();
                    dboardFrame.setVisible(true);
                    loggedInUsername = username;
                    createDashboardPanel();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error registering user. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Password does not meet the criteria. Please try again.");
            }
        });

        signInButton.addActionListener(e -> {
            String username = entuser.getText().trim();
            String password = entpsw.getText().trim();

            String query2 = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query2)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    loggedInUsername = username;
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    loginFrame.dispose();
                    dboardFrame.setVisible(true);
                    createDashboardPanel();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error logging in. Please try again.");
            }
        });

        // Add components to the login panel
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.GRAY);
        panel.setPreferredSize(new Dimension(500, 700));
        panel.add(label);
        panel.add(entuser);
        panel.add(entpsw);
        panel.add(registerButton);
        panel.add(signInButton);
        loginFrame.add(panel);

        // Show the login frame
        loginFrame.setVisible(true);
    }

    private static void refreshTodayTasks() {
        // Clear the existing rows in the today table
        todayTableModel.setRowCount(0);

        // Query today's tasks and add them to the today table model
        String query5 = "SELECT task, done FROM tasks WHERE username=? AND dateassigned=CURDATE()";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query5);
            preparedStatement.setString(1, loggedInUsername);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String task = rs.getString("task");
                boolean done = rs.getBoolean("done");
                todayTableModel.addRow(new Object[]{task, done});
            }
            preparedStatement.close();
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void refreshFutureTasks() {
        // Clear the existing rows in the future table
        futureTableModel.setRowCount(0);

        // Query future tasks and add them to the future table model
        String query6 = "SELECT task, done, dateassigned FROM tasks WHERE username=? AND dateassigned > CURDATE()";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query6);
            preparedStatement.setString(1, loggedInUsername);
            ResultSet rs = preparedStatement.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
            while (rs.next()) {
                String task = rs.getString("task");
                boolean done = rs.getBoolean("done");
                futureTableModel.addRow(new Object[]{task, done});
            }
            preparedStatement.close();
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private static void refreshCalendar() {
        // Clear the existing tasks from the calendar panels
        for (JPanel eventsArea : dayPanels.values()) {
            eventsArea.removeAll();
        }
    
        // Query tasks and add them to the correct day panels
        String query = "SELECT task, done, dateassigned FROM tasks WHERE username=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, loggedInUsername);
            ResultSet rs = preparedStatement.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
            while (rs.next()) {
                String task = rs.getString("task");
                boolean done = rs.getBoolean("done");
                Date dateAssigned = rs.getDate("dateassigned");
                String dateStr = dateFormat.format(dateAssigned);
                JPanel eventsArea = dayPanels.get(dateStr);
                if (eventsArea != null) {
                    JLabel taskLabel = new JLabel(task);
                    taskLabel.setForeground(done ? Color.GREEN : Color.RED);
                    eventsArea.add(taskLabel);
                    eventsArea.revalidate();
                    eventsArea.repaint();
                }
            }
            rs.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error refreshing calendar.");
        }
    }
    

    private static void createDashboardPanel() {
        // Create and configure the frame
        dboardFrame.setLayout(null);
    
        // Initialize table models and tables
        String[] columnNames1 = {"Task", "Done"};
        DefaultTableModel tableModel1 = new DefaultTableModel(columnNames1, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        JTable tdJTable1 = new JTable(tableModel1);
        tdJTable1.setPreferredScrollableViewportSize(new Dimension(200, 150));
        TableColumnModel columnModel1 = tdJTable1.getColumnModel();
        columnModel1.getColumn(0).setPreferredWidth(150);
        columnModel1.getColumn(1).setPreferredWidth(50);
    
        // Assign the table model to futureTableModel
        futureTableModel = tableModel1;
    
        // Add TableModelListener to update the database and refresh the calendar when value changes
        tableModel1.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 1) {
                boolean newValue = (Boolean) tableModel1.getValueAt(row, column);
                String task = (String) tableModel1.getValueAt(row, 0);
    
                String updateQuery = "UPDATE tasks SET done = ? WHERE username = ? AND task = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setBoolean(1, newValue);
                    preparedStatement.setString(2, loggedInUsername);
                    preparedStatement.setString(3, task);
                    preparedStatement.executeUpdate();
                    refreshCalendar();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating task status.");
                }
            }
        });
    
        // Initialize the second table model and table
        String[] columnNames2 = {"Task", "Done"};
        DefaultTableModel tableModel2 = new DefaultTableModel(columnNames2, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        JTable tdJTable2 = new JTable(tableModel2);
        tdJTable2.setPreferredScrollableViewportSize(new Dimension(200, 150));
        TableColumnModel columnModel2 = tdJTable2.getColumnModel();
        columnModel2.getColumn(0).setPreferredWidth(150);
        columnModel2.getColumn(1).setPreferredWidth(50);
    
        // Assign the table model to todayTableModel
        todayTableModel = tableModel2;
    
        // Add TableModelListener to update the database and refresh the calendar when value changes for the second table
        tableModel2.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 1) {
                boolean newValue = (Boolean) tableModel2.getValueAt(row, column);
                String task = (String) tableModel2.getValueAt(row, 0);
                String updateQuery = "UPDATE tasks SET done = ? WHERE username = ? AND task = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setBoolean(1, newValue);
                    preparedStatement.setString(2, loggedInUsername);
                    preparedStatement.setString(3, task);
                    preparedStatement.executeUpdate();
                    refreshCalendar();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating task status.");
                }
            }
        });
    
        // Create and populate the calendar panel
        JPanel calendarPanel = new JPanel(new GridLayout(1, 7));
        calendarPanel.setBounds(250, 70, 500, 375);
    
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
        for (String day : days) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            Date date = calendar.getTime();
            JLabel dayLabel = new JLabel(day + " - " + dateFormat.format(date), SwingConstants.CENTER);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dayPanel.add(dayLabel, BorderLayout.NORTH);
            dayPanel.setBackground(Color.GRAY);
            JPanel eventsArea = new JPanel();
            eventsArea.setLayout(new BoxLayout(eventsArea, BoxLayout.Y_AXIS));
            eventsArea.setBackground(Color.GRAY);
            eventsArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            dayPanel.add(eventsArea, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
            dayPanels.put(dateFormat.format(date), eventsArea);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    
        // Populate table and calendar with initial data
        String query1 = "SELECT task, done, dateassigned FROM tasks WHERE username=? AND dateassigned > CURDATE() AND NOT dateassigned = CURDATE()";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setString(1, loggedInUsername);
            ResultSet rs1 = preparedStatement1.executeQuery();
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd");
            while (rs1.next()) {
                String task = rs1.getString("task");
                boolean done = rs1.getBoolean("done");
                Date dateAssigned = rs1.getDate("dateassigned");
                String dateStr = dateFormat1.format(dateAssigned);
                tableModel1.addRow(new Object[]{task, done});
    
                JPanel eventsArea = dayPanels.get(dateStr);
                if (eventsArea != null) {
                    JLabel taskLabel = new JLabel(task);
                    taskLabel.setForeground(done ? Color.GREEN : Color.RED);
                    eventsArea.add(taskLabel);
                    eventsArea.revalidate();
                    eventsArea.repaint();
                }
            }
            rs1.close();
            preparedStatement1.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong");
        }
    
        // Populate the second table
        String query2 = "SELECT task, done FROM tasks WHERE username=? AND dateassigned = CURDATE()";
        try {
            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setString(1, loggedInUsername);
            ResultSet rs2 = preparedStatement2.executeQuery();
            while (rs2.next()) {
                String task = rs2.getString("task");
                boolean done = rs2.getBoolean("done");
                tableModel2.addRow(new Object[]{task, done});
            }
            rs2.close();
            preparedStatement2.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Something went wrong");
        }
    
        // Create today panel
        JPanel tdPanel = new JPanel(null);
        tdPanel.setBackground(Color.GRAY);
        tdPanel.setBounds(25, 70, 200, 375);
    
        JLabel label4 = new JLabel("Future Tasks");
        label4.setFont(new Font("Times New Roman", Font.BOLD, 12));
        label4.setBounds(0, 0, 200, 15);
        tdPanel.add(label4);
    
        JScrollPane scrollPane1 = new JScrollPane(tdJTable1);
        scrollPane1.setBounds(0, 15, 200, 180);
        tdPanel.add(scrollPane1);
    
        JLabel label5 = new JLabel("Today's Tasks");
        label5.setFont(new Font("Times New Roman", Font.BOLD, 12));
        label5.setBounds(0, 195, 200, 15);
        tdPanel.add(label5);
    
        JScrollPane scrollPane2 = new JScrollPane(tdJTable2);
        scrollPane2.setBounds(0, 210, 200, 180);
        tdPanel.add(scrollPane2);
    
        // Create input fields and buttons for task management
        JTextField addTaskField = new JTextField("Enter a task");
        JTextField addDateField = new JTextField("Enter date yyyy-mm-dd");
    
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String addTask = addTaskField.getText();
            String addDate = addDateField.getText();
            String query4 = "INSERT INTO tasks (username, task, dateassigned, done) VALUES (?, ?, ?, FALSE)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query4);
                preparedStatement.setString(1, loggedInUsername);
                preparedStatement.setString(2, addTask);
                preparedStatement.setString(3, addDate);
                preparedStatement.executeUpdate();
                refreshCalendar();
                refreshFutureTasks();
                refreshTodayTasks();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding task.");
            }
        });
    
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            String deleteTask = addTaskField.getText();
            String query5 = "DELETE FROM tasks WHERE username=? AND task=?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query5);
                preparedStatement.setString(1, loggedInUsername);
                preparedStatement.setString(2, deleteTask);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    refreshCalendar();
                    refreshFutureTasks();
                    refreshTodayTasks();
                    JOptionPane.showMessageDialog(null, "Task deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Task not found.");
                }
                preparedStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting task.");
            }
        });
        
    
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.GRAY);
        controlPanel.setPreferredSize(new Dimension(200, 500));
        controlPanel.setBounds(800, 70, 200, 500);
        controlPanel.setLayout(null);
        addTaskField.setBounds(10, 10, 110, 30);
        addDateField.setBounds(10, 50, 110, 30);
        addButton.setBounds(10, 90, 110, 30);
        deleteButton.setBounds(10, 130, 110, 30);
        controlPanel.add(addTaskField);
        controlPanel.add(addDateField);
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
    
        // Create a panel with null layout for dashboard
        JPanel panel2 = new JPanel(null);
        panel2.setBackground(Color.GRAY);
        panel2.setPreferredSize(new Dimension(1000, 500));
        panel2.setBounds(0, 0, 1000, 500);
    
        JLabel welcomeLabel = new JLabel("Welcome! " + loggedInUsername);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        welcomeLabel.setBounds(425, 15, 250, 40);
    
        // Add components to the dashboard panel
        panel2.add(welcomeLabel);
        panel2.add(calendarPanel);
        panel2.add(tdPanel);
        dboardFrame.add(controlPanel);
        dboardFrame.add(panel2);
    
        // Ensure the frame is visible and update layout
        dboardFrame.setSize(1000, 500); // Adjust as needed
        dboardFrame.setVisible(true);
        dboardFrame.revalidate();
        dboardFrame.repaint();
    }
}