import java.sql.*;

public class DatabaseTest {
    private static final String URL = "jdbc:mysql://localhost:3306/SPAM_DETECTOR";
    private static final String USER = "root";
    private static final String PASSWORD = "Pass1224@";

    public static void main(String[] args) {
        System.out.println("--- Starting Java Database Test (Extended) ---");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

            // 1. WRITE (Ab hum naye columns bhi update karenge)
            System.out.println("Step 1: Writing data to MySQL (ID=1)...");
            
            // "ON DUPLICATE KEY UPDATE" best hai kyunki ye ID 1 ko hi refresh karega
            String updateSQL = "INSERT INTO dashboard_stats (id, total_scanned, spam_detected, ham_detected, suspicious_cases, avg_confidence) " +
                               "VALUES (1, 1, 1, 0, 0, 0.92) " +
                               "ON DUPLICATE KEY UPDATE " +
                               "total_scanned = total_scanned + 1, " +
                               "spam_detected = spam_detected + 1, " +
                               "avg_confidence = 0.95"; // Example update
            
            PreparedStatement pst = con.prepareStatement(updateSQL);
            int rowsAffected = pst.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            // 2. READ (Sare columns fetch karke dekhte hain)
            System.out.println("\nStep 2: Reading all stats from MySQL...");
            String selectSQL = "SELECT * FROM dashboard_stats WHERE id = 1";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(selectSQL);

            if (rs.next()) {
                System.out.println("------------------------------------");
                System.out.println("Total Scanned   : " + rs.getInt("total_scanned"));
                System.out.println("Spam Detected   : " + rs.getInt("spam_detected"));
                System.out.println("Ham Detected    : " + rs.getInt("ham_detected"));
                System.out.println("Suspicious Cases: " + rs.getInt("suspicious_cases"));
                System.out.println("Avg Confidence  : " + rs.getFloat("avg_confidence"));
                System.out.println("------------------------------------");
                System.out.println("SUCCESS: Java can Read & Write all columns!");
            }

            con.close();
        } catch (Exception e) {
            System.out.println("TEST FAILED! Check if table 'dashboard_stats' exists with new columns.");
            e.printStackTrace();
        }
    }
}