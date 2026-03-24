# Task Manager Desktop App (Java + MySQL)

## Overview
This is a Java Swing-based desktop application for managing daily tasks with a structured dashboard interface and database integration.

The application allows users to:
- Register and log in securely  
- Add, delete, and manage tasks  
- View tasks in a dashboard  
- Track progress using a calendar view  

---

## Features

### User Authentication
- User registration with password validation:
  - Minimum 8 characters  
  - At least one uppercase letter  
  - At least one digit  
  - At least one special character (@#$&_-)  
- Login authentication using MySQL database  

---

### Task Management
- Add tasks with a specific date  
- Delete tasks  
- Mark tasks as completed  
- Tasks stored per user  

---

### Dashboard
- Weekly calendar (Monday–Sunday layout)  
- Task categorization:
  - Today's Tasks  
  - Future Tasks  
- Color-coded task status:
  - Completed  
  - Pending  

---

## Tech Stack

- Language: Java  
- GUI: Swing (AWT + Swing components)  
- Database: MySQL  
- Connectivity: JDBC  

---

## Setup Instructions

### 1. Prerequisites
- Java JDK (8 or higher)  
- MySQL Server  
- Git  

---

### 2. Database Setup

```sql
CREATE DATABASE k1;
USE k1;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100)
);

CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    task VARCHAR(255),
    dateassigned DATE,
    done BOOLEAN,
    FOREIGN KEY (username) REFERENCES users(username)
);
````

---

### 3. Configure Database Connection

Update credentials in your Java file:

```java
connection = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/k1",
    "root",
    "your_password"
);
```

---

### 4. Run the Application

```bash
javac firstproject.java
java firstproject
```

---

## How It Works

1. User launches the application and sees the login/register screen
2. After login, the dashboard is displayed
3. Tasks are fetched from MySQL and shown in:

   * Tables (today and future tasks)
   * Calendar (weekly view)
4. Any updates (add, delete, mark complete) are reflected in real time

---

## Project Structure

```
firstproject.java   # Main application file
```

---

## Known Limitations

* Passwords are stored in plain text
* UI is not responsive
* Database credentials are hardcoded
* Limited input validation

---

## Future Improvements

* Add password hashing (e.g., BCrypt)
* Improve UI using JavaFX
* Add task editing functionality
* Implement reminders or notifications
* Externalize configuration (database credentials)
* Add filtering and sorting

---

## Author

Kritika Ghosh

---

## License

This project is for academic purposes and can be modified or extended freely.

```
```
