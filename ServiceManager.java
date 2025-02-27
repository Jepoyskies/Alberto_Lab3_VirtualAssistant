import java.sql.*;
import java.util.Scanner;

public class ServiceManager {
    private Connection conn;
    private Scanner scanner;

    public ServiceManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void serviceMenu() {
        while (true) {
            System.out.println("\n=== Service Management ===");
            System.out.println("1. Add Service");
            System.out.println("2. View Services");
            System.out.println("3. Update Service");
            System.out.println("4. Delete Service");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: addService(); break;
                case 2: viewServices(); break;
                case 3: updateService(); break;
                case 4: deleteService(); break;
                case 5: return;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void addService() {
        System.out.print("Enter service name: ");
        String name = scanner.nextLine();
        System.out.print("Enter service rate per hour: ");
        double rate = scanner.nextDouble();

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Services (name, rate) VALUES (?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, rate);
            stmt.executeUpdate();
            System.out.println("Service added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewServices() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Services")) {
            System.out.println("\n=== Services List ===");
            while (rs.next()) {
                System.out.printf("%d. %s ($%.2f/hr)\n", rs.getInt("service_id"), rs.getString("name"), rs.getDouble("rate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateService() {
        viewServices();
        System.out.print("Enter service ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new service name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new service rate: ");
        double newRate = scanner.nextDouble();

        try (PreparedStatement stmt = conn.prepareStatement("UPDATE Services SET name = ?, rate = ? WHERE service_id = ?")) {
            stmt.setString(1, newName);
            stmt.setDouble(2, newRate);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Service updated successfully!");
            } else {
                System.out.println("Service ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteService() {
        viewServices();
        System.out.print("Enter service ID to delete: ");
        int id = scanner.nextInt();

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Services WHERE service_id = ?")) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Service deleted successfully!");
            } else {
                System.out.println("Service ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
