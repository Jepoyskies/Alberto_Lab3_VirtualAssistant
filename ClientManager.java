import java.sql.*;
import java.util.Scanner;

public class ClientManager {
    private Connection conn;
    private Scanner scanner;

    public ClientManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void clientMenu() {
        while (true) {
            System.out.println("\n=== Client Management ===");
            System.out.println("1. Add Client");
            System.out.println("2. View Clients");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: addClient(); break;
                case 2: viewClients(); break;
                case 3: return;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    public void addClient() {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine();
        System.out.print("Enter client email: ");
        String email = scanner.nextLine();

        String sql = "INSERT INTO Clients (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            System.out.println("Client added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding client!");
            e.printStackTrace();
        }
    }

    public void viewClients() {
        String sql = "SELECT * FROM Clients";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n=== Client List ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("client_id") + 
                                   " | Name: " + rs.getString("name") + 
                                   " | Email: " + rs.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving clients!");
            e.printStackTrace();
        }
    }
}
