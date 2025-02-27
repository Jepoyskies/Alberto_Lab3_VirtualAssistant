import java.sql.*;
import java.util.Scanner;

public class InvoiceManager {
    private Connection conn;
    private Scanner scanner;

    public InvoiceManager(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void invoiceMenu() {
        while (true) {
            System.out.println("\n=== Invoice Management ===");
            System.out.println("1. Create New Invoice");
            System.out.println("2. View All Invoices");
            System.out.println("3. View Invoice by Client");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: createInvoice(); break;
                case 2: viewAllInvoices(); break;
                case 3: viewInvoiceByClient(); break;
                case 4: return;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void createInvoice() {
        try {
            System.out.println("\nSelect client:");
            String query = "SELECT client_id, name FROM Clients";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println(rs.getInt("client_id") + ". " + rs.getString("name"));
            }

            System.out.print("Enter client ID: ");
            int clientId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String insertInvoice = "INSERT INTO Invoices (client_id, date_created) VALUES (?, NOW())";
            PreparedStatement pstmt = conn.prepareStatement(insertInvoice, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, clientId);
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            int invoiceId = 0;
            if (generatedKeys.next()) {
                invoiceId = generatedKeys.getInt(1);
            }

            while (true) {
                System.out.println("\nAdd services to invoice:");
                System.out.println("1. Add service");
                System.out.println("2. Finish invoice");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();

                if (choice == 2) break;

                System.out.println("\nAvailable services:");
                String serviceQuery = "SELECT service_id, name, rate FROM Services";
                ResultSet rsServices = stmt.executeQuery(serviceQuery);
                while (rsServices.next()) {
                    System.out.println(rsServices.getInt("service_id") + ". " + rsServices.getString("name") + " ($" + rsServices.getDouble("rate") + "/hr)");
                }

                System.out.print("Select service ID: ");
                int serviceId = scanner.nextInt();
                System.out.print("Enter hours: ");
                int hours = scanner.nextInt();

                String insertDetails = "INSERT INTO InvoiceDetails (invoice_id, service_id, hours) VALUES (?, ?, ?)";
                PreparedStatement detailStmt = conn.prepareStatement(insertDetails);
                detailStmt.setInt(1, invoiceId);
                detailStmt.setInt(2, serviceId);
                detailStmt.setInt(3, hours);
                detailStmt.executeUpdate();

                System.out.println("Service added to invoice.");
            }

            System.out.println("Invoice created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewAllInvoices() {
        try {
            String query = "SELECT i.invoice_id, c.name, i.date_created FROM Invoices i JOIN Clients c ON i.client_id = c.client_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n=== Invoices ===");
            while (rs.next()) {
                System.out.println("Invoice ID: " + rs.getInt("invoice_id") + " | Client: " + rs.getString("name") + " | Date: " + rs.getDate("date_created"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewInvoiceByClient() {
        try {
            System.out.print("\nEnter client ID: ");
            int clientId = scanner.nextInt();

            String query = "SELECT i.invoice_id, i.date_created, s.name, d.hours, s.rate FROM Invoices i " +
                    "JOIN InvoiceDetails d ON i.invoice_id = d.invoice_id " +
                    "JOIN Services s ON d.service_id = s.service_id " +
                    "WHERE i.client_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n=== Invoice Details ===");
            while (rs.next()) {
                System.out.println("Invoice ID: " + rs.getInt("invoice_id") + " | Date: " + rs.getDate("date_created"));
                System.out.println("Service: " + rs.getString("name") + " | Hours: " + rs.getInt("hours") + " | Rate: $" + rs.getDouble("rate") + "/hr");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
