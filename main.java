import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        try {
            // Database connection details
            String url = "jdbc:mysql://localhost:3306/InvoiceDB"; // Ensure InvoiceDB exists
            String user = "root";  // Change this if your MySQL username is different
            String password = "1234";  // Your MySQL password

            // Connect to MySQL
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL successfully!");

            // Initialize managers
            ClientManager clientManager = new ClientManager(conn, scanner);
            ServiceManager serviceManager = new ServiceManager(conn, scanner);
            InvoiceManager invoiceManager = new InvoiceManager(conn, scanner);

            while (true) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. Manage Clients");
                System.out.println("2. Manage Services");
                System.out.println("3. Manage Invoices");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input! Enter a number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        clientManager.clientMenu();
                        break;
                    case 2:
                        serviceManager.serviceMenu();
                        break;
                    case 3:
                        invoiceManager.invoiceMenu();
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed! Check MySQL is running.");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                scanner.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
