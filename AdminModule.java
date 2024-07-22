package computer.system.project.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminModule {
    private static Scanner scanner = new Scanner(System.in);

    public static void run() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n1. View all reports");
            System.out.println("2. Update report status");
            System.out.println("3. Manage Departments");
            System.out.println("4. Manage Maintenance");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            
            int choice = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("  ");


            switch (choice) {
                case 1:
                    viewAllReports();
                    break;
                case 2:
                    updateReportStatus();
                    break;
                case 3:
                    manageDepartments();
                    break;
                case 4:
                    manageMaintenance();
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        System.out.println("Logged out successfully.");
    }

    private static void viewAllReports() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT r.*, u.username AS user_name, d.name AS department_name FROM reports r JOIN users u ON r.user_id = u.id JOIN departments d ON r.department_id = d.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nAll Reports:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String userName = rs.getString("user_name");
                String computerId = rs.getString("computer_id");
                String issueDescription = rs.getString("issue_description");
                String status = rs.getString("status");
                String departmentName = rs.getString("department_name");
                System.out.printf("ID: %d \n", id);
                System.out.printf("      User         : %s \n",userName);
                System.out.printf("      Computer ID  : %s \n",computerId);
                System.out.printf("      Issue        : %s \n",issueDescription);
                System.out.printf("      Status       : %s \n",status);
                System.out.printf("      Department   : %s%n \n", departmentName);
                System.out.println("  ");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReportStatus() {
    	 System.out.println("  ");

        System.out.print("Enter report ID: ");
        int reportId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter the new status (pending, in progress, repaired): ");
        String newStatus = scanner.nextLine().trim();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE reports SET status = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newStatus);
            stmt.setInt(2, reportId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Report status updated successfully.");
            } else {
                System.out.println("Report ID " + reportId + " does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void manageDepartments() {
    	 System.out.println("  ");
        System.out.println("\n1. Add Department");
        System.out.println("2. View Departments");
        System.out.print("Choose an option: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

        switch (choice) {
            case 1:
                addDepartment();
                break;
           
            case 2:
                viewDepartments();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void addDepartment() {
    	 System.out.println("  ");
        System.out.print("Enter department name: ");
        String departmentName = scanner.nextLine().trim();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO departments (name) VALUES (?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, departmentName);
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

    private static void manageMaintenance() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n1. Assign Maintenance");
            System.out.println("2. View Maintenance Tasks");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    assignMaintenance();
                    break;
                
                case 2:
                    viewMaintenanceTasks();
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void assignMaintenance() {
        System.out.print("Enter report ID: ");
        int reportId = Integer.parseInt(scanner.nextLine().trim());

        if (!isValidReportId(reportId)) {
            System.out.println("Report ID " + reportId + " does not exist.");
            return;
        }

        System.out.print("Enter name to assign: ");
        String userName = scanner.nextLine().trim();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO maintenance (report_id, assigned_to) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, reportId);
            stmt.setString(2, userName); // Store user name directly
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Maintenance assigned successfully.");
            } else {
                System.out.println("Failed to assign maintenance.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidReportId(int reportId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id FROM reports WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

   
    private static void viewMaintenanceTasks() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT m.*, m.assigned_to AS assigned_user FROM maintenance m";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("  ");

            System.out.println("\nMaintenance Tasks:");
            while (rs.next()) {
                int id = rs.getInt("id");
                int reportId = rs.getInt("report_id");
                String assignedUser = rs.getString("assigned_user");
                String status = rs.getString("maintenance_status");
                String updatedAt = rs.getString("updated_at");
                System.out.printf("ID: %d, Report ID: %d, Assigned To: %s, Status: %s, Updated At: %s%n", id, reportId, assignedUser, status, updatedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   
}
