package computer.system.project.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserModule {
    private static Scanner scanner = new Scanner(System.in);

    public static void run(int userId) {
        boolean loggedOut = false;

        while (!loggedOut) {
            System.out.println("\n1. Report an issue");
            System.out.println("2. View my reports");
            System.out.println("3. Logout");
            System.out.println("  ");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    reportIssue(userId);
                    break;
                case 2:
                    viewReports(userId);
                    break;
                case 3:
                    loggedOut = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("  ");
        System.out.println("Logged out successfully.");
    }

    private static void reportIssue(int userId) {
    	 System.out.println("  ");
        System.out.print("Enter computer ID: ");
        String computerId = scanner.nextLine().trim();
        System.out.println("  ");
        System.out.print("Enter issue description: ");
        
        String issueDescription = scanner.nextLine().trim();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT department_id FROM users WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            int departmentId = 0;
            if (rs.next()) {
                departmentId = rs.getInt("department_id");
            }

            query = "INSERT INTO reports (user_id, computer_id, issue_description, department_id) VALUES (?, ?, ?, ?)";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, computerId);
            stmt.setString(3, issueDescription);
            stmt.setInt(4, departmentId);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
            	 System.out.println("  ");

                System.out.println("Issue reported successfully.");
            } else {
            	 System.out.println("  ");

                System.out.println("Failed to report issue.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReports(int userId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT r.*, d.name AS department_name FROM reports r JOIN departments d ON r.department_id = d.id WHERE r.user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("  ");

            System.out.println("\nMy Reports:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String computerId = rs.getString("computer_id");
                String issueDescription = rs.getString("issue_description");
                String status = rs.getString("status");
                String departmentName = rs.getString("department_name");
                System.out.printf("ID          : %d \n",id);
                System.out.printf("Computer ID : %s\n",computerId);
                System.out.printf("Issue       : %s\n",issueDescription);
                System.out.printf("Status      : %s \n",status);
                System.out.printf("Department  : %s%n \n",departmentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
