import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseManager {
    // Database Credentials
    private static final String URL = "jdbc:mysql://localhost:3306/SPAM_DETECTOR";
    private static final String USER = "root";
    private static final String PASSWORD = "Pass1224@"; 

    /**
     * Updates the dashboard statistics in MySQL based on the scan results.
     * @param result The final decision from Flask (SPAM, HAM, or SUSPICIOUS)
     * @param currentConfidence The confidence score received from the model
     */
    public static void updateStats(String result, double currentConfidence) {
        
        // 1. Map String result to Integer flags for SQL
        int isSpam = ("SPAM".equalsIgnoreCase(result)) ? 1 : 0;
        int isHam = ("HAM".equalsIgnoreCase(result)) ? 1 : 0;
        int isSuspicious = ("SUSPICIOUS".equalsIgnoreCase(result)) ? 1 : 0;
        
        // 2. Optimized SQL Query: Updates all counters and calculates Average Confidence in one go
        String query = "UPDATE dashboard_stats SET " +
                       "spam_detected = spam_detected + ?, " +
                       "ham_detected = ham_detected + ?, " +
                       "suspicious_cases = suspicious_cases + ?, " + 
                       "avg_confidence = (avg_confidence * total_scanned + ?) / (total_scanned + 1), " +
                       "total_scanned = total_scanned + 1 " +
                       "WHERE id = 1";

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
                con.setAutoCommit(true); 
                
                try (PreparedStatement pst = con.prepareStatement(query)) {
                    // Mapping parameters to the '?' in the query
                    pst.setInt(1, isSpam);
                    pst.setInt(2, isHam);
                    pst.setInt(3, isSuspicious);
                    pst.setDouble(4, currentConfidence);
                    
                    System.out.println(">>> Database Action: Updating stats for " + result);
                    int rows = pst.executeUpdate();
                    
                    if (rows > 0) {
                        System.out.println("✅ Success! MySQL Automated Update Complete.");
                    } else {
                        System.err.println("❌ ALERT: Row with ID=1 not found. Check your database table!");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}