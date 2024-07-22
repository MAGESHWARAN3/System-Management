CREATE DATABASE IF NOT EXISTS computer_system;
USE computer_system;
CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('user', 'admin') NOT NULL,
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
CREATE TABLE reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    computer_id VARCHAR(50) NOT NULL,
    issue_description TEXT NOT NULL,
    status ENUM('pending', 'in progress', 'repaired') DEFAULT 'pending',
    department_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
CREATE TABLE maintenance (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    report_id INT NOT NULL,
    assigned_to VARCHAR(50) NOT NULL,
    maintenance_status ENUM('assigned', 'in progress', 'completed') DEFAULT 'assigned',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES reports(id)
); 

