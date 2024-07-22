package computer.system.project.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DepartmentModule {
    private static Scanner scanner = new Scanner(System.in);

    public static void run() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n1. Add Department");
            System.out.println("2. View Departments");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    addDepartment();
                    break;
                case 2:
                    viewDepartments();
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addDepartment() {
        System.out.print("Enter department name: ");
        String name = scanner.nextLine().trim();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO departments (name) VALUES (?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Department added successfully.");
            } else {
                System.out.println("Failed to add department.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewDepartments() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM departments";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nDepartments:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.printf("ID: %d, Name: %s%n", id, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
