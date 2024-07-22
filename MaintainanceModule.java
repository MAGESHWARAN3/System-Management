package computer.system.project.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MaintenanceModule {
    private static Scanner scanner = new Scanner(System.in);

    public static void run() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n1. Assign Maintenance");
            System.out.println("2. Update Maintenance Status");
            System.out.println("3. View Maintenance Tasks");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    assignMaintenance();
                    break;
                case 2:
                    updateMaintenanceStatus();
                    break;
                case 3:
                    viewMaintenanceTasks();
                    break;
                case 4:
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

        System.out.print("Enter user name to assign: ");
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

    private static void updateMaintenanceStatus() {
        System.out.print("Enter maintenance ID: ");
        int maintenanceId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter the new status (assigned, in progress, completed): ");
        String newStatus = scanner.nextLine().trim();

        if (!isValidStatus(newStatus)) {
            System.out.println("Invalid status. Please enter one of: assigned, in progress, completed.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE maintenance SET maintenance_status = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newStatus);
            stmt.setInt(2, maintenanceId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Maintenance status updated successfully.");
            } else {
                System.out.println("Failed to update maintenance status. No matching maintenance task found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewMaintenanceTasks() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT m.*, u.username AS assigned_user FROM maintenance m JOIN users u ON m.assigned_to = u.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nMaintenance Tasks:");
            while (rs.next()) {
                int id = rs.getInt("id");
                int reportId = rs.getInt("report_id");
                String assignedUser = rs.getString("assigned_to"); // Get user name directly
                String status = rs.getString("maintenance_status");
                String updatedAt = rs.getString("updated_at");
                System.out.printf("ID: %d, Report ID: %d, Assigned To: %s, Status: %s, Updated At: %s%n", id, reportId, assignedUser, status, updatedAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidStatus(String status) {
        return status.equalsIgnoreCase("assigned") || status.equalsIgnoreCase("in progress") || status.equalsIgnoreCase("completed");
    }
}
