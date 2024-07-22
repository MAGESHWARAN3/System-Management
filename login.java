package computer.system.project.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Login {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
        	 System.out.println("");
            System.out.println("      WELCOME TO SYSTEM MANAGEMENT    ");
            System.out.println("");

            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                login();
            } else if (choice == 2) {
                register();
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void login() {
    	 System.out.println("  ");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");
                if (role.equals("admin")) {
                    AdminModule.run();
                } else {
                    UserModule.run(userId);
                }
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void register() {
    	 System.out.println("  ");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        String role = "user"; // Since there's only one admin, all new registrations are users.

        int departmentId = selectDepartment();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, role, department_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setInt(4, departmentId);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
            	 System.out.println("  ");
                System.out.println("Registration successful. You can now log in.");
            } else {
            	 System.out.println("  ");
                System.out.println("Failed to register. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int selectDepartment() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM departments";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("  ");
            System.out.println("Select department:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.printf("%d. %s%n", id, name);
            }
            System.out.println("  ");
            System.out.print("Choose a department by ID: ");
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
